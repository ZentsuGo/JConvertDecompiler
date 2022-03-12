package com.zentsugo.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.strobel.decompiler.Decompiler;

public class Converter {
	private static void decompile(String jclass, String jfile) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(jfile); //output (java file to fill up)
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
		    Decompiler.decompile(
		        jclass, //input (class to decompile)
		        new com.strobel.decompiler.PlainTextOutput(writer)
		    );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		finally {
		    writer.flush();
		    writer.close();
		}
	}
	
	/*** Decompile java class files ***/
	public static void convert(File jclass, File jfile) {
		decompile(jclass.getAbsolutePath(), jfile.getAbsolutePath());
	}
	
	/*** Compile java files ***/
	public static boolean reverse(File jfile, File jclass) {
		return compile(jfile.getAbsolutePath(), jclass.getAbsolutePath());
	}
	
	private static boolean compile(String jfile, String jclass) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		System.out.println("COMPILING [" + jfile + "] FILE TO " + jclass + " LOCATION");
		int result = compiler.run(null, null, null,
		             jfile, "-d", jclass);
		return result == 0 ? true : false; //true if success else false if failed
		
		//////////////////////////////////////CODE TO COMPILE JAVA FILES
		
	    /*JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	    DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<JavaFileObject>();
	    StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticsCollector, null, null);
	    Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(Arrays.asList(jfile));
	    JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticsCollector, null, options, compilationUnits);
	    boolean result = task.call();
	    
	    if (!result) {
	        List<Diagnostic<? extends JavaFileObject>> diagnostics = diagnosticsCollector.getDiagnostics();
	        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
	            // read error dertails from the diagnostic object
	            System.out.println(diagnostic.getMessage(null));
	        }
	    }
	    
	    fileManager.close();
	    
		return result;*/
	}
	
	public static File checkDir(File jfile) throws IOException {
		if (Listener.exportLocation == null) {
			System.err.println("No export location.");
			return null;
		}
		
		String path = Listener.exportLocation + "\\" + Listener.importLocation.toURI().relativize(jfile.toURI()).getPath(); //problem here
		File temp = new File(path);
		
		File file = new File(temp.getParentFile().getAbsolutePath() + "\\");
		
		if (file.toPath().getNameCount() != 0) { //is not root (C:\\ or D:\\ ...)
			if (!file.exists()) {
				if (file.mkdirs()) {} else {
				    throw new IOException("Failed to create directory " + file.getParent());
				}
			}
		}
		
		return file;
	}
	
	public static File checkJFile(File jfile) throws IOException {
		if (Listener.exportLocation == null) {
			System.err.println("No export location.");
			return null;
		}
		
		//creates directories and files to get the same structure directory of the import location files to export location files
		String path = Listener.exportLocation + "\\" + Listener.importLocation.toURI().relativize(jfile.toURI()).getPath(); //problem here
		File temp = new File(path);
		
		//new java file to create for decompiler mode, no need for compiler mode as compiling a java file already creates a new java class file
		File file = new File(temp.getParentFile().getAbsolutePath() + "\\" + getExtension(temp)[0] + ".java"); //dir + file //problem here
		
		//check if directory exists
		if (file.toPath().getNameCount() != 0) { //is not root (C:\\ or D:\\ ...)
			if (!file.getParentFile().exists()) {
				if (file.getParentFile().mkdirs()) { //if not then create it
					try {
						file.createNewFile(); //and create the file (overwrite)
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println("Export Location Directory created automatically.");
				} else {
				    throw new IOException("Failed to create directory " + file.getParent());
				}
			} else {
				try {
					file.createNewFile(); //else the directory already exists and we create the file (overwrite)
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return file;
	}
	
	/***
	 * *
	 * String list with 2 arguments :
	 * 0 = filename,
	 * 1 = extension
	 * @return
	 * String array
	 */
	public static String[] getExtension(File file) {
		String extension = "";
		int i = file.getName().lastIndexOf(".");
		if (i == -1) return null;
		if (i > 0)
			extension = file.getName().substring(i + 1);
		
		String[] array = new String[2];
		array[0] = file.getName().substring(0, i);
		array[1] = extension;
		
		return array;
	}
}
