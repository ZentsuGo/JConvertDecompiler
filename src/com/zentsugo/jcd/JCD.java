package com.zentsugo.jcd;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.zentsugo.list.ListPopup;
import com.zentsugo.utils.Converter;
import com.zentsugo.utils.Listener;
import com.zentsugo.list.CellRenderer;

@SuppressWarnings("serial")
public class JCD extends JFrame {
	private JTextField textField;
	private JTextField textField_1;
	private static JProgressBar progressBar;
	private static JList<File> list;
	private static ListModel<File> listModel = new DefaultListModel<File>();
	
	public static JCD jcd;
	
	public static void main(String[] args) {
		jcd = new JCD();
	}
	
	public JCD() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		getContentPane().setBackground(Color.WHITE);
		setTitle("JCD " + Listener.VERSION + " " + (Listener.compileMode == true ? "(compiler mode) " : "(decompiler mode)"));
		setSize(512, 328);
		setPreferredSize(new Dimension(512, 328));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		getContentPane().setLayout(null);
		pack();
		
		textField = new JTextField();
		textField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (!Listener.compileMode)
						importCFile();
					else
						importJFile();
				}
			}
		});
		textField.setEditable(false);
		textField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		textField.setBounds(12, 32, 482, 22);
		getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblLocation = new JLabel("Import Location :");
		lblLocation.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));
		lblLocation.setBounds(12, 13, 482, 16);
		getContentPane().add(lblLocation);
		
		JSeparator separator = new JSeparator();
		separator.setBackground(Color.GRAY);
		separator.setBounds(12, 67, 482, 2);
		getContentPane().add(separator);
		
		JLabel lblExportLocation = new JLabel("Export Location :");
		lblExportLocation.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));
		lblExportLocation.setBounds(12, 82, 482, 16);
		getContentPane().add(lblExportLocation);
		
		textField_1 = new JTextField();
		textField_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1)
					exportLocation();
			}
		});
		textField_1.setEditable(false);
		textField_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		textField_1.setBounds(12, 100, 482, 22);
		getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBackground(Color.GRAY);
		separator_1.setOrientation(SwingConstants.VERTICAL);
		separator_1.setBounds(271, 135, 7, 119);
		getContentPane().add(separator_1);
		
		JButton btnDecompileConvert = new JButton("Decompile & Convert");
		btnDecompileConvert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Listener.files.isEmpty() || Listener.importLocation == null || Listener.exportLocation == null) {
					JOptionPane.showMessageDialog(jcd, "No class to decompile/convert to a location", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if (!Listener.compileMode) {
					//decompiler mode
					int result = JOptionPane.showConfirmDialog(jcd,
							"Are you sure you want to decompile the content of these classes and convert it to java files ?",
							"Decompile and Convert", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
					if (result == JOptionPane.YES_OPTION) {
						//new thread to process progress bar update
						System.out.println("Decompiling & Converting all java class files...");
						
						setPBMaximum();
						progressBar.setString("0/" + getPBMaximum() + " files");
						
						ProcessJ p = new ProcessJ();
						p.start();
					}
				} else {
					//compiler mode
					int result = JOptionPane.showConfirmDialog(jcd,
							"Are you sure you want to compile the content of these classes to make java class files ?",
							"Compile the files",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.INFORMATION_MESSAGE);
					if (result == JOptionPane.YES_OPTION) {
						//new thread to process progress bar update
						System.out.println("Compile the java class files...");
						
						setPBMaximum();
						progressBar.setString("0/" + getPBMaximum() + " files");
						
						ProcessC p = new ProcessC();
						p.start();
					}
				}
			}
		});
		btnDecompileConvert.setBackground(Color.LIGHT_GRAY);
		btnDecompileConvert.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));
		btnDecompileConvert.setBounds(281, 135, 213, 43);
		getContentPane().add(btnDecompileConvert);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setForeground(Color.GREEN);
		progressBar.setBackground(Color.LIGHT_GRAY);
		progressBar.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));
		progressBar.setBounds(281, 188, 213, 22);
		progressBar.setString("0 files");
		getContentPane().add(progressBar);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(Color.LIGHT_GRAY);
		setJMenuBar(menuBar);
		
		JMenu mnImport = new JMenu("File");
		mnImport.setFont(new Font("Yu Gothic UI", Font.PLAIN, 15));
		mnImport.setBackground(Color.WHITE);
		menuBar.add(mnImport);
		
		JMenu mnImport_1 = new JMenu("Import");
		mnImport_1.setFont(new Font("Yu Gothic UI", Font.PLAIN, 15));
		mnImport.add(mnImport_1);
		
		JButton btnFile = new JButton("File");
		mnImport_1.add(btnFile);
		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!Listener.compileMode)
					importCFile();
				else
					importJFile();
			}
		});
		btnFile.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));
		btnFile.setBackground(Color.WHITE);
		
		JButton btnFolder = new JButton("Folder");
		btnFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!Listener.compileMode)
					importCFolder();
				else
					importJFolder();
			}
		});
		mnImport_1.add(btnFolder);
		btnFolder.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));
		btnFolder.setBackground(Color.WHITE);
		
		JMenu mnExport = new JMenu("Export");
		mnExport.setFont(new Font("Yu Gothic UI", Font.PLAIN, 15));
		mnImport.add(mnExport);
		
		JButton btnFile_1 = new JButton("Directory");
		btnFile_1.setBackground(Color.WHITE);
		btnFile_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportLocation();
			}
		});
		btnFile_1.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));
		mnExport.add(btnFile_1);
		
		JMenu mnOptions = new JMenu("Options");
		mnOptions.setFont(new Font("Yu Gothic UI", Font.PLAIN, 15));
		menuBar.add(mnOptions);
		
		JButton btnCompiler = new JButton("Compiler Mode");
		btnCompiler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//compiler mode or decompiler mode
				if (!Listener.compileMode) {
					Listener.compileMode = true;
					setTitle("JCD " + Listener.VERSION + " (compiler mode)");
					btnCompiler.setText("Decompiler Mode");
					btnDecompileConvert.setText("Compile the files");
				} else {
					Listener.compileMode = false;
					setTitle("JCD " + Listener.VERSION + " (decompiler mode)");
					btnCompiler.setText("Compiler Mode");
					btnDecompileConvert.setText("Decompile & Convert");
				}
				
				list.repaint();
			}
		});
		btnCompiler.setBackground(Color.WHITE);
		btnCompiler.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));
		mnOptions.add(btnCompiler);
		
		list = new JList<File>(listModel); //data has type Object[]
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//right click
				if (e.getButton() == MouseEvent.BUTTON3) {
					new ListPopup(list).show(list, e.getX(), e.getY());
				}
			}
		});
		list.setBounds(11, 135, 248, 119);
		getContentPane().add(list);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		list.setCellRenderer(new CellRenderer(true));
		
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setBounds(11, 135, 248, 119);
		listScroller.setPreferredSize(new Dimension(250, 80));
		getContentPane().add(listScroller);
		
		JButton btnNewButton = new JButton("Clear");
		btnNewButton.setBackground(Color.WHITE);
		btnNewButton.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Listener.files.size() <= 0 || Listener.files.isEmpty()) return;
				int result = JOptionPane.showConfirmDialog(jcd,
						"Are you sure you want to clear all files ?",
						"Confirmation",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.INFORMATION_MESSAGE);
				if (result == JOptionPane.YES_OPTION) {
					Listener.files.clear();
					list.setModel(new DefaultListModel<File>());
					Listener.importLocation = null;
					textField.setText("");
					getProgressBar().setValue(0);
					getProgressBar().setMaximum(0);
					getProgressBar().setString("0 files");
				}
			}
		});
		btnNewButton.setBounds(281, 223, 213, 31);
		getContentPane().add(btnNewButton);
		
		setVisible(true);
	}
	
	//Class Files
	private void importCFile() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Choose a java file class");
		fc.setCurrentDirectory(new File(System.getProperty("user.home")));
		
		int result = fc.showOpenDialog(jcd);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			//file selected
			//check if file is java class
			File file = fc.getSelectedFile();
			if (Converter.getExtension(file)[1].equals("class")) {
				//it is a java file class
				//save file to list and display its name to the user by gui
				Listener.files.add(file);
				
				//list fill with new class name linked list
				addListItem(file);
				
				Listener.importLocation = file.getParentFile();
				textField.setText(file.getParentFile().getAbsolutePath());
				
				setPBMaximum();
				progressBar.setString("0/" + getPBMaximum() + " files");
			} else {
				JOptionPane.showMessageDialog(jcd, "Select a java file class !", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	//Class Files
	private void importCFolder() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Choose a folder");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setCurrentDirectory(new File(System.getProperty("user.home")));
		
		int result = fc.showOpenDialog(jcd);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			//directory selected
			File dir = fc.getSelectedFile();
			
			if (!dir.isDirectory()) {
				System.err.println("Select a folder!");
				return;
			}
			
			//get all files/directories from the directory and check if there are java class files and then add it to the list
			File[] listFiles = dir.listFiles();
			
			checkFolder(listFiles, 0);
			
			//same list here
			setList(Listener.files);
			
			Listener.importLocation = dir;
			textField.setText(dir.getAbsolutePath());
			
			setPBMaximum();
			progressBar.setString("0/" + getPBMaximum() + " files");
		}
	}
	
	//Java File
	private void importJFile() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Choose a java file");
		fc.setCurrentDirectory(new File(System.getProperty("user.home")));
		
		int result = fc.showOpenDialog(jcd);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			//file selected
			//check if file is java
			File file = fc.getSelectedFile();
			if (Converter.getExtension(file)[1].equals("java")) {
				//it is a java file
				//save file to list and display its name to the user by gui
				Listener.files.add(file);
				
				addListItem(file);
				
				Listener.importLocation = file.getParentFile();
				textField.setText(file.getParentFile().getAbsolutePath());
				
				setPBMaximum();
				progressBar.setString("0/" + getPBMaximum() + " files");
			} else {
				JOptionPane.showMessageDialog(jcd, "Select a java file !", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	//Java Files
	private void importJFolder() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Choose a folder");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setCurrentDirectory(new File(System.getProperty("user.home")));
		
		int result = fc.showOpenDialog(jcd);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			//directory selected
			File dir = fc.getSelectedFile();
			
			if (!dir.isDirectory()) {
				System.err.println("Select a folder!");
				return;
			}
			
			//get all files/directories from the directory and check if there are java class files and then add it to the list
			File[] listFiles = dir.listFiles();
			
			checkFolder(listFiles, 1);
			
			//same list here
			setList(Listener.files);
			
			Listener.importLocation = dir;
			textField.setText(dir.getAbsolutePath());
			
			setPBMaximum(); //set progress bar maximum automatically
			progressBar.setString("0/" + getPBMaximum() + " files");
		}
	}
	
	private void exportLocation() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Choose an export directory");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setCurrentDirectory(new File(System.getProperty("user.home")));
		
		int result = fc.showOpenDialog(jcd);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			//file selected
			//check if file is java class
			File dir = fc.getSelectedFile();
			if (dir.canWrite() && dir.canRead()) {
				//it is a java file class
				//save file to list and display its name to the user by gui
				Listener.exportLocation = dir;
				textField_1.setText(dir.getAbsolutePath());
			}
		}
	}
	
	/***
	 * @param listFiles
	 * list of files
	 * @param extension
	 * 0 = java class,
	 * 1 = java file
	 */
	private static void checkFolder(File[] listFiles, int extension) {
		ArrayList<File[]> newDirectories = new ArrayList<File[]>();
		for (int i = 0; i < listFiles.length; i++) {
			File f = listFiles[i];
			if (f.isFile()) {
				if (Listener.files.contains(f)) {
					/***
					 * 
					 * 
					 * IMPORTANT TO KNOW :
					 * Since the files are working by references in the application (apparently lol),
					 * their content are automatically updated if changed, so if the user tries
					 * to readd the same file, we ignore it as we don't need to overwrite it
					 * 
					 * 
					 */
					continue;
				}
				if (extension == 0) {
					String[] fextens = Converter.getExtension(f);
					if (fextens == null) {
						System.out.println(f.getName() + " has no extension, skipped.");
						continue;
					}
					String extens = fextens[1];
					if (extens.equals("class")) {
						System.out.println("Found a java class ...");
						Listener.files.add(f);
					}
				} else if (extension == 1) {
					String[] fextens = Converter.getExtension(f);
					if (fextens == null) {
						System.out.println(f.getName() + " has no extension, skipped.");
						continue;
					}
					String extens = fextens[1];
					if (extens.equals("java")) {
						System.out.println("Found a java file ...");
						Listener.files.add(f);
					}
				} else {
					System.err.println("Wrong extension number ! Choose between 0 (java file) and 1 (class file).");
					return;
				}
			} else if (f.isDirectory()) {
				System.out.println("Found a new directory ...");
				newDirectories.add(f.listFiles());
			}
		}
		System.out.println("Checking the new directories ...");
		for (File[] dir : newDirectories) {
			checkFolder(dir, extension);
		}
	}
	
	public static void setList(List<File> array) {
		DefaultListModel<File> dlm = new DefaultListModel<File>();
		for (File i : array)
			dlm.addElement(i);
		list.setModel((ListModel<File>) dlm);
	}
	
	public static void addListItem(File file) {
		((DefaultListModel<File>) list.getModel()).addElement(file);
	}
	
	public static void setPBMaximum() { //set progress bar maximum
		int count = 0;
		for (int i = 0; i < Listener.files.size(); i++) {
			File f = Listener.files.get(i);
			if (!Listener.compileMode) {
				System.out.println("Mode decompiler...");
				if (Converter.getExtension(f)[1].equals("class")) {
					System.out.println("Found a java class ...");
					count++;
				}
			} else {
				if (Converter.getExtension(f)[1].equals("java")) {
					count++;
				}
			}
			
			if (!Converter.getExtension(f)[1].equals("java") && !Converter.getExtension(f)[1].equals("class")) {
				System.out.println("File : " + f.getName());
				System.err.println("Warning : file with wrong extension found, removed immediatly.");
				Listener.files.remove(i);
			}
			
			getProgressBar().setMaximum(count);
		}
	}
	
	public static int getPBMaximum() {
		return getProgressBar().getMaximum();
	}
	
	public static JProgressBar getProgressBar() {
		return progressBar;
	}
	
	public static JCD getFrame() {
		return jcd;
	}
}
