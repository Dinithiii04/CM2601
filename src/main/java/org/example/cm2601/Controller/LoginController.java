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

        // Prompt for username
        System.out.print("Enter your user Name: ");
        String username = scanner.nextLine().trim();

        // Check if the username exists
        if (!userDatabase.isUserExists(username)) {
            System.out.println("User name not found. Please sign up first.");
            return null;
        }

        // Retrieve the user by username
        User user = userDatabase.getUser(username);

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
