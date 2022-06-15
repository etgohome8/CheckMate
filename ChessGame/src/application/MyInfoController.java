package application;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import chess.database.Common;
import chess.database.ConnUtil;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import oracle.jdbc.internal.OracleResultSet;
import oracle.sql.BLOB;

public class MyInfoController implements Initializable{
	
	private Parent root;
	private Stage stage;
	private Scene scene;
	static int pwCheck = 0; //일치하면 1, 안썻으면 0, 불일치하면 -1
	private FileChooser fileChooser = new FileChooser();
    @FXML
    private TextField nickField;
    @FXML
    private Label idLabel,statusLabel,gameLabel, winLabel,loseLabel,percentLabel;
    @FXML
    private Button backButton,changeButton,deleteButton, photoButton;  
    @FXML
    private PasswordField checkField;
    @FXML
    private RadioButton femaleButton;
    @FXML
    private RadioButton maleButton;
    @FXML
    private PasswordField pwField;
    @FXML ImageView photoImage;
    byte[] person_image = null;
    private String imagePath;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		idLabel.setText(Common.getId());
		nickField.setText(Common.getNick());
		nickField.setDisable(true);
		pwField.setDisable(true);
		checkField.setDisable(true);
		if(Common.getGender().equals("male")) {
			maleButton.setSelected(true);
			femaleButton.setDisable(true);
		}else {//여자일때
			femaleButton.setSelected(true);
			maleButton.setDisable(true);
		}
		double rate;
		if(Common.getGame() !=0) {
			rate = (Common.getWin()*100)/(Common.getGame());
		}else {rate = 0.00;}
		gameLabel.setText(Common.getGame()+"전");
		winLabel.setText(Common.getWin()+"승");
		loseLabel.setText(Common.getLose()+"패");
		percentLabel.setText(rate+"%");
		if(!Common.getimagePath().equals("no")) {
			Image img = new Image(Common.getimagePath());
			
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
		}
    }
	@FXML
	public void updateInfo(ActionEvent event) {
		if(changeButton.getText().equals("수정")) {
			changeButton.setText("완료");
			nickField.setEditable(true);
			pwField.setEditable(true);
			checkField.setEditable(true);
			nickField.setDisable(false);
			pwField.setDisable(false);
			checkField.setDisable(false);
			nickField.requestFocus();
		}else {//수정을 한번 눌러서 버튼이 "완료" 일 때
			update();
		}
	}
	
	public void update() {
		Connection conn = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql1 = "";
		
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			// id가 id 인 정보에서 pw 랑 nick을 변경하는 sql 기릿 
			sql1 = "update chess_user set password = ?, nick = ? where id = ?";
			
			if((pwField.getText()).equals((checkField).getText()) && !(pwField.getText()).equals("")){ //일치하고 ""이 아닐때
				//비밀번호도 update 해야함
				changeButton.setText("수정");
				nickField.setDisable(true);
				pwField.setDisable(true);
				checkField.setDisable(true);
				if(Common.getNick().equals(nickField.getText()) && Common.getPw().equals(pwField.getText())) {
					//비번 기존이랑 똑같고 닉도 안바꾸면 그냥 아무것도 출력안됨
					statusLabel.setText("");
				}else {
					pstmt1 = conn.prepareStatement(sql1);
					pstmt1.setString(1, pwField.getText());
					Common.setPw(pwField.getText());
					pstmt1.setString(2, nickField.getText());
					Common.setNick(nickField.getText());
					pstmt1.setString(3, Common.getId());
					pstmt1.executeUpdate();
					statusLabel.setText("정보가 수정되었습니다");
					statusLabel.setTextFill(Color.BLUE);
				}
				
			}else if ((pwField.getText()).equals((checkField).getText()) && (pwField.getText()).equals("")) {//둘다 아무것도 입력 안함
				//비밀번호는 update 안함
				changeButton.setText("수정");
				nickField.setDisable(true);
				pwField.setDisable(true);
				checkField.setDisable(true);
				if(Common.getNick().equals(nickField.getText())) {//비번도 안바꾸면 그냥 아무것도 출력안됨
					statusLabel.setText("");
				}else {
					pstmt1 = conn.prepareStatement(sql1);
					pstmt1.setString(1, Common.getPw());
					pstmt1.setString(2, nickField.getText());
					Common.setNick(nickField.getText());
					pstmt1.setString(3, Common.getId());
					pstmt1.executeUpdate();
					statusLabel.setText("정보가 수정되었습니다");
					statusLabel.setTextFill(Color.BLUE);
				}
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
	
	public void deleteComp() {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql1 = "";
		StringBuffer sql2 = new StringBuffer();
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			//chess_user 에서 삭제
			sql1 = "delete from chess_user where id = ? ";
			pstmt1 = conn.prepareStatement(sql1);
			pstmt1.setString(1, Common.getId());
			rs = pstmt1.executeQuery();
			
			//user_id 테이블 삭제
			sql2.append("drop table user_");
			sql2.append(Common.getId());
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
	
	public void upload(File file) {
		try {
            BufferedImage bufferedImage = ImageIO.read(file);
            WritableImage image = SwingFXUtils.toFXImage(bufferedImage, null);
            photoImage.setImage(image);
            photoImage.setFitWidth(108);
            photoImage.setFitHeight(144);
            photoImage.scaleXProperty();
            photoImage.scaleYProperty();
            photoImage.setSmooth(true);
            photoImage.setCache(true);
            FileInputStream fin = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];

            for (int readNum; (readNum = fin.read(buf)) != -1;) {
                bos.write(buf, 0, readNum);
            }
            person_image = bos.toByteArray();
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public void savePhoto(File file) {
		try {
			Connection conn = null;
			PreparedStatement pstmt2 = null;
			ResultSet rs = null;
			String sql2 = "";
			FileInputStream fis = new FileInputStream(file);
			try {//정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				// id가 id 인 정보에서 pw 랑 nick을 변경하는 sql 기릿 
				sql2 = "update chess_user set userim = ? where id = ?";
				pstmt2 = conn.prepareStatement(sql2);
				pstmt2.setBinaryStream(1, (InputStream)fis, file.length());
				pstmt2.setString(2, Common.getId());
				pstmt2.executeUpdate();
				
			}catch (Exception e2) {
				e2.printStackTrace();
				System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
			}finally {// 6단계
				 try {if (rs != null) rs.close();	} catch (Exception e) {}
				 try {if (conn != null) conn.close();	} catch (Exception e) {}
				 try {if (pstmt2 != null) pstmt2.close();	} catch (Exception e) {}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
			Common.setimagePath(imagePath);
			
			Image img = new Image(Common.getimagePath());
			
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
				Common.setimagePath(imagePath);
				pstmt2.setString(2, Common.getId());
				pstmt2.executeUpdate();
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
    public void toLobby(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Lobby.fxml"));
		root = loader.load();
		LobbyController lobby = loader.getController();
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setResizable(false);
		
		stage.show();
    }

	@FXML
	public void deleteAccount(ActionEvent event) throws IOException {
		try {
			
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		Alert alert= new Alert(AlertType.CONFIRMATION);
		Image image = new Image(getClass().getResource("/image/sadface.png").toURI().toString());
		ImageView imageView = new ImageView(image);
		alert.setGraphic(imageView);

		alert.setWidth(400);
		alert.setX(screenBounds.getMaxX()/2 -200);
		alert.setY(screenBounds.getMaxY()/2 -50);
		alert.setResizable(false);
		alert.setTitle("탈퇴 하지마....");
		alert.setHeaderText("탈퇴 시 모든 기록이 사라지며 계정을 복구할 수 없습니다\n"
				+ "탈퇴 하면 저주를 받을 수도 있습니다...");
		alert.setContentText("정말로 탈퇴하시겠습니까?");
		
		if(alert.showAndWait().get() == ButtonType.OK) {
			Alert lastalert= new Alert(AlertType.CONFIRMATION);
			Image image2 = new Image(getClass().getResource("/image/worriedface.png").toURI().toString());
			ImageView imageView2 = new ImageView(image2);
			lastalert.setGraphic(imageView2);
			lastalert.setWidth(360);
			lastalert.setX(screenBounds.getMaxX()/2 -180);
			lastalert.setY(screenBounds.getMaxY()/2 -50);
			lastalert.setResizable(false);
			lastalert.setTitle("한 번 더 물어볼게요..");
			lastalert.setHeaderText("");
			lastalert.setContentText("진짜로 탈퇴하시겠습니까?");
			
			if(lastalert.showAndWait().get() == ButtonType.OK) {
				Alert reallastalert= new Alert(AlertType.CONFIRMATION);
				Image image3 = new Image(getClass().getResource("/image/angryface.png").toURI().toString());
				ImageView imageView3 = new ImageView(image3);
				reallastalert.setGraphic(imageView3);
				reallastalert.setWidth(360);
				reallastalert.setX(screenBounds.getMaxX()/2 -180);
				reallastalert.setY(screenBounds.getMaxY()/2 -50);
				reallastalert.setResizable(false);
				reallastalert.setTitle("진짜 마지막으로 물어볼게");
				reallastalert.setHeaderText("");
				reallastalert.setContentText("정말로 탈퇴할거야!?!?");
				
				if(reallastalert.showAndWait().get() == ButtonType.OK) {
					Alert delcomp= new Alert(AlertType.INFORMATION);
					delcomp.setGraphic(imageView3);
					delcomp.setWidth(360);
					delcomp.setX(screenBounds.getMaxX()/2 -180);
					delcomp.setY(screenBounds.getMaxY()/2 -50);
					delcomp.setResizable(false);
					delcomp.setTitle("탈퇴 완료..");
					delcomp.setHeaderText("");
					delcomp.setContentText("회원탈퇴 완료..");
					delcomp.showAndWait();
					
					deleteComp();
					
					root = FXMLLoader.load(getClass().getResource("/gui/Main.fxml"));
					stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
					scene = new Scene(root);
					stage.setScene(scene);
					stage.setResizable(false);
					stage.setX(screenBounds.getMaxX()/2 -160);
					stage.setY(screenBounds.getMaxY()/2 -200);
					stage.show();
				}
			}
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
}

