package belaevstanislav.feedagregator.util.view;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import belaevstanislav.feedagregator.R;

public class MyToolbar {
    public static Toolbar setToolbar(final AppCompatActivity activity) {
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onBackPressed();
                }
            });
        }
        return toolbar;
    }
}
