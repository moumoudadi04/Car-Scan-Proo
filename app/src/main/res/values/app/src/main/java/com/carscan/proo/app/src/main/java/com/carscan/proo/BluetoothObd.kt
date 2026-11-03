package com.carscan.proo

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.util.UUID

class BluetoothObd {

    private var bluetoothSocket: BluetoothSocket? = null

    private val sppUuid = UUID.fromString(
        "00001101-0000-1000-8000-00805F9B34FB"
    )

    fun connect(
        adapter: BluetoothAdapter,
        device: BluetoothDevice
    ) {
        adapter.cancelDiscovery()

        val newSocket =
            device.createRfcommSocketToServiceRecord(sppUuid)

        newSocket.connect()

        bluetoothSocket = newSocket
    }

    fun getInputStream() =
        bluetoothSocket?.inputStream
            ?: throw IllegalStateException("Bluetooth disconnected")

    fun getOutputStream() =
        bluetoothSocket?.outputStream
            ?: throw IllegalStateException("Bluetooth disconnected")

    fun disconnect() {
        try {
            bluetoothSocket?.close()
        } catch (_: Exception) {}

        bluetoothSocket = null
    }
}
