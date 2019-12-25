package wk.doraemon.db;

import java.sql.*;

/**
 * Created by TF on 2018/11/20.
 */
public class PostGISUtil {

    private static String DRIVER = "org.postgresql.Driver";
    private static String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static String USER = "postgres";
    private static String PASSWD = "postgres";
    private static Connection CONN = null;

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {

        if(CONN!=null) {
            return CONN;
        }
        try {
            CONN = DriverManager.getConnection(URL,USER,PASSWD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return CONN;
    }

    public static void close(ResultSet rs, PreparedStatement ps) {
        try {
            if(rs!=null) {
                rs.close();
            }
            if(ps!=null) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
