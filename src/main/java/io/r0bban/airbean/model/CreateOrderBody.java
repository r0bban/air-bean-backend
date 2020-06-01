package io.r0bban.airbean.model;

import java.util.List;

public class CreateOrderBody {

    private List <OrderedProduct> orderedProducts;
    private Boolean isUserOrder;
    private long userId;

    public List<OrderedProduct> getOrderedProducts() {
        return orderedProducts;
    }

    public void setOrderedProducts(List<OrderedProduct> orderedProducts) {
        this.orderedProducts = orderedProducts;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Boolean getIsUserOrder() {
        return isUserOrder;
    }

    public void setIsUserOrder(Boolean userOrder) {
        isUserOrder = userOrder;
    }
}