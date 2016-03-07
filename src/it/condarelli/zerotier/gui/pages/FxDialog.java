package it.condarelli.zerotier.gui.pages;

import it.condarelli.zerotier.gui.memento.Memento;
import it.condarelli.zerotier.gui.memento.RestorableController;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class FxDialog extends FxController implements RestorableController {

	static public Node create(FxController parent, Class<? extends FxController> clazz) {
		return create(parent, null, StageStyle.UTILITY, clazz);
	}

	public static Node create(Stage stage, StageStyle style, Class<? extends FxController> clazz) {
		return create(null, stage, style, clazz);
	}
	public static Node create(FxController parent, Stage stage, StageStyle style, Class<? extends FxController> clazz) {
		Node n = FxController.create(clazz);
		if (stage == null) {
			stage = new Stage(style);
		}
		stage.setTitle(FxLoader.getController(n).getTitle());
		Pane layout = new StackPane(n);
		Scene scene = new Scene(layout);
		stage.setScene(scene);
		scene.getStylesheets().add(clazz.getResource("application.css").toExternalForm());
		scene.getStylesheets().add(clazz.getResource("validation.css").toExternalForm());
		FxController c = FxLoader.getController(n);
		if (c instanceof FxDialog) {
			FxDialog d = (FxDialog)c;
			d.setupMemento(stage);
			if (parent != null)
				d.properties.put(FxAdapter.parent, parent);
			d.initialize(parent);
		}
		stage.show();
		return n;
	}

	protected void initialize(FxAdapter parent) {}

	public static void close(Node n) {
		Window w = n.getScene().getWindow();
		if (w instanceof Stage)
			((Stage) w).close();
		else
			w.hide();
	}
	
	protected FxDialog(String title) {
		super(title);
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}
	@Override
	public void doRestore(Memento m) {}
	@Override
	public void doSave(Memento m) {}
	
	private void _doRestore(Memento m, Stage s) {
		Memento c;
		c = m.getExistingChild("_Window_X");
		if (c != null)
			s.setX(c.getNumber());
		c = m.getExistingChild("_Window_Y");
		if (c != null)
			s.setY(c.getNumber());
		c = m.getExistingChild("_Window_W");
		if (c != null)
			s.setWidth(c.getNumber());
		c = m.getExistingChild("_Window_H");
		if (c != null)
			s.setHeight(c.getNumber());
		doRestore(m);
	}
	private void _doSave(Memento m) {
		Window w = root.getScene().getWindow();
		m.getChild("_Window_X").setNumber(w.getX());
		m.getChild("_Window_Y").setNumber(w.getY());
		m.getChild("_Window_W").setNumber(w.getWidth());
		m.getChild("_Window_H").setNumber(w.getHeight());
		doSave(m);
	}
  private void setupMemento(Stage s) {
		Memento m = Memento.get().getChild(getName());
		s.setOnHidden(event -> {
			_doSave(m);
		});
		_doRestore(m, s);
	}

}
