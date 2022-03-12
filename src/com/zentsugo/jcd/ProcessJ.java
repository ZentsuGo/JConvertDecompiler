package com.zentsugo.jcd;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.zentsugo.utils.Converter;
import com.zentsugo.utils.Listener;

public class ProcessJ extends Thread {
	
	/***
	 * ProcessJ = Processus for Java Files, decompile java class files and convert them (their content) into java files
	 */
	
	@Override
	public synchronized void run() throws NullPointerException {
		if (Listener.floaded != 0)
			Listener.floaded = 0;
		
		for (File f : Listener.files) {
			//check java class files in list decompiler mode
			if (!Converter.getExtension(f)[1].equals("class")) continue; //not in compiler mode for java files (or others) so ignore them
			
			File jfile = null;
			try {
				jfile = Converter.checkJFile(f); //create new empty java files and directory structure to create it or not
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Converter.convert(f, jfile); //decompile java class files and convert them (copy content) into the java files
			
			//increase the variable to keep track of process progress
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
