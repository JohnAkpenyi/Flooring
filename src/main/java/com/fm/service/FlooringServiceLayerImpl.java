package com.fm.service;

import com.fm.dao.FlooringAuditDao;
import com.fm.dao.FlooringDao;
import com.fm.dao.FlooringDaoException;
import com.fm.dto.Order;
import com.fm.dto.Product;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 *
 * @author Joe McAdams
 * @email joedmcadams@gmail.com
 * 
 */
public class FlooringServiceLayerImpl implements FlooringServiceLayer {
    private final FlooringDao dao;
    private final FlooringAuditDao auditDao;

    private static final String VALID_NAMES = "^[A-Za-z\\s,.`]+$";
    private static final String VALID_DATES = "^(1[0-2]|0[1-9])-(3[01]|[12][0-9]|0[1-9])-[0-9]{4}$";
    private static final String VALID_STATES = "^[A-Z][A-Z]+$";
    private static final String VALID_AREAS = "^([0-9]+\\.?[0-9]*|\\.[0-9]+)$";
    private static final String VALID_ORDER_NUMBERS = "^\\d+$";
    
    public FlooringServiceLayerImpl(FlooringDao dao, FlooringAuditDao auditDao) {
        this.dao = dao;
        this.auditDao = auditDao;
    }
    
    @Override
    public List<Order> getOrdersByDate(String date) throws InputValidationException, FlooringDaoException{
        
        //Validate date format
        if(!date.matches(VALID_DATES)) {
            auditDao.writeAuditEntry("ATTEMPTED TO DISPLAY ORDERS WITH AN INVALID DATE.");
            throw new InputValidationException("ERROR: Date format should be MM-DD-YYYY");
        }
        else{
            auditDao.writeAuditEntry("DISPLAY ORDERS FOR - " + date);
            return dao.getOrdersByDate(LocalDate.parse(date, DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        }
    }

    @Override
    public void importAllData() throws FlooringDaoException {
        try{
            dao.importOrderData();
            dao.importProductData();
            dao.importTaxData();
            auditDao.writeAuditEntry("Orders, Products & Tax Data imported successfully");
        }
        catch(FlooringDaoException e){
            auditDao.writeAuditEntry("Orders, Products & Tax Data failed to import");
            throw new FlooringDaoException(e.getMessage());
        }
        
    }

    @Override
    public Order createOrder(String date, String customerName, String state, String productType, String areaStr) 
                            throws DateDuplicationException, InputValidationException, TaxCodeViolationException, FlooringDaoException{
        
        BigDecimal area;
        //Convert string to BigDecimal with input validation
        if(!areaStr.matches(VALID_AREAS)){
            throw new InputValidationException("Invalid area input");
        }
        else{
            area = new BigDecimal(areaStr);
        }
        //Validate date format
        if(!date.matches(VALID_DATES)) {
            throw new InputValidationException("Invalid date format");
        }
        //Input validation for customer name (not blank, only valid characters)
        else if(!customerName.matches(VALID_NAMES)){
            throw new InputValidationException("The name entered contains invalid characters or is an empty field.");
        }
        //Verify date is in the future
        else if(LocalDate.parse(date, DateTimeFormatter.ofPattern("MM-dd-yyyy")).isBefore(LocalDate.now())){
            throw new DateDuplicationException("The date entered is before today's date.");
        }
        else if(!state.matches(VALID_STATES)){
            throw new InputValidationException("invalid state");
        }
        //Verify state exists in tax file
        else if(!dao.checkTaxCode(state)){
            throw new TaxCodeViolationException("State entered is not present in the tax code file.");
        }
        //Verify the productType is a valid product 
        else if(!dao.checkProductType(productType)){
            throw new InputValidationException("The product type entered does not exist.");
        }
        //everything is valid, create object and return it so a summary can be shown
        else{
            auditDao.writeAuditEntry("ORDER INPUT VALIDATED, CREATING ORDER OBJECT FOR VERIFICATION.");
            return dao.createOrder(date, customerName, state, productType, area);
            
        }
    }
    
    @Override
    public int submitOrder(Order order) throws FlooringDaoException{
        auditDao.writeAuditEntry("ORDER VERIFIED AND SUBMITTED");
        return dao.addOrder(order);
    }

    @Override
    public List<Product> getProducts() throws FlooringDaoException{
        auditDao.writeAuditEntry("LIST OF ALL PRODUCTS RETRIEVED");
        return dao.getProducts();
    }

    @Override
    public Order findOrderToEdit(String date, String customerName) throws InputValidationException, UnknownItemException, FlooringDaoException{
        //Validate date format
        if(!date.matches(VALID_DATES)) {
            throw new InputValidationException("Invalid date format.");
        }
        //Input validation for customer customerName (not blank, only valid characters)
        else if(!customerName.matches(VALID_NAMES)){
            throw new InputValidationException("The name entered contains invalid characters or is an empty field.");
        }
        
        Order order = dao.getOrderByNameDate(date, customerName);
        auditDao.writeAuditEntry("EDIT RETRIEVAL INPUT VALIDATED, ORDER OBJECT RETRIEVED");
        
        //Does the order exist
        if(order == null){
            auditDao.writeAuditEntry("RETRIEVED A NULL ORDER OBJECT FOR EDIT.");
            throw new UnknownItemException("There is not an order on " + date + " under the name " + customerName + ".");
        }
        else{
            auditDao.writeAuditEntry("ORDER OBJECT FOR EDIT EXISTS AND RETRIEVED.");
            return order;
        }
    }

    @Override
    public Order editOrder(Order order, String customerName, String state, String productType, String areaStr) 
            throws InputValidationException, TaxCodeViolationException, FlooringDaoException {
        BigDecimal area;
        
        //Convert string to BigDecimal
        if(areaStr.equals("")){
            area = order.getArea(); 
        }
        else if (!areaStr.equals("") && !areaStr.matches(VALID_AREAS)){
            throw new InputValidationException("Invalid input for area.");
        }
        else{
            area = new BigDecimal(areaStr);
        }
        
        //Input validation for customerName (not blank, only valid characters)
        if(!customerName.equals("") && !customerName.matches(VALID_NAMES)){
            throw new InputValidationException("The name entered contains invalid characters or is an empty field.");
        }
        else if(!state.equals("") && !state.matches(VALID_STATES)){
            throw new InputValidationException("The state was not entered properly.");
        }
        //Verify state exists in tax file
        else if(!state.equals("") && !dao.checkTaxCode(state)){
            throw new TaxCodeViolationException("We cannot sell products in your state.");
        }
        //Verify the productType is a valid product 
        else if(!productType.equals("") && !dao.checkProductType(productType)){
            throw new InputValidationException("The product type entered does not exist.");
        }
        
        else{
            auditDao.writeAuditEntry("NEW ORDER FIELDS FOR EDIT VALIDATED, SETTING FIELDS.");
            if(!areaStr.equals("")){
                order.setArea(area);
            }
            if(!state.equals("")){
                order.setState(state);
            }
            if(!productType.equals("")){
                order.setProductType(productType);
            }
            if (!customerName.equals("")){
                order.setCustomerName(customerName);
            }
            auditDao.writeAuditEntry("FIELDS SET, RECALULATING COST VALUES.");
            dao.recalculateOrder(order);
        }
        auditDao.writeAuditEntry("RETURNING ORDER OBJECT WITH UPDATED FIELDS AND COST VALUES.");
        return order;
    }

    @Override
    public void changeOrder(Order orderToChange) throws FlooringDaoException {
        auditDao.writeAuditEntry("EDITED ORDER BEING ADDED BACK TO COLLECTION IN PLACE OF OLD ORDER.");
        dao.updateOrder(orderToChange);
    }

    @Override
    public Order getOrderToRemove(String date, String orderNumber) throws InputValidationException, UnknownItemException, FlooringDaoException{
        //Validate date format
        if(!date.matches(VALID_DATES)) {
            throw new InputValidationException("Invalid date format.");
        }
        //Input validation for customer customerName (not blank, only valid characters)
        else if(!orderNumber.matches(VALID_ORDER_NUMBERS)){
            throw new InputValidationException("Invalid order number.");
        }
        auditDao.writeAuditEntry("ORDER REMOVAL INPUT VALIDATED, RETRIEVING ORDER OBJECT TO REMOVE.");
        Order order = dao.getOrderByOrderNumberDate(orderNumber, date);
        
        //Does the order exist
        if(order == null){
            auditDao.writeAuditEntry("RETRIEVED A NULL ORDER OBJECT FOR REMOVAL.");
            throw new UnknownItemException("There is not an order on " + date + " with the order number " + orderNumber + ".");
        }
        else{
            auditDao.writeAuditEntry("ORDER OBJECT FOR REMOVAL EXISTS AND RETRIEVED.");
            return order;
        }
    }

    @Override
    public void removeOrder(Order order) throws FlooringDaoException {
        dao.removeOrder(order);
        auditDao.writeAuditEntry("ORDER OBJECT REMOVED FROM COLLECTION.");
    }

    @Override
    public void exportAllData() throws FlooringDaoException{
        dao.exportOrderData();
        auditDao.writeAuditEntry("ORDER DATA EXPORTED TO ORDER TEXT FILES BY DATE.");
    }

    @Override
    public void exportBackupData() throws FlooringDaoException {
        dao.exportBackupOrderData();
        auditDao.writeAuditEntry("ORDER DATA EXPORTED TO BACKUP DATA FILE.");
    }

}
