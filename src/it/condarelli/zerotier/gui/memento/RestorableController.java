package it.condarelli.zerotier.gui.memento;

/**
 * The base class for a JavaFX controller which is meant to be able to restore
 * its state.
 * 
 * @author Tom Schindl
 *
 */
public interface RestorableController {
	/**
	 * Restores a previous state of the child controller by using a specific Memento.
	 * Actual implementation will be in the concrete child controller.
	 * 
	 * @param m: the Memento carrying the info to be restored
	 */
	void doRestore(Memento m);
	
	/**
	 * Saves the state of the child controller by using a specific Memento.
	 * Actual implementation will be in the concrete child controller.
	 * 
	 * @param m: the Memento carrying the info to be saved
	 */
	void doSave(Memento m);
	
}