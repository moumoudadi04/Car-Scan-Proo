package com.carscan.proo

class EcuManager(
    private val connection: ObdConnection
) {

    fun readVin(): String {
        return connection.sendCommand("0902")
    }

    fun readFreezeFrame(): String {
        return connection.sendCommand("0202")
    }

    fun readSupportedPids(): String {
        return connection.sendCommand("0100")
    }

    fun readStoredCodes(): String {
        return connection.sendCommand("03")
    }

    fun readPendingCodes(): String {
        return connection.sendCommand("07")
    }

    fun readPermanentCodes(): String {
        return connection.sendCommand("0A")
    }
}
