package tk.leoforney.passchecker.student;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;

import androidx.annotation.NonNull;
import tk.leoforney.passchecker.Car;

public class CarViewHolder extends ChildViewHolder {

    private TextView carTextView;

    public CarViewHolder(View itemView) {
        super(itemView);
        carTextView = (TextView) itemView;
    }

    public void bind(@NonNull Car car) {
        if (carTextView != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(car.plateNumber).append("] ").append(car.color).append(" ").append(car.year).append(" ").append(car.make).append(" ").append(car.model);
            carTextView.setText(sb.toString());
        }
    }
}
