package entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "country")
    private String country;

    // Связь: Один бренд -> Много часов (One-to-Many)
    // cascade = CascadeType.ALL означает, что при сохранении Бренда сохранятся и его часы
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Watch> watches = new ArrayList<>();

    public Brand() {} // Обязательный пустой конструктор

    public Brand(String name, String country) {
        this.name = name;
        this.country = country;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public List<Watch> getWatches() { return watches; }

    // Метод для удобного добавления часов
    public void addWatch(Watch watch) {
        watches.add(watch);
        watch.setBrand(this);
    }

    @Override
    public String toString() {
        return "Бренд: " + name + " (" + country + ")";
    }
}