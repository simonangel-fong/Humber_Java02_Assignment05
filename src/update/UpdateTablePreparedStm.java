package update;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.*;

public class UpdateTablePreparedStm {

    private final static String class_name = "com.mysql.cj.jdbc.Driver";
    private final static String data_url = "jdbc:mysql://localhost:3307/itc_5201";
    private final static String userName = "root";
    private final static String pwd = "";

    private static Connection conn = null;
    private static ResultSet rset_check = null;
    private static Savepoint save1 = null;

    // Self-define exception class (throw only letter is allowed)
    private static class InvalidCountryNameException extends Exception {
        public InvalidCountryNameException() {
        }
    }

    // Create Connection
    private static void createConnection(String class_name, String data_url, String userName, String pwd)
            throws ClassNotFoundException, SQLException {
        Class.forName(class_name);
        conn = DriverManager.getConnection(data_url, userName, pwd);
    }

    // Close Connection
    private static void CloseConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
            if (rset_check != null) {
                rset_check.close();
            }
            System.out.println("Connection Closed.\n");
        } catch (SQLException sql) {
            String.format("\nSQL Error!\tCause:\t%s.", sql.getMessage());
        }
    }

    public static void main(String[] args) throws SQLException, InvalidCountryNameException {
        try {
            // Set up
            createConnection(class_name, data_url, userName, pwd);
            conn.setAutoCommit(false);
            save1 = conn.setSavepoint("save1");
            System.out.print("\nMySQL Database connected.\n");

            // create the prepared statement
            String sqlStat = "INSERT INTO country " +
                    "VALUES (" +
                    "? ,? ,?" +
                    ")";
            PreparedStatement pstmt = conn.prepareStatement(sqlStat);

            // Insert Data
            Scanner inputReader = new Scanner(System.in);
            System.out.println("Please Enter country information: ");
            System.out.println("Country ID?: ");
            int para1 = inputReader.nextInt();

            System.out.println("Country Name?: ");
            String para2 = inputReader.next();
            if (!para2.matches("^[a-zA-Z]+$")) {
                throw new InvalidCountryNameException();
            }

            System.out.println("Life Expectancy?: ");
            float para3 = inputReader.nextFloat();
            inputReader.close();

            // set the query parameters
            pstmt.setInt(1, para1);
            pstmt.setString(2, para2);
            pstmt.setFloat(3, para3);

            // Execute
            System.out.println("Insert Statment: " + pstmt + "\n");
            pstmt.executeUpdate();
            conn.commit();

            // Check
            String sqlStat_check = "SELECT * FROM country " +
                    "WHERE country_id = " +
                    para1;
            Statement pstmt_check = conn.createStatement();
            rset_check = pstmt_check.executeQuery(sqlStat_check);
            if (rset_check.next()) {
                System.out.println("Values Inserted: ");
                System.out.println("Country ID: " + rset_check.getInt(1) + "\n"
                        + "Country Name :" + rset_check.getString(2) + "\n"
                        + "Life Expectancy :" + rset_check.getFloat(3) + "\n");
            } else {
                System.out.println("No data found for country ID " + para1);
            }

            // Exit and catch exception
            System.out.println("\nExiting Program...");
        } catch (ClassNotFoundException cnf) {
            System.out.println(
                    String.format("\nError: \tFail to create MySQL connection.\nCause:\t%s.", cnf.getMessage()));
        } catch (SQLException sql) {
            System.out.println(
                    String.format("\nSQL Error!\nCause:\t%s.", sql.getMessage()));
        } catch (InputMismatchException ime) {
            conn.rollback(save1);
            System.out.println(
                    String.format("\nInput Error! Please modify your input (Only number is allowed)!"));
        } catch (InvalidCountryNameException icne) {
            conn.rollback(save1);
            System.out.println(
                    String.format("\nInput Error! Please modify your input (Only letter is allowed)!"));
        } finally {
            CloseConnection();
        }
        System.out.println("Program Exited.");
    }
}
