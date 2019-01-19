package edu.vcu.cyber.dashboard.ui.docking;

import javax.swing.*;
import java.util.List;

public interface DockingItem extends Dockable
{
	/**
	 * @param item - the object that is trying to be docked
	 * @param pos  - argument for position to attempt to be docked
	 * @return true if successfully docked
	 */
	boolean dock(JComponent item, int pos);

	/**
	 * @param item - the object that is trying to be docked
	 * @return true if successfully docked
	 */
	boolean dock(JComponent item);

	/**
	 * @param item - item to remove from the dock
	 */
	void undock(JComponent item);

	/**
	 * Determines if it can be undocked
	 * (be removed from)
	 */
	boolean canUndock();

	/**
	 * Determines if it can be docked
	 * (be added to)
	 */
	boolean canDock();

	/**
	 * @return the component to move around
	 */
	JComponent getComponent();

	DockingItem getParentContainer();

	void setParentContainer(DockingItem item);


}
