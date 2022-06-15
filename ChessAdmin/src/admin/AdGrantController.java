package admin;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import chess.database.Common;
import chess.database.ConnUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class AdGrantController implements Initializable{
    @FXML
    private Label adminLabel;
    @FXML
    private Button cancelButton;
    @FXML
    private ToggleGroup grant;
    @FXML
    private RadioButton grantButton;
    @FXML
    private RadioButton revokeButton;
    @FXML
    private Button updateButton;

    @FXML
    public void cancel(ActionEvent event) {

    }
    @FXML
    public void updatePermiission(ActionEvent event) {
    	
    	if(grantButton.isSelected()) {
    		Common.setAdminGrant("Y");
    	}else {Common.setAdminGrant("N");}
    	
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	String sql = "";
    	try {
			conn = ConnUtil.getConnection();
			sql = "update chess_admin set permission = ? where id = ?";
			ps = conn.prepareStatement(sql);
			String permit = Common.getAdminGrant().equals("Y") ? "y" : "n";
			ps.setString(1,permit);
			ps.setString(2,Common.getDetailAdmin());
			rs = ps.executeQuery();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try{if(!rs.isClosed()) rs.close();}catch(Exception e){}
			try{if(!conn.isClosed()) conn.close();}catch(Exception e){}
			try{if(!ps.isClosed()) ps.close();}catch(Exception e){}
		}
    	
    }
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		adminLabel.setText(Common.getDetailAdmin());
		if(Common.getAdminGrant().equals("Y")) {
			grantButton.setSelected(true);
		}else {
			revokeButton.setSelected(true);
		}
	}
    
    
    
}
