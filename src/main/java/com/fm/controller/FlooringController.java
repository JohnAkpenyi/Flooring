package com.fm.controller;

import com.fm.service.InputValidationException;
import com.fm.service.TaxCodeViolationException;
import com.fm.dao.FlooringDaoException;
import com.fm.service.FlooringServiceLayer;
import com.fm.service.DateDuplicationException;
import com.fm.dto.Order;
import com.fm.view.FlooringView;
import com.fm.service.UnknownItemException;
import static org.springframework.util.StringUtils.capitalize;

public class FlooringController {

    private final FlooringView view;
    private final FlooringServiceLayer serviceLayer;

    public FlooringController(FlooringServiceLayer service, FlooringView view){
        this.serviceLayer = service;
        this.view = view;
    }
    
    public void run(){
        try{
            serviceLayer.importAllData();
            
            boolean exit = false;
            while(!exit){
                switch(view.displayGetMenuChoice()){
                    case 1 -> displayAllOrders();
                    case 2 -> addNewOrder();
                    case 3 -> editOrder();
                    case 4 -> removeOrder();
                    case 5 -> exportData();
                    case 6 -> exit = true;
                }
            }
            
            serviceLayer.exportAllData();
        }
        catch(FlooringDaoException e){
            view.displayErrorMessage(e.getMessage());
        }
    }

    private void addNewOrder() {
        boolean finished = false;
        while(!finished){
            try{
                Order order = serviceLayer.createOrder(view.getDate(), view.getName(), view.getState(), capitalize(view.getProductType(serviceLayer.getProducts())), view.getArea());
                view.showSummary(order);
                boolean valid;
                do{
                    String choice = view.getConfirmation();
                    switch (choice) {
                        case "y" -> {
                            view.displayOrderNumber(serviceLayer.submitOrder(order));
                            view.displayOrderSubmissionSuccess();
                            valid = true;
                        }
                        case "n" -> {
                            view.displayOrderNotSubmitted();
                            valid = true;
                        }
                        default -> {
                            view.displayInvalidChoice();
                            valid =  false;
                            break;
                        }
                    }
                }while(!valid);

                finished = true;
            }
            catch(DateDuplicationException | FlooringDaoException | InputValidationException | TaxCodeViolationException e ){
                view.displayErrorMessage(e.getMessage());
                finished = true;
            }
        }
    }

    private void displayAllOrders(){

        boolean finished = false;

        while(!finished){
            try{
                view.displayOrders(serviceLayer.getOrdersByDate(view.getDate()));
                finished = true;
            }
            catch(InputValidationException e ){
                view.displayErrorMessage(e.getMessage());
            } 
            catch (FlooringDaoException e) {
                view.displayErrorMessage(e.getMessage());
                finished = true;
            }
        }
    }



    private void editOrder() {

        Order currentOrder = null;
        boolean finished = false;
        
        while(!finished){
            try{
                currentOrder = serviceLayer.findOrderToEdit(view.getDate(), view.getName());
                finished = true;
            }
            catch(InputValidationException e){
                view.displayErrorMessage(e.getMessage());
            }
            catch(UnknownItemException | FlooringDaoException e){
                view.displayErrorMessage(e.getMessage());
                return;
            }
        }
        
        finished = false;

        while(!finished){
            try{
                view.showEditing();
                view.showSummary(currentOrder);
                view.showEditInstructions();

                currentOrder = serviceLayer.editOrder(currentOrder, view.getName(), view.getState(), capitalize(view.getProductType(serviceLayer.getProducts())), view.getArea());
                view.showSummary(currentOrder);

                boolean isValid;

                do{
                    String choice = view.getConfirmation();
                    switch (choice) {
                        case "y" -> {
                            serviceLayer.changeOrder(currentOrder);
                            view.displayOrderSubmissionSuccess();
                            isValid = true;
                        }
                        case "n" -> {
                            view.displayOrderNotSubmitted();
                            isValid = true;
                        }
                        default -> {
                            view.displayInvalidChoice();
                            isValid =  false;
                        }
                    }
                }while(!isValid);

                finished = true;
            }
            catch(InputValidationException e){
                view.displayErrorMessage(e.getMessage());
            }
            catch(TaxCodeViolationException | FlooringDaoException e){
                view.displayErrorMessage(e.getMessage());
                finished = true;
            }
        }
    }

    private void removeOrder() {
        Order orderToRemove = null;
        boolean finished = false;
        
        while(!finished){
            try{
                orderToRemove = serviceLayer.getOrderToRemove(view.getDate(), view.getOrderNumber());
                finished = true;
            }
            catch(InputValidationException e){
                view.displayErrorMessage(e.getMessage());
            }
            catch(UnknownItemException | FlooringDaoException e){
                view.displayErrorMessage(e.getMessage());
                return;
            }
        }
        
        boolean isValid = true;

                do{
                    view.showSummary(orderToRemove);
                    String choice = view.getRemoveOrderConfirmation();
                    switch (choice) {
                        case "y"->
                        {
                            try{
                                serviceLayer.removeOrder(orderToRemove);
                                view.displayRemoveOrderSuccess();
                                isValid = true;
                                break;
                            } 
                            catch (FlooringDaoException e) {
                                view.displayErrorMessage(e.getMessage());
                            }
                        }
                        case "n" -> {
                            view.displayOrderNotRemoved();
                            isValid = true;
                            break;
                        }
                        default -> {
                            view.displayInvalidChoice();
                            isValid =  false;
                            break;
                        }
                    }
                }while(!isValid);
    }

    private void exportData() {
        try{
            serviceLayer.exportBackupData();
            view.displayBackupSuccess();
        } 
        catch (FlooringDaoException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }
}
