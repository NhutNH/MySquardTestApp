package com.mobile.nhut.firebase.base;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class AppSettings implements AppSettingConstants, SharedPreferences.OnSharedPreferenceChangeListener {

  private static AppSettings sInstance;

  private final Object LOCK = new Object();

  private final SharedPreferences mPreferences;

  private String mUserId;

  private String mToken;

  public AppSettings(Context context) {
    mPreferences = context.getSharedPreferences(KEY_PREF_NAME, Context.MODE_PRIVATE);
    loadPrefs(mPreferences);
    mPreferences.registerOnSharedPreferenceChangeListener(this);
  }

  public static AppSettings getInstance(Context context) {
    if (sInstance == null) {
      synchronized (AppSettings.class) {
        if (sInstance == null) {
          sInstance = new AppSettings(context);
        }
      }
    }
    return sInstance;
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
    loadPrefs(prefs);
  }

  @Override
  protected void finalize() throws Throwable {
    mPreferences.unregisterOnSharedPreferenceChangeListener(this);
    super.finalize();
  }

  public SharedPreferences getPreferences() {
    return mPreferences;
  }

  private void updateBoolean(String key, boolean value, boolean commit) {
    SharedPreferences.Editor editor = mPreferences.edit();
    editor.putBoolean(key, value);
    if (commit) {
      commit(editor);
    }
  }

  private void updateInt(String key, int value, boolean commit) {
    SharedPreferences.Editor editor = mPreferences.edit();
    editor.putInt(key, value);
    if (commit) {
      commit(editor);
    }
  }

  private void updateLong(String key, long value, boolean commit) {
    SharedPreferences.Editor editor = mPreferences.edit();
    editor.putLong(key, value);
    if (commit) {
      commit(editor);
    }
  }

  private void updateString(String key, String value, boolean commit) {
    SharedPreferences.Editor editor = mPreferences.edit();
    editor.putString(key, value);
    if (commit) {
      commit(editor);
    }
  }

  public void reset() {
    SharedPreferences.Editor editor = mPreferences.edit();
    editor.clear();
    commit(editor);
    loadPrefs(mPreferences);
  }

  public void resetButKeep(String... keysToKeep) {
    Map<String, ?> settings = mPreferences.getAll();
    Map<String, Object> settingsToKeep = null;
    if (keysToKeep != null && keysToKeep.length > 0) {
      settingsToKeep = new HashMap<String, Object>();
      for (String keyToKeep : keysToKeep) {
        if (settings.containsKey(keyToKeep)) {
          Object valueToKeep = settings.get(keyToKeep);
          if (valueToKeep != null) {
            settingsToKeep.put(keyToKeep, valueToKeep);
          }
        }
      }
    }

    reset();
    if (settingsToKeep != null && settingsToKeep.size() > 0) {
      SharedPreferences.Editor editor = mPreferences.edit();
      for (String keyToKeep : settingsToKeep.keySet()) {
        Object valueToKeep = settingsToKeep.get(keyToKeep);
        if (valueToKeep instanceof String) {
          editor.putString(keyToKeep, (String) valueToKeep);
        } else if (valueToKeep instanceof Integer) {
          editor.putInt(keyToKeep, (Integer) valueToKeep);
        } else if (valueToKeep instanceof Float) {
          editor.putFloat(keyToKeep, (Float) valueToKeep);
        } else if (valueToKeep instanceof Boolean) {
          editor.putBoolean(keyToKeep, (Boolean) valueToKeep);
        } else if (valueToKeep instanceof Long) {
          editor.putLong(keyToKeep, (Long) valueToKeep);
        }
      }
      commit(editor);
    }
  }

  private void commit(SharedPreferences.Editor editor) {
    synchronized (LOCK) {
      editor.apply();
    }
  }

  public String getUserId() {
    return mUserId;
  }

  public void setUserId(String userId) {
    mUserId = userId;
    this.updateString(KEY_USER_ID, userId, true);
  }

  public String getToken() {
    return mToken;
  }

  public void setToken(String token) {
    mToken = token;
    this.updateString(KEY_OAUTH_TOKEN, token, true);
  }

  private void loadPrefs(SharedPreferences prefs) {
    synchronized (LOCK) {
      mUserId = prefs.getString(KEY_USER_ID, null);
      mToken = prefs.getString(KEY_OAUTH_TOKEN, null);
    }
  }

  public void clearSharePre() {
    setUserId(null);
    setToken(null);
  }
}
