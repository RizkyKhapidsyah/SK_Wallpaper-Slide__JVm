package com.rk;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;


// TODO: Auto-generated Javadoc
/**
 * The Class FileUtils.
 */
public class FileUtils {

	 /**
 	 * List files.
 	 *
 	 * @param directory the directory
 	 * @param recurse the recurse
 	 * @param filter the filter
 	 * @return the file[]
 	 */
 	public static File[] listFiles(File directory, boolean recurse, FileFilter filter) {
	    	if (!recurse) {
	    		return directory.listFiles(filter);
	    	} else {
	    		Collection<File> Files = new LinkedList<File>();
	            innerListFiles(Files, directory, filter);
	            return (File[]) Files.toArray(new File[Files.size()]);
	        }
	    }

	    /**
    	 * Inner list files.
    	 *
    	 * @param files the files
    	 * @param directory the directory
    	 * @param filter the filter
    	 */
    	public static void innerListFiles(Collection<File> files, File directory, FileFilter filter) {
	    	//get list files path in directory
	        File[] foundfiles = directory.listFiles();
	        if (foundfiles != null) {
	            for (int i = 0; i < foundfiles.length; i++) {
	            	//if exist subfolder, loop again
	                if (foundfiles[i].isDirectory()) {
	                    innerListFiles(files, foundfiles[i], filter);
	                } else {
	                	File[] validfiles = directory.listFiles((FileFilter) filter);
	                	for (int j = 0; j < validfiles.length; j++) {
	                		files.add(validfiles[j]);
	                	}
	                }
	            }

	        }
	    }
	    
	    /**
    	 * List directories.
    	 *
    	 * @param directory the directory
    	 * @param filter the filter
    	 * @return the string[]
    	 */
    	public static String[] listDirectories(File directory, FileFilter filter)
	    {
	    	ArrayList<File> arraylistFolders = new ArrayList<File>();
			//get list folders path in StorageDirectory
	    	innerListDirectories(arraylistFolders, directory, filter);
			ArrayList<String> arraylistFiles = new ArrayList<String>();
			for(File f : arraylistFolders)
			{
				//check, if folder include valid image file
				if(f.listFiles(CheckImageExtension.isValidImage).length > 0)
					arraylistFiles.add(f.toString());
			}
			
			return (String[]) arraylistFiles.toArray(new String[arraylistFiles.size()]);
	    }
	    
	    /**
    	 * Inner list directories.
    	 *
    	 * @param Directories the directories
    	 * @param Directory the directory
    	 * @param filter the filter
    	 */
    	public static void innerListDirectories(ArrayList<File> Directories, File Directory, FileFilter filter)
		{
	    	//get list folders path in root Directory
			File[] Files = Directory.listFiles(filter);
			if(Files.length > 0)
			{
				for (int i = 0; i < Files.length; i++)
				{
					Directories.add(Files[i]);
					//if exist subfolder, loop again
					if(Files[i].isDirectory()){
						innerListDirectories(Directories, Files[i], filter);
					}
				}
			}
		}
}
