package org.example.cm2601.Controller;

import org.example.cm2601.model.CurrentUser;
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

        System.out.print("Enter your username: ");
        String username = scanner.nextLine().trim();

        if (userDatabase.isUserExists(username)) {
            System.out.println("Username already exists. Please try logging in.");
            return null;
        }

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        User newUser = new User(username, password);
        userDatabase.addUser(newUser);
        System.out.println("Signup successful! Welcome, " + username);

        CurrentUser.setCurrentUser(username); // Set CurrentUser here
        return newUser;
    }


}



