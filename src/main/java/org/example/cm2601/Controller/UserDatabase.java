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
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            JSONArray jsonArray = new JSONArray(jsonContent.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String username = jsonObject.getString("username");
                String password = jsonObject.getString("password");

                // Assuming User has a constructor that takes username and password
                User user = new User(username, password);
                users.put(username, user);
            }
        } catch (FileNotFoundException e) {
            System.out.println("User database file not found. Starting with an empty database.");
        } catch (IOException e) {
            System.out.println("Error loading user database: " + e.getMessage());
        }
    }

    private void saveUsers() {
        JSONArray jsonArray = new JSONArray();
        for (User user : users.values()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", user.getUsername());
            jsonObject.put("password", user.getPassword());
            jsonArray.put(jsonObject);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(jsonArray.toString(4)); // Pretty print with 4 spaces indentation
        } catch (IOException e) {
            System.out.println("Error saving user database: " + e.getMessage());
        }
    }
}
