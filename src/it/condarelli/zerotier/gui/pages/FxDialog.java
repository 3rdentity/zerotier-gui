package it.condarelli.zerotier.gui.pages;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class FxDialog extends FxController {

	public static Node create(Stage stage, StageStyle style, Class<? extends FxController> clazz) {
		Node n = FxController.create(clazz);
		if (stage == null) {
			stage = new Stage(style);
		}
		stage.setTitle(getTitle());
		Pane layout = new StackPane(n);
		Scene scene = new Scene(layout);
		stage.setScene(scene);
		scene.getStylesheets().add(clazz.getResource("application.css").toExternalForm());
		scene.getStylesheets().add(clazz.getResource("validation.css").toExternalForm());
		stage.show();
		return n;
	}

	public static void close(Node n) {
		Window w = n.getScene().getWindow();
		if (w instanceof Stage)
			((Stage) w).close();
		else
			w.hide();

	}

}
