package belaevstanislav.feedagregator.main;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

import belaevstanislav.feedagregator.R;
import belaevstanislav.feedagregator.util.Constant;
import belaevstanislav.feedagregator.util.view.MyToolbar;

public class SettingsActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            setContentView(Constant.LAYOUT_SETTINGS);

            // toolbar
            MyToolbar.setToolbar(this);

            // fragment
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content, new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_xml);
        }
    }
}
