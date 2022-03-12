package com.zentsugo.list;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPopupMenu;

import com.zentsugo.jcd.JCD;
import com.zentsugo.utils.Listener;

@SuppressWarnings("serial")
public class ListPopup extends JPopupMenu {
	private JButton remove;
	
	public ListPopup(JList<File> list){
		setFont(new Font("Yu Gothic UI", Font.PLAIN, 15));
        remove = new JButton("Remove selected files");
        remove.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		int[] indices = list.getSelectedIndices();
        		
        		setVisible(false);
        		if (indices.length <= 0) return;
        		
        		for (int i = indices.length-1; i >= 0; i--) {
        			int ind = indices[i];
	        		((DefaultListModel<File>) list.getModel()).remove(ind);
	        		Listener.files.remove(ind);
        		}
        		
        		System.out.println("Removed file(s).");
        		
        		JCD.getProgressBar().setMaximum(Listener.files.size());
        		JCD.getProgressBar().setString("0/" + Listener.files.size() + " files");
        	}
        });
        remove.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));
        remove.setBackground(Color.WHITE);
        add(remove);
    }
}
