package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Birthday {
    private int id;
    private String name;
    private LocalDate birthDate;
    private String chatId;

    public Birthday(int id, String name, LocalDate birthDate, String chatId) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.chatId = chatId;
    }

    public Birthday(String name, LocalDate birthDate, String chatId) {
        this.name = name;
        this.birthDate = birthDate;
        this.chatId = chatId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return id + ". " + name + "   " + birthDate.format(formatter);
    }
}
