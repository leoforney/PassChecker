package tk.leoforney.passchecker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wonderkiln.camerakit.CameraView;

public class CameraFragment extends Fragment {

    CameraView cameraView;
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
        cameraView.start();
        Log.d(TAG, "Started");
    }

    @Override
    public void onPause() {
        cameraView.stop();
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
        view.findViewById(R.id.picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraView.isStarted()) {
                    cameraView.captureImage();
                }
            }
        });
        cameraView = view.findViewById(R.id.camera);
        cameraView.addCameraKitListener(uploader);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
