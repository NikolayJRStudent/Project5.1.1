package com.test;


import com.CRUD.StarApplication;
import com.CRUD.entity.User;
import com.CRUD.errorService.UserNotFoundException;
import com.CRUD.service.UserService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = StarApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {
    @Autowired
    public UserService userService;
    private static Long id;
    private final Date birthday = new Date(100, 3, 25);


    @Test
    @Order(1)
    public void testFunctionSaveUserInDataBase(){
        User user = new User();
        user.setEmail("testemail@gmail.com");
        user.setName("TestMan");
        user.setSurName("LastTest");
        user.setBirthday(birthday);
        userService.saveUser(user);
        id = user.getId();
        assertNotEquals(user.getId(), 0L);
        User userFromDB = userService.getUserById(user.getId());
        assertEquals(userFromDB.getEmail(), "testemail@gmail.com");
        assertEquals(userFromDB.getName(), "TestMan");
        assertEquals(userFromDB.getSurName(), "LastTest");


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String expectedDateString = formatter.format(birthday);
        String actualDateString = formatter.format(userFromDB.getBirthday());
        assertEquals(actualDateString, expectedDateString);


    }

    @Test
    @Order(2)
    public void testFunctionDeleteUserAndGetUserByIDWithDataBase(){
        userService.deleteUserById(id);
        RuntimeException exception =
                assertThrows(UserNotFoundException.class, () -> userService.getUserById(id));
        assertEquals(exception.getMessage(), "User not found for id: " +  id);
    }

    @Test
    @Order(3)
    public void testFunctionFindUsersByBirthdayBetween(){

        Date betweenBirthdayStart = new Date(100, 3, 1); // Дата начала диапазона
        Date betweenBirthdayEnd = new Date(106, 3, 30); // Дата конца диапазона


        List<User> users = userService.findUsersByBirthdayBetween(betweenBirthdayStart, betweenBirthdayEnd);


        assertFalse(users.isEmpty());

        for (User user : users) {
            System.out.println("User ID: " + user.getId());
            System.out.println("User Name: " + user.getName());
            System.out.println(user.getBirthday());
        }


        for (User user : users) {
            assertTrue(user.getBirthday().after(betweenBirthdayStart) || user.getBirthday().equals(betweenBirthdayStart));
            assertTrue(user.getBirthday().before(betweenBirthdayEnd) || user.getBirthday().equals(betweenBirthdayEnd));
        }
    }

}
