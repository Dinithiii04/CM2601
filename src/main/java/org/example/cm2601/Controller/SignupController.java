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
        System.out.println("\n--- Signup ---");

        String username;
        while (true) {
            System.out.print("Enter your username: ");
            username = scanner.nextLine().trim();

            if (username.length() < 3) {
                System.out.println("Error: Username must have at least 3 characters.");
            } else if (!username.matches("[a-zA-Z]+")) {
                System.out.println("Error: Username must contain only letters.");
            } else if (userDatabase.isUserExists(username)) {
                System.out.println("Error: Username already exists. Please try another.");
            } else {
                break;
            }
        }

        String password;
        while (true) {
            System.out.print("Enter your password: ");
            password = scanner.nextLine();

            if (password.length() < 5) {
                System.out.println("Error: Password must have at least 5 characters.");
            } else {
                break;
            }
        }

        User newUser = new User(username, password);
        userDatabase.addUser(newUser);
        System.out.println("Signup successful! Welcome, " + username);

        return newUser;
    }


}



