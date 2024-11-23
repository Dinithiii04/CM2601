package org.example.cm2601;

import org.example.cm2601.Controller.*;
import org.example.cm2601.model.User;

import java.util.Scanner;

public class NewsApp {
    public static void main(String[] args) {

        // Disable MongoDB driver logging to avoid cluttering console logs
        System.setProperty("org.slf4j.simpleLogger.log.org.mongodb.driver", "OFF");

        //Initialize objects
        UserDatabase userDatabase = new UserDatabase();

        SignupController signupController = new SignupController(userDatabase);
        LoginController loginController = new LoginController(userDatabase);
        FetchNews fetchNews = new FetchNews();
        CategorizeNews categorizeNews = new CategorizeNews(userDatabase);

        HomeController homeController = new HomeController(userDatabase, loginController, signupController, fetchNews);

        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;
        User currentUser = null;  // keeping track with the logging user

        while (isRunning) {
            // Display menu
            System.out.println("\n-------------- Personalized News Recommendation System --------------");
            System.out.println("1. Login");
            System.out.println("2. Signup");
            System.out.println("3. Exit \n");

            int option = -1; // Initialize with an invalid value

            while (true) { // Loop until valid input is received
                System.out.print("* Select an option: ");
                try {
                    option = Integer.parseInt(scanner.nextLine()); // Parse input as an integer
                    break; // Exit loop if input is valid
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                }
            }

            // Handle menu options
            switch (option) {
                case 1: // Login and navigate to home
                    currentUser = loginController.login();
                    if (currentUser != null) {
                        homeController.showHome(currentUser);
                    }
                    break;
                case 2: // Signup and navigate to home
                    currentUser = signupController.signup();
                    if (currentUser != null) {
                        homeController.showHome(currentUser);
                    }
                    break;
                case 3: // Exit application
                    System.out.println("\nExiting the application. Goodbye!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid option. Please select 1, 2, or 3.");
            }
        }

        scanner.close();
    }
}
