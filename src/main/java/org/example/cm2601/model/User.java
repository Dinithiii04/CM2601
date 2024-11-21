package org.example.cm2601.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String username;
    private String password;
    private Map<String, Integer> preferences = new HashMap<>(); // Initialize here
    private List<String> readingHistory = new ArrayList<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.preferences = new HashMap<>();
        this.readingHistory = new ArrayList<>();
    }




    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, Integer> getPreferences() {
        return preferences;
    }
    public List<String> getReadingHistory() {
        return readingHistory;
    }

    // Methods to update preferences
    public void addPreference(String category) {
        preferences.put(category, preferences.getOrDefault(category, 0) + 1);
    }

    public void setPreferences(Map<String, Integer> preferences) {
        this.preferences = preferences;
    }

    public void setReadingHistory(List<String> readingHistory) {
        this.readingHistory = readingHistory;
    }

    public void addToReadingHistory(String articleTitle) {
        readingHistory.add(articleTitle);
    }
}
