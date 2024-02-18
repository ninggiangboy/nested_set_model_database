package dev.ngb;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Objects;

public class Main {

    private static final String CONNECTION_URL = "jdbc:postgresql://localhost:5432/address";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final ClassLoader classLoader = Main.class.getClassLoader();
    private static final String PATTERN_RGX = "\\uFEFF";
    
    private static final String SQL_INSERT_LEVEL_1 =
            "INSERT INTO public.addresses (address_id, address_name, level, left_index, right_index) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_INSERT = "SELECT insert_data(?, ?, ?, ?)";

    @SneakyThrows
    public static void main(String[] args) {
        Connection connection = connectToDatabase();
        insertCity(connection);
        insertDistrict(connection);
        insertWard(connection);
    }

    private static Connection connectToDatabase() {
        try {
            return DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            System.exit(0);
            return null;
        }
    }

    @SneakyThrows
    private static void insertCity(Connection connection) {
        File city = new File(Objects.requireNonNull(classLoader.getResource("city.csv")).getFile());
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(city))) {
            String line;
            while ((line = br.readLine()) != null) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_LEVEL_1)) {
                    String[] values = line.replaceAll(PATTERN_RGX, "").split(",");
                    preparedStatement.setString(1, values[0]);
                    preparedStatement.setString(2, values[1]);
                    preparedStatement.setInt(3, 1);
                    preparedStatement.setInt(4, count++);
                    preparedStatement.setInt(5, count++);
                    preparedStatement.execute();
                }
            }
            System.out.println("City has been inserted successfully");
        }
    }

    @SneakyThrows
    private static void insertDistrict(Connection connection) {
        File district = new File(Objects.requireNonNull(classLoader.getResource("district.csv")).getFile());
        try (BufferedReader br = new BufferedReader(new FileReader(district))) {
            String line;
            while ((line = br.readLine()) != null) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT)) {
                    String[] values = line.replaceAll(PATTERN_RGX, "").split(",");
                    preparedStatement.setString(1, values[2]);
                    preparedStatement.setString(2, values[0]);
                    preparedStatement.setString(3, values[1]);
                    preparedStatement.setInt(4, 2);
                    preparedStatement.execute();
                }
            }
            System.out.println("District has been inserted successfully");
        }
    }

    @SneakyThrows
    private static void insertWard(Connection connection) {
        File ward = new File(Objects.requireNonNull(classLoader.getResource("ward.csv")).getFile());
        try (BufferedReader br = new BufferedReader(new FileReader(ward))) {
            String line;
            while ((line = br.readLine()) != null) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT)) {
                    String[] values = line.replaceAll(PATTERN_RGX, "").split(",");
                    preparedStatement.setString(1, values[2]);
                    preparedStatement.setString(2, values[0]);
                    preparedStatement.setString(3, values[1]);
                    preparedStatement.setInt(4, 3);
                    preparedStatement.execute();
                }
            }
            System.out.println("Ward has been inserted successfully");
        } finally {
            connection.close();
        }
    }
}
