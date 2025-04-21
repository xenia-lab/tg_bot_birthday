package org.example;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ReminderScheduler {
    private final DatabaseHandler dbHandler;
    private final long chatId;
    private final MyTelegramBot bot;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ReminderScheduler(DatabaseHandler dbHandler, long chatId, MyTelegramBot bot) {
        this.dbHandler = dbHandler;
        this.chatId = chatId;
        this.bot = bot;
    }

    public void start() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkReminders();
            }
        } , 0, 60*1000);
    }

    private  int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        if (birthDate == null || currentDate == null) {
            return 0;
        }
        return Period.between(birthDate, currentDate).getYears();
    }

    private void checkReminders() {
        List<Reminder> allReminders = dbHandler.getAllReminders();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<Birthday> allBirthdays = dbHandler.getAllBirthdays();

        for (Reminder reminder: allReminders) {
            for (Birthday birthday: allBirthdays) {
                LocalDate birthdayDate = birthday.getBirthDate();
                LocalDate nextBirthday = birthdayDate.withYear(today.getYear());
                if (nextBirthday.isBefore(today)) {
                    nextBirthday = nextBirthday.plusYears(1);
                }

                System.out.println(now.getHour());
                System.out.println(now.getMinute());



                LocalDate reminderDate = nextBirthday.minusDays(reminder.getDaysBefore());
                System.out.println("Today: " + today);
                System.out.println("Next Birthday: " + nextBirthday);
                System.out.println("Reminder Date: " + reminderDate);

                if (today.equals(reminderDate) && now.getHour() == reminder.getTime().getHour() && now.getMinute() == reminder.getTime().getMinute()) {
                    String formattedBirthdayDate = nextBirthday.format(dateTimeFormatter);

                    int age = calculateAge(birthdayDate, nextBirthday);

                    String message = "Напоминание: " + reminder.getTheme() + ".\nДля дня рождения: "+ birthday.getName() + " " + formattedBirthdayDate + " (Исполняется " + age + " лет/год/года)";
                    System.out.println(message);

                    bot.sendMessage(chatId, message);
                }
            }
        }
    }


}
