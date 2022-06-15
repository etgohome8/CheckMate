package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import chess.database.Common;
import chess.database.ConnUtil;
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
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GameController implements Initializable {
	private Parent root;
	private Stage stage;
	private Scene scene;
	Socket socket;
	private String chatLog = "";
	private ListView<String> userView;
	public ObservableList<String> userId;
	@FXML Button buttonA8, buttonB8,buttonC8,buttonD8,buttonE8, buttonF8,buttonG8,buttonH8;
	@FXML Button buttonA7, buttonB7,buttonC7,buttonD7,buttonE7, buttonF7,buttonG7,buttonH7;
	@FXML Button buttonA6, buttonB6,buttonC6,buttonD6,buttonE6, buttonF6,buttonG6,buttonH6;
	@FXML Button buttonA5, buttonB5,buttonC5,buttonD5,buttonE5, buttonF5,buttonG5,buttonH5;
	@FXML Button buttonA4, buttonB4,buttonC4,buttonD4,buttonE4, buttonF4,buttonG4,buttonH4;
	@FXML Button buttonA3, buttonB3,buttonC3,buttonD3,buttonE3, buttonF3,buttonG3,buttonH3;
	@FXML Button buttonA2, buttonB2,buttonC2,buttonD2,buttonE2, buttonF2,buttonG2,buttonH2;
	@FXML Button buttonA1, buttonB1,buttonC1,buttonD1,buttonE1, buttonF1,buttonG1,buttonH1;
	@FXML Button startButton;
	ImageView blank1, blank2 ;
	ImageView blackRook1,blackRook2 , whiteRook1,whiteRook2 ;
	ImageView blackKing , whiteKing;
	ImageView blackQueen , whiteQueen ;
	ImageView blackKnight1,blackKnight2 , whiteKnight1,whiteKnight2 ;
	ImageView blackBishop1,blackBishop2 , whiteBishop1,whiteBishop2 ;
	ImageView blackPawn1,blackPawn2,blackPawn3,blackPawn4,blackPawn5,blackPawn6,blackPawn7,blackPawn8;
	ImageView whitePawn1,whitePawn2,whitePawn3,whitePawn4,whitePawn5,whitePawn6,whitePawn7,whitePawn8;
	
	@FXML
	private TextArea chatArea;
	@FXML
	private TextField chatField;
	@FXML
	private Button drawButton;
	@FXML
	private Button outButton;
	@FXML
	private Label gameLabel;
	@FXML
	private Label gameLabel1;
	@FXML
	private Label idLabel;
	@FXML
	private Label idLabel1;
	@FXML
	private Label nickLabel;
	@FXML
	private Label nickLabel1;
	@FXML
	private Label percentLabel;
	@FXML
	private Label percentLabel1;
	@FXML
	private ImageView picImageView;
	@FXML
	private ImageView picImageView1;
	@FXML
	private Button resignButton;
	@FXML
	private Label winloseLabel;
	@FXML
	private Label winloseLabel1;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// 나
		idLabel.setText(Common.getId() + " 님");
		disPlaytextArea("[시스템] 즐겜하세요~");
		if(Common.getNick().equals("닉네임")) {//닉네임이 설정돼있지 않은 경우에는 id로 환영메세지
			nickLabel.setText("닉네임 없음");
		}else {//닉네임이 설정돼있을 경우에는 닉네임으로 환영메세지
			nickLabel.setText("닉: "+ Common.getNick());
		}
		winloseLabel.setText(Common.getWin() + " 승 " + Common.getLose() + " 패");
		double rate;
		if (Common.getWin() + Common.getLose() != 0) {
			rate = (Common.getWin() * 100) / (Common.getWin() + Common.getLose());
		} else {
			rate = 0.00;
		}
		percentLabel.setText("승률: " + rate + " %");
		startC("127.0.0.1", 7777);
		if (!Common.getimagePath().equals("no")) {
			Image img = new Image(Common.getimagePath());
			double w = 0;
			double h = 0;
			double ratioX = picImageView.getFitWidth() / img.getWidth();
			double ratioY = picImageView.getFitHeight() / img.getHeight();
			double reducCoeff = 0;
			if (ratioX >= ratioY) {
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

		/////// 상대 ///////// 
		if(Common.getMyColor().equals("white")){
			idLabel1.setText("상대" + " 님");
			winloseLabel1.setText(8 + " 승 " + 2 + " 패");
			nickLabel1.setText("닉: 체스마스터");
			double rate2;
			if (Common.getWin() + Common.getLose() != 0) {
				rate2 = (Common.getWin() * 100) / (Common.getWin() + Common.getLose());
			} else {
				rate2 = 80.00;
			}
			percentLabel1.setText("승률: " + rate + " %");
			if (!Common.getimagePath().equals("no")) {
				Image img = new Image("C:\\Users\\Owner\\Downloads\\chessAI.jpg");
				double w = 0;
				double h = 0;
				double ratioX = picImageView1.getFitWidth() / img.getWidth();
				double ratioY = picImageView1.getFitHeight() / img.getHeight();
				double reducCoeff = 0;
				if (ratioX >= ratioY) {
					reducCoeff = ratioY;
				} else {
					reducCoeff = ratioX;
				}
				w = img.getWidth() * reducCoeff;
				h = img.getHeight() * reducCoeff;
				picImageView1.setX((picImageView1.getFitWidth() - w) / 2);
				picImageView1.setY((picImageView1.getFitHeight() - h) / 2);
				picImageView1.setImage(img);
			}
		}
		// 검정색일 경우에는 로비에서 이미 w이 누군지 알 수 있어서 정보 불러올거임!!@!!@!@!@#@
		else {
			Connection conn = null;
			PreparedStatement pstmt1 = null;
			ResultSet rs = null;
			String sql1 = "";
			
			try {//정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				
				sql1 = "select * from chess_user where id = ?";
				pstmt1 = conn.prepareStatement(sql1);
				pstmt1.setString(1, Common.getOppoId());
				rs = pstmt1.executeQuery();
				
				if (rs.next()) { // 일치하는게 있다면 정보 다 가져옴
					idLabel1.setText(rs.getString("id") + " 님");
					winloseLabel1.setText(rs.getInt("win") + " 승 " + rs.getInt("lose") + " 패");
					nickLabel1.setText("닉: "+rs.getString("nick"));
					
					percentLabel1.setText("승률: " + rs.getDouble("rate") + " %");
					if (!rs.getString("image").equals("no")) {
						Image img = new Image(rs.getString("image"));
						double w = 0;
						double h = 0;
						double ratioX = picImageView1.getFitWidth() / img.getWidth();
						double ratioY = picImageView1.getFitHeight() / img.getHeight();
						double reducCoeff = 0;
						if (ratioX >= ratioY) {
							reducCoeff = ratioY;
						} else {
							reducCoeff = ratioX;
						}
						w = img.getWidth() * reducCoeff;
						h = img.getHeight() * reducCoeff;
						picImageView1.setX((picImageView1.getFitWidth() - w) / 2);
						picImageView1.setY((picImageView1.getFitHeight() - h) / 2);
						picImageView1.setImage(img);
					}
					
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
		
	}

	public void disPlaytextArea(String message) {
		chatArea.appendText(message + "\n");
	}

	public void startC(String IP, int port) {
		Thread thread = new Thread() {
			public void run() {
				try {
					socket = new Socket(IP, port);
					receive();
				} catch (UnknownHostException e) {
					e.printStackTrace();
					System.err.println("언노운 호스트");
				} catch (ConnectException e) {
					System.err.println("안 열림");
					disPlaytextArea("※ 서버가 열리지 않았습니다 ※");

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
					// displayText("※ 서버가 닫혔습니다※ \n");
					stopC();
				}
			}
		};
		thread.start();
	}

	public void receive() {
		while (true) {
			try {
				InputStream in = socket.getInputStream();
				byte[] buffer = new byte[512];
				int len = in.read(buffer);
				if (len == -1)
					throw new IOException();
				String room = new String(buffer, 0, 1);
				if(room.equals("G")) {
					String message = new String(buffer, 1, len-1, "UTF-8");
					chatArea.appendText(message);
					saveChat();
					chatLog = "중복";
				}

			} catch (Exception e) {
				stopC();
				// e.printStackTrace();
				// System.err.println("Client receive 에서");
				break;
			}
		}
	}

	public void saveChat() {
		try {
			Connection conn = null;
			PreparedStatement pstmt1 = null;
			String sql1 = null;

			try {// 정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				// chess_user 에 등록
				sql1 = "INSERT INTO chat_log(id,text) VALUES(?,?)";
				pstmt1 = conn.prepareStatement(sql1);
				if (chatLog.equals("")) {
					pstmt1.setString(1, Common.getId());
					pstmt1.setString(2, "시스템 알림");
					pstmt1.executeUpdate();
				} else if (chatLog.equals("중복")) {

				} else {
					pstmt1.setString(1, Common.getId());
					pstmt1.setString(2, "[1번 게임방]" + chatLog);
					pstmt1.executeUpdate();
				}

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
			} finally {// 6단계
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (Exception e) {
				}
				try {
					if (pstmt1 != null) {
						pstmt1.close();
					}
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
		}
	}

	public void stopC() {
		try {
			if (socket != null && !socket.isClosed()) {
				// String nick = nickField.getText();
				// type="exit";
				sendM("[" + (Common.getNick().equals("닉네임") ? Common.getId() : Common.getNick()) + "님 퇴장]\n");
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
	public void askDraw(ActionEvent event) {

	}

	@FXML
	public void toLobby(ActionEvent event) {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setWidth(400);
		alert.setX(screenBounds.getMaxX() / 2 - 200);
		alert.setY(screenBounds.getMaxY() / 2 - 50);
		alert.setResizable(false);
		alert.setTitle("방 나가기");
		alert.setHeaderText("");
		alert.setContentText("정말로 나가시겠습니까?");

		if (alert.showAndWait().get() == ButtonType.OK) {
			stopC();
			removeFromRoom1();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Lobby.fxml"));
			try {
				root = loader.load();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LobbyController lobby = loader.getController();
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.setResizable(false);

			stage.show();
		}
	}

	public void removeFromRoom1() {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql1 = "";
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			//room1에서
			sql1 = "delete from room1 where id = ? ";
			pstmt1 = conn.prepareStatement(sql1);
			pstmt1.setString(1, Common.getId());
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
	@FXML
	public void sendMessage(ActionEvent event) {
		// String nick = nickField.getText();
		String message = "G"+(Common.getNick().equals("닉네임") ? Common.getId() : Common.getNick()) + " : "
				+ chatField.getText() + "\n";
		// type ="chat";
		chatLog = chatField.getText();
		if (chatField.getText().equals("")) {
		} else {
			sendM(message);
		}
		chatField.setText("");
		chatField.requestFocus();
	}

	@FXML
	public void sendResign(ActionEvent event) {
		try {
			root = FXMLLoader.load(getClass().getResource("/gui/Lobby.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
		}
	}

	/////////////////////////////////체스판
	@FXML
	public void btA8(ActionEvent event) {
		System.out.println("A8");
	}
	
	public void getPieceImage() {
		try {
			////빈것
//			Image blankImage = new Image(getClass().getResource("/pieceImage2/zz.png").toURI().toString());
//			blank1 = new ImageView(blankImage);
//			blank2 = new ImageView(blankImage);
//			blank1.setFitWidth(25); blank1.setFitHeight(30);
//			blank2.setFitWidth(25); blank2.setFitHeight(30);
			/////black pieces
			Image bRook = new Image(getClass().getResource("/pieceImage2/blackRook.png").toURI().toString());
			Image bKing = new Image(getClass().getResource("/pieceImage2/blackKing.png").toURI().toString());
			Image bQueen = new Image(getClass().getResource("/pieceImage2/blackQueen.png").toURI().toString());
			Image bKnight = new Image(getClass().getResource("/pieceImage2/blackKnight.png").toURI().toString());
			Image bBishop = new Image(getClass().getResource("/pieceImage2/blackBishop.png").toURI().toString());
			Image bPawn = new Image(getClass().getResource("/pieceImage2/blackPawn.png").toURI().toString());
			blackRook1 = new ImageView(bRook);
			blackRook2 = new ImageView(bRook);
			blackKing = new ImageView(bKing);
			blackQueen = new ImageView(bQueen);
			blackKnight1 = new ImageView(bKnight);
			blackKnight2 = new ImageView(bKnight);
			blackBishop1 = new ImageView(bBishop);
			blackBishop2 = new ImageView(bBishop);
			blackPawn1 = new ImageView(bPawn);
			blackPawn2 = new ImageView(bPawn);
			blackPawn3 = new ImageView(bPawn);
			blackPawn4 = new ImageView(bPawn);
			blackPawn5 = new ImageView(bPawn);
			blackPawn6 = new ImageView(bPawn);
			blackPawn7 = new ImageView(bPawn);
			blackPawn8 = new ImageView(bPawn);
			blackRook1.setFitWidth(25); blackRook1.setFitHeight(30);
			blackRook2.setFitWidth(25); blackRook2.setFitHeight(30);
			blackKing.setFitWidth(25); blackKing.setFitHeight(30);
			blackQueen.setFitWidth(25); blackQueen.setFitHeight(30);
			blackKnight1.setFitWidth(25); blackKnight1.setFitHeight(30);
			blackKnight2.setFitWidth(25); blackKnight2.setFitHeight(30);
			blackBishop1.setFitWidth(25); blackBishop1.setFitHeight(30);
			blackBishop2.setFitWidth(25); blackBishop2.setFitHeight(30);
			blackPawn1.setFitWidth(25); blackPawn1.setFitHeight(30);
			blackPawn2.setFitWidth(25); blackPawn2.setFitHeight(30);
			blackPawn3.setFitWidth(25); blackPawn3.setFitHeight(30);
			blackPawn4.setFitWidth(25); blackPawn4.setFitHeight(30);
			blackPawn5.setFitWidth(25); blackPawn5.setFitHeight(30);
			blackPawn6.setFitWidth(25); blackPawn6.setFitHeight(30);
			blackPawn7.setFitWidth(25); blackPawn7.setFitHeight(30);
			blackPawn8.setFitWidth(25); blackPawn8.setFitHeight(30);
			///white pieces
			Image wRook = new Image(getClass().getResource("/pieceImage2/whiteRook.png").toURI().toString());
			Image wKing = new Image(getClass().getResource("/pieceImage2/whiteKing.png").toURI().toString());
			Image wQueen = new Image(getClass().getResource("/pieceImage2/whiteQueen.png").toURI().toString());
			Image wKnight = new Image(getClass().getResource("/pieceImage2/whiteKnight.png").toURI().toString());
			Image wBishop = new Image(getClass().getResource("/pieceImage2/whiteBishop.png").toURI().toString());
			Image wPawn = new Image(getClass().getResource("/pieceImage2/whitePawn.png").toURI().toString());
			whiteRook1 = new ImageView(wRook);
			whiteRook2 = new ImageView(wRook);
			whiteKing = new ImageView(wKing);
			whiteQueen = new ImageView(wQueen);
			whiteKnight1 = new ImageView(wKnight);
			whiteKnight2 = new ImageView(wKnight);
			whiteBishop1 = new ImageView(wBishop);
			whiteBishop2 = new ImageView(wBishop);
			whitePawn1 = new ImageView(wPawn);
			whitePawn2 = new ImageView(wPawn);
			whitePawn3 = new ImageView(wPawn);
			whitePawn4 = new ImageView(wPawn);
			whitePawn5 = new ImageView(wPawn);
			whitePawn6 = new ImageView(wPawn);
			whitePawn7 = new ImageView(wPawn);
			whitePawn8 = new ImageView(wPawn);
			whiteRook1.setFitWidth(25); whiteRook1.setFitHeight(30);
			whiteRook2.setFitWidth(25); whiteRook2.setFitHeight(30);
			whiteKing.setFitWidth(25); whiteKing.setFitHeight(30);
			whiteQueen.setFitWidth(25); whiteQueen.setFitHeight(30);
			whiteKnight1.setFitWidth(25); whiteKnight1.setFitHeight(30);
			whiteKnight2.setFitWidth(25); whiteKnight2.setFitHeight(30);
			whiteBishop1.setFitWidth(25); whiteBishop1.setFitHeight(30);
			whiteBishop2.setFitWidth(25); whiteBishop2.setFitHeight(30);
			whitePawn1.setFitWidth(25); whitePawn1.setFitHeight(30);
			whitePawn2.setFitWidth(25); whitePawn2.setFitHeight(30);
			whitePawn3.setFitWidth(25); whitePawn3.setFitHeight(30);
			whitePawn4.setFitWidth(25); whitePawn4.setFitHeight(30);
			whitePawn5.setFitWidth(25); whitePawn5.setFitHeight(30);
			whitePawn6.setFitWidth(25); whitePawn6.setFitHeight(30);
			whitePawn7.setFitWidth(25); whitePawn7.setFitHeight(30);
			whitePawn8.setFitWidth(25); whitePawn8.setFitHeight(30);
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void startBlack() {
		getPieceImage();
		buttonA8.setGraphic(whiteRook1);
		buttonB8.setGraphic(whiteKnight1);
		buttonC8.setGraphic(whiteBishop1);
		buttonD8.setGraphic(whiteQueen);
		buttonE8.setGraphic(whiteKing);
		buttonF8.setGraphic(whiteBishop2);
		buttonG8.setGraphic(whiteKnight2);
		buttonH8.setGraphic(whiteRook2);
		buttonA7.setGraphic(whitePawn1);
		buttonB7.setGraphic(whitePawn2);
		buttonC7.setGraphic(whitePawn3);
		buttonD7.setGraphic(whitePawn4);
		buttonE7.setGraphic(whitePawn5);
		buttonF7.setGraphic(whitePawn6);
		buttonG7.setGraphic(whitePawn7);
		buttonH7.setGraphic(whitePawn8);
		
		buttonA1.setGraphic(blackRook1);
		buttonB1.setGraphic(blackKnight1);
		buttonC1.setGraphic(blackBishop1);
		buttonD1.setGraphic(blackQueen);
		buttonE1.setGraphic(blackKing);
		buttonF1.setGraphic(blackBishop2);
		buttonG1.setGraphic(blackKnight2);
		buttonH1.setGraphic(blackRook2);
		buttonA2.setGraphic(blackPawn1);
		buttonB2.setGraphic(blackPawn2);
		buttonC2.setGraphic(blackPawn3);
		buttonD2.setGraphic(blackPawn4);
		buttonE2.setGraphic(blackPawn5);
		buttonF2.setGraphic(blackPawn6);
		buttonG2.setGraphic(blackPawn7);
		buttonH2.setGraphic(blackPawn8);
	}
	public void startWhite() {
		getPieceImage();
		buttonA8.setGraphic(blackRook1);
		buttonB8.setGraphic(blackKnight1);
		buttonC8.setGraphic(blackBishop1);
		buttonD8.setGraphic(blackQueen);
		buttonE8.setGraphic(blackKing);
		buttonF8.setGraphic(blackBishop2);
		buttonG8.setGraphic(blackKnight2);
		buttonH8.setGraphic(blackRook2);
		buttonA7.setGraphic(blackPawn1);
		buttonB7.setGraphic(blackPawn2);
		buttonC7.setGraphic(blackPawn3);
		buttonD7.setGraphic(blackPawn4);
		buttonE7.setGraphic(blackPawn5);
		buttonF7.setGraphic(blackPawn6);
		buttonG7.setGraphic(blackPawn7);
		buttonH7.setGraphic(blackPawn8);
		
		buttonA1.setGraphic(whiteRook1);
		buttonB1.setGraphic(whiteKnight1);
		buttonC1.setGraphic(whiteBishop1);
		buttonD1.setGraphic(whiteQueen);
		buttonE1.setGraphic(whiteKing);
		buttonF1.setGraphic(whiteBishop2);
		buttonG1.setGraphic(whiteKnight2);
		buttonH1.setGraphic(whiteRook2);
		buttonA2.setGraphic(whitePawn1);
		buttonB2.setGraphic(whitePawn2);
		buttonC2.setGraphic(whitePawn3);
		buttonD2.setGraphic(whitePawn4);
		buttonE2.setGraphic(whitePawn5);
		buttonF2.setGraphic(whitePawn6);
		buttonG2.setGraphic(whitePawn7);
		buttonH2.setGraphic(whitePawn8);
	}
	@FXML
	public void startGame(ActionEvent event){
		if(Common.getMyColor().equals("black")) {
			startBlack();
		}else if(Common.getMyColor().equals("white")) {
			startWhite();
		}
		
		
		
		
		
		
		
		
		
		
	}
	
}
