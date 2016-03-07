package it.condarelli.zerotier.gui;

import it.condarelli.zerotier.gui.memento.Memento;
import it.condarelli.zerotier.gui.pages.Controller;
import it.condarelli.zerotier.gui.pages.FxController;
import it.condarelli.zerotier.gui.pages.FxDialog;
import it.condarelli.zerotier.gui.pages.FxLoader;
import it.condarelli.zerotier.gui.pages.Login;
import it.condarelli.zerotier.gui.pages.Status;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class Main extends Application {
	private BorderPane					root;

	@Override
	public void start(Stage primaryStage) {
		try {
			Node n = FxDialog.create(primaryStage, StageStyle.UTILITY, Login.class);
			registerLogin(n);
			primaryStage.setWidth(350);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		Memento.save();
	}
	private void registerLogin(Node n) {
		FxController c = FxLoader.getController(n);
		if (c != null)
			FxDialog.register(n, (String param) -> {
					if (param.equals("LOG.OK")) {
							Window w = n.getScene().getWindow();
							if (w instanceof Stage)
								((Stage) w).close();
							else
								w.hide();
						Node r;
						r = FxDialog.create(c, Status.class);
						registerStatus(r);
						r = FxDialog.create(c, Controller.class);
						registerDispatcher(r);
					}
			});
	}

	private void registerStatus(Node n) {
		FxController.register(n, (String param) -> {
					switch (param) {
					case "CTR.PRESENT":
						Node r = FxController.create(Controller.class);
						root.setCenter(r);
						registerDispatcher(r);
					}
			});
	}

	private void registerDispatcher(Node n) {
		FxController.register(n, (String param) -> {
					switch (param) {
					case "CTR.PRESENT":
						Node r = FxController.create(Controller.class);
						root.setCenter(r);
						registerDispatcher(r);
					}
			});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
