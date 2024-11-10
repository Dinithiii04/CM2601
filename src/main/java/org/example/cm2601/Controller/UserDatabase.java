package org.example.cm2601.Controller;

import org.example.cm2601.model.User;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserDatabase {
    private static final String FILE_PATH = "users.txt";
    private Map<String, User> users = new HashMap<>();

    public UserDatabase() {
        loadUsers();
    }

    // Add a method to check if a user exists
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
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            users = (Map<String, User>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("User database file not found. Starting with an empty database.");
            //handle no database ( only signup option)
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading user database: " + e.getMessage());
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.out.println("Error saving user database: " + e.getMessage());
        }
    }
}
