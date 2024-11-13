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
            String content = article.has("content") && !article.get("content").isJsonNull() ? article.get("content").getAsString() : "";
            String url = article.get("url").getAsString();

            if (!content.isEmpty()) {
                String category = classifyTextWithHuggingFace(content);

                JsonObject categorizedArticle = new JsonObject();
                categorizedArticle.addProperty("title", title);
                categorizedArticle.addProperty("content", content);
                categorizedArticle.addProperty("url", url);
                categorizedArticle.addProperty("category", category);

                categorizedArticles.add(categorizedArticle);
            }
        }

        saveToFile(categorizedArticles);
    }

    private void saveToFile(JsonArray categorizedArticles) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH))) {
            writer.write(categorizedArticles.toString());
            System.out.println("Categorized articles saved to " + OUTPUT_FILE_PATH);
        } catch (Exception e) {
            System.out.println("Error saving categorized news: " + e.getMessage());
        }
    }

    private String classifyTextWithHuggingFace(String text) {
        try {
            URL url = new URL(HUGGING_FACE_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + HUGGING_FACE_API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{\"inputs\": \"" + text.replace("\"", "\\\"") + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine.trim());
                }
                return "Unknown";
            }

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

        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }
}
