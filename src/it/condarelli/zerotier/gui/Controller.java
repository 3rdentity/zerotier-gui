package it.condarelli.zerotier.gui;

import java.util.LinkedList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

public class Controller {
	protected Node root = null;
	protected List<Callback<String, String>> listeners = new LinkedList<>(); 
	
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
	
	static public Node create(Class<? extends Controller> clazz) {
		Node n = FxLoader.load(clazz);
		Controller c = FxLoader.getController(n);
		if (c != null)
			c.setNode(n);
		return n;
	}
	
	public void fire(String s) {
		for (Callback<String, String> c : listeners) {
			c.call(s);
		}
	}
	public void register(Callback<String, String> c) {
		listeners.add(c);
	}
	public void release(Callback<String, String> c) {
		listeners.remove(c);
	}
}
