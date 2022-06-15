package chess.database;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Common {
	static String id;
	static String pw;
	static String gender;
	static String nick;
	static int game, win, lose;
	static String imagePath = "";
	static byte[] data;
	static File im;
	static boolean isOn = false;
	public static boolean isOn() {
		return isOn;
	}

	public static void setOn(boolean isOn) {
		Common.isOn = isOn;
	}

	static String room1white = "";
	static String room1black = "";
	static String room2white = "";
	static String room2black = "";
	static String room3white = "";
	static String room3black = "";
	static int room1 = 0;
	static int room2 = 0;
	static int room3 = 0;
	static String myColor;
	static String oppoId="";
	static double rate = 0;
	
	

	public static double getRate() {
try {
			
			Connection conn = null;
			Statement stmt1 = null;
			String sql2 = null;
			ResultSet rs = null;

			try {// 정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				//들어온 메세지 구분 SR, WW, RE
				sql2 = "select * from chess_user where id = "+"'"+Common.getId()+"'";
				stmt1 = conn.createStatement();
				rs = stmt1.executeQuery(sql2);
				if(rs.next()) {
					rate = rs.getDouble("rate");
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
			} finally {// 6단계
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (Exception e) {}
				try {
					if (stmt1 != null) {
						stmt1.close();
					}
				} catch (Exception e) {}
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (Exception e) {}
			}
		} catch (Exception e) {}
		return rate;
	}

	public static void setRate(double rate) {
		Common.rate = rate;
	}

	public static String getOppoId() {
		return oppoId;
	}

	public static void setOppoId(String oppoId) {
		Common.oppoId = oppoId;
	}

	public static String getMyColor() {
		return myColor;
	}

	public static void setMyColor(String myColor) {
		Common.myColor = myColor;
	}

	public static int getRoom1() {
		return room1;
	}

	public static void setRoom1(int room1) {
		Common.room1 = room1;
	}

	public static int getRoom2() {
		return room2;
	}

	public static void setRoom2(int room2) {
		Common.room2 = room2;
	}

	public static int getRoom3() {
		return room3;
	}

	public static void setRoom3(int room3) {
		Common.room3 = room3;
	}

	public static String getimagePath() {
		return imagePath;
	}

	public static void setimagePath(String imagePath) {
		Common.imagePath = imagePath;
	}

	public static byte[] getData() {
		return data;
	}

	public static void setData(byte[] data) {
		Common.data = data;
	}

	public static void update(String id, String pw, String gender, String nick, int game, int win, int lose, File im) {
		Common.id = id;
		Common.pw = pw;
		Common.gender = gender;
		Common.nick = nick;
		Common.game = game;
		Common.win = win;
		Common.lose = lose;
		Common.im = im;
	}

	public static File getIm() {
		return im;
	}

	public static void setIm(File im) {
		Common.im = im;
	}

	public static String getPw() {
		return pw;
	}

	public static void setPw(String pw) {
		Common.pw = pw;
	}

	public static String getGender() {
		return gender;
	}

	public static void setGender(String gender) {
		Common.gender = gender;
	}

	public static String getNick() {
		return nick;
	}

	public static void setNick(String nick) {
		Common.nick = nick;
	}

	public static int getGame() {
		try {
			
			Connection conn = null;
			Statement stmt1 = null;
			String sql2 = null;
			ResultSet rs = null;

			try {// 정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				//들어온 메세지 구분 SR, WW, RE
				sql2 = "select * from chess_user where id = "+"'"+Common.getId()+"'";
				stmt1 = conn.createStatement();
				rs = stmt1.executeQuery(sql2);
				if(rs.next()) {
					game = rs.getInt("game");
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
			} finally {// 6단계
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (Exception e) {}
				try {
					if (stmt1 != null) {
						stmt1.close();
					}
				} catch (Exception e) {}
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (Exception e) {}
			}
		} catch (Exception e) {}
		return game;
	}

	public static void setGame(int game) {
		Common.game = game;
	}

	public static int getWin() {
		try {
			
			Connection conn = null;
			Statement stmt1 = null;
			String sql2 = null;
			ResultSet rs = null;

			try {// 정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				//들어온 메세지 구분 SR, WW, RE
				sql2 = "select * from chess_user where id = "+"'"+Common.getId()+"'";
				stmt1 = conn.createStatement();
				rs = stmt1.executeQuery(sql2);
				if(rs.next()) {
					win = rs.getInt("win");
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
			} finally {// 6단계
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (Exception e) {}
				try {
					if (stmt1 != null) {
						stmt1.close();
					}
				} catch (Exception e) {}
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (Exception e) {}
			}
		} catch (Exception e) {}
	
		
		
		
		return win;
	}

	public static void setWin(int win) {
		Common.win = win;
	}

	public static int getLose() {
		try {
			
			Connection conn = null;
			Statement stmt1 = null;
			String sql2 = null;
			ResultSet rs = null;

			try {// 정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				//들어온 메세지 구분 SR, WW, RE
				sql2 = "select * from chess_user where id = "+"'"+Common.getId()+"'";
				stmt1 = conn.createStatement();
				rs = stmt1.executeQuery(sql2);
				if(rs.next()) {
					lose = rs.getInt("lose");
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
			} finally {// 6단계
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (Exception e) {}
				try {
					if (stmt1 != null) {
						stmt1.close();
					}
				} catch (Exception e) {}
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (Exception e) {}
			}
		} catch (Exception e) {}
		return lose;
	}

	public static void setLose(int lose) {
		Common.lose = lose;
	}

	public static String getId() {
		return id;
	}

	public static void setId(String id) {
		Common.id = id;
	}

	public static String getRoom1white() {
		return room1white;
	}

	public static void setRoom1white(String room1white) {
		Common.room1white = room1white;
	}

	public static String getRoom1black() {
		return room1black;
	}

	public static void setRoom1black(String room1black) {
		Common.room1black = room1black;
	}

	public static String getRoom2white() {
		return room2white;
	}

	public static void setRoom2white(String room2white) {
		Common.room2white = room2white;
	}

	public static String getRoom2black() {
		return room2black;
	}

	public static void setRoom2black(String room2black) {
		Common.room2black = room2black;
	}

	public static String getRoom3white() {
		return room3white;
	}

	public static void setRoom3white(String room3white) {
		Common.room3white = room3white;
	}

	public static String getRoom3black() {
		return room3black;
	}

	public static void setRoom3black(String room3black) {
		Common.room3black = room3black;
	}

}
