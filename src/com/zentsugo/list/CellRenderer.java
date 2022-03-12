package com.zentsugo.list;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

import com.zentsugo.utils.Converter;
import com.zentsugo.utils.Listener;

@SuppressWarnings("serial")
public class CellRenderer extends DefaultListCellRenderer {

    private boolean pad;
    private Border padBorder = new EmptyBorder(3,3,3,3);

    public CellRenderer(boolean pad) {
        this.pad = pad;
    }

    @SuppressWarnings("rawtypes")
	@Override
    public Component getListCellRendererComponent(
        JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus) {

        Component c = super.getListCellRendererComponent(
            list,value,index,isSelected,cellHasFocus);
        JLabel l = (JLabel)c;
        File f = (File)value;
        l.setText(f.getName());
        l.setIcon(FileSystemView.getFileSystemView().getSystemIcon(f));
        
        String extension = Converter.getExtension(new File(value.toString()))[1];
        
        if (!Listener.compileMode) {
        	//decompiler mode
        	if (extension.equals("class")) {
        		setBackground(Color.YELLOW);
        		setForeground(Color.BLACK);
        		if (isSelected) setBackground(Color.ORANGE);
        	}
        } else {
        	//compiler mode
        	if (extension.equals("java")) {
        		setBackground(Color.YELLOW);
        		setForeground(Color.BLACK);
        		if (isSelected) setBackground(Color.ORANGE);
        	}
        }
        
        if (pad) {
            l.setBorder(padBorder);
        }

        return l;
    }
}

