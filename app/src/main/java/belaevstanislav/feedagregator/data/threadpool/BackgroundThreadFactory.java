package belaevstanislav.feedagregator.data.threadpool;

import android.os.Process;
import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

public class BackgroundThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(@NonNull Runnable r) {
        return new Thread(new RunnableBackgroundWrapper(r));
    }

    private class RunnableBackgroundWrapper implements Runnable {
        private final Runnable runnable;

        public RunnableBackgroundWrapper(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            runnable.run();
        }
    }
}
