package com.github.lahahana.xtrpc.test.service.impl;

import com.github.lahahana.xtrpc.test.domain.User;
import com.github.lahahana.xtrpc.test.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

    @Override
    public List<User> getAllUsers() {
        List<User> relatedUsers = new ArrayList<>();
        User user1 = new User(0,"HaHa1", 21);
        User user2 = new User(1,"HaHa2", 22);
        relatedUsers.add(user1);
        relatedUsers.add(user2);
        return relatedUsers;
    }
}
