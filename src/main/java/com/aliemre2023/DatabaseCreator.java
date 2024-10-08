// mvn exec:java -Dexec.mainClass="com.aliemre2023.DatabaseCreator"
// from root

package com.aliemre2023;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseCreator {
    static void createCustomerTable(String dbUrl, String tableName){
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");

            // String dbUrl = "jdbc:sqlite:src/main/resources/customers.db";
            connection = DriverManager.getConnection(dbUrl);

            if(connection != null){
                Statement statement = connection.createStatement();

                // SQL

                // EXECUTEUPDATE for INSERT UPDATE DELETE, CREATE TABLE
                String sql = String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        id INTEGER,
                        name TEXT,
                        mail TEXT,
                        phone TEXT,
                        money DOUBLE
                    )
                """, tableName);

                statement.executeUpdate(sql);
                /* 
                statement.executeUpdate("""
                    INSERT INTO customers (id, name, mail, phone, money)
                    VALUES (1234567893, 'Micheal Scott', 'mikelasc@gmail.com', '+90 5000000003', 1000.0)
                """);
                */

                System.out.println("Table created and data inserted.");


                // Show all content
                ResultSet resultSet = statement.executeQuery("SELECT * FROM customers");

                while(resultSet.next()){
                    System.out.println("ID: " + resultSet.getInt("id"));
                    System.out.println("Name: " + resultSet.getString("name"));
                    System.out.println("Mail: " + resultSet.getString("mail"));
                    System.out.println("Phone: " + resultSet.getString("phone"));
                    System.out.println("Money: " + resultSet.getDouble("money"));   
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    static void createStorageTable(String dbUrl, String cName){
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");

            // String dbUrl = "jdbc:sqlite:src/main/resources/storage.db";
            connection = DriverManager.getConnection(dbUrl);

            if(connection != null){
                Statement statement = connection.createStatement();

                //// Table for atm storage

                String sql = String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        five INTEGER,
                        ten INTEGER,
                        twenty INTEGER,
                        fifty INTEGER,
                        hundred INTEGER,
                        twohundred INTEGER
                    )
                """, cName);
                statement.executeUpdate(sql);
                
                /* 
                statement.executeUpdate("""
                    INSERT INTO storage
                    VALUES (1000, 1000, 1000, 1000, 1000, 1000)
                """);
                */
                

                ResultSet resultSet = statement.executeQuery("SELECT * FROM storage LIMIT 1");
                while(resultSet.next()){
                    System.out.println("5: " + resultSet.getInt("five"));
                    System.out.println("10: " + resultSet.getInt("ten"));
                    System.out.println("20: " + resultSet.getInt("twenty"));
                    System.out.println("50: " + resultSet.getInt("fifty"));
                    System.out.println("100: " + resultSet.getInt("hundred"));   
                    System.out.println("200: " + resultSet.getInt("twohundred"));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        String dbUrl = "jdbc:sqlite:src/main/resources/customers.db";
        String cName = "customers";
        createCustomerTable(dbUrl, cName);

        String dbUrl2 = "jdbc:sqlite:src/main/resources/storage.db";
        String sName = "storage";
        createStorageTable(dbUrl2, sName);

    }
}
