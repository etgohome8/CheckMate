package admin;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

public class AdminLoginController {
	private String id,pw;
	static boolean isCorrect = false;
	static boolean isPermit = false;
	private Parent root;
	private Stage stage;
	private Scene scene;
	
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

    public void DBCheck() {
		Connection conn = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql1 = "";

		id = idText.getText();
		pw = pwText.getText();
		
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			
			sql1 = "select * from chess_admin where id = ? and password = ?";
			pstmt1 = conn.prepareStatement(sql1);
			pstmt1.setString(1, id);
			pstmt1.setString(2, pw);
			rs = pstmt1.executeQuery();
			
			if (rs.next()) { // 일치하는게 있다면?
				isCorrect=true;
				if(rs.getString("permission").equals("y")) {
					isPermit = true;
				}else {
					isPermit = false;
				}
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
    
    @FXML
    public void adminRegis(ActionEvent event) {
		try {
			FXMLLoader lobbyLoader = new FXMLLoader(getClass().getResource("/gui/AdminSignUp.fxml"));
			root = lobbyLoader.load();
			stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
			
		} catch(Exception e) {
			System.out.println(e);
		}
    }

    @FXML
    public void logIn(ActionEvent event) throws URISyntaxException {
    	DBCheck();
    	if(isCorrect && isPermit) {
    		statusLabel.setText("로그인성공");
    		try {
    			
    			Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    			//로비 회원정보에 정보 전송
    			FXMLLoader lobbyLoader = new FXMLLoader(getClass().getResource("/gui/AdminLobby.cServer.fxml"));
				root = lobbyLoader.load();
				
				
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
    	}else if(isCorrect && !isPermit){
    		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    		Dialog<String> permissionDialog;	
    		permissionDialog = new Dialog<String>();
    		permissionDialog.setTitle("No Permission");
    		Stage stage2 = (Stage)permissionDialog.getDialogPane().getScene().getWindow();
    		stage2.getIcons().add(new Image(getClass().getResource("/image/Chess_Icon_cut.png").toURI().toString()));
    		ButtonType aboutButton = new ButtonType("확인",ButtonData.OK_DONE);
    		permissionDialog.setContentText
    				("승인되지 않은 관리자 계정입니다.\n"
    						+ "승인 후 로그인 바랍니다.");
    		permissionDialog.getDialogPane().getButtonTypes().add(aboutButton);
    		permissionDialog.setResizable(false);
    		permissionDialog.setX(screenBounds.getMaxX()/2 - 180);
    		permissionDialog.setY(screenBounds.getMaxY()/2 - 100);
    		permissionDialog.showAndWait();
    		
    	}else{
    		statusLabel.setTextFill(Color.RED);
    		statusLabel.setText("아이디 및 비밀번호를 확인해주세요");
    	}
    	
    }
	
	
}
