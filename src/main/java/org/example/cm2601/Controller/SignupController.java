package org.example.cm2601.Controller;

import org.example.cm2601.model.User;

import java.util.Scanner;

public class SignupController {
    private UserDatabase userDatabase;

    public SignupController(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    public User signup() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== Signup ===");

        System.out.print("Enter a username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter a password: ");
        String password = scanner.nextLine();

        // Check if user already exists
        if (userDatabase.getUser(username) != null) {
            System.out.println("Username already taken. Please try a different one.");
            return null;
        }

        // Create and save the new user
        User newUser = new User(username, password);
        if (userDatabase.addUser(newUser)) {
            System.out.println("Signup successful! Welcome, " + username);
            return newUser;
        } else {
            System.out.println("Error creating account. Please try again.");
            return null;
        }
    }
}
