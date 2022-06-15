package admin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import observableList.OnlineList;

public class LobbyServerController implements Initializable{
	private Parent root;
	private Stage stage;
	private Scene scene;
	// 재사용이 가능한 ThreadPool
	public String onScreen;
	public static ExecutorService threadPool;
	public static Vector<User> users = new Vector<User>();
	private ServerSocket ss;
	
	@FXML
	private Button logoutButton, toAdminButton, toChatButton, toGameButton;
	@FXML
	private Button toRankButton, toServerButton, toUserButton;
	@FXML
	private TextArea serverTextArea;
	@FXML
	private TextField onlineTextField;
	@FXML
	private Button startButton, stopButton;
	@FXML 
	private TextArea textArea;
	public String nick,id;
	private boolean isHere = true;
		
	
	public void onScreen(String message) {
		serverTextArea.appendText(message+"\n");
	}
	// 서버 열거야
	public void startS(String IP, int port) {
		try {
			ss = new ServerSocket();
			ss.bind(new InetSocketAddress(IP,port));
			Common.setServerOn(true);
		} catch (Exception e) {
			e.printStackTrace();
			if(!ss.isClosed()) {
				//stopS();
				System.err.print("startS 에서 문제 발생 ");
			}
			return;
		}
		
		Runnable thread = new Runnable() {

			@Override
			public void run() {
				while(!ss.isClosed()) {//열렸을 때만 받을거다
					try {
						Socket socket;
						socket = ss.accept();
						users.add(new User(socket));
						String tell = "[유저 접속]"
								+socket.getInetAddress();
						//onScreen(tell);
						Common.setNum(users.size());
						
					} catch (IOException e) {
						if(!ss.isClosed()) {
							try {
								stopS();
							} catch (Exception e1) {
								e1.printStackTrace();
								System.err.println("소켓 닫혔대~");
							}
						}
						break;
					}
				}
			}
		};
		//제한이 없어서 cpu에 무리가 간다는것 같다.
//		threadPool = Executors.newCachedThreadPool();
		//test 단계에선 10로 제한 둔다
		threadPool = Executors.newFixedThreadPool(10);
		threadPool.submit(thread);
	}

	public void stopS() {
		try {
			//모든 소켓 닫기
			Iterator<User> iterator = users.iterator();
			while(iterator.hasNext()) {
				User next = iterator.next();
				next.getSocket().close();
				iterator.remove();
				Common.setNum(users.size());
			}
			//서버 소켓 닫기
			if(ss!=null || !ss.isClosed()) {
				ss.close();
			}
			//쓰레드풀 종료
			if(threadPool!=null || !threadPool.isShutdown()) {
				threadPool.shutdown();
			}
			//onlinelist에서 다 삭제
			Connection conn = null;
			Statement stmt = null;
			PreparedStatement pstmt1 = null;
			ResultSet rs = null;
			String sql1 = "";
			try {//정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				//onlineuser에서
				sql1 = "delete from onlineuser";
				pstmt1 = conn.prepareStatement(sql1);
				rs = pstmt1.executeQuery();
				
			}catch (Exception e) {
				e.printStackTrace();
				System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
			}finally {// 6단계
				 try {if (rs != null) rs.close();	} catch (Exception e) {}
				 try {if (conn != null) conn.close();	} catch (Exception e) {}
				 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e) {}
			}
		} catch (Exception e) {
			System.out.println("들어와있는게없음");
		}
		
		
	}
	
	@FXML
	public void startServer(ActionEvent event) throws Exception {
		startS("127.0.0.1", 7777);
		Common.setServerOn(true);
		startButton.setDisable(true);
		stopButton.setDisable(false);
		String message = "[서버시작]";
		onScreen(message);
		onlineTextField.setText(Integer.toString(users.size()));
		Common.setNum(users.size());
		//serverTextArea.appendText(message);
	}
	
	@FXML
	public void stopServer(ActionEvent event) throws Exception {
		try {
			stopS();
			Common.setServerOn(false);
			startButton.setDisable(false);
			stopButton.setDisable(true);
			String message = "[서버종료]";
			onScreen(message);
			onlineTextField.setText(Integer.toString(users.size()));
			Common.setNum(users.size());
			//serverTextArea.appendText(message);
		}catch (Exception e) {
			System.out.println("왜야");
		} 	
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1){
		textArea.setEditable(false);
		if (Common.isServerOn()) {
			serverTextArea.appendText("[서버 실행 중]\n");
			//onlineTextField.setText(Integer.toString(users.size()));
			Common.setNum(users.size());
			startButton.setDisable(true);
			stopButton.setDisable(false);
			onLineUser();
		} else {
			startButton.setDisable(false);
			stopButton.setDisable(true);
		}
		onLineUser();
		onLineList();
	}

	public void onLineUser() {//온라인 유저 수를 계속 업데이트하는 메서드
		try {
			Thread thread = new Thread() {
				public void run() {
					while(isHere) {
						//온라인 유저 수 표기
						Platform.runLater(()->{
							onlineTextField.setText(Integer.toString(users.size()));
						});
						
						Common.setNum(users.size());
						try { sleep(1000); } catch (InterruptedException e) {}
					}
				}
			};
			thread.start();
		} catch (Exception e) {
			System.err.println("여기가 문제. 쓰레드 도는 중");
		}
	}	
	public void onLineList() throws IllegalStateException{//온라인 유저를 계속 표기하는 메서드
		Thread thread = new Thread() {
			public void run() {
				while(true) {//서버가 켜져있을 경우에만..
					Connection conn = null;
					String sql1 = null;
					PreparedStatement pstmt1 = null;
					ResultSet rs = null;
					try {//정적 동적 쿼리 4,5단계
						conn = ConnUtil.getConnection();
						
						sql1 = "select * from onlineuser order by aaa";
						pstmt1 = conn.prepareStatement(sql1);
						rs = pstmt1.executeQuery();
						StringBuffer sb = new StringBuffer();
						while(rs.next()) {
							//StringBuffer sb = new StringBuffer();
							String buing = rs.getString("id");
							sb.append(buing+"\n");
						}Platform.runLater(()->{textArea.setText(sb.toString() +"\n");});
						//1초마다 리스트를 자동으로 새로고침함.
						try { sleep(1000); } catch (InterruptedException e) {}						
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
				
			}
		};
		thread.start();
	}
	//창 바꾸는 메소드들 
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
			stopS();
			
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
			Common.setNum(users.size());
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
    		Common.setNum(users.size());
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
    		Common.setNum(users.size());
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
    		Common.setNum(users.size());
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
    		Common.setNum(users.size());
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
    		Common.setNum(users.size());
			root = FXMLLoader.load(getClass().getResource("/gui/AdminLobby.fAdmin.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		}catch (Exception e) {}
    }
}

////////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////
//하나의 클라이언트와 통신하게 하는 클라이언트 클래스
class User {
	private Socket socket; 
	private String id;
	private String nick;
	private String mess;
	private String nextplease;
	private int nextval;
	private int NUMNUM;
	private int nextval_detailgame;
	private static String white;
	private static String black;

	public String getMess() {
		return mess;
	}
	
	public void setID(String id) {
		this.id = id;
	}
	
	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public String getNick() {
		return nick;
	}
	
	public String getID() {
		return id;
	}
	
	//패키지가 달라서 private은 사용불가 public메서드로 리턴.
	public Socket getSocket() {
		return socket;
	}
	
	public User(Socket socket) { 
		this.socket = socket; 
		receive();
	}
	// 반복적으로 유저로부터 메세지를 받는 메소드
	public void receive() {
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				try {
					while(true) { //반복적으로 받을 것
						InputStream in = socket.getInputStream();
						byte[] buffer = new byte[512];
						int length =in.read(buffer);
						if (length==-1) {
							throw new IOException();
						}
						String check = "[메시지 수신 성공]"
								+socket.getInetAddress();
						System.out.println(check);
						//버퍼의 0에서부터 길이만큼 [3]로 인코딩
						String message = new String(buffer,0,length,"UTF-8");
						System.out.println("받은 메세지: "+message);
						mess = message;
						//받은 메시지를 반복적으로 모두에게 보냄
						for (User all : LobbyServerController.users) {
							all.sendtoUser(message);
						}
						saveChat();
						saveGame();
						detailGamePlay();
					}
				} catch (Exception e) {
					try {
						System.out.println("[메시지 수신 실패@@]"
								+ socket.getInetAddress());
								//+": "+Thread.currentThread().getName());
						LobbyServerController.users.remove(User.this);
						socket.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
				
			}
			
		};
		LobbyServerController.threadPool.submit(thread);
	}
	
	// 전체 유저에게 전달
	public void sendtoUser(String message) {
		Runnable thread = new Runnable() {
			public void run() {
				try {
					OutputStream out = socket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
				} catch (Exception e) {
					try {
						System.out.println("[메시지 송신 실패@@]"
								+socket.getInetAddress());
								//+socket.getRemoteSocketAddress()
								//+": "+Thread.currentThread().getName());
						LobbyServerController.users.remove(User.this);
						socket.close();
					} catch (Exception e2) {
					}
				}
			}
		};
		LobbyServerController.threadPool.submit(thread);
	}
	//채팅 내용 저장
	public void saveChat() {
		try {
			Connection conn = null;
			PreparedStatement pstmt1 = null;
			String sql1 = null;

			try {// 정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				// chess_user 에 등록
				sql1 = "INSERT INTO chat_log(id,text,loc) VALUES(?,?,?)";
				pstmt1 = conn.prepareStatement(sql1);
				//받은 메세지에서 위치, 아이디, 메세지 분리할것
				String[] sep = mess.split(" : ");
				//sep[0]에서 위치+아이디 분이
				byte[] by = (sep[0]).getBytes("UTF-8");
				String saveRoom = new String(by,0,1);
				String saveId = new String(by,1,by.length-1,"UTF-8");
				if(sep.length == 1) {
					// " : "로 분리가 안된거니 게임관련.
				}else {
					pstmt1.setString(1, saveId);
					if(saveRoom.equals("L")) {//로비
						pstmt1.setString(2, sep[1]);
						pstmt1.setString(3, "Lobby");
						pstmt1.executeUpdate();
					}else if(saveRoom.equals("G")) {//로비
						pstmt1.setString(2, sep[1]);
						pstmt1.setString(3, "Room1");
						pstmt1.executeUpdate();
					}else if(saveRoom.equals("H")) {//로비
						pstmt1.setString(2, sep[1]);
						pstmt1.setString(3, "Room2");
						pstmt1.executeUpdate();
					}else if(saveRoom.equals("I")) {//로비
						pstmt1.setString(2, sep[1]);
						pstmt1.setString(3, "Room3");
						pstmt1.executeUpdate();
					}
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
	//게임 내용 기록
	public void saveGame() {
		try {
			Connection conn = null;
			PreparedStatement pstmt1 = null;
			String sql1 = null;

			try {// 정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				//들어온 메세지 구분 SR, WW, RE
				String[] spe = mess.split(":");
				//시작
				if(spe[0].equals("SR1")||spe[0].equals("SR2")||spe[0].equals("SR3")) {
					nextSeq();
					sql1 = "insert into game_log(white,black) values(?,?)";
					pstmt1 = conn.prepareStatement(sql1);
					white = spe[1]; black = spe[2];
					pstmt1.setString(1, spe[1]);
					pstmt1.setString(2, spe[2]);
					pstmt1.executeUpdate();
					//플레이 기록용 게임 테이블 생성
					makeGameTable();
				//승리
				}else if(spe[0].equals("WW1")||spe[0].equals("WW2")||spe[0].equals("WW3")
						||spe[0].equals("RE1")||spe[0].equals("RE2")||spe[0].equals("RE3")) {
					sql1 = "update game_log set win = ? where game_no = ?";
					pstmt1 = conn.prepareStatement(sql1);
					pstmt1.setString(1, spe[1].equals("white")? "w":"b");
					pstmt1.setInt(2, Integer.parseInt(Common.getNext())+1);
					pstmt1.executeUpdate();
					if (spe[1].equals("white")) {
						whiteWin();
						blackLose();
					}else {
						whiteLose();
						blackWin();
						}
					System.out.println(NUMNUM+Common.getNext() + (spe[1].equals("white")? "w":"b"));
				}else if(spe[0].equals("DW1")||spe[0].equals("DW2")||spe[0].equals("DW3")){
					sql1 = "update game_log set win = ? where game_no = ?";
					pstmt1 = conn.prepareStatement(sql1);
					pstmt1.setString(1, "d");
					pstmt1.setInt(2, Integer.parseInt(Common.getNext())+1);
					pstmt1.executeUpdate();
					System.out.println(NUMNUM+Common.getNext() + (spe[0]));
					whiteDraw();
					blackDraw();
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
	public void whiteWin() {
		try {
			Connection conn = null;
			PreparedStatement pstmt11 = null;
			String sql1 = null;

			try {// 정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				// chess_user 에 등록
				sql1 = "update chess_user set win = win +1, game = game +1 , rate = ((win+1)*100)/(game+1) where id = ? ";
				pstmt11 = conn.prepareStatement(sql1);
				//받은 메세지에서 위치, 아이디, 메세지 분리할것
					pstmt11.setString(1, white);
					pstmt11.executeUpdate();

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
					if (pstmt11 != null) {
						pstmt11.close();
					}
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
		}
	}
	public void blackWin() {
		try {
			Connection conn = null;
			PreparedStatement pstmt1 = null;
			String sql1 = null;

			try {// 정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				// chess_user 에 등록
				sql1 = "update chess_user set win = win +1, game = game +1 , rate = ((win+1)*100)/(game+1) where id = ? ";
				pstmt1 = conn.prepareStatement(sql1);
				//받은 메세지에서 위치, 아이디, 메세지 분리할것
					pstmt1.setString(1, black);
					pstmt1.executeUpdate();

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
	public void whiteLose() {
		try {
			Connection conn = null;
			PreparedStatement pstmt1 = null;
			String sql1 = null;

			try {// 정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				// chess_user 에 등록
				sql1 = "update chess_user set lose = lose +1, game = game +1 , rate = (win*100)/(game+1) where id =";
				pstmt1 = conn.prepareStatement(sql1);
				//받은 메세지에서 위치, 아이디, 메세지 분리할것
					pstmt1.setString(1, white);
					pstmt1.executeUpdate();

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
	public void blackLose() {
		try {
			Connection conn = null;
			PreparedStatement pstmt1 = null;
			String sql1 = null;

			try {// 정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				// chess_user 에 등록
				sql1 = "update chess_user set lose = lose +1, game = game +1 , rate = (win*100)/(game+1) where id = ?";
				pstmt1 = conn.prepareStatement(sql1);
				//받은 메세지에서 위치, 아이디, 메세지 분리할것
					pstmt1.setString(1, black);
					pstmt1.executeUpdate();

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
	public void whiteDraw() {
		try {
			Connection conn = null;
			PreparedStatement pstmt1 = null;
			String sql1 = null;

			try {// 정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				// chess_user 에 등록
				sql1 = "update chess_user set game = game +1 , rate = (win*100)/(game+1) where id = ?";
				pstmt1 = conn.prepareStatement(sql1);
				//받은 메세지에서 위치, 아이디, 메세지 분리할것
					pstmt1.setString(1, white);
					pstmt1.executeUpdate();

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
	public void blackDraw() {
		try {
			Connection conn = null;
			PreparedStatement pstmt1 = null;
			String sql1 = null;

			try {// 정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				// chess_user 에 등록
				sql1 = "update chess_user set game = game +1 , rate = (win*100)/(game+1) where id = ? ";
				pstmt1 = conn.prepareStatement(sql1);
				//받은 메세지에서 위치, 아이디, 메세지 분리할것
					pstmt1.setString(1, black);
					pstmt1.executeUpdate();

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
	public void nextSeq() {//game_seq
		try {
			Connection conn = null;
			Statement stmt1 = null;
			String sql2 = null;
			ResultSet rs = null;

			try {// 정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				//들어온 메세지 구분 SR, WW, RE
				sql2 = "select game_seq.nextval from dual";
				stmt1 = conn.createStatement();
				rs = stmt1.executeQuery(sql2);
				if(rs.next()) {
					NUMNUM = rs.getInt("nextval");
					Common.setNext(rs.getInt("nextval"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
			} finally {// 6단계
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (Exception e) {}
				try {
					if (stmt1 != null) {
						stmt1.close();
					}
				} catch (Exception e) {}
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (Exception e) {}
			}
		} catch (Exception e) {}
	}
	//게임별 테이블을 만듬(움직임 기록)
	public void makeGameTable() {
		try {
			Connection conn = null;
			Statement stmt1 = null;
			StringBuffer sql2 = new StringBuffer();
			ResultSet rs =null;

			try {// 정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				//user_id 테이블 생성
				sql2.append("create table game_");
				sql2.append(Integer.toString(Integer.parseInt(Common.getNext())+1));
				sql2.append("(player varchar2(20),startt varchar2(10) , destination varchar2(10), imsi int default tst.nextval)");
				stmt1 = conn.createStatement();
				rs = stmt1.executeQuery(sql2.toString());
				
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
					if (stmt1 != null) {
						stmt1.close();
					}
				} catch (Exception e) {
				}
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
		}
	}
	//움직임을 위 테이블에 기록
	public void detailGamePlay() {
		try {
			Connection conn = null;
			PreparedStatement pstmt1 = null;
			String sql1 = null;

			try {// 정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				//SS1=닉=버튼
				//DD1=버튼=TR1
				String[] spe = mess.split("=");
				//시작지점
				if(spe[0].equals("SS1")||spe[0].equals("SS2")||spe[0].equals("SS3")) {
					nextSeq2();
					nextplease = Common.getNext();
					sql1 = "insert into game_"+Integer.toString(Integer.parseInt(Common.getNext())+1)+"(player,startt,imsi) values(?,?,?)";
					pstmt1 = conn.prepareStatement(sql1);
					pstmt1.setString(1, spe[1]);
					pstmt1.setString(2, spe[2]);
					pstmt1.setInt(3, nextval_detailgame);
					pstmt1.executeUpdate();
					System.out.println(spe[0]+" / "+spe[1]+" / "+spe[2]);
				//도착지
				}else if(spe[0].equals("DD1")||spe[0].equals("DD2")||spe[0].equals("DD3")) {
					sql1 = "update game_"+Integer.toString(Integer.parseInt(Common.getNext())+1)+" set destination = ? where imsi = ?";
					pstmt1 = conn.prepareStatement(sql1);
					pstmt1.setString(1, spe[1]);
					pstmt1.setInt(2, nextval_detailgame);
					pstmt1.executeUpdate();
					System.out.println(spe[0]+" / "+spe[1]+" / "+spe[2]);
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
	public void nextSeq2() {//tst
		try {
			Connection conn = null;
			Statement stmt1 = null;
			String sql2 = null;
			ResultSet rs = null;

			try {// 정적 동적 쿼리 4,5단계
				conn = ConnUtil.getConnection();
				//들어온 메세지 구분 SR, WW, RE
				sql2 = "select tst.nextval from dual";
				stmt1 = conn.createStatement();
				rs = stmt1.executeQuery(sql2);
				if(rs.next()) {
					nextval_detailgame = rs.getInt("nextval");
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("쿼리문 오류일 확률이 욜라게 무쟈게 높당");
			} finally {// 6단계
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (Exception e) {}
				try {
					if (stmt1 != null) {
						stmt1.close();
					}
				} catch (Exception e) {}
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (Exception e) {}
			}
		} catch (Exception e) {}
	}
}

