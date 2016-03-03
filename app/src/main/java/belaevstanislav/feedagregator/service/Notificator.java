package belaevstanislav.feedagregator.service;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;

import belaevstanislav.feedagregator.util.Constant;

public class Notificator {
    public static final String ACTION = Constant.DATASERVICE_ACTION;

    private final LocalBroadcastManager localBroadcastManager;

    public Notificator(LocalBroadcastManager localBroadcastManager) {
        this.localBroadcastManager = localBroadcastManager;
    }

    public void notifyReadyToShow() {
        Intent intent = new Intent(ACTION);
        intent.putExtra(NotificatorMessage.MESSAGE_KEY, (Parcelable) NotificatorMessage.READY_TO_SHOW);
        localBroadcastManager.sendBroadcast(intent);
    }
}
