package admin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class servertest {
	private Parent root;
	private Stage stage;
	private Scene scene;
	// ������ ������ ThreadPool
	public static String onScreen;
	public static ExecutorService threadPool;
	public static Vector<UserTest> users = new Vector<UserTest>();
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
	//private ListView<String> onlineList;
	//public ObservableList<String> userId;
	
	public String nick,id;
	
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
	
	// �̰� �����־� �� �ȵ�!!!
	public void onScreen(String message) {
		System.out.println("�� textarea���ȶ�???"+message);
		serverTextArea.appendText(message+"\n");
	}
	// ���� ���ž�
	public void startS(String IP, int port) {
		try {
			ss = new ServerSocket();
			ss.bind(new InetSocketAddress(IP,port));
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
						users.add(new UserTest(socket));
						String tell = "[���� ����]"
								+socket.getInetAddress();
						onScreen(tell);
//						userId = FXCollections.observableArrayList();
//						onlineList.setItems(userId);
						onlineTextField.setText(Integer.toString(users.size()));
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
			Iterator<UserTest> iterator = users.iterator();
			while(iterator.hasNext()) {
				UserTest next = iterator.next();
				next.getSocket().close();
				iterator.remove();
			}
			//���� ���� �ݱ�
			if(ss!=null && !ss.isClosed()) {
				ss.close();
			}
			//������Ǯ ����
			if(threadPool!=null && !threadPool.isShutdown()) {
				threadPool.shutdown();
			}
		} catch (Exception e) {
			System.out.println("�����ִ°Ծ���");
		}
	}
	
	@FXML
	public void startServer(ActionEvent event) throws Exception {
		startS("127.0.0.1", 7777);
		startButton.setDisable(true);
		stopButton.setDisable(false);
		String message = "[��������]";
		onScreen(message);
		onlineTextField.setText(Integer.toString(users.size()));
		//serverTextArea.appendText(message);
	}
	
	@FXML
	public void stopServer(ActionEvent event) throws Exception {
		try {
			stopS();
			startButton.setDisable(false);
			stopButton.setDisable(true);
			String message = "[��������]";
			onScreen(message);
			onlineTextField.setText(Integer.toString(users.size()));
			//serverTextArea.appendText(message);
		}catch (Exception e) {
			System.out.println("�־�");
		} 	
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

////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

class UserTest {
	private Socket socket; 
	private String id;
	private String nick;
	private String mess;
	
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
	
	//��Ű���� �޶� private���� ���Ұ� public�޼���� ����.
	public Socket getSocket() {
		return socket;
	}
	
	public UserTest(Socket socket) { 
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
						for (UserTest all : servertest.users) {
							all.sendtoUser(message);
						}
					}
				} catch (Exception e) {
					try {
						System.out.println("[�޽��� ���� ����@@]"
								+ socket.getInetAddress());
								//+": "+Thread.currentThread().getName());
						servertest.users.remove(UserTest.this);
						socket.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
				
			}
			
		};
		servertest.threadPool.submit(thread);
	}
	// servertest
	
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
						servertest.users.remove(UserTest.this);
						socket.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		};
		servertest.threadPool.submit(thread);
	}
}
