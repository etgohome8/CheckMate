package admin;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import observableList.Gamelog;
import observableList.IndividualGameLog;

public class LobbyGameDetailController implements Initializable{
	private Parent root;
	private Stage stage;
	private Scene scene;
	private boolean isHere=true;
	@FXML
	private Button logoutButton, toAdminButton, toChatButton, toGameButton;
	@FXML
	private Button toRankButton, toServerButton, toUserButton, backButton;
    @FXML
    private TextField onlineTextField;
    @FXML
    private TextField r1Black, r2Black, r3Black;
    @FXML
    private Label r1Status, r2Status, r3Status;
    @FXML
    private TextField r1White, r2White, r3White;
    private String r1w,r2w,r3w, r1b,r2b,r3b;
    @FXML private TableView<IndividualGameLog> tableView;
    @FXML private TableColumn<IndividualGameLog, String> pCol;
    @FXML private TableColumn<IndividualGameLog, String> sCol;
    @FXML private TableColumn<IndividualGameLog, String> dCol;
    private String player,startt,destination;
    ObservableList<IndividualGameLog> log = FXCollections.observableArrayList();
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		updatePlayer();
		displayGameLog();
	}
	public void displayGameLog() {
    	onlineTextField.setText(Integer.toString(Common.getNum()));
		tableView.setPlaceholder(new Label("[조회] 버튼을 눌러주세요"));
		getDBLog();
		
		pCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPlayer()));
		sCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStartt()));
		dCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDestination()));
    	
    	tableView.setItems(log);
    	onlineTextField.setText(Integer.toString(Common.getNum()));
    	//Platform.runLater( () -> tableView.scrollTo(tableView.getItems().size()-1) );
    
	}
	public void getDBLog() {
		Connection conn = null;
		StringBuffer sql2 = new StringBuffer();
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			sql2.append("select * from game_");
			sql2.append(Common.getDetailGame());
			sql2.append(" order by imsi");
			pstmt1 = conn.prepareStatement(sql2.toString());
			rs = pstmt1.executeQuery();
			
			while(rs.next()) { // 일치하는게 있다면?
				player = rs.getString("player");
				startt = rs.getString("startt");
				destination = rs.getString("destination");
				log.add(new IndividualGameLog(player,startt,destination));
				//System.out.println(seq+"\t"+time+"\t"+id+"\t"+text);
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
								r1Status.setText("대기");
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
								r2Status.setText("대기");
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
								r3Status.setText("대기");
								r3Status.setTextFill(Color.LIGHTGRAY);
							});
						}
						try {
							sleep(1000);
						} catch (Exception e) {					}
						//Platform.runLater(()->{userListArea.setText("");});	
					} catch (Exception e) {
						System.err.println("다른방 가서 곧 스레드 종료되니 걱정 ㄴㄴ");
						e.printStackTrace();
					}
					
				}
			}
		};
		t.start();
    }
	public void checkCurrentPlayer1W() {//1번방에 있는 사람 확인
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
		}finally {// 6단계
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (ps != null) ps.close();	} catch (Exception e) {}
		}
	}
	public void checkCurrentPlayer1B() {//1번방에 있는 사람 확인
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
		}finally {// 6단계
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (ps != null) ps.close();	} catch (Exception e) {}
		}
	}
	public void checkCurrentPlayer2W() {//2번방에 있는 사람 확인
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
		}finally {// 6단계
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (ps != null) ps.close();	} catch (Exception e) {}
		}
	}
	public void checkCurrentPlayer2B() {//2번방에 있는 사람 확인
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
		}finally {// 6단계
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (ps != null) ps.close();	} catch (Exception e) {}
		}
	}
	public void checkCurrentPlayer3W() {//3번방에 있는 사람 확인
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
		}finally {// 6단계
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (ps != null) ps.close();	} catch (Exception e) {}
		}
	}
	public void checkCurrentPlayer3B() {//3번방에 있는 사람 확인
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
		}finally {// 6단계
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (ps != null) ps.close();	} catch (Exception e) {}
		}
	}
    
	  /////메뉴 이동///////
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
