package com.carscan.proo

import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

class ObdConnection {

    private var socket: Socket? = null
    private var input: InputStream? = null
    private var output: OutputStream? = null

    @Synchronized
    fun connectWifi(host: String, port: Int) {
        disconnect()

        val newSocket = Socket()
        newSocket.connect(
            InetSocketAddress(host, port),
            7000
        )
        newSocket.soTimeout = 4000

        socket = newSocket
        input = newSocket.getInputStream()
        output = newSocket.getOutputStream()
    }

    @Synchronized
    fun setStreams(
        inputStream: InputStream,
        outputStream: OutputStream
    ) {
        disconnect()

        input = inputStream
        output = outputStream
    }

    @Synchronized
    fun sendCommand(command: String): String {

        val out = output
            ?: throw IllegalStateException("Not connected")

        val ins = input
            ?: throw IllegalStateException("Not connected")

        out.write((command.trim() + "\r").toByteArray())
        out.flush()

        val result = StringBuilder()
        val deadline = System.currentTimeMillis() + 4000

        while (System.currentTimeMillis() < deadline) {

            if (ins.available() > 0) {
                val value = ins.read()

                if (value < 0) break

                if (value.toChar() == '>') break

                result.append(value.toChar())
            } else {
                Thread.sleep(20)
            }
        }

        return result.toString().trim()
    }

    fun initialize(): List<String> {
        return listOf(
            "ATZ",
            "ATE0",
            "ATL0",
            "ATS0",
            "ATH0",
            "ATSP0"
        ).map {
            sendCommand(it)
        }
    }

    @Synchronized
    fun disconnect() {
        try { input?.close() } catch (_: Exception) {}
        try { output?.close() } catch (_: Exception) {}
        try { socket?.close() } catch (_: Exception) {}

        input = null
        output = null
        socket = null
    }

    fun isConnected(): Boolean {
        return input != null && output != null
    }
}
