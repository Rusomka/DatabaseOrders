package org.example;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class App {
    private static EntityManagerFactory emf;
    private static EntityManager em;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            emf = Persistence.createEntityManagerFactory("JPAConfig");
            em = emf.createEntityManager();
            try {
                while (true) {
                    System.out.println("1: add Client");
                    System.out.println("2: add Product");
                    System.out.println("3: add Orders");
                    System.out.println("4: view Orders");
                    System.out.println("5: delete Order");
                    System.out.println("6: delete Product");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addClient(sc);
                            break;
                        case "2":
                            addProduct(sc);
                            break;
                        case "3":
                            addOrder(sc);
                            break;
                        case "4":
                            viewOrders();
                            break;
                        case "5":
                            deleteOrder(sc);
                            break;
                        case "6":
                            deleteProduct(sc);
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                em.close();
                emf.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    public static void addClient(Scanner sc) {
        System.out.println("Enter client name");
        String name = sc.nextLine();
        System.out.println("Enter client address");
        String address = sc.nextLine();
        System.out.println("Enter client phone");
        String phoneStr = sc.nextLine();
        Long phone = Long.parseLong(phoneStr);
        Client client = new Client(name, address, phone);
        em.getTransaction().begin();
        try {
            em.persist(client);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }


    public static void addProduct(Scanner sc) {
        System.out.println("Enter product name");
        String name = sc.nextLine();
        System.out.println("Enter description product");
        String description = sc.nextLine();
        System.out.println("Enter price product");
        String priceStr = sc.nextLine();
        Long price = Long.parseLong(priceStr);
        Product product = new Product(name, description, price);
        em.getTransaction().begin();
        try {
            em.persist(product);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    public static void addOrder(Scanner sc) {
        System.out.println("Enter client id");
        String clientId = sc.nextLine();
        long id = Long.parseLong(clientId);

        Client client = em.getReference(Client.class, id);
        if (client == null) {
            System.out.println("No client");
            return;
        }
        System.out.println("select products for the customer");
        System.out.println("Enter product id or 0 to exit");
        String idPr;
        long idProduct;

        List<Product> products = new ArrayList<>();
        do {
            idPr = sc.nextLine();
            idProduct = Long.parseLong(idPr);
            if (idProduct != 0) {
                Product product = em.getReference(Product.class, idProduct);
                products.add(product);
            }
        } while (idProduct != 0);

        em.getTransaction().begin();
        Order order = new Order(new Date());
        for (Product p : products) {
            order.addProducts(client, p);
        }
        try {
            em.persist(order);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    public static void viewOrders() {
        TypedQuery<Order> ordersQuery = em.createQuery("SELECT x FROM Order x", Order.class);
        List<Order> orders = ordersQuery.getResultList();
        for (Order o : orders) {
            System.out.println(o.toString());
        }
    }

    public static void deleteOrder(Scanner sc) {
        System.out.println("Enter id to delete order");
        String idStr = sc.nextLine();
        Long id = Long.parseLong(idStr);
        Order order = em.getReference(Order.class, id);

        em.getTransaction().begin();
        try {
            em.remove(order);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    public static void deleteProduct(Scanner sc) {
        System.out.println("Enter the product id to uninstall");
        String idStr = sc.nextLine();
        long id = Long.parseLong(idStr);

        Product product = em.getReference(Product.class, id);

        em.getTransaction().begin();
        try {
            if (product.getOrders().size() != 0) {
                Order order = product.getOrders().get(0);
                order.getProducts().remove(product);
                em.merge(order);
            }
            em.remove(product);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }

    }
}
