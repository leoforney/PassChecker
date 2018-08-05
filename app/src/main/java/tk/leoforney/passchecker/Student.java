package tk.leoforney.passchecker;

import android.os.Parcel;

import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leo on 7/27/2018.
 */
public class Student implements Parent<Car> {
    public String name;
    public int id;
    public List<Car> cars;

    public Student() {
        if (cars == null) cars = new ArrayList<>();
    }

    public Student(String name, int id, List<Car> items) {
        this.name = name;
        this.id = id;
        if (items != null) cars = items;
        if (cars == null) cars = new ArrayList<>();
    }


    @Override
    public String toString() {
        return name + " : " + id;
    }

    @Override
    public List<Car> getChildList() {
        return cars;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public Car getCarPosition(int index) {
        return cars.get(index);
    }
}
