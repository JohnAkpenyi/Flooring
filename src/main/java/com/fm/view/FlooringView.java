package com.fm.view;

import com.fm.dto.Order;
import com.fm.dto.Product;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class FlooringView {
    private UserIO io;
    
    public FlooringView(UserIO io){
        this.io = io;
    }
    
    public int displayGetMenuChoice() {
        io.print("<<Flooring Program>>");
        io.print(" 1. Display Orders");
        io.print(" 2. Add an Order");
        io.print(" 3. Edit an Order");
        io.print(" 4. Remove an Order");
        io.print(" 5. Export All Data");
        io.print(" 6. Quit");
        return io.readInt("Enter a menu option from above: ", 1, 6);
    }

    public void displayOrders(List<Order> orders) {
        String tableHeader = String.format("%-15s%-15s%-30s%-20s%-45s%-15s%-10s", "Order Number",
                "Date", "Customer Name", "State", "Product Type", "Area(SqFT)", "Total");
        io.print("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
        io.print(tableHeader);
        for(Order order : orders){
            String fOrderStr = String.format("%-15s%-15s%-30s%-20s%-45s%-15s$%-10s", order.getOrderNumber(),
                order.getOrderDate().format(DateTimeFormatter.ofPattern("MM-dd-yyyy")), order.getCustomerName(), order.getState(),
                order.getProductType(), order.getArea(), order.getTotal());
            /*ADD MORE FIELDS TO DISPLAY TO USER ABOUT ORDERS?*/
            io.print(fOrderStr);
        }
        io.print("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
        io.readString("Press enter to continue.");
    }

    public String getDate() {
        return io.readString("Enter the date of the order in the format of mm-dd-yyyy: ");
    }

    public void displayErrorMessage(String message) {
        io.print(message);
    }
    
    public String getName(){
        return io.readString("Please Enter your name: ");
    }
    
    public String getState(){
        return io.readString("Please Enter your state abbreviation (i.e TX, WA, KY): ").toUpperCase();
    }
    
    public String getProductType(List<Product> products){
        String tableHeader = String.format("%-35s%-35s%-35s", "Product Type",
                "Cost Per Square Foot", "Labor Cost Per Square Foot");
        io.print("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
        io.print(tableHeader);
        for(Product product : products){
            String fProductStr = String.format("%-35s$%-35s$%-35s", product.getProductType(),
                product.getCostPerSqFt(), product.getLaborCostPerSqFt());
            io.print(fProductStr);
        }
        io.print("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
        return io.readString("Enter the name of the product type you wish to purchase: ").toLowerCase();
    }
    
    public String getArea(){
        return io.readString("What is the amount you'd like to purchase in square feet: ");
    }

    public void showSummary(Order order) {
        String fOrderSummaryStr = String.format("%s\n%s\n%s\n%s\n%s\n%s\n", "Date: " + order.getOrderDate().format(DateTimeFormatter.ofPattern("MM-dd-yyyy")), "Order for: " + order.getCustomerName(), 
                "Product Type: " + order.getProductType(), "Area(SqFT): " + order.getArea(), "State: " + order.getState(), "Order total: $" + order.getTotal());
        io.print("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
        io.print("<<ORDER SUMMARY>>");
        io.print(fOrderSummaryStr);
        io.print("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
       
    }

    public String getConfirmation() {
        return io.readString("Would you like to submit the order? (y/n)").toLowerCase();
    }
    
    public String getRemoveOrderConfirmation() {
        return io.readString("Would you like to remove the order? (y/n)").toLowerCase();
    }

    public void displayOrderNotSubmitted() {
        io.print("Order has not been submitted.");
    }

    public void displayInvalidChoice() {
        io.print("Invalid input! Please re-enter one of the correct options.");
    }

    public void showEditing() {
        io.print("Order(s) to edit found.");
    }

    public String getOrderNumber() {
        return io.readString("Enter the order number: ");
    }

    public void displayRemoveOrderSuccess() {
        io.print("Order removed successfully.");
    }

    public void displayOrderNotRemoved() {
        io.print("Order was not removed.");
    }
    
    public void displayOrderNumber(int orderNumber){
        io.print("The unique order number is: " + orderNumber);
    }

    public void showEditInstructions() {
        io.print("Enter in the details of the order you wish to change. If you do not wish to change a certain field, leave it blank and press enter.");
    }
    
    public void displayOrderSubmissionSuccess(){
        io.print("Order was submitted successfully.");
    }

    public void displayBackupSuccess() {
        io.print("Order information backed up successfully.");
    }

}
