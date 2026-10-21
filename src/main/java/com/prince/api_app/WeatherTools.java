package com.prince.api_app;

import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class WeatherTools {

    private static final String GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search";

    private final RestClient weatherClient;

    public WeatherTools(RestClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    @Tool(description = "Get the current weather for a city. Use this before recommending what to read.")
    public CurrentWeather getCurrentWeather(
            @ToolParam(description = "City name, e.g. Nairobi") String city) {

        if (city == null || city.isBlank() || city.length() > 100) {
            throw new IllegalArgumentException("City name must be 1-100 characters");
        }

        GeoResult place = geocode(city);

        ForecastResponse forecast = weatherClient.get()
                .uri(uri -> uri
                        .path("/forecast")
                        .queryParam("latitude", "{lat}")
                        .queryParam("longitude", "{lon}")
                        .queryParam("current", "temperature_2m,relative_humidity_2m,apparent_temperature,"
                                + "is_day,precipitation,weather_code,wind_speed_10m")
                        .queryParam("timezone", "auto")
                        .build(place.latitude(), place.longitude()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new IllegalStateException("Weather service is unavailable");
                })
                .body(ForecastResponse.class);

        if (forecast == null || forecast.current() == null) {
            throw new IllegalStateException("Weather service returned no data for " + place.name());
        }

        Current current = forecast.current();
        return new CurrentWeather(
                place.name(),
                describeWeatherCode(current.weatherCode()),
                current.temperature(),
                current.apparentTemperature(),
                current.humidity(),
                current.windSpeed(),
                current.precipitation(),
                current.isDay() == 1,
                forecast.timezone());
    }

    private GeoResult geocode(String city) {
        GeoSearchResponse search = weatherClient.get()
                .uri(GEOCODING_URL + "?name={name}&count=1&language=en&format=json", city)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new IllegalStateException("Geocoding service is unavailable");
                })
                .body(GeoSearchResponse.class);

        if (search == null || search.results() == null || search.results().isEmpty()) {
            throw new IllegalArgumentException("No location found for city: " + city);
        }
        return search.results().get(0);
    }

    // WMO 4677 weather interpretation codes, as published by Open-Meteo.
    private static String describeWeatherCode(int code) {
        return switch (code) {
            case 0 -> "Clear sky";
            case 1 -> "Mainly clear";
            case 2 -> "Partly cloudy";
            case 3 -> "Overcast";
            case 45, 48 -> "Fog";
            case 51, 53, 55, 56, 57 -> "Drizzle";
            case 61, 63, 65 -> "Rain";
            case 66, 67 -> "Freezing rain";
            case 71, 73, 75, 77 -> "Snow";
            case 80, 81, 82 -> "Rain showers";
            case 85, 86 -> "Snow showers";
            case 95, 96, 99 -> "Thunderstorm";
            default -> "Unknown";
        };
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record GeoSearchResponse(List<GeoResult> results) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record GeoResult(String name, double latitude, double longitude, String country) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ForecastResponse(String timezone, Current current) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Current(
            @JsonProperty("temperature_2m") double temperature,
            @JsonProperty("relative_humidity_2m") int humidity,
            @JsonProperty("apparent_temperature") double apparentTemperature,
            @JsonProperty("is_day") int isDay,
            double precipitation,
            @JsonProperty("weather_code") int weatherCode,
            @JsonProperty("wind_speed_10m") double windSpeed) {
    }
}
