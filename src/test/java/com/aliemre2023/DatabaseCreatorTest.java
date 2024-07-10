package com.aliemre2023;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DatabaseCreatorTest {
    private Connection connection;

    @Before
    public void setUp() throws Exception {
        Class.forName("org.sqlite.JDBC");
    }

    @After
    public void tearDown() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void testCreateCustomersTable() throws Exception {
        String dbUrl = "jdbc:sqlite:src/test/resources/test_customers.db";
        connection = DriverManager.getConnection(dbUrl);
        String tableName = "test_customers";
        DatabaseCreator.createCustomerTable(dbUrl, tableName);

        Statement statement = connection.createStatement();

        statement.executeUpdate("""
            INSERT INTO test_customers (id, name, mail, phone, money)
            VALUES (1234567893, 'Micheal Scott', 'mikelasc@gmail.com', '+90 5000000003', 1000.0)
        """);

        ResultSet resultSet = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'");
        assertNotNull(resultSet);
        assertEquals("Expected '" + tableName + "' table to be created", true, resultSet.next());

        // Inserted data verification
        resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE id=1234567893");
        assertNotNull(resultSet);
        assertEquals("Expected a record in the result set", true, resultSet.next());
        assertEquals("Name has some problem", "Micheal Scott", resultSet.getString("name"));
        assertEquals("Mail has some problem", "mikelasc@gmail.com", resultSet.getString("mail"));
        assertEquals("Phone has some problem", "+90 5000000003", resultSet.getString("phone"));
        assertEquals("Money has some problem", 1000.0, resultSet.getDouble("money"), 0.001);
    }

    @Test
    public void testCreateStorageTable() throws Exception {
        String dbUrl = "jdbc:sqlite:src/test/resources/test_storage.db";
        connection = DriverManager.getConnection(dbUrl);
        String tableName = "test_storage";
        DatabaseCreator.createStorageTable(dbUrl, tableName);

        Statement statement = connection.createStatement();

        statement.executeUpdate("""
            INSERT INTO test_storage
            VALUES (1000, 1000, 1000, 1000, 1000, 1000)
        """);

        ResultSet resultSet = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'");
        assertNotNull(resultSet);
        assertEquals("Expected '" + tableName + "' table to be created", true, resultSet.next());

        // Inserted data verification
        resultSet = statement.executeQuery("SELECT * FROM " + tableName + " LIMIT 1");
        assertNotNull(resultSet);
        assertEquals("Expected a record in the result set", true, resultSet.next());
        assertEquals("Five has some problem", 1000, resultSet.getInt("five"));
        assertEquals("Ten has some problem", 1000, resultSet.getInt("ten"));
        assertEquals("Twenty has some problem", 1000, resultSet.getInt("twenty"));
        assertEquals("Fifty has some problem", 1000, resultSet.getInt("fifty"));
        assertEquals("Hundred has some problem", 1000, resultSet.getInt("hundred"));
        assertEquals("Two hundred has some problem", 1000, resultSet.getInt("twohundred"));
    }
}
