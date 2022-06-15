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
	// ������ ������ ThreadPool
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
	// ���� ���ž�
	public void startS(String IP, int port) {
		try {
			ss = new ServerSocket();
			ss.bind(new InetSocketAddress(IP,port));
			Common.setServerOn(true);
		} catch (Exception e) {
			e.printStackTrace();
			if(!ss.isClosed()) {
				//stopS();
				System.err.print("startS ���� ���� �߻� ");
			}
			return;
		}
		
		Runnable thread = new Runnable() {

			@Override
			public void run() {
				while(!ss.isClosed()) {//������ ���� �����Ŵ�
					try {
						Socket socket;
						socket = ss.accept();
						users.add(new User(socket));
						String tell = "[���� ����]"
								+socket.getInetAddress();
						//onScreen(tell);
						Common.setNum(users.size());
						
					} catch (IOException e) {
						if(!ss.isClosed()) {
							try {
								stopS();
							} catch (Exception e1) {
								e1.printStackTrace();
								System.err.println("���� ������~");
							}
						}
						break;
					}
				}
			}
		};
		//������ ��� cpu�� ������ ���ٴ°� ����.
//		threadPool = Executors.newCachedThreadPool();
		//test �ܰ迡�� 10�� ���� �д�
		threadPool = Executors.newFixedThreadPool(10);
		threadPool.submit(thread);
	}

	public void stopS() {
		try {
			//��� ���� �ݱ�
			Iterator<User> iterator = users.iterator();
			while(iterator.hasNext()) {
				User next = iterator.next();
				next.getSocket().close();
				iterator.remove();
				Common.setNum(users.size());
			}
			//���� ���� �ݱ�
			if(ss!=null || !ss.isClosed()) {
				ss.close();
			}
			//������Ǯ ����
			if(threadPool!=null || !threadPool.isShutdown()) {
				threadPool.shutdown();
			}
			//onlinelist���� �� ����
			Connection conn = null;
			Statement stmt = null;
			PreparedStatement pstmt1 = null;
			ResultSet rs = null;
			String sql1 = "";
			try {//���� ���� ���� 4,5�ܰ�
				conn = ConnUtil.getConnection();
				//onlineuser����
				sql1 = "delete from onlineuser";
				pstmt1 = conn.prepareStatement(sql1);
				rs = pstmt1.executeQuery();
				
			}catch (Exception e) {
				e.printStackTrace();
				System.err.println("������ ������ Ȯ���� ���� ����� ����");
			}finally {// 6�ܰ�
				 try {if (rs != null) rs.close();	} catch (Exception e) {}
				 try {if (conn != null) conn.close();	} catch (Exception e) {}
				 try {if (pstmt1 != null) pstmt1.close();	} catch (Exception e) {}
			}
		} catch (Exception e) {
			System.out.println("�����ִ°Ծ���");
		}
		
		
	}
	
	@FXML
	public void startServer(ActionEvent event) throws Exception {
		startS("127.0.0.1", 7777);
		Common.setServerOn(true);
		startButton.setDisable(true);
		stopButton.setDisable(false);
		String message = "[��������]";
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
			String message = "[��������]";
			onScreen(message);
			onlineTextField.setText(Integer.toString(users.size()));
			Common.setNum(users.size());
			//serverTextArea.appendText(message);
		}catch (Exception e) {
			System.out.println("�־�");
		} 	
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1){
		textArea.setEditable(false);
		if (Common.isServerOn()) {
			serverTextArea.appendText("[���� ���� ��]\n");
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

	public void onLineUser() {//�¶��� ���� ���� ��� ������Ʈ�ϴ� �޼���
		try {
			Thread thread = new Thread() {
				public void run() {
					while(isHere) {
						//�¶��� ���� �� ǥ��
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
			System.err.println("���Ⱑ ����. ������ ���� ��");
		}
	}	
	public void onLineList() throws IllegalStateException{//�¶��� ������ ��� ǥ���ϴ� �޼���
		Thread thread = new Thread() {
			public void run() {
				while(true) {//������ �������� ��쿡��..
					Connection conn = null;
					String sql1 = null;
					PreparedStatement pstmt1 = null;
					ResultSet rs = null;
					try {//���� ���� ���� 4,5�ܰ�
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
						//1�ʸ��� ����Ʈ�� �ڵ����� ���ΰ�ħ��.
						try { sleep(1000); } catch (InterruptedException e) {}						
					}catch (Exception e) {
						e.printStackTrace();
						System.err.println("������ ������ Ȯ���� ���� ����� ����");
					}finally {// 6�ܰ�
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
	//â �ٲٴ� �޼ҵ�� 
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
//�ϳ��� Ŭ���̾�Ʈ�� ����ϰ� �ϴ� Ŭ���̾�Ʈ Ŭ����
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
	
	//��Ű���� �޶� private�� ���Ұ� public�޼���� ����.
	public Socket getSocket() {
		return socket;
	}
	
	public User(Socket socket) { 
		this.socket = socket; 
		receive();
	}
	// �ݺ������� �����κ��� �޼����� �޴� �޼ҵ�
	public void receive() {
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				try {
					while(true) { //�ݺ������� ���� ��
						InputStream in = socket.getInputStream();
						byte[] buffer = new byte[512];
						int length =in.read(buffer);
						if (length==-1) {
							throw new IOException();
						}
						String check = "[�޽��� ���� ����]"
								+socket.getInetAddress();
						System.out.println(check);
						//������ 0�������� ���̸�ŭ [3]�� ���ڵ�
						String message = new String(buffer,0,length,"UTF-8");
						System.out.println("���� �޼���: "+message);
						mess = message;
						//���� �޽����� �ݺ������� ��ο��� ����
						for (User all : LobbyServerController.users) {
							all.sendtoUser(message);
						}
						saveChat();
						saveGame();
						detailGamePlay();
					}
				} catch (Exception e) {
					try {
						System.out.println("[�޽��� ���� ����@@]"
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
	
	// ��ü �������� ����
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
						System.out.println("[�޽��� �۽� ����@@]"
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
	//ä�� ���� ����
	public void saveChat() {
		try {
			Connection conn = null;
			PreparedStatement pstmt1 = null;
			String sql1 = null;

			try {// ���� ���� ���� 4,5�ܰ�
				conn = ConnUtil.getConnection();
				// chess_user �� ���
				sql1 = "INSERT INTO chat_log(id,text,loc) VALUES(?,?,?)";
				pstmt1 = conn.prepareStatement(sql1);
				//���� �޼������� ��ġ, ���̵�, �޼��� �и��Ұ�
				String[] sep = mess.split(" : ");
				//sep[0]���� ��ġ+���̵� ����
				byte[] by = (sep[0]).getBytes("UTF-8");
				String saveRoom = new String(by,0,1);
				String saveId = new String(by,1,by.length-1,"UTF-8");
				if(sep.length == 1) {
					// " : "�� �и��� �ȵȰŴ� ���Ӱ���.
				}else {
					pstmt1.setString(1, saveId);
					if(saveRoom.equals("L")) {//�κ�
						pstmt1.setString(2, sep[1]);
						pstmt1.setString(3, "Lobby");
						pstmt1.executeUpdate();
					}else if(saveRoom.equals("G")) {//�κ�
						pstmt1.setString(2, sep[1]);
						pstmt1.setString(3, "Room1");
						pstmt1.executeUpdate();
					}else if(saveRoom.equals("H")) {//�κ�
						pstmt1.setString(2, sep[1]);
						pstmt1.setString(3, "Room2");
						pstmt1.executeUpdate();
					}else if(saveRoom.equals("I")) {//�κ�
						pstmt1.setString(2, sep[1]);
						pstmt1.setString(3, "Room3");
						pstmt1.executeUpdate();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("������ ������ Ȯ���� ���� ����� ����");
			} finally {// 6�ܰ�
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
	//���� ���� ���
	public void saveGame() {
		try {
			Connection conn = null;
			PreparedStatement pstmt1 = null;
			String sql1 = null;

			try {// ���� ���� ���� 4,5�ܰ�
				conn = ConnUtil.getConnection();
				//���� �޼��� ���� SR, WW, RE
				String[] spe = mess.split(":");
				//����
				if(spe[0].equals("SR1")||spe[0].equals("SR2")||spe[0].equals("SR3")) {
					nextSeq();
					sql1 = "insert into game_log(white,black) values(?,?)";
					pstmt1 = conn.prepareStatement(sql1);
					white = spe[1]; black = spe[2];
					pstmt1.setString(1, spe[1]);
					pstmt1.setString(2, spe[2]);
					pstmt1.executeUpdate();
					//�÷��� ��Ͽ� ���� ���̺� ����
					makeGameTable();
				//�¸�
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
				System.err.println("������ ������ Ȯ���� ���� ����� ����");
			} finally {// 6�ܰ�
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

			try {// ���� ���� ���� 4,5�ܰ�
				conn = ConnUtil.getConnection();
				// chess_user �� ���
				sql1 = "update chess_user set win = win +1, game = game +1 , rate = ((win+1)*100)/(game+1) where id = ? ";
				pstmt11 = conn.prepareStatement(sql1);
				//���� �޼������� ��ġ, ���̵�, �޼��� �и��Ұ�
					pstmt11.setString(1, white);
					pstmt11.executeUpdate();

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("������ ������ Ȯ���� ���� ����� ����");
			} finally {// 6�ܰ�
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

			try {// ���� ���� ���� 4,5�ܰ�
				conn = ConnUtil.getConnection();
				// chess_user �� ���
				sql1 = "update chess_user set win = win +1, game = game +1 , rate = ((win+1)*100)/(game+1) where id = ? ";
				pstmt1 = conn.prepareStatement(sql1);
				//���� �޼������� ��ġ, ���̵�, �޼��� �и��Ұ�
					pstmt1.setString(1, black);
					pstmt1.executeUpdate();

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("������ ������ Ȯ���� ���� ����� ����");
			} finally {// 6�ܰ�
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

			try {// ���� ���� ���� 4,5�ܰ�
				conn = ConnUtil.getConnection();
				// chess_user �� ���
				sql1 = "update chess_user set lose = lose +1, game = game +1 , rate = (win*100)/(game+1) where id =";
				pstmt1 = conn.prepareStatement(sql1);
				//���� �޼������� ��ġ, ���̵�, �޼��� �и��Ұ�
					pstmt1.setString(1, white);
					pstmt1.executeUpdate();

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("������ ������ Ȯ���� ���� ����� ����");
			} finally {// 6�ܰ�
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

			try {// ���� ���� ���� 4,5�ܰ�
				conn = ConnUtil.getConnection();
				// chess_user �� ���
				sql1 = "update chess_user set lose = lose +1, game = game +1 , rate = (win*100)/(game+1) where id = ?";
				pstmt1 = conn.prepareStatement(sql1);
				//���� �޼������� ��ġ, ���̵�, �޼��� �и��Ұ�
					pstmt1.setString(1, black);
					pstmt1.executeUpdate();

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("������ ������ Ȯ���� ���� ����� ����");
			} finally {// 6�ܰ�
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

			try {// ���� ���� ���� 4,5�ܰ�
				conn = ConnUtil.getConnection();
				// chess_user �� ���
				sql1 = "update chess_user set game = game +1 , rate = (win*100)/(game+1) where id = ?";
				pstmt1 = conn.prepareStatement(sql1);
				//���� �޼������� ��ġ, ���̵�, �޼��� �и��Ұ�
					pstmt1.setString(1, white);
					pstmt1.executeUpdate();

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("������ ������ Ȯ���� ���� ����� ����");
			} finally {// 6�ܰ�
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

			try {// ���� ���� ���� 4,5�ܰ�
				conn = ConnUtil.getConnection();
				// chess_user �� ���
				sql1 = "update chess_user set game = game +1 , rate = (win*100)/(game+1) where id = ? ";
				pstmt1 = conn.prepareStatement(sql1);
				//���� �޼������� ��ġ, ���̵�, �޼��� �и��Ұ�
					pstmt1.setString(1, black);
					pstmt1.executeUpdate();

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("������ ������ Ȯ���� ���� ����� ����");
			} finally {// 6�ܰ�
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

			try {// ���� ���� ���� 4,5�ܰ�
				conn = ConnUtil.getConnection();
				//���� �޼��� ���� SR, WW, RE
				sql2 = "select game_seq.nextval from dual";
				stmt1 = conn.createStatement();
				rs = stmt1.executeQuery(sql2);
				if(rs.next()) {
					NUMNUM = rs.getInt("nextval");
					Common.setNext(rs.getInt("nextval"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("������ ������ Ȯ���� ���� ����� ����");
			} finally {// 6�ܰ�
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
	//���Ӻ� ���̺��� ����(������ ���)
	public void makeGameTable() {
		try {
			Connection conn = null;
			Statement stmt1 = null;
			StringBuffer sql2 = new StringBuffer();
			ResultSet rs =null;

			try {// ���� ���� ���� 4,5�ܰ�
				conn = ConnUtil.getConnection();
				//user_id ���̺� ����
				sql2.append("create table game_");
				sql2.append(Integer.toString(Integer.parseInt(Common.getNext())+1));
				sql2.append("(player varchar2(20),startt varchar2(10) , destination varchar2(10), imsi int default tst.nextval)");
				stmt1 = conn.createStatement();
				rs = stmt1.executeQuery(sql2.toString());
				
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("������ ������ Ȯ���� ���� ����� ����");
			} finally {// 6�ܰ�
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
	//�������� �� ���̺� ���
	public void detailGamePlay() {
		try {
			Connection conn = null;
			PreparedStatement pstmt1 = null;
			String sql1 = null;

			try {// ���� ���� ���� 4,5�ܰ�
				conn = ConnUtil.getConnection();
				//SS1=��=��ư
				//DD1=��ư=TR1
				String[] spe = mess.split("=");
				//��������
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
				//������
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
				System.err.println("������ ������ Ȯ���� ���� ����� ����");
			} finally {// 6�ܰ�
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

			try {// ���� ���� ���� 4,5�ܰ�
				conn = ConnUtil.getConnection();
				//���� �޼��� ���� SR, WW, RE
				sql2 = "select tst.nextval from dual";
				stmt1 = conn.createStatement();
				rs = stmt1.executeQuery(sql2);
				if(rs.next()) {
					nextval_detailgame = rs.getInt("nextval");
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("������ ������ Ȯ���� ���� ����� ����");
			} finally {// 6�ܰ�
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

