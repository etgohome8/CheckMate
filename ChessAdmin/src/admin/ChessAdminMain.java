package admin;
	
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import chess.database.Common;
import chess.database.ConnUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class ChessAdminMain extends Application {
	@Override
	public void start(Stage stage) throws IOException{
		try {
			Rectangle2D screenBounds = Screen.getPrimary().getBounds();
			 
			Parent root = FXMLLoader.load(getClass().getResource("/gui/AdminLogin.fxml"));
			Scene scene = new Scene(root);
			stage.setTitle("체크마테");
			
			stage.getIcons().add(new Image(getClass().getResource("/image/Chess_Icon.png").toURI().toString()));
			stage.setScene(scene);
			stage.setResizable(false);
			stage.setX(screenBounds.getMaxX()/2 -160);
			stage.setY(screenBounds.getMaxY()/2 -200);
			stage.show();		
			
			//이거 안하면 이상하게 창을 닫아도 서버가 계속 돌아감...
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			    @Override
			    public void handle(WindowEvent t) {
			    	removeFromOnlineuser();
			    	removeFromRoom1();
			    	removeFromRoom2();
			    	removeFromRoom3();
			        Platform.exit();
			        System.exit(0);
			    }
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	//////////// onlineuser room1 room2 room3에서 다 삭제
	public void removeFromOnlineuser() {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql1 = "";
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			//onlineuser에서
			sql1 = "delete from onlineuser";
			pstmt1 = conn.prepareStatement(sql1);
			rs = pstmt1.executeQuery();
			
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
		}finally {// 6단계
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e) {}
		}
	}
	public void removeFromRoom1() {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql1 = "";
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			//room1에서
			sql1 = "delete from room1";
			pstmt1 = conn.prepareStatement(sql1);
			rs = pstmt1.executeQuery();
			
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
		}finally {// 6단계
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e) {}
		}
	}
	public void removeFromRoom2() {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql1 = "";
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			//room1에서
			sql1 = "delete from room2 ";
			pstmt1 = conn.prepareStatement(sql1);
			rs = pstmt1.executeQuery();
			
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
		}finally {// 6단계
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e) {}
		}
	}
	public void removeFromRoom3() {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql1 = "";
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			//room1에서
			sql1 = "delete from room3 ";
			pstmt1 = conn.prepareStatement(sql1);
			rs = pstmt1.executeQuery();
			
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
		}finally {// 6단계
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e) {}
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
