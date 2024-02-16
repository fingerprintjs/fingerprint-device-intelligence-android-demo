package com.fingerprintjs.android.fpjs_pro_demo.constants

object Protected {
    init {
        System.loadLibrary("jni-helper")
    }

    val apiKey: String
        get() = stringFromJNI()

    private external fun stringFromJNI(): String
}
