package com.example.demo;

import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextAnalyzerUtils {

    // Contar palabras usando Streams
    public static int getWordCount(String text) {
        return (int) Arrays.stream(text.trim().split("\\s+"))
                .filter(word -> !word.isEmpty())
                .count();
    }

    // Contar caracteres sin espacios
    public static int getCharCount(String text) {
        return (int) text.codePoints().filter(c -> !Character.isWhitespace(c)).count();
    }

    // Extraer correos electrónicos
    public static String getEmails(String text) {
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        List<String> emails = emailPattern.matcher(text)
                .results()
                .map(MatchResult::group)
                .collect(Collectors.toList());

        return emails.isEmpty() ? "-" : String.join(", ", emails);
    }

    // Extraer URLs
    public static String getURLs(String text) {
        Pattern urlPattern = Pattern.compile("(?:(?:https?|ftp)://|www\\.)\\S+\\.[a-zA-Z]{2,}(?:/\\S*)?");
        List<String> urls = urlPattern.matcher(text)
                .results()
                .map(MatchResult::group)
                .collect(Collectors.toList());

        return urls.isEmpty() ? "-" : String.join(", ", urls);
    }
}
