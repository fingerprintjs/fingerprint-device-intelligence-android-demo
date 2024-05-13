package com.fingerprintjs.android.fpjs_pro_demo.constants

object Protected {
    init {
        System.loadLibrary("jni-helper")
    }

    val apiKey: String
        get() = stringsFromJNI()[0]!!

    val signupUrl: String
        get() = stringsFromJNI()[1]!!

    val smartSignalsBaseUrl: String?
        get() = stringsFromJNI()[2]

    val smartSignalsOrigin: String?
        get() = stringsFromJNI()[3]

    private external fun stringFromJNI(): String

    private fun stringsFromJNI(): List<String?> {
        return stringFromJNI().split("\n")
            .map { it.takeIf { it != PLACEHOLDER_STRING } }
    }

    private const val PLACEHOLDER_STRING = "__placeholder__"
}
