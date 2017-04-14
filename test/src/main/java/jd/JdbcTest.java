package jd;

import java.sql.*;

/**
 * Created by liangkuai on 2017/4/11.
 */

public class JdbcTest {

    public static void main(String[] args) {
        Connection con;
        PreparedStatement pre;
        ResultSet result;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String url = "jdbc:sqlserver://localhost:1433;DatabaseName=jxgl";
        String user = "sa";
        String pwd = "sa";

        try {
            con = DriverManager.getConnection(url, user, pwd);
            pre = con.prepareStatement("SELECT * FROM 学生");
            result = pre.executeQuery();

            while (result.next()) {
                int stuNO = result.getInt(1);
                int classNO = result.getInt(2);
                String stuNum = result.getString(3);
                String stuName = result.getString(4);
                String stuSex = result.getString(5);
                String other = result.getString(6);
                System.out.println(stuNO + ", " + classNO + ", " + stuNum
                        + ", " + stuName + ", " + stuSex + ", " + other);
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
