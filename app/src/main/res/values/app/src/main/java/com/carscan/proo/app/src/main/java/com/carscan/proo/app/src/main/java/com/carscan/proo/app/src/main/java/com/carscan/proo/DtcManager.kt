package com.carscan.proo

class DtcManager(
    private val connection: ObdConnection
) {

    fun readCodes(): List<String> {

        val response = connection.sendCommand("03")

        val hex = response.uppercase()
            .replace(Regex("[^0-9A-F]"), "")

        val index = hex.indexOf("43")

        if (index < 0) return emptyList()

        val data = hex.substring(index + 2)

        val codes = mutableListOf<String>()

        for (pair in data.chunked(4)) {

            if (pair.length != 4) continue

            val a = pair.substring(0, 2).toIntOrNull(16)
                ?: continue

            val b = pair.substring(2, 4).toIntOrNull(16)
                ?: continue

            if (a == 0 && b == 0) continue

            val family = when ((a shr 6) and 3) {
                0 -> 'P'
                1 -> 'C'
                2 -> 'B'
                else -> 'U'
            }

            val digit = (a shr 4) and 3
            val digit2 = a and 15

            codes.add(
                "$family$digit" +
                digit2.toString(16).uppercase() +
                (b shr 4).toString(16).uppercase() +
                (b and 15).toString(16).uppercase()
            )
        }

        return codes.distinct()
    }

    fun clearCodes(): String {

        return connection.sendCommand("04")
    }
}
