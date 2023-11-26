package com.example.englishdictnew1.model;

import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

@Component
public class Dictionary {
    private final Hashtable<String, String> hashtable;

    public Dictionary() {
        hashtable = new Hashtable<>();
        loadWordsFromDatabase();
    }

    private void loadWordsFromDatabase() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/english_dictionary", "root", "");
             Statement stmt = connection.createStatement()) {

            String query = "SELECT Word, Meaning FROM word_meaning_examples";
            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                String word = resultSet.getString("Word").toLowerCase();
                String meaning = resultSet.getString("Meaning");

                if (meaning != null) {
                    hashtable.put(word, meaning);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getMeaning(String word) {
        String meaning = hashtable.get(word.toLowerCase());
        if (meaning != null) {
            return meaning;
        } else {
            List<String> similarWords = findSimilarWords(word.toLowerCase()); 
            if (!similarWords.isEmpty()) {
                StringBuilder suggestion = new StringBuilder("Did you mean any of these? ");
                for (String similarWord : similarWords) {
                    suggestion.append("'").append(similarWord).append("' ");
                }
                return suggestion.toString();
            } else {
                return "Word not found.";
            }
        }
    }

    private List<String> findSimilarWords(String word) {
        Map<String, Integer> wordDistances = new HashMap<>();
        List<String> similarWords = new ArrayList<>();

        for (String dictWord : hashtable.keySet()) {
            int distance = calculateLevenshteinDistance(word, dictWord);
            wordDistances.put(dictWord, distance);
        }

        wordDistances.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(4)
                .forEach(entry -> {
                    if (entry.getValue() <= 2) { 
                        similarWords.add(entry.getKey());
                    }
                });

        return similarWords;
    }

    private int calculateLevenshteinDistance(String word1, String word2) {
        int[][] distance = new int[word1.length() + 1][word2.length() + 1];

        for (int i = 0; i <= word1.length(); i++) {
            distance[i][0] = i;
        }
        for (int j = 0; j <= word2.length(); j++) {
            distance[0][j] = j;
        }

        for (int i = 1; i <= word1.length(); i++) {
            for (int j = 1; j <= word2.length(); j++) {
                int cost = (word1.charAt(i - 1) == word2.charAt(j - 1)) ? 0 : 1;
                distance[i][j] = Math.min(
                        Math.min(distance[i - 1][j] + 1, distance[i][j - 1] + 1),
                        distance[i - 1][j - 1] + cost
                );
            }
        }

        return distance[word1.length()][word2.length()];
    }
}
