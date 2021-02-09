package com.rk;

import wp.code.bhak.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Environment;

// TODO: Auto-generated Javadoc
/**
 * The Class SearchImages.
 */
public class SearchImages extends AsyncTask<Void, Void, String[]>{

	/** The progress dialog. */
	ProgressDialog progressDialog = null;
	
	/** The context. */
	Context context;
	
	/**
	 * Instantiates a new search images.
	 *
	 * @param Context the context
	 */
	public SearchImages (Context Context)
	{
		this.context = Context;
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String[] doInBackground(Void... params) {

		return FileUtils.listDirectories(Environment.getExternalStorageDirectory(), CheckImageExtension.isValidDirectory);
	}
	
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String[] result) {
		if(progressDialog.isShowing())
			progressDialog.dismiss();
		if(result.length > 0)
		{
			ListActivity list = (ListActivity) context;
			list.setListAdapter(new DataAdapter(context, R.layout.listitem, result));
		}
		else{
			new AlertDialog.Builder(context)
			.setTitle(context.getString(R.string.alertDialog_title))
			.setMessage(context.getString(R.string.alertDialog_msg))
			.setNegativeButton(context.getString(android.R.string.cancel), new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					((Activity) context).finish();
				}
			}).show();
		}
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		progressDialog = ProgressDialog.show(context, 
				context.getString(R.string.progressDialog_title), 
				context.getString(R.string.progressDialog_msg), 
				true);
		progressDialog.setCancelable(true);
		
	}
}
