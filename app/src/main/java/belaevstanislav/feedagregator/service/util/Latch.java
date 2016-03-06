package belaevstanislav.feedagregator.service.util;

import belaevstanislav.feedagregator.service.Notificator;

public class Latch {
    private int count;
    private final Notificator notificator;

    public Latch(int count, Notificator notificator) {
        this.count = count;
        this.notificator = notificator;
    }

    public synchronized void countDownAndTryNotify() {
        count--;
        if (count == 0) {
            notificator.notifyReadyToShow();
        }
    }
}
