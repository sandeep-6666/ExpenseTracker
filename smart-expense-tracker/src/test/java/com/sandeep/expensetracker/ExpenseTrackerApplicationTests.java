package com.sandeep.expensetracker;

import com.sandeep.expensetracker.entity.User;
import com.sandeep.expensetracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ExpenseTrackerApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void listUsers() {
        List<User> users = userRepository.findAll();
        System.out.println("=== USERS IN DATABASE ===");
        for (User u : users) {
            System.out.println("ID: " + u.getId() + ", Email: " + u.getEmail() + ", Name: " + u.getName());
        }
        System.out.println("=========================");
    }
}


