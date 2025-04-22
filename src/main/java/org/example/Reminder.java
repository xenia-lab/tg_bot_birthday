package org.example;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reminder {
    private int id;
    private int daysBefore;
    private LocalTime time;
    private String theme;
    private String chatId;

    public Reminder(int id, int daysBefore, LocalTime time, String theme, String chatId) {
        this.id = id;
        this.daysBefore = daysBefore;
        this.time = time;
        this.theme = theme;
        this.chatId = chatId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDaysBefore() {
        return daysBefore;
    }

    public void setDaysBefore(int daysBefore) {
        this.daysBefore = daysBefore;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return  " За " + daysBefore +
                " дней до события в " + time +
                ", Тема: " + theme;
    }
}
