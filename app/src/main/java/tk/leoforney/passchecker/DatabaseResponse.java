package tk.leoforney.passchecker;

public class DatabaseResponse {

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
        this.plateNumber = car.plateNumber;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    private Student student;
    private Car car;
    private Type type;
    private String plateNumber = "";

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    private String timestamp;

    public DatabaseResponse(Student student, Car car, Type type) {
        this.student = student;
        this.car = car;
        this.plateNumber = this.car.plateNumber;
        this.type = type;
    }

    public DatabaseResponse() {
        this.student = new Student();
        this.car = new Car();
        this.type = Type.ERROR;
    }

    public enum Type {
        OK,
        NOPLATES,
        PLATEONLY,
        STUDENTONLY,
        ERROR
    }
}
