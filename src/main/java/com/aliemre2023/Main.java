// mvn exec:java -Dexec.mainClass="com.aliemre2023.Main"
// from root

package com.aliemre2023;

import java.util.Calendar;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class Main{
    static int[] papel = {5, 10, 20, 50, 100, 200};
    static int[] papel_calculater(double amount) throws Exception {
        int[] storage = {0, 0, 0, 0, 0, 0}; 
        int[] best_division = {0, 0, 0, 0, 0, 0};

        Connection conn = null;
        Class.forName("org.sqlite.JDBC");
        String dbUrl = "jdbc:sqlite:src/main/resources/storage.db";
        conn = DriverManager.getConnection(dbUrl);
        Statement statement = conn.createStatement();
        String sqlQuery_forStorageInfo = "SELECT * FROM storage LIMIT 1";
        ResultSet storage_info = statement.executeQuery(sqlQuery_forStorageInfo);
        storage[0] = storage_info.getInt("five");
        storage[1] = storage_info.getInt("ten");
        storage[2] = storage_info.getInt("twenty");
        storage[3] = storage_info.getInt("fifty");
        storage[4] = storage_info.getInt("hundred");
        storage[5] = storage_info.getInt("twohundred");

        for(int i = storage.length - 1; i >= 0; i--){
            if(amount >= papel[i]) {
                best_division[i] = (int) (amount / papel[i]);
                amount -= best_division[i] * papel[i];
            }
        }

        int deficient = 0;
        for(int i = storage.length - 1; i >= 0; i--){
            if(deficient != 0){
                best_division[i] += deficient / papel[i];
                deficient = 0;
            }

            if(storage[i] >= best_division[i]){
                storage[i] -= best_division[i];
            }
            else{
                int temp = best_division[i] - storage[i];
                best_division[i] = storage[i];
                storage[i] = 0;
                deficient = temp * papel[i];
            }
        }

        String sqlQuery_forStorageUpdate = String.format("UPDATE storage SET five=%d, ten=%d, twenty=%d, fifty=%d, hundred=%d, twohundred=%d WHERE rowid = (SELECT rowid FROM storage LIMIT 1)", storage[0], storage[1], storage[2], storage[3], storage[4], storage[5]);
        statement.executeUpdate(sqlQuery_forStorageUpdate);

        return best_division;
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        // Database arranging for usage

        Connection conn = null;
        Class.forName("org.sqlite.JDBC"); // throws Exception need

        String dbUrl = "jdbc:sqlite:src/main/resources/customers.db";
        conn = DriverManager.getConnection(dbUrl);

        Statement statement = conn.createStatement();
               

        System.out.println("---Welcome to the ATM !!---");

        System.out.println("Please enter your ID");
        System.out.print("ID: ");
        int id = scanner.nextInt();
        boolean is_exist = false;

        String sqlQuery_forAccount = String.format("SELECT * FROM customers WHERE id = %d", id);
        ResultSet customer_info = statement.executeQuery(sqlQuery_forAccount);

        String customer_name = customer_info.getString("name");
        double customer_money = customer_info.getDouble("money");
        String customer_mail = customer_info.getString("mail");

        String receipt = "";
        if (customer_name != null) {
            is_exist = true;
            System.out.println("Have a nice day " + customer_name);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            receipt += "----------- RECEIPT ----------- \n";
            receipt += String.format("%s : %s \n", customer_name, timeStamp);
        }
        else {
            System.out.println("User not found");
        }
        
        while(is_exist){
            System.out.println("Select the operation");
            System.out.println("0_ Total Money");
            System.out.println("1_ Deposit money");
            System.out.println("2_ Withdraw money");
            System.out.println("3- Send money");
            System.out.println("-1_ Exit");

            System.out.print("Selection: ");
            int selection = scanner.nextInt();

            if(selection == 0){
                System.out.println(String.format("Current money: %.2f", customer_money));
            }
            else if(selection == 1){
                System.out.print("How much money do you deposit: ");
                Double money_deposit = scanner.nextDouble();
                int[] puted_papels = papel_calculater(money_deposit);
                int puted_money = 0;

                for(int i = 0; i < papel.length; i++){
                    puted_money += papel[i] * puted_papels[i];
                }
                customer_money += puted_money;

                String sqlQuery_forUpdateDeposit = String.format("UPDATE customers SET money=%.2f WHERE id=%d", customer_money, id);
                statement.executeUpdate(sqlQuery_forUpdateDeposit);

                String message = String.format("%d money is deposited.\n", puted_money);
                System.out.print(message);
                receipt += message;
            }
            else if(selection == 2){
                System.out.print("How much money do you withdraw: ");
                Double money_withdraw = scanner.nextDouble();
                if(customer_money >= money_withdraw){
                    int[] given_papels = papel_calculater(money_withdraw);
                    int given_money = 0;
                    for(int i = 0; i < papel.length; i++){
                        given_money += papel[i] * given_papels[i];
                    }
                    customer_money -= given_money;

                    String sqlQuery_forUpdateWithdraw = String.format("UPDATE customers SET money=%.2f WHERE id=%d", customer_money, id);
                    statement.executeUpdate(sqlQuery_forUpdateWithdraw);

                    String message = String.format("%d money is withdrawed.\n", given_money);
                    System.out.print(message);
                    receipt += message;
                }
                else{
                    System.out.println("Money in your account is not enough. You have " + customer_money);
                }
            }
            else if (selection == 3) {
                System.out.print("ID you want to send money: ");
                int taker_id = scanner.nextInt();

                String sqlQuery_forTakerAccount = String.format("SELECT * FROM customers WHERE id=%d", taker_id);
                ResultSet taker_info = statement.executeQuery(sqlQuery_forTakerAccount);

                String taker_name = taker_info.getString("name");
                Double taker_money = taker_info.getDouble("money");
                // String taker_mail = taker_info.getString("mail");

                if (taker_name != null) {
                    System.out.println("How much money do you want to send to " + taker_name);
                    System.out.print("Amount: ");
                    double amount = scanner.nextDouble();

                    if (customer_money >= amount) {
                        customer_money -= amount;
                        String sqlQuery_forTakerAccountMoneyUpdate = String.format("UPDATE customers SET money=%.2f WHERE id=%d", taker_money+amount, taker_id);
                        statement.executeUpdate(sqlQuery_forTakerAccountMoneyUpdate);
                        String sqlQuery_forSenderAccountMoneyUpdate = String.format("UPDATE customers SET money=%.2f WHERE id=%d", customer_money, id);
                        statement.executeUpdate(sqlQuery_forSenderAccountMoneyUpdate);

                        System.out.println(amount);
                        System.out.println(taker_name);

                        String message = String.format("%.0f money sended to %s.\n", amount, taker_name);
                        System.out.println("Transfer completed successfully.");
                        System.out.print(message);
                        receipt += message;

                        // notification for taker, (valid mail rrequired)
                        //
                    }
                    else {
                        System.out.println("Money in your account is not enough. You have " + customer_money);
                    }

                }
                else {
                    System.out.println("There is noone with id " + taker_id);
                }

            }
            else {
                System.out.println("Do you want receipt(1/0): ");
                int input = scanner.nextInt();
                if(input == 1) {
                    receipt += "-------------------------------\n";
                    //System.out.println(receipt);

                    Mail mail = new Mail();
                    String[] customer_mail_asArray = {customer_mail};
                    mail.run(customer_mail_asArray ,"Receipt", receipt);
                    
                    System.out.println("Your receipt sended to your mail.");    
                }
                System.out.println("System exit.");
                break;
            }
            System.out.println("----------------------------");
        }

    
        scanner.close();
        scanner = null; 
    }
}