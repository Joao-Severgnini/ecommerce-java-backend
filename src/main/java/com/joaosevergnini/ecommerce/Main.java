package com.joaosevergnini.ecommerce;

import com.joaosevergnini.ecommerce.application.service.CustomerService;
import com.joaosevergnini.ecommerce.application.service.OrderService;
import com.joaosevergnini.ecommerce.application.service.ProductService;
import com.joaosevergnini.ecommerce.domain.model.Customer;
import com.joaosevergnini.ecommerce.domain.model.Order;
import com.joaosevergnini.ecommerce.domain.model.OrderItem;
import com.joaosevergnini.ecommerce.domain.model.Product;
import com.joaosevergnini.ecommerce.infrastructure.pesistence.DatabaseInitializer;
import com.joaosevergnini.ecommerce.infrastructure.pesistence.repository.CustomerRepository;
import com.joaosevergnini.ecommerce.infrastructure.pesistence.repository.OrderItemRepository;
import com.joaosevergnini.ecommerce.infrastructure.pesistence.repository.OrderRepository;
import com.joaosevergnini.ecommerce.infrastructure.pesistence.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {

        // Repositories
        var customerRepository = new CustomerRepository();
        var productRepository = new ProductRepository();
        var orderItemRepository = new OrderItemRepository();
        var orderRepository = new OrderRepository();

        // Services
        var customerService = new CustomerService(customerRepository);
        var productService = new ProductService(productRepository);
        var orderService = new OrderService(
                orderRepository,
                orderItemRepository,
                productRepository,
                customerRepository
        );

        System.out.println("=== ECOMMERCE APPLICATION STARTED ===");

        DatabaseInitializer.init();

        // 1- Create a new customer
        var customer = new Customer("John Doe", "johnDoe@gmail.com");
        customer = customerService.createCustomer(customer);
        System.out.println("Created Customer: " + customer.getName());

        // 2- Create products
        var mouse = new Product("Mouse", BigDecimal.valueOf(100), 50);
        var keyboard = new Product("Keyboard", BigDecimal.valueOf(200), 5);

        mouse = productService.createProduct(mouse);
        keyboard = productService.createProduct(keyboard);

        System.out.println("Products created: ");
        System.out.println(mouse);
        System.out.println(keyboard);

        // 3- Create order
        List<OrderItem> items = List.of(
                new OrderItem(mouse.getId(), mouse.getPrice(), 2),
                new OrderItem(keyboard.getId(), keyboard.getPrice(), 1)
        );

        Order order = orderService.createOrder(customer.getId(), items);

        // 4- Find order
        Optional<Order> findOrder = orderService.findOrderById(order.getId());

        if (findOrder.isPresent()) {
            System.out.println("Order found, id: " + findOrder.get().getId());
        } else {
            System.out.println("Order not found");
        }

        // 5- Pay order
        orderService.payOrder(order);
        System.out.println("Order paid: " + order.getId());
        System.out.println("Order status after payment: " + order.getStatus());

        // 6- Deliver order
        // Ship the order before delivering
        orderService.shipOrder(order);
        System.out.println("Order shipped: " + order.getId());
        System.out.println("Order status after shipping: " + order.getStatus());

        orderService.deliverOrder(order);
        System.out.println("Order delivered: " + order);
        System.out.println("Order status after delivery: " + order.getStatus());

        // 7- Error case: Try to cancel a delivered order
        try {
            orderService.cancelOrder(order.getId());
        } catch (Exception e) {
            System.out.println("Error canceling order: " + e.getMessage());
        }

        System.out.println("=== ECOMMERCE APPLICATION FINISHED ===");

    }
}
