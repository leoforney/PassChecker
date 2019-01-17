package tk.leoforney.passchecker;

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

import com.camerakit.CameraKitView;

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

    @Override
    public void onPicture(Image image) {

    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onCameraDisconnected() {

    }
}
