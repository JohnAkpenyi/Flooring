
package com.fm.service;

import com.fm.dao.FlooringDao;
import com.fm.dao.FlooringDaoException;
import com.fm.dto.Order;
import com.fm.dto.Product;
import com.fm.dto.Tax;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FlooringDaoStubImpl implements FlooringDao {
    
    public Order order1, order2;
    private final HashMap<Integer, Order> ordersMap = new HashMap<>();
    private final HashMap<String, Product> productMap = new HashMap<>();
    private final HashMap<String, Tax> taxMap = new HashMap<>();
    
    public FlooringDaoStubImpl(){
        productMap.put("Carpet", new Product("Carpet", new BigDecimal("2.25"), new BigDecimal("2.10")));
        productMap.put("Tile", new Product("Tile", new BigDecimal("3.50"), new BigDecimal("4.15")));
        taxMap.put("TX", new Tax("TX", "Texas", new BigDecimal("4.45")));
        taxMap.put("WA", new Tax("WA", "Washington", new BigDecimal("9.25")));
        order1 = new Order("Test Name", "TX", "Carpet", LocalDate.parse("01-01-2022", DateTimeFormatter.ofPattern("MM-dd-yyyy")), new BigDecimal("250.00"), 
                taxMap.get("TX").getTaxRate(), productMap.get("Carpet").getCostPerSqFt(), productMap.get("Carpet").getLaborCostPerSqFt());
        order2 = new Order("Test NameTwo", "WA", "Tile", LocalDate.parse("02-02-2222", DateTimeFormatter.ofPattern("MM-dd-yyyy")), new BigDecimal("250.00"), 
                taxMap.get("WA").getTaxRate(), productMap.get("Tile").getCostPerSqFt(), productMap.get("Tile").getLaborCostPerSqFt());
        ordersMap.put(1, order1);
        ordersMap.put(2, order2);
    }
    
    @Override
    public List<Order> getAllOrders() {
        return new ArrayList<>(ordersMap.values());
    }

    @Override
    public List<Order> getOrdersByDate(LocalDate date) {
        return getAllOrders().stream().filter((order) -> order.getOrderDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public void importOrderData() throws FlooringDaoException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void importProductData() throws FlooringDaoException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void importTaxData() throws FlooringDaoException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Order createOrder(String date, String customerName, String state, String productType, BigDecimal area) {
        return new Order(customerName, state, productType, LocalDate.parse(date, DateTimeFormatter.ofPattern("MM-dd-yyyy")), area, 
                taxMap.get(state).getTaxRate(), productMap.get(productType).getCostPerSqFt(), productMap.get(productType).getLaborCostPerSqFt());
   
    }

    @Override
    public int addOrder(Order newOrder) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean checkTaxCode(String state) {
        return taxMap.containsKey(state);
    }

    @Override
    public List<Product> getProducts() {
        return new ArrayList<>(productMap.values());
    }

    @Override
    public boolean checkProductType(String productType) {
        return productMap.containsKey(productType);    
    }

    @Override
    public Order getOrderByNameDate(String date, String customerName) {
        List<Order> orderList = getAllOrders().stream().filter((ord) -> ord.getOrderDate().equals(LocalDate.parse(date, DateTimeFormatter.ofPattern("MM-dd-yyyy"))) 
                && ord.getCustomerName().equals(customerName)).collect(Collectors.toList());
        Order order = null;
        if(!orderList.isEmpty()){
            order = orderList.get(0);
        }
        return order;
    }

    @Override
    public void updateOrder(Order newOrder) throws FlooringDaoException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void recalculateOrder(Order order) {
        order.setCostPerSqFt(productMap.get(order.getProductType()).getCostPerSqFt());
        order.setLaborCostPerSqFt(productMap.get(order.getProductType()).getLaborCostPerSqFt());
        order.setTaxRate(taxMap.get(order.getState()).getTaxRate());
        order.recalculate();    
    }

    @Override
    public Order getOrderByOrderNumberDate(String orderNumber, String date) {
        if(ordersMap.containsKey(Integer.parseInt(orderNumber)) && ordersMap.get(Integer.parseInt(orderNumber)).getOrderDate().equals(LocalDate.parse(date, DateTimeFormatter.ofPattern("MM-dd-yyyy")))){
            return ordersMap.get(Integer.parseInt(orderNumber));
        }
        else{
            return null;
        }
    }

    @Override
    public void removeOrder(Order orderToRemove) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void exportOrderData() throws FlooringDaoException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void exportBackupOrderData() throws FlooringDaoException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
