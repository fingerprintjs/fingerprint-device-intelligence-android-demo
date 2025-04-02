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
        object : TypeToken<Drn.SuspectScore.Minimum>() {}.type,
        MinimumDeserializer()
    )
    .registerTypeAdapter(
        object : TypeToken<Drn.SuspectScore.Maximum>() {}.type,
        MaximumDeserializer()
    )
    .create()

private fun parseBody(body: String): Result<Drn, Unit> {
    return runCatching {
        gson.fromJson(body, DrnData::class.java).data
    }.mapError { }
}

fun Result<Response, Error<*>>.parseDRN(): DrnResponse =
    mapError {
        when (it) {
            is Error.IO -> DrnError.NetworkError(cause = it.cause)
            is Error.Unknown -> DrnError.Unknown
        }
    }.andThen {
        if (it.isSuccessful) {
            val body = it.body
            if (body == null) {
                Err(DrnError.ParseError)
            } else {
                parseBody(body).mapError { DrnError.ParseError }
            }
        } else {
            if (it.code == CODE_VISITOR_ID_NOT_FOUND) {
                Err(DrnError.ApiError.VisitorNotFound)
            } else {
                Err(DrnError.ApiError.UnknownApiError)
            }
        }
    }

class MinimumDeserializer : JsonDeserializer<Drn.SuspectScore.Minimum> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Drn.SuspectScore.Minimum {
        val jsonObject = json?.asJsonObject
            ?: throw NullPointerException("MinimumDeserializer cannot parse null object")
        if (context == null) {
            throw NullPointerException("context cannot be null")
        }

        val value = jsonObject.get("value").asInt
        val signals: List<Drn.SuspectScore.Signal> = SignalListDeserializer().deserialize(
            json = jsonObject.get("signals"),
            typeOfT = List::class.java,
            context = context
        )

        return Drn.SuspectScore.Minimum(value, signals)
    }
}

class MaximumDeserializer : JsonDeserializer<Drn.SuspectScore.Maximum> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Drn.SuspectScore.Maximum {
        val jsonObject = json?.asJsonObject
            ?: throw NullPointerException("MinimumDeserializer cannot parse null object")
        if (context == null) {
            throw NullPointerException("context cannot be null")
        }

        val value = jsonObject.get("value").asInt
        val percentile = jsonObject.get("percentile").asFloat
        val signals: List<Drn.SuspectScore.Signal> = SignalListDeserializer().deserialize(
            json = jsonObject.get("signals"),
            typeOfT = List::class.java,
            context = context
        )

        return Drn.SuspectScore.Maximum(percentile, value, signals)
    }
}

class SignalListDeserializer : JsonDeserializer<List<Drn.SuspectScore.Signal>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<Drn.SuspectScore.Signal> {
        val jsonObject = json?.asJsonObject
            ?: throw NullPointerException("SignalListAdapter cannot parse null object")
        if (context == null) {
            throw NullPointerException("context cannot be null")
        }

        return jsonObject.keySet().map { key ->
            when (key) {
                "vpn" -> context.deserialize(
                    jsonObject.get("vpn"),
                    Drn.SuspectScore.Signal.Vpn::class.java
                ) as Drn.SuspectScore.Signal.Vpn

                "ipBLocklist" -> context.deserialize(
                    jsonObject.get("ipBLocklist"),
                    Drn.SuspectScore.Signal.IpBLocklist::class.java
                ) as Drn.SuspectScore.Signal.IpBLocklist

                else -> Drn.SuspectScore.Signal.SimpleSignal(
                    key = key,
                    value = jsonObject.get(key).asInt
                )
            }
        }
    }
}
