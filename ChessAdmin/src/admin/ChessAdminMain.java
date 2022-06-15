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
			stage.setTitle("üũ����");
			
			stage.getIcons().add(new Image(getClass().getResource("/image/Chess_Icon.png").toURI().toString()));
			stage.setScene(scene);
			stage.setResizable(false);
			stage.setX(screenBounds.getMaxX()/2 -160);
			stage.setY(screenBounds.getMaxY()/2 -200);
			stage.show();		
			
			//�̰� ���ϸ� �̻��ϰ� â�� �ݾƵ� ������ ��� ���ư�...
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
	//////////// onlineuser room1 room2 room3���� �� ����
	public void removeFromOnlineuser() {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql1 = "";
		try {//���� ���� ���� 4,5�ܰ�
			conn = ConnUtil.getConnection();
			//onlineuser����
			sql1 = "delete from onlineuser";
			pstmt1 = conn.prepareStatement(sql1);
			rs = pstmt1.executeQuery();
			
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("������ ������ Ȯ���� ���� ����� ����");
		}finally {// 6�ܰ�
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
		try {//���� ���� ���� 4,5�ܰ�
			conn = ConnUtil.getConnection();
			//room1����
			sql1 = "delete from room1";
			pstmt1 = conn.prepareStatement(sql1);
			rs = pstmt1.executeQuery();
			
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("������ ������ Ȯ���� ���� ����� ����");
		}finally {// 6�ܰ�
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
		try {//���� ���� ���� 4,5�ܰ�
			conn = ConnUtil.getConnection();
			//room1����
			sql1 = "delete from room2 ";
			pstmt1 = conn.prepareStatement(sql1);
			rs = pstmt1.executeQuery();
			
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("������ ������ Ȯ���� ���� ����� ����");
		}finally {// 6�ܰ�
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
		try {//���� ���� ���� 4,5�ܰ�
			conn = ConnUtil.getConnection();
			//room1����
			sql1 = "delete from room3 ";
			pstmt1 = conn.prepareStatement(sql1);
			rs = pstmt1.executeQuery();
			
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("������ ������ Ȯ���� ���� ����� ����");
		}finally {// 6�ܰ�
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e) {}
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
