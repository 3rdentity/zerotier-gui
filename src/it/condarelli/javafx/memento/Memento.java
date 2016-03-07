package it.condarelli.javafx.memento;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class used to restore a JavaFx controller's state.
 * Marshalled and unmarshalled using JAXB.
 * Can store a String, double, and int,
 * and have any number of other children of the same type.
 * 
 * @author Mauro Condarelli
 *
 */
@XmlRootElement
public class Memento {
	private String text;
	private double number;
	private int integer;
	@XmlElement(name = "child")
	private Map<String,Memento> children = new HashMap<>();
	
	private static Memento root;
	private static File getMementoFile() {
    String home = System.getProperty("user.home");
    File rootDir = new File(home, ".zerotier");
    if (!rootDir.exists()) {
    	rootDir.mkdirs();
    }
    File f = new File(rootDir, "memento");
    return f;
	}
	public static Memento get() {
		if (root == null) {
			root = loadMemento();
		}
		return root;
	}
	
	/**
	 * Method used to initialize the Memento object needed to restore the state
	 * of registered controllers.
	 *
	 */
	public static Memento loadMemento(File f) {
		Memento memento = null;
		try {
			JAXBContext context = JAXBContext.newInstance(Memento.class);
			Unmarshaller m = context.createUnmarshaller();
			//m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			Object o = m.unmarshal(f);
			if (o instanceof Memento) {
				memento = (Memento) o;
			}
		} catch (Exception e) {
			if (!(e.getCause() instanceof FileNotFoundException))
				e.printStackTrace();
		}

		if (memento == null) {
			memento = new Memento();
		}
		return memento;
	}
	
	public static Memento loadMemento() {
		return loadMemento(getMementoFile());
	}

	/**
	 * Gets this Memento integer value.
	 * 
	 * @return an int value representing some integer to be restored
	 */
	public int getInteger() {
		return integer;
	}
	
	/**
	 * Gets this Memento text value.
	 * 
	 * @return a String instance representing some text to be restored
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Gets this Memento numerical value.
	 * 
	 * @return a double value representing some floating point number to be restored
	 */
	public double getNumber() {
		return number;
	}
	
	/**
	 * Sets this Memento integer value.
	 * 
	 * @param integer: the int value representing some integer to be saved
	 */
	public void setInteger(int integer) {
		this.integer = integer;
	}
	
	/**
	 * Sets this Memento numerical value.
	 * 
	 * @param number: the double value representing some floating point number to be saved
	 */
	public void setNumber(double number) {
		this.number = number;
	}
	
	/**
	 * Sets this Memento text value.
	 * 
	 * @param text: the String instance representing some text to be saved
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * Gets a Memento child from the Map of all available children that belong to this Memento.
	 * 
	 * @param name: the name of the memento child one needs
	 * @return a Memento child instance
	 */
	public Memento getExistingChild(String name) {
		return children.get(name);
	}
	
	public Memento getChild(String name) {
		Memento m = children.get(name);
		if( m == null ) {
			m = new Memento();
			children.put(name, m);
		}
		
		return m;
	}


	public static void storeMemento(File f, Memento memento) {
		// saves the memento in order for it to be available for future
		// restoring operations
		if (memento != null) {
			try {
				JAXBContext context = JAXBContext.newInstance(Memento.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				m.marshal(memento, f);
				//m.marshal(memento, System.out);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public static void save() {
		if (root != null)
			storeMemento(getMementoFile(), root);
	}
}