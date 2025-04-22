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
    private final GiftCommandHandler giftCommandHandler;
    private final StatisticsCommandHandler statisticsCommandHandler;
    private final ReminderCommandHandler reminderCommandHandler;

    public CommandHandler(DatabaseHandler dbHandler, Storage storage) {
        this.dbHandler = dbHandler;
        this.storage = storage;
        this.giftCommandHandler = new GiftCommandHandler(dbHandler, this);
        this.statisticsCommandHandler = new StatisticsCommandHandler(dbHandler);
        this.reminderCommandHandler = new ReminderCommandHandler(dbHandler, this);

    }

    public String handleCommand(String messageText, long chatId) {
        try {

            if(messageText.startsWith("/")) {
            setBotState(State.IDLE);
            }

            switch (botState) {
                case AWAITING_GIFT_OWNER_NUMBER:
                    return giftCommandHandler.handleAwaitingGiftOwnerNumber(messageText, chatId);
                case AWAITING_GIFT_NAME:
                    return giftCommandHandler.handleAwaitingGiftName(messageText, chatId);
                case AWAITING_GIFT_OWNER_NUMBER_TO_REMOVE:
                    return giftCommandHandler.handleRemoveGiftAwaitingOwnerNumber(messageText, chatId);
                case AWAITING_GIFT_NUMBER_TO_REMOVE:
                    return giftCommandHandler.handleAwaitingGiftNumberToRemove(messageText, chatId);
                case AWAITING_GIFT_OWNER_NUMBER_TO_VIEW:
                    return giftCommandHandler.handleAwaitingGiftOwnerNumberForView(messageText, chatId);
                case AWAITING_REMINDER_THEME:
                    return reminderCommandHandler.handleAwaitingReminderTheme(messageText);
                case AWAITING_REMINDER_DAYS:
                    return reminderCommandHandler.handleAwaitingReminderDays(messageText);
                case AWAITING_REMINDER_TIME:
                    return reminderCommandHandler.handleAwaitingReminderTime(messageText, chatId);
                case AWAITING_REMINDER_NUMBER_TO_REMOVE:
                    return reminderCommandHandler.handleAwaitingReminderNumberToRemove(messageText, chatId);
                default: {
                    switch (messageText) {
                        case "/start":
                            return "Добро пожаловать в BirthdayHelper Bot!\n\nДля получения списка команд введите /help\nНе забывайте поздравлять близких вам людей!";
                        case "/help":
                            return "Доступные команды:\n/start - запуск\n/add_new_birthday - добавление дня рождения\n/remove_birthday - удаление дня рождения\n/add_gift - добавляет подарок в список подарков\n/remove_gift - удаляет подарок из списка подарков\n/view_gifts - выводит список подарков для конкретного человека\n/set_reminder - добавляет уведомление\n/remove_reminder - удаляет уведомление\n/example_of_congratulations - пример поздравления\n/statistics - статистика на ближайший месяц\n/help - список команд";
                        case "/add_new_birthday":
                            botState = State.AWAITING_NAME;
                            return "Введите Имя для добавления дня рождения:";
                        case "/remove_birthday":
                            List<Birthday> birthdays = dbHandler.getAllBirthdays(String.valueOf(chatId));
                            if (birthdays.isEmpty()) {
                                return "Список дней рождений пуст!";
                            }
                            StringBuilder sb = new StringBuilder("Список всех дней рождений:\n");
                            for (int i = 0; i < birthdays.size(); i++) {
                                Birthday birthday = birthdays.get(i);
                                sb.append((i + 1)).append(". ").append(birthday.getName()).append(" ").append(birthday.getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))).append("\n");
                            }
                            sb.append("Введите номер записи, которую хотите удалить:");
                            botState = State.AWAITING_ID_TO_REMOVE;
                            return sb.toString();
                        case "/example_of_congratulations":
                            return storage.getRandQuote();
                        case "/add_gift":
                            return giftCommandHandler.handleAddGiftCommand(chatId);
                        case "/remove_gift":
                            return giftCommandHandler.handleRemoveGiftCommand(chatId);
                        case "/view_gifts":
                            return giftCommandHandler.handleViewGiftCommand(chatId);
                        case "/set_reminder":
                            return reminderCommandHandler.handleSetReminderCommand();
                        case "/remove_reminder":
                            return reminderCommandHandler.handleRemoveReminderCommand(chatId);
                        case "/statistics":
                            return statisticsCommandHandler.handleStatisticsCommand(String.valueOf(chatId));
                        default:
                            if (botState == State.AWAITING_NAME) {
                                tempName = messageText;
                                botState = State.AWAITING_DATE;
                                return "Введите дату рождения в формате дд.мм.гггг:";
                            } else if (botState == State.AWAITING_DATE) {
                                try {
                                    LocalDate birthDate = LocalDate.parse(messageText, dateFormatter);
                                    Birthday newBirthday = new Birthday(tempName, birthDate, String.valueOf(chatId));
                                    dbHandler.addBirthday(newBirthday);
                                    botState = State.IDLE;
                                    return "День рождения " + tempName + " добавлен!";
                                } catch (DateTimeParseException e) {
                                    botState = State.AWAITING_DATE;
                                    return "Неверный формат даты. Пожалуйста, введите дату в формате дд.мм.гггг";
                                }
                            } else if (botState == State.AWAITING_ID_TO_REMOVE) {
                                try {
                                    int numberToRemove = Integer.parseInt(messageText);
                                    List<Birthday> birthdaysToRemove = dbHandler.getAllBirthdays(String.valueOf(chatId));
                                    if (numberToRemove >= 1 && numberToRemove <= birthdaysToRemove.size()) {
                                        Birthday birthdayToRemove = birthdaysToRemove.get(numberToRemove-1);
                                        int idToRemove = birthdayToRemove.getId();
                                        dbHandler.removeBirthday(idToRemove);
                                        botState = State.IDLE;
                                        return "День рождения с номером " + numberToRemove + " удален!";
                                    } else {
                                        botState = State.AWAITING_ID_TO_REMOVE;
                                        return "Неверный номер. Введите номер из списка.";
                                    }

                                } catch (NumberFormatException e) {
                                    botState = State.AWAITING_ID_TO_REMOVE;
                                    return "Неверный формат номера. Введите номер из списка.";
                                }
                            }
                            return "Не понимаю команду. Используйте /help";
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error handling command: " + messageText);
            e.printStackTrace();
            return "Произошла ошибка при обработке команды. Пожалуйста, попробуйте позже.";
        }
    }

    public State getBotState() {
        return botState;
    }

    public void setBotState(State botState) {
        this.botState = botState;
    }
}
