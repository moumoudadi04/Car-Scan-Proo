package com.carscan.proo

data class VehicleData(
    val speed: Int?,
    val temperature: Int?,
    val rpm: Double?,
    val engineLoad: Int?,
    val voltage: Double?
)

class ObdEngine(
    private val connection: ObdConnection
) {

    private fun readBytes(
        response: String,
        service: String,
        pid: String,
        count: Int
    ): List<Int>? {

        val hex = response.uppercase()
            .replace(Regex("[^0-9A-F]"), "")

        val marker = service + pid
        val index = hex.indexOf(marker)

        if (index < 0) return null

        val start = index + marker.length
        val end = start + count * 2

        if (end > hex.length) return null

        return hex.substring(start, end)
            .chunked(2)
            .mapNotNull { it.toIntOrNull(16) }
            .takeIf { it.size == count }
    }

    private fun readSingle(
        command: String,
        pid: String
    ): Int? {

        val response = connection.sendCommand(command)

        return readBytes(response, "41", pid, 1)
            ?.firstOrNull()
    }

    fun readSpeed(): Int? {
        return readSingle("010D", "0D")
    }

    fun readTemperature(): Int? {
        return readSingle("0105", "05")
            ?.minus(40)
    }

    fun readRpm(): Double? {

        val response = connection.sendCommand("010C")

        val bytes = readBytes(response, "41", "0C", 2)
            ?: return null

        return (bytes[0] * 256 + bytes[1]) / 4.0
    }

    fun readEngineLoad(): Int? {
        val value = readSingle("0104", "04")
            ?: return null

        return value * 100 / 255
    }

    fun readVoltage(): Double? {

        val response = connection.sendCommand("0142")

        val bytes = readBytes(response, "41", "42", 2)
            ?: return null

        val value = bytes[0] * 256 + bytes[1]

        return value / 1000.0
    }

    fun readAll(): VehicleData {

        return VehicleData(
            speed = readSpeed(),
            temperature = readTemperature(),
            rpm = readRpm(),
            engineLoad = readEngineLoad(),
            voltage = readVoltage()
        )
    }
}
