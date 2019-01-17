package tk.leoforney.passchecker.student;

import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import androidx.annotation.NonNull;
import tk.leoforney.passchecker.R;
import tk.leoforney.passchecker.Student;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class StudentViewHolder extends ParentViewHolder {

    private ImageButton arrow;
    private TextView nameTv, idTv;

    public StudentViewHolder(@NonNull View itemView) {
        super(itemView);
        arrow = itemView.findViewById(R.id.drop_down_button);
        nameTv = itemView.findViewById(R.id.student_name);
        idTv = itemView.findViewById(R.id.student_id);
    }

    public void bind(@NonNull Student student) {
        if (nameTv != null && idTv != null) {
            nameTv.setText(student.name);
            idTv.setText(Integer.toString(student.id));
            Log.d("StudentViewHolder", "Student binded " + student.name);
        }
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        Log.d("StudentViewHolder", String.valueOf(expanded));
        super.onExpansionToggled(expanded);
        if (expanded) {
            animateCollapse();
        } else {
            animateExpand();
        }
    }
}
