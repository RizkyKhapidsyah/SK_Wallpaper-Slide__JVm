package com.rk;

import wp.code.bhak.R;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class FolderItem.
 */
public class FolderItem {
	
	/** The image. */
	public ImageView image;
	
	/** The foldername. */
	public TextView foldername;
	
	/** The folderpath. */
	public TextView folderpath;
	
	/**
	 * Instantiates a new folder item.
	 *
	 * @param v the v
	 */
	public FolderItem(final View v)
	{
		this.image = (ImageView)v.findViewById(R.id.image);
		this.foldername = (TextView)v.findViewById(R.id.foldername);
		this.folderpath = (TextView)v.findViewById(R.id.folderpath);
		v.setTag(this);
	}
}
