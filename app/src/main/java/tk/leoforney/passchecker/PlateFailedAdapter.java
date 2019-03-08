package tk.leoforney.passchecker;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class PlateFailedAdapter extends RecyclerView.Adapter<PlateFailedAdapter.PlateFailedViewHolder> {

    final static String TAG = "PlateFailedAdapter";
    private List<DatabaseResponse> responseList;
    private Activity parent;

    public static class PlateFailedViewHolder extends RecyclerView.ViewHolder {

        public Button viewImage;
        public ImageButton clearButton;
        public TextView plateNumber;
        public DatabaseResponse response;

        public PlateFailedViewHolder(View v) {
            super(v);
            viewImage = v.findViewById(R.id.button_view_image_result);
            plateNumber = v.findViewById(R.id.textview_plate_number_result);
            clearButton = v.findViewById(R.id.button_car_checked_result);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlateFailedAdapter(Activity activity, List<DatabaseResponse> responseList) {
        this.parent = activity;
        this.responseList = responseList;
    }

    public PlateFailedAdapter(Activity activity) {
        this.parent = activity;
        this.responseList = new ArrayList<>();
    }

    public void addResponse(DatabaseResponse response) {
        boolean add = true;
        for (DatabaseResponse iteratedResponse : responseList) {
            boolean similar = ImageChecker.similarityOfStrings(iteratedResponse.getPlateNumber(), response.getPlateNumber());
            if (iteratedResponse
                    .getPlateNumber()
                    .toLowerCase()
                    .replace(" ", "")
                    .equals(response
                            .getPlateNumber()
                            .toLowerCase()
                            .replace(" ", "")) || similar) {
                add = false;
            }
        }
        if (add) {
            responseList.add(0, response);
            MediaPlayer mPlayerFailed = MediaPlayer.create(parent, R.raw.failed);
            mPlayerFailed.setOnCompletionListener(MediaPlayer::release);
            mPlayerFailed.start();
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PlateFailedAdapter.PlateFailedViewHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.camera_student_failed_result, parent, false);
        PlateFailedViewHolder vh = new PlateFailedViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(PlateFailedViewHolder holder, int position) {

        DatabaseResponse response = responseList.get(position);

        holder.response = response;

        holder.viewImage.setOnClickListener(view -> {
            String url = "http://" + CredentialsManager.getInstance(parent).getIP() +
                    "/submissions/" + holder.response.getTimestamp() + ".jpg";
            Log.d(TAG, "Fetching image from: " + url);
            ImageView imageView = new ImageView(parent);
            Glide.with(parent).load(url)
                    .dontAnimate()
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            Log.d(TAG, "Image Ready");
                            if (Looper.myLooper() == null)
                                Looper.prepare();
                            parent.runOnUiThread(() -> showImage(resource, parent));
                        }
                    });
        });

        holder.clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_car_checked_result:
                        Log.d(getClass().getName(), "Remove button clicked");
                        parent.runOnUiThread(() -> {
                            responseList.remove(holder.response);
                            notifyDataSetChanged();
                        });

                        break;
                }
            }
        });

        holder.plateNumber.setText(response.getPlateNumber().toUpperCase());
        Log.d("PlateFailedHolder", "Plate binded: " + response.getPlateNumber());

    }

    public void showImage(Drawable drawable, Activity activity) {
        Dialog builder = new Dialog(activity);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView imageView = new ImageView(activity);
        imageView.setPadding(10, 10, 10, 10);
        imageView.setImageDrawable(drawable);
        Log.d(TAG, (imageView.getDrawable() == null) ? "Image not present" : "Image visible");
        builder.setOnDismissListener(dialogInterface -> imageView.setVisibility(View.GONE));
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        builder.show();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return responseList.size();
    }
}