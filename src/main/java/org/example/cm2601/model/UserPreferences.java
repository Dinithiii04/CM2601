package org.example.cm2601.model;

import com.google.gson.*;
import java.io.*;
import java.util.*;

public class UserPreferences {
    private static final String USERS_FILE = "users.json";  // Ensure only this file is used

    // Method to save or update user preferences in the JSON file
    public static void savePreferences(String username, String category) {
        List<JsonObject> allUsers = loadUsers();  // Load existing users from the file

        // Find user by username
        JsonObject userJson = findUser(allUsers, username);


        // Add or update the category in the preferences for the correct user
        JsonArray preferencesArray = userJson.getAsJsonArray("preferences");
        updateCategory(preferencesArray, category);

        // Save all users back to the JSON file
        saveUsers(allUsers);
    }

    // Helper method to find a user by username
    private static JsonObject findUser(List<JsonObject> users, String username) {
        for (JsonObject user : users) {
            if (user.get("username").getAsString().equals(username)) {
                return user;  // Return the user if found
            }
        }
        return null;  // Return null if the user is not found
    }

    // Helper method to check if a category already exists in the preferences
    // Helper method to update or add a category with its count
    private static void updateCategory(JsonArray preferences, String category) {
        for (JsonElement element : preferences) {
            JsonObject preference = element.getAsJsonObject();
            if (preference.get("category").getAsString().equals(category)) {
                // Increment the count if the category exists
                int currentCount = preference.get("count").getAsInt();
                preference.addProperty("count", currentCount + 1);
                return;
            }
        }
        // Add a new category if it doesn't exist
        JsonObject newPreference = new JsonObject();
        newPreference.addProperty("category", category);
        newPreference.addProperty("count", 1); // Initialize with a count of 1
        preferences.add(newPreference);
    }


    // Load users from the JSON file (users.json)
    private static List<JsonObject> loadUsers() {
        List<JsonObject> users = new ArrayList<>();
        try (FileReader reader = new FileReader(USERS_FILE)) {
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            for (JsonElement element : jsonArray) {
                users.add(element.getAsJsonObject());
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
        return users;  // Return the list of users
    }

    // Save all users back to the JSON file (users.json)
    private static void saveUsers(List<JsonObject> users) {
        JsonArray usersArray = new JsonArray();
        for (JsonObject user : users) {
            usersArray.add(user);  // Add each user to the users array
        }
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            writer.write(usersArray.toString());  // Write the users array to the file
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }
}