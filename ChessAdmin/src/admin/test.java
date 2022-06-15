package admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import chess.database.ConnUtil;

public class test {

	public static void main(String[] args) {
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
				char[] date = dates.toCharArray();
				String datte = new String(date,0,10);
				System.out.println(datte);
			}
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
		}finally {// 6단계
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e) {}
		}
	}

}
