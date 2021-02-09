package com.rk;

import wp.code.bhak.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.text.Html;

// TODO: Auto-generated Javadoc
/**
 * The Class WallpaperSlideshowSettings.
 */
public class WallpaperSlideshowSettings extends PreferenceActivity{
	
	/** The context. */
	Context context;
	
	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getPreferenceManager().setSharedPreferencesName(getString(R.string.preference));
		addPreferencesFromResource(R.xml.settings);
		
		
		final ListPreference preFolder = (ListPreference) findPreference(getString(R.string.prefolder_key));
		preFolder.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				preFolder.getDialog().hide();
				startActivity(new Intent(getBaseContext(),SelectFolderActivity.class));
				return true;
			}
		});
		
		
		final CheckBoxPreference preScroll = (CheckBoxPreference) findPreference(getString(R.string.prescroll_key));
		preScroll.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if ((Boolean) newValue == true) {
					
					new AlertDialog.Builder(context)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(android.R.string.dialog_alert_title)
					.setMessage(R.string.prescroll_dialog)
					.setPositiveButton(android.R.string.ok, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
					.setNegativeButton(android.R.string.cancel, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							preScroll.setChecked(false);
						}
					}).show();
				}
				return true;
			}
		});
		
		final Preference preAbout = (Preference) findPreference(getString(R.string.preabout_key));
		preAbout.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				String versionName;
				try {
					PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
					versionName = pi.versionName;
				} catch (PackageManager.NameNotFoundException e) {
					versionName = "";
				}
				new AlertDialog.Builder(context)
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setMessage(Html.fromHtml(getString(R.string.preabout_dialog).replaceAll("\\{VersionName\\}", versionName)))
				.setPositiveButton(android.R.string.ok, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
					}
				}).show();
				return false;
			}
		});
	}
}
