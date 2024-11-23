package org.example.cm2601.Controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchNews {

    private static final String NEWS_API_URL = "https://newsapi.org/v2/everything?q='a'&language=en&pageSize=50&apiKey=e0d920ae183d4477b6ee87f72a57438c";

    public JsonArray fetchNews() {
        try {
            HttpURLConnection newsConn = (HttpURLConnection) new URL(NEWS_API_URL).openConnection();
            newsConn.setRequestMethod("GET");

            BufferedReader newsReader = new BufferedReader(new InputStreamReader(newsConn.getInputStream()));
            StringBuilder newsResponse = new StringBuilder();
            String line;
            while ((line = newsReader.readLine()) != null) {
                newsResponse.append(line);
            }
            newsReader.close();

            // Parse the response string into a JSON object.
            JsonObject newsJson = JsonParser.parseString(newsResponse.toString()).getAsJsonObject();
            return newsJson.getAsJsonArray("articles");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
