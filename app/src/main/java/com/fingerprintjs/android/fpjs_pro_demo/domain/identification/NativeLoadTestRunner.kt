package com.fingerprintjs.android.fpjs_pro_demo.domain.identification

import android.os.SystemClock
import android.util.Log
import com.fingerprintjs.android.fpjs_pro.Configuration
import com.fingerprintjs.android.fpjs_pro.Fingerprint
import com.fingerprintjs.android.fpjs_pro.FingerprintException
import com.fingerprintjs.android.fpjs_pro.FingerprintFactory
import com.fingerprintjs.android.fpjs_pro.FingerprintResponse
import com.fingerprintjs.android.fpjs_pro.NativeLoaderTestHooks
import com.fingerprintjs.android.fpjs_pro_demo.App
import com.fingerprintjs.android.fpjs_pro_demo.constants.Credentials
import com.fingerprintjs.android.fpjs_pro_demo.domain.custom_api_keys.CustomApiKeysUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

enum class NativeLoadTestCase(
    val title: String,
    val description: String,
) {
    ImmediateIdentifyAfterInit(
        title = "1. Init → identify immediately",
        description = "Create client (starts preload) and call getVisitorId right away.",
    ),
    FailLoadThenIdentify(
        title = "2. Fail native load → identify",
        description = "Init preload fails once; identify retries load and should get real native signals (not -2).",
    ),
    SlowLoadThenIdentify(
        title = "3. Slow native load (3s) → identify",
        description = "Load sleeps 3s (await timeout is 1s). Identify should still return.",
    ),
    HungLoadDoubleIdentify(
        title = "4. Hung load + double identify",
        description = "Load never finishes; fire two identifies. Should not ANR; native stays unloaded (signals s=-2).",
    ),
}

data class NativeLoadTestResult(
    val case: NativeLoadTestCase,
    val log: String,
    val success: Boolean,
)

class NativeLoadTestRunner @Inject constructor(
    private val app: App,
    private val customApiKeysUseCase: CustomApiKeysUseCase,
    @param:Named("networkTimeoutMillis") private val networkTimeoutMillis: Int,
) {
    suspend fun run(case: NativeLoadTestCase): NativeLoadTestResult = withContext(Dispatchers.IO) {
        val lines = mutableListOf<String>()
        val appendLog: (String) -> Unit = { msg ->
            val line = "[${SystemClock.elapsedRealtime()}] $msg"
            lines += line
            Log.i(TAG, line)
        }

        var success = false
        NativeLoaderTestHooks.enableRequestSignalCapture()
        try {
            appendLog("Starting: ${case.title}")
            when (case) {
                NativeLoadTestCase.ImmediateIdentifyAfterInit -> {
                    NativeLoaderTestHooks.resetToRealLoad()
                    appendLog("hooks: resetToRealLoad, isLoaded=${NativeLoaderTestHooks.isLoaded()}")
                    val client = createFreshClient(appendLog)
                    appendLog("calling getVisitorId immediately…")
                    success = identifyOnce(client, appendLog)
                }

                NativeLoadTestCase.FailLoadThenIdentify -> {
                    NativeLoaderTestHooks.simulateLoadFailure()
                    appendLog("hooks: simulateLoadFailure (first attempt only)")
                    val client = createFreshClient(appendLog)
                    Thread.sleep(300)
                    appendLog("after init preload, isLoaded=${NativeLoaderTestHooks.isLoaded()} (expect false)")
                    if (NativeLoaderTestHooks.isLoaded()) {
                        appendLog("FAIL: init preload should have failed")
                        success = false
                    } else {
                        appendLog("calling getVisitorId (should retry native load)…")
                        val identified = identifyOnce(client, appendLog)
                        val loadedAfter = NativeLoaderTestHooks.isLoaded()
                        appendLog("after identify, isLoaded=$loadedAfter (expect true)")
                        success = identified && loadedAfter
                        if (identified && !loadedAfter) {
                            appendLog("FAIL: got visitorId but native still not loaded (−2 on native signals)")
                        }
                    }
                }

                NativeLoadTestCase.SlowLoadThenIdentify -> {
                    NativeLoaderTestHooks.simulateSlowLoad(delayMs = 3_000)
                    appendLog("hooks: simulateSlowLoad(3000), isLoaded=${NativeLoaderTestHooks.isLoaded()}")
                    val client = createFreshClient(appendLog)
                    success = identifyOnce(client, appendLog)
                    appendLog("after identify, isLoaded=${NativeLoaderTestHooks.isLoaded()} (may still be loading)")
                }

                NativeLoadTestCase.HungLoadDoubleIdentify -> {
                    NativeLoaderTestHooks.simulateHungLoad()
                    appendLog("hooks: simulateHungLoad, isLoaded=${NativeLoaderTestHooks.isLoaded()}")
                    val client = createFreshClient(appendLog)
                    coroutineScope {
                        val first = async { identifyOnce(client, appendLog, label = "identify#1") }
                        val second = async {
                            Thread.sleep(50)
                            identifyOnce(client, appendLog, label = "identify#2")
                        }
                        val r1 = first.await()
                        val r2 = second.await()
                        // Hung load: native must stay unloaded; identify should still return.
                        val loaded = NativeLoaderTestHooks.isLoaded()
                        appendLog("double identify done: #1=$r1 #2=$r2 isLoaded=$loaded (expect false)")
                        success = (r1 || r2) && !loaded
                        if ((r1 || r2) && loaded) {
                            appendLog("FAIL: hung load should leave native unloaded")
                        }
                    }
                }
            }
        } catch (t: Throwable) {
            appendLog("ERROR: ${t::class.java.simpleName}: ${t.message}")
            success = false
        } finally {
            NativeLoaderTestHooks.resetToRealLoad()
            NativeLoaderTestHooks.disableRequestSignalCapture()
            appendLog("hooks reset to real load (cleanup)")
        }

        NativeLoadTestResult(
            case = case,
            log = lines.joinToString("\n"),
            success = success,
        )
    }

    private suspend fun createFreshClient(appendLog: (String) -> Unit): Fingerprint {
        val keys = customApiKeysUseCase.state.first()
        val t0 = SystemClock.elapsedRealtime()
        val client = FingerprintFactory(app).createInstance(
            Configuration(
                apiKey = if (keys.enabled) keys.public else Credentials.apiKey,
                endpointUrl = if (keys.enabled) keys.region.endpointUrl else Credentials.endpointUrl,
                allowUseOfLocationData = true,
            )
        )
        appendLog("createInstance() returned in ${SystemClock.elapsedRealtime() - t0}ms (preload started)")
        appendLog("isLoaded right after create=${NativeLoaderTestHooks.isLoaded()}")
        return client
    }

    private suspend fun identifyOnce(
        client: Fingerprint,
        appendLog: (String) -> Unit,
        label: String = "identify",
    ): Boolean {
        val t0 = SystemClock.elapsedRealtime()
        return try {
            val response = client.getVisitorId(networkTimeoutMillis)
            val elapsed = SystemClock.elapsedRealtime() - t0
            logIdentifySuccess(label, elapsed, response, appendLog)
            response.visitorId.isNotBlank()
        } catch (e: FingerprintException) {
            val elapsed = SystemClock.elapsedRealtime() - t0
            appendLog("$label FAIL in ${elapsed}ms error=${e.error}")
            appendLog("$label native isLoaded=${NativeLoaderTestHooks.isLoaded()}")
            logNativeSignalSnapshot(label, appendLog)
            false
        }
    }

    private fun logIdentifySuccess(
        label: String,
        elapsed: Long,
        response: FingerprintResponse,
        appendLog: (String) -> Unit,
    ) {
        appendLog("$label OK in ${elapsed}ms")
        appendLog("$label visitorId='${response.visitorId}' (blank=${response.visitorId.isBlank()})")
        appendLog("$label eventId='${response.eventId}'")
        appendLog("$label suspectScore=${response.suspectScore}")
        appendLog("$label errorMessage=${response.errorMessage}")
        appendLog("$label sealedResultPresent=${!response.sealedResult.isNullOrBlank()} len=${response.sealedResult?.length ?: 0}")
        appendLog("$label native isLoaded=${NativeLoaderTestHooks.isLoaded()}")
        logNativeSignalSnapshot(label, appendLog)
        appendLog("$label asJson=${response.asJson}")
    }

    private fun logNativeSignalSnapshot(label: String, appendLog: (String) -> Unit) {
        val signals = NativeLoaderTestHooks.lastRequestSignals(NATIVE_SIGNAL_KEYS)
        NATIVE_SIGNAL_KEYS.forEach { key ->
            appendLog("$label request[$key]=${signals[key]}")
        }
    }

    private companion object {
        const val TAG = "NativeLoadTest"
        // a58 radioVersion, a62 networkCellInfo, a89 networkRoaming, a90 developerSettings (native-related)
        val NATIVE_SIGNAL_KEYS = listOf("a58", "a62", "a89", "a90")
    }
}
