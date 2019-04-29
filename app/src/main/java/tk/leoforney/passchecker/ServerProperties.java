package tk.leoforney.passchecker;

import android.app.Activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServerProperties {

    private static ServerProperties instance;

    private HashMap<String, String> storedValues;
    private OkHttpClient client;
    private Activity activity;

    public static ServerProperties getInstance(Activity activity) {
        if (instance == null) {
            instance = new ServerProperties(activity);
        }
        return instance;
    }

    private ServerProperties(Activity activity) {
        storedValues = new HashMap<>();
        client = new OkHttpClient.Builder()
                .writeTimeout(3L, TimeUnit.SECONDS)
                .build();
    }

    public String getProperty(String key, String def) {
        if (!storedValues.containsKey(key)) {
            Request sendingThresholdRequest = new Request.Builder()
                    .url("http://" + CredentialsManager.getInstance(activity).getIP() + "/getProperty/" + key)
                    .get()
                    .build();
            client.newCall(sendingThresholdRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String body = response.body().string();
                    if (!body.equals("Property not set")) {
                        storedValues.put(key, response.body().string());
                    }
                }
            });
            return def;
        } else {
            return storedValues.get(key);
        }
    }

    public int getPropertyInt(String key, int def) {
        return Integer.valueOf(getProperty(key, String.valueOf(def)));
    }

    public boolean getPropertyBool(String key, boolean def) {
        return Boolean.getBoolean(getProperty(key, String.valueOf(def)));
    }
}
