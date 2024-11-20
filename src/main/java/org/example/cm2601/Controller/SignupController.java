package org.example.cm2601.Controller;

import org.example.cm2601.model.CurrentUser;
import org.example.cm2601.model.User;

import java.util.Scanner;
import java.util.UUID;

public class SignupController {
    private UserDatabase userDatabase;

    public SignupController(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    public User signup() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== Signup ===");

        System.out.print("Enter your username: ");
        String username = scanner.nextLine().trim();

        if (userDatabase.isUserExists(username)) {
            System.out.println("Username already exists. Please try logging in.");
            return null;
        }

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        // Generate a unique userId using UUID (Universally Unique Identifier)
        String userId = UUID.randomUUID().toString();

        // Create the new user with userId, username, and password
        User newUser = new User(userId, username, password);

        // Add the user to the database
        userDatabase.addUser(newUser);
        System.out.println("Signup successful! Welcome, " + username);

        // Set CurrentUser with userId, username, and password
        CurrentUser.setCurrentUser(newUser.getUserId(), newUser.getUsername(), newUser.getPassword());
        return newUser;
    }
}
