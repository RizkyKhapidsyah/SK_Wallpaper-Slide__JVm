package com.rk;

import wp.code.bhak.R;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class WallpaperSlideshowActivity.
 */
public class WallpaperSlideshowActivity extends Activity {
    
    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Set text guide
        final TextView txtguide = (TextView) this.findViewById(R.id.txtguide);
        txtguide.setText(Html.fromHtml(getResources().getString(R.string.guide)));
        
        //Call Live wallpaper screen
        final Button btnSetwallpaper = (Button) this.findViewById(R.id.btnsetwallpaper);
        btnSetwallpaper.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER));
				
			}
		});
        
        //Call Settings screen
        final Button btnSettings = (Button) this.findViewById(R.id.btnsetings);
        btnSettings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(), WallpaperSlideshowSettings.class));
				
			}
		});
        
    }
}