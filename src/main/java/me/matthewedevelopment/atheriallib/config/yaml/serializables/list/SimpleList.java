package me.matthewedevelopment.atheriallib.config.yaml.serializables.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Matthew E on 12/23/2023 at 10:06 PM for the project AtherialLib
 */
public abstract class SimpleList<T, S extends SimpleList<T, S>> implements Iterable<T> {

    private List<T> list;

    public List<T> getList() {
        return list;
    }

    public SimpleList(List<T> list) {
        this.list = list;
    }

    public boolean isEmpty() {
        return getSize()<=0;

    }
    public int getSize(){
        return list.size();
    }
    public SimpleList() {
        this.list = new ArrayList<>();
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    public S add(T value) {
        list.add(value);
        return (S) this;
    }


}