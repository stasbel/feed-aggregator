package belaevstanislav.feedagregator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import belaevstanislav.feedagregator.singleton.database.DatabaseManager;
import belaevstanislav.feedagregator.singleton.images.ImagesManager;
import belaevstanislav.feedagregator.singleton.storage.StorageManager;
import belaevstanislav.feedagregator.singleton.threads.ThreadsManager;

public class LoginActivity extends Activity {
    private TwitterAuthClient twitterAuthClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        StorageManager.initialize();
        DatabaseManager.initialize();
        ThreadsManager.initialize();
        ImagesManager.initialize();

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

        Button button = (Button)findViewById(R.id.go_to_list_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FeedListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        twitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseManager.getInstance().close();
    }
}
