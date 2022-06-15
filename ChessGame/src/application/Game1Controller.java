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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Game1Controller implements Initializable {
	private Parent root;
	private Stage stage;
	private Scene scene;
	private Socket socket;
	private boolean myTurn;
	private boolean isAlone = true; // 혼자냐
	private String opId, opNick;
	private int opWin, opLose;
	private int pick=0; //주울때 0, 내릴떄 1
	Node node;
	ImageView copy;
	Image img;
	private double opRate;
	private String chatLog = "";
	public ObservableList<String> userId;
	@FXML Button A8, B8, C8, D8, E8, F8, G8, H8;
	@FXML Button A7, B7, C7, D7, E7, F7, G7, H7;
	@FXML Button A6, B6, C6, D6, E6, F6, G6, H6;
	@FXML Button A5, B5, C5, D5, E5, F5, G5, H5;
	@FXML Button A4, B4, C4, D4, E4, F4, G4, H4;
	@FXML Button A3, B3, C3, D3, E3, F3, G3, H3;
	@FXML Button A2, B2, C2, D2, E2, F2, G2, H2;
	@FXML Button A1, B1, C1, D1, E1, F1, G1, H1;
	@FXML ImageView colorImage, myPoro0,myPoro1,myPoro2,myPoro3,myPoro4,myPoro5,myPoro6,myPoro7,myPoro8,myPoro9,myPoro10;
	@FXML ImageView colorImage1,Oppo0,Oppo2,Oppo3,Oppo1,Oppo4,Oppo5,Oppo6,Oppo7,Oppo8,Oppo9,Oppo10;
	@FXML Button startButton;
	Image blankImage;
	Image bRook, bKing,bPawn,bQueen,bKnight,bBishop;
	Image wRook, wKing,wPawn,wQueen,wKnight,wBishop;
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
	private TextField chatField,readyStatus;
	@FXML
	private Button drawButton;
	@FXML
	private Button outButton;
	@FXML
	private Label gameLabel,gameLabel1;
	@FXML
	private Label winLabel,loseLabel,drawLabel;
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
	private Label winloseLabel;
	@FXML
	private Label winloseLabel1;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		displayMyInfo();
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
								reset();
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
						try {sleep(1000);} catch (Exception e) {}
						//Platform.runLater(()->{userListArea.setText("");});	
					} catch (Exception e) {
						System.err.println("다른방 가서 곧 스레드 종료되니 걱정 ㄴㄴ");
					}
				}
			}
		};
		oppCheck.start();
	}
	public void displayMyInfo() {
		startButton.setText(Common.getMyColor().equals("white")? "시작" : "준비");
		if(startButton.getText().equals("시작")) {
			startButton.setDisable(true);
		}
		// 내 정보 출력
		idLabel.setText(Common.getId() + " 님");
		disPlaytextArea("[시스템] 즐겜하세요~");
		if(Common.getNick().equals("닉네임")) {//닉네임이 설정돼있지 않은 경우에는 id로 환영메세지
			nickLabel.setText("닉네임 없음");
		}else {//닉네임이 설정돼있을 경우에는 닉네임으로 환영메세지
			nickLabel.setText("닉: "+ Common.getNick());
		}
		winloseLabel.setText(Common.getWin() + " 승 " + Common.getLose() + " 패");
		percentLabel.setText("승률: " + Common.getRate() + " %");
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
	}
	//상대가 있는지 반복적으로 확인하는 메서드
	public void checkOppo() {
			Connection conn = null;
			PreparedStatement pstmt1 = null;
			ResultSet rs = null;
			String sql1 = "";
			
			try {//정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				
				sql1 = "select * from room1 where colour = ?";
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
		//이거는 일단 절대경로로했습니다.. 급해서
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
				String ready = new String(buffer,0,3);
				if(room.equals("G")) {//메세지 거름
					String message = new String(buffer, 1, len-1, "UTF-8");
					chatArea.appendText(message);
					//saveChat();
					chatLog = "중복";
				}
				String tur = new String(buffer, 0, len, "UTF-8");
				String[] turn = tur.split("=");
				
				if(turn.length==3) {
					if(turn[2].equals("TR1")) {
					myTurn =! myTurn;
					}
				}
				
				if(ready.equals("RR1")) {//준비됐다고 신호를 보냄
					Platform.runLater(()->{reset();});
					if(Common.getMyColor().equals("white")) {
						readyStatus.setText("준비");
						startButton.setDisable(false);
					}
				}else if(ready.equals("SR1")) {//시작을 누름
					readyStatus.setText("시작");
					if(Common.getMyColor().equals("black")) {
						//startG();
						Platform.runLater(()->{
							startG();
							myTurn = false;
							startButton.setDisable(false);
							startButton.setText("기권");
						});
					
					}
				}else if(ready.equals("RE1")) {//누군가 기권을 누름
					if(readyStatus.getText().equals("승")) {//기권을 누른 사람
						loseLabel.setVisible(true);
						chatArea.appendText("[시스템] 기권하셨습니다.\n");
						//Common.setLose(Common.getLose()+1);
						Platform.runLater(()->{displayMyInfo();
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}displayMyInfo();
						});
					}else {//이긴 사람
						//Common.setWin(Common.getWin()+1);
						winLabel.setVisible(true);
						chatArea.appendText("[시스템] 상대가 기권했습니다.\n");
						Platform.runLater(()->{displayMyInfo();
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}displayMyInfo();
						});
					}
					
					if(Common.getMyColor().equals("white")) {//
						Platform.runLater(()->{
							myTurn = true;
							readyStatus.setText("");
							startButton.setDisable(true);
							startButton.setText("시작");
							colorImage.setFitWidth(25);colorImage.setFitHeight(30);colorImage.setImage(wKing);
							colorImage1.setFitWidth(25);colorImage1.setFitHeight(30);colorImage1.setImage(bKing);
						});
					}else {
						Platform.runLater(()->{
							myTurn = false;
							readyStatus.setText("");
							startButton.setDisable(false);
							startButton.setText("준비");
							colorImage.setFitWidth(25);colorImage.setFitHeight(30);colorImage.setImage(bKing);
							colorImage1.setFitWidth(25);colorImage1.setFitHeight(30);colorImage1.setImage(wKing);
						});
					}
				}else if(ready.equals("WW1")) {//이겼을 때
					if(!readyStatus.getText().equals("  ")) {//진 사람
						//Common.setLose(Common.getLose()+1);
						loseLabel.setVisible(true);
						chatArea.appendText("[시스템] 왕이 죽었습니다...\n");
						Platform.runLater(()->{displayMyInfo();
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}displayMyInfo();
						});
					}else {//이긴 사람
						//Common.setWin(Common.getWin()+1);
						winLabel.setVisible(true);
						chatArea.appendText("[시스템] 이겼습니다\n");
						Platform.runLater(()->{displayMyInfo();
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}displayMyInfo();
						});
					}
					
					if(Common.getMyColor().equals("white")) {//
						Platform.runLater(()->{
							myTurn = true;
							readyStatus.setText("");
							startButton.setDisable(true);
							startButton.setText("시작");
							colorImage.setFitWidth(25);colorImage.setFitHeight(30);colorImage.setImage(wKing);
							colorImage1.setFitWidth(25);colorImage1.setFitHeight(30);colorImage1.setImage(bKing);
						});
					}else {
						Platform.runLater(()->{
							myTurn = false;
							readyStatus.setText("");
							startButton.setDisable(false);
							startButton.setText("준비");
							colorImage.setFitWidth(25);colorImage.setFitHeight(30);colorImage.setImage(bKing);
							colorImage1.setFitWidth(25);colorImage1.setFitHeight(30);colorImage1.setImage(wKing);
						});
					}
					
					
				}else if(ready.equals("AD1")) {//무르기 신청
					if(readyStatus.getText().equals("신청")) {//신청한 사람
						chatArea.appendText("상대에게 요청을 보냈습니다.\n");
					}else {//신청을 받는 사람
						Platform.runLater(()->{
							chatArea.appendText("상대가 무승 요청을 보냈습니다.\n");
							askD();
						});
					}
					
				}else if(ready.equals("DW1")) {//무르기 신청
					Platform.runLater(()->{
						drawLabel.setVisible(true);
						Platform.runLater(()->{displayMyInfo();
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}displayMyInfo();
						});
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}displayMyInfo();
						});
					
					if(Common.getMyColor().equals("white")) {//
						Platform.runLater(()->{
							myTurn = true;
							readyStatus.setText("");
							startButton.setDisable(true);
							startButton.setText("시작");
							colorImage.setFitWidth(25);colorImage.setFitHeight(30);colorImage.setImage(wKing);
							colorImage1.setFitWidth(25);colorImage1.setFitHeight(30);colorImage1.setImage(bKing);
						});
					}else {
						Platform.runLater(()->{
							myTurn = false;
							readyStatus.setText("");
							startButton.setDisable(false);
							startButton.setText("준비");
							colorImage.setFitWidth(25);colorImage.setFitHeight(30);colorImage.setImage(bKing);
							colorImage1.setFitWidth(25);colorImage1.setFitHeight(30);colorImage1.setImage(wKing);
						});
					}
					
				}else if(ready.equals("TR1")) {
					myTurn =! myTurn;
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
	public void disPlaytextArea(String message) {
		chatArea.appendText(message + "\n");
	}
	public void saveChat() {//서버에서 저장하는걸로 바뀌어서 사용 안함
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
//					pstmt1.setString(1, Common.getId());
//					pstmt1.setString(2, "시스템 알림");
//					pstmt1.executeUpdate();
				} else if (chatLog.equals("중복")) {

				} else {
					pstmt1.setString(1, Common.getId());
					pstmt1.setString(2, chatLog);
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
				sendM("G[" + (Common.getNick().equals("닉네임") ? Common.getId() : Common.getNick()) + "님 퇴장]\n");
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
	public void getPieceImage() {
		try {
			////빈것
			blankImage = new Image(getClass().getResource("/pieceImage/zz.png").toURI().toString());
			blank1 = new ImageView(blankImage);
			blank2 = new ImageView(blankImage);
			blank1.setFitWidth(25); blank1.setFitHeight(30);
			blank2.setFitWidth(25); blank2.setFitHeight(30);
			/////black pieces
			bRook = new Image(getClass().getResource("/pieceImage2/blackRook.png").toURI().toString());
			bKing = new Image(getClass().getResource("/pieceImage2/blackKing.png").toURI().toString());
			bQueen = new Image(getClass().getResource("/pieceImage2/blackQueen.png").toURI().toString());
			bKnight = new Image(getClass().getResource("/pieceImage2/blackKnight.png").toURI().toString());
			bBishop = new Image(getClass().getResource("/pieceImage2/blackBishop.png").toURI().toString());
			bPawn = new Image(getClass().getResource("/pieceImage2/blackPawn.png").toURI().toString());
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
			wRook = new Image(getClass().getResource("/pieceImage2/whiteRook.png").toURI().toString());
			wKing = new Image(getClass().getResource("/pieceImage2/whiteKing.png").toURI().toString());
			wQueen = new Image(getClass().getResource("/pieceImage2/whiteQueen.png").toURI().toString());
			wKnight = new Image(getClass().getResource("/pieceImage2/whiteKnight.png").toURI().toString());
			wBishop = new Image(getClass().getResource("/pieceImage2/whiteBishop.png").toURI().toString());
			wPawn = new Image(getClass().getResource("/pieceImage2/whitePawn.png").toURI().toString());
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
	public void reset() {
		loseLabel.setVisible(false);
		winLabel.setVisible(false);
		drawLabel.setVisible(false);
		A3.setGraphic(null);		A4.setGraphic(null);		A5.setGraphic(null);		A6.setGraphic(null);
		B3.setGraphic(null);		B4.setGraphic(null);		B5.setGraphic(null);		B6.setGraphic(null);
		C3.setGraphic(null);		C4.setGraphic(null);		C5.setGraphic(null);		C6.setGraphic(null);
		D3.setGraphic(null);		D4.setGraphic(null);		D5.setGraphic(null);		D6.setGraphic(null);
		E3.setGraphic(null);		E4.setGraphic(null);		E5.setGraphic(null);		E6.setGraphic(null);
		F4.setGraphic(null);		F3.setGraphic(null);		F5.setGraphic(null);		F6.setGraphic(null);
		G3.setGraphic(null);		G4.setGraphic(null);		G5.setGraphic(null);		G6.setGraphic(null);
		H3.setGraphic(null);		H4.setGraphic(null);		H5.setGraphic(null);		H6.setGraphic(null);
		A8.setGraphic(null);		B8.setGraphic(null);		C8.setGraphic(null);		D8.setGraphic(null);
		E8.setGraphic(null);		F8.setGraphic(null);		G8.setGraphic(null);		H8.setGraphic(null);
		A7.setGraphic(null);		B7.setGraphic(null);		C7.setGraphic(null);		D7.setGraphic(null);
		E7.setGraphic(null);		F7.setGraphic(null);		G7.setGraphic(null);		H7.setGraphic(null);
		
		A1.setGraphic(null);		B1.setGraphic(null);		C1.setGraphic(null);		D1.setGraphic(null);
		E1.setGraphic(null);		F1.setGraphic(null);		G1.setGraphic(null);		H1.setGraphic(null);
		A2.setGraphic(null);		B2.setGraphic(null);		C2.setGraphic(null);		D2.setGraphic(null);
		E2.setGraphic(null);		F2.setGraphic(null);		G2.setGraphic(null);		H2.setGraphic(null);
	}
	public void startBlack() {
		reset();
		A8.setGraphic(whiteRook1);		B8.setGraphic(whiteKnight1);
		C8.setGraphic(whiteBishop1);	D8.setGraphic(whiteQueen);
		E8.setGraphic(whiteKing);		F8.setGraphic(whiteBishop2);
		G8.setGraphic(whiteKnight2);	H8.setGraphic(whiteRook2);
		A7.setGraphic(whitePawn1);		B7.setGraphic(whitePawn2);
		C7.setGraphic(whitePawn3);		D7.setGraphic(whitePawn4);
		E7.setGraphic(whitePawn5);		F7.setGraphic(whitePawn6);
		G7.setGraphic(whitePawn7);		H7.setGraphic(whitePawn8);
		
		A1.setGraphic(blackRook1);		B1.setGraphic(blackKnight1);
		C1.setGraphic(blackBishop1);	D1.setGraphic(blackQueen);
		E1.setGraphic(blackKing);		F1.setGraphic(blackBishop2);
		G1.setGraphic(blackKnight2);	H1.setGraphic(blackRook2);
		A2.setGraphic(blackPawn1);		B2.setGraphic(blackPawn2);
		C2.setGraphic(blackPawn3);		D2.setGraphic(blackPawn4);
		E2.setGraphic(blackPawn5);		F2.setGraphic(blackPawn6);
		G2.setGraphic(blackPawn7);		H2.setGraphic(blackPawn8);
	}
	public void startWhite() {
		reset();
		A8.setGraphic(blackRook1);		B8.setGraphic(blackKnight1);
		C8.setGraphic(blackBishop1);	D8.setGraphic(blackQueen);
		E8.setGraphic(blackKing);		F8.setGraphic(blackBishop2);
		G8.setGraphic(blackKnight2);	H8.setGraphic(blackRook2);
		A7.setGraphic(blackPawn1);		B7.setGraphic(blackPawn2);
		C7.setGraphic(blackPawn3);		D7.setGraphic(blackPawn4);
		E7.setGraphic(blackPawn5);		F7.setGraphic(blackPawn6);
		G7.setGraphic(blackPawn7);		H7.setGraphic(blackPawn8);
		
		A1.setGraphic(whiteRook1);		B1.setGraphic(whiteKnight1);
		C1.setGraphic(whiteBishop1);	D1.setGraphic(whiteQueen);
		E1.setGraphic(whiteKing);		F1.setGraphic(whiteBishop2);
		G1.setGraphic(whiteKnight2);	H1.setGraphic(whiteRook2);
		A2.setGraphic(whitePawn1);		B2.setGraphic(whitePawn2);
		C2.setGraphic(whitePawn3);		D2.setGraphic(whitePawn4);
		E2.setGraphic(whitePawn5);		F2.setGraphic(whitePawn6);
		G2.setGraphic(whitePawn7);		H2.setGraphic(whitePawn8);
	}
	@FXML
	public void startGame(ActionEvent event){
		pick=0;
		if(startButton.getText().equals("준비")) {
			startButton.setDisable(true);
			//흑 준비됐다고 신호 보냄
			sendM("RR1");
			readyStatus.setText("대기");
			
		}else if(startButton.getText().equals("시작")) {
			checkOppo();
			if(Common.getOppoId().equals("")) {
				noOppoDisplay();
				Platform.runLater(()->{
					idLabel1.setText("입장 전");
					winloseLabel1.setText("      승     패");
					nickLabel1.setText("닉네임");
					percentLabel1.setText("승률 : 0.00%");
					reset();
				});
			}
			else {
				Platform.runLater(()->{
					startButton.setDisable(true);
					readyStatus.setText("");
					//Start Room 1 //게임 동시시작
					sendM("SR1:"+Common.getId()+":"+Common.getOppoId());
					startG();
					myTurn=true;
					startButton.setDisable(false);
					startButton.setText("기권");
				});
			}
			
		}else if(startButton.getText().equals("기권")) {
			Rectangle2D screenBounds = Screen.getPrimary().getBounds();
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setWidth(400);
			alert.setX(screenBounds.getMaxX() / 2 - 200);
			alert.setY(screenBounds.getMaxY() / 2 - 50);
			alert.setResizable(false);
			alert.setTitle("기권이라니..");
			alert.setHeaderText("");
			alert.setContentText("정말로 기권하시겠습니까??");

			if (alert.showAndWait().get() == ButtonType.OK) {
				readyStatus.setText("승");
				sendM("RE1:"+ (Common.getMyColor().equals("white")? "black":"white"));
			}
		}
	}

	public void startG() {
		reset();
		pick = 0;
		if (Common.getMyColor().equals("black")) {
			getPieceImage();
			startBlack();
			colorImage.setFitWidth(25);colorImage.setFitHeight(30);colorImage.setImage(bKing);
			colorImage1.setFitWidth(25);colorImage1.setFitHeight(30);colorImage1.setImage(wKing);
		} else if (Common.getMyColor().equals("white")) {
			getPieceImage();
			startWhite();
			colorImage.setFitWidth(25);colorImage.setFitHeight(30);colorImage.setImage(wKing);
			colorImage1.setFitWidth(25);colorImage1.setFitHeight(30);colorImage1.setImage(bKing);
		}
	}

	@FXML
	public void askDraw(ActionEvent event) {
		sendM("AD1");
		readyStatus.setText("신청");
	}
	public void askD() {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		Alert drawReq = new Alert(AlertType.CONFIRMATION);
		drawReq.setWidth(400);
		drawReq.setX(screenBounds.getMaxX()/2 - 180);
		drawReq.setY(screenBounds.getMaxY()/2 - 100);
//		drawReq.setX(100);
//		drawReq.setY(200);
		drawReq.setResizable(false);
		drawReq.setTitle("Draw Requested");
		drawReq.setHeaderText("");
		drawReq.setContentText("무승부를 받아들이시겠습니까?");
		if (drawReq.showAndWait().get() == ButtonType.OK) {
			
			Platform.runLater(()->{
				readyStatus.setText("드로");
			});
			sendM("DW1:");
		}
	}
	/////////////////////////////////체스판
	@FXML public void btA1(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(A1.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+A1.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+A1.getId());
					copy = (ImageView) A1.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+A1.getId()+"\n"); sendM("DD1="+A1.getId()+"=TR1");
				A1.setGraphic(null); A1.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btA2(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(A2.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+A2.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+A2.getId());
					copy = (ImageView) A2.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+A2.getId()+"\n"); sendM("DD1="+A2.getId()+"=TR1");
				A2.setGraphic(null); A2.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btA3(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(A3.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+A3.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+A3.getId());
					copy = (ImageView) A3.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+A3.getId()+"\n"); sendM("DD1="+A3.getId()+"=TR1");
				A3.setGraphic(null); A3.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btA4(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(A4.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+A4.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+A4.getId());
					copy = (ImageView) A4.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+A4.getId()+"\n"); sendM("DD1="+A4.getId()+"=TR1");
				A4.setGraphic(null); A4.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btA5(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(A5.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+A5.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+A5.getId());
					copy = (ImageView) A5.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+A5.getId()+"\n"); sendM("DD1="+A5.getId()+"=TR1");
				A5.setGraphic(null); A5.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btA6(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(A6.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+A6.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+A6.getId());
					copy = (ImageView) A6.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+A6.getId()+"\n"); sendM("DD1="+A6.getId()+"=TR1");
				A6.setGraphic(null); A6.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btA7(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(A7.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+A7.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+A7.getId());
					copy = (ImageView) A7.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+A7.getId()+"\n"); sendM("DD1="+A7.getId()+"=TR1");
				A7.setGraphic(null); A7.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btA8(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(A8.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+A8.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+A8.getId());
					copy = (ImageView) A8.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+A8.getId()+"\n"); sendM("DD1="+A8.getId()+"=TR1");
				A8.setGraphic(null); A8.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btB1(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(B1.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+B1.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+B1.getId());
					copy = (ImageView) B1.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+B1.getId()+"\n"); sendM("DD1="+B1.getId()+"=TR1");
				B1.setGraphic(null); B1.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btB2(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(B2.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+B2.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+B2.getId());
					copy = (ImageView) B2.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+B2.getId()+"\n"); sendM("DD1="+B2.getId()+"=TR1");
				B2.setGraphic(null); B2.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btB3(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(B3.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+B3.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+B3.getId());
					copy = (ImageView) B3.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+B3.getId()+"\n"); sendM("DD1="+B3.getId()+"=TR1");
				B3.setGraphic(null); B3.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btB4(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(B4.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+B4.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+B4.getId());
					copy = (ImageView) B4.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+B4.getId()+"\n"); sendM("DD1="+B4.getId()+"=TR1");
				B4.setGraphic(null); B4.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btB5(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(B5.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+B5.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+B5.getId());
					copy = (ImageView) B5.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+B5.getId()+"\n"); sendM("DD1="+B5.getId()+"=TR1");
				B5.setGraphic(null); B5.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btB6(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(B6.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+B6.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+B6.getId());
					copy = (ImageView) B6.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+B6.getId()+"\n"); sendM("DD1="+B6.getId()+"=TR1");
				B6.setGraphic(null); B6.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btB7(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(B7.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+B7.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+B7.getId());
					copy = (ImageView) B7.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+B7.getId()+"\n"); sendM("DD1="+B7.getId()+"=TR1");
				B7.setGraphic(null); B7.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btB8(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(B8.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+B8.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+B8.getId());
					copy = (ImageView) B8.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+B8.getId()+"\n"); sendM("DD1="+B8.getId()+"=TR1");
				B8.setGraphic(null); B8.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btC1(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(C1.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+C1.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+C1.getId());
					copy = (ImageView) C1.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+C1.getId()+"\n"); sendM("DD1="+C1.getId()+"=TR1");
				C1.setGraphic(null); C1.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btC2(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(C2.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+C2.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+C2.getId());
					copy = (ImageView) C2.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+C2.getId()+"\n"); sendM("DD1="+C2.getId()+"=TR1");
				C2.setGraphic(null); C2.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btC3(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(C3.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+C3.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+C3.getId());
					copy = (ImageView) C3.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+C3.getId()+"\n"); sendM("DD1="+C3.getId()+"=TR1");
				C3.setGraphic(null); C3.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btC4(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(C4.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+C4.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+C4.getId());
					copy = (ImageView) C4.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+C4.getId()+"\n"); sendM("DD1="+C4.getId()+"=TR1");
				C4.setGraphic(null); C4.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btC5(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(C5.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+C5.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+C5.getId());
					copy = (ImageView) C5.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+C5.getId()+"\n"); sendM("DD1="+C5.getId()+"=TR1");
				C5.setGraphic(null); C5.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btC6(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(C6.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+C6.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+C6.getId());
					copy = (ImageView) C6.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+C6.getId()+"\n"); sendM("DD1="+C6.getId()+"=TR1");
				C6.setGraphic(null); C6.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btC7(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(C7.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+C7.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+C7.getId());
					copy = (ImageView) C7.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+C7.getId()+"\n"); sendM("DD1="+C7.getId()+"=TR1");
				C7.setGraphic(null); C7.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btC8(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(C8.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+C8.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+C8.getId());
					copy = (ImageView) C8.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+C8.getId()+"\n"); sendM("DD1="+C8.getId()+"=TR1");
				C8.setGraphic(null); C8.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btD1(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(D1.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+D1.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+D1.getId());
					copy = (ImageView) D1.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+D1.getId()+"\n"); sendM("DD1="+D1.getId()+"=TR1");
				D1.setGraphic(null); D1.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btD2(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(D2.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+D2.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+D2.getId());
					copy = (ImageView) D2.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+D2.getId()+"\n"); sendM("DD1="+D2.getId()+"=TR1");
				D2.setGraphic(null); D2.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btD3(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(D3.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+D3.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+D3.getId());
					copy = (ImageView) D3.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+D3.getId()+"\n"); sendM("DD1="+D3.getId()+"=TR1");
				D3.setGraphic(null); D3.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btD4(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(D4.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+D4.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+D4.getId());
					copy = (ImageView) D4.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+D4.getId()+"\n"); sendM("DD1="+D4.getId()+"=TR1");
				D4.setGraphic(null); D4.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btD5(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(D5.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+D5.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+D5.getId());
					copy = (ImageView) D5.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+D5.getId()+"\n"); sendM("DD1="+D5.getId()+"=TR1");
				D5.setGraphic(null); D5.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btD6(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(D6.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+D6.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+D6.getId());
					copy = (ImageView) D6.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+D6.getId()+"\n"); sendM("DD1="+D6.getId()+"=TR1");
				D6.setGraphic(null); D6.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btD7(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(D7.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+D7.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+D7.getId());
					copy = (ImageView) D7.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+D7.getId()+"\n"); sendM("DD1="+D7.getId()+"=TR1");
				D7.setGraphic(null); D7.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btD8(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(D8.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+D8.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+D8.getId());
					copy = (ImageView) D8.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+D8.getId()+"\n"); sendM("DD1="+D8.getId()+"=TR1");
				D8.setGraphic(null); D8.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btE1(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(E1.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+E1.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+E1.getId());
					copy = (ImageView) E1.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+E1.getId()+"\n"); sendM("DD1="+E1.getId()+"=TR1");
				E1.setGraphic(null); E1.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btE2(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(E2.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+E2.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+E2.getId());
					copy = (ImageView) E2.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+E2.getId()+"\n"); sendM("DD1="+E2.getId()+"=TR1");
				E2.setGraphic(null); E2.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btE3(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(E3.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+E3.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+E3.getId());
					copy = (ImageView) E3.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+E3.getId()+"\n"); sendM("DD1="+E3.getId()+"=TR1");
				E3.setGraphic(null); E3.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btE4(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(E4.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+E4.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+E4.getId());
					copy = (ImageView) E4.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+E4.getId()+"\n"); sendM("DD1="+E4.getId()+"=TR1");
				E4.setGraphic(null); E4.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btE5(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(E5.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+E5.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+E5.getId());
					copy = (ImageView) E5.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+E5.getId()+"\n"); sendM("DD1="+E5.getId()+"=TR1");
				E5.setGraphic(null); E5.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btE6(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(E6.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+E6.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+E6.getId());
					copy = (ImageView) E6.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+E6.getId()+"\n"); sendM("DD1="+E6.getId()+"=TR1");
				E6.setGraphic(null); E6.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btE7(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(E7.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+E7.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+E7.getId());
					copy = (ImageView) E7.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+E7.getId()+"\n"); sendM("DD1="+E7.getId()+"=TR1");
				E7.setGraphic(null); E7.setGraphic(copy); pick=0; copy = null;}
		}
	}

	@FXML public void btE8(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(E8.getGraphic()!=null) {
			sendM("G[move] "+Common.getNick()+" >> "+E8.getId()+"-> ");
			sendM("SS1="+Common.getId()+"="+E7.getId());
			copy = (ImageView) E8.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
		}else {
				sendM("G" + E8.getId() + "\n");
				E8.setGraphic(null);
				E8.setGraphic(copy);
				pick = 0;
				copy = null;
				readyStatus.setText("  ");
				sendM("WW1:"+Common.getMyColor());
			}
		}
	}

	@FXML public void btF1(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(F1.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+F1.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+F1.getId());
					copy = (ImageView) F1.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+F1.getId()+"\n"); sendM("DD1="+F1.getId()+"=TR1");
				F1.setGraphic(null); F1.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btF2(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(F2.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+F2.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+F2.getId());
					copy = (ImageView) F2.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+F2.getId()+"\n"); sendM("DD1="+F2.getId()+"=TR1");
				F2.setGraphic(null); F2.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btF3(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(F3.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+F3.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+F3.getId());
					copy = (ImageView) F3.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+F3.getId()+"\n"); sendM("DD1="+F3.getId()+"=TR1");
				F3.setGraphic(null); F3.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btF4(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(F4.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+F4.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+F4.getId());
					copy = (ImageView) F4.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+F4.getId()+"\n"); sendM("DD1="+F4.getId()+"=TR1");
				F4.setGraphic(null); F4.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btF5(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(F5.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+F5.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+F5.getId());
					copy = (ImageView) F5.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+F5.getId()+"\n"); sendM("DD1="+F5.getId()+"=TR1");
				F5.setGraphic(null); F5.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btF6(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(F6.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+F6.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+F6.getId());
					copy = (ImageView) F6.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+F6.getId()+"\n"); sendM("DD1="+F6.getId()+"=TR1");
				F6.setGraphic(null); F6.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btF7(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(F7.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+F7.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+F7.getId());
					copy = (ImageView) F7.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+F7.getId()+"\n"); sendM("DD1="+F7.getId()+"=TR1");
				F7.setGraphic(null); F7.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btF8(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(F8.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+F8.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+F8.getId());
					copy = (ImageView) F8.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+F8.getId()+"\n"); sendM("DD1="+F8.getId()+"=TR1");
				F8.setGraphic(null); F8.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btG1(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(G1.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+G1.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+G1.getId());
					copy = (ImageView) G1.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+G1.getId()+"\n"); sendM("DD1="+G1.getId()+"=TR1");
				G1.setGraphic(null); G1.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btG2(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(G2.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+G2.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+G2.getId());
					copy = (ImageView) G2.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+G2.getId()+"\n"); sendM("DD1="+G2.getId()+"=TR1");
				G2.setGraphic(null); G2.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btG3(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(G3.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+G3.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+G3.getId());
					copy = (ImageView) G3.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+G3.getId()+"\n"); sendM("DD1="+G3.getId()+"=TR1");
				G3.setGraphic(null); G3.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btG4(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(G4.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+G4.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+G4.getId());
					copy = (ImageView) G4.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+G4.getId()+"\n"); sendM("DD1="+G4.getId()+"=TR1");
				G4.setGraphic(null); G4.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btG5(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(G5.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+G5.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+G5.getId());
					copy = (ImageView) G5.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+G5.getId()+"\n"); sendM("DD1="+G5.getId()+"=TR1");
				G5.setGraphic(null); G5.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btG6(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(G6.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+G6.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+G6.getId());
					copy = (ImageView) G6.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+G6.getId()+"\n"); sendM("DD1="+G6.getId()+"=TR1");
				G6.setGraphic(null); G6.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btG7(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(G7.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+G7.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+G7.getId());
					copy = (ImageView) G7.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+G7.getId()+"\n"); sendM("DD1="+G7.getId()+"=TR1");
				G7.setGraphic(null); G7.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btG8(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(G8.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+G8.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+G8.getId());
					copy = (ImageView) G8.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+G8.getId()+"\n"); sendM("DD1="+G8.getId()+"=TR1");
				G8.setGraphic(null); G8.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btH1(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(H1.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+H1.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+H1.getId());
					copy = (ImageView) H1.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+H1.getId()+"\n"); sendM("DD1="+H1.getId()+"=TR1");
				H1.setGraphic(null); H1.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btH2(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(H2.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+H2.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+H2.getId());
					copy = (ImageView) H2.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+H2.getId()+"\n"); sendM("DD1="+H2.getId()+"=TR1");
				H2.setGraphic(null); H2.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btH3(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(H3.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+H3.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+H3.getId());
					copy = (ImageView) H3.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+H3.getId()+"\n"); sendM("DD1="+H3.getId()+"=TR1");
				H3.setGraphic(null); H3.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btH4(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(H4.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+H4.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+H4.getId());
					copy = (ImageView) H4.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+H4.getId()+"\n"); sendM("DD1="+H4.getId()+"=TR1");
				H4.setGraphic(null); H4.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btH5(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(H5.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+H5.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+H5.getId());
					copy = (ImageView) H5.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+H5.getId()+"\n"); sendM("DD1="+H5.getId()+"=TR1");
				H5.setGraphic(null); H5.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btH6(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(H6.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+H6.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+H6.getId());
					copy = (ImageView) H6.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+H6.getId()+"\n"); sendM("DD1="+H6.getId()+"=TR1");
				H6.setGraphic(null); H6.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btH7(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(H7.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+H7.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+H7.getId());
					copy = (ImageView) H7.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+H7.getId()+"\n"); sendM("DD1="+H7.getId()+"=TR1");
				H7.setGraphic(null); H7.setGraphic(copy); pick=0; copy = null;}
		}
	}
	@FXML public void btH8(ActionEvent event) {
		if(myTurn) { if(pick==0) { if(H8.getGraphic()!=null) {
					sendM("G[move] "+Common.getNick()+" >> "+H8.getId()+"-> ");
					sendM("SS1="+Common.getId()+"="+H8.getId());
					copy = (ImageView) H8.getGraphic();copy.setFitWidth(25); copy.setFitHeight(30);pick=1;}else {}
			}else {sendM("G"+H8.getId()+"\n"); sendM("DD1="+H8.getId()+"=TR1");
				H8.setGraphic(null); H8.setGraphic(copy); pick=0; copy = null;}
		}
	}
}
