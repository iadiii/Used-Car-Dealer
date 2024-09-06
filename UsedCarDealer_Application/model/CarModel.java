package model;

public class CarModel {

    private int carId;
    private String make;
    private String model;
    private int year;
    private double price;
    private String color;
    private int quantity;  // New attribute for tracking inventory

    // Constructor
    public CarModel(int carId, String make, String model, int year, double price, String color, int quantity) {
        this.carId = carId;
        this.make = make;
        this.model = model;
        this.year = year;
        this.price = price;
        this.color = color;
        this.quantity = quantity;  // Initialize quantity in constructor
    }

    // Getters and setters
    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
