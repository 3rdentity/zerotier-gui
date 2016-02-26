package it.condarelli.zerotier.gui;

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
			Node n = Controller.create(Login.class);
			registerLogin(n);
			root.setTop(n);
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void registerLogin(Node n) {
		Controller c = FxLoader.getController(n);
		if (c != null)
			c.register(new Callback<String, String>() {
				@Override
				public String call(String param) {
					if (param.equals("LOG.OK")) {
						Node n = Controller.create(Status.class);
						root.setTop(n);
						registerStatus(n);
					}
					return null;
				}
			});
	}

	private void registerStatus(Node n) {
		Controller c = FxLoader.getController(n);
		if (c != null)
			c.register(new Callback<String, String>() {
				@Override
				public String call(String param) {
					switch (param) {
					case "CTR.PRESENT":
						Node n = Controller.create(Dispatcher.class);
						root.setCenter(n);
						registerDispatcher(n);
					}
					return null;
				}

				private void registerDispatcher(Node n) {
					// TODO Auto-generated method stub
					
				}
			});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
