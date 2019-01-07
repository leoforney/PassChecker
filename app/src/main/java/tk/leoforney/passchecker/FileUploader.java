package tk.leoforney.passchecker;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileUploader {

    Activity activity;
    OkHttpClient client;
    Gson gson;
    private final static String TAG = "FileUploader";

    public FileUploader(Activity activity) {
        this.activity = activity;
        client = new OkHttpClient.Builder()
                .writeTimeout(3L, TimeUnit.SECONDS)
                .build();
        gson = new Gson();
    }

    //@Override
    public void onImage(/*CameraKitImage cameraKitImage*/) {
        byte[] data = new byte[5];//cameraKitImage.getJpeg();
        Log.d(TAG, "Data received: " + data.length);

        MultipartBody.Builder buildernew = new MultipartBody.Builder().setType(MultipartBody.FORM);
        MediaType type = MediaType.parse("image/jpeg");
        RequestBody imageBody = RequestBody.create(type, data);
        buildernew.addFormDataPart("image", "tempImage", imageBody);
        MultipartBody requestBody = buildernew.build();

        Request request = new Request.Builder()
                .url("http://" + activity.getResources().getString(R.string.server_url) + "/getStudentName")
                .addHeader("Token", CredentialsManager.getInstance(activity).getToken())
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();
                Log.d(TAG, responseString);
                Student student = gson.fromJson(responseString, Student.class);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "Student name: " + responseString, Toast.LENGTH_LONG).show();

                    }
                });
            }
        });
    }

}
