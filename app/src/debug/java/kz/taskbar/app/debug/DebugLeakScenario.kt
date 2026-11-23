package kz.taskbar.app.debug

import android.app.Activity

/** A deliberately unsafe singleton reference for a controlled LeakCanary demonstration.
 * Only included in debug APK; NEVER hold an Activity in a singleton in production.
 */
internal object DebugLeakScenario {
    private var retainedActivity: Activity? = null
    fun capture(activity: Activity) { retainedActivity = activity }
    fun release() { retainedActivity = null }
    fun hasReference(): Boolean = retainedActivity != null
}
