package org.example.cm2601.Controller;

import org.example.cm2601.model.NewsArticle;
import org.example.cm2601.model.User;
import com.google.gson.JsonArray;

import java.util.List;
import java.util.Scanner;

public class HomeController {
    private UserDatabase userDatabase;
    private LoginController loginController;
    private SignupController signupController;
    private FetchNews fetchNews;
    private CategorizeNews categorizeNews;

    public HomeController(UserDatabase userDatabase, LoginController loginController, SignupController signupController, FetchNews fetchNews, CategorizeNews categorizeNews) {
        this.userDatabase = userDatabase;
        this.loginController = loginController;
        this.signupController = signupController;
        this.fetchNews = fetchNews;
        this.categorizeNews = categorizeNews;
    }

    public void showHome(User user) {
        if (user == null) return;

        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n=== Welcome to Your Personalized News Feed, " + user.getUsername() + " ===");
            System.out.println("1. View News");
            System.out.println("2. Add Category to Preferences");
            System.out.println("3. View Reading History");
            System.out.println("4. Logout");

            System.out.print("Choose an option: ");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    displayNews(user);
                    break;
                case 2:
                    addPreference(user);
                    break;
                case 3:
                    viewReadingHistory(user);
                    break;
                case 4:
                    System.out.println("Logging out. Goodbye, " + user.getUsername() + "!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }

    private void displayNews(User user) {
        List<String> preferences = user.getPreferences().isEmpty() ? null : List.copyOf(user.getPreferences());

        System.out.println("Fetching articles...");
        JsonArray articlesJsonArray = fetchNews.fetchNews();

        if (articlesJsonArray != null) {
            categorizeNews.categorizeNews(articlesJsonArray); // This will print categorized articles to console.
        } else {
            System.out.println("No articles available. Try adding preferences.");
        }
    }

    private void addPreference(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a new category to add to your preferences: ");
        String category = scanner.nextLine().trim();
        user.addPreference(category);
        System.out.println("Added " + category + " to your preferences.");
    }

    private void viewReadingHistory(User user) {
        System.out.println("\n=== Reading History ===");
        List<String> history = user.getReadingHistory();
        if (history.isEmpty()) {
            System.out.println("No reading history available.");
        } else {
            history.forEach(System.out::println);
        }
    }
}
