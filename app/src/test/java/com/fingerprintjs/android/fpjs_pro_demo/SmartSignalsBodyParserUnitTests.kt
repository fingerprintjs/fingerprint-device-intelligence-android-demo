package com.fingerprintjs.android.fpjs_pro_demo

import com.fingerprintjs.android.fpjs_pro_demo.di.components.common.CommonComponentStorage
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignal
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignalInfo
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignalsError
import com.github.michaelbull.result.get
import junit.framework.TestCase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import org.junit.Test

class SmartSignalsBodyParserUnitTests {
    private val parser = CommonComponentStorage.commonComponent.smartSignalsBodyParser()
    private val json = CommonComponentStorage.commonComponent.json()

    @Test
    fun givenCorrectSmartSignal_whenGetSmartSignal_thenSmartSignalParsedCorrectly() {
        testSmartSignalParsing<SmartSignal.Emulator>(
            jsonEntry = """
            {
                "data": {
                    "result": true
                }
            }
            """.trimIndent(),
            expectedKey = "emulator"
        ) {
            TestCase.assertTrue(it is SmartSignalInfo.Success)
            it as SmartSignalInfo.Success
            TestCase.assertEquals(SmartSignal.Emulator(true), it.typedData)
        }
    }

    @Test
    fun givenIncorrectSmartSignal_whenGetSmartSignal_thenSmartSignalReturnsParseError() {
        testSmartSignalParsing<SmartSignal.Emulator>(
            jsonEntry = """
            {
                "data": "unexpected"
            }
            """.trimIndent(),
            expectedKey = "emulator"
        ) {
            TestCase.assertTrue(it is SmartSignalInfo.ParseError)
            it as SmartSignalInfo.ParseError
        }
    }

    @Test
    fun givenCorrectSmartSignalWithUnknownFields_whenGetSmartSignal_thenSmartSignalParsedCorrectly() {
        testSmartSignalParsing<SmartSignal.Emulator>(
            jsonEntry = """
            {
                "unknown": 1,
                "data": {
                    "result": true,
                    "unknown": 2
                }
            }
            """.trimIndent(),
            expectedKey = "emulator"
        ) {
            TestCase.assertTrue(it is SmartSignalInfo.Success)
            it as SmartSignalInfo.Success
            TestCase.assertEquals(SmartSignal.Emulator(true), it.typedData)
        }
    }

    @Test
    fun givenSmartSignalError_whenGetSmartSignal_thenSmartSignalErrorReturned() {
        testSmartSignalParsing<SmartSignal.Emulator>(
            jsonEntry = """
            { 
                "data": {
                    "result": true,
                    "comment": "data object is expected to be ignored"
                },
                "error": {
                    "code": "some_code",
                    "message": "some_message"
                }
            }
            """.trimIndent(),
            expectedKey = "emulator"
        ) {
            TestCase.assertTrue(it is SmartSignalInfo.Error)
        }
    }


    @Test
    fun givenSmartSignalNotPresent_whenGetSmartSignal_thenSmartSignalDisabledReturned() {
        testSmartSignalParsing<SmartSignal.Emulator>(
            jsonEntry = "{\"comment\": \"stub\"}",
            actualKey = "some_key",
            expectedKey = "emulator"
        ) {
            TestCase.assertTrue(it is SmartSignalInfo.Disabled)
        }
    }

    @Test
    fun givenSmartSignalWithWhateverPayloadObject_whenGetSmartSignal_thenRawDataPreserved() {
        val entry = """
                {
                    "unexpected_prop1": "string",
                    "unexpected_prop2": 1
                }
            """.trimIndent()
        testSmartSignalParsing<SmartSignal.Emulator>(
            jsonEntry = entry,
            expectedKey = "emulator"
        ) {
            TestCase.assertTrue(it is SmartSignalInfo.WithRawData)
            it as SmartSignalInfo.WithRawData
            TestCase.assertEquals(entry, json.encodeToString(it.rawData))
        }
    }

    @Test
    fun givenSmartSignalWithWhateverPayloadPrimitive_whenGetSmartSignal_thenRawDataPreserved() {
        val entry = "0"

        testSmartSignalParsing<SmartSignal.Emulator>(
            jsonEntry = entry,
            expectedKey = "emulator"
        ) {
            TestCase.assertTrue(it is SmartSignalInfo.WithRawData)
            it as SmartSignalInfo.WithRawData
            TestCase.assertEquals(entry, json.encodeToString(it.rawData))
        }
    }

    @Test
    fun givenSmartSignalWithWhateverPayloadArray_whenGetSmartSignal_thenRawDataPreserved() {
        val entry = """
        [
            0,
            1,
            2
        ]
        """.trimIndent()

        testSmartSignalParsing<SmartSignal.Emulator>(
            jsonEntry = entry,
            expectedKey = "emulator"
        ) {
            TestCase.assertTrue(it is SmartSignalInfo.WithRawData)
            it as SmartSignalInfo.WithRawData
            TestCase.assertEquals(entry, json.encodeToString(it.rawData))
        }
    }

    @Test
    fun givenCorrentErrorBody_whenParseSmartSignalsError_thenCorrectErrorReturned() {
        val result = parser.parseSmartSignalsError("""
        {
            "error": {
                "message": "some_value",
                "code": "TokenRequired"
            }
        }
        """.trimIndent())
        TestCase.assertEquals(SmartSignalsError.TokenRequired, result.get())
    }

    @Test
    fun givenIncorrectErrorBody_whenParseSmartSignalsError_thenErrorReturned() {
        val result = parser.parseSmartSignalsError("whatever_string")
        TestCase.assertTrue(result.isErr)
    }

    private inline fun <reified T : SmartSignal> testSmartSignalParsing(
        jsonEntry: String,
        expectedKey: String,
        actualKey: String = expectedKey,
        test: (SmartSignalInfo<T>) -> Unit,
    ) {
        val jsonString = """
            {
            "$actualKey": $jsonEntry
            }
        """.trimIndent()
        val jsonObject = json.parseToJsonElement(jsonString) as JsonObject

        val signal = with(parser) { jsonObject.getSmartSignal<T>(expectedKey) }

        test(signal)
    }
}
