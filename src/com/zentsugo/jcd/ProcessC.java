package com.zentsugo.jcd;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.zentsugo.utils.Converter;
import com.zentsugo.utils.Listener;

public class ProcessC extends Thread {
	
	/***
	 * ProcessC = Processus for Class Files, compile java files into java files
	 */
	
	@Override
	public synchronized void run() throws NullPointerException {
		if (Listener.floaded != 0)
			Listener.floaded = 0;
		
		for (File f : Listener.files) {
			//check the java files in list compiler mode
			if (!Converter.getExtension(f)[1].equals("java")) continue; //not in decompiler mode for java class files (or others) so ignore them
			
			File jclass = null;
			try {
				jclass = Converter.checkDir(f); //check the directory structure to create it or not
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			boolean done = Converter.reverse(f, jclass);
			
			//increase the variable to keep track of process progress
			if (done)
				Listener.floaded++;
			
			JCD.getProgressBar().setString(Listener.floaded + "/" + Listener.files.size() + " files");
			JCD.getProgressBar().setValue(Listener.floaded);
		}
		
		try {
			wait(1000); //wait a second
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//reset progress bar
		JCD.getProgressBar().setValue(0);
		
		JOptionPane.showMessageDialog(JCD.getFrame(), "Process finished.", "Decompile & Convert", JOptionPane.INFORMATION_MESSAGE);
	}
}
