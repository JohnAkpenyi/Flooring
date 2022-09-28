package com.fm.dao;

import com.fm.dto.Order;
import com.fm.dto.Product;
import com.fm.dto.Tax;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.time.LocalDate;


public class FlooringDaoFileImpl implements FlooringDao {
    private final String FILE_PRODUCT,
            FILE_TAX,
            FILE_ORDER_PREFIX,
            ORDERS_DIRECTORY,
            FILE_ORDER_BACKUP;
    private final HashMap<Integer, Order> ordersMap = new HashMap<>();
    private final HashMap<String, Product> productMap = new HashMap<>();
    private final HashMap<String, Tax> taxMap = new HashMap<>();
    private static final String DELIMITER = "::";
    
    public FlooringDaoFileImpl(String ordersDir, String orderFilePrefix, String backupOrderFile, String productFile, String taxFile){
        this.ORDERS_DIRECTORY = ordersDir;
        this.FILE_ORDER_PREFIX = orderFilePrefix;
        this.FILE_PRODUCT = productFile;
        this.FILE_TAX = taxFile;
        this.FILE_ORDER_BACKUP = backupOrderFile;
    }
    
    @Override
    public List<Order> getAllOrders(){
        return new ArrayList<>(ordersMap.values());
    }
    
    @Override
    public List<Order> getOrdersByDate(LocalDate date) {
        return getAllOrders()
                .stream()
                .filter((order) -> order.getOrderDate()
                        .equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public void importOrderData() throws FlooringDaoException{
        
        File ordersDirectory = new File(ORDERS_DIRECTORY);
        String currentLine;
        Order currentOrder;
        
        try{
            for(File ordersFile : ordersDirectory.listFiles()){

                Scanner scanner = new Scanner(new BufferedReader(new FileReader(ordersFile)));
                scanner.nextLine(); //Skip header line

                while(scanner.hasNextLine()){

                    currentLine = scanner.nextLine();
                    currentOrder = unmarshallOrder(currentLine);
                    String dateString = ordersFile.getName().replace(FILE_ORDER_PREFIX, "");
                    dateString = dateString.replace(".txt", "");
                    LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("MMddyyyy"));
                    date.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
                    currentOrder.setOrderDate(date);
                    ordersMap.put(currentOrder.getOrderNumber(), currentOrder);
                }
                scanner.close();
            }  
        }
        catch(FileNotFoundException e){
            throw new FlooringDaoException("ERROR: Problem reading order files");
        }

    }

    private Order unmarshallOrder(String orderAsText){
        String orderTokens[] = orderAsText.split(DELIMITER);
        Order orderFromFile = new Order(orderTokens[0], orderTokens[1], orderTokens[2], new BigDecimal(orderTokens[3]),
                orderTokens[4],new BigDecimal(orderTokens[5]), new BigDecimal(orderTokens[6]), new BigDecimal(orderTokens[7]),
                new BigDecimal(orderTokens[8]), new BigDecimal(orderTokens[9]),new BigDecimal(orderTokens[10]),
                new BigDecimal(orderTokens[11]));
        return orderFromFile;
    }
    
    @Override
    public void exportOrderData() throws FlooringDaoException{
        PrintWriter pw;
        String orderAsText;

        //Sort orders into Hash Map with date as key, list of orders as value
        Map<LocalDate, List<Order>> ordersByDate = getOrdersByDateMap();

        try{
            //Get rid of removed items in the directory
            tidyUpDirectory();
            
            for(List<Order> orderList : ordersByDate.values()){

                //Get the date based on the current list
                String dateStr = orderList.get(0).getOrderDate()
                        .format(DateTimeFormatter.ofPattern("MMddyyyy"));
                
                //Name the file using the date
                pw = new PrintWriter(new FileWriter(new File(ORDERS_DIRECTORY, FILE_ORDER_PREFIX + dateStr + ".txt")));
                
                //HEADER: first line of file
                pw.println("OrderNumber" + DELIMITER + "CustomerName" + DELIMITER + "State" + DELIMITER + "TaxRate" + DELIMITER + "ProductType"
                        + DELIMITER + "Area" + DELIMITER + "CostPerSquareFoot" + DELIMITER + "LaborCostPerSquareFoot"
                        + DELIMITER + "MaterialCost" + DELIMITER + "LaborCost" + DELIMITER + "Tax" + DELIMITER + "Total");
                pw.flush();
                
                //insert each order for given date
                for(Order order : orderList){
                    orderAsText = marshallOrder(order);
                    pw.println(orderAsText);
                    pw.flush();
                }
                pw.close();
            }
        } 
        catch (IOException e) {
            throw new FlooringDaoException("ERROR: Problem writing to order files.");
        }
        
    }

    //Structure
    //OrderNumber::CustomerName::State::TaxRate::ProductType
    //::Area::CostPerSquareFoot::LaborCostPerSquareFoot
    //::MaterialCost::LaborCost::Tax::Total*/
    private String marshallOrder(Order order){
        String orderString = order.getOrderNumber() + DELIMITER + order.getCustomerName() + DELIMITER
                + order.getState() + DELIMITER + order.getTaxRate() + DELIMITER
                + order.getProductType() + DELIMITER + order.getArea() + DELIMITER 
                + order.getCostPerSqFt() + DELIMITER + order.getLaborCostPerSqFt() + DELIMITER 
                + order.getMaterialCost() + DELIMITER + order.getLaborCost() + DELIMITER 
                + order.getTaxCost() + DELIMITER  + order.getTotal();
        return orderString;
    }
    
    
    @Override
    public void importProductData() throws FlooringDaoException {
        Scanner scan = null;

        try{
            scan = new Scanner(new BufferedReader(new FileReader(FILE_PRODUCT)));
        }
        catch(FileNotFoundException e){
            throw new FlooringDaoException("ERROR: PROBLEM READING product file");
        }
        
        String currentLine;
        Product product;
        
        scan.nextLine(); //Skip the header file, which is the first
        
        while(scan.hasNextLine()){
            currentLine = scan.nextLine();
            product = unmarshallProduct(currentLine);
            productMap.put(product.getProductType(), product);
        }
        scan.close();
    }
    
    private Product unmarshallProduct(String productAsText) {
        String productTokens[] = productAsText.split(DELIMITER);
        Product productFromFile = new Product(productTokens[0], new BigDecimal(productTokens[1]), 
                new BigDecimal(productTokens[2]));
        return productFromFile;
    }
    
    @Override
    public void importTaxData() throws FlooringDaoException {
        Scanner scan = null;
        try{
            scan = new Scanner(new BufferedReader(new FileReader(FILE_TAX)));
        }
        catch(FileNotFoundException e){
            throw new FlooringDaoException("Couldn't read tax file");
        }

        String currentLine;
        Tax tax;
        scan.nextLine(); //Skip the header file, which is the first
        
        while(scan.hasNextLine()){
            currentLine = scan.nextLine();
            tax = unmarshallTax(currentLine);
            taxMap.put(tax.getStateAbbrev(), tax);
        }
        
        scan.close();
    }

    private Tax unmarshallTax(String taxAsText) {
        String[] tTokens = taxAsText.split(DELIMITER);
        Tax taxFromFile = new Tax(tTokens[0], tTokens[1], new BigDecimal(tTokens[2]));
        return taxFromFile;
    }

    @Override
    public int addOrder(Order newOrder) {
             
        //Get total/size then add one
        if(!ordersMap.isEmpty()){
            Order order = getAllOrders().stream().max(Comparator.comparing(var -> var.getOrderNumber())).get();
            int orderNumber = order.getOrderNumber() + 1;

            //Give the new order number to a newly placed order and add the order to the map
            newOrder.setOrderNumber(orderNumber);
            ordersMap.put(newOrder.getOrderNumber(), newOrder);
            return orderNumber;
        }
        else{
            int orderNumber = 1;
            newOrder.setOrderNumber(orderNumber);
            ordersMap.put(newOrder.getOrderNumber(), newOrder);
            return orderNumber;
        }

    }

    @Override
    public Order createOrder(String date, String customerName, String state, String productType, BigDecimal area) {
        return new Order(customerName, state, productType, LocalDate.parse(date, DateTimeFormatter.ofPattern("MM-dd-yyyy")), area, 
                taxMap.get(state).getTaxRate(), productMap.get(productType).getCostPerSqFt(), productMap.get(productType).getLaborCostPerSqFt());
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
    public boolean checkProductType(String productType){
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
        ordersMap.replace(newOrder.getOrderNumber(), newOrder);
        exportOrderData();
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
        ordersMap.remove(orderToRemove.getOrderNumber(), orderToRemove);
    }

    private Map<LocalDate, List<Order>> getOrdersByDateMap() {
        return getAllOrders().stream().collect(Collectors.groupingBy(order -> order.getOrderDate()));
    }

    @Override
    public void exportBackupOrderData() throws FlooringDaoException {
        PrintWriter out;
        
        try{
            out = new PrintWriter(new FileWriter(FILE_ORDER_BACKUP));
        } 
        catch (IOException ex) {
            throw new FlooringDaoException("Cannot write to file");
        }
        //Header line for file
        out.println("OrderNumber" + DELIMITER + "CustomerName" + DELIMITER + "State" + DELIMITER + "TaxRate" + DELIMITER + "ProductType"
                + DELIMITER + "Area" + DELIMITER + "CostPerSquareFoot" + DELIMITER + "LaborCostPerSquareFoot"
                + DELIMITER + "MaterialCost" + DELIMITER + "LaborCost" + DELIMITER + "Tax" + DELIMITER + "Total" + DELIMITER + "Date");
        out.flush();
        String orderText;
        List<Order> orderList = this.getAllOrders();
        for(Order order : orderList){
            orderText = marshallOrderWithDate(order);
            out.println(orderText);
            out.flush();
        }
        
        out.close();
    }

    private String marshallOrderWithDate(Order currentOrder) {
                String orderString = currentOrder.getOrderNumber() + DELIMITER + currentOrder.getCustomerName() + DELIMITER
                + currentOrder.getState() + DELIMITER + currentOrder.getTaxRate() + DELIMITER
                + currentOrder.getProductType() + DELIMITER + currentOrder.getArea() + DELIMITER
                + currentOrder.getCostPerSqFt() + DELIMITER + currentOrder.getLaborCostPerSqFt() + DELIMITER
                + currentOrder.getMaterialCost() + DELIMITER + currentOrder.getLaborCost() + DELIMITER
                + currentOrder.getTaxCost() + DELIMITER  + currentOrder.getTotal() + DELIMITER + currentOrder.getOrderDate().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        return orderString;
    }

    private void tidyUpDirectory() throws IOException{
       File ordersDir = new File(ORDERS_DIRECTORY);
       
       for(File file : ordersDir.listFiles()){
           file.delete();
       }
    }
}
