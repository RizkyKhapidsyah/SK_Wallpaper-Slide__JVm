package com.rk;

import wp.code.bhak.R;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

// TODO: Auto-generated Javadoc
/**
 * The Class SelectFolderActivity.
 */
public class SelectFolderActivity extends ListActivity{

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectfolderlist);
		new SearchImages(this).execute();
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				getSharedPreferences(getString(R.string.preference),Context.MODE_PRIVATE).edit()
				.putString(getResources().getString(R.string.prefolder_key), ((TextView)arg1.findViewById(R.id.folderpath)).getText().toString())
				.commit();
				
				finish();
			}
		});
		
	}
}
