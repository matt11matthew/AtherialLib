package me.matthewedevelopment.atheriallib.io;

/**
 * Created by Matthew E on 12/13/2023 at 8:49 PM for the project AtherialLib
 */
@FunctionalInterface
public interface Callback<T>  {
    void call(T t);
}
