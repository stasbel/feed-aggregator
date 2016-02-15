package belaevstanislav.feedagregator.util;

import java.util.Calendar;

public class HelpfullMethod {
    public static long getNowTime() {
        return Calendar.getInstance().getTime().getTime() / 1000;
    }
}
