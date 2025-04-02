package com.fingerprintjs.android.fpjs_pro_demo.domain.drn

import com.fingerprintjs.android.fpjs_pro_demo.network.HttpClient.Error
import com.fingerprintjs.android.fpjs_pro_demo.network.HttpClient.Response
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
private const val CODE_VISITOR_ID_NOT_FOUND = 404

private val gson: Gson = GsonBuilder()
    .setDateFormat(DATE_FORMAT)
    .registerTypeAdapter(
        object : TypeToken<DRN.SuspectScore.Minimum>() {}.type,
        MinimumDeserializer()
    )
    .registerTypeAdapter(
        object : TypeToken<DRN.SuspectScore.Maximum>() {}.type,
        MaximumDeserializer()
    )
    .create()

private fun parseBody(body: String): Result<DRN, Unit> {
    return runCatching {
        gson.fromJson(body, DRNData::class.java).data
    }.mapError { }
}

fun Result<Response, Error<*>>.parseDRN(): DRNResponse =
    mapError {
        when (it) {
            is Error.IO -> DRNError.NetworkError(cause = it.cause)
            is Error.Unknown -> DRNError.Unknown
        }
    }.andThen {
        if (it.isSuccessful) {
            val body = it.body
            if (body == null) {
                Err(DRNError.ParseError)
            } else {
                parseBody(body).mapError { DRNError.ParseError }
            }
        } else {
            if (it.code == CODE_VISITOR_ID_NOT_FOUND) {
                Err(DRNError.VisitorNotFound)
            } else {
                Err(DRNError.UnknownApiError)
            }
        }
    }

class MinimumDeserializer : JsonDeserializer<DRN.SuspectScore.Minimum> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): DRN.SuspectScore.Minimum {
        val jsonObject = json?.asJsonObject
            ?: throw NullPointerException("MinimumDeserializer cannot parse null object")
        if (context == null) {
            throw NullPointerException("context cannot be null")
        }

        val value = jsonObject.get("value").asInt
        val signals: List<DRN.SuspectScore.Signal> = SignalListDeserializer().deserialize(
            json = jsonObject.get("signals"),
            typeOfT = List::class.java,
            context = context
        )

        return DRN.SuspectScore.Minimum(value, signals)
    }
}

class MaximumDeserializer : JsonDeserializer<DRN.SuspectScore.Maximum> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): DRN.SuspectScore.Maximum {
        val jsonObject = json?.asJsonObject
            ?: throw NullPointerException("MinimumDeserializer cannot parse null object")
        if (context == null) {
            throw NullPointerException("context cannot be null")
        }

        val value = jsonObject.get("value").asInt
        val percentile = jsonObject.get("percentile").asFloat
        val signals: List<DRN.SuspectScore.Signal> = SignalListDeserializer().deserialize(
            json = jsonObject.get("signals"),
            typeOfT = List::class.java,
            context = context
        )

        return DRN.SuspectScore.Maximum(percentile, value, signals)
    }
}

class SignalListDeserializer : JsonDeserializer<List<DRN.SuspectScore.Signal>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<DRN.SuspectScore.Signal> {
        val jsonObject = json?.asJsonObject
            ?: throw NullPointerException("SignalListAdapter cannot parse null object")
        if (context == null) {
            throw NullPointerException("context cannot be null")
        }

        return jsonObject.keySet().map { key ->
            when (key) {
                "vpn" -> context.deserialize(
                    jsonObject.get("vpn"),
                    DRN.SuspectScore.Signal.VPN::class.java
                ) as DRN.SuspectScore.Signal.VPN

                "ipBLocklist" -> context.deserialize(
                    jsonObject.get("ipBLocklist"),
                    DRN.SuspectScore.Signal.IpBLocklist::class.java
                ) as DRN.SuspectScore.Signal.IpBLocklist

                else -> DRN.SuspectScore.Signal.SimpleSignal(
                    key = key,
                    value = jsonObject.get(key).asInt
                )
            }
        }
    }
}
