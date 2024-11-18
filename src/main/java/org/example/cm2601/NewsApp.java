package org.example.cm2601;

import org.example.cm2601.Controller.*;
import org.example.cm2601.model.User;
import com.google.gson.JsonArray;

import java.util.Scanner;

public class NewsApp {
    public static void main(String[] args) {
        // Create necessary objects for user management, fetching, categorizing news, and handling home navigation
        UserDatabase userDatabase = new UserDatabase();

        SignupController signupController = new SignupController(userDatabase);
        LoginController loginController = new LoginController(userDatabase);
        FetchNews fetchNews = new FetchNews();
        CategorizeNews categorizeNews = new CategorizeNews();

        HomeController homeController = new HomeController(userDatabase, loginController, signupController, fetchNews, categorizeNews);

        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            // Display menu
            System.out.println("\n=== Personalized News Recommendation System ===");
            System.out.println("1. Login");
            System.out.println("2. Signup");
            System.out.println("3. Fetch and Categorize News");
            System.out.println("4. View Saved News Titles");
            System.out.println("5. Exit");
            System.out.print("Select an option: ");

            int option = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character after the integer input

            switch (option) {
                case 1: // Login and navigate to home
                    User user = loginController.login();
                    if (user != null) {
                        homeController.showHome(user);
                    }
                    break;
                case 2: // Signup and navigate to home
                    user = signupController.signup();
                    if (user != null) {
                        homeController.showHome(user);
                    }
                    break;
                case 3: // Fetch and categorize news
                    JsonArray articles = fetchNews.fetchNews();
                    if (articles != null && !articles.isEmpty()) {
                        categorizeNews.categorizeAndSaveNews(articles);
                        System.out.println("News articles fetched and categorized successfully.");
                    } else {
                        System.out.println("No articles fetched.");
                    }
                    break;
                case 4: // View saved news titles
                    JsonArray savedArticles = categorizeNews.loadFromFile();
                    if (savedArticles != null && !savedArticles.isEmpty()) {
                        System.out.println("Saved News Titles:");
                        for (int i = 0; i < savedArticles.size(); i++) {
                            System.out.println((i + 1) + ". " + savedArticles.get(i).getAsJsonObject().get("title").getAsString());
                        }
                    } else {
                        System.out.println("No saved articles available.");
                    }
                    break;
                case 5: // Exit application
                    System.out.println("Exiting the application. Goodbye!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid option. Please select 1, 2, 3, 4, or 5.");
            }
        }

        scanner.close();
    }
}
