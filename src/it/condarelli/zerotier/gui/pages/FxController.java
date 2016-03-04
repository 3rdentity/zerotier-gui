package it.condarelli.zerotier.gui.pages;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class FxController implements FxAdapter {
	protected static String title = "Untitled";
	
	public interface Signal {
		public void handle(String param);
	}
	protected Node root = null;
	protected Map<String, FxAdapter> properties = new HashMap<>();
	protected List<Signal> listeners = new LinkedList<>(); 
	
	public Node getNode() {
		return root;
	}
	
	protected void setNode(Node n) {
		root = n;
	}
	
	public Pane getPane() {
		Node n = getNode();
		if (n instanceof Pane) {
			return (Pane) n;
		}
		return null;
	}

	static public Node create(Class<? extends FxController> clazz) {
		return create(null, clazz);
	}
	static public Node create(FxController parent, Class<? extends FxController> clazz) {
		Node n = FxLoader.load(clazz);
		FxController c = FxLoader.getController(n);
		if (c != null) {
			c.setNode(n);
			c.properties.put(FxAdapter.parent, parent);
		}
		return n;
	}
	
	static public String getTitle() {
		return title;
	}
	
	public void fire(String s) {
		for (Signal c : listeners) {
			c.handle(s);
		}
	}
	public void register(Signal cb) {
		listeners.add(cb);
	}
	public void release(Signal c) {
		listeners.remove(c);
	}
	public static void register(Node n, Signal cb) {
		FxController c = FxLoader.getController(n);
		if (c != null)
			c.register(cb);
	}

}
