package tk.leoforney.passchecker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jiangdg.usbcamera.UVCCameraHelper;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.Size;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.widget.CameraViewInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class USBCameraActivity extends AppCompatActivity implements ServerListener, CameraDialog.CameraDialogParent, CameraViewInterface.Callback {
    private static final String TAG = "USBCameraActivity";
    @BindView(R.id.camera_view)
    public View mTextureView;
    @BindView(R.id.seekbar_brightness)
    public SeekBar mSeekBrightness;
    @BindView(R.id.seekbar_contrast)
    public SeekBar mSeekContrast;

    private UVCCameraHelper mCameraHelper;
    private CameraViewInterface mUVCCameraView;
    private ImageChecker uploader;
    private AlertDialog mDialog;

    private boolean isRequest;
    private boolean isPreview;

    /* Imported from CameraFragment, adapted for USBCameraActivity */
    private TextureView textureView;
    TextView plateNumberTextView, studentNameTextView, cameraStatusTextView;
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PlateFailedAdapter adapter;
    private ServerProperties serverProperties;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usbcamera);
        ButterKnife.bind(this);

        studentNameTextView = findViewById(R.id.usb_textview_student_name_camera_result);
        plateNumberTextView = findViewById(R.id.usb_textview_plate_number_camera_result);
        cameraStatusTextView = findViewById(R.id.usb_camera_status);

        recyclerView = findViewById(R.id.usb_pass_failed_result_recyclerview);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PlateFailedAdapter(this);
        recyclerView.setAdapter(adapter);
        initView();

        uploader = new ImageChecker(this);
        uploader.setServerListener(this);

        // step.1 initialize UVCCameraHelper
        mUVCCameraView = (CameraViewInterface) mTextureView;
        mUVCCameraView.setCallback(this);
        mCameraHelper = UVCCameraHelper.getInstance();
        mCameraHelper.setDefaultFrameFormat(UVCCameraHelper.FRAME_FORMAT_YUYV);
        mCameraHelper.initUSBMonitor(this, mUVCCameraView, listener);

        //mCameraHelper.setOnPreviewFrameListener(nv21Yuv -> Log.d(TAG, "Byte data received"));
        uploader.height = mCameraHelper.getPreviewHeight();
        uploader.width = mCameraHelper.getPreviewWidth();
        mCameraHelper.setOnPreviewFrameListener(uploader);
    }

    private void initView() {

        mSeekBrightness.setMax(100);
        mSeekBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mCameraHelper != null && mCameraHelper.isCameraOpened()) {
                    mCameraHelper.setModelValue(UVCCameraHelper.MODE_BRIGHTNESS, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekContrast.setMax(100);
        mSeekContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mCameraHelper != null && mCameraHelper.isCameraOpened()) {
                    mCameraHelper.setModelValue(UVCCameraHelper.MODE_CONTRAST, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private UVCCameraHelper.OnMyDevConnectListener listener = new UVCCameraHelper.OnMyDevConnectListener() {

        @Override
        public void onAttachDev(UsbDevice device) {
            if (mCameraHelper == null || mCameraHelper.getUsbDeviceCount() == 0) {
                showShortMsg("No USB Camera");
                return;
            }
            // request open permission
            if (!isRequest) {
                isRequest = true;
                if (mCameraHelper != null) {
                    mCameraHelper.requestPermission(0);
                }
            }
        }

        @Override
        public void onDettachDev(UsbDevice device) {
            // close camera
            if (isRequest) {
                isRequest = false;
                mCameraHelper.closeCamera();
                showShortMsg(device.getDeviceName() + " is out");
            }
        }

        @Override
        public void onConnectDev(UsbDevice device, boolean isConnected) {
            if (!isConnected) {
                showShortMsg("Failed to connect, check params");
                isPreview = false;
            } else {
                isPreview = true;
                showShortMsg("Connecting to camera");
                // initialize seekbar
                // need to wait UVCCamera initialize over
                new Thread(() -> {
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Looper.prepare();
                    if (mCameraHelper != null && mCameraHelper.isCameraOpened()) {
                        mSeekBrightness.setProgress(mCameraHelper.getModelValue(UVCCameraHelper.MODE_BRIGHTNESS));
                        mSeekContrast.setProgress(mCameraHelper.getModelValue(UVCCameraHelper.MODE_CONTRAST));
                    }
                    Looper.loop();
                }).start();
            }
        }

        @Override
        public void onDisConnectDev(UsbDevice device) {
            showShortMsg("Disconnecting");
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        // step.2 register USB event broadcast
        if (mCameraHelper != null) {
            mCameraHelper.registerUSB();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // step.3 unregister USB event broadcast
        if (mCameraHelper != null) {
            mCameraHelper.unregisterUSB();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_usb_camera_toobar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_resolution:
                if (mCameraHelper == null || !mCameraHelper.isCameraOpened()) {
                    showShortMsg("Camera Open Failed");
                    return super.onOptionsItemSelected(item);
                }
                showResolutionListDialog();
                break;
            case R.id.menu_focus:
                if (mCameraHelper == null || !mCameraHelper.isCameraOpened()) {
                    showShortMsg("Camera open failed");
                    return super.onOptionsItemSelected(item);
                }
                mCameraHelper.startCameraFoucs();
                break;
            case R.id.menu_return:
                startActivity(new Intent(USBCameraActivity.this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showResolutionListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(USBCameraActivity.this);
        View rootView = LayoutInflater.from(USBCameraActivity.this).inflate(R.layout.layout_dialog_list, null);
        ListView listView = rootView.findViewById(R.id.listview_dialog);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(USBCameraActivity.this, android.R.layout.simple_list_item_1, getResolutionList());
        if (adapter != null) {
            listView.setAdapter(adapter);
        }
        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            if (mCameraHelper == null || !mCameraHelper.isCameraOpened())
                return;
            final String resolution = (String) adapterView.getItemAtPosition(position);
            String[] tmp = resolution.split("x");
            if (tmp != null && tmp.length >= 2) {
                int widht = Integer.valueOf(tmp[0]);
                int height = Integer.valueOf(tmp[1]);
                mCameraHelper.updateResolution(widht, height);
            }
            mDialog.dismiss();
        });

        builder.setView(rootView);
        mDialog = builder.create();
        mDialog.show();
    }

    // example: {640x480,320x240,etc}
    private List<String> getResolutionList() {
        List<Size> list = mCameraHelper.getSupportedPreviewSizes();
        List<String> resolutions = null;
        if (list != null && list.size() != 0) {
            resolutions = new ArrayList<>();
            for (Size size : list) {
                if (size != null) {
                    resolutions.add(size.width + "x" + size.height);
                }
            }
        }
        return resolutions;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // step.4 release uvc camera resources
        if (mCameraHelper != null) {
            mCameraHelper.getUSBMonitor().unregister();
            mCameraHelper.getUSBMonitor().destroy();
            mCameraHelper.release();
        }
    }

    private void showShortMsg(String msg) {
        runOnUiThread(() -> {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            cameraStatusTextView.setText(msg);
        });
    }

    @Override
    public USBMonitor getUSBMonitor() {
        return mCameraHelper.getUSBMonitor();
    }

    @Override
    public void onDialogResult(boolean canceled) {
        if (canceled) {
            showShortMsg("Cancel operation");
        }
    }

    public boolean isCameraOpened() {
        return mCameraHelper.isCameraOpened();
    }

    @Override
    public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
        if (!isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.startPreview(mUVCCameraView);
            isPreview = true;
        }
    }

    boolean cameraGood = false;

    @Override
    public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {
    }

    @Override
    public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
        if (isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.stopPreview();
            isPreview = false;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    @UiThread
    public void response(DatabaseResponse response) {
        Log.d(TAG, "Response type: " + response.getType());
        if (!cameraGood) {
            runOnUiThread(() -> {
                cameraStatusTextView.setText("Camera & Server OK  ✔");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cameraStatusTextView.setTextColor(getResources().getColor(R.color.cameraOkay, getTheme()));
                } else {
                    cameraStatusTextView.setTextColor(getResources().getColor(R.color.cameraOkay));
                }
            });
            cameraGood = true;
        }
        switch (response.getType()) {
            case OK:
                if (!plateNumberTextView.getText().toString().equals("Plate #: " + response.getPlateNumber().toUpperCase())) {
                    runOnUiThread(() -> {
                        RelativeLayout parent = (RelativeLayout) plateNumberTextView.getParent();
                        parent.setBackgroundColor(getResources().getColor(R.color.panelGreen));
                        plateNumberTextView.setText("Plate #: " + response.getPlateNumber().toUpperCase());
                        studentNameTextView.setText("Student: " + response.getStudent().name);
                    });
                    MediaPlayer mPlayerChecked = MediaPlayer.create(this, R.raw.checked);
                    mPlayerChecked.setOnCompletionListener(MediaPlayer::release);
                    mPlayerChecked.start();
                }
                break;
            case PASSINVALID:
                if (!plateNumberTextView.getText().toString().equals("Plate #: " + response.getPlateNumber().toUpperCase())) {
                    runOnUiThread(() -> {
                        RelativeLayout parent = (RelativeLayout) plateNumberTextView.getParent();
                        parent.setBackgroundColor(getResources().getColor(R.color.cameraError));
                        plateNumberTextView.setText("Plate #: " + response.getPlateNumber().toUpperCase());
                        studentNameTextView.setText("Student: " + response.getStudent().name + "[INVALID]");
                    });
                    MediaPlayer mPlayerChecked = MediaPlayer.create(this, R.raw.invalid);
                    mPlayerChecked.setOnCompletionListener(MediaPlayer::release);
                    mPlayerChecked.start();
                }
                break;
            case PLATEONLY:
                boolean demo = ServerProperties.getInstance(this).getPropertyBool("demoMode", true);
                if (!demo) {
                    if (response.getPlateNumber().equals("")) {
                        adapter.addResponse(response);
                        runOnUiThread(adapter::notifyDataSetChanged);
                    }
                }
                break;
        }
    }

}
