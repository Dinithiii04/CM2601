package org.example.cm2601.Controller;

import org.example.cm2601.model.NewsArticle;
import org.example.cm2601.model.User;

import java.util.List;
import java.util.Scanner;

public class HomeController {
    private UserDatabase userDatabase;
    private LoginController loginController;
    private SignupController signupController;
    private NewsAPIClient newsAPIClient; // New API client for fetching news articles

    public HomeController(UserDatabase userDatabase, LoginController loginController, SignupController signupController) {
        this.userDatabase = userDatabase;
        this.loginController = loginController;
        this.signupController = signupController;
        this.newsAPIClient = new NewsAPIClient(); // Initializing NewsAPIClient instance
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
                    displayNews(user); // Fetch and display news based on user's preferences
                    break;
                case 2:
                    addPreference(user); // Add new category to user's preferences
                    break;
                case 3:
                    viewReadingHistory(user); // Show the user's reading history
                    break;
                case 4:
                    System.out.println("Logging out. Goodbye, " + user.getUsername() + "!");
                    isRunning = false; // Exit the loop to log out
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }

    private void displayNews(User user) {
        List<String> preferences = user.getPreferences().isEmpty() ? null : List.copyOf(user.getPreferences());

        // Fetch articles from the NewsAPIClient based on user preferences
        List<NewsArticle> articles;
        if (preferences == null || preferences.isEmpty()) { // Check if user has preferences
            System.out.println("Fetching trending articles...");
            articles = newsAPIClient.fetchNews(); // Fetch all trending news if no preferences
        } else {
            System.out.println("Fetching articles based on your preferences...");
            articles = filterArticlesByPreferences(newsAPIClient.fetchNews(), preferences); // Filter news based on preferences
        }

        // Display articles or prompt if no articles are available
        if (articles.isEmpty()) {
            System.out.println("No articles available. Try adding preferences.");
        } else {
            System.out.println("=== News Feed ===");
            for (NewsArticle article : articles) {
                System.out.println(article); // Display each article
                user.addReadingHistory(article.getTitle()); // Add article title to user's reading history
            }
        }
    }

    // New helper method to filter articles by user's preferences
    private List<NewsArticle> filterArticlesByPreferences(List<NewsArticle> articles, List<String> preferences) {
        return articles.stream()
                .filter(article -> preferences.stream()
                        .anyMatch(pref -> article.getTitle().toLowerCase().contains(pref.toLowerCase())))
                .toList(); // Check if article title contains any preference keywords
    }

    private void addPreference(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a new category to add to your preferences: ");
        String category = scanner.nextLine().trim();
        user.addPreference(category); // Add new category to user's preference list
        System.out.println("Added " + category + " to your preferences.");
    }

    private void viewReadingHistory(User user) {
        System.out.println("\n=== Reading History ===");
        List<String> history = user.getReadingHistory();
        if (history.isEmpty()) {
            System.out.println("No reading history available.");
        } else {
            history.forEach(System.out::println); // Display each item in reading history
        }
    }
}
