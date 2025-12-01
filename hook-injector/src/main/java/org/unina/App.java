package org.unina;

import java.io.IOException;

public class App {
    public static void main(String[] args) {
        Repository repo = new Repository("path/to/repo/src");
        try {
            repo.createCopy();
        } catch (IOException e) {
            System.err.println("Error creating repository copy: " + e.getMessage());
            return;
        }
    }
}
