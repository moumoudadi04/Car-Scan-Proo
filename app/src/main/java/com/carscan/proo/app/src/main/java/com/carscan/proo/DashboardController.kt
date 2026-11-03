package com.carscan.proo

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class DashboardController(
    private val obdEngine: ObdEngine,
    private val dashboard: DashboardView,
    private val onStatus: (String) -> Unit
) {

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private val running = AtomicBoolean(false)

    fun start() {
        if (!running.compareAndSet(false, true)) return

        onStatus("جاري قراءة بيانات السيارة...")

        executor.execute {
            while (running.get()) {
                try {
                    val speed = obdEngine.readSpeed()
                    val temperature = obdEngine.readTemperature()
                    val rpm = obdEngine.readRpm()

                    handler.post {
                        dashboard.updateData(
                            newSpeed = (speed ?: 0).toFloat(),
                            newTemperature = (temperature ?: 0).toFloat(),
                            newRpm = (rpm ?: 0.0).toFloat()
                        )

                        onStatus(
                            if (speed != null && temperature != null && rpm != null)
                                "البيانات مستلمة من السيارة"
                            else
                                "بعض بيانات السيارة غير متاحة"
                        )
                    }

                } catch (e: Exception) {
                    handler.post {
                        onStatus("خطأ في قراءة بيانات السيارة")
                    }
                }

                try {
                    Thread.sleep(700)
                } catch (_: InterruptedException) {
                    break
                }
            }
        }
    }

    fun stop() {
        running.set(false)
    }
}
