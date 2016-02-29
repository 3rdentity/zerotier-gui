package it.condarelli.zerotier.gui;

import it.condarelli.zerotier.gui.pages.FxController;
import it.condarelli.zerotier.gui.pages.Controller;
import it.condarelli.zerotier.gui.pages.FxLoader;
import it.condarelli.zerotier.gui.pages.Login;
import it.condarelli.zerotier.gui.pages.Status;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Main extends Application {
	private BorderPane root;

	@Override
	public void start(Stage primaryStage) {
		try {
			root = (BorderPane) FxLoader.load(MainWindow.class);
			Node n = FxController.create(Login.class);
			registerLogin(n);
			root.setTop(n);
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			scene.getStylesheets().add(getClass().getResource("validation.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void registerLogin(Node n) {
		FxController c = FxLoader.getController(n);
		if (c != null)
			c.register(new Callback<String, String>() {
				@Override
				public String call(String param) {
					if (param.equals("LOG.OK")) {
						Node n = FxController.create(Status.class);
						root.setTop(n);
						registerStatus(n);
						n = FxController.create(Controller.class);
						root.setBottom(n);
						registerDispatcher(n);
					}
					return null;
				}
			});
	}

	private void registerStatus(Node n) {
		FxController c = FxLoader.getController(n);
		if (c != null)
			c.register(new Callback<String, String>() {
				@Override
				public String call(String param) {
					switch (param) {
					case "CTR.PRESENT":
						Node n = FxController.create(Controller.class);
						root.setCenter(n);
						registerDispatcher(n);
					}
					return null;
				}
			});
	}

	private void registerDispatcher(Node n) {
		FxController c = FxLoader.getController(n);
		if (c != null)
			c.register(new Callback<String, String>() {
				@Override
				public String call(String param) {
					switch (param) {
					case "CTR.PRESENT":
						Node n = FxController.create(Controller.class);
						root.setCenter(n);
						registerDispatcher(n);
					}
					return null;
				}
			});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
