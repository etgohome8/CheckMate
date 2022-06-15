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
import java.util.LinkedList;
import java.util.ResourceBundle;

import chess.database.Common;
import chess.database.ConnUtil;
import javafx.application.Platform;
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
import piece.Piece;

public class Game2Controller implements Initializable {
	private Parent root;
	private Stage stage;
	private Scene scene;
	Image img;
	private String opId, opNick;
	private int opWin, opLose;
	private double opRate;
	Socket socket;
	private boolean isAlone = true; // 혼자냐
	private String chatLog = "";
	private ListView<String> userView;
	public ObservableList<String> userId;
//	@FXML Button buttonA8, buttonB8,buttonC8,buttonD8,buttonE8, buttonF8,buttonG8,buttonH8;
//	@FXML Button buttonA7, buttonB7,buttonC7,buttonD7,buttonE7, buttonF7,buttonG7,buttonH7;
//	@FXML Button buttonA6, buttonB6,buttonC6,buttonD6,buttonE6, buttonF6,buttonG6,buttonH6;
//	@FXML Button buttonA5, buttonB5,buttonC5,buttonD5,buttonE5, buttonF5,buttonG5,buttonH5;
//	@FXML Button buttonA4, buttonB4,buttonC4,buttonD4,buttonE4, buttonF4,buttonG4,buttonH4;
//	@FXML Button buttonA3, buttonB3,buttonC3,buttonD3,buttonE3, buttonF3,buttonG3,buttonH3;
//	@FXML Button buttonA2, buttonB2,buttonC2,buttonD2,buttonE2, buttonF2,buttonG2,buttonH2;
//	@FXML Button buttonA1, buttonB1,buttonC1,buttonD1,buttonE1, buttonF1,buttonG1,buttonH1;
	@FXML Button startButton;
	
	public static LinkedList<Piece> ps = new LinkedList<>();
	Piece selectedPiece = null;
	
	Piece bPawn1,bPawn2,bPawn3,bPawn4,bPawn5,bPawn6,bPawn7,bPawn8;
	Piece wPawn1,wPawn2,wPawn3,wPawn4,wPawn5,wPawn6,wPawn7,wPawn8;
	Piece bRook1,bRook2 , wRook1,wRook2 ;
	Piece bKing , wKing;
	Piece bQueen , wQueen ;
	Piece bKnight1,bKnight2 , wKnight1,wKnight2 ;
	Piece bBishop1,bBishop2 , wBishop1,wBishop2 ;
	
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
		// 내 정보 출력
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
		Thread oppCheck = new Thread() {
			public void run() {
				while(isAlone) {
					try {
						//혼자라면 상대 정보 확인
						checkOppo();
						if(Common.getOppoId().equals("")) {
							noOppoDisplay();
							Platform.runLater(()->{
								idLabel1.setText("입장 전");
								winloseLabel1.setText("      승     패");
								nickLabel1.setText("닉네임");
								percentLabel1.setText("승률 : 0.00%");
							});
						}
						else {
							yesOppoDisplay();
							Platform.runLater(()->{
								idLabel1.setText(opId + " 님");
								winloseLabel1.setText(opWin + " 승 " + opLose + " 패");
								nickLabel1.setText("닉: "+opNick);
								
								percentLabel1.setText("승률: " + opRate + " %");
								
							});
						}
						
						//1초 마다 확인
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
		oppCheck.start();
					
		
		
	}
	//상대가 있는지 반복적으로 확인하는 메서드
	public void checkOppo() {
			Connection conn = null;
			PreparedStatement pstmt1 = null;
			ResultSet rs = null;
			String sql1 = "";
			
			try {//정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				
				sql1 = "select * from room2 where colour = ?";
				pstmt1 = conn.prepareStatement(sql1);
				//내 색의 반대인 사람이 있는지 확인하는거
				pstmt1.setString(1, Common.getMyColor().equals("white") ? "b" : "w");
				rs = pstmt1.executeQuery();
				
				if (rs.next()) { // 일치하는게 있다면 id 가져옴
					Common.setOppoId(rs.getString("id"));
				}else {
					Common.setOppoId("");
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
	public void noOppoDisplay() {
		Image img = new Image("C:\\pmProject\\javaWorkSpace\\ChessGame\\src\\image\\avatar3.png");
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
	public void yesOppoDisplay() {
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
				opId = rs.getString("id");
				opNick = rs.getString("nick");
				opWin = rs.getInt("win");
				opLose = rs.getInt("lose");
				opRate = rs.getDouble("rate");
				if (!rs.getString("image").equals("no")) {
					img = new Image(rs.getString("image"));
					
				}else {Image img = new Image("C:\\pmProject\\javaWorkSpace\\ChessGame\\src\\image\\avatar3.png");}
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
			
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
		}finally {// 6단계
			 try {if (rs != null) rs.close();	} catch (Exception e) {}
			 try {if (conn != null) conn.close();	} catch (Exception e) {}
			 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e) {}
		}
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
				if(room.equals("H")) {
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
	@FXML
	public void sendMessage(ActionEvent event) {
		// String nick = nickField.getText();
		String message = "H"+(Common.getNick().equals("닉네임") ? Common.getId() : Common.getNick()) + " : "
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
	public void disPlaytextArea(String message) {
		chatArea.appendText(message + "\n");
	}
	public void saveChat() {//채팅 디비에 저장
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
					pstmt1.setString(2, "[2번 게임방]" + chatLog);
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
	public void toLobby(ActionEvent event) {
		isAlone = false;
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
			removeFromRoom2();
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
	public void removeFromRoom2() {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		String sql1 = "";
		try {//정적 동적 쿼리 4,5단계
			conn = ConnUtil.getConnection();
			//room1에서
			sql1 = "delete from room2 where id = ? ";
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
	public void askDraw(ActionEvent event) {

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
	public void makePieces() {
		wKing = new Piece(4,7,true,"king", ps) ;
		wQueen = new Piece(3,7,true,"queen", ps) ;
		wKnight1 = new Piece(2,7,true,"knight", ps);
		wKnight2 = new Piece(6,7,true,"knight", ps);
		wBishop1 = new Piece(1,7,true,"bishop", ps);
		wBishop2 = new Piece(6,7,true,"bishop", ps);
		wRook1 = new Piece(0,7,true,"rook", ps);
		wRook2 = new Piece(7,7,true,"rook", ps);
		wPawn1 = new Piece(1,6,true,"pawn", ps);
		wPawn2 = new Piece(2,6,true,"pawn", ps);
		wPawn3 = new Piece(3,6,true,"pawn", ps);
		wPawn4 = new Piece(4,6,true,"pawn", ps);
		wPawn5 = new Piece(5,6,true,"pawn", ps);
		wPawn6 = new Piece(6,6,true,"pawn", ps);
		wPawn7 = new Piece(7,6,true,"pawn", ps);
		wPawn8 = new Piece(0,6,true,"pawn", ps);

		bKing = new Piece(4,0,false,"king", ps) ;
		bQueen = new Piece(3,0,false,"queen", ps) ;
		bKnight1 = new Piece(2,0,false,"knight", ps);
		bKnight2 = new Piece(6,0,false,"knight", ps);
		bBishop1 = new Piece(1,0,false,"bishop", ps);
		bBishop2 = new Piece(6,0,false,"bishop", ps);
		bRook1 = new Piece(0,0,false,"rook", ps);
		bRook2 = new Piece(7,0,false,"rook", ps);
		bPawn1 = new Piece(1,1,false,"pawn", ps);
		bPawn2 = new Piece(2,1,false,"pawn", ps);
		bPawn3 = new Piece(3,1,false,"pawn", ps);
		bPawn4 = new Piece(4,1,false,"pawn", ps);
		bPawn5 = new Piece(5,1,false,"pawn", ps);
		bPawn6 = new Piece(6,1,false,"pawn", ps);
		bPawn7 = new Piece(7,1,false,"pawn", ps);
		bPawn8 = new Piece(0,1,false,"pawn", ps);
		

		
		
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
	@FXML
	public void startGame(ActionEvent event){
		getPieceImage();
		
	}
	public void Hing() {
		try {
			Image wRook = new Image(getClass().getResource("/pieceImage2/whiteRook.png").toURI().toString());
			Image wKing = new Image(getClass().getResource("/pieceImage2/whiteKing.png").toURI().toString());
			Image wQueen = new Image(getClass().getResource("/pieceImage2/whiteQueen.png").toURI().toString());
			Image wKnight = new Image(getClass().getResource("/pieceImage2/whiteKnight.png").toURI().toString());
			Image wBishop = new Image(getClass().getResource("/pieceImage2/whiteBishop.png").toURI().toString());
			Image wPawn = new Image(getClass().getResource("/pieceImage2/whitePawn.png").toURI().toString());
			Image bRook = new Image(getClass().getResource("/pieceImage2/blackRook.png").toURI().toString());
			Image bKing = new Image(getClass().getResource("/pieceImage2/blackKing.png").toURI().toString());
			Image bQueen = new Image(getClass().getResource("/pieceImage2/blackQueen.png").toURI().toString());
			Image bKnight = new Image(getClass().getResource("/pieceImage2/blackKnight.png").toURI().toString());
			Image bBishop = new Image(getClass().getResource("/pieceImage2/blackBishop.png").toURI().toString());
			Image bPawn = new Image(getClass().getResource("/pieceImage2/blackPawn.png").toURI().toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		Piece brook = new Piece(0, 0, false, "rook", ps);
		Piece bkinght = new Piece(1, 0, false, "knight", ps);
		Piece bbishop = new Piece(2, 0, false, "bishop", ps);
		Piece bqueen = new Piece(3, 0, false, "queen", ps);
		Piece bking = new Piece(4, 0, false, "king", ps);
		Piece bbishop2 = new Piece(5, 0, false, "bishop", ps);
		Piece bkight2 = new Piece(6, 0, false, "knight", ps);
		Piece brook2 = new Piece(7, 0, false, "rook", ps);
		Piece bpawn1 = new Piece(1, 1, false, "pawn", ps);
		Piece bpawn2 = new Piece(2, 1, false, "pawn", ps);
		Piece bpawn3 = new Piece(3, 1, false, "pawn", ps);
		Piece bpawn4 = new Piece(4, 1, false, "pawn", ps);
		Piece bpawn5 = new Piece(5, 1, false, "pawn", ps);
		Piece bpawn6 = new Piece(6, 1, false, "pawn", ps);
		Piece bpawn7 = new Piece(7, 1, false, "pawn", ps);
		Piece bpawn8 = new Piece(0, 1, false, "pawn", ps);

		Piece wrook = new Piece(0, 7, true, "rook", ps);
		Piece wkinght = new Piece(1, 7, true, "knight", ps);
		Piece wbishop = new Piece(2, 7, true, "bishop", ps);
		Piece wqueen = new Piece(3, 7, true, "queen", ps);
		Piece wking = new Piece(4, 7, true, "king", ps);
		Piece wbishop2 = new Piece(5, 7, true, "bishop", ps);
		Piece wkight2 = new Piece(6, 7, true, "knight", ps);
		Piece wrook2 = new Piece(7, 7, true, "rook", ps);
		Piece wpawn1 = new Piece(1, 6, true, "pawn", ps);
		Piece wpawn2 = new Piece(2, 6, true, "pawn", ps);
		Piece wpawn3 = new Piece(3, 6, true, "pawn", ps);
		Piece wpawn4 = new Piece(4, 6, true, "pawn", ps);
		Piece wpawn5 = new Piece(5, 6, true, "pawn", ps);
		Piece wpawn6 = new Piece(6, 6, true, "pawn", ps);
		Piece wpawn7 = new Piece(7, 6, true, "pawn", ps);
		Piece wpawn8 = new Piece(0, 6, true, "pawn", ps);
		
		
	}
	public static Piece getPiece(int x, int y) {//x,p 는 실제 scale p
		int xp = x/64; //xp,xy는 
		int yp = y/64;
		for(Piece p: ps) {
			if(p.x==xp&&p.y==yp) {
				return p;
			}
		}
		//아무것도 없으면 널 리턴
		return null;
	}
}
