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
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class Main extends Application {
	public final static boolean	use_dialogs	= true;
	private BorderPane					root;

	@Override
	public void start(Stage primaryStage) {
		try {
			if (!use_dialogs)
				root = (BorderPane) FxLoader.load(MainWindow.class);
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
						if (use_dialogs) {
							Window w = n.getScene().getWindow();
							if (w instanceof Stage)
								((Stage) w).close();
							else
								w.hide();
						}
						Node r;
						r = FxController.create(c, Status.class);
						registerStatus(r);
						if (use_dialogs) {
							Stage status = new Stage(StageStyle.UTILITY);
							status.setTitle("ZeroTier Client Status");
							Pane layout = new StackPane(r);
							status.setScene(new Scene(layout));
							status.show();
						} else {
							root.setTop(r);
						}
						r = FxController.create(c, Controller.class);
						registerDispatcher(r);
						if (use_dialogs) {
							Stage controller = new Stage(StageStyle.UTILITY);
							controller.setTitle("ZeroTier Controller Status");
							Pane layout = new StackPane(r);
							controller.setScene(new Scene(layout));
							controller.show();
						} else {
							root.setBottom(r);
						}
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
