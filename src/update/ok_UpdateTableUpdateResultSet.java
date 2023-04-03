package update;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.*;

public class ok_UpdateTableUpdateResultSet {

    private final static String class_name = "com.mysql.cj.jdbc.Driver";
    private final static String data_url = "jdbc:mysql://localhost:3307/itc_5201";
    private final static String userName = "root";
    private final static String pwd = "";

    private static Connection conn = null;
    private static ResultSet rset_check = null;
    private static Savepoint save1 = null;

    // Self-define exception class (throw only 2 options are allowed)
    private static class InvalidOptionException extends Exception {
        public InvalidOptionException() {
        }
    }

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

    public static void main(String[] args) throws SQLException, InvalidOptionException, InvalidCountryNameException {
        String para2a = "";
        float para2b = 0.0f;

        try {
            // Set up
            createConnection(class_name, data_url, userName, pwd);
            conn.setAutoCommit(false);
            save1 = conn.setSavepoint("save1");
            System.out.print("\nMySQL Database connected.\n");

            // Insert Data
            Scanner inputReader = new Scanner(System.in);
            System.out.println("Please Enter Column to be changed: (COUNTRY_NAME / LIFE_EXPECTANCY) ");
            String para1 = inputReader.next();
            if (!"COUNTRY_NAME".equals(para1) && !"LIFE_EXPECTANCY".equals(para1)) {
                throw new InvalidOptionException();
            }

            if (para1.equals("COUNTRY_NAME")) {
                System.out.println("New Country Name?");
                para2a = inputReader.next();
                if (!para2a.matches("^[a-zA-Z]+$")) {
                    throw new InvalidCountryNameException();
                }
            } else {
                System.out.println("New Life Expectancy?");
                para2b = inputReader.nextFloat();
            }

            System.out.println("Which CountryID you wish to change? ");
            int para3 = inputReader.nextInt();
            inputReader.close();

            // Use result set to update
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery("SELECT * FROM country");
            int found_id = 1;

            rs.beforeFirst();
            while (rs.next()) {
                int countryId = rs.getInt(1);
                if (countryId == para3) {
                    break;
                }
                found_id++;
            }

            // update row
            rs.absolute(found_id);
            if (para1.equals("COUNTRY_NAME")) {
                rs.updateString(2, para2a);
            } else {
                rs.updateFloat(3, para2b);
            }
            rs.updateRow();
            rs.beforeFirst();
            conn.commit();

            // Check
            String sqlStat_check = "SELECT * FROM country " +
                    "WHERE country_id = " +
                    para3;
            PreparedStatement pstmt_check = conn.prepareStatement(sqlStat_check);
            rset_check = pstmt_check.executeQuery();
            if (rset_check.next()) {
                System.out.println("Values Updated: ");
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
        } catch (InvalidOptionException ioe) {
            conn.rollback(save1);
            System.out.println(
                    String.format(
                            "\nInput Error! Please modify your input (Only (COUNTRY_NAME / LIFE_EXPECTANCY) is allowed)!"));
        } catch (InvalidCountryNameException ioe) {
            conn.rollback(save1);
            System.out.println(
                    String.format("\nInput Error! Please modify your input (Only letter is allowed)!"));
        } finally {
            CloseConnection();
        }
        System.out.println("Program Exited.");
    }
}
