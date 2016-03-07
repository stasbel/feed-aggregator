package belaevstanislav.feedagregator.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import belaevstanislav.feedagregator.R;
import belaevstanislav.feedagregator.feedsource.twitter.TWITTER;
import belaevstanislav.feedagregator.util.Constant;
import belaevstanislav.feedagregator.util.view.MyToolbar;
import belaevstanislav.feedagregator.util.helpmethod.HelpMethod;

public class LoginActivity extends AppCompatActivity {
    private TwitterAuthClient twitterAuthClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            setContentView(R.layout.login_layout);

            // toolbar
            MyToolbar.setToolbar(this);

            // login button(s)
            ImageButton loginButtonTWITTER = (ImageButton) findViewById(R.id.twitter_login_button);
            twitterAuthClient = new TwitterAuthClient();
            loginButtonTWITTER.setOnClickListener(new TWITTERLoginCliclListener());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }

    private class TWITTERLoginCliclListener implements View.OnClickListener {
        @Override public void onClick(View v) {
            TWITTER.login(LoginActivity.this, twitterAuthClient, new TWITTER.LoginCallback() {
                @Override public void onSuccess(Result<TwitterSession> result) {
                    // ...
                }

                @Override public void onFail(TwitterException e) {
                    // ...
                }

                @Override public void onAllreadyLoginIn() {
                    HelpMethod.toastShort(LoginActivity.this, Constant.TOAST_TWITTER_ALREADY_LOGIN_IN);
                }
            });
        }
    }
}
