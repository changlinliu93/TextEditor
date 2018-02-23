package TextEditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ColorBoxRenderer extends JComponent implements ListCellRenderer{

	Color color;
	public void paintComponent(Graphics g) {
		g.setColor(color);
		g.fillRect(0, 0, getWidth(), getHeight());
		}
	
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		d.height=18;
		d.width=30;
		return d;
		}
	
	
	public Component getListCellRendererComponent(JList list,
             Object value,
             int index,
             boolean isSelected,
             boolean cellHasFocus) {
			 	
		if (value instanceof Color) {
			color = (Color) value;
			if (isSelected) color = color.darker();		
		}
		 
		return this;
		 
	 }


}
