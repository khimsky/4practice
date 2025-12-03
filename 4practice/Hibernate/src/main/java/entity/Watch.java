package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "watches")
public class Watch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "model")
    private String model;

    @Column(name = "price")
    private double price;

    @Column(name = "material")
    private String material;

    // Связь: Много часов -> Один бренд (Many-to-One)
    @ManyToOne
    @JoinColumn(name = "brand_id") // Создаст колонку brand_id в таблице watches
    private Brand brand;

    public Watch() {}

    public Watch(String model, double price, String material) {
        this.model = model;
        this.price = price;
        this.material = material;
    }

    // Геттеры и сеттеры
    public String getModel() { return model; }
    public double getPrice() { return price; }
    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }

    @Override
    public String toString() {
        return "  - Модель: " + model + ", Материал: " + material + ", Цена: " + price;
    }
}