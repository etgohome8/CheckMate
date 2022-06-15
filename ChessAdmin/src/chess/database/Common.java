package chess.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import observableList.ChatLog;

public class Common {
	static int onlineNum;
	static String date;
	static boolean serverOn;
	static String detailUser;
	static String detailAdmin="";
	static String adminGrant="";
	static String detailGame="";
	static int next;
	
	public static String getNext() {
		return Integer.toString(next);
	}
	public static void setNext(int next) {
		Common.next = next;
	}
	public static String getDetailGame() {
		return detailGame;
	}
	public static void setDetailGame(String detailGame) {
		Common.detailGame = detailGame;
	}
	public static String getAdminGrant() {
		return adminGrant;
	}
	public static void setAdminGrant(String adminGrant) {
		Common.adminGrant = adminGrant;
	}
	public static String getDetailAdmin() {
		return detailAdmin;
	}
	public static void setDetailAdmin(String detailAdmin) {
		Common.detailAdmin = detailAdmin;
	}
	public static String getDetailUser() {
		return detailUser;
	}
	public static void setDetailUser(String detailUser) {
		Common.detailUser = detailUser;
	}
	public static int getOnlineNum() {
		return onlineNum;
	}
	public static void setOnlineNum(int onlineNum) {
		Common.onlineNum = onlineNum;
	}
	public static String getDate() {
		return date;
	}
	public static void setDate(String date) {
		Common.date = date;
	}
	public static boolean isServerOn() {
		return serverOn;
	}
	public static void setServerOn(boolean serverOn) {
		Common.serverOn = serverOn;
	}
	public static int getNum() {
		return onlineNum;
	}
	public static void setNum(int num) {
		onlineNum = num;
	}
	public static String getToday() {
		Connection conn = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql1 = "";
		
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			
			sql1 = "select sysdate from dual";
			pstmt1 = conn.prepareStatement(sql1);
			rs = pstmt1.executeQuery();
			if(rs.next()) {
				String dates = rs.getString(1);
				char[] datee = dates.toCharArray();
				date = new String(datee,0,10);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
		}finally {// 6단계
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e) {}
		}
		return date;
	}
	
}
