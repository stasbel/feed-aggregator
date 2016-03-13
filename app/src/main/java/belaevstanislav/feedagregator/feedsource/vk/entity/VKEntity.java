package belaevstanislav.feedagregator.feedsource.vk.entity;

import android.content.Context;
import android.os.Parcelable;
import android.view.View;

// TODO надо сделать все entity https://vk.com/dev/attachments_w
public interface VKEntity extends Parcelable {
    View getView(Context context);
}
