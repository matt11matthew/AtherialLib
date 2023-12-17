package me.matthewedevelopment.atheriallib.io;

@FunctionalInterface
public interface DoubleReplacer<T, E> {
    T replace(E var1, T var2);
}
