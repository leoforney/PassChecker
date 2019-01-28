package tk.leoforney.passchecker;

import android.Manifest;
import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import androidx.fragment.app.Fragment;
import me.aflak.ezcam.EZCam;
import me.aflak.ezcam.EZCamCallback;

public class CameraFragment extends Fragment implements EZCamCallback {

    private static final String TAG = "CameraFragment";
    private EZCam cam;
    private TextureView textureView;
    FileUploader uploader;

    public CameraFragment() {

    }


    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        uploader = new FileUploader(getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Started");
    }

    @Override
    public void onPause() {
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "Camera opened!");

        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.CAMERA
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {}
        }).check();

        cam = new EZCam(getContext());
        textureView = view.findViewById(R.id.textureView);
        String id = cam.getCamerasList().get(CameraCharacteristics.LENS_FACING_BACK);
        cam.selectCamera(id);
        cam.setCameraCallback(this);
        cam.open(CameraDevice.TEMPLATE_PREVIEW, textureView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        cam.stopPreview();
        cam.close();
        super.onDetach();
    }

    @Override
    public void onCameraReady() {
        // triggered after cam.open(...)
        // you can set capture settings for example:
        cam.setCaptureSetting(CaptureRequest.COLOR_CORRECTION_ABERRATION_MODE, CameraMetadata.COLOR_CORRECTION_ABERRATION_MODE_HIGH_QUALITY);
        cam.setCaptureSetting(CaptureRequest.CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_OFF);

        // then start the preview
        cam.startPreview();
    }

    int imagesReceived = 0;

    @Override
    public void onPicture(Image image) {
        Log.d(TAG, "Image receieved");
        imagesReceived++;
        if (imagesReceived > 15) {
            Log.d(TAG, "Count 15");
            uploader.onPicture(image);
            imagesReceived = 0;
        }
        if (image != null) {
            image.close();
        }
    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onCameraDisconnected() {
    }
}
