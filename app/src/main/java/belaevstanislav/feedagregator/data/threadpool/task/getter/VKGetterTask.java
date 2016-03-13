package belaevstanislav.feedagregator.data.threadpool.task.getter;

import android.util.Log;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.data.threadpool.task.parser.VKParserTask;
import belaevstanislav.feedagregator.feeditem.core.FeedItemCore;
import belaevstanislav.feedagregator.feeditem.core.VKFeedItemCore;
import belaevstanislav.feedagregator.feedsource.vk.VKDataStore;
import belaevstanislav.feedagregator.service.util.Latch;
import belaevstanislav.feedagregator.util.Constant;

public class VKGetterTask extends GetterTask implements Runnable {
    private final Latch latch;
    private final boolean isNeedToCache;

    public VKGetterTask(Data data, Latch latch, boolean isNeedToCache) {
        super(data);
        this.latch = latch;
        this.isNeedToCache = isNeedToCache;
    }

    @Override
    public void run() {
        // TODO реализовать проход по страницам (результатов может быть больше, чем 100) + deal with auth
        // TODO TIME
        long time = 0;
        VKRequest feedRequest = new VKRequest(Constant.VK_API_NEWSFEED_GET,
                VKParameters.from(VKApiConst.FILTERS, Constant.VK_CONST_FILTERS,
                        VKApiConst.COUNT, Constant.VK_CONST_COUNT,
                        Constant.VK_API_CONST_START_TIME, time));
        feedRequest.executeSyncWithListener(new VKCallback());
    }

    private class HandleItemTask extends GetterTask implements Runnable {
        private final JSONObject item;
        private final VKDataStore dataStore;
        private final CountDownLatch countDownLatch;

        public HandleItemTask(Data data, JSONObject item, VKDataStore dataStore, CountDownLatch countDownLatch) {
            super(data);
            this.item = item;
            this.dataStore = dataStore;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                FeedItemCore core = new VKFeedItemCore(item);
                long id = data.database.insertCore(core);

                countDownLatch.countDown();

                data.taskPool.submitFeedItemBuilderTask(new VKParserTask(data, core, id, item, dataStore, isNeedToCache));
            } catch (JSONException exeption) {
                Log.e("VK", "VK_PARSECORE_EXCEPTION");
                exeption.printStackTrace();
            }
        }
    }

    private class VKCallback extends VKRequest.VKRequestListener {
        @Override
        public void onComplete(VKResponse response) {
            super.onComplete(response);

            try {
                JSONObject root = response.json.getJSONObject(Constant.VK_KEY_RESPONSE);

                HashMap<Integer, JSONObject> profiles = new HashMap<>();
                JSONArray profilesList = root.getJSONArray(Constant.VK_KEY_PROFILES);
                for (int index = 0; index < profilesList.length(); index++) {
                    JSONObject profile = profilesList.getJSONObject(index);
                    profiles.put(profile.getInt(Constant.VK_KEY_ID), profile);
                }

                HashMap<Integer, JSONObject> groups = new HashMap<>();
                JSONArray groupsList = root.getJSONArray(Constant.VK_KEY_GROUPS);
                for (int index = 0; index < groupsList.length(); index++) {
                    JSONObject group = groupsList.getJSONObject(index);
                    groups.put(group.getInt(Constant.VK_KEY_ID), group);
                }

                VKDataStore dataStore = new VKDataStore(profiles, groups);

                JSONArray itemList = root.getJSONArray(Constant.VK_KEY_ITEMS);
                final int size = itemList.length();

                if (size > 0) {
                    CountDownLatch countDownLatch = new CountDownLatch(size);
                    for (int index = 0; index < size; index++) {
                        JSONObject item = itemList.getJSONObject(index);
                        data.taskPool.submitRunnableTask(new HandleItemTask(data, item, dataStore, countDownLatch));
                    }

                    try {
                        countDownLatch.await();
                    } catch (InterruptedException exception) {
                        Log.e("VK", "VK_THREADS_EXCEPTION");
                        exception.printStackTrace();
                    }
                }
            } catch (JSONException exception) {
                Log.e("VK", "VK_PARSE_EXCEPTION");
                exception.printStackTrace();
            }
            latch.countDownAndTryNotify();
        }

        @Override
        public void onError(VKError error) {
            super.onError(error);
            Log.e("VK", "VK_GET_EXCEPTION");
            latch.countDownAndTryNotify();
        }
    }
}
