package org.example.cm2601;

import org.example.cm2601.Controller.*;
import org.example.cm2601.model.User;
import com.google.gson.JsonArray;

import java.util.Scanner;

public class NewsApp {
    public static void main(String[] args) {
        UserDatabase userDatabase = new UserDatabase();

        SignupController signupController = new SignupController(userDatabase);
        LoginController loginController = new LoginController(userDatabase);
        FetchNews fetchNews = new FetchNews();
        CategorizeNews categorizeNews = new CategorizeNews();

        HomeController homeController = new HomeController(userDatabase, loginController, signupController, fetchNews, categorizeNews);

        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n=== Personalized News Recommendation System ===");
            System.out.println("1. Login");
            System.out.println("2. Signup");
            System.out.println("5. Exit");
            System.out.print("Select an option: ");

            int option = scanner.nextInt();

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
                    if (articles != null && articles.size() > 0) {
                        categorizeNews.categorizeAndSaveNews(articles);
                        System.out.println("News articles fetched and categorized successfully.");
                    } else {
                        System.out.println("No articles fetched.");
                    }
                    break;
                case 4: // View saved news titles
                    showSavedNewsTitles();
                    break;
                case 5: // Exit application
                    System.out.println("Exiting the application. Goodbye!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid option. Please select 1, 2, 3, 4, or 5.");
            }
        }
    }

    private static void showSavedNewsTitles() {
        try {
            JsonArray savedArticles = CategorizeNews.loadFromFile();

            if (savedArticles != null && savedArticles.size() > 0) {
                System.out.println("\nFetched and Categorized News Titles:");
                for (int i = 0; i < savedArticles.size(); i++) {
                    String title = savedArticles.get(i).getAsJsonObject().get("title").getAsString();
                    System.out.println((i + 1) + ". " + title);
                }

                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter the number of the news title you want to view: ");
                int choice = scanner.nextInt();

                if (choice >= 1 && choice <= savedArticles.size()) {
                    String title = savedArticles.get(choice - 1).getAsJsonObject().get("title").getAsString();
                    String description = savedArticles.get(choice - 1).getAsJsonObject().get("description").getAsString();
                    String url = savedArticles.get(choice - 1).getAsJsonObject().get("url").getAsString();
                    String category = savedArticles.get(choice - 1).getAsJsonObject().get("category").getAsString();

                    System.out.println("\nYou selected: " + title);
                    System.out.println("Description: " + description);
                    System.out.println("URL: " + url);
                    System.out.println("Category: " + category);
                } else {
                    System.out.println("Invalid choice.");
                }
            } else {
                System.out.println("No saved news articles available.");
            }
        } catch (Exception e) {
            System.out.println("Error loading saved news: " + e.getMessage());
        }
    }
}
