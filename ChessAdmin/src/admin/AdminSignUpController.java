package admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import chess.database.ConnUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AdminSignUpController {
	private Parent root;
	private Stage stage;
	private Scene scene;

	private String id;
	private String pw;
	private String check;
	private String gender;
	
	static int idW = -1; // 썼으면 1 아니면 -1
	static int idC = -1; // 확인했으면 2 사용가능이면 1 중복이면 0 안했으면 -1
	static int pwW = -1; // 썼으면 1 아니면 -1
	static int pwC = -1; // 같으면 1 아니면 -1
	
    @FXML
    private Button backButton;
    @FXML
    private PasswordField checkPw;
    @FXML
    private Label completeLabel;
    @FXML
    private Button idCheckButton;
    @FXML
    private Label idLabel;
    @FXML
    private Button iniButton;
    @FXML
    private Label pwLabel;
    @FXML
    private Button signButton;
    @FXML
    private TextField signId;
    @FXML
    private PasswordField signPw;

    public void checkDB() {
		Connection conn = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql1 = "";
		id = signId.getText();
		
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			sql1 = "select id from chess_admin where id = ?";
			pstmt1 = conn.prepareStatement(sql1);
			pstmt1.setString(1, id);
			rs = pstmt1.executeQuery();

			if (rs.next()) { // 중복확인 및 비번 확인
				//중복
				idW = 1;
				idC = 0;
			} else if(!rs.next() && id.equals("")) { // 아무것도 안침
				idW = -1;
				idC = -1;
			} else if(!rs.next() && !id.equals("")) { // 사용가능
				idW = 1;
				idC = 1;
			}
			else { //없고 뭐가쳐지긴함.
				System.out.println("다른 경우의 수");
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
		}finally {// 6단계
			 try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {}
			try {
				if (pstmt1!=null) {
					pstmt1.close();
				}
			} catch (Exception e) {}
		}
	}
    
    @FXML
    public void checkID(ActionEvent event) {
		checkDB();
		completeLabel.setText("");
		pwLabel.setText("");
		if(idW==-1) {
			idLabel.setText("아이디를 입력하세요");
			idLabel.setTextFill(Color.RED);
		}else if(idC==1) {
			idLabel.setText("사용가능한 아이디");
			idLabel.setTextFill(Color.BLUE);
		}else if (idC == 0) {
			idLabel.setText("이미 사용 중인 아이디");
			idLabel.setTextFill(Color.RED);
		}
    }
    
	public void checkPW() {
		pw = signPw.getText();
		check = checkPw.getText();
		if(pw!="" && pw.equals(check)) {
			pwC = 1;
		}else {
			pwC = -1;
		}
		System.out.println("비밀번호 동일여부: "+ (pwC==1? "맞음":"틀림"));
	}

    @FXML
    public void clearAll(ActionEvent event) {
    	idW = -1;
		idC = -1;
		pwW = -1;
		pwC = -1;
		signId.setDisable(false);
		signId.setText("");
		idLabel.setText("");
		pwLabel.setText("");
		signPw.setText("");
		checkPw.setText("");
		completeLabel.setText("");
    }

	@FXML
	public void signUp(ActionEvent event) {
		
		checkPW();
		if(!id.equals(signId.getText()))idC=-1;  //중복확인한 아이디랑 입력된 아이디가 다른 경우 (중복 확인 후 아이디 다시 입력한 경우)
		if(idC==1&& pwC==1) {//중복안되고 비번 일치
			pwLabel.setText("");
			idLabel.setText("");
			completeLabel.setText("회원가입 완료");
			completeLabel.setTextFill(Color.BLUE);
			id = signId.getText();
			pw = signPw.getText();
		
			try {
				Connection conn = null;
				PreparedStatement pstmt1 = null;
				Statement smt1 = null;
				ResultSet rs = null;
				String sql1 = null;
				id = signId.getText();
				pw = signPw.getText();
				
				try {//정적 동적 쿼리 4,5단계
					conn = ConnUtil.getConnection();
					//chess_user 에 등록
					sql1= "INSERT INTO chess_admin(no,id,password) VALUES(admin_seq.nextval,?,?)";
					pstmt1 = conn.prepareStatement(sql1);
					pstmt1.setString(1, id);
					pstmt1.setString(2, pw);
					pstmt1.executeUpdate();
					
				}catch (Exception e) {
					e.printStackTrace();
					System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
				}finally {// 6단계
					 try {
						if (rs != null) {
							rs.close();
						}
					} catch (Exception e) {}
					try {
						if (conn != null) {
							conn.close();
						}
					} catch (Exception e) {}
					try {
						if (smt1!=null) {
							smt1.close();
						}
					} catch (Exception e) {}
					try {
						if (pstmt1!=null) {
							pstmt1.close();
						}
					} catch (Exception e) {}
				}
			} catch (Exception e) {	}
			
		}else if(idC==-1 || idW==-1) {
			completeLabel.setText("");
			idLabel.setText("");
			pwLabel.setTextFill(Color.RED);
			pwLabel.setText("아이디 중복확인 진행하세요");
		}else if(idC==0) {
			completeLabel.setText("");
			idLabel.setText("");
			pwLabel.setTextFill(Color.RED);
			pwLabel.setText("사용불가한 아이디입니다.");
		}else if (idC==1 && pwC==-1) {
			completeLabel.setText("");
			idLabel.setText("");
			pwLabel.setTextFill(Color.RED);
			pwLabel.setText("비밀번호가 다릅니다");
		}else {
			
			idLabel.setText("");
			completeLabel.setTextFill(Color.RED);
			completeLabel.setText("다른 경우의 수");
		}
		
		System.out.println(id);
		System.out.println(pw);
		
	}

    @FXML
	public void switchtoMain(ActionEvent event) throws IOException {
		idW = -1;
		idC = -1;
		pwW = -1;
		pwC = -1;

		root = FXMLLoader.load(getClass().getResource("/gui/AdminLogin.fxml"));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}
