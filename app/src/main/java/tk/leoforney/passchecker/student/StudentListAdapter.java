package tk.leoforney.passchecker.student;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import tk.leoforney.passchecker.Car;
import tk.leoforney.passchecker.R;
import tk.leoforney.passchecker.Student;

public class StudentListAdapter extends ExpandableRecyclerAdapter<Student, Car, StudentViewHolder, CarViewHolder> {

    /**
     * Primary constructor. Sets up {@link #mParentList} and {@link #mFlatItemList}.
     * <p>
     * Any changes to {@link #mParentList} should be made on the original instance, and notified via
     * {@link #notifyParentInserted(int)}
     * {@link #notifyParentRemoved(int)}
     * {@link #notifyParentChanged(int)}
     * {@link #notifyParentRangeInserted(int, int)}
     * {@link #notifyChildInserted(int, int)}
     * {@link #notifyChildRemoved(int, int)}
     * {@link #notifyChildChanged(int, int)}
     * methods and not the notify methods of RecyclerView.Adapter.
     *
     * @param parentList List of all parents to be displayed in the RecyclerView that this
     *                   adapter is linked to
     */

    List<Student> students;

    public StudentListAdapter(@NonNull List<Student> parentList) {
        super(parentList);
        students = parentList;
        Log.d("StudentListAdapter", "There are " + parentList.size() + " students");
    }

    @UiThread
    @NonNull
    @Override
    public StudentViewHolder onCreateParentViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_detail, parent, false);
        return new StudentViewHolder(view);
    }

    @UiThread
    @NonNull
    @Override
    public CarViewHolder onCreateChildViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car_detail, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(@NonNull StudentViewHolder parentViewHolder, int parentPosition, @NonNull Student parent) {
        parentViewHolder.bind(parent);
    }

    @Override
    public void onBindChildViewHolder(@NonNull CarViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull Car child) {
        childViewHolder.bind(child);
    }
}
