package belaevstanislav.feedagregator.util;

public class AsyncLatch {
    private int count;
    private InvokingMethod invokingMethod;

    public AsyncLatch(int count, InvokingMethod invokingMethod) {
        this.count = count;
        this.invokingMethod = invokingMethod;
    }

    public synchronized void countDownAndTryInvoke() {
        count--;
        if (count == 0) {
            invokingMethod.invoke();
        }
    }
}
