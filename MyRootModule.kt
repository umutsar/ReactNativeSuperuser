// android/app/src/main/java/com/projectName/MyRootModule.kt
package com.batterycontrol

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import java.io.IOException

class MyRootModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    private var hasRootAccess: Boolean? = null

    override fun getName(): String {
        return "MyRootModule"
    }

    private fun checkRootAccess(): Boolean {
        if (hasRootAccess != null) {
            return hasRootAccess as Boolean
        }
        try {
            val process = Runtime.getRuntime().exec("su")
            process.outputStream.close()
            process.waitFor()
            hasRootAccess = process.exitValue() == 0
        } catch (e: Exception) {
            hasRootAccess = false
        }
        return hasRootAccess as Boolean
    }

    @ReactMethod
    fun runCommand(command: String, promise: Promise) {
        if (!checkRootAccess()) {
            promise.reject("E_NO_ROOT_ACCESS", "Root access is not granted.")
            return
        }

        val output = StringBuilder()
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            process.inputStream.bufferedReader().use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    output.append(line).append("\n")
                }
            }
            process.errorStream.bufferedReader().use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    output.append(line).append("\n")
                }
            }
            process.waitFor()
            promise.resolve(output.toString())
        } catch (e: IOException) {
            e.printStackTrace()
            promise.reject("IOException", e)
        } catch (e: InterruptedException) {
            e.printStackTrace()
            promise.reject("InterruptedException", e)
        } finally {
            process?.destroy()
        }
    }
}
