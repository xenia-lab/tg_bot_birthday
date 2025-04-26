package org.example;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ReminderCommandHandler {

    private final DatabaseHandler dbHandler;
    private final CommandHandler mainCommandHandler;
    private int tempDaysBefore;
    private String tempTheme;


    public ReminderCommandHandler(DatabaseHandler dbHandler, CommandHandler mainCommandHandler) {
        this.dbHandler = dbHandler;
        this.mainCommandHandler = mainCommandHandler;
    }

    public String handleSetReminderCommand() {
        mainCommandHandler.setBotState(State.AWAITING_REMINDER_THEME);
        return "Выберите тему уведомления:\n1. Напоминание о дне рождения\n2. Выбрать подарок для именинника\n3. Выбрать образ на вечеринку\nИли введите свое";
    }

    public String handleAwaitingReminderTheme(String messageText) {
        String theme;
        switch (messageText) {
            case "1":
                theme = "Напоминание о Дне рождения";
                break;
            case "2":
                theme = "Выбрать подарок для именинника";
                break;
            case "3":
                theme = "Выбрать образ на вечеринку";
                break;
            default:
                theme = messageText.trim();
                break;
        }
        tempTheme = theme;
        mainCommandHandler.setBotState(State.AWAITING_REMINDER_DAYS);
        return "Введите за сколько дней до события вы хотите получать напоминание (число).";
    }

    public String handleAwaitingReminderDays(String messageText) {
        final int MAX_DAYS_BEfORE = 36500;
        try {
            tempDaysBefore = Integer.parseInt(messageText);

            if (tempDaysBefore > MAX_DAYS_BEfORE) {
                mainCommandHandler.setBotState(State.AWAITING_REMINDER_DAYS);
                return "Максимальное количество дней 36500 (это примерно 100 лет). Введите число.";
            }
        } catch  (NumberFormatException e) {
            mainCommandHandler.setBotState(State.AWAITING_REMINDER_DAYS);
            return "Неверный формат. Введите целое число.";
        }

        mainCommandHandler.setBotState(State.AWAITING_REMINDER_TIME);
        return "Введите время суток, в которое вы хотите получать напоминание (чч:мм)";
    }

    public String handleAwaitingReminderTime(String messageText, long chatId) {
        LocalTime time;
        try {
            time = LocalTime.parse(messageText, DateTimeFormatter.ofPattern("HH:mm"));
        } catch  (DateTimeParseException e) {
            mainCommandHandler.setBotState(State.AWAITING_REMINDER_TIME);
            return "Неверный формат времени. Введите в формате чч:мм.";
        }

        Reminder newReminder = new Reminder(0, tempDaysBefore, time, tempTheme, String.valueOf(chatId));
        dbHandler.addReminder(newReminder, String.valueOf(chatId));
        tempTheme = null;
        tempDaysBefore = 0;

        mainCommandHandler.setBotState(State.IDLE);
        return "Напоминание установлено.";


    }

    public String handleRemoveReminderCommand(long chatId) {
        List<Reminder> reminders = dbHandler.getAllReminders(String.valueOf(chatId));

        if (reminders.isEmpty()) {
            mainCommandHandler.setBotState(State.IDLE);
            return "Напоминаний нет.";
        }

        StringBuilder reminderListBuilder = new StringBuilder("Список напоминаний:\n");

        for (int i = 0; i < reminders.size(); i++) {
            reminderListBuilder.append((i+1)).append(". ").append(reminders.get(i)).append("\n");
        }

        reminderListBuilder.append("Введите номер напоминания для удаления: ");
        mainCommandHandler.setBotState(State.AWAITING_REMINDER_NUMBER_TO_REMOVE);
        return reminderListBuilder.toString();
    }

    public String handleAwaitingReminderNumberToRemove(String messageText, long chatId) {
        try {
            int reminderIndex;
            reminderIndex = Integer.parseInt(messageText) - 1;

            List<Reminder> reminders = dbHandler.getAllReminders(String.valueOf(chatId));
            if (reminderIndex >= 0 && reminderIndex < reminders.size()) {
                int reminderId = reminders.get(reminderIndex).getId();
                dbHandler.removeReminder(reminderId);
                mainCommandHandler.setBotState(State.IDLE);
                return "Напоминание удалено!";
            } else {
                mainCommandHandler.setBotState(State.IDLE);
                return "Неверный номер напоминания. Введите номер из спика.";
            }
        } catch (NumberFormatException e) {
            mainCommandHandler.setBotState(State.AWAITING_REMINDER_NUMBER_TO_REMOVE);
            return "Неверный формат номера. Введите номер из списка.";
        }
    }
}
