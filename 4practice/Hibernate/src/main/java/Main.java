import entity.Brand;
import entity.Watch;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== МЕНЮ HIBERNATE ЧАСЫ ===");
            System.out.println("1. Добавить новый БРЕНД (производителя)");
            System.out.println("2. Добавить ЧАСЫ (к существующему бренду)");
            System.out.println("3. Показать все данные");
            System.out.println("0. Выход");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Очистка буфера после числа

            switch (choice) {
                case 1 -> addBrand();
                case 2 -> addWatch();
                case 3 -> showAll();
                case 0 -> {
                    System.out.println("Выход...");
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    // --- 1. Добавление Бренда ---
    private static void addBrand() {
        System.out.print("Введите название бренда: ");
        String name = scanner.nextLine();
        System.out.print("Введите страну: ");
        String country = scanner.nextLine();

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Brand brand = new Brand(name, country);
            session.persist(brand); // Сохраняем объект в БД

            transaction.commit();
            System.out.println("✅ Бренд успешно добавлен!");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    // --- 2. Добавление Часов (Связывание таблиц) ---
    private static void addWatch() {
        // Сначала покажем бренды, чтобы пользователь знал ID
        showBrandsSimple();

        System.out.print("Введите ID бренда, к которому относятся часы: ");
        int brandId = scanner.nextInt();
        scanner.nextLine();

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // ВАЖНО: Сначала ищем бренд в базе по ID
            Brand brand = session.get(Brand.class, brandId);

            if (brand == null) {
                System.out.println("❌ Бренд с таким ID не найден!");
                return;
            }

            // Если бренд найден, спрашиваем данные о часах
            System.out.print("Введите модель: ");
            String model = scanner.nextLine();
            System.out.print("Введите материал: ");
            String material = scanner.nextLine();
            System.out.print("Введите цену: ");
            double price = scanner.nextDouble();

            // Создаем часы
            Watch watch = new Watch(model, price, material);

            // СВЯЗЫВАЕМ ОБЪЕКТЫ:
            // 1. Добавляем часы в список бренда
            brand.addWatch(watch);
            // 2. Благодаря CascadeType.ALL в классе Brand,
            // нам достаточно обновить (merge) бренд или сохранить часы.
            // Самый надежный способ здесь — просто сохранить часы (Hibernate сам увидит связь).
            session.persist(watch);

            transaction.commit();
            System.out.println("✅ Часы добавлены к бренду " + brand.getName());

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    // --- 3. Вывод всего списка ---
    private static void showAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Brand> brands = session.createQuery("from Brand", Brand.class).list();

            System.out.println("\n=== ПОЛНЫЙ СПИСОК ===");
            if (brands.isEmpty()) {
                System.out.println("База пуста.");
                return;
            }

            for (Brand b : brands) {
                System.out.println("🏢 Производитель: " + b.getName() + " (" + b.getId() + ")");

                List<Watch> watches = b.getWatches();
                if (watches.isEmpty()) {
                    System.out.println("    (нет моделей)");
                } else {
                    for (Watch w : watches) {
                        System.out.printf("    ⌚ Модель: %-10s | Материал: %-10s | Цена: %.2f\n",
                                w.getModel(), w.getModel(), w.getPrice());
                    }
                }
                System.out.println("-------------------------");
            }
        }
    }

    // Вспомогательный метод, чтобы просто показать список брендов для выбора ID
    private static void showBrandsSimple() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Brand> brands = session.createQuery("from Brand", Brand.class).list();
            System.out.println("--- Доступные бренды ---");
            for (Brand b : brands) {
                System.out.println("ID: " + b.getId() + " | " + b.getName());
            }
            System.out.println("------------------------");
        }
    }
}