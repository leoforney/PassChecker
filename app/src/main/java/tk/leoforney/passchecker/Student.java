package tk.leoforney.passchecker;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leo on 7/27/2018.
 */
public class Student extends ExpandableGroup<Car> {
    public String name;
    public int id;
    public List<Car> cars;

    public Student() {
        super("", null);
        if (cars == null) cars = new ArrayList<>();
    }

    public Student(String name, int id) {
        super(name + " - " + id, this.cars);
        this.name = name;
        this.id = id;
        if (cars == null) cars = new ArrayList<>();

    }

    public Student(String title, List<Car> items) {
        super(title, items);
    }

    @Override
    public String toString() {
        return name + " : " + id;
    }
}
