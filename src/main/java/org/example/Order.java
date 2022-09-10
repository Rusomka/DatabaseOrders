package org.example;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date date;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "clients_id")
    private Client clients;
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "Carts_Products", joinColumns = @JoinColumn(name = "orders_id"),
            inverseJoinColumns = @JoinColumn(name = "products_id"))
    private Set<Product> products = new HashSet<>();

    public Order() {
    }

    public Order(Date date) {
        this.date = date;
    }

    public void addProducts(Client client, Product product) {
        products.add(product);
        client.getOrders().add(this);
        this.setClients(client);
        product.getOrders().add(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Client getClients() {
        return clients;
    }

    public void setClients(Client clients) {
        this.clients = clients;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", date=" + date +
                ", clients_id=" + clients.getId() +
                '}';
    }
}
