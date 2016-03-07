package it.condarelli.javafx.memento;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import it.condarelli.javafx.FxLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 * Factory to be used to register a RestorableController within the Application,
 * and to restore its state through a Memento.
 * 
 * @author Tom Schindl
 *
 */
public class FxRestorable extends FxLoader {

	private static class Restorable {
		private static final List<Restorable> l = new LinkedList<>();
		private final Memento m;
		private final RestorableController c;
		Restorable(Memento m, RestorableController c) {
			this.m = m.getChild(c.getClass().getSimpleName());
			this.c = c;
			l.add(this);
		}
		static void save() {
			for (Restorable r : l) {
				r.c.doSave(r.m);
			}
		}
		static void restore() {
			for (Restorable r : l) {
				r.c.doRestore(r.m);
			}
		}
	}

	public static Node load(URL url, Memento m, ResourceBundle rb) {
		FXMLLoader l = new FXMLLoader(url, rb);
		return doLoad(l, url, m);
	}
	
	public static Node load(URL url, Memento m) {
		FXMLLoader l = new FXMLLoader(url);
		return doLoad(l, url, m);
	}

	protected static Node doLoad(FXMLLoader l, URL url, Memento m) {
		Node n = doLoad(l, url);
		if (n != null) {
			Object o = getController(n);
			if ( m != null && o instanceof RestorableController ) {
				new Restorable(m, (RestorableController) o);				
			}
		}
		return n;
	}

	public static void save() {
		Restorable.save();
	}

	public static void restore() {
		Restorable.restore();
	}

}