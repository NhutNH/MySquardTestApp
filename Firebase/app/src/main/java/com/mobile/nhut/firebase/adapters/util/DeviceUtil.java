package com.mobile.nhut.firebase.adapters.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

public class DeviceUtil {

  public static Point getDeviceDimensions(Context context) {
    Point result = new Point();
    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = windowManager.getDefaultDisplay();
    if (Build.VERSION.SDK_INT >= 11) {
      try {
        display.getRealSize(result);
        return result;
      } catch (NoSuchMethodError var5) {
        result.x = display.getWidth();
        result.y = display.getHeight();
      }
    } else {
      DisplayMetrics metrics = new DisplayMetrics();
      windowManager.getDefaultDisplay().getMetrics(metrics);
      result.x = metrics.widthPixels;
      result.y = metrics.heightPixels;
    }

    return result;
  }

  public static int getDimensionPixelSizeFromAttribute(Activity context, int attrId) {
    if (attrId == 0) {
      return 0;
    }
    TypedValue typedValue = new TypedValue();
    context.getTheme().resolveAttribute(attrId, typedValue, true);
    DisplayMetrics metrics = new DisplayMetrics();
    context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
    return typedValue.complexToDimensionPixelSize(typedValue.data, metrics);
  }

  public static int getDimensionPixelSize(Context context, int resId) {
    return context.getResources().getDimensionPixelSize(resId);
  }
}
