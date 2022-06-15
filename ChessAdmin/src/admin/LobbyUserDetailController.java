package admin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import chess.database.Common;
import chess.database.ConnUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class LobbyUserDetailController implements Initializable{
	private Parent root;
	private Stage stage;
	private Scene scene;
	private String id,nick,genderr,date,imagePath,isBan;
	private int win,lose,game;
	@FXML
	private Button logoutButton, toAdminButton, toChatButton, toGameButton;
	@FXML
	private Button toRankButton, toServerButton, toUserButton;
	@FXML
	private Button photoButton,deletePhotoButton, changeButton, backButton ,deleteButton;
    @FXML
    private ToggleGroup bann,gender;
    @FXML
    private RadioButton bannTrue, bannFalse,maleButton, femaleButton;
    @FXML
    private PasswordField pwField, checkField;
    @FXML
    private TextField winField,loseField,gameField;
    @FXML
    private Label idLabel;
    @FXML
    private Label joinLabel,statusLabel;
    @FXML
    private TextField nickField;
	@FXML
	private ImageView photoImage;
	private FileChooser fileChooser = new FileChooser();
	byte[] person_image = null;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		statusLabel.setText("");
		id = Common.getDetailUser();
		showInfo();		

	}
	public void showInfo() {
		getDB();
		idLabel.setText(id);
		nickField.setText(nick);
		nickField.setEditable(false);
		pwField.setEditable(false);
		checkField.setEditable(false);
		if(genderr.equals("male")) {
			maleButton.setSelected(true);
			femaleButton.setDisable(true);
		}else {//여자일때
			femaleButton.setSelected(true);
			maleButton.setDisable(true);
		}
		double rate;
		if(game !=0) {
			rate = (win*100)/game;
		}else {rate = 0.00;}
		winField.setText(Integer.toString(win));
		loseField.setText(Integer.toString(lose));
		gameField.setText(Integer.toString(game));
		winField.setEditable(false);
		loseField.setEditable(false);
		gameField.setEditable(false);
		joinLabel.setText(date);
		if(isBan.equals("t")) {
			bannTrue.setSelected(true);
			bannFalse.setDisable(true);
		}else {//여자일때
			bannFalse.setSelected(true);
			bannTrue.setDisable(true);
		}
		
		if(!imagePath.equals("no")) {
			Image img = new Image(imagePath);
			
			double w = 0;
            double h = 0;

            double ratioX = photoImage.getFitWidth() / img.getWidth();
            double ratioY = photoImage.getFitHeight() / img.getHeight();

            double reducCoeff = 0;
            if(ratioX >= ratioY) {
                reducCoeff = ratioY;
            } else {
                reducCoeff = ratioX;
            }

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            photoImage.setX((photoImage.getFitWidth() - w) / 2);
            photoImage.setY((photoImage.getFitHeight() - h) / 2);
			
			photoImage.setImage(img);
		}else {
			System.out.println("이미지 없어서 기본이미지");
		}
	}
	public void getDB() {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		StringBuffer sqlbuff = new StringBuffer();
		String sql = "";
		try {
			conn = ConnUtil.getConnection();
			sqlbuff.append("select * from chess_user where id='");
			sqlbuff.append(id);
			sqlbuff.append("'");
			sql = sqlbuff.toString();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if(rs.next()) {
				id = rs.getString("id");
				nick = rs.getString("nick");
				genderr = rs.getString("gender");
				date = rs.getString("join");
				imagePath = rs.getString("image");
				isBan = rs.getString("blacklist");
				win = rs.getInt("win");
				lose = rs.getInt("lose");
				game = rs.getInt("game");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("쿼리문 오류");
		}finally {
			try {if (rs != null) rs.close();	} catch (Exception e) {}
			try {if (conn != null) conn.close();	} catch (Exception e) {}
			try {if (st != null) st.close();	} catch (Exception e) {}
		}
		
	}
	public void updateInfo(ActionEvent event) {
		if(changeButton.getText().equals("수정")) {
			changeButton.setText("완료");
			nickField.setEditable(true);
			pwField.setEditable(true);
			checkField.setEditable(true);
			winField.setEditable(true);
			loseField.setEditable(true);
			maleButton.setDisable(false);
			femaleButton.setDisable(false);
			bannFalse.setDisable(false);
			bannTrue.setDisable(false);
			nickField.requestFocus();
		}else {//수정을 한번 눌러서 버튼이 "완료" 일 때
			update();
		}
	}
	public void update() {
		Connection conn = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql = "";
		
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			statusLabel.setText("");
			if((pwField.getText()).equals((checkField).getText()) && !(pwField.getText()).equals("")){ //일치하고 ""이 아닐때
				//비밀번호도 update 해야함
				sql = "update chess_user set nick = ?,password = ?,gender = ?,"
						+ "win =?,lose =?,game=?,blacklist=?,rate =? where id = ? "; //9
				changeButton.setText("수정");
				
				pstmt1 = conn.prepareStatement(sql);
				pstmt1.setString(1, nickField.getText());
				pstmt1.setString(2, pwField.getText());
				if(maleButton.isSelected()) {
					pstmt1.setString(3, "male");
				}else {pstmt1.setString(3, "female");}
				pstmt1.setInt(4, Integer.parseInt(winField.getText()));
				pstmt1.setInt(5, Integer.parseInt(loseField.getText()));
				game = Integer.parseInt(winField.getText())+Integer.parseInt(loseField.getText());
				pstmt1.setInt(6, game);
				gameField.setText(Integer.toString(game));
				if(bannFalse.isSelected()) {
					pstmt1.setString(7, "f");
				}else {pstmt1.setString(7, "t");}
				double rate;
				if(Integer.parseInt(winField.getText())+Integer.parseInt(loseField.getText()) !=0) {
					rate = (Integer.parseInt(winField.getText())*100)/(Integer.parseInt(winField.getText())+Integer.parseInt(loseField.getText()));
				}else {rate = 0.00;}
				pstmt1.setDouble(8, rate);
				pstmt1.setString(9, idLabel.getText());
				pstmt1.executeUpdate();
				statusLabel.setText("정보가 수정되었습니다");
				statusLabel.setTextFill(Color.BLUE);
				showInfo();	
			}else if ((pwField.getText()).equals((checkField).getText()) && (pwField.getText()).equals("")) {//둘다 아무것도 입력 안함
				//비밀번호는 update 안함
				sql = "update chess_user set nick = ?,gender = ?,win =?,lose =?,game=?,blacklist=?, rate =? where id = ? ";//8
				changeButton.setText("수정");
				pstmt1 = conn.prepareStatement(sql);
				pstmt1.setString(1, nickField.getText());
				if(maleButton.isSelected()) {
					pstmt1.setString(2, "male");
				}else {pstmt1.setString(2, "female");}
				pstmt1.setInt(3, Integer.parseInt(winField.getText()));
				pstmt1.setInt(4, Integer.parseInt(loseField.getText()));
				game = Integer.parseInt(winField.getText())+Integer.parseInt(loseField.getText());
				pstmt1.setInt(5, game);
				gameField.setText(Integer.toString(game));
				if(bannFalse.isSelected()) {
					pstmt1.setString(6, "f");
				}else {pstmt1.setString(6, "t");}
				double rate;
				if(Integer.parseInt(winField.getText())+Integer.parseInt(loseField.getText()) !=0) {
					rate = (Integer.parseInt(winField.getText())*100)/(Integer.parseInt(winField.getText())+Integer.parseInt(loseField.getText()));
				}else {rate = 0.00;}
				pstmt1.setDouble(7, rate);
				pstmt1.setString(8, id);
				pstmt1.executeUpdate();
				statusLabel.setText("정보가 수정되었습니다");
				statusLabel.setTextFill(Color.BLUE);

			}else if(!(pwField.getText()).equals((checkField).getText())) {//일치 안함
				statusLabel.setTextFill(Color.RED);
				statusLabel.setText("비밀번호가 일치하지 않습니다");
			}else {
				statusLabel.setText("어딘가 이상하다");
				statusLabel.setTextFill(Color.RED);
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
	public void uploadPhoto(ActionEvent event) {
		//png, jpeg 이미지 파일로 한정 
		FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("모든 이미지 파일", "*.png","*.jpg","*.jpeg","*.jpe","*.jfif");
		FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG (*.png)", "*.png");
		FileChooser.ExtensionFilter JPEGFilter = new FileChooser.ExtensionFilter("JPEG (*.jpg,*.jpeg,*.jpe,*.jfif)", "*.jpg","*.jpeg","*.jpe","*.jfif");
		fileChooser.getExtensionFilters().add(allFilter);
		fileChooser.getExtensionFilters().add(pngFilter);
		fileChooser.getExtensionFilters().add(JPEGFilter);
		//바탕화면.. 사람마다 경로가 다르다면 씨드라이브
		if((new File(System.getProperty("user.home")+"\\Desktop")).canRead()) {
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home")+"\\Desktop"));
		}else {
			fileChooser.setInitialDirectory(new File("c:/"));
		}
		File file = fileChooser.showOpenDialog(new Stage());
		if(file != null) {
			//이미지 경로로 하겠음...
			//upload(file);
			//savePhoto(file);
			//Common.setIm(file);
			imagePath = file.getPath();
			
			Image img = new Image(imagePath);
			
			double w = 0;
            double h = 0;

            double ratioX = photoImage.getFitWidth() / img.getWidth();
            double ratioY = photoImage.getFitHeight() / img.getHeight();

            double reducCoeff = 0;
            if(ratioX >= ratioY) {
                reducCoeff = ratioY;
            } else {
                reducCoeff = ratioX;
            }
            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            photoImage.setX((photoImage.getFitWidth() - w) / 2);
            photoImage.setY((photoImage.getFitHeight() - h) / 2);
			
			photoImage.setImage(img);
			Connection conn = null;
			PreparedStatement pstmt2 = null;
			ResultSet rs = null;
			String sql2 = "";
			
			try {//정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				// id가 id 인 정보에서 pw 랑 nick을 변경하는 sql 기릿 
				sql2 = "update chess_user set image = ? where id = ?";
				pstmt2 = conn.prepareStatement(sql2);
				pstmt2.setString(1, imagePath);
				pstmt2.setString(2, id);
				pstmt2.executeUpdate();
				statusLabel.setText("사진이 수정되었습니다");
				statusLabel.setTextFill(Color.BLUE);
			}catch (Exception e) {
				e.printStackTrace();
				System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
			}finally {// 6단계
				 try {if (rs != null) rs.close();	} catch (Exception e) {}
				 try {if (conn != null) conn.close();	} catch (Exception e) {}
				 try {if (pstmt2 != null) pstmt2.close();	} catch (Exception e) {}
			}
		}
	}
	@FXML
    public void deletePhoto(ActionEvent event) {
		Connection conn = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql = "";
		
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			sql = "update chess_user set image = 'no' where id = ? "; //9
			
			pstmt1 = conn.prepareStatement(sql);
			pstmt1.setString(1, idLabel.getText());
			pstmt1.executeUpdate();
			statusLabel.setText("사진이 삭제되었습니다");
			statusLabel.setTextFill(Color.BLUE);
			Image image = new Image("C:\\pmProject\\javaWorkSpace\\ChessGame\\src\\image\\avatar3.png");
			photoImage.setX(0);
            photoImage.setY(0);
			photoImage.setImage(image);
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
	public void deleteUser(ActionEvent event){
		try {
			Rectangle2D screenBounds = Screen.getPrimary().getBounds();
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setWidth(400);
			alert.setX(screenBounds.getMaxX() / 2 - 200);
			alert.setY(screenBounds.getMaxY() / 2 - 50);
			alert.setResizable(false);
			alert.setTitle("회원탈퇴");
			alert.setHeaderText("  회원 탈퇴 후 복원할 수 없습니다");
			alert.setContentText("정말로 탈퇴시키겠습니까??");
			
			if (alert.showAndWait().get() == ButtonType.OK) {
				//chess_user+해당 유저의 user_xx 테이블 삭제할거임
				Connection conn = null;
				Statement stmt = null;
				PreparedStatement pstmt1 = null;
				ResultSet rs = null;
				String sql = "";
				StringBuffer sql2 = new StringBuffer();
				try {//정적 동적 쿼리 4,5단계
					conn = ConnUtil.getConnection();
					//chess_user 에서 row 삭제
					sql = "delete from chess_user where id = ? ";
					pstmt1 = conn.prepareStatement(sql);
					pstmt1.setString(1, id);
					rs = pstmt1.executeQuery();
					
					//user_id 테이블 삭제
					sql2.append("drop table user_");
					sql2.append(id);
					sql2.append(" purge");
					stmt = conn.createStatement();
					rs = stmt.executeQuery(sql2.toString());
					
				}catch (Exception e) {
					e.printStackTrace();
					System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
				}finally {// 6단계
					 try {if (rs != null) rs.close();	} catch (Exception e) {}
					 try {if (conn != null) conn.close();	} catch (Exception e) {}
					 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e) {}
				}
				
			}
		} catch (Exception e) {
			System.err.println("선택한게 없음");
		}
	}
	@FXML
	public void toUser(ActionEvent event) {
		try {
			root = FXMLLoader.load(getClass().getResource("/gui/AdminLobby.aUser.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
		}
	}
	@FXML
	public void toGame(ActionEvent event) {
		try {
			root = FXMLLoader.load(getClass().getResource("/gui/AdminLobby.bGame.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
		}
	}
	@FXML
	public void toServer(ActionEvent event) {
		try {
			root = FXMLLoader.load(getClass().getResource("/gui/AdminLobby.cServer.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
		}
	}
	@FXML
	public void toChat(ActionEvent event) {
		try {
			root = FXMLLoader.load(getClass().getResource("/gui/AdminLobby.dChat.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
		}
	}
	@FXML
	public void toRank(ActionEvent event) {
		try {
			root = FXMLLoader.load(getClass().getResource("/gui/AdminLobby.eRank.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
		}
	}
	@FXML
	public void toAdmin(ActionEvent event) {
		try {
			root = FXMLLoader.load(getClass().getResource("/gui/AdminLobby.fAdmin.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
		}
	}
	@FXML
	public void switchtoLogin(ActionEvent event) throws IOException {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setWidth(400);
		alert.setX(screenBounds.getMaxX() / 2 - 200);
		alert.setY(screenBounds.getMaxY() / 2 - 50);
		alert.setResizable(false);
		alert.setTitle("로그아웃");
		alert.setHeaderText("");
		alert.setContentText("정말로 로그아웃하시겠습니까?");

		if (alert.showAndWait().get() == ButtonType.OK) {
			root = FXMLLoader.load(getClass().getResource("/gui/AdminLogin.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.setX(screenBounds.getMaxX() / 2 - 160);
			stage.setY(screenBounds.getMaxY() / 2 - 200);
			stage.show();
		}
	}
}
