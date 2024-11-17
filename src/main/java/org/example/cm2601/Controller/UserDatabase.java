package org.example.cm2601.Controller;

import org.example.cm2601.model.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserDatabase {
    private static final String FILE_PATH = "users.json";
    private Map<String, User> users = new HashMap<>();

    public UserDatabase() {
        loadUsers();
    }

    public boolean isUserExists(String username) {
        return users.containsKey(username);
    }

    public boolean addUser(User user) {
        if (isUserExists(user.getUsername())) {
            return false; // Username already exists
        }
        users.put(user.getUsername(), user);
        saveUsers();
        return true;
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public boolean verifyUser(String username, String password) {
        User user = users.get(username);
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
                String username = jsonObject.getString("username");
                String password = jsonObject.getString("password");

                // Create the user object
                User user = new User(username, password);

                // Handle preferences
                JSONArray preferencesArray = jsonObject.optJSONArray("preferences");
                if (preferencesArray != null) {
                    for (int j = 0; j < preferencesArray.length(); j++) {
                        JSONObject preference = preferencesArray.getJSONObject(j);
                        String category = preference.getString("category");

                        user.addPreference(category);
                    }
                }

                // Add the user to the map
                users.put(username, user);
            }
        } catch (IOException | org.json.JSONException e) {
            System.out.println("Error loading user database: " + e.getMessage());
        }
    }



    private void saveUsers() {
        JSONArray jsonArray = new JSONArray();
        for (User user : users.values()) {
            JSONObject jsonObject = new JSONObject();
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
