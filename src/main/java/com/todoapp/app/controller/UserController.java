package com.todoapp.app.controller;

import javax.validation.Valid;

import com.todoapp.app.entity.User;
import com.todoapp.app.request.TodoRequest;
import com.todoapp.app.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/user/home")
public class UserController {

    @Autowired
    private UserService userService;

    private static final String VIEW_NAME = "user/home";

    @GetMapping
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) userService.loadUserByUsername(auth.getName());

        modelAndView.addObject("user", user);
        modelAndView.addObject("todoUser", new TodoRequest());

        modelAndView.setViewName(VIEW_NAME);

        return modelAndView;
    }

    @PostMapping
    public ModelAndView createTodo(@Valid TodoRequest todo) {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) userService.loadUserByUsername(auth.getName());

        if (todo.getItemContent().isEmpty() || todo.getItemContent().isBlank()) {
            modelAndView.addObject("errorMessage", "Cannot create empty item");
        } else {
            userService.createTodo(auth.getName(), todo);
            modelAndView.addObject("successMessage", "Item has been created successfully");
        }

        modelAndView.addObject("user", user);
        modelAndView.addObject("todoUser", new TodoRequest());

        modelAndView.setViewName(VIEW_NAME);

        return modelAndView;
    }

    @PostMapping("/edit")
    public ModelAndView updateTodo(@RequestParam(name = "id") Long id,
            @RequestParam(name = "edit") @Valid TodoRequest todo,
            @RequestParam(name = "completed", required = false) boolean completed) {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) userService.loadUserByUsername(auth.getName());

        userService.updateTodo(id, todo, completed);

        modelAndView.addObject("user", user);
        modelAndView.addObject("todoUser", new TodoRequest());

        modelAndView.setViewName(VIEW_NAME);
        return modelAndView;
    }

    @PostMapping("/delete")
    public ModelAndView deleteTodo(@RequestParam(name = "id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) userService.loadUserByUsername(auth.getName());

        userService.deleteTodo(id);

        modelAndView.addObject("user", user);
        modelAndView.addObject("todoUser", new TodoRequest());

        modelAndView.setViewName(VIEW_NAME);
        return modelAndView;
    }

}
