package tk.leoforney.passchecker;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.camerakit.CameraKitView;

import androidx.fragment.app.Fragment;

public class CameraFragment extends Fragment implements CameraKitView.CameraListener, CameraKitView.ImageCallback, CameraKitView.FrameCallback {

    private CameraKitView cameraKitView;
    private static final String TAG = "CameraFragment";

    public CameraFragment() {

    }


    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    FileUploader uploader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        uploader = new FileUploader(getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraKitView.onResume();
        cameraKitView.captureFrame(this);
        Log.d(TAG, "Started");
    }

    @Override
    public void onPause() {
        cameraKitView.onPause();
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
        cameraKitView = view.findViewById(R.id.camera);
        cameraKitView.setCameraListener(this);
        cameraKitView.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        if (cameraKitView != null) {
            cameraKitView.onStop();
        }
        super.onDetach();
    }

    @Override
    public void onOpened() {

    }

    @Override
    public void onClosed() {

    }

    @Override
    public void onImage(CameraKitView cameraKitView, byte[] bytes) {
        /*
        Log.d(TAG, "Image received");
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cameraKitView.captureImage(this);*/
    }

    @Override
    public void onFrame(CameraKitView cameraKitView, byte[] bytes) {
        Log.d(TAG, "Image received");
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cameraKitView.captureFrame(this);
    }
}
