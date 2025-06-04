package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private List<Transaction> transactions = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.categories.add(new Category("Geral"));
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Category> getCategories() {
        return categories;
    }
}