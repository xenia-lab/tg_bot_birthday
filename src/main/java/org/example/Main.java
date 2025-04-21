package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {

        String botUsername = "BdayHelp_bot";
        String botToken = "7525336630:AAG0bT8j1nBhwtp-28vy0egX4_1f3UCAHjo";

        long chatId = 1147436672;


        try {
            DatabaseHandler dbHandler = new DatabaseHandler();
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            Storage storage = new Storage();
            CommandHandler commandHandler = new CommandHandler(dbHandler, storage);
            MyTelegramBot bot = new MyTelegramBot(botUsername, botToken, storage);
            ReminderScheduler scheduler = new ReminderScheduler(dbHandler, chatId, bot);
            scheduler.start();
            botsApi.registerBot(bot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}