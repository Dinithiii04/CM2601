package org.example.cm2601.Controller;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.*;

public class NewsRecommender {

    // Method to reorder news articles based on user preferences
    public JsonArray recommendNews(JsonArray categorizedArticles, String username) {
        // Fetch user preferences
        List<Map.Entry<String, Integer>> sortedPreferences = getSortedUserPreferences(username);

        // Separate articles by category and sort by score within each category
        Map<String, List<JsonObject>> categorizedMap = new HashMap<>();
        for (int i = 0; i < categorizedArticles.size(); i++) {
            JsonObject article = categorizedArticles.get(i).getAsJsonObject();
            String category = article.get("category").getAsString();

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
                    recommendedArticle.addProperty("recommended", true);
                    sortedArticles.add(recommendedArticle);
                }
                categorizedMap.remove(preferredCategory); // Avoid re-adding articles later
            }
        }

        // Add remaining non-recommended articles, sorted by score
        List<JsonObject> remainingArticles = new ArrayList<>();
        for (List<JsonObject> articles : categorizedMap.values()) {
            remainingArticles.addAll(articles);
        }
        remainingArticles.sort((a, b) -> Float.compare(
                b.get("score").getAsFloat(),
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
        List<Map.Entry<String, Integer>> sortedPreferences = new ArrayList<>();
        JsonObject user = UserPreferences.getUserPreferences(username);
        if (user != null && user.has("preferences")) {
            JsonArray preferencesArray = user.getAsJsonArray("preferences");
            Map<String, Integer> preferenceMap = new HashMap<>();

            for (int i = 0; i < preferencesArray.size(); i++) {
                JsonObject preference = preferencesArray.get(i).getAsJsonObject();
                String category = preference.get("category").getAsString();
                int count = preference.get("count").getAsInt();
                preferenceMap.put(category, count);
            }

            sortedPreferences.addAll(preferenceMap.entrySet());
            sortedPreferences.sort((a, b) -> Integer.compare(b.getValue(), a.getValue())); // Sort by count descending
        }
        return sortedPreferences;
    }
}

