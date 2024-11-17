package org.example.cm2601.model;

import com.google.gson.*;
import java.io.*;
import java.util.*;

public class UserPreferences {
    private static final String USERS_FILE = "users.json"; // Path to the JSON file storing user data

    // Method to save or update user preferences in the JSON file
    public static void savePreferences(String username, String category) {
        List<JsonObject> allUsers = loadUsers();  // Load existing users
        JsonObject userJson = findUser(allUsers, username); // Find the user

        if (userJson == null) {
            // If the user does not exist, create a new user with empty preferences
            userJson = new JsonObject();
            userJson.addProperty("username", username);
            userJson.addProperty("password", ""); // Placeholder for password
            userJson.add("preferences", new JsonArray());
            allUsers.add(userJson);
        }

        // Update preferences for the user
        JsonArray preferencesArray = userJson.getAsJsonArray("preferences");
        updateCategoryCount(preferencesArray, category);

        // Save all users back to the JSON file
        saveUsers(allUsers);
    }


    // Helper method to update or add a category with a count
    private static void updateCategoryCount(JsonArray preferencesArray, String category) {
        boolean categoryExists = false;

        for (JsonElement element : preferencesArray) {
            JsonObject preference = element.getAsJsonObject();
            if (preference.has(category)) {
                int currentCount = preference.get(category).getAsInt();
                preference.addProperty(category, currentCount + 1); // Increment count
                categoryExists = true;
                break;
            }
        }

        if (!categoryExists) {
            JsonObject newPreference = new JsonObject();
            newPreference.addProperty(category, 1); // Add new category with count 1
            preferencesArray.add(newPreference);
        }
    }

    // Helper method to find a user by username
    private static JsonObject findUser(List<JsonObject> users, String username) {
        for (JsonObject user : users) {
            if (user.get("username").getAsString().equals(username)) {
                return user; // Return the user if found
            }
        }
        return null; // Return null if the user is not found
    }

    // Helper method to create a new user
    private static JsonObject createUser(String username) {
        JsonObject newUser = new JsonObject();
        newUser.addProperty("username", username);
        newUser.addProperty("password", ""); // Placeholder for password
        newUser.add("preferences", new JsonArray());
        return newUser;
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
        return users; // Return the list of users
    }

    // Save all users back to the JSON file (users.json)
    private static void saveUsers(List<JsonObject> users) {
        JsonArray usersArray = new JsonArray();
        for (JsonObject user : users) {
            usersArray.add(user); // Add each user to the users array
        }
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            writer.write(usersArray.toString()); // Write the users array to the file
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }
}
