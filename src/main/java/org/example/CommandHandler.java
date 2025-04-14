package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CommandHandler {

    private final DatabaseHandler dbHandler;
    private State botState = State.IDLE;
    private String tempName;
    private final Storage storage;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public CommandHandler(DatabaseHandler dbHandler, Storage storage) {
        this.dbHandler = dbHandler;
        this.storage = storage;
    }

    public String handleCommand(String messageText, long chatId) {
        switch (messageText) {
            case "/start":
                return "Добро пожаловать в BirthdayHelper Bot!\n\nДля получения списка команд введите /help\nНе забывайте поздравлять близких вам людей!";
            case "/help":
                return "Доступные команды:\n/start - запуск\n/add_new_birthday - добавление дня рождения\n/remove_birthday - удаление дня рождения\n/example_of_congratulations - пример поздравления\n/help - список команд";
            case "/add_new_birthday":
                botState = State.AWAITING_NAME;
                return "Введите Имя для добавления дня рождения:";
            case "/remove_birthday":
                List<Birthday> birthdays = dbHandler.getAllBirthdays();
                if (birthdays.isEmpty()) {
                    return "Список дней рождений пуст!";
                }
                StringBuilder sb = new StringBuilder("Список всех дней рождений:\n");
                for (Birthday birthday : birthdays) {
                    sb.append(birthday).append("\n");
                }
                sb.append("Введите ID записи, которую хотите удалить:");
                botState = State.AWAITING_ID_TO_REMOVE;
                return sb.toString();
            case "/example_of_congratulations":
                return storage.getRandQuote();
            default:
                if (botState == State.AWAITING_NAME) {
                    tempName = messageText;
                    botState = State.AWAITING_DATE;
                    return "Введите дату рождения в формате дд.мм.гггг:";
                } else if (botState == State.AWAITING_DATE) {
                    try {
                        LocalDate birthDate = LocalDate.parse(messageText, dateFormatter);
                        Birthday newBirthday = new Birthday(tempName, birthDate);
                        dbHandler.addBirthday(newBirthday);
                        botState = State.IDLE;
                        return "День рождения " + tempName + " добавлен!";
                    } catch (DateTimeParseException e) {
                        botState = State.AWAITING_DATE;
                        return "Неверный формат даты. Пожалуйста, введите дату в формате дд.мм.гггг";
                    }
                } else if (botState == State.AWAITING_ID_TO_REMOVE) {
                    try {
                        int idToRemove = Integer.parseInt(messageText);
                        dbHandler.removeBirthday(idToRemove);
                        botState = State.IDLE;
                        return "День рождения с ID " + idToRemove + " удален!";
                    } catch (NumberFormatException e) {
                        botState = State.AWAITING_ID_TO_REMOVE;
                        return "Неверный формат ID. Введите числовой ID из списка.";
                    }
                }
                return "Не понимаю команду. Используйте /help";
        }
    }

    public State getBotState() {
        return botState;
    }

    public void setBotState(State botState) {
        this.botState = botState;
    }
}
