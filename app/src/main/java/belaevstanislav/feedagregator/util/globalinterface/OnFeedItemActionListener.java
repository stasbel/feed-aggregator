package belaevstanislav.feedagregator.util.globalinterface;

public interface OnFeedItemActionListener {
    void onOpen(int position, long id, boolean isFullWay);
    void onDelete(int position, long id);
}
