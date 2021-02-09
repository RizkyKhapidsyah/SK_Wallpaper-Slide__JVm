package com.rk;

import java.io.File;

import wp.code.bhak.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

// TODO: Auto-generated Javadoc
/**
 * The Class DataAdapter.
 */
public class DataAdapter extends ArrayAdapter<String>{

	/** The Inflater. */
	private final LayoutInflater Inflater;
	
	/** The folders. */
	private final String[] folders;
	
	/**
	 * Instantiates a new data adapter.
	 *
	 * @param context the context
	 * @param textViewResourceId the text view resource id
	 * @param objects the objects
	 */
	public DataAdapter(Context context, int textViewResourceId,String[] objects) {
		super(context, textViewResourceId, objects);
		this.Inflater = LayoutInflater.from(context);
		this.folders = objects; 
	}
	
	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null)
		{
			convertView = Inflater.inflate(R.layout.listitem, null);
			new FolderItem(convertView);
		}
		final FolderItem folderItem = (FolderItem)convertView.getTag();
		
		String item = folders[position];
		if(item != null)
		{
			try {
				final File[] files = new File(item).listFiles(CheckImageExtension.isValidImage);
				
				if(files.length > 0){
					Bitmap image = ImageMemoryCache.getImage(files[0].getAbsolutePath());
					if(image == null){
						image = BitmapFactory.decodeFile(files[0].getAbsolutePath());
						ImageMemoryCache.setImage(files[0].getAbsolutePath(), image);
					}
					Bitmap resizeImage = null;
		        	int srcWidth = image.getWidth();
		        	int srcHeight = image.getHeight();
		        	Matrix scaleMat = new Matrix();
		        	float scaleX = (float)75 / (float)srcWidth;
		        	float scaleY = (float)75 / (float)srcHeight;
		        	scaleMat.postScale(scaleX, scaleY);
		        	resizeImage = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), scaleMat, true);
		        	
					folderItem.image.setImageBitmap(resizeImage);
					folderItem.foldername.setText(new File(item).getName() + " (" + files.length + ")");
					folderItem.folderpath.setText(item);
				}
			} catch (Exception e) {
			}
		}
		return convertView;
	}

}
