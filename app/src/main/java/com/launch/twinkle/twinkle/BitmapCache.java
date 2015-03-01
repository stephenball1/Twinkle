package com.launch.twinkle.twinkle;

import android.graphics.Bitmap;
import android.util.LruCache;

public class BitmapCache {
  // Get max available VM memory, exceeding this amount will throw an
  // OutOfMemory exception. Stored in kilobytes as LruCache takes an
  // int in its constructor.
  static final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

  // Use 1/8th of the available memory for this memory cache.
  static final int cacheSize = maxMemory / 8;

  private static LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(cacheSize) {
    @Override
    protected int sizeOf(String key, Bitmap bitmap) {
      // The cache size will be measured in kilobytes rather than
      // number of items.
      return bitmap.getByteCount() / 1024;
    }
  };

  public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
    if (getBitmapFromMemCache(key) == null) {
      cache.put(key, bitmap);
    }
  }

  public static Bitmap getBitmapFromMemCache(String key) {
    return cache.get(key);
  }
}
