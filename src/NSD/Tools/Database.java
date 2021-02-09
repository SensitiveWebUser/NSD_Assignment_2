package NSD.Tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    Connection conn = null;

    public Database() {

        try {

            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:..\\NSD_Assigment_2_DB.sqlite";

            conn = DriverManager.getConnection(url);

            if (conn != null) {
                System.out.println("[Server] Successfully connected to SQLite database");
            }

        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("[Server] An error occurred while connecting SQLite database");
            ex.printStackTrace();
        }

    }

}
