package com.fm.dao;

import com.fm.dto.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FlooringDaoFileImplTest {
    private FlooringDao testDao;
    
    public FlooringDaoFileImplTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() throws Exception{
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        testDao = ctx.getBean("testDao", FlooringDaoFileImpl.class);
        
        testDao.importOrderData();
        testDao.importProductData();
        testDao.importTaxData();
    }
    
    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testGetAllOrders() {
        assertNotNull(testDao.getAllOrders());
        assertEquals(testDao.getAllOrders().size(), 3);
    }
    
    @Test
    public void testGetOrdersByDate(){
        List<Order> ordersByDate = testDao.getOrdersByDate(LocalDate.parse(("06-01-2013"), DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        assertNotNull(ordersByDate);
        assertEquals(ordersByDate.size(), 1);
        assertEquals(ordersByDate.get(0).getState(),  "CA");
        
    }
    
    @Test
    public void testCreateAddOrder(){
        Order order = testDao.createOrder("12-12-2121", "TEST NAME", "KY", "Wood", new BigDecimal("100"));
        assertNotNull(order);
        assertEquals(order.getArea(), new BigDecimal("100.00"));
        assertEquals(order.getOrderDate(), LocalDate.parse("12-12-2121", DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        assertEquals(order.getCostPerSqFt(), new BigDecimal("5.150"));
        assertEquals(order.getTaxRate(), new BigDecimal("6.000"));
        int orderNumber = testDao.addOrder(order);
        assertNotNull(orderNumber);
        assertEquals(orderNumber, 4);
        assertTrue(testDao.getAllOrders().contains(order));
    }
    
    @Test
    public void testGetOrderByNameDate(){
        Order order = testDao.getOrderByNameDate("06-01-2013", "Ada Lovelace");
        assertNotNull(order);
        assertEquals(order.getArea(), new BigDecimal("249.00"));
        assertEquals(order.getOrderNumber(), 1);
        assertEquals(order.getState(), "CA");
        assertEquals(order.getTotal(), new BigDecimal("2381.06"));
        order = testDao.getOrderByNameDate("06-05-2130", "Ada Lovelace");
        assertNull(order);
        order = testDao.getOrderByNameDate("06-01-2013", "Ada Hatelace");
        assertNull(order);
    }
    
    @Test
    public void testRemoveOrder(){
        Order order = testDao.createOrder("12-12-2121", "TEST NAME", "KY", "Wood", new BigDecimal("100"));
        testDao.addOrder(order);
        assertTrue(testDao.getAllOrders().contains(order));
        
        testDao.removeOrder(order);
        assertFalse(testDao.getAllOrders().contains(order));
    }
}
