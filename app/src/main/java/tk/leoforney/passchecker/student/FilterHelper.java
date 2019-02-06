package tk.leoforney.passchecker.student;

import android.util.Log;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import tk.leoforney.passchecker.Car;
import tk.leoforney.passchecker.Student;

public class FilterHelper extends Filter {
    static List<Student> currentList;
    static StudentListAdapter adapter;

    public static FilterHelper newInstance(List<Student> currentList, StudentListAdapter adapter) {
        FilterHelper.adapter = adapter;
        FilterHelper.currentList = currentList;
        return new FilterHelper();
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults = new FilterResults();

        if (constraint != null && constraint.length() > 0) {
            //Change to lower
            constraint = constraint.toString().toLowerCase();

            //Hold filters we find
            List<Student> foundFilters = new ArrayList<>();

            //ITERATE CURRENT LIST
            for (Student iteratedStudent : currentList) {
                if (iteratedStudent.name.toLowerCase().replace(" ", "").contains(constraint)) {
                    foundFilters.add(iteratedStudent);
                    Log.d("FilterHelper", iteratedStudent.name + " has " + constraint);
                }
                if (String.valueOf(iteratedStudent.id).toLowerCase().replace(" ", "").contains(constraint)) {
                    foundFilters.add(iteratedStudent);
                }
                for (Car car: iteratedStudent.cars) {
                    if (car.plateNumber.toLowerCase().replace(" ", "").contains(((String) constraint).replace(" ", "")));
                }
            }

            for (Student student: foundFilters) {
                Log.d("FilterHelper", student.name + " : " + student.id);
            }

            //SET RESULTS TO FILTER LIST
            filterResults.count = foundFilters.size();
            filterResults.values = foundFilters;
        } else {
            //NO ITEM FOUND.LIST REMAINS INTACT
            filterResults.count = currentList.size();
            filterResults.values = currentList;
        }

        //RETURN RESULTS
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

        adapter.setStudents((List<Student>) filterResults.values);
        adapter.notifyDataSetChanged();
    }
}