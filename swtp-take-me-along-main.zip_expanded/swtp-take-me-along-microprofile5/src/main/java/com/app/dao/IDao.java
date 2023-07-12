package com.app.dao;

import java.util.Collection;
import java.util.Optional;

public interface IDao<T, P> {

    Optional<T> getById(P id);

    Collection<T> getAll();

    P insert(T t);

    void update(T t);

    void delete(T t);
}
