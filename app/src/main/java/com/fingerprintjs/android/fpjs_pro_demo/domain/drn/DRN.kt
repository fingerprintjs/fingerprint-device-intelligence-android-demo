package com.fingerprintjs.android.fpjs_pro_demo.domain.drn

import com.google.gson.annotations.SerializedName
import java.util.Date

data class DRN(
    @SerializedName("regionalActivity")
    val regionalActivity: RegionalActivity?,

    @SerializedName("suspectScore")
    val suspectScore: SuspectScore?,

    @SerializedName("timestamps")
    val timestamps: Timestamps?,
) {
    data class RegionalActivity(
        @SerializedName("startDate")
        val startDate: Date,

        @SerializedName("endDate")
        val endDate: Date,

        @SerializedName("countries")
        val countries: List<Country>,
    ) {
        data class Country(
            @SerializedName("code")
            val code: String,

            @SerializedName("detectors")
            val detectors: List<Detector>
        ) {
            data class Detector(
                @SerializedName("type")
                val type: Type,

                @SerializedName("activityPercentage")
                val activityPercentage: Float
            ) {
                enum class Type {
                    @SerializedName("origin")
                    ORIGIN,

                    @SerializedName("ip")
                    IP
                }
            }
        }
    }

    data class SuspectScore(
        @SerializedName("startDate")
        val startDate: Date,

        @SerializedName("endDate")
        val endDate: Date,

        @SerializedName("minimum")
        val minimum: Minimum,

        @SerializedName("maximum")
        val maximum: Maximum,
    ) {
        sealed class Signal {
            data class VPN(
                @SerializedName("timezoneMismatch")
                val timezoneMismatch: Int? = null,

                @SerializedName("publicVPN")
                val publicVPN: Int? = null,

                @SerializedName("auxiliaryMobile")
                val auxiliaryMobile: Int? = null,
            ) : Signal()

            data class IpBLocklist(
                @SerializedName("emailSpam")
                val emailSpam: Int? = null,

                @SerializedName("attackSource")
                val attackSource: Int? = null,
            ) : Signal()

            data class SimpleSignal(
                val key: String,
                val value: Int,
            ) : Signal()
        }

        data class Minimum(
            @SerializedName("value")
            val value: Int,

            @SerializedName("signals")
            val signals: List<Signal>,
        )

        data class Maximum(
            @SerializedName("percentile")
            val percentile: Float,

            @SerializedName("value")
            val value: Int,

            @SerializedName("signals")
            val signals: List<Signal>,
        )
    }

    data class Timestamps(
        @SerializedName("firstSeenAt")
        val firstSeenAt: Date,

        @SerializedName("lastSeenAt")
        val lastSeenAt: Date,
    )
}