package org.example.cm2601.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class User {
    private String username;
    private String password;
    private Map<String, Integer> preferences = new ConcurrentHashMap<>();   //Tracks user preferences for news categories
    private List<String> readingHistory = new ArrayList<>();     //Stores the titles of articles the user has read

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.preferences = new HashMap<>();   //Initialize empty map
        this.readingHistory = new ArrayList<>();   //Initialize empty array
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


    //Setters
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
