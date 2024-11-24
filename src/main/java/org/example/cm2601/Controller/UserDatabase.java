package org.example.cm2601.Controller;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.example.cm2601.model.User;

import java.util.ArrayList;
import java.util.List;


public class UserDatabase {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> usersCollection;

    public UserDatabase() {
        connectToMongoDB();
    }

    private void connectToMongoDB() {

        String connectionString = "mongodb+srv://newsappuser:dinimongo12@newsappcluster.kanzb.mongodb.net/?retryWrites=true&w=majority&appName=NewsAppCluster";

        ConnectionString connString = new ConnectionString(connectionString);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .build();
        mongoClient = MongoClients.create(settings);   // Create MongoDB client
        // Database name
        database = mongoClient.getDatabase("news_app");
        // Collection name
        usersCollection = database.getCollection("users");
    }


    public boolean isUserExists(String username) {     //Check if a user exists in the database by username
        Bson filter = Filters.eq("username", username);
        Document userDoc = usersCollection.find(filter).first();
        return userDoc != null;
    }

    public synchronized boolean addUser(User user) {
        if (isUserExists(user.getUsername())) {
            return false; // Username already exists
        }
        Document userDoc = new Document("username", user.getUsername())
                .append("password", user.getPassword())
                .append("preferences", new ArrayList<Document>());

        usersCollection.insertOne(userDoc);
        return true;
    }


    public User getUser(String username) {
        Bson filter = Filters.eq("username", username);
        Document userDoc = usersCollection.find(filter).first();
        if (userDoc != null) {
            return documentToUser(userDoc);
        }
        return null;
    }

    public boolean verifyUser(String username, String password) {
        User user = getUser(username);
        return user != null && user.getPassword().equals(password);
    }

    private User documentToUser(Document doc) {
        String username = doc.getString("username");
        String password = doc.getString("password");
        User user = new User(username, password);

        //  load Preferences
        List<Document> prefs = (List<Document>) doc.get("preferences");
        if (prefs != null) {
            for (Document pref : prefs) {
                String category = pref.getString("category");
                int count = pref.getInteger("count", 1);
                user.getPreferences().put(category, count);
            }
        }

        // Reading history
        List<String> history = (List<String>) doc.get("readingHistory");
        if (history != null) {
            user.setReadingHistory(history);
        }

        return user;
    }

    // Synchronized method to ensure thread safety when multiple threads try to update preferences
    public synchronized void updatePreferences(String username, String category) {
        Bson filter = Filters.eq("username", username);
        Document userDoc = usersCollection.find(filter).first();

        if (userDoc == null) {
            System.out.println("User not found: " + username);
            return;
        }

        // Load and update preferences
        List<Document> preferences = (List<Document>) userDoc.get("preferences");
        if (preferences == null) {
            preferences = new ArrayList<>();
        }

        boolean categoryExists = false;
        for (Document pref : preferences) {
            if (pref.getString("category").equals(category)) {
                int count = pref.getInteger("count", 0) + 1;
                pref.put("count", count);
                categoryExists = true;
                break;
            }
        }

        if (!categoryExists) {
            Document newPreference = new Document("category", category).append("count", 1);
            preferences.add(newPreference);
        }

        // Update the user's preferences in the database
        Bson update = Updates.set("preferences", preferences);
        usersCollection.updateOne(filter, update);
    }
    public MongoDatabase getDatabase() {
        return this.database;
    }

    public void addToReadHisotry(String username, String articleTitle){
        Bson filter = Filters.eq("Username", username);
        Bson update = Updates.push("readingHistory", articleTitle);
        usersCollection.updateOne(filter, update);

    }

}
