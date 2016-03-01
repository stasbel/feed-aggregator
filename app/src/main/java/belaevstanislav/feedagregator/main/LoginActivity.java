package belaevstanislav.feedagregator.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import belaevstanislav.feedagregator.R;
import belaevstanislav.feedagregator.util.MyToolbar;

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
            ImageButton imageButtonTWITTER = (ImageButton) findViewById(R.id.twitter_login_button);
            imageButtonTWITTER.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Twitter.getSessionManager().getActiveSession() == null
                            || Twitter.getSessionManager().getActiveSession().getAuthToken().isExpired()) {
                        twitterAuthClient = new TwitterAuthClient();
                        twitterAuthClient.authorize(LoginActivity.this, new Callback<TwitterSession>() {
                            @Override
                            public void success(Result<TwitterSession> result) {
                                //...
                            }

                            @Override
                            public void failure(TwitterException e) {
                                //...
                            }
                        });
                    } else {
                        Toast toast = Toast.makeText(LoginActivity.this, "Already login in TWITTER!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }
}
