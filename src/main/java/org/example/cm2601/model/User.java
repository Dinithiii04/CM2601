package org.example.cm2601.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User implements Serializable {
    private String username;
    private String password;
    private List<String> readingHistory;
    private Set<String> preferences;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.readingHistory = new ArrayList<>();
        this.preferences = new HashSet<>();  // Initialize with no preferences
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getReadingHistory() {
        return new ArrayList<>(readingHistory);
    }

    public void addReadingHistory(String articleId) {
        if (!readingHistory.contains(articleId)) {
            this.readingHistory.add(articleId);
        }
    }

    public Set<String> getPreferences() {
        return new HashSet<>(preferences);
    }

    public void addPreference(String category) {
        this.preferences.add(category);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", readingHistory=" + readingHistory +
                ", preferences=" + preferences +
                '}';
    }
}
