package org.example.cm2601;

import org.example.cm2601.Controller.*;
import org.example.cm2601.model.User;

import java.util.Scanner;

public class NewsApp {
    public static void main(String[] args) {
        // Initialize the user database, which will hold user accounts and preferences
        UserDatabase userDatabase = new UserDatabase();

        // Controllers for handling signup and login processes
        SignupController signupController = new SignupController(userDatabase);
        LoginController loginController = new LoginController(userDatabase);

        // Initialize the NewsAPIClient, which fetches news articles from an API
        NewsAPIClient newsAPIClient = new NewsAPIClient(); // New: Client for fetching news from the API

        // HomeController manages user-specific functions such as showing news and adding preferences
        HomeController homeController = new HomeController(userDatabase, loginController, signupController);

        // Set up a scanner for taking user input from the console
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        // Main application loop
        while (isRunning) {
            // Display main menu options
            System.out.println("\n=== Personalized News Recommendation System ===");
            System.out.println("1. Login"); // Option 1 for existing users to log in
            System.out.println("2. Signup"); // Option 2 for new users to create an account
            System.out.println("3. Exit"); // Option 3 to exit the application
            System.out.print("Select an option: ");

            // Get user selection from input
            int option = scanner.nextInt();

            // Process user selection
            switch (option) {
                case 1:
                    // Attempt to log in the user
                    User user = loginController.login();

                    // If login is successful, show the personalized home page
                    if (user != null) {
                        homeController.showHome(user); // Pass the logged-in user to HomeController
                    }
                    break;

                case 2:
                    // Attempt to sign up a new user
                    user = signupController.signup();

                    // If signup is successful, show the personalized home page
                    if (user != null) {
                        homeController.showHome(user); // Pass the newly signed-up user to HomeController
                    }
                    break;

                case 3:
                    // Exit the application
                    System.out.println("Exiting the application. Goodbye!");
                    isRunning = false; // Set running  false to end loop
                    break;

                default:
                    // Handle invalid menu options
                    System.out.println("Invalid option. Please select 1, 2, or 3.");
            }
        }
    }
}

