package com.aliemre2023;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class mainTest {

    private Connection conn_storage;
    private Connection conn_customers;
    public ResultSet customer_info;
    public int[] storage = {0, 0, 0, 0, 0, 0};

    @BeforeEach
    public void setUp() throws Exception {
        Class.forName("org.sqlite.JDBC");
        String dbUrl = "jdbc:sqlite:src/test/resources/test_storage.db";
        conn_storage = DriverManager.getConnection(dbUrl);
        Statement statement = conn_storage.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS storage (five INTEGER, ten INTEGER, twenty INTEGER, fifty INTEGER, hundred INTEGER, twohundred INTEGER)");
        statement.executeUpdate("DELETE FROM storage");
        statement.executeUpdate("INSERT INTO storage VALUES (10, 10, 10, 10, 10, 10)");

        String dbUrl2 = "jdbc:sqlite:src/test/resources/test_customers.db";
        conn_customers = DriverManager.getConnection(dbUrl2);
        Statement statement2 = conn_customers.createStatement();
        // mail column gives error why?
        statement2.executeUpdate("CREATE TABLE IF NOT EXISTS customers (id INTEGER, name TEXT, phone TEXT, money DOUBLE)");
        statement2.executeUpdate("DELETE FROM customers");
        statement2.executeUpdate("""
            INSERT INTO customers (id, name, phone, money)
            VALUES (1, 'test user', '+00 0000000000', 100.0)
        """);

        String sqlQuery_forAccount = String.format("SELECT * FROM customers WHERE id = %d", 1);
        customer_info = statement2.executeQuery(sqlQuery_forAccount);
        
        String sqlQuery_forStorageInfo = "SELECT * FROM storage LIMIT 1";
        ResultSet storage_info = statement.executeQuery(sqlQuery_forStorageInfo);
        storage[0] = storage_info.getInt("five");
        storage[1] = storage_info.getInt("ten");
        storage[2] = storage_info.getInt("twenty");
        storage[3] = storage_info.getInt("fifty");
        storage[4] = storage_info.getInt("hundred");
        storage[5] = storage_info.getInt("twohundred");

    }

    @AfterEach
    public void tearDown() throws Exception {
        if(conn_customers != null) conn_customers.close();
        if(conn_storage != null) conn_storage.close();
    }

    @Test
    public void test_PapelCalculater() throws Exception{
        double amount = 386;

        int[] expectedDistribution = {1, 1, 1, 1, 1, 1};
        int[] result = Main.papel_calculater(amount);

        assertArrayEquals(expectedDistribution, result, "Distribution is incorrect.");
    }

    @Test
    public void test_StorageStatue() throws Exception{
        double amount = 999;
        int[] distribution = Main.papel_calculater(amount);

        for(int i = 0; i < storage.length; i++){
            storage[i] -= distribution[i];
        }

        int[] result = {9, 10, 8, 9, 9, 6};
        assertArrayEquals(result, storage, "Storage is not updated properly.");
    }


    @Test
    public void test_CustomerInfoAfterDeposit() throws Exception {
        int id = 1;

        double money = customer_info.getDouble("money");
        double depositedAmount = 200.00;

        double expectedAmount = money + depositedAmount;

        Main.papel_calculater(depositedAmount);

        Statement statement = conn_customers.createStatement();
        String sqlQuery = String.format("UPDATE customers SET money=%.2f WHERE id=%d", expectedAmount, id);
        statement.executeUpdate(sqlQuery);

        String sqlQueryForCheckingBalance = String.format("SELECT money FROM customers WHERE id=%d", id);
        double actualBalance = statement.executeQuery(sqlQueryForCheckingBalance).getDouble("money");

        assertEquals(expectedAmount, actualBalance, 0.01, "Customer balance was not updated correctly after deposit.");
    }

    @Test
    public void test_CustomerInfoAfterWithdraw() throws Exception {
        int id = 1;
        double money = customer_info.getDouble("money");

        double withdrawedAmount = 50.00;

        double expectedAmount = money + withdrawedAmount;

        Main.papel_calculater(withdrawedAmount);

        Statement statement = conn_customers.createStatement();
        String sqlQuery = String.format("UPDATE customers SET money=%.2f WHERE id=%d", expectedAmount, id);
        statement.executeUpdate(sqlQuery);

        String sqlQueryForCheckingBalance = String.format("SELECT money FROM customers WHERE id=%d", id);
        double actualBalance = statement.executeQuery(sqlQueryForCheckingBalance).getDouble("money");

        assertEquals(expectedAmount, actualBalance, 0.01, "Customer balance was not updated correctly after withdraw.");
    }
}
