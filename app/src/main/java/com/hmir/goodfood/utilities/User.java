package com.hmir.goodfood.utilities;

import java.util.List;

public class User {
    private String email;
    private String username;
    private long age;
    private double height;
    private double weight;
    private List<String> health_label;

    public User(String email, String username, long age, double height, double weight, List<String> health_label) {
        this.email = email;
        this.username = username;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.health_label = health_label;
    }

    public User() {
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getHealth_label() {
        return health_label;
    }

    public void setHealth_label(List<String> health_label) {
        this.health_label = health_label;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}