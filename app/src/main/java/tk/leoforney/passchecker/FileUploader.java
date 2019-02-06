package tk.leoforney.passchecker;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import me.aflak.ezcam.EZCamCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileUploader implements EZCamCallback, AbstractUVCCameraHandler.OnPreViewResultListener {

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

    @Override
    public void onCameraReady() {

    }

    @Override
    public void onPicture(Image image) {
        byte[] jpegData = NV21toJPEG(YUV420toNV21(image), image.getWidth(), image.getHeight(), 100);
        upload(jpegData);
    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onCameraDisconnected() {

    }

    public void upload(byte[] data) {

        Log.d(TAG, "Data received: " + data.length);

        MultipartBody.Builder buildernew = new MultipartBody.Builder().setType(MultipartBody.FORM);
        MediaType type = MediaType.parse("image/jpeg");
        RequestBody imageBody = RequestBody.create(type, data);
        buildernew.addFormDataPart("image", "tempImage", imageBody);
        MultipartBody requestBody = buildernew.build();

        Log.d(TAG, "Image ready to upload");

        Request request = new Request.Builder()
                .url("http://" + CredentialsManager.getInstance(activity).getIP() + "/plateNumber")
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
                Log.d(TAG, "Response: " + responseString);
                if (!responseString.toLowerCase().contains("no plates")) {
                    //Student student = gson.fromJson(responseString, Student.class);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "Plate Number: " + responseString, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    // Keep in mind, we're using nv21Yuv as it's most efficient in the sample
    @Override
    public void onPreviewResult(byte[] bytes) {

    }

    private static byte[] NV21toJPEG(byte[] nv21, int width, int height, int quality) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        yuv.compressToJpeg(new Rect(0, 0, width, height), quality, out);
        return out.toByteArray();
    }

    private static byte[] YUV420toNV21(Image image) {
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];

        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    channelOffset = width * height + 1;
                    outputStride = 2;
                    break;
                case 2:
                    channelOffset = width * height;
                    outputStride = 2;
                    break;
            }

            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();

            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
        }
        return data;
    }
}
