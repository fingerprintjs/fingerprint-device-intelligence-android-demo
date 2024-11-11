package com.fingerprintjs.android.fpjs_pro_demo.di.components.common

object CommonComponentStorage {
    val commonComponent: CommonComponent by lazy { DaggerCommonComponent.create() }
}