package org.example;

import java.sql.*;
import java.time.LocalDate;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    private final String url;
    private final String user;
    private final String password;

    public DatabaseHandler() {
        this.url = "jdbc:postgresql://localhost:5432/Birthday_Bot";
        this.user = "postgres";
        this.password = "109615";
        createTables();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void createTables() {
        createBirthdaysTable();
        createGiftsTable();
        createRemindersTable();
    }

    public void createBirthdaysTable() {
        String sql = "CREATE TABLE IF NOT EXISTS birthdays (\n"
                + "id SERIAL PRIMARY KEY,\n"
                + "name VARCHAR(255) NOT NULL,\n"
                + "birth_date DATE NOT NULL,\n"
                + "chat_id VARCHAR(255) NOT NULL\n"
                + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица создана");
        } catch (SQLException e) {
            System.err.println("Ошибка создания таблицы:");
            e.printStackTrace();
        }
    }

    public void createGiftsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS gifts (\n"
                + "id SERIAL PRIMARY KEY,\n"
                + "name_gift VARCHAR(255) NOT NULL,\n"
                + "person_id INTEGER REFERENCES birthdays(id), \n"
                + "chat_id VARCHAR(255) NOT NULL \n"
                + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица создана");
        } catch (SQLException e) {
            System.err.println("Ошибка создания таблицы:");
            e.printStackTrace();
        }
    }

    public void createRemindersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS reminders (\n"
                + "id SERIAL PRIMARY KEY,\n"
                + "days_before INTEGER NOT NULL,\n"
                + "time TIME NOT NULL, \n"
                + "theme VARCHAR(255) NOT NULL, \n"
                + "chat_id VARCHAR(255) NOT NULL \n"
                + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица создана");
        } catch (SQLException e) {
            System.err.println("Ошибка создания таблицы:");
            e.printStackTrace();
        }
    }

    public void addBirthday(Birthday birthday) {
        String sql = "INSERT INTO birthdays(name, birth_date, chat_id) VALUES(?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, birthday.getName());
            pstmt.setDate(2, Date.valueOf(birthday.getBirthDate()));
            pstmt.setString(3, String.valueOf(birthday.getChatId()));
            pstmt.executeUpdate();
            System.out.println("День рождение добавлен.");
        } catch (SQLException e) {
            System.err.println("Ошибка добавления:");
            e.printStackTrace();
        }
    }

    public void removeBirthday(int id){
        String sql = "DELETE FROM birthdays WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

            System.out.println("День рождение" + id + " удален.");

        } catch (SQLException e) {
            System.err.println("Ошибка удаления дня рождения " + id + ":");
            e.printStackTrace();
        }
    }

    public List<Birthday> getAllBirthdays(String chatId) {
        String sql = "SELECT id, name, birth_date FROM birthdays WHERE chat_id = ?";
        List<Birthday> birthdays = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setString(1, String.valueOf(chatId));
             ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                java.sql.Date birthDateSql = rs.getDate("birth_date");

                LocalDate birthDate = birthDateSql.toLocalDate();

                birthdays.add(new Birthday(id, name, birthDate, chatId));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения списка дней рождений:");
            e.printStackTrace();
        }
        return birthdays;
    }

    public void addGift(String name, int personId, String chatId) {
        String sql = "INSERT INTO gifts(name_gift, person_id, chat_id) VALUES(?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, personId);
            pstmt.setString(3, chatId);
            pstmt.executeUpdate();
            System.out.println("Подарок добавлен.");
        } catch (SQLException e) {
            System.err.println("Ошибка добавления:");
            e.printStackTrace();
        }
    }

    public void removeGift(String giftName, int personId, String chatId) {
        String sql = "DELETE FROM gifts WHERE id = (SELECT id FROM gifts WHERE name_gift = ? AND person_id = ? AND chat_id = ? LIMIT 1)" ;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, giftName);
            pstmt.setInt(2, personId);
            pstmt.setString(3, chatId);
            pstmt.executeUpdate();

            System.out.println("Подарок удален.");

        } catch (SQLException e) {
            System.err.println("Ошибка удаления подарка :");
            e.printStackTrace();
        }
    }

    public List<Gift> getGiftsForPerson(int personId, String chatId) {
        String sql = "SELECT name_gift FROM gifts WHERE person_id = ? AND chat_id = ?";
        List<Gift> gifts = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setInt(1, personId);
             pstmt.setString(2, chatId);
             ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String giftName = rs.getString("name_gift");
                gifts.add(new Gift(giftName, personId, chatId));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения списка подарков:");
            e.printStackTrace();
        }
        return gifts;
    }

    public List<Birthday> getBirthdaysForNextMonth(String chatId) {
        List<Birthday> birthdays = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate nextMonth = now.plusMonths(1);
        LocalDate startOfNextMonth = nextMonth.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfNextMonth = nextMonth.with(TemporalAdjusters.lastDayOfMonth());

        String sql = "SELECT name, birth_date FROM birthdays WHERE chat_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, chatId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                LocalDate birthDate = rs.getDate("birth_date").toLocalDate();
                LocalDate thisYearBirthday = birthDate.withYear(now.getYear());
                if (!thisYearBirthday.isBefore(startOfNextMonth) && !thisYearBirthday.isAfter(endOfNextMonth)) {
                    birthdays.add(new Birthday(name, birthDate, chatId));
                }
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получени ядней рождений на следующий месяц!");
            e.printStackTrace();
        }
        return birthdays;
    }

    public void addReminder(Reminder reminder, String chatId) {
        String sql = "INSERT INTO reminders(days_before, time, theme, chat_id) VALUES(?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reminder.getDaysBefore());
            pstmt.setTime(2, Time.valueOf(reminder.getTime()));
            pstmt.setString(3, reminder.getTheme());
            pstmt.setString(4, chatId);
            pstmt.executeUpdate();
            System.out.println("Уведомление добавлено.");
        } catch (SQLException e) {
            System.err.println("Ошибка добавления уведомления:");
            e.printStackTrace();
        }
    }

    public void removeReminder(int reminderId){
        String sql = "DELETE FROM reminders WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reminderId);
            pstmt.executeUpdate();

            System.out.println("Уведомление " + reminderId + " удалено.");

        } catch (SQLException e) {
            System.err.println("Ошибка удаления уведомления " + reminderId + ":");
            e.printStackTrace();
        }
    }

    public List<Reminder> getAllReminders(String chatId) {
        String sql = "SELECT id, days_before, time, theme FROM reminders WHERE chat_id = ?";
        List<Reminder> reminders = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setString(1, chatId);
                 ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                int daysBefore = rs.getInt("days_before");
                LocalTime time = rs.getTime("time").toLocalTime();
                String theme = rs.getString("theme");
                reminders.add(new Reminder(id, daysBefore, time, theme, chatId));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения списка уведомлений:");
            e.printStackTrace();
        }
        return reminders;
    }



}
