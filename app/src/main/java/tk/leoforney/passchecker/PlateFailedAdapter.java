package tk.leoforney.passchecker;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class PlateFailedAdapter extends RecyclerView.Adapter<PlateFailedAdapter.PlateFailedViewHolder> {

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
        for (DatabaseResponse iteratedResponse: responseList) {
            boolean similar = ImageChecker.similarityOfStrings(iteratedResponse.getPlateNumber(), response.getPlateNumber());
            if(iteratedResponse.getPlateNumber().toLowerCase().replace(" ", "").equals(response.getPlateNumber().toLowerCase().replace(" ", ""))) {
                if (similar) add = false;
            }
        }
        if (add) responseList.add(0, response);
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

        holder.plateNumber.setText(response.getPlateNumber());
        Log.d("PlateFailedHolder", "Plate binded: " + response.getPlateNumber());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return responseList.size();
    }
}