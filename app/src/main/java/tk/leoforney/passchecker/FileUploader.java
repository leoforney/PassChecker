package tk.leoforney.passchecker;

import android.content.Context;
import android.util.Log;

import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileUploader implements CameraKitEventListener {

    Context context;
    OkHttpClient client;
    private final static String TAG = "FileUploader";

    public FileUploader(Context context) {
        this.context = context;
        client = new OkHttpClient();
    }

    @Override
    public void onEvent(CameraKitEvent cameraKitEvent) {

    }

    @Override
    public void onError(CameraKitError cameraKitError) {

    }

    @Override
    public void onImage(CameraKitImage cameraKitImage) {
        byte[] data = cameraKitImage.getJpeg();
        MultipartBody.Builder buildernew = new MultipartBody.Builder().setType(MultipartBody.FORM);
        MediaType type = MediaType.parse("image/jpeg");
        RequestBody imageBody = RequestBody.create(type, data);
        buildernew.addFormDataPart("image", "tempImage", imageBody);
        MultipartBody requestBody = buildernew.build();
        Request request = new Request.Builder()
                .url("http://192.168.43.74:8080/getStudentName")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, response.body().string());
            }
        });
    }

    @Override
    public void onVideo(CameraKitVideo cameraKitVideo) {

    }
}
