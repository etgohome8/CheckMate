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
		
		try {//���� ���� ���� 4,5�ܰ�
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
			System.err.println("������ ������ Ȯ���� ���� ����� ����");
		}finally {// 6�ܰ�
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e) {}
		}
	}

}
