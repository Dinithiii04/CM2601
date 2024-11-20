package org.example.cm2601.Controller;

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

        System.out.print("Enter your username: ");
        String username = scanner.nextLine().trim();

        if (!userDatabase.isUserExists(username)) {
            System.out.println("Username not found. Please sign up first.");
            return null;
        }

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        if (userDatabase.verifyUser(username, password)) {
            System.out.println("Login successful! Welcome, " + username);
            return userDatabase.getUser(username);
        } else {
            System.out.println("Invalid password. Please try again.");
            return null;
        }
    }

}



