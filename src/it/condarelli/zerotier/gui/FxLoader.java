package it.condarelli.zerotier.gui;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class FxLoader {
	protected static Map<URL, Object>		controllerMap	= new HashMap<>();
	protected static Map<Node, Object>	rootMap				= new HashMap<>();

	public static Node load(URL url, ResourceBundle rb) {
		FXMLLoader l = new FXMLLoader(url, rb);
		return doLoad(l, url);
	}

	public static Node load(URL url) {
		FXMLLoader l = new FXMLLoader(url);
		return doLoad(l, url);
	}

	public static Node load(Class<?> clazz) {
		try {
			Object c;
			c = clazz.newInstance();

			String s = clazz.getSimpleName() + ".fxml";
			URL url = clazz.getResource(s);
			FXMLLoader l = new FXMLLoader(url);
			l.setController(c);
			return doLoad(l, url);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected static Node doLoad(FXMLLoader l, URL url) {
		try {
			Node n = (Node) l.load();
			Object controller = l.getController();
			controllerMap.put(url, controller);
			rootMap.put(n, controller);
			return n;
		} catch (IOException e) {
			System.err.println("Unable to load '" + url.toString() + "':" + e.getMessage());
		}
		return null;
	}
	
	public static Controller getController(Node n) {
		Object o = rootMap.get(n);
		if (o instanceof Controller)
			return (Controller) o;
		return null;
	}
	
	public static Controller getController(URL u) {
		Object o = controllerMap.get(u);
		if (o instanceof Controller)
			return (Controller) o;
		return null;
	}
	
	public static Object getControllerObject(URL u) {
		return controllerMap.get(u);
	}
	
	public static Object getControllerObject(Node n) {
		return rootMap.get(n);
	}

}
