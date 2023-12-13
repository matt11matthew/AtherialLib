package me.matthewedevelopment.atheriallib.io;

@FunctionalInterface
public interface Replacer<T> {
    T replace( T t);
}