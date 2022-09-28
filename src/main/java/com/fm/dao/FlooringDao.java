package com.fm.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.fm.dto.Order;
import com.fm.dto.Product;

public interface FlooringDao {


    /**
     * Returns a list of all orders by a specific date
     * @param date
     * @return
     */
    public List<Order> getOrdersByDate(LocalDate date);

    /**
     * Returns a list of all orders
     * @return
     */
    public List<Order> getAllOrders();


    /**
     * Reads all order files into ordersMap
     */
    public void importOrderData() throws FlooringDaoException;
    
    /**
     * Reads all product data from file
     */
    public void importProductData() throws FlooringDaoException;
    
    /**
     * Reads all tax data from file
     */
    public void importTaxData() throws FlooringDaoException;
    
    /**
     * Creates a new order object using parameters and matching product/tax data from product/tax maps
     * @param date
     * @param customerName
     * @param state
     * @param productType
     * @param area
     * @return
     */
    public Order createOrder(String date, String customerName, String state, String productType, BigDecimal area);
    
    /**
     * Adds order object
     * @param newOrder
     * @return
     */
    public int addOrder(Order newOrder);


    public boolean checkTaxCode(String state);

    /**
     * Returns a list of all products
     * @return
     */
    public List<Product> getProducts();

    /**
     * Checks if Product exists
     * @param productType
     * @return
     */
    public boolean checkProductType(String productType);

    /**
     * Returns an order object that contains the given date and name
     * @param date
     * @param customerName
     * @return
     */
    public Order getOrderByNameDate(String date, String customerName);

    /**
     * Replaces an order object
     * @param newOrder
     * @throws FlooringDaoException
     */
    public void updateOrder(Order newOrder) throws FlooringDaoException;

    /**
     * re-evaluates based on productType and state
     * @param order
     */
    public void recalculateOrder(Order order);

    /**
     * Returns an order matching the passed in orderNumber and date
     * @param orderNumber
     * @param date
     * @return
     */
    public Order getOrderByOrderNumberDate(String orderNumber, String date);

    /**
     * Removes the order matching the order object
     * @param orderToRemove
     */
    public void removeOrder(Order orderToRemove);

    /**
     * Exports order data into individual files named based on date
     * @throws FlooringDaoException
     */
    public void exportOrderData() throws FlooringDaoException;

    /**
     * Exports all order data into a text file
     * @throws FlooringDaoException
     */
    public void exportBackupOrderData()  throws FlooringDaoException;

}
