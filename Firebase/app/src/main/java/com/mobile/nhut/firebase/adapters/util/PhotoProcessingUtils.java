package com.mobile.nhut.firebase.adapters.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PhotoProcessingUtils {
    public static Bitmap scaleImage(Context context, Uri photoUri, boolean isTakePhoto, boolean isLandScape) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = 0;
        if (!isTakePhoto) {
            orientation = getOrientation(context, photoUri);
        } else if (!isLandScape) {
            orientation = 90;
        }

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > 516 || rotatedHeight > 516) {
            float widthRatio = ((float) rotatedWidth) / ((float) 516);
            float heightRatio = ((float) rotatedHeight) / ((float) 516);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

        if (srcBitmap == null) {
            return null;
        }

        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);
            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0,
                    srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
        }
        String type = context.getContentResolver().getType(photoUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (type == null || type == "") {
            srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        } else if (type.equals("image/png")) {
            srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        } else if (type.equals("image/jpg") || type.equals("image/jpeg")) {
            srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }
        byte[] bMapArray = baos.toByteArray();
        baos.close();
        return BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
    }

    public static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor == null || cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public static int dpToPx(int dp, Activity activity) {
        float density = activity.getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static Uri createUriFromPath(String path) {
        File file = new File(path);
        Uri uri = null;

        if (file != null) {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public static Bitmap scaleLocalImageforList(String imagePath, ImageView imageView){
        try {
            Drawable drawing = imageView.getDrawable();
            Bitmap imageViewBitmap = ((BitmapDrawable) drawing).getBitmap();

            // Get current dimensions
            int width = imageViewBitmap.getWidth();
            int height = imageViewBitmap.getHeight();

            File imageFile = new File(imagePath);

            BitmapFactory.Options dbo = new BitmapFactory.Options();
            dbo.inJustDecodeBounds = true;
            Bitmap originBitmap = BitmapFactory.decodeStream(new FileInputStream(imageFile), null, dbo);
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
                srcBitmap = BitmapFactory.decodeStream(new FileInputStream(imageFile), null, options);
            } else if (pictureWidth < width) {
                widthRatio = ((float) width) / ((float) pictureWidth);
                heightRatio = ((float) height) / ((float) pictureWidth);
                maxRatio = Math.max(widthRatio, heightRatio);

                // Create the mBitmap from file
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = (int) maxRatio;
                srcBitmap = BitmapFactory.decodeStream(new FileInputStream(imageFile), null, options);
            } else {
                srcBitmap = BitmapFactory.decodeStream(new FileInputStream(imageFile));
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
}