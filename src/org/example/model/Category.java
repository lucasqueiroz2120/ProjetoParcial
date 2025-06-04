package org.example.model;

public class Category {
    private static int counter = 1;
    private int id;
    private String name;

    public Category(String name) {
        this.name = name;
        this.id = counter++;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}