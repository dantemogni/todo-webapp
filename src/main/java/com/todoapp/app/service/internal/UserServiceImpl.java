package com.todoapp.app.service.internal;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.todoapp.app.entity.Role;
import com.todoapp.app.entity.Todo;
import com.todoapp.app.entity.User;
import com.todoapp.app.repositories.TodoRepository;
import com.todoapp.app.repositories.UserRepository;
import com.todoapp.app.request.TodoRequest;
import com.todoapp.app.request.UserSignUpRequest;
import com.todoapp.app.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User getUserById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public User createUser(UserSignUpRequest userRequest) {
        User user = new User();

        Set<Role> authorities = new HashSet<>();
        authorities.add(new Role("ROLE_USER"));

        user.setUsername(userRequest.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setModifiedAt(LocalDateTime.now());
        user.setAuthorities(authorities);

        return userRepository.save(user);
    }

    @Override
    public void createTodo(String username, TodoRequest todoRequest) {
        User user = (User) this.loadUserByUsername(username);
        Todo todo = new Todo();

        todo.setUser(user);
        todo.setContent(todoRequest.getItemContent());

        user.getTodoList().add(todo);

        todoRepository.save(todo);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findOneByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User with username - " + username + ", not found"));
    }

    @Override
    public List<Todo> getAllTodos(Long userId) {
        return todoRepository
                .findAllByUserId(userId)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }
}