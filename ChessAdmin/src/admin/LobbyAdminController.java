package admin;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import chess.database.Common;
import chess.database.ConnUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import observableList.AdminList;

public class LobbyAdminController implements Initializable{
	private Parent root;
	private Stage stage;
	private Scene scene;
	private Popup popup;
	String sql1 = "";
	PreparedStatement pstmt1 = null;
	@FXML
	private Button logoutButton, toAdminButton, toChatButton, toGameButton;
	@FXML
	private Button toRankButton, toServerButton, toUserButton,searchButton,allButton,deleteButton,grantButton ;
	@FXML
	private TextField searchField;
	@FXML
    private ChoiceBox<String> searchChoice;
    @FXML
    private TableView<AdminList> adminTableView;
    @FXML
    private TableColumn<AdminList,String> seqCol;
    @FXML
    private TableColumn<AdminList,String> idCol;
    @FXML
    private TableColumn<AdminList,String> joinCol;
    @FXML
    private TableColumn<AdminList,String> permissionCol;
    private static String searchOption = "";
    private String[] choice = { "아이디", "권한(y/n)" };
	ObservableList<AdminList> adminDB = FXCollections.observableArrayList();
	@Override
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		adminTableView.setPlaceholder(new Label("검색과 일치하는 관리자가 없습니다."));
		toAdminButton.requestFocus();
		sql1 = "select * from chess_admin order by no";
		
		getAdminDB();
		searchChoice.getItems().addAll(choice);
		searchChoice.setOnAction(this::getOption);
		
		seqCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNo()));
		idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
		joinCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getJoin()));
		permissionCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPermit()));
		
		adminTableView.setItems(adminDB);
    	//onlineTextField.setText(Integer.toString(Common.getNum()));
		
		
	}
	
	public void getOption(ActionEvent event) {
		searchOption = searchChoice.getValue();
	}

	public void grant(ActionEvent event) throws Exception {
		try {
			int selectedIndex = adminTableView.getSelectionModel().getSelectedIndex();
			String idOfGrant = "";
			idOfGrant =idCol.getCellData(selectedIndex);
			String permissionOfGrant = permissionCol.getCellData(selectedIndex);
			System.out.println(idOfGrant);
			System.out.println(permissionOfGrant);
			//선택된게 없을 때는 뜨지않음
			if(permissionOfGrant.equals("Y") || permissionOfGrant.equals("N")){
				Common.setDetailAdmin(idOfGrant);
				Common.setAdminGrant(permissionOfGrant);
				Rectangle2D screenBounds = Screen.getPrimary().getBounds();
				Parent parent = FXMLLoader.load(getClass().getResource("/gui/AdGrant.fxml"));
	
				Scene scene = new Scene(parent);
				Stage stage = new Stage();
				stage.setTitle("관리자 권한 수정");
				stage.getIcons().add(new Image(getClass().getResource("/image/Chess_Icon.png").toURI().toString()));
				stage.setX(screenBounds.getMaxX()/2 -160);
				stage.setY(screenBounds.getMaxY()/2 -100);
				stage.setScene(scene);
				stage.showAndWait();
			}else {
				System.err.println("선택한게 업슴");
			}
			
			

		} catch (Exception e) {		}
	}
         


	
	//하나 선택했을 때 삭제
	public void delete(ActionEvent event) {
		try {
			Rectangle2D screenBounds = Screen.getPrimary().getBounds();
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setWidth(400);
			alert.setX(screenBounds.getMaxX() / 2 - 200);
			alert.setY(screenBounds.getMaxY() / 2 - 50);
			alert.setResizable(false);
			alert.setTitle("관리자탈퇴");
			alert.setHeaderText("  관리자 탈퇴 후 복원할 수 없습니다");
			alert.setContentText("정말로 탈퇴시키겠습니까??");
			
			if (alert.showAndWait().get() == ButtonType.OK) {
				int selectedIndex = adminTableView.getSelectionModel().getSelectedIndex();
				String idOfDelete = idCol.getCellData(selectedIndex);
				System.out.println(idOfDelete);
				adminTableView.getItems().remove(selectedIndex);
				//chess_user+해당 유저의 user_xx 테이블 삭제할거임
				Connection conn = null;
				Statement stmt = null;
				PreparedStatement pstmt1 = null;
				ResultSet rs = null;
				String sql1 = "";
				StringBuffer sql2 = new StringBuffer();
				try {//정적 동적 쿼리 4,5단계
					conn = ConnUtil.getConnection();
					//chess_user 에서 row 삭제
					sql1 = "delete from chess_admin where id = ? ";
					pstmt1 = conn.prepareStatement(sql1);
					pstmt1.setString(1, idOfDelete);
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
		} catch (Exception e) {
			System.err.println("선택한게 없음");
		}
	}
	
	public void clearTable() {
		adminTableView.getItems().clear();
		//adminTableView.refresh();
	}
	
	@FXML
	public void search(ActionEvent event) {
		clearTable();
		StringBuffer sql = new StringBuffer(); 
		if(searchOption.equals("") || searchOption.equals("아이디")){
			sql.append("select * from chess_admin where id = '");
			sql.append(searchField.getText());
			sql.append("'");
			sql1 = sql.toString();
		}else if(searchOption.equals("권한(y/n)")){
			sql.append("select * from chess_admin where permission = '");
			if(searchField.getText().equals("y") || searchField.getText().equals("Y") || searchField.getText().equals("T") || searchField.getText().equals("t")) {
				sql.append("y");
			}else if(searchField.getText().equals("n") || searchField.getText().equals("N")|| searchField.getText().equals("f")|| searchField.getText().equals("F")) {
				sql.append("n");
			}else {
				sql.append(searchField.getText());
			}
			sql.append("'");
			sql1 = sql.toString();
		}
		getAdminDB();
		//adminTableView.getItems().clear();
		
		seqCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNo()));
		idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
		joinCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getJoin()));
		permissionCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPermit()));
		adminTableView.setItems(adminDB);
	}
	
	public void getAdminDB() {
		Connection conn = null;
		ResultSet rs = null;
		
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			
			//sql1 = "select * from chess_admin order by no";
			pstmt1 = conn.prepareStatement(sql1);
			//pstmt1.setString(1, ID);
			rs = pstmt1.executeQuery();
			
			while(rs.next()) { // 일치하는게 있다면?
				//(int no, String id, String join, String permit)
				adminDB.add(new AdminList(rs.getInt("no"),rs.getString("id"),rs.getString("join"),rs.getString("permission")   ));
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

    @FXML
    public void toUser(ActionEvent event) {
		try {
			root = FXMLLoader.load(getClass().getResource("/gui/AdminLobby.aUser.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		}catch (Exception e) {}
    }
    @FXML
    public void toGame(ActionEvent event) {
    	try {
			root = FXMLLoader.load(getClass().getResource("/gui/AdminLobby.bGame.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		}catch (Exception e) {}
    }
    @FXML
    public void toServer(ActionEvent event) {
    	try {
			root = FXMLLoader.load(getClass().getResource("/gui/AdminLobby.cServer.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		}catch (Exception e) {}
    }
    @FXML
    public void toChat(ActionEvent event) {
    	try {
			root = FXMLLoader.load(getClass().getResource("/gui/AdminLobby.dChat.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		}catch (Exception e) {}
    }
    @FXML
    public void toRank(ActionEvent event) {
    	try {
			root = FXMLLoader.load(getClass().getResource("/gui/AdminLobby.eRank.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		}catch (Exception e) {}
    }
    @FXML
    public void toAdmin(ActionEvent event) {
    	try {
			root = FXMLLoader.load(getClass().getResource("/gui/AdminLobby.fAdmin.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		}catch (Exception e) {}
    }
}
