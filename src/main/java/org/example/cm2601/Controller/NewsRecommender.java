package org.example.cm2601.Controller;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.example.cm2601.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsRecommender {

    private UserDatabase userDatabase;

    public NewsRecommender(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    // Method to reorder news articles based on user preferences
    // Synchronized method to ensure thread safety in concurrent environments
    public synchronized JsonArray recommendNews(JsonArray categorizedArticles, String username) {
        // Fetch user preferences
        List<Map.Entry<String, Integer>> sortedPreferences = getSortedUserPreferences(username);

        //Separate articles into a map categorized by their "category" property
        Map<String, List<JsonObject>> categorizedMap = new HashMap<>();
        for (int i = 0; i < categorizedArticles.size(); i++) {
            JsonObject article = categorizedArticles.get(i).getAsJsonObject();
            String category = article.get("category").getAsString();
            // Group articles by their category
            categorizedMap.computeIfAbsent(category, k -> new ArrayList<>()).add(article);
        }

        // Sort articles within each category by score (descending)
        for (Map.Entry<String, List<JsonObject>> entry : categorizedMap.entrySet()) {
            entry.getValue().sort((a, b) -> Float.compare(
                    b.get("score").getAsFloat(),
                    a.get("score").getAsFloat()
            ));
        }

        // Build a new sorted JsonArray
        JsonArray sortedArticles = new JsonArray();

        // Add recommended articles based on preferences
        for (Map.Entry<String, Integer> preference : sortedPreferences) {
            String preferredCategory = preference.getKey();
            if (categorizedMap.containsKey(preferredCategory)) {
                List<JsonObject> articles = categorizedMap.get(preferredCategory);
                for (JsonObject article : articles) {
                    JsonObject recommendedArticle = article.deepCopy();
                    recommendedArticle.addProperty("recommended", true);  // Mark as recommended
                    sortedArticles.add(recommendedArticle);
                }
                categorizedMap.remove(preferredCategory); // Avoid re-adding articles later
            }
        }

        // Add remaining non-recommended articles, sorted by score
        List<JsonObject> remainingArticles = new ArrayList<>();
        for (List<JsonObject> articles : categorizedMap.values()) {
            remainingArticles.addAll(articles);  // Collect articles from categories not in preferences
        }
        remainingArticles.sort((a, b) -> Float.compare(
                b.get("score").getAsFloat(),  // Higher score first
                a.get("score").getAsFloat()
        ));
        for (JsonObject article : remainingArticles) {
            article.addProperty("recommended", false);
            sortedArticles.add(article);
        }

        return sortedArticles;
    }


    // Helper method to get user preferences sorted by count in descending order
    private List<Map.Entry<String, Integer>> getSortedUserPreferences(String username) {
        User user = userDatabase.getUser(username);
        List<Map.Entry<String, Integer>> sortedPreferences = new ArrayList<>();

        if (user != null) {
            // Get user's preferences as a map of category and count
            Map<String, Integer> preferences = user.getPreferences();
            sortedPreferences.addAll(preferences.entrySet());
            sortedPreferences.sort((a, b) -> Integer.compare(b.getValue(), a.getValue())); // Sort by count descending
        }

        return sortedPreferences;
    }

}

