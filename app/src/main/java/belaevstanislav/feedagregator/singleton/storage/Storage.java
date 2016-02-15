package belaevstanislav.feedagregator.singleton.storage;

import android.content.Context;
import android.content.SharedPreferences;

import belaevstanislav.feedagregator.util.Constant;
import belaevstanislav.feedagregator.util.HelpfullMethod;

public class Storage {
    private final SharedPreferences sharedPreferences;

    public Storage(Context context) {
        sharedPreferences = context.getSharedPreferences(Constant.STORAGE_NAME, Context.MODE_PRIVATE);
    }

    /*public void saveString(StorageKeys key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key.toString(), value);
        editor.apply();
    }*/

    public void saveLong(StorageKeys key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key.toString(), value);
        editor.apply();
    }

    /*public String getString(StorageKeys key) {
        return sharedPreferences.getString(key.toString(), "");
    }*/

    public long getLong(StorageKeys key) {
        Long defaultValue;
        switch (key) {
            case LAST_TIME_OF_FEED_LIST_REFRESH:
                defaultValue = HelpfullMethod.getNowTime();
                break;
            case LAST_TWEET_ID:
                defaultValue = 0L;
                break;
            default:
                defaultValue = 0L;
                break;
        }
        return sharedPreferences.getLong(key.toString(), defaultValue);
    }

    /*public void delete(StorageKeys key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key.toString());
        editor.apply();
    }*/

    public boolean isInMemory(StorageKeys key) {
        return sharedPreferences.contains(key.toString());
    }
}
