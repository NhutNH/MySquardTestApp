package com.mobile.nhut.firebase.adapters.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

  private static ImageLoader sInstance;

  private static final int THREAD_NUMBERS = 5;

  private static final int TIME_OUT = 30000;

  private int mStubId;

  private MemoryCache mMemoryCache = new MemoryCache();

  private FileCache mFileCache;

  private Map<ImageView, String> mImageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

  private ExecutorService mExecutorService;

  private boolean mIsLocalImage;

  public static ImageLoader getInstance(Context context) {
    if (sInstance == null) {
      synchronized (ImageLoader.class) {
        sInstance = new ImageLoader(context);
      }
    }
    return sInstance;
  }

  private ImageLoader(Context context) {
    mFileCache = new FileCache(context);
    mExecutorService = Executors.newFixedThreadPool(THREAD_NUMBERS);
  }

  public void displayImage(String url, int loader, ImageView imageView, boolean isLocal) {
    mStubId = loader;
    mImageViews.put(imageView, url);
    mIsLocalImage = isLocal;

    Bitmap bitmap = mMemoryCache.get(url);
    if (bitmap != null) {
      imageView.setImageBitmap(bitmap);
    } else {
      queuePhoto(url, imageView);
      imageView.setImageResource(loader);
    }
  }

  private void queuePhoto(String url, ImageView imageView) {
    PhotoToLoad p = new PhotoToLoad(url, imageView);
    mExecutorService.submit(new PhotosLoader(p));
  }

  private Bitmap getBitmap(String url) {
    File f = mFileCache.getFile(url);

    //from SD cache
    Bitmap b = decodeFile(f);
    if (b != null) {
      return b;
    }

    //from web
    try {
      Bitmap bitmap = null;
      URL imageUrl = new URL(url);
      HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
      conn.setConnectTimeout(TIME_OUT);
      conn.setReadTimeout(TIME_OUT);
      conn.setInstanceFollowRedirects(true);
      InputStream is = conn.getInputStream();
      OutputStream os = new FileOutputStream(f);
      ImageLoaderUtils.CopyStream(is, os);
      os.close();
      bitmap = decodeFile(f);
      return bitmap;
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  private Bitmap getBitmap(String url, ImageView imageView) {
    File f = mFileCache.getFile(url);

    //from web
    try {
      URL imageUrl = new URL(url);
      HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
      conn.setConnectTimeout(TIME_OUT);
      conn.setReadTimeout(TIME_OUT);
      conn.setInstanceFollowRedirects(true);
      InputStream is = conn.getInputStream();
      OutputStream os = new FileOutputStream(f);
      ImageLoaderUtils.CopyStream(is, os);
      os.close();

      Drawable drawing = imageView.getDrawable();
      Bitmap imageViewBitmap = ((BitmapDrawable) drawing).getBitmap();

      // Get current dimensions
      int width = imageViewBitmap.getWidth();
      int height = imageViewBitmap.getHeight();

      BitmapFactory.Options dbo = new BitmapFactory.Options();
      dbo.inJustDecodeBounds = true;
      Bitmap originBitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, dbo);
      //is.close();

      int pictureWidth = dbo.outWidth;
      int pictureHeight = dbo.outHeight;

      float widthRatio = 0f;
      float heightRatio = 0f;
      float maxRatio = 0f;
      Bitmap srcBitmap;

      if (pictureWidth > width) {
        widthRatio = ((float) pictureWidth) / ((float) width);
        heightRatio = ((float) pictureHeight) / ((float) width);
        maxRatio = Math.max(widthRatio, heightRatio);

        // Create the mBitmap from file
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = (int) maxRatio;
        srcBitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
      } else if (pictureWidth < width) {
        widthRatio = ((float) width) / ((float) pictureWidth);
        heightRatio = ((float) height) / ((float) pictureWidth);
        maxRatio = Math.max(widthRatio, heightRatio);

        // Create the mBitmap from file
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = (int) maxRatio;
        srcBitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
      } else {
        srcBitmap = BitmapFactory.decodeStream(new FileInputStream(f));
      }

      if (srcBitmap == null) {
        return originBitmap;
      }

      return srcBitmap;
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  //decodes image and scales it to reduce memory consumption
  private Bitmap decodeFile(File f) {
    try {
      //decode image size
      BitmapFactory.Options o = new BitmapFactory.Options();
      o.inJustDecodeBounds = true;
      BitmapFactory.decodeStream(new FileInputStream(f), null, o);

      //Find the correct scale value. It should be the power of 2.
      final int REQUIRED_SIZE = 70;
      int width_tmp = o.outWidth, height_tmp = o.outHeight;
      int scale = 1;
      while (true) {
        if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
          break;
        }
        width_tmp /= 2;
        height_tmp /= 2;
        scale *= 2;
      }

      //decode with inSampleSize
      BitmapFactory.Options o2 = new BitmapFactory.Options();
      o2.inSampleSize = scale;
      return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
    } catch (FileNotFoundException e) {
    }
    return null;
  }

  //Task for the queue
  private class PhotoToLoad {

    public String url;

    public ImageView mImageView;

    public PhotoToLoad(String u, ImageView i) {
      url = u;
      mImageView = i;
    }
  }

  private class PhotosLoader implements Runnable {

    PhotoToLoad mPhotoToLoad;

    PhotosLoader(PhotoToLoad photoToLoad) {
      this.mPhotoToLoad = photoToLoad;
    }

    @Override
    public void run() {
      if (imageViewReused(mPhotoToLoad)) {
        return;
      }

      //            Bitmap bmp=getBitmap(mPhotoToLoad.url);
      Bitmap bmp = null;
      if(mIsLocalImage){
        bmp = PhotoProcessingUtils.scaleLocalImageforList(mPhotoToLoad.url, mPhotoToLoad.mImageView);
      }else{
        bmp = getBitmap(mPhotoToLoad.url, mPhotoToLoad.mImageView);
      }
      mMemoryCache.put(mPhotoToLoad.url, bmp);
      if (imageViewReused(mPhotoToLoad)) {
        return;
      }
      BitmapDisplayer bd = new BitmapDisplayer(bmp, mPhotoToLoad);
      Activity a = (Activity) mPhotoToLoad.mImageView.getContext();
      a.runOnUiThread(bd);
    }
  }

  private boolean imageViewReused(PhotoToLoad photoToLoad) {
    String tag = mImageViews.get(photoToLoad.mImageView);
    if (tag == null || !tag.equals(photoToLoad.url)) {
      return true;
    }
    return false;
  }

  //Used to display mBitmap in the UI thread
  private class BitmapDisplayer implements Runnable {

    Bitmap mBitmap;

    PhotoToLoad mPhotoToLoad;

    public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
      mBitmap = b;
      mPhotoToLoad = p;
    }

    public void run() {
      if (imageViewReused(mPhotoToLoad)) {
        return;
      }
      if (mBitmap != null) {
        mPhotoToLoad.mImageView.setImageBitmap(mBitmap);
      } else {
        if (mStubId != 0) {
          mPhotoToLoad.mImageView.setImageResource(mStubId);
        }
      }
    }
  }

  public void clearCache() {
    mMemoryCache.clear();
    mFileCache.clear();
  }
}