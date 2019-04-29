package tk.leoforney.passchecker;

import android.util.Base64;

import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leo on 7/27/2018.
 */
public class Student extends Person implements Parent<Car> {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public PassType getPassType() {
        if (passType == null) {
            String decoded = new String(Base64.decode("eyJ0eXBlIjoiRlVMTFlFQVIifQ==", Base64.DEFAULT));
            this.passType = new Gson().fromJson(decoded, PassType.class);
        }
        return passType;
    }

    public void setPassType(PassType passType) {
        this.passType = passType;
    }

    public int id;
    public List<Car> cars;
    public PassType passType;

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
