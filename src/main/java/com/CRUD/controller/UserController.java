package com.CRUD.controller;


import com.CRUD.entity.User;
import com.CRUD.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Value("${user.minAge}")
    private int minAge;

    @GetMapping("/")
    public String getHomePage(Model model){
        return findPaginated(1, "email", "asc", model);
    }

    @GetMapping("/showNewUserForm")
    public String showNewUserForm(Model model){
        User user = new User();
        model.addAttribute("user", user);
        return "new_user";
    }

    @PostMapping("/saveUser")
    public String saveNewUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        // Проверка возраста пользователя
        LocalDate birthday = user.getBirthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate minAgeDate = LocalDate.now().minusYears(minAge); //
        if (birthday.isAfter(minAgeDate)) {
            bindingResult.rejectValue("birthday", "invalid", "User must be at least " + minAge + " years old");
        }

        // Проверка ошибок валидации
        if (bindingResult.hasErrors()) {
            // Проверка наличия ошибки валидации для поля email
            FieldError emailError = bindingResult.getFieldError("email");
            if (emailError != null) {
                // Если есть ошибка валидации для поля email, добавляем сообщение об ошибке

                model.addAttribute("emailError", emailError.getDefaultMessage());
            }
            return "new_user"; // Возвращаем страницу с формой, чтобы пользователь мог исправить ошибки
        }

        // Ваш код сохранения пользователя
        userService.saveUser(user);
        return "redirect:/";
    }

    @GetMapping("/searchUsersByBirthday")
    public String searchUsersByBirthday(@RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
                                        @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
                                        Model model) {
        // Конвертация Date в LocalDate
        LocalDate fromLocalDate = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toLocalDate = toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Проверка, что дата "От" меньше даты "До"
        if (fromLocalDate.isAfter(toLocalDate)) {
            // Если дата "От" больше даты "До", перенаправляем на страницу с ошибкой
            return "errorPage";
        }

        // Конвертация LocalDate обратно в Date для использования в сервисе
        List<User> users = userService.findUsersByBirthdayBetween(
                Date.from(fromLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(toLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        );

        // Добавляем найденных пользователей в модель для передачи на страницу
        model.addAttribute("users", users);

        // Возвращаем имя представления, которое отобразит найденных пользователей
        return "search_results";
    }

    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable(value = "id")long id, Model model){
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "update_user";
    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable(value = "id") long id){
        userService.deleteUserById(id);
        return "redirect:/";
    }

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDir") String sortDir,
                                Model model) {

        int pageSize = 3;

        Page<User> page = userService.findPaginated(pageNo, pageSize, sortField, sortDir);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("listUsers", page.getContent());

        return "index";
    }
}
