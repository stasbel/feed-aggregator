package belaevstanislav.feedagregator.feedsource.vk;

import org.json.JSONObject;

import java.util.HashMap;

public class VKDataStore {
    public final HashMap<Integer, JSONObject> profiles;
    public final HashMap<Integer, JSONObject> groups;

    public VKDataStore(HashMap<Integer, JSONObject> profiles, HashMap<Integer, JSONObject> groups) {
        this.profiles = profiles;
        this.groups = groups;
    }
}
