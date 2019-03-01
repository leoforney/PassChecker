package tk.leoforney.passchecker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.aflak.ezcam.EZCam;
import me.aflak.ezcam.EZCamCallback;

public class CameraFragment extends Fragment implements ServerListener {

    private static final String TAG = "CameraFragment";
    TextView plateNumberTextView;
    TextView studentNameTextView;
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PlateFailedAdapter adapter;
    ImageChecker uploader;

    Preview preview;
    Camera camera;
    Activity act;
    Context ctx;

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
        uploader = new ImageChecker(getActivity());
        uploader.setServerListener(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "Camera opened!");

        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.CAMERA
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
            }
        }).check();

        ctx = this.getActivity();
        act = this.getActivity();

        preview = new Preview(this.getActivity(), view.findViewById(R.id.surfaceView), uploader);
        preview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((FrameLayout) view.findViewById(R.id.surfaceView).getParent()).addView(preview);
        preview.setKeepScreenOn(true);

        studentNameTextView = view.findViewById(R.id.textview_student_name_camera_result);
        plateNumberTextView = view.findViewById(R.id.textview_plate_number_camera_result);

        recyclerView = view.findViewById(R.id.pass_failed_result_recyclerview);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PlateFailedAdapter(getActivity());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        // Cam stop from here
        super.onDetach();
    }


    @Override
    @UiThread
    public void response(DatabaseResponse response) {
        Log.d(TAG, "Response type: " + response.getType());
        switch (response.getType()) {
            case OK:
                getActivity().runOnUiThread(() -> {
                    plateNumberTextView.setText("Plate #: " + response.getPlateNumber().toUpperCase());
                    studentNameTextView.setText("Student: " + response.getStudent().name);
                });
                break;
            case PLATEONLY:
                adapter.addResponse(response);
                getActivity().runOnUiThread(adapter::notifyDataSetChanged);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if(numCams > 0){
            try{
                camera = Camera.open(0);
                //camera.setPreviewCallbackWithBuffer(uploader);
                camera.startPreview();
                preview.setCamera(camera);
            } catch (RuntimeException ex){
                Toast.makeText(ctx, "Camera not found", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause() {
        if(camera != null) {
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
        }
        super.onPause();
    }

    private void resetCam() {
        camera.startPreview();
        preview.setCamera(camera);
    }

}
