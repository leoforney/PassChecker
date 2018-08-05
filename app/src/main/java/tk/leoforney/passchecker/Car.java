package tk.leoforney.passchecker;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

/**
 * Created by Leo on 7/27/2018.
 */
public class Car {
    public String plateNumber, make, model, color;
    public int year, id;

    private Gson gson;

    public Car() {
        gson = new Gson();
    }

    public Car(String plateNumber, String color, String make, String model, int year, int id) {
        this.plateNumber = plateNumber;
        this.color = color;
        this.make = make;
        this.model = model;
        this.year = year;
        this.id = id;
        gson = new Gson();
    }

    @Override
    public String toString() {
        return plateNumber + " : " + make + " : " + model + " : " + color + " : " + year + " : " + id;
    }
}
