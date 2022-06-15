package application;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import chess.database.Common;
import chess.database.ConnUtil;
import javafx.application.Platform;
import javafx.collections.ObservableList;
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
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class LobbyController implements Initializable{
	private Parent root;
	private Stage stage;
	private Scene scene;
	Socket socket;
	private boolean isOn = true;
	@FXML
	private Label gameLabel;
	@FXML
	private Label room1W,room1B,room2W,room2B,room3W,room3B,room1Label,room2Label,room3Label;
	@FXML
	private Label idLabel;
	@FXML
	private Button myInfoButton, logoutButton, sendButton, gameButton, gameButton2, gameButton3;
	@FXML
	private Label nickLabel;
	@FXML
	private Label percentLabel;
	@FXML
	private ImageView picImageView;
	@FXML
	private Label winloseLabel;
	@FXML
	private TextArea textArea,userListArea;
	@FXML
	private TextField chatField,numField;
	@FXML 
	private ListView<String> userView;
	public ObservableList<String> userId;
	byte[] person_image = null;
	private String chatLog="";
	private String r1w,r2w,r3w;
	private int onlineSu;

	public void startC(int type,String IP, int port) {
    	Thread thread = new Thread() {
    		public void run() {
    			try {
					socket = new Socket(IP,port);
					receive();
				} catch (UnknownHostException e) {
					e.printStackTrace();
					System.err.println("언노운 호스트");
				} catch (ConnectException e) {
					System.err.println("안 열림");  
					disPlaytextArea("※ 서버가 열리지 않았습니다 ※");
					isOn = false;
		        	
				} catch (Exception e) {
					e.printStackTrace();
				}
    			
    		}
    	};
   		thread.start();
    }
	
	public void sendM(String message) {
		Thread thread = new Thread() {
    		public void run() {
    			try {
					OutputStream out = socket.getOutputStream();
					byte[] buffer = (message).getBytes("UTF-8");
					out.write(buffer);
					out.flush();
				} catch (Exception e) {
//					System.err.println("sendM 문제");
//					e.printStackTrace();
					//displayText("※ 서버가 닫혔습니다※ \n");
					stopC();
				}
    		}
    	};
		thread.start();
	}
	
	public void receive() {
    	while(true) {
	    	try {
				InputStream in = socket.getInputStream();
				byte[] buffer = new byte[512];
				int len = in.read(buffer);
				if(len == -1) throw new IOException();
				String room = new String(buffer, 0, 1);
				if(room.equals("L")) {
					String message = new String(buffer, 1, len-1, "UTF-8");
					textArea.appendText(message);
					//saveChat();
					chatLog = "중복";
				}
				
			} catch (Exception e) {
				stopC();
				//e.printStackTrace();
				//System.err.println("Client receive 에서");
				break;
			}
    	}
    }
	
	public void saveChat() {
		try {
			Connection conn = null;
			PreparedStatement pstmt1 = null;
			String sql1 = null;
			
			try {//정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				//chess_user 에 등록
				sql1= "INSERT INTO chat_log(id,text) VALUES(?,?)";
				pstmt1 = conn.prepareStatement(sql1);
				if(chatLog.equals("")) {
					pstmt1.setString(1, Common.getId());
					pstmt1.setString(2, "시스템 알림");
					pstmt1.executeUpdate();
					}else if (chatLog.equals("중복")){
					
				}
				else {
					pstmt1.setString(1, Common.getId());
					pstmt1.setString(2, chatLog);
					pstmt1.executeUpdate();
				}

				
			}catch (Exception e) {
				e.printStackTrace();
				System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
			}finally {// 6단계
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
		} catch (Exception e) {	}
	}
	
	public void stopC() {
		try {
			if(socket !=null && !socket.isClosed()) {
				//String nick = nickField.getText();
				//type="exit";
				sendM("["+(Common.getNick().equals("닉네임")? Common.getId() : Common.getNick())+"님 퇴장]\n");
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
		} catch (Exception e) {
			e.printStackTrace();
			System.err.print("stopC에서");
		}
	}
	
	@FXML
	public void sendMessage(ActionEvent event) {
    	//String nick = nickField.getText();
    	String message = "L"+(Common.getNick().equals("닉네임")? Common.getId() : Common.getNick()) + " : "+chatField.getText()+"\n";
    	//type ="chat";
    	chatLog = chatField.getText();
    	 if(chatField.getText().equals("")) {}
    	 else {
    		 sendM(message);
    	 }
    	 chatField.setText("");
    	chatField.requestFocus();
	}
	public void removeFromOnlineuser() {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql1 = "";
		StringBuffer sql2 = new StringBuffer();
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			//chess_user 에서 삭제
			sql1 = "delete from onlineuser where id = ? ";
			pstmt1 = conn.prepareStatement(sql1);
			pstmt1.setString(1, Common.getId());
			rs = pstmt1.executeQuery();
			// id가 id 인 정보에서 pw 랑 nick을 변경하는 sql 기릿 
			
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
	public void logOut(ActionEvent event) throws IOException {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		Alert alert= new Alert(AlertType.CONFIRMATION);
		alert.setWidth(400);
		alert.setX(screenBounds.getMaxX()/2 -200);
		alert.setY(screenBounds.getMaxY()/2 -50);
		alert.setResizable(false);
		alert.setTitle("로그아웃");
		alert.setHeaderText("");
		alert.setContentText("정말로 로그아웃하시겠습니까?");
		
		if(alert.showAndWait().get() == ButtonType.OK) {
			stopC();
			//이니셜라이즈의 쓰레드 종료
	    	isOn=false;
			removeFromOnlineuser();
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
	@FXML
	public void toGame(ActionEvent event){
		//room1 table에 입력
		Connection conn = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql1 = "";
		
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			sql1 = "insert into room1(id,colour) values(?,'w')";
			pstmt1 = conn.prepareStatement(sql1);
			pstmt1.setString(1, Common.getId());
			rs = pstmt1.executeQuery();
			Common.setMyColor("white");
			try {
				//방에 b가 이미 있는 경우 그사람이 내 상대 
				if(!room1B.getText().equals("")) {
					Common.setOppoId(room1B.getText());
				}
				//완전 비어있는 방
				else {
					Common.setOppoId("");
				}
				stopC();
				//이니셜라이즈의 쓰레드 종료
		    	isOn=false;
				root = FXMLLoader.load(getClass().getResource("/gui/Game.fxml"));
				stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				scene = new Scene(root);
				stage.setScene(scene);
				stage.show();
			}catch (Exception e) {}
		}catch (SQLIntegrityConstraintViolationException e){//w가 이미 있는 경우!
			try {
				conn = ConnUtil.getConnection();
				sql1 = "insert into room1(id,colour) values(?,'b')";
				pstmt1 = conn.prepareStatement(sql1);
				pstmt1.setString(1, Common.getId());
				rs = pstmt1.executeQuery();
				Common.setMyColor("black");
				try {
					if(!room1W.getText().equals("")) {
						Common.setOppoId(room1W.getText());
						System.out.println("이건데");
					}
					else {
						Common.setOppoId(r1w);
					}
					stopC();
					//이니셜라이즈의 쓰레드 종료
			    	isOn=false;
					root = FXMLLoader.load(getClass().getResource("/gui/Game.fxml"));
					stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
					scene = new Scene(root);
					stage.setScene(scene);
					stage.show();
				}catch (Exception e2) {}
				finally {// 6단계
					 try {if (rs != null) rs.close();	} catch (Exception e2) {}
					 try {if (conn != null) conn.close();	} catch (Exception e2) {}
					 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e2) {}
				}
			} catch (Exception e2) {
				System.err.println("다차싸");
				showFull();
			}finally {// 6단계
				 try {if (rs != null) rs.close();	} catch (Exception e2) {}
				 try {if (conn != null) conn.close();	} catch (Exception e2) {}
				 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e2) {}
			}
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당"
					+ "\n사람이 다 참");
		}finally {// 6단계
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e) {}
		}
	}
	@FXML
	public void toGame2(ActionEvent event){
		stopC();
		//room1 table에 입력
		Connection conn = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql1 = "";
		
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			sql1 = "insert into room2(id,colour) values(?,'w')";
			pstmt1 = conn.prepareStatement(sql1);
			pstmt1.setString(1, Common.getId());
			rs = pstmt1.executeQuery();
			Common.setMyColor("white");
			try {
				//방에 b가 이미 있는 경우 그사람이 내 상대 
				if(!room2B.getText().equals("")) {
					Common.setOppoId(room2B.getText());
				}
				else {
					Common.setOppoId("");
				}
				stopC();
				//이니셜라이즈의 쓰레드 종료
		    	isOn=false;
				root = FXMLLoader.load(getClass().getResource("/gui/GameR2.fxml"));
				stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				scene = new Scene(root);
				stage.setScene(scene);
				stage.show();
			}catch (Exception e) {}
		}catch (SQLIntegrityConstraintViolationException e){//w가 이미 있는 경우!
			try {
				conn = ConnUtil.getConnection();
				sql1 = "insert into room2(id,colour) values(?,'b')";
				pstmt1 = conn.prepareStatement(sql1);
				pstmt1.setString(1, Common.getId());
				rs = pstmt1.executeQuery();
				Common.setMyColor("black");
				try {
					if(!room2W.getText().equals("")) {
						Common.setOppoId(room2W.getText());
						System.out.println("이건데");
					}
					else {
						checkCurrentPlayer2W();
						Common.setOppoId(r2w);
					}
					stopC();
					//이니셜라이즈의 쓰레드 종료
			    	isOn=false;
					root = FXMLLoader.load(getClass().getResource("/gui/Game2.fxml"));
					stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
					scene = new Scene(root);
					stage.setScene(scene);
					stage.show();
				}catch (Exception e2) {}
				finally {// 6단계
					 try {if (rs != null) rs.close();	} catch (Exception e2) {}
					 try {if (conn != null) conn.close();	} catch (Exception e2) {}
					 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e2) {}
				}
			} catch (Exception e2) {
				System.err.println("다차싸");
				showFull();
			}finally {// 6단계
				 try {if (rs != null) rs.close();	} catch (Exception e2) {}
				 try {if (conn != null) conn.close();	} catch (Exception e2) {}
				 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e2) {}
			}
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당"
					+ "\n사람이 다 참");
		}finally {// 6단계
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e) {}
		}
	}
	@FXML
	public void toGame3(ActionEvent event){
		//room1 table에 입력
		Connection conn = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql1 = "";
		
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			sql1 = "insert into room3(id,colour) values(?,'w')";
			pstmt1 = conn.prepareStatement(sql1);
			pstmt1.setString(1, Common.getId());
			rs = pstmt1.executeQuery();
			Common.setMyColor("white");
			try {
				stopC();
				//이니셜라이즈의 쓰레드 종료
		    	isOn=false;
				root = FXMLLoader.load(getClass().getResource("/gui/Game3.fxml"));
				stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				scene = new Scene(root);
				stage.setScene(scene);
				stage.show();
			}catch (Exception e) {}
		}catch (SQLIntegrityConstraintViolationException e){//w가 이미 있는 경우!
			try {
				conn = ConnUtil.getConnection();
				sql1 = "insert into room3(id,colour) values(?,'b')";
				pstmt1 = conn.prepareStatement(sql1);
				pstmt1.setString(1, Common.getId());
				rs = pstmt1.executeQuery();
				Common.setMyColor("black");
				try {
					stopC();
					//이니셜라이즈의 쓰레드 종료
			    	isOn=false;
					root = FXMLLoader.load(getClass().getResource("/gui/Game3.fxml"));
					stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
					scene = new Scene(root);
					stage.setScene(scene);
					stage.show();
				}catch (Exception e2) {}
				finally {// 6단계
					 try {if (rs != null) rs.close();	} catch (Exception e2) {}
					 try {if (conn != null) conn.close();	} catch (Exception e2) {}
					 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e2) {}
				}
			} catch (Exception e2) {
				System.err.println("다차싸");
				showFull();
			}finally {// 6단계
				 try {if (rs != null) rs.close();	} catch (Exception e2) {}
				 try {if (conn != null) conn.close();	} catch (Exception e2) {}
				 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e2) {}
			}
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당"
					+ "\n사람이 다 참");
		}finally {// 6단계
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e) {}
		}
		
    	
	}
    @FXML
    public void toMyInfo(ActionEvent event) throws IOException {
    	//이니셜라이즈의 쓰레드 종료
    	isOn=false;
    	//DBCheck();
    	//다른데 갔다오면 채팅이 끊임.. 다른데 가면 연결 끊고 다시 하는 식으로..ㅠ
    	stopC();
    	FXMLLoader infoLoader = new FXMLLoader(getClass().getResource("/gui/MyInfo.fxml"));
		root = infoLoader.load();
		MyInfoController info = infoLoader.getController();
		//info.getFromLobby(ID,PW,NICK,GENDER,GAME,WIN,LOSE);
		
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
    }
	public void disPlaytextArea(String message) {
		textArea.appendText(message+"\n");
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
				Common.setRoom1white(rs.getString("id"));
				r1w = rs.getString("id");
				
			}else {
				Common.setRoom1white("");
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
				Common.setRoom1black(rs.getString("id"));
			}else {
				Common.setRoom1black("");
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
				Common.setRoom2white(rs.getString("id"));
				r2w = rs.getString("id");
			}else {
				Common.setRoom2white("");
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
				Common.setRoom2black(rs.getString("id"));
			}else {
				Common.setRoom2black("");
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
				Common.setRoom3white(rs.getString("id"));
				r3w = rs.getString("id");
			}else {
				Common.setRoom3white("");
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
				Common.setRoom3black(rs.getString("id"));
			}else {
				Common.setRoom3black("");
			}
		} catch (Exception e) {
		}finally {// 6단계
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (ps != null) ps.close();	} catch (Exception e) {}
		}
	}
	public void onLineList() {//온라인 유저를 계속 표기하는 메서드
		Connection conn = null;
		String sql1 = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			
			sql1 = "select * from onlineuser order by aaa";
			pstmt1 = conn.prepareStatement(sql1);
			rs = pstmt1.executeQuery();
			onlineSu = 0;
			StringBuffer sb = new StringBuffer();
			while(rs.next()) {
				//StringBuffer sb = new StringBuffer();
				String buing = rs.getString("nick");
				sb.append(buing+"\n");
				onlineSu++;
			}Platform.runLater(()->{userListArea.setText(sb.toString());});
			
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
		}finally {// 6단계
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e) {}
		}
		//if(!Common.isServerOn()) {break;}
	}
	public void inini() {
		idLabel.setText(Common.getId() + " 님");
		if(Common.getNick().equals("닉네임")) {//닉네임이 설정돼있지 않은 경우에는 id로 환영메세지
			nickLabel.setText("닉네임을 설정하세요");
			disPlaytextArea("[시스템] "+Common.getId()+"님 체스마테 로비입니다.");
		}else {//닉네임이 설정돼있을 경우에는 닉네임으로 환영메세지
			nickLabel.setText("닉: "+ Common.getNick());
			disPlaytextArea("[시스템] "+Common.getNick()+"님 체스마테 로비입니다.");
		}
		winloseLabel.setText(Common.getWin() + " 승 " + Common.getLose() + " 패");
		double rate;
		if(Common.getWin()+Common.getLose() !=0) {
			rate = (Common.getWin()*100)/(Common.getWin()+Common.getLose());
		}else {rate = 0.00;}
		percentLabel.setText("승률: "+ rate + " %");
		startC(2,"127.0.0.1",7777);
		if(!Common.getimagePath().equals("no")) {
			Image img = new Image(Common.getimagePath());
			
			double w = 0;
            double h = 0;

            double ratioX = picImageView.getFitWidth() / img.getWidth();
            double ratioY = picImageView.getFitHeight() / img.getHeight();

            double reducCoeff = 0;
            if(ratioX >= ratioY) {
                reducCoeff = ratioY;
            } else {
                reducCoeff = ratioX;
            }

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            picImageView.setX((picImageView.getFitWidth() - w) / 2);
            picImageView.setY((picImageView.getFitHeight() - h) / 2);
            picImageView.setImage(img);
		}
		Thread t = new Thread() {
			public void run() {
				while(isOn) {
					try {
						checkCurrentPlayer1W();
						checkCurrentPlayer1B();
						checkCurrentPlayer2W();
						checkCurrentPlayer2B();
						checkCurrentPlayer3W();
						checkCurrentPlayer3B();
						Platform.runLater( ()->{	
							room1W.setText(Common.getRoom1white());
							room1B.setText(Common.getRoom1black());
							room2W.setText(Common.getRoom2white());
							room2B.setText(Common.getRoom2black());
							room3W.setText(Common.getRoom3white());
							room3B.setText(Common.getRoom3black());
						});
						if(!room1W.getText().equals("") && !room1B.getText().equals("")) {
							Platform.runLater( ()->{	room1Label.setText("FULL");});
						}else {Platform.runLater( ()->{	room1Label.setText("");});}
						if(!room2W.getText().equals("") && !room2B.getText().equals("")) {
							Platform.runLater( ()->{	room2Label.setText("FULL");});
						}else {Platform.runLater( ()->{	room2Label.setText("");});}
						if(!room3W.getText().equals("") && !room3B.getText().equals("")) {
							Platform.runLater( ()->{	room3Label.setText("FULL");});
						}else {Platform.runLater( ()->{	room3Label.setText("");});}
						onLineList();
						Platform.runLater(()->{numField.setText(Integer.toString(onlineSu));});	
						try {
							sleep(1000);
						} catch (Exception e) {					}
						//Platform.runLater(()->{userListArea.setText("");});	
					} catch (Exception e) {
						System.err.println("다른방 가서 곧 스레드 종료되니 걱정 ㄴㄴ");
					}
					
				}
			}
		};
		t.start();
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		inini();
	}
	
	public void upload(File file) {
		try {
            BufferedImage bufferedImage = ImageIO.read(file);
            WritableImage image = SwingFXUtils.toFXImage(bufferedImage, null);
            picImageView.setImage(image);
            picImageView.setFitWidth(81);
            picImageView.setFitHeight(108);
            picImageView.scaleXProperty();
            picImageView.scaleYProperty();
            picImageView.setSmooth(true);
            picImageView.setCache(true);
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
	public void showFull() {
		try {
			Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    		Dialog<String> permissionDialog;	
    		permissionDialog = new Dialog<String>();
    		permissionDialog.setTitle("정원초과");
    		Stage stage2 = (Stage)permissionDialog.getDialogPane().getScene().getWindow();
    		stage2.getIcons().add(new Image(getClass().getResource("/image/Chess_Icon_cut.png").toURI().toString()));
    		ButtonType aboutButton = new ButtonType("확인",ButtonData.OK_DONE);
    		permissionDialog.setContentText
    				("정원 초과된 방입니다.\n"
    						+ "다른 방을 선택하세요");
    		permissionDialog.getDialogPane().getButtonTypes().add(aboutButton);
    		permissionDialog.setResizable(false);
    		permissionDialog.setX(screenBounds.getMaxX()/2 - 180);
    		permissionDialog.setY(screenBounds.getMaxY()/2 - 100);
    		permissionDialog.showAndWait();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
