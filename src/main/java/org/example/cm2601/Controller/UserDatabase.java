package org.example.cm2601.Controller;

import org.example.cm2601.model.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserDatabase {
    private static final String FILE_PATH = "users.json";
    private Map<String, User> users = new HashMap<>(); // Map keyed by userId

    public UserDatabase() {
        loadUsers();
    }

    public boolean isUserExists(String userId) {
        return users.containsKey(userId);
    }

    public boolean addUser(User user) {
        if (isUserExists(user.getUserId())) {
            return false; // userId already exists
        }
        users.put(user.getUserId(), user); // Keyed by userId
        saveUsers();
        return true;
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public boolean verifyUser(String userId, String password) {
        User user = users.get(userId);
        return user != null && user.getPassword().equals(password);
    }

    private void loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("User database file not found. Starting with an empty database.");
            return; // No file to load, so start with an empty user map
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            // If the file is empty, initialize an empty JSON array
            if (jsonContent.length() == 0) {
                System.out.println("User database file is empty. Initializing an empty user list.");
                return;
            }

            // Parse the entire JSON array
            JSONArray jsonArray = new JSONArray(jsonContent.toString());

            // Iterate through the users array and parse each user
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String userId = jsonObject.getString("userId");
                String username = jsonObject.getString("username");
                String password = jsonObject.getString("password");

                // Create the user object
                User user = new User(userId, username, password);

                // Handle preferences
                JSONArray preferencesArray = jsonObject.optJSONArray("preferences");
                if (preferencesArray != null) {
                    for (int j = 0; j < preferencesArray.length(); j++) {
                        JSONObject preference = preferencesArray.getJSONObject(j);
                        String category = preference.getString("category");

                        user.addPreference(category);
                    }
                }

                // Add the user to the map, keyed by userId
                users.put(userId, user);
            }
        } catch (IOException | org.json.JSONException e) {
            System.out.println("Error loading user database: " + e.getMessage());
        }
    }

    private void saveUsers() {
        JSONArray jsonArray = new JSONArray();
        for (User user : users.values()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user.getUserId()); // Save userId
            jsonObject.put("username", user.getUsername());
            jsonObject.put("password", user.getPassword());

            // Add preferences as a list of JSON objects
            JSONArray preferencesArray = new JSONArray();
            for (String preference : user.getPreferences()) {
                JSONObject preferenceObject = new JSONObject();
                preferenceObject.put("category", preference);
                preferencesArray.put(preferenceObject);
            }
            jsonObject.put("preferences", preferencesArray);

            // Add the user to the JSON array
            jsonArray.put(jsonObject);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(jsonArray.toString(4)); // Pretty print with 4 spaces indentation
        } catch (IOException e) {
            System.out.println("Error saving user database: " + e.getMessage());
        }
    }
}
