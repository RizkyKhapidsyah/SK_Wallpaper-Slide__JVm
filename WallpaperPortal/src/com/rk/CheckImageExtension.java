package com.rk;

import java.io.File;
import java.io.FileFilter;

// TODO: Auto-generated Javadoc
/**
 * The Class CheckImageExtension.
 */
public class CheckImageExtension {

	/** The is valid image. */
	public static FileFilter isValidImage = new FileFilter() {
		
		@Override
		public boolean accept(File pathname) {
			final String name = pathname.getName();
	        String ext = null;
	        int i = name.lastIndexOf('.');

	        if (i > 0 &&  i < name.length() - 1) {
	            ext = name.substring(i+1).toLowerCase();
	        }
	        if (ext == null)
	        	return false;
	        else if (!ext.equals("jpg") && !ext.equals("jpeg") && !ext.equals("png") && !ext.equals("gif"))
					return false;
			else
				return true;
		}
	};
	
	/** The is valid directory. */
	public static FileFilter isValidDirectory = new FileFilter() {
		
		@Override
		public boolean accept(File pathname) {
			if(!pathname.isDirectory())
				return false;
			else if(pathname.getName().startsWith("."))
				return false;
			else 
				return true;
		}
	};

}
