package tk.leoforney.passchecker;

import android.os.Parcelable;

/**
 * Created by Leo on 7/27/2018.
 */
public class Car implements Parcelable {
    public String plateNumber, make, model, color;
    public int year, id;

    public Car() {

    }

    public Car(String plateNumber, String color, String make, String model, int year, int id) {
        this.plateNumber = plateNumber;
        this.color = color;
        this.make = make;
        this.model = model;
        this.year = year;
        this.id = id;
    }

    @Override
    public String toString() {
        return plateNumber + " : " + make + " : " + model + " : " + color + " : " + year + " : " + id;
    }
}
