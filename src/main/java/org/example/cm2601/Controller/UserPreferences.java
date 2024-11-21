
package org.example.cm2601.Controller;

import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserPreferences {
    private static final String USERS_FILE = "users.json";  // Ensure only this file is used

    // Method to update user preferences in memory
    public static void updatePreferences(String username, String category) {
        // Load all users from the file
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
        boolean categoryExists = false;
        // find category
        for ( JsonElement element: preferencesArray ){
            JsonObject preference =  element.getAsJsonObject();
            if (preference.get("category").getAsString().equals(category)){
                //increment the count
                int currentCount = preference.has("count")?preference.get("count").getAsInt(): 0;  //compact if loop
                preference.addProperty("count", currentCount + 1);
                categoryExists = true;
                break;
            }
        }

        //if category doesn't exists
        if(!categoryExists){
            JsonObject newPreference = new JsonObject();
            newPreference.addProperty("category", category);
            newPreference.addProperty("count", 1);
            preferencesArray.add(newPreference);

        }



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

    public static JsonObject getUserPreferences(String username) {
        // Load all users
        List<JsonObject> allUsers = loadUsers();

        // Find the user object
        for (JsonObject user : allUsers) {
            if (user.get("username").getAsString().equals(username)) {
                return user;  // Return the user if found
            }
        }

        // If the user is not found, return null
        return null;
    }



    // Load users from the JSON file
    private static List<JsonObject> loadUsers() {
        List<JsonObject> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if(!file.exists()){
            System.out.println("user not found!");
            return users;
        }
        try (FileReader reader = new FileReader(USERS_FILE)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            if(jsonElement == null || jsonElement.isJsonNull()){
                System.out.println("user file empty or invalid");
                return users;
            }
            if(!jsonElement.isJsonArray()){
                System.out.println("invalid json");
                return users;
            }

            JsonArray jsonArray = jsonElement.getAsJsonArray();
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
