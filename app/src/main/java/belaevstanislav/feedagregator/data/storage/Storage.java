package belaevstanislav.feedagregator.data.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import belaevstanislav.feedagregator.util.Constant;
import belaevstanislav.feedagregator.util.helpmethod.HelpMethod;

public class Storage {
    private final SharedPreferences sharedPreferences;

    public Storage(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /*public void saveString(StorageKey key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key.toString(), value);
        editor.apply();
    }*/

    public void saveLong(StorageKey key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key.toString(), value);
        editor.apply();
    }

    /*public String getString(StorageKey key) {
        return sharedPreferences.getString(key.toString(), "");
    }*/

    public long getLong(StorageKey key) {
        Long defaultValue;
        switch (key) {
            case LAST_TIME_OF_FEED_LIST_REFRESH:
                defaultValue = HelpMethod.getNowTime();
                break;
            case LAST_TWEET_ID:
                defaultValue = Constant.STORAGE_DEFAULT_LAST_TWEET_ID;
                break;
            default:
                defaultValue = Constant.STORAGE_DEFAULT_LONG;
                break;
        }
        return sharedPreferences.getLong(key.toString(), defaultValue);
    }

    public boolean getBoolean(StorageKey key) {
        return sharedPreferences.getBoolean(key.toString(), Constant.STORAGE_DEFAULT_IS_SAVE_NEWS);
    }

    /*public void delete(StorageKey key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key.toString());
        editor.apply();
    }*/

    public boolean isInMemory(StorageKey key) {
        return sharedPreferences.contains(key.toString());
    }
}
