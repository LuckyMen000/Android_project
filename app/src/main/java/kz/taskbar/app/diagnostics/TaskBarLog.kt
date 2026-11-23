package kz.taskbar.app.diagnostics

import android.util.Log
import kz.taskbar.app.BuildConfig

/** Logging is intentionally disabled in release; never log task contents or credentials. */
object TaskBarLog {
    const val APP = "TB_APP"
    const val TASK = "TB_TASK"
    const val DATABASE = "TB_DB"
    const val NETWORK = "TB_NETWORK"
    const val LAB = "TB_LAB"

    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) Log.d(tag, message)
    }

    fun i(tag: String, message: String) {
        if (BuildConfig.DEBUG) Log.i(tag, message)
    }

    fun w(tag: String, message: String, cause: Throwable? = null) {
        if (BuildConfig.DEBUG) Log.w(tag, message, cause)
    }

    fun e(tag: String, message: String, cause: Throwable? = null) {
        if (BuildConfig.DEBUG) Log.e(tag, message, cause)
    }
}
