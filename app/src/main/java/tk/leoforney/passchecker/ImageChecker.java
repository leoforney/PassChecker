package tk.leoforney.passchecker;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.Image;
import android.util.Log;

import com.google.gson.Gson;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
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

public class ImageChecker implements EZCamCallback, AbstractUVCCameraHandler.OnPreViewResultListener, Camera.PreviewCallback {

    Activity activity;
    OkHttpClient client;
    Gson gson;
    ServerListener listener;
    private final static String TAG = "ImageChecker";

    public ImageChecker(Activity activity) {
        this.activity = activity;
        client = new OkHttpClient.Builder()
                .writeTimeout(3L, TimeUnit.SECONDS)
                .readTimeout(3L, TimeUnit.SECONDS)
                .cache(null)
                .build();
        Request sendingThresholdRequest = new Request.Builder()
                .url("http://" + CredentialsManager.getInstance(activity).getIP() + "/getProperty/frameCount")
                .get()
                .build();
        client.newCall(sendingThresholdRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                if (isInteger(responseBody)) {
                    sendingThreshold = Integer.parseInt(responseBody);
                }
            }
        });
        gson = new Gson();
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }

    public void setServerListener(ServerListener listener) {
        this.listener = listener;
    }

    public void removeServerListener() {
        this.listener = null;
    }

    @Override
    public void onCameraReady() {

    }

    @Override
    public void onPicture(Image image) {
        byte[] jpegData = NV21toJPEG(YUV420toNV21(image), image.getWidth(), image.getHeight());
        upload(jpegData);
    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onCameraDisconnected() {

    }

    public void upload(byte[] data) {
        upload(data, true);
    }

    public void upload(byte[] data, boolean rotate) {
        if (data != null) {
            Log.d(TAG, "Data received: " + data.length);

            MultipartBody.Builder buildernew = new MultipartBody.Builder().setType(MultipartBody.FORM);
            MediaType type = MediaType.parse("image/jpeg");
            RequestBody imageBody = RequestBody.create(type, data);
            buildernew.addFormDataPart("image", "image.jpg", imageBody);
            MultipartBody requestBody = buildernew.build();

            Log.d(TAG, "Image ready to upload");

            Request request = new Request.Builder()
                    .url("http://" + CredentialsManager.getInstance(activity).getIP() + "/checkInDatabase")
                    .addHeader("Token", CredentialsManager.getInstance(activity).getToken())
                    .addHeader("Sender", rotate ? "Mobile(Rotate)" : "Mobile(NoRotate)")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    final String responseString = response.body().string();
                    Log.d(TAG, responseString);
                    DatabaseResponse databaseResponse = gson.fromJson(responseString, DatabaseResponse.class);
                    if (listener != null) {
                        listener.response(databaseResponse);
                    }
                }
            });
        }
    }

    public int width, height;

    // Keep in mind, we're using nv21Yuv as it's most efficient in the sample
    int usbFrameCount = 0;
    int sendingThreshold = 5;
    @Override
    public void onPreviewResult(byte[] nv21Yuv) {
        usbFrameCount++;
        if (usbFrameCount > sendingThreshold) {
            Log.d(TAG, "Byte data received from USB Camera");
            byte[] jpegData = NV21toJPEG(nv21Yuv, width, height);
            upload(jpegData, false);
            usbFrameCount = 0;
        }
    }

    private static byte[] NV21toJPEG(byte[] nv21, int width, int height) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        yuv.compressToJpeg(new Rect(0, 0, width, height), 90, out);
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

    private static double threshold = 66; // Similarity threshold in percentage out of 100

    public static boolean similarityOfStrings(String s1, String s2, double threshold) {
        double similarityCount = 0;
        double totalCount = s1.length();
        List<Character> origin = charList(s1.toLowerCase().replace(" ", "").toCharArray());
        List<Character> comparison = charList(s2.toLowerCase().replace(" ", "").toCharArray());
        for (Character iteratedOrigin: origin) {
            for (Character comparisonChar: comparison) {
                if (comparisonChar != null) {
                    if (comparisonChar.equals(iteratedOrigin)) {
                        similarityCount++;

                        comparison.remove(comparisonChar);
                        break;
                    }
                }
            }
        }
        double percentage = ((similarityCount/totalCount) * 100);
        if (percentage > 100) {
            percentage = 100;
        }
        Log.d(TAG, "S: " + similarityCount + " T: " + totalCount + " " + percentage + "%");
        if (percentage > threshold) return true;
        return false;
    }

    public static boolean similarityOfStrings(String s1, String s2) {
        return similarityOfStrings(s1, s2, threshold);
    }

    public static List<Character> charList(char[] array) {
        List<Character> chars = new ArrayList<>();
        for (char ch: array) {
            chars.add(ch);
        }
        return chars;
    }

    int frameCount = 0;

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        frameCount++;
        if (frameCount > 15) {
            Camera.Parameters parameters = camera.getParameters();
            int format = parameters.getPreviewFormat();
            //YUV formats require more conversion
            if (format == ImageFormat.NV21 || format == ImageFormat.YUY2 || format == ImageFormat.NV16) {
                int w = parameters.getPreviewSize().width;
                int h = parameters.getPreviewSize().height;
                byte[] dataJpg = NV21toJPEG(bytes, w, h);
                Log.d(TAG, "Byte data received from Camera");
                if (dataJpg != null) {
                    upload(dataJpg);
                }
                dataJpg = null;
                System.gc();
                camera.addCallbackBuffer(bytes);
                camera.setPreviewCallbackWithBuffer(this);
                //camera.addCallbackBuffer(null);
            }
        }

    }
}
