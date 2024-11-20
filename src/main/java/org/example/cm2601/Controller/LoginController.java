package org.example.cm2601.Controller;

import org.example.cm2601.model.CurrentUser;
import org.example.cm2601.model.User;

import java.util.Scanner;

public class LoginController {
    private UserDatabase userDatabase;

    public LoginController(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    public User login() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== Login ===");

        // Prompt for userId
        System.out.print("Enter your user ID: ");
        String userId = scanner.nextLine().trim();

        // Check if the userId exists
        if (!userDatabase.isUserExists(userId)) {
            System.out.println("User ID not found. Please sign up first.");
            return null;
        }

        // Retrieve the user by userId
        User user = userDatabase.getUser(userId);

        // Prompt for username and verify it matches
        System.out.print("Enter your username: ");
        String username = scanner.nextLine().trim();

        if (!user.getUsername().equals(username)) {
            System.out.println("Username does not match the provided User ID. Please try again.");
            return null;
        }

        // Prompt for password and verify it
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        if (user.getPassword().equals(password)) {
            System.out.println("Login successful! Welcome, " + username);

            // Set CurrentUser with userId, username, and password
            CurrentUser.setCurrentUser(user.getUserId(), user.getUsername(), user.getPassword());
            return user;
        } else {
            System.out.println("Invalid password. Please try again.");
            return null;
        }
    }
}
