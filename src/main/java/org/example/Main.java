package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {

        String botUsername = "BdayHelp_bot";
        String botToken = "7525336630:AAG0bT8j1nBhwtp-28vy0egX4_1f3UCAHjo";

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            Storage storage = new Storage();

            botsApi.registerBot(new MyTelegramBot(botUsername, botToken, storage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}