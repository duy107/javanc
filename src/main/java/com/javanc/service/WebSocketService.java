package com.javanc.service;
public interface WebSocketService<E> {
    void sendForOnlyUser(E entity);
    void sendForAllUser (E entity);
}
