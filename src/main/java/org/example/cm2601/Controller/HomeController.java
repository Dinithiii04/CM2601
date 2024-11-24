package org.example.cm2601.Controller;

import com.google.gson.JsonArray;
import org.example.cm2601.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class HomeController {
    //Dependencies
    private UserDatabase userDatabase;
    private LoginController loginController;
    private SignupController signupController;
    private FetchNews fetchNews;
    private CategorizeNews categorizeNews;


    // Caching mechanism to temporarily store fetched articles for users
    private Map<String, JsonArray> userArticlesCache = new HashMap<>();

    //composition
    public HomeController(UserDatabase userDatabase, LoginController loginController, SignupController signupController, FetchNews fetchNews) {
        this.userDatabase = userDatabase;
        this.loginController = loginController;
        this.signupController = signupController;
        this.fetchNews = fetchNews;
        this.categorizeNews = new CategorizeNews(userDatabase);
    }


    public void showHome(User user) {
        if (user == null) return;

        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n--- Welcome to Your Personalized News Feed, " + user.getUsername() + " ---");
            System.out.println("1. Fetch and Read News");
            System.out.println("2. View Session History");
            System.out.println("3. Logout \n");

            int option = -1; // Initialize with an invalid value

            while (true) {
                System.out.print("* Choose an option: ");
                try {
                    option = Integer.parseInt(scanner.nextLine());
                    break; // Exit loop if input is valid
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                }
            }

            // Process menu options
            switch (option) {
                case 1:
                    saveNewsToFile(user);
                    break;
                case 2:
                    viewReadingHistory(user);
                    break;
                case 3:
                    System.out.println("\nLogging out. Goodbye, " + user.getUsername() + "!");
                    // Cleanup user-specific articles from memory when logging out
                    categorizeNews.deleteUserArticles(user.getUsername());
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option (1, 2, or 3).");
            }
        }

    }

    //Fetches news articles for the user and saves them to categorized files.
    private void saveNewsToFile(User user) {

        System.out.println(" \n Fetching and categorizing articles...  ");
        JsonArray articlesJsonArray = userArticlesCache.getOrDefault(user.getUsername(), null);
        if (articlesJsonArray == null) {
            articlesJsonArray = fetchNews.fetchNews(); // Fetch articles if not in the cache
            if (articlesJsonArray != null) {
                userArticlesCache.put(user.getUsername(), articlesJsonArray); // store fetched articles
            }
        }

        if (articlesJsonArray != null) {
            categorizeNews.categorizeAndSaveNews(articlesJsonArray, user);

        } else {
            System.out.println("No articles available. Try again later.");
        }
    }

    // Adds a new preference
    private void addPreference(User user) {
        Scanner scanner = new Scanner(System.in);
        String category = scanner.nextLine().trim();
        user.addPreference(category); // Add category


        userDatabase.updatePreferences(user.getUsername(), category);
        System.out.println("Added " + category + " to your preferences.");
    }

    // Displays Reading history
    private void viewReadingHistory(User user) {
        System.out.println("\n-------------- Session History-------------- ");
        //refresh user data from db
        User refreshedUser = userDatabase.getUser(user.getUsername());
        if(refreshedUser == null){
            System.out.println("error retrieving user data");
            return;
        }


        List<String> history = user.getReadingHistory();
        if (history.isEmpty()) {
            System.out.println("No reading history available.");
        } else {
            for(int i=0; i< history.size(); i++){
                System.out.println((i+1) + ". " + history.get(i));
            }
        }
    }
}