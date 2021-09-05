package com.github.meg6pam.alinkabot.model;

import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;

public class Recipient extends User {
    private String telephone;
    private String email;
    private LocalDateTime date;
    private String role;
    private String description;

    public Recipient(String username) {
        super.setUserName(username);
        this.date = LocalDateTime.now();
        this.role = "USER";
    }

    public Recipient(String username, LocalDateTime date, String role) {
        super.setUserName(username);
        this.date = date;
        this.role = role;
    }

    public Recipient(String username, LocalDateTime date) {
        super.setUserName(username);
        this.date = date;
        this.role = "USER";
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getRole() {
        return role;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", firstname='" + getFirstName() + '\'' +
                ", lastname='" + getLastName() + '\'' +
                ", username='" + getUserName() + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", date=" + date +
                ", role='" + role + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
