package admin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import observableList.ChatLog;
import observableList.UserList;

public class LobbyUserController implements Initializable {
	private Parent root;
	private Stage stage;
	private Scene scene;
	String sql1 = "";
	PreparedStatement pstmt1 = null;
	@FXML
	private Button logoutButton, toAdminButton, toChatButton, toGameButton;
	@FXML
	private Button toRankButton, toServerButton, toUserButton, saveButton,showButton;
	@FXML
	private TableColumn<UserList, String> ageCol;
	@FXML
	private TableColumn<UserList, String> banCol;
	@FXML
	private TableView<UserList> userTableView;
	@FXML
	private TableColumn<UserList, String> genderCol;
	@FXML
	private TableColumn<UserList, String> idCol;
	@FXML
	private TableColumn<UserList, String> idseqCol;
	@FXML
	private TableColumn<UserList, String> joinCol;
	@FXML
	private TableColumn<UserList, String> loseCol;
	@FXML
	private TableColumn<UserList, String> nickCol;
	@FXML
	private TableColumn<UserList, String> rateCol;
	@FXML
	private Button searchButton;
	@FXML
	private ChoiceBox<String> searchChoice;
	@FXML
	private TextField searchField;
	@FXML
	private TableColumn<UserList, String> winCol;
	@FXML
	private Button deleteButton;
	@FXML
	private Button detailButton;
	private String searchOption = "";
	private String[] choice = { "아이디", "닉네임" };
	ObservableList<UserList> userDB = FXCollections.observableArrayList();
	private FileChooser fileChooser = new FileChooser();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		userTableView.setPlaceholder(new Label("검색과 일치하는 회원이 없습니다."));
		showshow();
    	//onlineTextField.setText(Integer.toString(Common.getNum()));
	}
	public void showshow() {
		sql1 = "select * from chess_user order by id_code";
		
		getUserDB();
		searchChoice.getItems().addAll(choice);
		searchChoice.setOnAction(this::getOption);
		
		idseqCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIdx()));
		idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
		nickCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNick()));
		genderCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGender()));
		ageCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAge()));
		winCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getWin()));
		loseCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLose()));
		rateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRate()));
		joinCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getJoindate()));
		banCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIsBlack()));
		
    	userTableView.setItems(userDB);
	}
		
	public void clearTable() {
		userTableView.getItems().clear();
		//adminTableView.refresh();
	}
	
	@FXML
	public void search(ActionEvent event) {
		clearTable();
		StringBuffer sql = new StringBuffer(); 
		if(searchOption.equals("") || searchOption.equals("아이디")){
			sql.append("select * from chess_user where id = '");
			sql.append(searchField.getText());
			sql.append("'");
			sql1 = sql.toString();
		}else if(searchOption.equals("닉네임")){
			sql.append("select * from chess_user where nick = '");
			sql.append(searchField.getText());
			sql.append("'");
			sql1 = sql.toString();
		}
		getUserDB();
		
		idseqCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIdx()));
		idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
		nickCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNick()));
		genderCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGender()));
		ageCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAge()));
		winCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getWin()));
		loseCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLose()));
		rateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRate()));
		joinCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getJoindate()));
		banCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIsBlack()));
		
    	userTableView.setItems(userDB);
	}
	
	public void getOption(ActionEvent event) {
		searchOption = searchChoice.getValue();
	}
	
	public void getUserDB() {
		Connection conn = null;
		ResultSet rs = null;
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			pstmt1 = conn.prepareStatement(sql1);
			//pstmt1.setString(1, ID);
			rs = pstmt1.executeQuery();
			
			while(rs.next()) { // 일치하는게 있다면?
				//(int idx, String id, String nick, String gender, int age, int win, int lose, double rate, String joindate, String isBlack)
				int win = rs.getInt("win");
				int lose = rs.getInt("lose");
				userDB.add(new UserList(rs.getInt("id_code"),rs.getString("id"),rs.getString("nick"),rs.getString("gender"),
						rs.getInt("age"),win,lose, rs.getDouble("rate") ,rs.getString("join"), rs.getString("blacklist") ));
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
    public void saveFile(ObservableList<UserList> userDB , File file) {
    	try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write("ID_Code\tID\tNICK\tGENDER\tAGE\tWIN\tLOSE\t승률\t가입일\t\t블랙리스트\n");
			for(UserList user : userDB) {
				out.write(user.ToString());
				out.newLine();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	@FXML
    public void saveUser(ActionEvent event) {
		//txt 파일로 한정 
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		//바탕화면.. 사람마다 경로가 다르다면 씨드라이브
		if((new File(System.getProperty("user.home")+"\\Desktop")).canRead()) {
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home")+"\\Desktop"));
		}else {
			fileChooser.setInitialDirectory(new File("c:/"));
		}
		File file = fileChooser.showSaveDialog(new Stage());
		if(file != null) {
			saveFile(userDB,file);
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
				int selectedIndex = userTableView.getSelectionModel().getSelectedIndex();
				String idOfDelete = idCol.getCellData(selectedIndex);
				System.out.println(idOfDelete);
				userTableView.getItems().remove(selectedIndex);
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
					sql1 = "delete from chess_user where id = ? ";
					pstmt1 = conn.prepareStatement(sql1);
					pstmt1.setString(1, idOfDelete);
					rs = pstmt1.executeQuery();
					
					//user_id 테이블 삭제
					sql2.append("drop table user_");
					sql2.append(idOfDelete);
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
	public void showAll(ActionEvent event) {
		clearTable();
		showshow();
	}
	@FXML
	public void showDetail(ActionEvent event) {
		int selectedIndex = userTableView.getSelectionModel().getSelectedIndex();
		String idOfDetail = idCol.getCellData(selectedIndex);
		if(idOfDetail != null) {
			Common.setDetailUser(idOfDetail);
			System.out.println(idOfDetail);
			try {
				root = FXMLLoader.load(getClass().getResource("/gui/AdminLobby.aDetail.fxml"));
				stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				scene = new Scene(root);
				stage.setScene(scene);
				stage.show();
			} catch (Exception e) {}
		}else {
			System.err.println("선택한 회원이 없음");
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

}
