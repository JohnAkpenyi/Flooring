package com.fm.service;

import com.fm.dao.FlooringDaoException;
import com.fm.dto.Order;
import com.fm.dto.Product;
import java.util.List;

public interface FlooringServiceLayer {

    /**
     * Validates all parameter inputs
     * and returns a newly created order object using the parameters
     * @param date
     * @param customerName
     * @param state
     * @param productType
     * @param area
     * @return
     * @throws DateDuplicationException
     * @throws InputValidationException
     * @throws TaxCodeViolationException
     * @throws FlooringDaoException
     */
    public Order createOrder(String date, String customerName, String state, String productType, String area)  throws DateDuplicationException,
            InputValidationException, TaxCodeViolationException, FlooringDaoException;

    /**
     * Validates the string "date",
     * returns a list of orders with that date if valid
     * @param date
     * @return
     * @throws InputValidationException
     * @throws FlooringDaoException
     */
    public List<Order> getOrdersByDate(String date) throws InputValidationException, FlooringDaoException;

    /**
     * Pass through to add an order to the ordersMap in dao
     * @param order
     * @return
     * @throws FlooringDaoException
     */
    public int submitOrder(Order order) throws FlooringDaoException;
    
    /**
     * Gets a list of all products from the dao productsMap.
     * @return
     * @throws FlooringDaoException
     */
    public List<Product> getProducts() throws FlooringDaoException;
    
    /**
     * Calls dao data import methods for orders, products and taxes.
     * @throws FlooringDaoException
     */
    public void importAllData() throws FlooringDaoException;

    /**
     * Gets an order from the dao orderMap with a matching date and customer customerName
 after validating that the date and customerName are valid inputs.
     * @param date
     * @param customerName
     * @return
     * @throws InputValidationException
     * @throws UnknownItemException
     * @throws FlooringDaoException
     */
    public Order findOrderToEdit(String date, String customerName) throws InputValidationException, UnknownItemException, FlooringDaoException;

    /**
     * Validates all parameter inputs according to business logic
     * and updates/calculates new values for order fields and returns the object 
     * @param order
     * @param customerName
     * @param state
     * @param productType
     * @param area
     * @return
     * @throws InputValidationException
     * @throws TaxCodeViolationException
     * @throws FlooringDaoException
     */
    public Order editOrder(Order order, String customerName, String state, String productType, String area) throws InputValidationException, TaxCodeViolationException, FlooringDaoException;

    /**
     * Takes an order object as parameter and passes through to dao
     * to replace an existing order object in the ordersMap
     * @param orderToChange
     * @throws FlooringDaoException
     */
    public void changeOrder(Order orderToChange) throws FlooringDaoException;

    /**
     * Gets an order based on the date and orderNumber and returns it to
     * the user.
     * @param date
     * @param orderNumber
     * @return
     * @throws InputValidationException
     * @throws UnknownItemException
     * @throws FlooringDaoException
     */
    public Order getOrderToRemove(String date, String orderNumber) throws InputValidationException, UnknownItemException, FlooringDaoException;

    /**
     * Pass through to remove the matching order object from orderMap in dao
     * @param order
     * @throws FlooringDaoException
     */
    public void removeOrder(Order order) throws FlooringDaoException;

    /**
     * Pass through for dao export of all data
     * @throws FlooringDaoException
     */
    public void exportAllData() throws FlooringDaoException;

    /**
     * Pass through for dao export of backup data
     * @throws FlooringDaoException
     */
    public void exportBackupData() throws FlooringDaoException;
    
}
