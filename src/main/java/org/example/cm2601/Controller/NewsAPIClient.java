package org.example.cm2601.Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.example.cm2601.model.NewsArticle;
import org.json.JSONArray;
import org.json.JSONObject;

public class NewsAPIClient {
    private static final String API_KEY = "6ba0fd8128e843dc9056000e06496c8e";
    private static final String BASE_URL = "https://newsapi.org/v2/top-headlines?country=us&apiKey=" + API_KEY;

    public List<NewsArticle> fetchNews() {
        List<NewsArticle> articles = new ArrayList<>();
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray articlesArray = jsonResponse.getJSONArray("articles");

            for (int i = 0; i < articlesArray.length(); i++) {
                JSONObject articleJSON = articlesArray.getJSONObject(i);
                String title = articleJSON.optString("title", "No Title");
                String description = articleJSON.optString("description", "No Description");
                String urlToArticle = articleJSON.optString("url", "No URL");

                articles.add(new NewsArticle(title, description, urlToArticle));
            }
        } catch (Exception e) {
            System.out.println("Error fetching or parsing news: " + e.getMessage());
        }
        return articles;
    }
}
