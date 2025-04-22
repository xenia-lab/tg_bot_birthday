package org.example;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StatisticsCommandHandler {
    private final DatabaseHandler dbHandler;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public StatisticsCommandHandler(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }
    public String handleStatisticsCommand(String chatId) {
        List<Birthday> birthdaysNextMonth = dbHandler.getBirthdaysForNextMonth(chatId);

        Collections.sort(birthdaysNextMonth, Comparator.comparing(b -> b.getBirthDate().getDayOfMonth()));

        int count = birthdaysNextMonth.size();
        StringBuilder statistics = new StringBuilder("Статистика на следующий месяц:\nКоличество дней рождений в следующем месяце: " + count + "\n");
        for (Birthday birthday : birthdaysNextMonth) {
            statistics.append(birthday.getName()).append(" - ").append(birthday.getBirthDate().format(dateFormatter)).append("\n");
        }
        return statistics.toString();
    }
}
