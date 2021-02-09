package com.rk;

import java.io.File;

import wp.code.bhak.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.GestureDetector.SimpleOnGestureListener;

// TODO: Auto-generated Javadoc
/**
 * The Class WallpaperSlideshowService.
 */
public class WallpaperSlideshowService extends WallpaperService{

	/** The m handler. */
	private final Handler mHandler = new Handler();
	
	/* (non-Javadoc)
	 * @see android.service.wallpaper.WallpaperService#onCreateEngine()
	 */
	@Override
	public Engine onCreateEngine() {
		return new WallpaperSlideshowEngine();
	}
	
	/**
	 * The Class WallpaperSlideshowEngine.
	 */
	private class WallpaperSlideshowEngine extends Engine implements OnSharedPreferenceChangeListener{
		
		/** The double taps. */
		GestureDetector doubleTaps = null;
		
		// Canvas stuff
    	/** The m paint. */
		private final Paint mPaint = new Paint();
    	
	    /** The m scaler. */
	    private final Matrix mScaler = new Matrix();
        
        /** The m width. */
        private int mWidth = 0;
        
        /** The m height. */
        private int mHeight = 0;
        
        /** The m min width. */
        private int mMinWidth = 0;
        
        /** The m min height. */
        private int mMinHeight = 0;
        
        /** The m x offset. */
        private float mXOffset = 0;
        
        /** The m y offset. */
        private float mYOffset = 0;
        
        /** The m visible. */
        private boolean mVisible = false;
        
        /** The m bitmap. */
        private Bitmap mBitmap = null;
        
        /** The m bitmap path. */
        private String mBitmapPath = null;
        
        /** The m index. */
        private int mIndex = -1;
        
        /** The m last draw time. */
        private long mLastDrawTime = 0;
        
        /** The m storage ready. */
        private boolean mStorageReady = true;
		
		// Preferences
		/** The Prefs. */
		private SharedPreferences Prefs;
        
        /** The pre folder. */
        private String preFolder = null;
        
        /** The pre duration. */
        private int preDuration = 0;
        
        /** The pre random. */
        private boolean preRandom = false;
        
        /** The pre rotate. */
        private boolean preRotate = false;
        
        /** The pre scroll. */
        private boolean preScroll = false;
        
        /** The pre recurse. */
        private boolean preRecurse = false;
        
        /** The pre touch events. */
        private boolean preTouchEvents = false;
        
        /** The pre screen awake. */
        private boolean preScreenAwake = false;
		
        /** The running. */
        private final Runnable running = new Runnable() {
            public void run() {
            	if (preDuration > 0)
            		drawFrame();
            }
        };
        

        /**
         * Instantiates a new wallpaper slideshow engine.
         */
        public WallpaperSlideshowEngine(){
        	
            final Paint paint = mPaint;
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setAntiAlias(true);
            paint.setTextSize(18f);
            
            Prefs = getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE);
        	
			Prefs.registerOnSharedPreferenceChangeListener(this);
				
			onSharedPreferenceChanged(Prefs, null);
			
			doubleTaps = new GestureDetector(WallpaperSlideshowService.this, new SimpleOnGestureListener(){
				@Override
				public boolean onDoubleTap(MotionEvent e) {
					if (preTouchEvents) {
						mLastDrawTime = 0;
						drawFrame();
						return true;
            		  }
            		  return false;
				}
			});
			
        }
        
		/* (non-Javadoc)
		 * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
		 */
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if (key == null) {
    			preFolder = sharedPreferences.getString(
    					getString(R.string.prefolder_key),
    					getString(R.string.prefolder_default));
    			preDuration = Integer.valueOf(sharedPreferences.getString(
    					getString(R.string.preduration_key),
    					getString(R.string.preduration_default)));
    			preRandom = sharedPreferences.getBoolean(
    					getString(R.string.prerandom_key),
    					Boolean.valueOf(getString(R.string.prerandom_default)));
    			preRotate = sharedPreferences.getBoolean(
    					getString(R.string.prerotate_key), 
    					Boolean.valueOf(getString(R.string.prerotate_default)));
    			preScroll = sharedPreferences.getBoolean(
    					getString(R.string.prescroll_key),
    					Boolean.valueOf(getString(R.string.prescroll_default)));
    			preRecurse = sharedPreferences.getBoolean(
    					getString(R.string.presubfolder_key),
    					Boolean.valueOf(getString(R.string.presubfolder_default)));
    			preTouchEvents = sharedPreferences.getBoolean(
    					getString(R.string.predoubletap_key),
    					Boolean.valueOf(getString(R.string.predoubletap_default)));
    			preScreenAwake = sharedPreferences.getBoolean(
    					getString(R.string.preawake_key),
    					Boolean.valueOf(getString(R.string.preawake_default)));
                mLastDrawTime = 0;
    		} else if (key.equals(getString(R.string.prefolder_key))) {
    			preFolder = sharedPreferences.getString(key,
    					getString(R.string.prefolder_default));
    			mIndex = -1;
                mLastDrawTime = 0;
    		} else if (key.equals(getString(R.string.preduration_key))) {
    			preDuration = Integer.parseInt(sharedPreferences.getString(key,
    					getString(R.string.preduration_default)));
    		} else if (key.equals(getString(R.string.prerandom_key))) {
    			preRandom = sharedPreferences.getBoolean(key,
    					Boolean.valueOf(getString(R.string.prerandom_default)));
    		} else if (key.equals(getString(R.string.prerotate_key))) {
    			preRotate = sharedPreferences.getBoolean(key,
    					Boolean.valueOf(getString(R.string.prerotate_default)));
    		} else if (key.equals(getString(R.string.prescroll_key))) {
    			preScroll = sharedPreferences.getBoolean(key,
    					Boolean.valueOf(getString(R.string.prescroll_default)));
    			if (preScroll == true) {
    				mLastDrawTime = 0;
    			}
    		} else if (key.equals(getString(R.string.presubfolder_key))) {
    			preRecurse = sharedPreferences.getBoolean(key,
    					Boolean.valueOf(getString(R.string.presubfolder_default)));
    		} else if (key.equals(getString(R.string.predoubletap_key))) {
    			preTouchEvents = sharedPreferences.getBoolean(key,
    					Boolean.valueOf(getString(R.string.predoubletap_default)));
    		} else if (key.equals(getString(R.string.preawake_key))) {
    			preScreenAwake = sharedPreferences.getBoolean(key,
    					Boolean.valueOf(getString(R.string.preawake_default)));
    		}
			Log.d("1111111111", preFolder);
		}
			
		/* (non-Javadoc)
		 * @see android.service.wallpaper.WallpaperService.Engine#onCreate(android.view.SurfaceHolder)
		 */
		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			
			IntentFilter intentfilter = new IntentFilter();
			intentfilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
			intentfilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
			intentfilter.addAction(Intent.ACTION_MEDIA_REMOVED);
			intentfilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
			intentfilter.addAction(Intent.ACTION_MEDIA_CHECKING);
			intentfilter.addAction(Intent.ACTION_MEDIA_EJECT);
			intentfilter.addAction(Intent.ACTION_MEDIA_NOFS);
			intentfilter.addAction(Intent.ACTION_MEDIA_SHARED);
			intentfilter.addDataScheme("file");
			registerReceiver(new BroadcastReceiver() {
				
				@Override
				public void onReceive(Context context, Intent intent) {
					String action = intent.getAction();
					if (action.equals(Intent.ACTION_MEDIA_MOUNTED)
    						|| action.equals(Intent.ACTION_MEDIA_CHECKING))	 {
    					mStorageReady = true;
    					setTouchEventsEnabled(true);
    					drawFrame();
    				} 
					else {
    					mStorageReady = false;
    					setTouchEventsEnabled(false);
    					mHandler.removeCallbacks(running);
    				}
					
				}
			}, intentfilter);
			
			// Register receiver for screen on events
			registerReceiver(new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					if (preScreenAwake) {
						mLastDrawTime = 0;
						drawFrame();
					}
				}
			}, new IntentFilter(Intent.ACTION_SCREEN_ON));
			
			setTouchEventsEnabled(mStorageReady);
		}
		
        /* (non-Javadoc)
         * @see android.service.wallpaper.WallpaperService.Engine#onDestroy()
         */
        @Override
        public void onDestroy() {
            super.onDestroy();
            mHandler.removeCallbacks(running);
            Prefs.unregisterOnSharedPreferenceChangeListener(this);
        }
        
        /* (non-Javadoc)
         * @see android.service.wallpaper.WallpaperService.Engine#onVisibilityChanged(boolean)
         */
        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                drawFrame();
            } else {
                mHandler.removeCallbacks(running);
            }
        }
		
		/* (non-Javadoc)
		 * @see android.service.wallpaper.WallpaperService.Engine#onSurfaceCreated(android.view.SurfaceHolder)
		 */
		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
			mLastDrawTime = 0;
			
		}
		
		
		/* (non-Javadoc)
		 * @see android.service.wallpaper.WallpaperService.Engine#onSurfaceChanged(android.view.SurfaceHolder, int, int, int)
		 */
		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			super.onSurfaceChanged(holder, format, width, height);
			 mWidth = width;
	            mHeight = height;
	            mMinWidth = width * 2; // cheap hack for scrolling
	            mMinHeight = height;
	            if (mBitmap != null) {
	            	mBitmap.recycle();
	            }
	            drawFrame();
		}
		
		/* (non-Javadoc)
		 * @see android.service.wallpaper.WallpaperService.Engine#onSurfaceDestroyed(android.view.SurfaceHolder)
		 */
		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
            mVisible = false;
            mHandler.removeCallbacks(running);
		}
		
        /* (non-Javadoc)
         * @see android.service.wallpaper.WallpaperService.Engine#onOffsetsChanged(float, float, float, float, int, int)
         */
        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                float xStep, float yStep, int xPixels, int yPixels) {
            mXOffset = xOffset;
            mYOffset = yOffset;
            drawFrame();
        }

    	/* (non-Javadoc)
	     * @see android.service.wallpaper.WallpaperService.Engine#onTouchEvent(android.view.MotionEvent)
	     */
	    @Override
    	public void onTouchEvent(MotionEvent event) {
    		super.onTouchEvent(event);
            this.doubleTaps.onTouchEvent(event);
    	}
    	
		/**
		 * Draw frame.
		 */
		void drawFrame() {
        	
            final SurfaceHolder holder = getSurfaceHolder();
            Canvas c = null;

            String state = Environment.getExternalStorageState();
            if (!state.equals(Environment.MEDIA_MOUNTED) &&
            		!state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            	return;
            }

        	try {
            	// Lock the canvas for writing
                c = holder.lockCanvas();
                
                // Do we need to get a new image?
                boolean getImage = false;
                if (mBitmapPath == null || mBitmap == null) {
                	getImage = true;
                }
                else if (preDuration > 0 && mLastDrawTime < System.currentTimeMillis() - preDuration) {
                	getImage = true;
                } else if (mLastDrawTime == 0) {
                	getImage = true;
                }
                
	            // Get image to draw
	            if (getImage) {
	            	// Get a list of files
	            	File[] files = FileUtils.listFiles(new File(preFolder), preRecurse, CheckImageExtension.isValidImage);
	            	if (files == null || files.length < 1) {
	            		throw new NoImagesInFolderException();
	            	}
	 
	            	// Increment counter
	               	int nFiles = files.length;
	            	if (preRandom) {
	        			int i = mIndex;
	        			do {
	        				mIndex = (int) (Math.random() * nFiles);
	        			} while (nFiles > 1 && mIndex == i);
	                } else {
		                if (++mIndex >= nFiles) {
		                	mIndex = 0;
		                }
	                }
		
	            	// Read file to bitmap
	            	mBitmapPath = files[mIndex].getAbsolutePath();
	            	mBitmap = getFormattedBitmap(mBitmapPath);
	
	                // Save the current time
	                mLastDrawTime = System.currentTimeMillis();
	            } else if (mBitmap != null && mBitmap.isRecycled()) {
	            	mBitmap = getFormattedBitmap(mBitmapPath);
	            }
            } catch (NoImagesInFolderException noie) {
            	c.drawColor(Color.BLACK);
            	c.translate(0, 30);
            	c.drawText("No photos found in selected folder, ",
            			c.getWidth()/2.0f, (c.getHeight()/2.0f)-15, mPaint);
            	c.drawText("press Settings to select a folder...",
            			c.getWidth()/2.0f, (c.getHeight()/2.0f)+15, mPaint);
            	holder.unlockCanvasAndPost(c);
            	return;
            } catch (NullPointerException npe) {
            	holder.unlockCanvasAndPost(c);
            	return;
            } catch (RuntimeException re) {
            	holder.unlockCanvasAndPost(c);
            	return;
            }

            try {
                if (c != null) {
                	int xPos;
                	int yPos;
                	if (preScroll) {
                		xPos = 0 - (int) (mWidth * mXOffset);
                		yPos = 0 - (int) (mHeight * mYOffset);
                	} else {
                		xPos = 0;
                		yPos = 0;
                	}
                	try {
                		c.drawColor(Color.BLACK);
                		c.drawBitmap(mBitmap, xPos, yPos, mPaint);
                	} catch (Throwable t) { }
                }
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }

            // Reschedule the next redraw
            mHandler.removeCallbacks(running);
            if (mVisible) {
            	mHandler.postDelayed(running, 1000 / 2);
            }
        }
		
		/**
		 * Gets the formatted bitmap.
		 *
		 * @param file the file
		 * @return the formatted bitmap
		 */
		private Bitmap getFormattedBitmap(String file) {
        	int targetWidth = (preScroll)? mMinWidth: mWidth;
        	int targetHeight = (preScroll)? mMinHeight: mHeight;

	        Bitmap bitmap = makeBitmap(Math.max(mMinWidth, mMinHeight),
	        			mMinWidth * mMinHeight, file, null);

        	if (bitmap == null) {
        		return Bitmap.createBitmap(targetWidth, targetHeight,
        				Bitmap.Config.ARGB_8888);
        	}
        	
        	int width = bitmap.getWidth();
        	int height = bitmap.getHeight();
        	
			if (preRotate) {
				int screenOrientation = getResources().getConfiguration().orientation;
				if (width > height
						&& screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
					bitmap = rotate(bitmap, 90, mScaler);
				}
				else if (height > width
						&& screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
					bitmap = rotate(bitmap, -90, mScaler);
				}
			}
        	
			if (width != targetWidth || height != targetHeight) {
        		bitmap = transform(mScaler, bitmap,
        				targetWidth, targetHeight, true, true);
        	}

        	return bitmap;
        }
		
		/**
		 * Make bitmap.
		 *
		 * @param minSideLength the min side length
		 * @param maxNumOfPixels the max num of pixels
		 * @param pathName the path name
		 * @param options the options
		 * @return the bitmap
		 */
		public Bitmap makeBitmap(int minSideLength, int maxNumOfPixels,
	            String pathName, BitmapFactory.Options options) {
	        try {
	            if (options == null) options = new BitmapFactory.Options();

	            options.inJustDecodeBounds = true;
	            BitmapFactory.decodeFile(pathName, options);
	            if (options.mCancel || options.outWidth == -1
	                    || options.outHeight == -1) {
	                return null;
	            }
	            
	            options.inSampleSize = computeSampleSize(
	                    options, minSideLength, maxNumOfPixels);
	            options.inJustDecodeBounds = false;

	            return BitmapFactory.decodeFile(pathName, options);
	        } catch (OutOfMemoryError ex) {
	            return null;
	        }
	    }
		
		/**
		 * Compute sample size.
		 *
		 * @param options the options
		 * @param minSideLength the min side length
		 * @param maxNumOfPixels the max num of pixels
		 * @return the int
		 */
		public int computeSampleSize(BitmapFactory.Options options,
	            int minSideLength, int maxNumOfPixels) {
	        int initialSize = computeInitialSampleSize(options, minSideLength,
	                maxNumOfPixels);

	        int roundedSize;
	        if (initialSize <= 8) {
	            roundedSize = 1;
	            while (roundedSize < initialSize) {
	                roundedSize <<= 1;
	            }
	        } else {
	            roundedSize = (initialSize + 7) / 8 * 8;
	        }

	        return roundedSize;
	    }

	    /**
    	 * Compute initial sample size.
    	 *
    	 * @param options the options
    	 * @param minSideLength the min side length
    	 * @param maxNumOfPixels the max num of pixels
    	 * @return the int
    	 */
    	private int computeInitialSampleSize(BitmapFactory.Options options,
	            int minSideLength, int maxNumOfPixels) {
	        double w = options.outWidth;
	        double h = options.outHeight;

	        int lowerBound = (maxNumOfPixels == -1) ? 1 :
	                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
	        int upperBound = (minSideLength == -1) ? 128 :
	                (int) Math.min(Math.floor(w / minSideLength),
	                Math.floor(h / minSideLength));

	        if (upperBound < lowerBound) {
	            // return the larger one when there is no overlapping zone.
	            return lowerBound;
	        }

	        if ((maxNumOfPixels == -1) &&
	                (minSideLength == -1)) {
	            return 1;
	        } else if (minSideLength == -1) {
	            return lowerBound;
	        } else {
	            return upperBound;
	        }
	    }

	    /**
    	 * Rotate.
    	 *
    	 * @param b the b
    	 * @param degrees the degrees
    	 * @param m the m
    	 * @return the bitmap
    	 */
    	public  Bitmap rotate(Bitmap b, int degrees, Matrix m) {
	        if (degrees != 0 && b != null) {
	        	if (m == null) {
	        		m = new Matrix();
	        	}
	            m.setRotate(degrees,
	                    (float) b.getWidth() / 2, (float) b.getHeight() / 2);
	            try {
	                Bitmap b2 = Bitmap.createBitmap(
	                        b, 0, 0, b.getWidth(), b.getHeight(), m, true);
	                if (b != b2) {
	                    b.recycle();
	                    b = b2;
	                }
	            } catch (OutOfMemoryError ex) {

	            }
	        }
	        return b;
	    }
	    
	    /**
    	 * Transform.
    	 *
    	 * @param scaler the scaler
    	 * @param source the source
    	 * @param targetWidth the target width
    	 * @param targetHeight the target height
    	 * @param scaleUp the scale up
    	 * @param recycle the recycle
    	 * @return the bitmap
    	 */
    	public Bitmap transform(Matrix scaler,
	                                   Bitmap source,
	                                   int targetWidth,
	                                   int targetHeight,
	                                   boolean scaleUp,
	                                   boolean recycle) {
	        int deltaX = source.getWidth() - targetWidth;
	        int deltaY = source.getHeight() - targetHeight;
	        if (!scaleUp && (deltaX < 0 || deltaY < 0)) {

	            Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight,
	                    Bitmap.Config.ARGB_8888);
	            Canvas c = new Canvas(b2);

	            int deltaXHalf = Math.max(0, deltaX / 2);
	            int deltaYHalf = Math.max(0, deltaY / 2);
	            Rect src = new Rect(
	                    deltaXHalf,
	                    deltaYHalf,
	                    deltaXHalf + Math.min(targetWidth, source.getWidth()),
	                    deltaYHalf + Math.min(targetHeight, source.getHeight()));
	            int dstX = (targetWidth  - src.width())  / 2;
	            int dstY = (targetHeight - src.height()) / 2;
	            Rect dst = new Rect(
	                    dstX,
	                    dstY,
	                    targetWidth - dstX,
	                    targetHeight - dstY);
	            c.drawBitmap(source, src, dst, null);
	            if (recycle) {
	                source.recycle();
	            }
	            return b2;
	        }
	        float bitmapWidthF = source.getWidth();
	        float bitmapHeightF = source.getHeight();

	        float bitmapAspect = bitmapWidthF / bitmapHeightF;
	        float viewAspect   = (float) targetWidth / targetHeight;

	        if (bitmapAspect > viewAspect) {
	            float scale = targetHeight / bitmapHeightF;
	            if (scale < .9F || scale > 1F) {
	                scaler.setScale(scale, scale);
	            } else {
	                scaler = null;
	            }
	        } else {
	            float scale = targetWidth / bitmapWidthF;
	            if (scale < .9F || scale > 1F) {
	                scaler.setScale(scale, scale);
	            } else {
	                scaler = null;
	            }
	        }

	        Bitmap b1;
	        if (scaler != null) {

	            b1 = Bitmap.createBitmap(source, 0, 0,
	                    source.getWidth(), source.getHeight(), scaler, true);
	        } else {
	            b1 = source;
	        }

	        if (recycle && b1 != source) {
	            source.recycle();
	        }

	        int dx1 = Math.max(0, b1.getWidth() - targetWidth);
	        int dy1 = Math.max(0, b1.getHeight() - targetHeight);

	        Bitmap b2 = Bitmap.createBitmap(
	                b1,
	                dx1 / 2,
	                dy1 / 2,
	                targetWidth,
	                targetHeight);

	        if (b2 != b1) {
	            if (recycle || b1 != source) {
	                b1.recycle();
	            }
	        }

	        return b2;
	    }


	    	    
	}
	
	/**
	 * The Class NoImagesInFolderException.
	 */
	class NoImagesInFolderException extends Exception {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;
	}
	
	/**
	 * The Class MediaNotReadyException.
	 */
	class MediaNotReadyException extends Exception {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;
	}
}
