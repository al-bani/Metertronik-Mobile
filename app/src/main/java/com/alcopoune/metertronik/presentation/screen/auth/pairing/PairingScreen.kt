package com.alcopoune.metertronik.presentation.screen.auth.pairing

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.alcopoune.metertronik.presentation.components.input.PrimaryButton
import com.alcopoune.metertronik.presentation.components.loading.LoadingDots
import com.alcopoune.metertronik.presentation.navigation.Routes
import com.alcopoune.metertronik.presentation.theme.MetertronikTheme
import com.alcopoune.metertronik.presentation.screen.auth.pairing.PairingStatus
import androidx.navigation.NavHostController
import com.alcopoune.metertronik.presentation.components.loading.GradientSearchLoader
import kotlinx.coroutines.delay

@Composable
fun PairingScreen(
    navController: NavHostController? = null,
    viewModel: PairingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val requiredPermissions = remember {
        buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(Manifest.permission.BLUETOOTH_SCAN)
                add(Manifest.permission.BLUETOOTH_CONNECT)
            }
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }.toTypedArray()
    }

    var permissionDenied by remember { mutableStateOf(false) }
    var showScanningScreen by remember { mutableStateOf(true) }
    var scanStartTime by remember { mutableStateOf(0L) }
    var hasReached5Seconds by remember { mutableStateOf(false) }
    var hasReached10Seconds by remember { mutableStateOf(false) }
    var deviceFoundBefore5Seconds by remember { mutableStateOf(false) }
    var noDeviceFoundCount by remember { mutableStateOf(0) }
    var noDeviceCountedThisAttempt by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = requiredPermissions.all { result[it] == true }
        permissionDenied = !granted
        if (granted) {
            showScanningScreen = true
            scanStartTime = System.currentTimeMillis()
            hasReached5Seconds = false
            hasReached10Seconds = false
            deviceFoundBefore5Seconds = false
            noDeviceCountedThisAttempt = false
            viewModel.refreshScan()
        }
    }

    val hasPermissions = {
        requiredPermissions.all { perm ->
            ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
        }
    }

    LaunchedEffect(Unit) {
        if (hasPermissions()) {
            showScanningScreen = true
            scanStartTime = System.currentTimeMillis()
            hasReached5Seconds = false
            hasReached10Seconds = false
            deviceFoundBefore5Seconds = false
            noDeviceCountedThisAttempt = false
            viewModel.startScan()
        } else {
            permissionLauncher.launch(requiredPermissions)
        }
    }

    // Track waktu scan dan device ditemukan
    LaunchedEffect(state.isScanning, scanStartTime, state.devices) {
        if (scanStartTime > 0) {
            // Cek jika device ditemukan sebelum 5 detik
            if (state.devices.isNotEmpty() && !deviceFoundBefore5Seconds) {
                deviceFoundBefore5Seconds = true
            }
            
            // Hitung waktu yang sudah berlalu
            var elapsed = System.currentTimeMillis() - scanStartTime
            
            // Tunggu hingga 5 detik jika belum tercapai
            if (elapsed < 5000 && !hasReached5Seconds) {
                delay(5000 - elapsed)
                hasReached5Seconds = true
            } else if (elapsed >= 5000 && !hasReached5Seconds) {
                hasReached5Seconds = true
            }
            
            // Jika device ditemukan sebelum 5 detik, tampilkan PairingScreen setelah 5 detik
            if (hasReached5Seconds && deviceFoundBefore5Seconds && state.devices.isNotEmpty()) {
                showScanningScreen = false
                return@LaunchedEffect
            }
            
            // Jika belum ada device, lanjutkan hingga 10 detik (total dari awal)
            if (state.devices.isEmpty() && !hasReached10Seconds) {
                elapsed = System.currentTimeMillis() - scanStartTime
                if (elapsed < 10000) {
                    delay(10000 - elapsed)
                }
                hasReached10Seconds = true
            }
        }
    }
    
    // Handle jika scan selesai sebelum 10 detik
    LaunchedEffect(state.isScanning, scanStartTime, hasReached10Seconds) {
        if (!state.isScanning && scanStartTime > 0 && !hasReached10Seconds) {
            val elapsed = System.currentTimeMillis() - scanStartTime
            if (elapsed < 10000) {
                delay(10000 - elapsed)
                hasReached10Seconds = true
            }
        }
    }

    // Jika device ditemukan setelah 5 detik dan masih di ScanningScreen
    LaunchedEffect(state.devices, hasReached5Seconds) {
        if (hasReached5Seconds && state.devices.isNotEmpty() && showScanningScreen && !deviceFoundBefore5Seconds) {
            showScanningScreen = false
        }
    }

    // Hitung berapa kali "No device found" terjadi (sekali per attempt scan)
    LaunchedEffect(showScanningScreen, hasReached10Seconds, state.isScanning, state.devices) {
        if (!showScanningScreen) return@LaunchedEffect

        if (state.devices.isNotEmpty()) {
            noDeviceFoundCount = 0
            noDeviceCountedThisAttempt = false
            return@LaunchedEffect
        }

        if (hasReached10Seconds && !state.isScanning && !noDeviceCountedThisAttempt) {
            noDeviceFoundCount += 1
            noDeviceCountedThisAttempt = true
        }
    }

    val hasConnected = state.devices.any { it.isPaired }

    if (showScanningScreen) {
        ScanningScreen(
            isScanning = state.isScanning,
            hasReached10Seconds = hasReached10Seconds,
            showHavingTrouble = noDeviceFoundCount >= 3,
            onScanNowClick = {
                if (hasPermissions()) {
                    showScanningScreen = true
                    scanStartTime = System.currentTimeMillis()
                    hasReached5Seconds = false
                    hasReached10Seconds = false
                    deviceFoundBefore5Seconds = false
                    noDeviceCountedThisAttempt = false
                    viewModel.refreshScan()
                } else {
                    permissionLauncher.launch(requiredPermissions)
                }
            }
        )
    } else {
        PairingScreenContent(
            state = state,
            permissionDenied = permissionDenied,
            onScanClick = {
                if (hasPermissions()) {
                    showScanningScreen = true
                    scanStartTime = System.currentTimeMillis()
                    hasReached5Seconds = false
                    hasReached10Seconds = false
                    deviceFoundBefore5Seconds = false
                    noDeviceCountedThisAttempt = false
                    viewModel.refreshScan()
                } else {
                    permissionLauncher.launch(requiredPermissions)
                }
            },
            onSelectDevice = { address ->
                val device = state.devices.find { it.address == address }
                if (device?.isPaired == true) return@PairingScreenContent
                viewModel.pairWithDevice(address)
            },
            hasConnected = hasConnected,
            onNextClick = {
                if (hasConnected) {
                    viewModel.persistPairedDeviceId()
                    navController?.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Pairing.route) { inclusive = true }
                    }
                }
            }
        )
    }
}

@Composable
private fun PairingScreenContent(
    state: PairingState,
    permissionDenied: Boolean,
    onScanClick: () -> Unit,
    onSelectDevice: (String) -> Unit,
    hasConnected: Boolean,
    onNextClick: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 56.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.QuestionMark,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.scrim,
                    modifier = Modifier.size(32.dp),
                )
                Spacer(modifier = Modifier.width(16.dp))
                PrimaryButton(
                    text = "Next",
                    onClick = onNextClick,
                    containerColor = MaterialTheme.colorScheme.onSecondary,
                    fullWidth = false,
                    enabled = hasConnected
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (state.error != null || permissionDenied) {
                Text(
                    text = state.error ?: "Izin BLE diperlukan untuk melakukan scan",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Choose your Metertronik",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.scrim.copy(0.7f),
                )
                PrimaryButton(
                    text = "SCAN",
                    onClick = onScanClick,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    fullWidth = false,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn {
                items(state.devices, key = { it.address }) { device ->
                    val isClickable = !device.isPaired &&
                        device.status != PairingStatus.CONNECTING &&
                        device.status != PairingStatus.READING_DEVICE_ID &&
                        device.status != PairingStatus.PAIRING_API &&
                        device.status != PairingStatus.WRITING_TOKEN &&
                        device.status != PairingStatus.POLLING

                    val isConnecting = device.status == PairingStatus.CONNECTING ||
                        device.status == PairingStatus.READING_DEVICE_ID ||
                        device.status == PairingStatus.PAIRING_API ||
                        device.status == PairingStatus.WRITING_TOKEN ||
                        device.status == PairingStatus.POLLING

                    val statusText = when {
                        device.isPaired || device.status == PairingStatus.CONNECTED -> "Device Connected"
                        device.status == PairingStatus.FAILED -> "Failed to Connecting"
                        isConnecting -> "Connecting..."
                        else -> "Active"
                    }

                    val statusColor = when {
                        device.isPaired || device.status == PairingStatus.CONNECTED -> MaterialTheme.colorScheme.surface
                        device.status == PairingStatus.FAILED -> MaterialTheme.colorScheme.error
                        isConnecting -> MaterialTheme.colorScheme.scrim.copy(0.7f)
                        else -> MaterialTheme.colorScheme.scrim
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .alpha(if (isClickable) 1f else 0.6f)
                            .clickable(
                                enabled = isClickable,
                                onClick = {
                                    onSelectDevice(device.address)
                                }
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = device.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.scrim,
                                fontWeight = FontWeight.Bold
                            )
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {

                                    if (device.status != PairingStatus.FAILED) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.surface,
                                                    shape = CircleShape
                                                )
                                        )
                                    }

                                    Text(
                                        text = statusText,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = statusColor
                                    )
                                }
                            }
                        }
                    }
                }

                if (state.devices.isEmpty()) {
                    item {
                        Text(
                            text = if (state.isScanning) "Mencari perangkat..." else "Tidak ada perangkat ditemukan",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.scrim.copy(0.7f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScanningScreen(
    isScanning: Boolean = true,
    hasReached10Seconds: Boolean = false,
    showHavingTrouble: Boolean = false,
    onScanNowClick: () -> Unit = {}
) {
   MetertronikTheme {
       Column(
           modifier = Modifier.fillMaxSize().padding(16.dp),
           verticalArrangement = Arrangement.Center,
           horizontalAlignment = Alignment.CenterHorizontally
       ) {
           Column(verticalArrangement = Arrangement.spacedBy(2.dp), horizontalAlignment = Alignment.CenterHorizontally) {
               Text(
                   text = "Plug the Metertronik Terminal",
                   style = MaterialTheme.typography.titleLarge,
                   color = MaterialTheme.colorScheme.scrim,
                   fontWeight = FontWeight.Bold
               )

               Text(
                   text = "Device will showing as Metertronik v1\n" +
                           "Please Report if didn't see it",
                   style = MaterialTheme.typography.bodyMedium,
                   color = MaterialTheme.colorScheme.scrim.copy(0.7f),
                   textAlign = TextAlign.Center
               )
           }
           Spacer(modifier = Modifier.height(58.dp))
           
           if (hasReached10Seconds && !isScanning) {
               // Tampilkan button SCAN NOW setelah 10 detik
               GradientSearchLoader(
                   isScanning = false,
                   onScanNowClick = {
                       onScanNowClick()
                   }
               )
           } else {
               GradientSearchLoader()
           }
           
           Spacer(modifier = Modifier.height(58.dp))
           Text(
               text = if (hasReached10Seconds && !isScanning) "No device found" else "Searching...",
               style = MaterialTheme.typography.titleLarge,
               color = MaterialTheme.colorScheme.scrim,
               fontWeight = FontWeight.SemiBold     
           )

           if (hasReached10Seconds && !isScanning && showHavingTrouble) {
               Spacer(modifier = Modifier.height(8.dp))
               Text(
                   text = "Having trouble?",
                   style = MaterialTheme.typography.bodyMedium,
                   color = MaterialTheme.colorScheme.scrim.copy(0.7f),
               )
           }
       }
   }
}