package org.example.cm2601.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User implements Serializable {
    private String username;
    private String password;
    private List<String> readingHistory; // Store article IDs of read articles
    private Set<String> preferences;     // Unique set of preferred categories

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.readingHistory = new ArrayList<>();
        this.preferences = new HashSet<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Get user's reading history
    public List<String> getReadingHistory() {
        return new ArrayList<>(readingHistory);
    }

    // Add article ID to reading history
    public void addReadingHistory(String articleId) {
        if (!readingHistory.contains(articleId)) {
            this.readingHistory.add(articleId);
        }
    }

    // Get user's preferred categories
    public Set<String> getPreferences() {
        return new HashSet<>(preferences);
    }

    // Add a preference category for the user
    public void addPreference(String category) {
        this.preferences.add(category);
    }

    // To display user details
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", readingHistory=" + readingHistory +
                ", preferences=" + preferences +
                '}';
    }
}
