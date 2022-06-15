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
	
	static int idW = -1; // ������ 1 �ƴϸ� -1
	static int idC = -1; // Ȯ�������� 2 ��밡���̸� 1 �ߺ��̸� 0 �������� -1
	static int pwW = -1; // ������ 1 �ƴϸ� -1
	static int pwC = -1; // ������ 1 �ƴϸ� -1
	
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
		
		try {//���� ���� ���� 4,5�ܰ�
			conn = ConnUtil.getConnection();
			sql1 = "select id from chess_admin where id = ?";
			pstmt1 = conn.prepareStatement(sql1);
			pstmt1.setString(1, id);
			rs = pstmt1.executeQuery();

			if (rs.next()) { // �ߺ�Ȯ�� �� ��� Ȯ��
				//�ߺ�
				idW = 1;
				idC = 0;
			} else if(!rs.next() && id.equals("")) { // �ƹ��͵� ��ħ
				idW = -1;
				idC = -1;
			} else if(!rs.next() && !id.equals("")) { // ��밡��
				idW = 1;
				idC = 1;
			}
			else { //���� ������������.
				System.out.println("�ٸ� ����� ��");
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("������ ������ Ȯ���� ���� ����� ����");
		}finally {// 6�ܰ�
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
			idLabel.setText("���̵� �Է��ϼ���");
			idLabel.setTextFill(Color.RED);
		}else if(idC==1) {
			idLabel.setText("��밡���� ���̵�");
			idLabel.setTextFill(Color.BLUE);
		}else if (idC == 0) {
			idLabel.setText("�̹� ��� ���� ���̵�");
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
		System.out.println("��й�ȣ ���Ͽ���: "+ (pwC==1? "����":"Ʋ��"));
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
		if(!id.equals(signId.getText()))idC=-1;  //�ߺ�Ȯ���� ���̵�� �Էµ� ���̵� �ٸ� ��� (�ߺ� Ȯ�� �� ���̵� �ٽ� �Է��� ���)
		if(idC==1&& pwC==1) {//�ߺ��ȵǰ� ��� ��ġ
			pwLabel.setText("");
			idLabel.setText("");
			completeLabel.setText("ȸ������ �Ϸ�");
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
				
				try {//���� ���� ���� 4,5�ܰ�
					conn = ConnUtil.getConnection();
					//chess_user �� ���
					sql1= "INSERT INTO chess_admin(no,id,password) VALUES(admin_seq.nextval,?,?)";
					pstmt1 = conn.prepareStatement(sql1);
					pstmt1.setString(1, id);
					pstmt1.setString(2, pw);
					pstmt1.executeUpdate();
					
				}catch (Exception e) {
					e.printStackTrace();
					System.err.println("������ ������ Ȯ���� ���� ����� ����");
				}finally {// 6�ܰ�
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
			pwLabel.setText("���̵� �ߺ�Ȯ�� �����ϼ���");
		}else if(idC==0) {
			completeLabel.setText("");
			idLabel.setText("");
			pwLabel.setTextFill(Color.RED);
			pwLabel.setText("���Ұ��� ���̵��Դϴ�.");
		}else if (idC==1 && pwC==-1) {
			completeLabel.setText("");
			idLabel.setText("");
			pwLabel.setTextFill(Color.RED);
			pwLabel.setText("��й�ȣ�� �ٸ��ϴ�");
		}else {
			
			idLabel.setText("");
			completeLabel.setTextFill(Color.RED);
			completeLabel.setText("�ٸ� ����� ��");
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
