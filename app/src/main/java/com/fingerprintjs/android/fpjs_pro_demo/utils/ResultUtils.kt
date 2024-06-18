package com.fingerprintjs.android.fpjs_pro_demo.utils

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.runCatching
import kotlin.coroutines.cancellation.CancellationException

/**
 * This file contains alternatives to canonical Result's extensions to respect Kotlin's structured concurrency.
 * Suspend modifier is not necessary, but I don't see a reason to call these extensions outside of coroutine scope.
 */

suspend inline fun <T, R> T.runCatchingCancellable(block: T.() -> R): Result<R, Throwable> {
    return this.runCatching { this.block() }.toCancellable()
}

@Suppress("RedundantSuspendModifier")
suspend inline fun <T, E> Result<T, E>.toCancellable(): Result<T, E> {
    return this.onFailure { if (it is CancellationException) throw it }
}
