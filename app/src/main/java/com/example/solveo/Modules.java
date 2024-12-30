package com.example.solveo;

public class Modules {
    private String title;

    // Default constructor required for Firebase
    public Modules() {}

    // Constructor to initialize title
    public Modules(String title) {
        this.title = title;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Setter for title
    public void setTitle(String title) {
        this.title = title;
    }
}
