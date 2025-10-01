package com.fingerprintjs.android.fpjs_pro_demo.utils

import junit.framework.TestCase
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Test

class JsonUtilsTests {
    @Test
    fun givenNumber_whenToJsonElement_thenJsonPrimitiveReturned() {
        val result = 42.toJsonElement()
        TestCase.assertTrue(result is JsonPrimitive)
        TestCase.assertEquals(JsonPrimitive(42), result)
    }

    @Test
    fun givenDouble_whenToJsonElement_thenJsonPrimitiveReturned() {
        val result = 3.14.toJsonElement()
        TestCase.assertTrue(result is JsonPrimitive)
        TestCase.assertEquals(JsonPrimitive(3.14), result)
    }

    @Test
    fun givenBoolean_whenToJsonElement_thenJsonPrimitiveReturned() {
        val result = true.toJsonElement()
        TestCase.assertTrue(result is JsonPrimitive)
        TestCase.assertEquals(JsonPrimitive(true), result)
    }

    @Test
    fun givenString_whenToJsonElement_thenJsonPrimitiveReturned() {
        val result = "test".toJsonElement()
        TestCase.assertTrue(result is JsonPrimitive)
        TestCase.assertEquals(JsonPrimitive("test"), result)
    }

    @Test
    fun givenNull_whenToJsonElement_thenJsonNullReturned() {
        val result = null.toJsonElement()
        TestCase.assertTrue(result is JsonNull)
    }

    @Test
    fun givenArray_whenToJsonElement_thenJsonArrayReturned() {
        val array = arrayOf(1, 2, 3)
        val result = array.toJsonElement()
        TestCase.assertTrue(result is JsonArray)
        TestCase.assertEquals(3, (result as JsonArray).size)
    }

    @Test
    fun givenList_whenToJsonElement_thenJsonArrayReturned() {
        val list = listOf("a", "b", "c")
        val result = list.toJsonElement()
        TestCase.assertTrue(result is JsonArray)
        TestCase.assertEquals(3, (result as JsonArray).size)
    }

    @Test
    fun givenMap_whenToJsonElement_thenJsonObjectReturned() {
        val map = mapOf("key" to "value", "number" to 42)
        val result = map.toJsonElement()
        TestCase.assertTrue(result is JsonObject)
        TestCase.assertEquals(2, (result as JsonObject).size)
    }

    @Test
    fun givenEmptyArray_whenToJsonArray_thenEmptyJsonArrayReturned() {
        val array = arrayOf<Any>()
        val result = array.toJsonArray()
        TestCase.assertTrue(result.isEmpty())
    }

    @Test
    fun givenEmptyList_whenToJsonArray_thenEmptyJsonArrayReturned() {
        val list = listOf<Any>()
        val result = list.toJsonArray()
        TestCase.assertTrue(result.isEmpty())
    }

    @Test
    fun givenEmptyMap_whenToJsonObject_thenEmptyJsonObjectReturned() {
        val map = emptyMap<String, Any>()
        val result = map.toJsonObject()
        TestCase.assertTrue(result.isEmpty())
    }

    @Test
    fun givenNestedStructure_whenToJsonElement_thenCorrectJsonReturned() {
        val nested = mapOf(
            "string" to "value",
            "number" to 123,
            "boolean" to true,
            "array" to listOf(1, 2, 3),
            "nested" to mapOf("inner" to "data")
        )
        val result = nested.toJsonElement()
        TestCase.assertTrue(result is JsonObject)
        val obj = result as JsonObject
        TestCase.assertEquals(5, obj.size)
        TestCase.assertTrue(obj["array"] is JsonArray)
        TestCase.assertTrue(obj["nested"] is JsonObject)
    }

    @Test
    fun givenMapWithNonStringKey_whenToJsonObject_thenNonStringKeysIgnored() {
        val map = mapOf<Any, Any>(
            "valid" to "included",
            123 to "ignored"
        )
        val result = map.toJsonObject()
        TestCase.assertEquals(1, result.size)
        TestCase.assertTrue(result.containsKey("valid"))
        TestCase.assertFalse(result.containsKey("123"))
    }

    @Test
    fun givenMixedTypeArray_whenToJsonArray_thenAllTypesConverted() {
        val array = arrayOf(1, "string", true, null)
        val result = array.toJsonArray()
        TestCase.assertEquals(4, result.size)
        TestCase.assertTrue(result[0] is JsonPrimitive)
        TestCase.assertTrue(result[1] is JsonPrimitive)
        TestCase.assertTrue(result[2] is JsonPrimitive)
        TestCase.assertTrue(result[3] is JsonNull)
    }

    @Test
    fun givenMixedTypeList_whenToJsonArray_thenAllTypesConverted() {
        val list = listOf(1, "string", true, null)
        val result = list.toJsonArray()
        TestCase.assertEquals(4, result.size)
        TestCase.assertTrue(result[0] is JsonPrimitive)
        TestCase.assertTrue(result[1] is JsonPrimitive)
        TestCase.assertTrue(result[2] is JsonPrimitive)
        TestCase.assertTrue(result[3] is JsonNull)
    }

    @Test
    fun givenMapWithNullValues_whenToJsonObject_thenNullsConvertedToJsonNull() {
        val map = mapOf(
            "present" to "value",
            "absent" to null
        )
        val result = map.toJsonObject()
        TestCase.assertEquals(2, result.size)
        TestCase.assertTrue(result["present"] is JsonPrimitive)
        TestCase.assertTrue(result["absent"] is JsonNull)
    }
}
