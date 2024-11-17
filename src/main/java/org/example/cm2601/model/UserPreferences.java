package org.example.cm2601.model;

import com.google.gson.*;
import java.io.*;
import java.util.*;

public class UserPreferences {
    private static final String USERS_FILE = "users.json";  // Ensure only this file is used

    // Method to save or update user preferences in the JSON file
    public static void savePreferences(String username, String category, float score) {
        String filePath = "users.json"; // Ensure this is the correct path for your users file
        JsonArray usersArray = new JsonArray();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            JsonElement root = JsonParser.parseReader(reader);

            // Ensure we're dealing with a JSON array
            if (root.isJsonArray()) {
                usersArray = root.getAsJsonArray();
            } else {
                System.out.println("Error: Expected a JSON array in the file.");
                return;
            }
        } catch (IOException e) {
            System.out.println("No existing user preferences found or error reading file.");
        }

        // Flag to check if the user was found
        boolean userFound = false;

        // Loop through the users array to find the matching user
        for (JsonElement userElement : usersArray) {
            JsonObject userObject = userElement.getAsJsonObject();
            String existingUsername = userObject.get("username").getAsString();

            // Check if this is the user we are looking for
            if (existingUsername.equals(username)) {
                JsonArray preferences = userObject.getAsJsonArray("preferences");
                preferences.add(category);  // Add new category to preferences
                userObject.add("preferences", preferences);  // Update preferences field
                userObject.addProperty("score", score);  // Add the score for the user

                userFound = true;  // Set the flag to true as we found the user
                break;  // Exit loop once user is found
            }
        }

        // If the user wasn't found, create a new user entry with the given data
        if (!userFound) {
            JsonObject newUser = new JsonObject();
            newUser.addProperty("username", username);
            JsonArray newPreferences = new JsonArray();
            newPreferences.add(category);
            newUser.add("preferences", newPreferences);
            newUser.addProperty("score", score);  // Add the score for the new user

            usersArray.add(newUser);  // Add new user to the array
        }

        // Save the updated JSON array back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(usersArray.toString());
            System.out.println("User preferences updated successfully.");
        } catch (IOException e) {
            System.out.println("Error saving user preferences: " + e.getMessage());
        }
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
    private static boolean containsCategory(JsonArray preferences, String category) {
        for (JsonElement element : preferences) {
            if (element.getAsString().equals(category)) {
                return true;  // Return true if the category is already present
            }
        }
        return false;  // Return false if the category is not present
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
