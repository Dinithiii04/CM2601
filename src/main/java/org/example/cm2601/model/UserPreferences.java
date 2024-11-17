
package org.example.cm2601.model;

import com.google.gson.*;
import java.io.*;
import java.util.*;

public class UserPreferences {
    private static final String USERS_FILE = "users.json";  // Ensure only this file is used

    // Method to update user preferences in memory
    public static void savePreferences(String username, String category) {
        // Load all users from the file
        System.out.println("log : loading user file");
        List<JsonObject> allUsers = loadUsers();


        // Find the user object
        JsonObject userJson = findUser(allUsers, username);

        if (userJson == null) {
            System.out.println("User not found: " + username);
            return; // Exit if the user is not found
        }

        // Get or initialize the preferences array
        JsonArray preferencesArray = userJson.getAsJsonArray("preferences");
        if (preferencesArray == null) {
            System.out.println("logging: intialise preference array");
            preferencesArray = new JsonArray();
            userJson.add("preferences", preferencesArray); // Initialize preferences array if not present
        }

        // Update the preferences
        updateCategory(preferencesArray, category);

        // This step ensures the changes are reflected in the allUsers list
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).get("username").getAsString().equals(username)) {
                allUsers.set(i, userJson);
                break;
            }
        }

        // Save back to the file
        saveUsers(allUsers);
        System.out.println("Preferences updated and saved.");
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

    // Helper method to update or add a category in the preferences array
    private static void updateCategory(JsonArray preferences, String category) {
        for (JsonElement element : preferences) {
            JsonObject preference = element.getAsJsonObject();
            if (preference.get("category").getAsString().equals(category)) {
                // Increment the count if the category exists
                System.out.println(" logging user found: incrementing value ");   //*
                int currentCount = preference.get("count").getAsInt();
                preference.addProperty("count", currentCount + 1);
                return;
            }
        }
        System.out.println("logging : adding new category");  //*
        // Add a new category if it doesn't exist
        JsonObject newPreference = new JsonObject();
        newPreference.addProperty("category", category);
        newPreference.addProperty("count", 1); // Initialize with a count of 1
        preferences.add(newPreference);
    }

    // Load users from the JSON file
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
        return users;
    }

    // Save all users back to the JSON file
    private static void saveUsers(List<JsonObject> users) {
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            // Convert the list of users to a JsonArray
            JsonArray usersArray = new JsonArray();
            for (JsonObject user : users) {
                usersArray.add(user);
            }

            // Write the JsonArray to the file
            writer.write(new Gson().toJson(usersArray));
            System.out.println("Users successfully saved to " + USERS_FILE);
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }
}
