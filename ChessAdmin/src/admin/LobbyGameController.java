package admin;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ResourceBundle;

import chess.database.Common;
import chess.database.ConnUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import observableList.ChatLog;
import observableList.Gamelog;

public class LobbyGameController implements Initializable{
	private Parent root;
	private Stage stage;
	private Scene scene;
	@FXML
	private Button logoutButton, toAdminButton, toChatButton, toGameButton;
	@FXML
	private Button toRankButton, toServerButton, toUserButton;
	@FXML private Button detailButton;
    @FXML
    private TextField onlineTextField;
    @FXML
    private TextField r1Black, r2Black, r3Black;
    @FXML
    private TextField r1White, r2White, r3White;
    private String r1w,r2w,r3w, r1b,r2b,r3b;
    @FXML
    private Label r1Status, r2Status, r3Status;
    @FXML
    private Button showAllButton;
    @FXML
    private Button showButton;
    @FXML private TableView<Gamelog> tableView;
    @FXML private TableColumn<Gamelog, String> noCol;
    @FXML private TableColumn<Gamelog, String> wCol;
    @FXML private TableColumn<Gamelog, String> bCol;
    @FXML private TableColumn<Gamelog, String> winCol;
    @FXML private TableColumn<Gamelog, String> dCol;
    private String sql="";
    ObservableList<Gamelog> log = FXCollections.observableArrayList();
    private int seq;
    private String white,black,win,date;
    private boolean isHere=Common.isServerOn();
    @Override
	public void initialize(URL location, ResourceBundle arg1) {
    	tableView.setPlaceholder(new Label("���õ� ��¥�� �÷����� ������ �����ϴ�."));
    	sql = "select * from game_log order by game_no";
    	displayGameLog();
    	updatePlayer();
    }
    
    public void updatePlayer() {
    	Thread t = new Thread() {
			public void run() {
				while(isHere) {
					try {
						
						checkCurrentPlayer1W();
						checkCurrentPlayer1B();
						checkCurrentPlayer2W();
						checkCurrentPlayer2B();
						checkCurrentPlayer3W();
						checkCurrentPlayer3B();
						/*
						    private TextField r1Black, r2Black, r3Black;
						    private TextField r1White, r2White, r3White;
						    private String r1w,r2w,r3w, r1b,r2b,r3b;
						    private Label r1Status, r2Status, r3Status;
						 */
						Platform.runLater( ()->{	
							r1White.setText(r1w);
							r1Black.setText(r1b);
							r2White.setText(r2w);
							r2Black.setText(r2b);
							r3White.setText(r3w);
							r3Black.setText(r3b);
						});
						Platform.runLater(()->{onlineTextField.setText(Integer.toString(Common.getNum()));});
						if (!r1White.getText().equals("") && !r1Black.getText().equals("")) {
							Platform.runLater(() -> {
								r1Status.setText("Playing");
								r1Status.setTextFill(Color.BLUE);
							});
						} else {
							Platform.runLater(() -> {
								r1Status.setText("���");
								r1Status.setTextFill(Color.LIGHTGRAY);
							});
						}
						if (!r2White.getText().equals("") && !r2Black.getText().equals("")) {
							Platform.runLater(() -> {
								r2Status.setText("Playing");
								r2Status.setTextFill(Color.BLUE);
							});
						} else {
							Platform.runLater(() -> {
								r2Status.setText("���");
								r2Status.setTextFill(Color.LIGHTGRAY);
							});
						}
						if (!r3White.getText().equals("") && !r3Black.getText().equals("")) {
							Platform.runLater(() -> {
								r3Status.setText("Playing");
								r3Status.setTextFill(Color.BLUE);
							});
						} else {
							Platform.runLater(() -> {
								r3Status.setText("���");
								r3Status.setTextFill(Color.LIGHTGRAY);
							});
						}
						try {
							sleep(1000);
						} catch (Exception e) {					}
						//Platform.runLater(()->{userListArea.setText("");});	
					} catch (Exception e) {
						System.err.println("�ٸ��� ���� �� ������ ����Ǵ� ���� ����");
						e.printStackTrace();
					}
					
				}
			}
		};
		t.start();
    }
    
	public void checkCurrentPlayer1W() {//1���濡 �ִ� ��� Ȯ��
		//System.out.println("checkCurrentPlayer1W\n" + Common.getRoom1white());
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			conn = ConnUtil.getConnection();
			
			sql = "select * from room1 where colour = 'w'";
			ps= conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				r1w = rs.getString("id");
				
			}else {
				r1w="";
			}
		} catch (Exception e) {
		}finally {// 6�ܰ�
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (ps != null) ps.close();	} catch (Exception e) {}
		}
	}
	public void checkCurrentPlayer1B() {//1���濡 �ִ� ��� Ȯ��
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			conn = ConnUtil.getConnection();
			
			sql = "select * from room1 where colour = 'b'";
			ps= conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				r1b = rs.getString("id");
				
			}else {
				r1b="";
			}
		} catch (Exception e) {
		}finally {// 6�ܰ�
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (ps != null) ps.close();	} catch (Exception e) {}
		}
	}
	public void checkCurrentPlayer2W() {//2���濡 �ִ� ��� Ȯ��
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			conn = ConnUtil.getConnection();
			
			sql = "select * from room2 where colour = 'w'";
			ps= conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				r2w = rs.getString("id");
			}else {
				r2w="";
			}
		} catch (Exception e) {
		}finally {// 6�ܰ�
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (ps != null) ps.close();	} catch (Exception e) {}
		}
	}
	public void checkCurrentPlayer2B() {//2���濡 �ִ� ��� Ȯ��
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			conn = ConnUtil.getConnection();
			
			sql = "select * from room2 where colour = 'b'";
			ps= conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				r2b = rs.getString("id");
			}else {
				r2b="";
			}
		} catch (Exception e) {
		}finally {// 6�ܰ�
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (ps != null) ps.close();	} catch (Exception e) {}
		}
	}
	public void checkCurrentPlayer3W() {//3���濡 �ִ� ��� Ȯ��
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			conn = ConnUtil.getConnection();
			
			sql = "select * from room3 where colour = 'w'";
			ps= conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				r3w = rs.getString("id");
				
			}else {
				r3w="";
			}
		} catch (Exception e) {
		}finally {// 6�ܰ�
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (ps != null) ps.close();	} catch (Exception e) {}
		}
	}
	public void checkCurrentPlayer3B() {//3���濡 �ִ� ��� Ȯ��
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			conn = ConnUtil.getConnection();
			
			sql = "select * from room3 where colour = 'b'";
			ps= conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				r3b = rs.getString("id");
			}else {
				r3b="";
			}
		} catch (Exception e) {
		}finally {// 6�ܰ�
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (ps != null) ps.close();	} catch (Exception e) {}
		}
	}
    ///
	public void displayGameLog() {
    	onlineTextField.setText(Integer.toString(Common.getNum()));
		tableView.setPlaceholder(new Label("[��ȸ] ��ư�� �����ּ���"));
		getDBLog();
		
		noCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSeq()));
		wCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getWhite()));
		bCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBlack()));
		winCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getWin()));
		dCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate()));
    	
    	tableView.setItems(log);
    	onlineTextField.setText(Integer.toString(Common.getNum()));
    	Platform.runLater( () -> tableView.scrollTo(tableView.getItems().size()-1) );
    
	}
	public void disOnline() {
		Thread thread = new Thread() {
			public void run() {
				while(isHere) {
					Platform.runLater(()->{onlineTextField.setText(Integer.toString(Common.getNum()));});
					try {
						sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
	}
	public void clearTable() {
		tableView.getItems().clear();
	}
	public void disPlay(ActionEvent event) {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		Dialog<ButtonType> chooseDate = new Dialog<ButtonType>();
		DatePicker datePicker = new DatePicker();
		datePicker.setValue(LocalDate.now());
		chooseDate.getDialogPane().setContent(datePicker);
		chooseDate.getDialogPane().setPadding(new Insets(20));
		ButtonType okayButton = new ButtonType("��ȸ", ButtonData.OK_DONE);
		ButtonType cancelButton = new ButtonType("���", ButtonData.CANCEL_CLOSE);
		chooseDate.getDialogPane().getButtonTypes().add(okayButton);
		//chooseDate.getDialogPane().getButtonTypes().add(cancelButton);
		chooseDate.setX(screenBounds.getMaxX() / 2 - 150);
		chooseDate.setY(screenBounds.getMaxY() / 2 - 50);
		chooseDate.setResizable(false);
		chooseDate.setTitle("��¥����");
		
		chooseDate.showAndWait().ifPresent(response -> {
			String selectedDate = datePicker.getValue().toString();
			//System.out.println(selectedDate);
			clearTable();
			sql = "select * from game_log where (to_char(datee,'rrrr-mm-dd')='"+ selectedDate +"') order by game_no";
			displayGameLog(); 
						
		});
	}
	public void getDBLog() {
		Connection conn = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		
		try {//���� ���� ���� 4,5�ܰ�
			conn = ConnUtil.getConnection();
			pstmt1 = conn.prepareStatement(sql);
			rs = pstmt1.executeQuery();
			
			while(rs.next()) { // ��ġ�ϴ°� �ִٸ�?
				seq = rs.getInt("game_no");
				white = rs.getString("white");
				black = rs.getString("black");
				win = rs.getString("win");
				date = rs.getString("datee");
				log.add(new Gamelog(seq,white,black,win,date));
				//System.out.println(seq+"\t"+time+"\t"+id+"\t"+text);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("������ ������ Ȯ���� ���� ����� ����");
		}finally {// 6�ܰ�
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e) {}
		}
	}
	@FXML
	public void showDetail(ActionEvent event) {
		int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
		String seqOfDetail = noCol.getCellData(selectedIndex);
		if(seqOfDetail != null) {
			Common.setDetailGame(seqOfDetail);
			try {
				isHere = false;
				root = FXMLLoader.load(getClass().getResource("/gui/AdminLobby.bDetail.fxml"));
				stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				scene = new Scene(root);
				stage.setScene(scene);
				stage.show();
			} catch (Exception e) {}
		}else {
			System.err.println("������ ������ ����");
		}
	}
    public void showAll(ActionEvent event) {
    	clearTable();
    	sql = "select * from game_log order by game_no";
    	displayGameLog(); 
    }
    
    /////�޴� �̵�///////
    @FXML
	public void switchtoLogin(ActionEvent event) throws IOException {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setWidth(400);
		alert.setX(screenBounds.getMaxX() / 2 - 200);
		alert.setY(screenBounds.getMaxY() / 2 - 50);
		alert.setResizable(false);
		alert.setTitle("�α׾ƿ�");
		alert.setHeaderText("");
		alert.setContentText("������ �α׾ƿ��Ͻðڽ��ϱ�?");

		if (alert.showAndWait().get() == ButtonType.OK) {
			isHere = false;
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
			isHere = false;
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
    		isHere = false;
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
    		isHere = false;
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
    		isHere = false;
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
    		isHere = false;
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
    		isHere = false;
			root = FXMLLoader.load(getClass().getResource("/gui/AdminLobby.fAdmin.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		}catch (Exception e) {}
    }
}
