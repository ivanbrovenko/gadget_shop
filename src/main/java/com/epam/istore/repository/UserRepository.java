package com.epam.istore.repository;

import com.epam.istore.entity.User;
import com.epam.istore.exception.RepositoryException;


public interface UserRepository {
    boolean add(User user) throws RepositoryException;

    boolean containsUser(User user) throws RepositoryException;

    User getUserByLoginAndPassword(String login, String password) throws RepositoryException;
}
