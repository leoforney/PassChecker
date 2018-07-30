package tk.leoforney.passchecker;

import android.content.Context;
import android.support.v4.view.LayoutInflaterCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import java.util.List;

public class PassRVAdapter extends ExpandableRecyclerAdapter<PassRVAdapter.ViewHolder, PassRVAdapter.StudentDetailViewHolder> {

    private final List<ParentObject> mValues;

    public PassRVAdapter(Context context, List<ParentObject> parentItemList) {
        super(context, parentItemList);
        mValues = parentItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_pass_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public ViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        return null;
    }

    @Override
    public StudentDetailViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void onBindParentViewHolder(ViewHolder viewHolder, int i, Object o) {
        Student student = (Student) o;
        viewHolder.studentName.setText(student.name);
        viewHolder.mItem = student;
    }

    @Override
    public void onBindChildViewHolder(StudentDetailViewHolder studentDetailViewHolder, int i, Object o) {
        Car car = (Car) o;
        studentDetailViewHolder.idTextview.setText(car.id);
        for (int j = 0; j <= 5; j++) {
            TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_car_detail, studentDetailViewHolder.carLayout);
            String text = "";
            switch (j) {
                case 1:

                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends ParentViewHolder {
        public View mView;
        public TextView studentName;
        public ImageButton dropDownButton;
        public Student mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            studentName = view.findViewById(R.id.student_name);
            dropDownButton = view.findViewById(R.id.parent_list_item_expand_arrow);
        }
    }

    public class StudentDetailViewHolder extends ChildViewHolder {

        TextView idTextview;
        LinearLayout carLayout;

        public StudentDetailViewHolder(View itemView) {
            super(itemView);

            idTextview = itemView.findViewById(R.id.student_id);
            carLayout = itemView.findViewById(R.id.student_cars_linear_layout);
        }
    }
}
