package pl.poznan.put.student.reminder.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Weather(
    val latitude: Double,
    val longitude: Double,
    @JsonProperty("generationtime_ms")
    val generationtimeMs: Double,
    @JsonProperty("utc_offset_seconds")
    val utcOffsetSeconds: Long,
    val timezone: String,
    @JsonProperty("timezone_abbreviation")
    val timezoneAbbreviation: String,
    val elevation: Long,
    @JsonProperty("daily_units")
    val dailyUnits: DailyUnits,
    val daily: Daily,
)

data class DailyUnits(
    val time: String,
    @JsonProperty("temperature_2m_max")
    val temperature2mMax: String,
)

data class Daily(
    val time: List<String>,
    @JsonProperty("temperature_2m_max")
    val temperature_2m_max: List<Double>,
)
