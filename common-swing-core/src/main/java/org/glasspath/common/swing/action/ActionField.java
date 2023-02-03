package org.glasspath.common.swing.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.plaf.ComponentUI;

public class ActionField extends JComponent {

	protected final JComponent field;
	protected final AbstractAction action;

	protected final List<ActionListener> actionListeners = new ArrayList<>();

	public ActionField() {
		this(new JTextField());
	}

	public ActionField(JComponent field) {
		this(field, null);
	}

	public ActionField(JComponent field, AbstractAction action) {

		this.field = field;
		this.action = action;

		add(field);

		setFocusable(true);

		// TODO?
		setUI(new FlatActionFieldUI());

	}

	@Override
	public void updateUI() {
		setUI(new FlatActionFieldUI());
	}

	public JComponent getField() {
		return field;
	}

	public AbstractAction getAction() {
		return action;
	}

	// TODO?
	@Override
	public boolean hasFocus() {
		return field.hasFocus();
	}

	public void addActionListener(ActionListener listener) {
		actionListeners.add(listener);
	}

	public void removeActionListener(ActionListener listener) {
		actionListeners.remove(listener);
	}

	protected void fireActionPerformed(ActionEvent e) {
		for (ActionListener listener : actionListeners) {
			listener.actionPerformed(e);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		field.setEnabled(enabled);

		ComponentUI ui = getUI();
		if (ui instanceof FlatActionFieldUI) {
			((FlatActionFieldUI) ui).button.setEnabled(enabled);
		}

	}

}
