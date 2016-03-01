package belaevstanislav.feedagregator.util.helpfullmethod;

import android.app.Activity;
import android.content.Intent;

import java.util.Calendar;

public class HelpfullMethod {
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
}
