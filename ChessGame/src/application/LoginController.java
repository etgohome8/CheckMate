package application;

import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import chess.database.Common;
import chess.database.ConnUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class LoginController {

	private String id,pw;
	static boolean isCorrect = false;
	static boolean isOnline = false;
	static boolean isBanned = false;
	private Parent root;
	private Stage stage;
	private Scene scene;
	private Socket socket;
    @FXML
    private TextField idText;
    @FXML
    private Button loginButton;
    @FXML
    private PasswordField pwText;
    @FXML
    private Button signButton;
    @FXML
    private Label statusLabel;
   
    public void checkServer() {
    	try {
    		socket = new Socket("127.0.0.1",7777);
    		Common.setOn(true);
		} catch (Exception e) {
			Common.setOn(false);
			System.out.println("서버안열림");
		}
    }
  
    public void DBCheck() {
		Connection conn = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql1 = "";

		id = idText.getText();
		pw = pwText.getText();
		
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			
			sql1 = "select * from chess_user where id = ? and password = ?";
			pstmt1 = conn.prepareStatement(sql1);
			pstmt1.setString(1, id);
			pstmt1.setString(2, pw);
			rs = pstmt1.executeQuery();
			
			if (rs.next()) { // 일치하는게 있다면 정보 다 가져옴
				isCorrect=true;
				Common.setId(id);
				Common.setPw(pw);
				Common.setGender(rs.getString("gender"));
				Common.setNick(rs.getString("nick"));
				Common.setGame(rs.getInt("game"));
				Common.setWin(rs.getInt("win"));
				Common.setLose(rs.getInt("lose"));
				String bann = rs.getString("blacklist");
				byte[] data = rs.getBytes("userim");
				Common.setData(data);
				Common.setimagePath(rs.getString("image"));
				
				
				
				isBanned = bann.equals("f") ? false :true;
				
			}else {
				isCorrect=false;
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
    
    public void DBCheck2() {//이미 온라인인지 확인
		Connection conn = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql1 = "";

		id = idText.getText();
		pw = pwText.getText();
		
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			
			sql1 = "select * from onlineuser where id = ?";
			pstmt1 = conn.prepareStatement(sql1);
			pstmt1.setString(1, id);
			rs = pstmt1.executeQuery();
			
			if (rs.next()) { // 일치하는게 있다면?
				isOnline=true;
				
			}else {
				isOnline=false;
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
    
    public void logIn(ActionEvent event) {
    	DBCheck();
    	DBCheck2();
    	checkServer();
    	if(isCorrect && !isBanned && !isOnline&&Common.isOn()) {
    		statusLabel.setText("로그인성공");
    		Common.setId(idText.getText());
    		//onlineuser 테이블에 추가
    		Connection conn = null;
    		PreparedStatement pstmt1 = null;
    		ResultSet rs = null;
    		String sql1 = "";

    		id = idText.getText();
    		pw = pwText.getText();
    		//온라인 유저 목록에 추가
    		try {//정적 동적 쿼리 4,5단계
    			conn = ConnUtil.getConnection();
    			sql1 = "insert into onlineuser(id,nick) values(?,?)";
    			pstmt1 = conn.prepareStatement(sql1);
    			pstmt1.setString(1, id);
    			pstmt1.setString(2, Common.getNick());
    			rs = pstmt1.executeQuery();
    			
    		}catch (Exception e) {
    			e.printStackTrace();
    			System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
    		}finally {// 6단계
    			 try {if (rs != null) rs.close();	} catch (Exception e) {}
    			 try {if (conn != null) conn.close();	} catch (Exception e) {}
    			 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e) {}
    		}
    		
    		
    		try {
				if(socket !=null && !socket.isClosed()) {
					//String nick = nickField.getText();
					//type="exit";
					Thread t = new Thread() {
		    			public void run() {
		    				try {
								socket.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
		    			}
		    		};
		    		t.start();
		    	}
    			Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    			//로비 회원정보에 정보 전송
    			FXMLLoader lobbyLoader = new FXMLLoader(getClass().getResource("/gui/Lobby.fxml"));
				root = lobbyLoader.load();
				LobbyController lobby = lobbyLoader.getController();
				
    			stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    			scene = new Scene(root);
    			stage.setScene(scene);
    			stage.setResizable(false);
    			stage.setX(screenBounds.getMaxX()/2 -300);
    			stage.setY(screenBounds.getMaxY()/2 -200);
    			
    			stage.show();
    			
    		} catch(Exception e) {
    			System.out.println(e);
    		}
    	}else if(isCorrect && !isBanned && !isOnline) {
    		statusLabel.setText("로그인성공");
    		Common.setId(idText.getText());
    		//onlineuser 테이블에 추가는 하지 않음 
    		
    		
    		try {
				if(socket !=null && !socket.isClosed()) {
					//String nick = nickField.getText();
					//type="exit";
					Thread t = new Thread() {
		    			public void run() {
		    				try {
								socket.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
		    			}
		    		};
		    		t.start();
		    	}
    			Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    			//로비 회원정보에 정보 전송
    			FXMLLoader lobbyLoader = new FXMLLoader(getClass().getResource("/gui/Lobby.fxml"));
				root = lobbyLoader.load();
				LobbyController lobby = lobbyLoader.getController();
				
    			stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    			scene = new Scene(root);
    			stage.setScene(scene);
    			stage.setResizable(false);
    			stage.setX(screenBounds.getMaxX()/2 -300);
    			stage.setY(screenBounds.getMaxY()/2 -200);
    			
    			stage.show();
    			
    		} catch(Exception e) {
    			System.out.println(e);
    		}
    	}
    	
    	
    	else if(isCorrect && isBanned){//비번일치하는데 정지계정
    		try {
	    		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
	    		Dialog<String> permissionDialog;	
	    		permissionDialog = new Dialog<String>();
	    		permissionDialog.setTitle("No Permission");
	    		Stage stage2 = (Stage)permissionDialog.getDialogPane().getScene().getWindow();
	    		stage2.getIcons().add(new Image(getClass().getResource("/image/Chess_Icon_cut.png").toURI().toString()));
	    		ButtonType aboutButton = new ButtonType("확인",ButtonData.OK_DONE);
	    		permissionDialog.setContentText
	    				("정지 당한 계정입니다.\n"
	    						+ "새로 가입 후 로그인하세요");
	    		permissionDialog.getDialogPane().getButtonTypes().add(aboutButton);
	    		permissionDialog.setResizable(false);
	    		permissionDialog.setX(screenBounds.getMaxX()/2 - 180);
	    		permissionDialog.setY(screenBounds.getMaxY()/2 - 100);
	    		permissionDialog.showAndWait();
    		}catch (Exception e) {}
    	}else if(isCorrect && isOnline){//이미 로그인한 계정
    		try {
	    		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
	    		Dialog<String> permissionDialog;	
	    		permissionDialog = new Dialog<String>();
	    		permissionDialog.setTitle("중복 로그인");
	    		Stage stage2 = (Stage)permissionDialog.getDialogPane().getScene().getWindow();
	    		stage2.getIcons().add(new Image(getClass().getResource("/image/Chess_Icon_cut.png").toURI().toString()));
	    		ButtonType aboutButton = new ButtonType("확인",ButtonData.OK_DONE);
	    		permissionDialog.setContentText
	    				("이미 접속 중인 계정입니다.\n"
	    						+ "다른 아이디로 로그인하세요");
	    		permissionDialog.getDialogPane().getButtonTypes().add(aboutButton);
	    		permissionDialog.setResizable(false);
	    		permissionDialog.setX(screenBounds.getMaxX()/2 - 180);
	    		permissionDialog.setY(screenBounds.getMaxY()/2 - 100);
	    		permissionDialog.showAndWait();
    		}catch (Exception e) {}
    	}else{
    		statusLabel.setTextFill(Color.RED);
    		statusLabel.setText("아이디 및 비밀번호를 확인해주세요");
    	}
    	
    }
    
    public void switchtoSignup(ActionEvent event) {
		try {
			FXMLLoader lobbyLoader = new FXMLLoader(getClass().getResource("/gui/Signup.fxml"));
			root = lobbyLoader.load();
			stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
			
		} catch(Exception e) {
			System.out.println(e);
		}
    }

    
    
}
