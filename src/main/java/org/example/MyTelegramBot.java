package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.List;
import java.util.ArrayList;

public class MyTelegramBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;
    private final DatabaseHandler dbHandler;
    private final CommandHandler commandHandler;
    private final Storage storage;


    public MyTelegramBot(String botUsername, String botToken, Storage storage) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.storage = storage;
        this.dbHandler = new DatabaseHandler();
        this.commandHandler = new CommandHandler(dbHandler,storage);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            System.out.println(chatId);
            String response = commandHandler.handleCommand(messageText, chatId);
            sendMessage(chatId, response);

            /*if (messageText.equals("/start")) {
                sendMessage(chatId, "Добро пожаловать в BirthdayHelper Bot!\n\nДля получения списка команд введите /help\nНе забывайте поздравлять близких вам людей!");
            }

            else if(messageText.equals("/example_of_congratulations")){
                Storage storage = new Storage();
                sendMessage(chatId, storage.getRandQuote());
            }

            else if (messageText.equals("/help")) {
                sendMessage(chatId, "Доступные команды:\n/start - запуск\n/add_new_birthday - добавление дня рождения\n/remove_birthday - удаление дня рождения\n/example_of_congratulations - пример поздравления\n/help - список команд");
            }

            else if (messageText.equals("/add_new_birthday") ||  messageText.equals("/remove_birthday")) {
                response = commandHandler.handleCommand(messageText, chatId);
            }

            else {
                sendMessage(chatId,"Не понимаю команду. Используйте /help");
            }*/

        }
    }


    public void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("/start"));
        row.add(new KeyboardButton("/help"));

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        keyboardMarkup.setSelective(false);

        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }

    @Override
    public String getBotUsername() {
        return "BdayHelp_bot";
    }

    @Override
    public String getBotToken() {
        return "7525336630:AAG0bT8j1nBhwtp-28vy0egX4_1f3UCAHjo";
    }
}


