package belaevstanislav.feedagregator.util.helpmethod;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

public class HelpMethod {
    public static long getNowTime() {
        return Calendar.getInstance().getTime().getTime() / 1000;
    }

    public static void createActivity(Activity activity, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
    }

    public static void createActivity(Activity activity, Class<?> cls, IntentModifier modifier) {
        Intent intent = new Intent(activity, cls);
        modifier.modify(intent);
        activity.startActivity(intent);
    }

    public static void toastShort(Activity activity, String message) {
        Toast toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
