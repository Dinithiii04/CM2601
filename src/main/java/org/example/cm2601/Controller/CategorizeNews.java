package org.example.cm2601.Controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.example.cm2601.model.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CategorizeNews {
    private UserDatabase userDatabase;
    private MongoCollection<Document> articlesCollection;

    public CategorizeNews(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
        this.articlesCollection = userDatabase.getDatabase().getCollection("articles");
    }



    private static final String HUGGING_FACE_API_URL = "https://api-inference.huggingface.co/models/Yueh-Huan/news-category-classification-distilbert";
    private static final String HUGGING_FACE_API_KEY = "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";
    private static final String OUTPUT_FILE_PATH = "categorized_news.json";

    // Method to categorize and save news articles
    public void categorizeAndSaveNews(JsonArray articles, User user) {
        JsonArray categorizedArticles = new JsonArray();

        for (int i = 0; i < articles.size(); i++) {
            JsonObject article = articles.get(i).getAsJsonObject();
            String title = article.get("title").getAsString();
            String description = article.has("description") && !article.get("description").isJsonNull() ?
                    article.get("description").getAsString() : "";
            String url = article.get("url").getAsString();

            // Classify the description using Hugging Face API
            if (!description.isEmpty()) {
                JsonObject classificationResult =  classifyTextWithHuggingFace(description);
                String category = classificationResult.get("category").getAsString();
                float score = classificationResult.get("score").getAsFloat();

                // Only proceed if category is valid
                if (!"Unknown".equals(category)) {
                    JsonObject categorizedArticle = new JsonObject();
                    categorizedArticle.addProperty("title", title);
                    categorizedArticle.addProperty("description", description);
                    categorizedArticle.addProperty("url", url);
                    categorizedArticle.addProperty("category", category);
                    categorizedArticle.addProperty("score", score);

                    categorizedArticles.add(categorizedArticle);
                }
            }
        }

        if (!categorizedArticles.isEmpty()) {
            saveArticlesToDatabase(categorizedArticles, user.getUsername());
            showNewsTitlesAndSelect(categorizedArticles, user);
        } else {
            System.out.println("No valid news articles to save.");
        }

    }
    public void deleteUserArticles(String username) {
        Bson filter = Filters.eq("username", username);
        articlesCollection.deleteMany(filter); // Delete all articles associated with the user
        System.out.println("All articles for user " + username + " have been removed.");
    }



    private void saveArticlesToDatabase(JsonArray categorizedArticles, String username) {
        List<Document> articleDocs = new ArrayList<>();

        for (JsonElement element : categorizedArticles) {
            JsonObject articleJson = element.getAsJsonObject();
            Document articleDoc = Document.parse(articleJson.toString());   // Convert JSON to MongoDB Document
            articleDoc.append("username", username); // Associate with the current user
            articleDocs.add(articleDoc);
        }

        if (!articleDocs.isEmpty()) {
            articlesCollection.insertMany(articleDocs);
        }
    }

    public JsonArray loadArticlesFromDatabase(String username) {
        JsonArray articlesArray = new JsonArray();
        FindIterable<Document> documents = articlesCollection.find(Filters.eq("username", username)); // Fetch only this user's articles

        for (Document doc : documents) {
            JsonObject jsonObject = JsonParser.parseString(doc.toJson()).getAsJsonObject();
            articlesArray.add(jsonObject);
        }

        return articlesArray;
    }


    // Method to show categorized news titles and allow user selection
    private void showNewsTitlesAndSelect(JsonArray categorizedArticles, User user) {
        if(user == null){
            System.out.println("no user found");
            return;
        }


        // Use NewsRecommender to reorder articles based on user's preferences
        NewsRecommender recommender = new NewsRecommender(userDatabase);
        JsonArray recommendedArticles = recommender.recommendNews(categorizedArticles, user.getUsername());

        Scanner scanner = new Scanner(System.in);
        boolean continueReading = true;

        while (continueReading) {
            System.out.println("\n----------------------------------------------------");
            System.out.println("        Fetched and Categorized News Titles:");
            System.out.println("----------------------------------------------------\n");

            // Display articles
            for (int i = 0; i < recommendedArticles.size(); i++) {
                JsonObject article = recommendedArticles.get(i).getAsJsonObject();
                String title = article.get("title").getAsString();
                String category = article.get("category").getAsString();
                boolean recommended = article.get("recommended").getAsBoolean();

                if(title.equals("[Removed]")){
                    continue;   //skip removed articles
                }
                    System.out.println((i + 1) + ". " + title + " - " + category + (recommended ? " [Recommended]" : ""));


            }

            System.out.println(" ");
            int choice = -1;

            // Validate integer input for news selection
            while (true) {
                System.out.print("\n * Enter the number of the news title you want to view: ");
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                    break; // Valid integer input, exit loop
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            }

            if (choice == 0) {
                System.out.println("\nExiting news reading. Returning to main menu...");
                continueReading = false;
            } else if (choice >= 1 && choice <= recommendedArticles.size()) {
                JsonObject selectedArticle = recommendedArticles.get(choice - 1).getAsJsonObject();
                String title = selectedArticle.get("title").getAsString();
                String description = selectedArticle.get("description").getAsString();
                String url = selectedArticle.get("url").getAsString();
                String category = selectedArticle.get("category").getAsString();

                System.out.println("\nYou selected: " + title);
                System.out.println("Description: " + description);
                System.out.println("URL: " + url);
                System.out.println("Category: " + category);

                // Update user preferences with the selected category
                userDatabase.updatePreferences(user.getUsername(), category);
                System.out.println("Your preference has been updated with the category: " + category);

                //add article titles to users history
                userDatabase.addToReadHisotry(user.getUsername(), title);
                user.addToReadingHistory(title);


                // Ask if the user wants to read more
                while (true) {
                    System.out.print("\nDo you want to read more news? (y/n): ");
                    String response = scanner.nextLine().trim().toLowerCase();

                    if (response.equals("y")) {
                        break; // Continue reading loop
                    } else if (response.equals("n")) {
                        System.out.println("Exiting news reading. Returning to main menu...");
                        continueReading = false;
                        break;
                    } else {
                        System.out.println("Invalid input. Please enter 'y' or 'n'.");
                    }
                }
            }
            else {
                System.out.println("Invalid choice. Please select a valid news title or enter 0 to exit.");
            }
        }
    }


    //  Classify text using the Hugging Face API
    private JsonObject classifyTextWithHuggingFace(String text) {
        int maxRetries = 5;  // Max retry attempts for API call
        int retryDelayMs = 5000;  // Delay between retries

        for (int attempt = 0; attempt < maxRetries; attempt++) {
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
                if (responseCode == 200) {
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

                            //Find the best category
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
                            JsonObject result = new JsonObject();
                            result.addProperty("category", bestLabel);
                            result.addProperty("score", bestScore);
                            return result;


                        }
                    }
                    JsonObject errorResponse = new JsonObject();
                    errorResponse.addProperty("category", "Unknown");
                    errorResponse.addProperty("score",0.0f );
                    return errorResponse;

                } else {
                    //handle API errors
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorResponse.append(errorLine.trim());
                    }

                    // Retry if the model is loading
                    if (errorResponse.toString().contains("currently loading")) {
                        System.out.println("Model loading, retrying in " + (retryDelayMs / 1000) + " seconds...");
                        Thread.sleep(retryDelayMs);
                        continue;
                    }
                    JsonObject errorResult = new JsonObject();
                    errorResult.addProperty("category", "Unknown");
                    errorResult.addProperty("score",0.0f );
                    return errorResult;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Return "Unknown" if all retries fail
        JsonObject unknownResult = new JsonObject();
        unknownResult.addProperty("category", "Unknown");
        unknownResult.addProperty("score",0.0f );
        return unknownResult;
    }
}
