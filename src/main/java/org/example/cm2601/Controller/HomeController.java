package org.example.cm2601.Controller;

import com.google.gson.JsonArray;
import org.example.cm2601.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class HomeController {
    private UserDatabase userDatabase;
    private LoginController loginController;
    private SignupController signupController;
    private FetchNews fetchNews;
    private CategorizeNews categorizeNews;

    private Map<String, JsonArray> userArticlesCache = new HashMap<>();


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
            System.out.println("\n=== Welcome to Your Personalized News Feed, " + user.getUsername() + " ===");
            System.out.println("1. Fetch and Read News");
            System.out.println("3. View Reading History");
            System.out.println("4. Logout \n");

            System.out.println(" Choose an option: ");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    saveNewsToFile(user);  // Make sure the user is passed correctly
                    break;
                case 2:
                    addPreference(user);   // Update preferences for the correct user
                    break;
                case 3:
                    viewReadingHistory(user);
                    break;
                case 4:
                    System.out.println("Logging out. Goodbye, " + user.getUsername() + "!");
                    categorizeNews.deleteUserArticles(user.getUsername()); // Cleanup user articles
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }

    private void saveNewsToFile(User user) {
        // Make sure user preferences are updated before saving news
        System.out.println(" \n Fetching and saving articles...  ");
        JsonArray articlesJsonArray = userArticlesCache.getOrDefault(user.getUsername(), null);
        if (articlesJsonArray == null) {
            articlesJsonArray = fetchNews.fetchNews();
            if (articlesJsonArray != null) {
                userArticlesCache.put(user.getUsername(), articlesJsonArray); // Cache fetched articles
            }
        }

        if (articlesJsonArray != null) {
            categorizeNews.categorizeAndSaveNews(articlesJsonArray, user); // Save each categorized article to a file individually.
            System.out.println("News saved into the file successfully. \n" ); // Success message moved here
        } else {
            System.out.println("No articles available. Try again later.");
        }
    }

    private void addPreference(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\n Enter a new category to add to your preferences: ");
        String category = scanner.nextLine().trim();
        user.addPreference(category); // Add category to the user’s preferences

        // Save the updated preferences in the file
        userDatabase.updatePreferences(user.getUsername(), category);
        System.out.println("Added " + category + " to your preferences.");
    }

    private void viewReadingHistory(User user) {
        System.out.println("\n=== Reading History ===");
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