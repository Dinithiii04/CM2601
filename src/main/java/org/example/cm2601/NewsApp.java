package org.example.cm2601;

import org.example.cm2601.Controller.*;
import org.example.cm2601.model.User;

import java.util.Scanner;

public class NewsApp {
    public static void main(String[] args) {
        UserDatabase userDatabase = new UserDatabase();

        SignupController signupController = new SignupController(userDatabase);
        LoginController loginController = new LoginController(userDatabase);

        // Initialize FetchNews and CategorizeNews instead of NewsToOpenAI
        FetchNews fetchNews = new FetchNews();
        CategorizeNews categorizeNews = new CategorizeNews();

        HomeController homeController = new HomeController(userDatabase, loginController, signupController, fetchNews, categorizeNews);

        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n=== Personalized News Recommendation System ===");
            System.out.println("1. Login");
            System.out.println("2. Signup");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    User user = loginController.login();
                    if (user != null) {
                        homeController.showHome(user);
                    }
                    break;
                case 2:
                    user = signupController.signup();
                    if (user != null) {
                        homeController.showHome(user);
                    }
                    break;
                case 3:
                    System.out.println("Exiting the application. Goodbye!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid option. Please select 1, 2, or 3.");
            }
        }
    }
}
