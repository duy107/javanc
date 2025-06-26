package com.javanc.repository.listener;

public interface IListener <E> {
    void afterUpdate(E entity);
    void beforeUpdate(E entity);
}
