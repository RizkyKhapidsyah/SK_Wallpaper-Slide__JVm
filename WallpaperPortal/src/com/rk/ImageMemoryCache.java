package com.rk;

import java.util.HashMap;

import android.graphics.Bitmap;

// TODO: Auto-generated Javadoc
/**
 * The Class ImageMemoryCache.
 */
public class ImageMemoryCache {
	
    /** The cache. */
    private static HashMap<String,Bitmap> cache = new HashMap<String,Bitmap>();  
      
    /**
     * Gets the image.
     *
     * @param key the key
     * @return the image
     */
    public static Bitmap getImage(String key) {  
        if (cache.containsKey(key)) {  
            return cache.get(key);  
        }  
        return null;  
    }  
      
    /**
     * Sets the image.
     *
     * @param key the key
     * @param image the image
     */
    public static void setImage(String key, Bitmap image) {  
        cache.put(key, image);  
    }
}
