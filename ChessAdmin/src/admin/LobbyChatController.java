package admin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import observableList.ChatLog;

public class LobbyChatController implements Initializable{
	private Parent root;
	private Stage stage;
	private Scene scene;
	String sql1="";
	@FXML
	private Button logoutButton, toAdminButton, toChatButton, toGameButton;
	@FXML
	private Button toRankButton, toServerButton, toUserButton,showAllButton	;
    @FXML
    private TableView<ChatLog> charTableView;
    @FXML
    private TableColumn<ChatLog, String> seqCol;
    @FXML
    private TableColumn<ChatLog, String> timeCol;
    @FXML
    private TableColumn<ChatLog, String> idCol;
    @FXML
    private TableColumn<ChatLog, String> textCol;
    @FXML
    private TableColumn<ChatLog, String> locCol;
    private FileChooser fileChooser = new FileChooser();
    private int seq;
    private String time,id,text,loc;
    @FXML
    private TextField onlineTextField;
    @FXML
    private Button saveButton, deleteButton;
    @FXML
    private TextArea serverTextArea;
    ObservableList<ChatLog> log = FXCollections.observableArrayList();

    @FXML
    public void deleteChat(ActionEvent event) {
    	charTableView.getItems().clear();
    }
	@Override
	public void initialize(URL location, ResourceBundle arg1) {
		sql1 = "select * from chat_log where (to_char(datee,'rrrr-mm-dd')='"+ Common.getToday() +"') order by chat_num";
		displayChat();  
		disOnline();
	}
    public void saveFile(ObservableList<ChatLog> log , File file) {
    	try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write("NO.\tTIME\t\t\tID\tTEXT\n");
			for(ChatLog logs : log) {
				out.write(logs.ToString());
				out.newLine();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	@FXML
    public void saveChat(ActionEvent event) {
		//txt ���Ϸ� ���� 
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		//����ȭ��.. ������� ��ΰ� �ٸ��ٸ� ������̺�
		if((new File(System.getProperty("user.home")+"\\Desktop")).canRead()) {
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home")+"\\Desktop"));
		}else {
			fileChooser.setInitialDirectory(new File("c:/"));
		}
		File file = fileChooser.showSaveDialog(new Stage());
		if(file != null) {
			saveFile(log,file);
		}
	}
	public void displayChat() {
    	onlineTextField.setText(Integer.toString(Common.getNum()));
		charTableView.setPlaceholder(new Label("[��ȸ] ��ư�� �����ּ���"));
		getDBLog();
		
		seqCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSeq()));
    	timeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTime()));
    	idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
    	textCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getText()));
    	locCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLoc()));
    	
    	charTableView.setItems(log);
    	onlineTextField.setText(Integer.toString(Common.getNum()));
    	Platform.runLater( () -> charTableView.scrollTo(charTableView.getItems().size()-1) );
    
	}
	public void disOnline() {
		Thread thread = new Thread() {
			public void run() {
				while(true) {
					Platform.runLater(()->{onlineTextField.setText(Integer.toString(Common.getNum()));});
					try {
						sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
	}
	// �ϳ��� �����ϴ°�
	public void clearTable() {
		charTableView.getItems().clear();
		//adminTableView.refresh();
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
			sql1 = "select * from chat_log where (to_char(datee,'rrrr-mm-dd')='"+ selectedDate +"') order by chat_num";
			displayChat(); 
						
		});
	}
    public void showAll(ActionEvent event) {
    	clearTable();
    	sql1 = "select * from chat_log order by chat_num";
    	displayChat(); 
    }
    
	public void getDBLog() {
		Connection conn = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		
		try {//���� ���� ���� 4,5�ܰ�
			conn = ConnUtil.getConnection();
			pstmt1 = conn.prepareStatement(sql1);
			rs = pstmt1.executeQuery();
			
			while(rs.next()) { // ��ġ�ϴ°� �ִٸ�?
				seq = rs.getInt("chat_num");
				time = rs.getString("time");
				id = rs.getString("id");
				text = rs.getString("text");
				loc = rs.getString("loc");
				log.add(new ChatLog(seq,time,id,text,loc));
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
