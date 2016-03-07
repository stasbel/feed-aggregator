package belaevstanislav.feedagregator.util;

import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import belaevstanislav.feedagregator.R;
import belaevstanislav.feedagregator.main.FeedListActivity;
import belaevstanislav.feedagregator.main.LoginActivity;
import belaevstanislav.feedagregator.main.SettingsActivity;
import belaevstanislav.feedagregator.util.helpfullmethod.HelpfullMethod;

public class MyDrawer {
    private static final long FEED_LIST_ITEM_IDITIFIER = 322;

    private static Drawer.OnDrawerItemClickListener createOnClickItemListener(final Activity activity, final Class<?> cls) {
        return new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                if (activity.getClass() != cls) {
                    HelpfullMethod.createActivity(activity, cls);
                }
                return false;
            }
        };
    }

    private static PrimaryDrawerItem createFeedListItem(final Activity activity) {
        return new PrimaryDrawerItem().withName(R.string.drawer_item_feedlist).withIcon(R.drawable.ic_list_black_48dp)
                .withIdentifier(FEED_LIST_ITEM_IDITIFIER)
                .withOnDrawerItemClickListener(createOnClickItemListener(activity, FeedListActivity.class));
    }

    private static PrimaryDrawerItem createLoginItem(final Activity activity) {
        return new PrimaryDrawerItem().withName(R.string.drawer_item_login).withIcon(R.drawable.ic_account_circle_black_48dp)
                .withOnDrawerItemClickListener(createOnClickItemListener(activity, LoginActivity.class));
    }

    private static PrimaryDrawerItem createSettingsItem(final Activity activity) {
        return new PrimaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(R.drawable.ic_settings_black_48dp)
                .withOnDrawerItemClickListener(createOnClickItemListener(activity, SettingsActivity.class));
    }

    private static PrimaryDrawerItem createExitItem(final Activity activity) {
        return new PrimaryDrawerItem().withName(R.string.drawer_item_exit).withIcon(R.drawable.ic_exit_to_app_black_48dp)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        activity.finish();
                        return true;
                    }
                });
    }

    public static Drawer createDrawer(Activity activity, Toolbar toolbar) {
        return new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withHeader(R.layout.drawer_header)
                .withCloseOnClick(true)
                .addDrawerItems(createFeedListItem(activity).withSetSelected(true),
                        new DividerDrawerItem(),
                        createLoginItem(activity),
                        new DividerDrawerItem(),
                        createSettingsItem(activity),
                        createExitItem(activity))
                .build();
    }

    public static void setBadge(Drawer drawer, int count) {
        drawer.updateBadge(FEED_LIST_ITEM_IDITIFIER, new StringHolder("+" + String.valueOf(count)));
    }
}
