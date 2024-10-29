package com.example.demo;

import org.springframework.stereotype.Component;

@Component
public class test {
    
    public static void main(String[] args) {
        String currentDirectory = System.getProperty("user.dir");
        System.out.println("Current working directory: " + currentDirectory);
    }
}
