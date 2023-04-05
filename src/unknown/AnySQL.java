package unknown;
// Due to the IDE, the package cannot created as instruction.

import java.util.Scanner;
import java.sql.*;

/**
 * This is a class represents a console that accepts uer's accepts user’s input
 * of SQL command from the console and execute the command using JDBC API.
 * 
 * @author Wenhao Fang
 * @version V1.001
 */
public class AnySQL {

    // region Private members
    private final static String CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    private final static String DB_URL = "jdbc:mysql://localhost/dbjava";

    private static Connection conn = null;
    private static Statement stmnt = null;
    private static ResultSet resultSet = null;
    private static Scanner sc = null;
    // endregion

    /**
     * Creates a connection with the MySQL
     * 
     * @param class_name The name of mysql driver class
     * @param data_url   The url of MySQL database
     * @param userName   User Name
     * @param pwd        Password
     * @throws ClassNotFoundException Exception when cannot find the class
     * @throws SQLException           The SQL Error
     */
    private static void createConnection(String class_name, String data_url, String userName, String pwd)
            throws ClassNotFoundException, SQLException {
        Class.forName(class_name);
        conn = DriverManager.getConnection(data_url, userName, pwd);
    }

    /**
     * Closes connection, statement, and ResultSet
     */
    private static void CloseConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
            if (stmnt != null) {
                stmnt.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
            System.out.println("Connection Closed.\n");
        } catch (SQLException sql) {
            String.format("\nSQL Error!\tCause:\t%s.", sql.getMessage());
        }
    }

    /**
     * Closes scanner.
     */
    private static void CloseScanner() {
        if (sc != null) {
            sc.close();
        }
        System.out.println("Scanner Closed.");
    }

    /**
     * Executes the input SQL statement.
     * 
     * @param sql The input SQL statement.
     */
    private static void ExecuteSQL(String sql) {

        try {
            // get the return of the execute
            boolean executeResult = stmnt.execute(sql);

            System.out.println("\n--------SQL Executed--------\n");

            // if it is true, which means the first return is a ResultSet
            if (executeResult == true) {
                resultSet = stmnt.getResultSet();
                ResultSetMetaData rsMetaData = resultSet.getMetaData();
                int column_count = rsMetaData.getColumnCount();// get the nubmer of columns

                // builds heading
                String heading = "";
                for (int i = 1; i <= column_count; i++) {
                    heading = String.format("%s%s\t",
                            heading, rsMetaData.getColumnLabel(i));
                }
                System.out.println(heading);

                // build content
                int row_count = 0;
                while (resultSet.next()) {
                    String row = "";
                    for (int i = 1; i <= column_count; i++) {
                        row = String.format("%s%s\t\t",
                                row, resultSet.getString(i));
                    }
                    System.out.println(row);
                    row_count += 1;
                }

                // print summary
                System.out.println(String.format("\n%d row(s) in set", row_count));
            } else {
                int count = stmnt.getUpdateCount(); // get affected row
                System.out.println(String.format("Query OK, %d row affected", count));
            }

        } catch (SQLException ex) {
            System.out.println("\n--------SQL Error--------");
            System.out.println(String.format("SQL State:\t%s", ex.getSQLState()));
            System.out.println(String.format("Error Code:\t%d", ex.getErrorCode()));
            System.out.println(String.format("Error Message:\t%s", ex.getMessage()));
            // ex.printStackTrace();
        }
    }

    public static void main(String[] args) {

        // region Defines variables
        String userName, pwd, sql_str = "";

        boolean flag = true;

        sc = new Scanner(System.in);

        // endregion

        System.out.println("\nWelcome to MySQL Terminal\n");
        System.out.print("\nPlease input your username:\nmysql> ");
        userName = sc.nextLine();

        System.out.print("\nPlease input your username:\nmysql> ");
        pwd = sc.nextLine();

        try {
            // create MySQL connection
            createConnection(CLASS_NAME, DB_URL, userName, pwd);
            stmnt = conn.createStatement();

            System.out.print("\nMySQL Database connected.\n");

            // Using a do while loop to catch input SQL statements.
            do {
                System.out.print("\nInput SQL statement: (Input \"q\" to exit.)\nmysql> ");
                sql_str = sc.nextLine();

                if (sql_str.equals("q")) {
                    flag = false;
                } else {
                    ExecuteSQL(sql_str);
                }
            } while (flag);
            System.out.println("\nExiting Program...");
        } catch (ClassNotFoundException cnf) {
            // cnf.printStackTrace();
            System.out.println(
                    String.format("\nError: \tFail to create MySQL connection.\nCause:\t%s.", cnf.getMessage()));
        } catch (SQLException sql) {
            // sql.printStackTrace();
            System.out.println(
                    String.format("\nSQL Error!\nCause:\t%s.", sql.getMessage()));
        } finally {
            CloseScanner();
            CloseConnection();
        }

        System.out.println("Program Exited.");
    }
}
