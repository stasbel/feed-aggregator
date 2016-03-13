package belaevstanislav.feedagregator.util.view;

import android.app.Activity;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import belaevstanislav.feedagregator.R;
import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.feedlist.FeedItemViewHolder;
import belaevstanislav.feedagregator.feedlist.SwipeCallback;
import belaevstanislav.feedagregator.feedsource.FeedSourceName;
import belaevstanislav.feedagregator.main.FeedListActivity;
import belaevstanislav.feedagregator.main.LoginActivity;
import belaevstanislav.feedagregator.main.SettingsActivity;
import belaevstanislav.feedagregator.util.Constant;
import belaevstanislav.feedagregator.util.globalinterface.FeedListQuerries;
import belaevstanislav.feedagregator.util.helpmethod.HelpMethod;

public class MenuDrawer extends Drawer {
    private static final long FEED_LIST_ITEM_IDINTIFIER = 0;
    private static final long TWITTER_ITEM_IDINTIFIER = 1;
    private static final long VK_ITEM_IDINTIFIER = 2;

    private final Activity activity;
    private final FeedListQuerries querries;
    private final Data data;

    public MenuDrawer(DrawerBuilder drawerBuilder, Activity activity, Toolbar toolbar,
                      SwipeCallback swipeCallback, Data data) {
        super(drawerBuilder);

        this.activity = activity;
        this.querries = (FeedListQuerries) activity;
        this.data = data;

        drawerBuilder.withActivity(activity)
                .withToolbar(toolbar)
                .withHeader(Constant.LAYOUT_DRAWER_HEADER)
                .withCloseOnClick(true)
                .withOnDrawerListener(new DrawerCallback(swipeCallback))
                .addDrawerItems(createFeedListItem(),
                        createTWITTERItem(),
                        createVKItem(),
                        new DividerDrawerItem(),
                        createLoginItem(),
                        new DividerDrawerItem(),
                        createSettingsItem(),
                        createExitItem())
                .build();
    }

    private Drawer.OnDrawerItemClickListener createOnClickItemListener(final Class<?> cls) {
        return new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                if (activity.getClass() != cls) {
                    HelpMethod.createActivity(activity, cls);
                }
                return false;
            }
        };
    }

    private PrimaryDrawerItem createFeedListItem() {
        return new PrimaryDrawerItem().withName(R.string.drawer_item_feedlist).withIcon(R.drawable.ic_list_black_48dp)
                .withIdentifier(FEED_LIST_ITEM_IDINTIFIER)
                .withBadgeStyle(new BadgeStyle().withTextColor(Constant.DRAWER_TEXT_COLOR).withColor(Constant.DRAWER_BACKGROUND_COLOR)
                        .withCorners(5))
                .withOnDrawerItemClickListener(createOnClickItemListener(FeedListActivity.class))
                .withSetSelected(true)
                .withOnDrawerItemClickListener(new OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        querries.showAll();
                        return false;
                    }
                });
    }

    private SecondaryDrawerItem createTWITTERItem() {
        return new SecondaryDrawerItem().withName("TWITTER")
                .withIdentifier(TWITTER_ITEM_IDINTIFIER)
                .withBadgeStyle(new BadgeStyle().withTextColor(Constant.DRAWER_TEXT_COLOR).withColor(Constant.DRAWER_TWITTER_COLOR)
                        .withCorners(5).withPadding(1))
                .withOnDrawerItemClickListener(new OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        querries.showOnlySource(FeedSourceName.TWITTER);
                        return false;
                    }
                });
    }

    private SecondaryDrawerItem createVKItem() {
        return new SecondaryDrawerItem().withName("VK")
                .withIdentifier(VK_ITEM_IDINTIFIER)
                .withBadgeStyle(new BadgeStyle().withTextColor(Constant.DRAWER_TEXT_COLOR).withColor(Constant.DRAWER_VK_COLOR)
                        .withCorners(5).withPadding(1))
                .withOnDrawerItemClickListener(new OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        querries.showOnlySource(FeedSourceName.VK);
                        return false;
                    }
                });
    }

    private PrimaryDrawerItem createLoginItem() {
        return new PrimaryDrawerItem().withName(R.string.drawer_item_login).withIcon(R.drawable.ic_account_circle_black_48dp)
                .withSelectable(false)
                .withOnDrawerItemClickListener(createOnClickItemListener(LoginActivity.class));
    }

    private PrimaryDrawerItem createSettingsItem() {
        return new PrimaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(R.drawable.ic_settings_black_48dp)
                .withSelectable(false)
                .withOnDrawerItemClickListener(createOnClickItemListener(SettingsActivity.class));
    }

    private PrimaryDrawerItem createExitItem() {
        return new PrimaryDrawerItem().withName(R.string.drawer_item_exit).withIcon(R.drawable.ic_exit_to_app_black_48dp)
                .withSelectable(false)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        activity.finish();
                        return true;
                    }
                });
    }

    @Nullable
    private StringHolder createBadgeStringHolder(int count, String prefix) {
        if (count > 0) {
            return new StringHolder(prefix + String.valueOf(count));
        } else {
            return null;
        }
    }

    public void selectFeedList() {
        setSelection(FEED_LIST_ITEM_IDINTIFIER);
    }

    public void updateBadges() {
        Cursor cursor = data.database.getAll();

        int ALLCount = cursor.getCount();
        int TWITTERCount = 0;
        int VKCount = 0;

        if (ALLCount > 0) {
            while (cursor.moveToNext()) {
                FeedSourceName sourceName = FeedSourceName.valueOf(cursor.getString(cursor.getColumnIndex(Constant.DATABASE_KEY_TABLE_SOURCE)));
                switch (sourceName) {
                    case TWITTER:
                        TWITTERCount++;
                        break;
                    case VK:
                        VKCount++;
                        break;
                }
            }
            cursor.moveToPosition(-1);
        }

        updateBadge(FEED_LIST_ITEM_IDINTIFIER, createBadgeStringHolder(ALLCount, "+"));
        updateBadge(TWITTER_ITEM_IDINTIFIER, createBadgeStringHolder(TWITTERCount, ""));
        updateBadge(VK_ITEM_IDINTIFIER, createBadgeStringHolder(VKCount, ""));
    }

    public Selection getSelection() {
        switch (getCurrentSelectedPosition()) {
            case 1:
                return Selection.ALL;
            case 2:
                return Selection.TWITTER;
            case 3:
                return Selection.VK;
        }
        return Selection.ALL;
    }

    public enum Selection {
        ALL,
        TWITTER,
        VK
    }

    private class DrawerCallback implements Drawer.OnDrawerListener {
        private final SwipeCallback swipeCallback;

        public DrawerCallback(SwipeCallback swipeCallback) {
            this.swipeCallback = swipeCallback;
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            FeedItemViewHolder viewHolder = swipeCallback.getLastSwiped();
            if (viewHolder != null) {
                viewHolder.resetSwipeState();
            }
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            //...
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            //...
        }
    }
}
