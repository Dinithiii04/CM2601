package org.example.cm2601;

import com.google.gson.JsonArray;
import org.example.cm2601.Controller.*;
import org.example.cm2601.model.User;

import java.util.Scanner;

public class NewsApp {
    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.log.org.mongodb.driver", "OFF");
        // Create necessary objects for user management, fetching, categorizing news, and handling home navigation
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
            System.out.println("\n=== Personalized News Recommendation System ===");
            System.out.println("1. Login");
            System.out.println("2. Signup");
            System.out.println("5. Exit");
            System.out.print("Select an option: ");

            int option = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character after the integer input

            switch (option) {
                case 1: // Login and navigate to home
                    currentUser = loginController.login();
                    if (currentUser != null) {
                        homeController.showHome(currentUser);
                    }
                    break;
                case 2: // Signup and navigate to home
                    currentUser  = signupController.signup();
                    if (currentUser  != null) {
                        homeController.showHome(currentUser );
                    }
                    break;
                case 3: // Fetch and categorize news
                    if(currentUser == null){
                        System.out.println("you need to logged into fetch");
                        break;
                    }
                    JsonArray articles = fetchNews.fetchNews();

                    if (articles != null && !articles.isEmpty()) {
                        categorizeNews.categorizeAndSaveNews(articles, currentUser);
                        System.out.println("News articles fetched and categorized successfully.");
                    } else {
                        System.out.println("No articles fetched.");
                    }
                    break;
                case 4: // View saved news titles
                    if (currentUser == null) {
                        System.out.println("You need to be logged in to view saved news titles.");
                        break;
                    }
                    JsonArray savedArticles = categorizeNews.loadArticlesFromDatabase(currentUser.getUsername());
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
