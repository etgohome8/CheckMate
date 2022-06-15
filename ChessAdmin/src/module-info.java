module ChessAdmin {
	requires javafx.controls;
	requires java.sql;
	requires javafx.fxml;
	requires javafx.base;
	requires javafx.graphics;
	
	opens admin to javafx.graphics, javafx.fxml;
}
