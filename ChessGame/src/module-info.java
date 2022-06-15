module ChessGame {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.sql;
	requires javafx.graphics;
	requires java.desktop;
	requires javafx.swing;
	requires ojdbc8;
	requires javafx.base;
	
	opens application to javafx.graphics, javafx.fxml;
}
