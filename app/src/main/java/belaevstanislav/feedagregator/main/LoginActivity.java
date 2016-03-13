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
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import belaevstanislav.feedagregator.R;
import belaevstanislav.feedagregator.feedsource.twitter.TWITTER;
import belaevstanislav.feedagregator.feedsource.vk.VK;
import belaevstanislav.feedagregator.util.Constant;
import belaevstanislav.feedagregator.util.helpmethod.HelpMethod;
import belaevstanislav.feedagregator.util.view.MyToolbar;

public class LoginActivity extends AppCompatActivity {
    private static final int TWITTER_REQUEST_CODE_RESULT = 140;
    private static final int VK_REQUEST_CODE_RESULT = 10485;

    private TwitterAuthClient twitterAuthClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            setContentView(Constant.LAYOUT_LOGIN);

            // toolbar
            MyToolbar.setToolbar(this);

            // login buttons

            // twitter
            ImageButton loginButtonTWITTER = (ImageButton) findViewById(R.id.twitter_login_button);
            twitterAuthClient = new TwitterAuthClient();
            loginButtonTWITTER.setOnClickListener(new TWITTERLoginCliclListener());

            // vk
            ImageButton loginButtonVK = (ImageButton) findViewById(R.id.vk_login_button);
            loginButtonVK.setOnClickListener(new VKLoginClickListener());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TWITTER_REQUEST_CODE_RESULT:
                super.onActivityResult(requestCode, resultCode, data);
                twitterAuthClient.onActivityResult(requestCode, resultCode, data);
                break;
            case VK_REQUEST_CODE_RESULT:
                if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKOnActivityResultCallback())) {
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
        twitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }

    private class TWITTERLoginCliclListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            TWITTER.login(LoginActivity.this, twitterAuthClient, new TWITTER.LoginCallback() {
                @Override
                public void onSuccess(Result<TwitterSession> result) {
                    // ...
                }

                @Override
                public void onFail(TwitterException e) {
                    // ...
                }

                @Override
                public void onAllreadyLoginIn() {
                    HelpMethod.toastShort(LoginActivity.this, Constant.TOAST_TWITTER_ALREADY_LOGIN_IN);
                }
            });
        }
    }

    private class VKLoginClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            VK.login(LoginActivity.this, new VK.LoginCallback() {
                @Override
                public void onAllreadyLoginIn() {
                    HelpMethod.toastShort(LoginActivity.this, Constant.TOAST_VK_ALREADY_LOGIN_IN);
                }
            });
        }
    }

    private class VKOnActivityResultCallback implements VKCallback<VKAccessToken> {
        @Override
        public void onResult(VKAccessToken res) {
            //...
        }

        @Override
        public void onError(VKError error) {
            //...
        }
    }
}
