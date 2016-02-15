package belaevstanislav.feedagregator.util;

import android.os.AsyncTask;

public abstract class HighPriorityAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    @SuppressWarnings("unchecked")
    protected abstract Result inBackground(Params... params);

    @Override @SuppressWarnings("unchecked")
    final protected Result doInBackground(Params... params) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        return inBackground(params);
    }
}
