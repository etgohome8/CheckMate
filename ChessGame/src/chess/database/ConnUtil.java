package chess.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnUtil {
	
	static {
		try {// �ε� ���� 2�ܰ�
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("�ε�����");
		} catch (Exception e) {
			System.err.println("����̹� �ε� ����");
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
