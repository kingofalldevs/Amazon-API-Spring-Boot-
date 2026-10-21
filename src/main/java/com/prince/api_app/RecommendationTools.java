package com.prince.api_app;

import java.util.Comparator;
import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class RecommendationTools {

    private final WeatherTools weatherTools;
    private final BookRepository bookRepository;

    public RecommendationTools(WeatherTools weatherTools, BookRepository bookRepository) {
        this.weatherTools = weatherTools;
        this.bookRepository = bookRepository;
    }

    @Tool(description = "Recommend books from the library to read, based on the current weather in a city.")
    public List<Book> recommendBooksForWeather(
            @ToolParam(description = "City name, e.g. Nairobi") String city,
            @ToolParam(description = "How many books to recommend, 1-10") int limit) {

        int max = Math.max(1, Math.min(limit, 10));
        CurrentWeather weather = weatherTools.getCurrentWeather(city);
        Mood mood = moodFor(weather.condition());

        return bookRepository.findAll().stream()
                .sorted(Comparator
                        .comparingInt((Book book) -> score(book, mood)).reversed()
                        .thenComparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER))
                .limit(max)
                .toList();
    }

    private enum Mood { COZY, ENERGIZED, ADVENTUROUS }

    private static Mood moodFor(String condition) {
        return switch (condition) {
            case "Clear sky", "Mainly clear", "Partly cloudy" -> Mood.ENERGIZED;
            case "Rain", "Rain showers", "Drizzle", "Thunderstorm", "Fog", "Overcast" -> Mood.COZY;
            case "Snow", "Snow showers", "Freezing rain" -> Mood.ADVENTUROUS;
            default -> Mood.ENERGIZED;
        };
    }

    private static int score(Book book, Mood mood) {
        String text = (book.getTitle() + " " + book.getAuthor()).toLowerCase();
        return switch (mood) {
            case COZY -> containsAny(text, "winter", "night", "home", "quiet", "rain", "cat", "tea") ? 2 : 0;
            case ADVENTUROUS -> containsAny(text, "mountain", "journey", "north", "ice", "wild", "sea") ? 2 : 0;
            case ENERGIZED -> containsAny(text, "sun", "summer", "day", "light", "road", "walk") ? 2 : 0;
        };
    }

    private static boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
