package org.example;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.sql.DriverManager;
import java.sql.SQLException;
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
        createTable();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS birthdays (\n"
                + "id SERIAL PRIMARY KEY,\n"
                + "name VARCHAR(255) NOT NULL,\n"
                + "birth_date DATE NOT NULL\n"
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
        String sql = "INSERT INTO birthdays(name, birth_date) VALUES(?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, birthday.getName());
            pstmt.setDate(2, Date.valueOf(birthday.getBirthDate()));
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

    public List<Birthday> getAllBirthdays() {
        String sql = "SELECT id, name, birth_date FROM birthdays";
        List<Birthday> birthdays = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                java.sql.Date birthDateSql = rs.getDate("birth_date");

                LocalDate birthDate = birthDateSql.toLocalDate();

                birthdays.add(new Birthday(id, name, birthDate));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка получения списка дней рождений:");
            e.printStackTrace();
        }
        return birthdays;
    }

}
