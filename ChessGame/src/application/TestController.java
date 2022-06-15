package application;

import java.awt.Frame;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TestController implements Initializable {
	static Frame frame;
	static Stage stage;

	@FXML
	Canvas can;
	@FXML
	Button button;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		GraphicsContext g = can.getGraphicsContext2D();
		boolean white = true;
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				if (white) {
					g.setFill(Color.IVORY);
					
				} else {
					g.setFill(Color.BLACK);

				}
				g.fillRect(x * 45, y * 45, 45, 45); // (x,y,w,h)
				white = !white;
			}
			white = !white;
		}

		can.setOnMouseClicked(e -> {

		});
		can.setOnMouseDragged(null);
		can.setOnMousePressed(null);
		can.setOnMouseReleased(null);
	}

	public void draw() {

	}

	public void Hing(ActionEvent event) {

	}

}
