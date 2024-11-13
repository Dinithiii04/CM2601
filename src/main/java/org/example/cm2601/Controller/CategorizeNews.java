package org.example.cm2601.Controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CategorizeNews {

    private static final String HUGGING_FACE_API_URL = "https://api-inference.huggingface.co/models/Yueh-Huan/news-category-classification-distilbert";
    private static final String HUGGING_FACE_API_KEY = "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";
    private static final String OUTPUT_FILE_PATH = "categorized_news.json";

    public void categorizeAndSaveNews(JsonArray articles) {
        JsonArray categorizedArticles = new JsonArray();

        for (int i = 0; i < articles.size(); i++) {
            JsonObject article = articles.get(i).getAsJsonObject();
            String title = article.get("title").getAsString();
            String description = article.has("description") && !article.get("description").isJsonNull() ? article.get("description").getAsString() : "";
            String url = article.get("url").getAsString();

            if (!description.isEmpty()) {
                // Classify article based on its description
                String category = classifyTextWithHuggingFace(description);

                // Prepare the categorized article as per required JSON structure
                JsonObject categorizedArticle = new JsonObject();
                categorizedArticle.addProperty("title", title);
                categorizedArticle.addProperty("description", description); // use "description" instead of "content"
                categorizedArticle.addProperty("url", url);
                categorizedArticle.addProperty("category", category);

                categorizedArticles.add(categorizedArticle);
            }
        }

        // Save the categorized articles to the JSON file
        if (categorizedArticles.size() > 0) {
            saveToFile(categorizedArticles);
        } else {
            System.out.println("No news articles to save.");
        }
    }

    private void saveToFile(JsonArray categorizedArticles) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH))) {
            // Write the categorized articles to the JSON file
            writer.write(categorizedArticles.toString());
        } catch (Exception e) {
            System.out.println("Error saving categorized news: " + e.getMessage());
        }
    }

    private String classifyTextWithHuggingFace(String text) {
        int maxRetries = 5;
        int retryDelayMs = 50000; // 20 seconds

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                URL url = new URL(HUGGING_FACE_API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + HUGGING_FACE_API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Prepare the input string for Hugging Face API
                String jsonInputString = "{\"inputs\": \"" + text.replace("\"", "\\\"") + "\"}";

                // Send POST request with text to Hugging Face API
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    // Read and parse the response
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    JsonElement jsonResponse = JsonParser.parseString(response.toString());
                    if (jsonResponse.isJsonArray()) {
                        JsonArray outerArray = jsonResponse.getAsJsonArray();
                        if (outerArray.size() > 0 && outerArray.get(0).isJsonArray()) {
                            JsonArray innerArray = outerArray.get(0).getAsJsonArray();
                            String bestLabel = "Unknown";
                            float bestScore = -1.0f;

                            for (JsonElement element : innerArray) {
                                if (element.isJsonObject()) {
                                    JsonObject result = element.getAsJsonObject();
                                    String label = result.get("label").getAsString();
                                    float score = result.get("score").getAsFloat();

                                    if (score > bestScore) {
                                        bestScore = score;
                                        bestLabel = label;
                                    }
                                }
                            }
                            return bestLabel;
                        }
                    }
                    return "Unknown";
                } else {
                    // Handle loading error response
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorResponse.append(errorLine.trim());
                    }

                    if (errorResponse.toString().contains("currently loading")) {
                        System.out.println("Model loading, retrying in " + (retryDelayMs / 1000) + " seconds...");
                        Thread.sleep(retryDelayMs);
                        continue; // retry after delay
                    }
                    return "Unknown";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Unknown";
            }
        }

        System.out.println("Max retries reached. Model might be unavailable.");
        return "Unknown";
    }

}