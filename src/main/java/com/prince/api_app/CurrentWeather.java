package com.prince.api_app;

public record CurrentWeather(String city,
                             String condition,
                             double temperatureC,
                             double feelsLikeC,
                             int humidityPercent,
                             double windSpeedKmh,
                             double precipitationMm,
                             boolean daytime,
                             String timezone) {
}
