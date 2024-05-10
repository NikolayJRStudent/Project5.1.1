package com.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import com.CRUD.StarApplication;
import com.CRUD.controller.UserController;
import com.CRUD.service.UserService;
import org.junit.jupiter.api.Test;
import com.CRUD.entity.User;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.matchers.JUnitMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StarApplication.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;

    @InjectMocks
    private UserController userController;

    @Test
    public void testSaveNewUser_ValidUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setBirthday(Date.from(LocalDate.now().minusYears(20).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        String result = userController.saveNewUser(user, bindingResult, model);

        verify(bindingResult, never()).rejectValue(anyString(), anyString(), anyString());
        verify(userService).saveUser(user);
        assertEquals("redirect:/", result);
    }

    @Test
    public void testSaveNewUser_InvalidUser() {

        User user = mock(User.class);

        when(user.getEmail()).thenReturn("invalidEmail");

        LocalDate invalidBirthday = LocalDate.now().minusYears(10);
        when(user.getBirthday()).thenReturn(Date.from(invalidBirthday.atStartOfDay(ZoneId.systemDefault()).toInstant()));


        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldError("email")).thenReturn(new FieldError("user", "email", "Invalid email"));


        String result = userController.saveNewUser(user, bindingResult, model);


        verify(bindingResult).rejectValue("birthday", "invalid", "User must be at least 18 years old");


        verify(model).addAttribute(eq("emailError"), anyString());


        assertEquals("new_task", result);
    }
    @Test
    public void testMainPage() throws Exception {

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(containsString("Add employee")))
                .andExpect(content().string(containsString("email")))
                .andExpect(content().string(containsString("Next")));

    }



}
