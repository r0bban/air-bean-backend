package io.r0bban.airbean.REST;

import io.r0bban.airbean.exception.ResourceNotFoundException;
import io.r0bban.airbean.integration.OrderRepository;
import io.r0bban.airbean.integration.ProductRepository;
import io.r0bban.airbean.integration.UserRepository;
import io.r0bban.airbean.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable(value = "id") Long orderId)
            throws ResourceNotFoundException {
        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Order not found on :: " + orderId));
        return ResponseEntity.ok().body(order);
    }

    @PostMapping("/orders")
    public Order createOrder(@Valid @RequestBody CreateOrderBody createOrderBody) throws ResourceNotFoundException {

        Order newOrder = new Order();
        newOrder.setTotalOrderPrice(0.0);
        User user;// = new User();

        for (OrderedProduct orderedProduct :createOrderBody.getOrderedProducts())
        {
            Product productSource =
                    productRepository
                            .findById(orderedProduct.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found on :: " + orderedProduct.getProductId()));

            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setProductId(productSource.getId());
            newOrderItem.setTitle(productSource.getTitle());
            newOrderItem.setUnitPrice(productSource.getPrice());
            newOrderItem.setQuantity(orderedProduct.getQuantity());
            newOrderItem.setTotalItemPrice(productSource.getPrice()*orderedProduct.getQuantity());
            newOrder.addOrderItem(newOrderItem);
            newOrder.setTotalOrderItemQuantity(newOrder.getTotalOrderItemQuantity()+orderedProduct.getQuantity());
            newOrder.setTotalOrderPrice(newOrder.getTotalOrderPrice()+newOrderItem.getTotalItemPrice());
        }
        long deliveryTime = 30+(newOrder.getTotalOrderItemQuantity()*7);
        newOrder.setEta(LocalDateTime.now().plusMinutes(deliveryTime));

        if (createOrderBody.getIsUserOrder()) {
            user =
                    userRepository
                            .findById(createOrderBody.getUserId())
                            .orElseThrow(() -> new ResourceNotFoundException("User not found on :: " + createOrderBody.getUserId()));
            user.addOrder(newOrder);
            newOrder.setUser(user);
        }


        return orderRepository.save(newOrder);
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<Order> updateProduct(
            @PathVariable(value = "id") Long orderId, @RequestParam("user") Long userId)
            throws ResourceNotFoundException {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found on :: " + userId));
        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Order not found on :: " + orderId));
        order.setUser(user);
//        order.setUpdatedAt(new Date());
        final Order updatedOrder = orderRepository.save(order);
        return ResponseEntity.ok(updatedOrder);
    }

//    @DeleteMapping("/product/{id}")
//    public Map<String, Boolean> deleteProduct(@PathVariable(value = "id") Long productId) throws Exception {
//        Product product =
//                productRepository
//                        .findById(productId)
//                        .orElseThrow(() -> new ResourceNotFoundException("Product not found on :: " + productId));
//        productRepository.delete(product);
//        Map<String, Boolean> response = new HashMap<>();
//        response.put("deleted", Boolean.TRUE);
//        return response;
//    }

}


