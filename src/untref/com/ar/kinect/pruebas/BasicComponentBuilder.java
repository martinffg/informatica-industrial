package untref.com.ar.kinect.pruebas;

import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;

import java.awt.Insets;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class BasicComponentBuilder {

		private Form form;
		private JPanel panel;		

		
public BasicComponentBuilder(Form form, JPanel panel){
	
	this.setForm(form);
	this.setPanel(panel);
	
		
}

public JRadioButton construirRadioButton(String nombre, int posX, int posY){
	
	JRadioButton radioButton = new JRadioButton(nombre);
	
	GridBagConstraints c = new GridBagConstraints();
	c.weightx = 1;
	c.anchor = GridBagConstraints.WEST;
	c.gridx = posX;
	c.gridy = posY;
	c.insets = new Insets(5, 5, 5, 5);
	
	radioButton.setFocusPainted(false);
	radioButton.setVisible(false);
	
	this.form.getContentPane().add(panel, c);
	this.panel.add(radioButton, c);
	
	return radioButton;
}


public JButton construirButton(String nombre, int posX, int posY){
	
	JButton button = new JButton(nombre);
	
	GridBagConstraints c = new GridBagConstraints();
	c.weightx = 1;
	c.anchor = GridBagConstraints.WEST;
	c.gridx = posX;
	c.gridy = posY;
	c.insets = new Insets(5, 5, 5, 5);
	
	button.setFocusPainted(false);
	button.setVisible(false);
	
	this.form.getContentPane().add(panel, c);
	this.panel.add(button, c);
	
	return button;
}


public JTextField construirInputText(int posX, int posY){
	
	JTextField input =new JTextField();
	
	GridBagConstraints c = new GridBagConstraints();
	c.weightx = 1;
	c.anchor = GridBagConstraints.EAST;
	c.gridx = posX;
	c.gridy = posY;
	c.insets = new Insets(5, 5, 5, 5);
	
	input.setPreferredSize(new Dimension(75,25));
	input.setVisible(false);
	
	
	this.form.getContentPane().add(panel, c);
	this.panel.add(input, c);
	
	return input;
}

public JComboBox<String> construirCombo(int posX, int posY, String[] opciones){
	
	JComboBox<String> combo = new JComboBox<String>(opciones);
	
	GridBagConstraints c = new GridBagConstraints();
	c.weightx = 1;
	c.anchor = GridBagConstraints.EAST;
	c.gridx = posX;
	c.gridy = posY;
	c.insets = new Insets(5, 5, 5, 5);
	
	combo.setPreferredSize(new Dimension(75,25));
	combo.setSelectedIndex(0);//Por defecto el combo se crea con el primer elemento seleccionado
	combo.setVisible(false);
	
	this.form.getContentPane().add(panel, c);
	this.panel.add(combo, c);
	
	return combo;
}

public JLabel construirLabel(String nombre, int posX, int posY){
	
	JLabel label = new JLabel(nombre);
	
	GridBagConstraints c = new GridBagConstraints();
	c.anchor = GridBagConstraints.WEST;
	c.gridx = posX;
	c.gridy = posY;
	c.insets = new Insets(5, 5, 5, 5);

	label.setVisible(false);
	this.form.getContentPane().add(panel, c);
	this.panel.add(label, c);	
	return label;	
	
}

public JScrollBar construirScrollBar(int posX,int posY){
		
	final JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
	scrollBar.setMaximum(100);
	scrollBar.setMinimum(0);
	scrollBar.setVisibleAmount(0);
	scrollBar.setValue(50);
	scrollBar.addAdjustmentListener(new AdjustmentListener() {

		@Override
		public void adjustmentValueChanged(AdjustmentEvent arg0) {

			form.setAlpha((float) scrollBar.getValue() / 100);
		}
	});
	
	scrollBar.setVisible(false);
	
	GridBagConstraints c = new GridBagConstraints();
	c.weightx = 1;	
	c.anchor = GridBagConstraints.WEST;
	c.fill = GridBagConstraints.HORIZONTAL;
	c.gridx = posX;
	c.gridy = posY;
	c.insets = new Insets(5, 5, 5, 5);					
	panel.add(scrollBar, c);
	
	return scrollBar;
	
}

public Form getForm() {
	return form;
}


public void setForm(Form form) {
	this.form = form;
}


public JPanel getPanel() {
	return panel;
}


public void setPanel(JPanel panel) {
	this.panel = panel;
}
	
}
