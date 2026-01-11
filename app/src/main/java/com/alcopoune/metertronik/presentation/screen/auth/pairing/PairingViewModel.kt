package com.alcopoune.metertronik.presentation.screen.auth.pairing

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alcopoune.metertronik.data.local.DataStorage
import com.alcopoune.metertronik.data.repository.PairingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.text.Charsets

@HiltViewModel
class PairingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pairingRepository: PairingRepository,
    private val dataStorage: DataStorage
) : ViewModel() {

    companion object {
        private const val SCAN_DURATION_MS = 10_000L
        private const val DEFAULT_MTU = 23
    }

    private val METERTRONIK_SERVICE_UUID =
        UUID.fromString("9f1c0001-6b9a-4e9d-bc2a-9c1a9d000001")

    private val DEVICE_ID_CHARACTERISTIC_UUID =
        UUID.fromString("9f1c0002-6b9a-4e9d-bc2a-9c1a9d000002")

    private val TOKEN_CHARACTERISTIC_UUID =
        UUID.fromString("9f1c0003-6b9a-4e9d-bc2a-9c1a9d000003")
    private val bluetoothAdapter: BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter

    private val _state = MutableStateFlow(PairingState())
    val state: StateFlow<PairingState> = _state.asStateFlow()

    private var isScanningInternal = false
    private var pairingJob: Job? = null

    private data class GattConnection(
        val gatt: BluetoothGatt,
        val callback: PairingGattCallback
    )

    private class PairingGattCallback : BluetoothGattCallback() {
        var servicesContinuation: CancellableContinuation<GattConnection>? = null
        var readContinuation: CancellableContinuation<ByteArray>? = null
        var writeContinuation: CancellableContinuation<Unit>? = null
        var mtuContinuation: CancellableContinuation<Int>? = null
        var lastNegotiatedMtu: Int? = DEFAULT_MTU

        override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices()
            } else if (status != BluetoothGatt.GATT_SUCCESS && servicesContinuation?.isActive == true) {
                servicesContinuation?.resumeWithException(
                    IllegalStateException("Koneksi GATT gagal ($status)")
                )
                servicesContinuation = null
                gatt.close()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED && servicesContinuation?.isActive == true) {
                servicesContinuation?.resumeWithException(IllegalStateException("Koneksi GATT terputus ($status)"))
                servicesContinuation = null
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS && servicesContinuation?.isActive == true) {
                servicesContinuation?.resume(GattConnection(gatt, this))
            } else if (servicesContinuation?.isActive == true) {
                servicesContinuation?.resumeWithException(IllegalStateException("Gagal discover service ($status)"))
            }
            servicesContinuation = null
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            if (mtuContinuation?.isActive == true) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    lastNegotiatedMtu = mtu
                    mtuContinuation?.resume(mtu)
                } else {
                    mtuContinuation?.resumeWithException(IllegalStateException("MTU gagal ($status)"))
                }
            }
            mtuContinuation = null
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (readContinuation?.isActive == true) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    readContinuation?.resume(characteristic.value)
                } else {
                    readContinuation?.resumeWithException(IllegalStateException("Baca karakteristik gagal ($status)"))
                }
            }
            readContinuation = null
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (writeContinuation?.isActive == true) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    writeContinuation?.resume(Unit)
                } else {
                    writeContinuation?.resumeWithException(IllegalStateException("Tulis karakteristik gagal ($status)"))
                }
            }
            writeContinuation = null
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let { handleResult(it) }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            results.forEach { handleResult(it) }
        }

        override fun onScanFailed(errorCode: Int) {
            isScanningInternal = false
            _state.update {
                it.copy(
                    isScanning = false,
                    error = "Scan gagal dengan kode $errorCode"
                )
            }
        }
    }

    fun pairWithDevice(address: String) {
        pairingJob?.cancel()
        pairingJob = viewModelScope.launch {
            _state.update { it.copy(selectedAddress = address, error = null) }
            performPairing(address)
        }
    }

    fun persistPairedDeviceId() {
        val paired = _state.value.devices.firstOrNull { it.isPaired && !it.deviceId.isNullOrBlank() }
        val deviceId = paired?.deviceId ?: return
        viewModelScope.launch {
            dataStorage.saveDeviceId(deviceId)
        }
    }

    fun refreshScan() {
        viewModelScope.launch {
            pairingJob?.cancel()
            stopScanInternal()
            _state.update {
                it.copy(
                    devices = emptyList(),
                    selectedAddress = null,
                    error = null
                )
            }
            startScan()
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun performPairing(address: String) {
        if (!hasRequiredPermissions()) {
            _state.update {
                it.copy(
                    error = "Izin BLE belum diberikan"
                )
            }
            return
        }

        stopScanInternal()
        updateDeviceStatus(address, PairingStatus.CONNECTING, "Menghubungkan ke perangkat...")

        var connection: GattConnection? = null
        var negotiatedMtu = DEFAULT_MTU

        try {
            connection = connectGatt(address)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                negotiatedMtu = runCatching { requestMtu(connection, 185) }
                    .getOrDefault(DEFAULT_MTU)
            }
            delay(400) // kecil untuk menghindari race selepas MTU

            updateDeviceStatus(address, PairingStatus.READING_DEVICE_ID, "Membaca ID perangkat...")
            val deviceId = readDeviceId(connection)
            delay(600)

            updateDeviceStatus(
                address = address,
                status = PairingStatus.PAIRING_API,
                message = "Mengirim pairing ke server...",
                deviceId = deviceId
            )

            val pairingResult = pairingRepository.pairUser(deviceId)
            // Jika user/device sudah pernah dipair (server mengembalikan isPaired=true),
            // bisa jadi pairing_token tidak dikirim lagi. Dalam kasus ini, kita cukup
            // anggap pairing sudah valid dan lanjutkan menyimpan deviceId lokal.
            val token = pairingResult.pairingToken
            if (pairingResult.isPaired && token.isNullOrBlank()) {
                updateDeviceStatus(
                    address = address,
                    status = PairingStatus.CONNECTED,
                    message = "Connected",
                    isPaired = true,
                    deviceId = deviceId
                )
                return
            }

            val tokenNonNull = token
                ?: throw IllegalStateException("pairing_token kosong dari server")

            updateDeviceStatus(
                address = address,
                status = PairingStatus.WRITING_TOKEN,
                message = "Mengirim token ke perangkat...",
                deviceId = deviceId
            )
            writePairingToken(connection, tokenNonNull, negotiatedMtu)

            updateDeviceStatus(
                address = address,
                status = PairingStatus.POLLING,
                message = "Menunggu konfirmasi pairing...",
                deviceId = deviceId
            )

            val paired = pollPairingStatus(deviceId, address)
            if (paired) {
                updateDeviceStatus(
                    address = address,
                    status = PairingStatus.CONNECTED,
                    message = "Connected",
                    isPaired = true,
                    deviceId = deviceId
                )
            } else {
                updateDeviceStatus(
                    address = address,
                    status = PairingStatus.FAILED,
                    message = "Pairing belum dikonfirmasi",
                    deviceId = deviceId
                )
            }
        } catch (e: Exception) {
            updateDeviceStatus(
                address = address,
                status = PairingStatus.FAILED,
                message = e.message ?: "Pairing gagal"
            )
            _state.update { it.copy(error = e.message) }
        } finally {
            delay(300)
            connection?.gatt?.disconnect()
            delay(200)
            connection?.gatt?.close()
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun connectGatt(address: String): GattConnection =
        suspendCancellableCoroutine { cont ->
            val device = bluetoothAdapter?.getRemoteDevice(address)
                ?: run {
                    cont.resumeWithException(IllegalStateException("Perangkat tidak ditemukan"))
                    return@suspendCancellableCoroutine
                }

            val callback = PairingGattCallback().apply {
                servicesContinuation = cont
            }

            val gatt = device.connectGatt(context, false, callback)
            cont.invokeOnCancellation { gatt.close() }
        }

    @SuppressLint("MissingPermission")
    private suspend fun readDeviceId(connection: GattConnection): String {
        val characteristic = connection.gatt.getService(METERTRONIK_SERVICE_UUID)
            ?.getCharacteristic(DEVICE_ID_CHARACTERISTIC_UUID)
            ?: throw IllegalStateException("Characteristic device_id tidak ditemukan")

        val value = readCharacteristic(connection, characteristic)
        val deviceId = value.toString(Charsets.UTF_8).trim()
        if (deviceId.isBlank()) throw IllegalStateException("device_id kosong")
        return deviceId
    }

    @SuppressLint("MissingPermission")
    private suspend fun writePairingToken(
        connection: GattConnection,
        token: String,
        negotiatedMtu: Int
    ) {
        val characteristic = connection.gatt
            .getService(METERTRONIK_SERVICE_UUID)
            ?.getCharacteristic(TOKEN_CHARACTERISTIC_UUID)
            ?: throw IllegalStateException("Characteristic pairing_token tidak ditemukan")


        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE


        val payload = token.toByteArray(Charsets.UTF_8)
        val mtuPayload = maxOf((connection.callback.lastNegotiatedMtu ?: negotiatedMtu) - 3, 20)

        for (chunk in payload.asList().chunked(mtuPayload)) {
            characteristic.value = chunk.toByteArray()

            if (characteristic.writeType == BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE) {
                val started = connection.gatt.writeCharacteristic(characteristic)
                if (!started) {
                    throw IllegalStateException("Gagal memulai write NO_RESPONSE")
                }
            } else {
                writeCharacteristicWithRetry(connection, characteristic)
            }

            delay(120)
        }
    }


    private suspend fun requestMtu(connection: GattConnection, requestedMtu: Int): Int =
        suspendCancellableCoroutine { cont ->
            connection.callback.mtuContinuation = cont
            val started = connection.gatt.requestMtu(requestedMtu)
            if (!started) {
                connection.callback.mtuContinuation = null
                cont.resumeWithException(IllegalStateException("Gagal memulai request MTU"))
            }
            cont.invokeOnCancellation { connection.callback.mtuContinuation = null }
        }

    private suspend fun pollPairingStatus(deviceId: String, address: String): Boolean {
        repeat(10) { attempt ->
            val status = pairingRepository.getPairingStatus(deviceId)
            if (status.isPaired) return true
            updateDeviceStatus(
                address = address,
                status = PairingStatus.POLLING,
                message = "Menunggu konfirmasi... (${attempt + 1}/10)",
                deviceId = deviceId
            )
            delay(2_000)
        }
        return false
    }

    @SuppressLint("MissingPermission")
    private suspend fun readCharacteristic(
        connection: GattConnection,
        characteristic: BluetoothGattCharacteristic
    ): ByteArray = suspendCancellableCoroutine { cont ->
        connection.callback.readContinuation = cont
        val started = connection.gatt.readCharacteristic(characteristic)
        if (!started) {
            connection.callback.readContinuation = null
            cont.resumeWithException(IllegalStateException("Tidak dapat memulai pembacaan karakteristik"))
        }
        cont.invokeOnCancellation { connection.callback.readContinuation = null }
    }

    @SuppressLint("MissingPermission")
    private suspend fun writeCharacteristic(
        connection: GattConnection,
        characteristic: BluetoothGattCharacteristic
    ) = suspendCancellableCoroutine<Unit> { cont ->

        // 🔥 GUARD: BLE masih sibuk
        if (connection.callback.writeContinuation != null) {
            cont.resumeWithException(
                IllegalStateException("BLE busy, write in progress")
            )
            return@suspendCancellableCoroutine
        }

        connection.callback.writeContinuation = cont

        val started = connection.gatt.writeCharacteristic(characteristic)
        if (!started) {
            connection.callback.writeContinuation = null
            cont.resumeWithException(
                IllegalStateException("Tidak dapat memulai penulisan karakteristik")
            )
        }

        cont.invokeOnCancellation {
            connection.callback.writeContinuation = null
        }
    }

    private suspend fun writeCharacteristicWithRetry(
        connection: GattConnection,
        characteristic: BluetoothGattCharacteristic,
        retries: Int = 2
    ) {
        repeat(retries + 1) { attempt ->
            try {
                writeCharacteristic(connection, characteristic)
                return
            } catch (e: Exception) {
                if (attempt == retries) throw e
                delay(100)
            }
        }
    }

    private fun updateDeviceStatus(
        address: String,
        status: PairingStatus,
        message: String? = null,
        isPaired: Boolean? = null,
        deviceId: String? = null
    ) {
        _state.update { current ->
            val updated = current.devices.toMutableList()
            val index = updated.indexOfFirst { it.address == address }
            if (index < 0) return@update current
            val existing = updated[index]
            updated[index] = existing.copy(
                status = status,
                statusMessage = message,
                isPaired = isPaired ?: existing.isPaired,
                deviceId = deviceId ?: existing.deviceId
            )
            current.copy(devices = updated)
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScan() {
        if (!hasRequiredPermissions()) {
            _state.update {
                it.copy(
                    isScanning = false,
                    error = "Izin BLE belum diberikan"
                )
            }
            return
        }

        if (!isLocationEnabled()) {
            _state.update {
                it.copy(
                    isScanning = false,
                    error = "Nyalakan lokasi untuk melanjutkan scan"
                )
            }
            return
        }

        val adapter = bluetoothAdapter
        if (adapter == null || !adapter.isEnabled) {
            _state.update {
                it.copy(
                    isScanning = false,
                    error = "Bluetooth tidak tersedia atau belum aktif"
                )
            }
            return
        }

        if (isScanningInternal) return

        _state.update { it.copy(isScanning = true, error = null) }

        val filters = listOf(
            ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(METERTRONIK_SERVICE_UUID))
                .build()
        )

        scanner()?.startScan(
            filters, // jangan batasi, biar semua iklan masuk lalu difilter di handleResult jika perlu
            ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build(),
            scanCallback
        )
        isScanningInternal = true

        viewModelScope.launch {
            delay(SCAN_DURATION_MS)
            stopScanInternal()
        }
    }

    fun stopScan() {
        viewModelScope.launch { stopScanInternal() }
    }

    override fun onCleared() {
        super.onCleared()
        stopScanInternal()
    }

    private fun hasRequiredPermissions(): Boolean {
        val permissions = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(Manifest.permission.BLUETOOTH_SCAN)
                add(Manifest.permission.BLUETOOTH_CONNECT)
            }
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        return permissions.all { perm ->
            ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun isLocationEnabled(): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        return lm?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
            lm?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
    }

    @SuppressLint("MissingPermission")
    private fun stopScanInternal() {
        if (!isScanningInternal) return
        scanner()?.stopScan(scanCallback)
        isScanningInternal = false
        _state.update { it.copy(isScanning = false) }
    }

    @SuppressLint("MissingPermission")
    private fun scanner(): BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner

    private fun handleResult(result: ScanResult) {
        val device = result.device ?: return
        val address = device.address ?: return
        val name = device.name?.takeIf { it.isNotBlank() } ?: "Unknown Device"

        _state.update { current ->
            val updated = current.devices.toMutableList()
            val index = updated.indexOfFirst { it.address == address }
            val existing = updated.getOrNull(index)
            val newItem = (existing ?: PairingDevice(name = name, address = address)).copy(
                name = name,
                rssi = result.rssi
            )

            if (index >= 0) updated[index] = newItem else updated.add(newItem)

            current.copy(
                devices = updated.sortedBy { it.name },
                error = null
            )
        }
    }
}