package chess.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnUtil {
	
	static {
		try {// 로딩 성공 2단계
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("로딩성공");
		} catch (Exception e) {
			System.err.println("드라이버 로딩 실패");
			System.exit(0);
		}
		
	}
	
	public static Connection getConnection() throws SQLException{
		String url = "jdbc:oracle:thin:@localhost:1522/XEPDB1";
		final String dbId = "mytest";
		final String dbPw = "mytest";
		return DriverManager.getConnection(url, dbId, dbPw);
	}
	
}
