package me.matthewedevelopment.atheriallib.config.yaml.serializables.list;

import java.util.List;

/**
 * Created by Matthew E on 12/23/2023 at 10:09 PM for the project AtherialLib
 */
public class IntSimpleList extends SimpleList<Integer, IntSimpleList> {
    public IntSimpleList(List<Integer> list) {
        super(list);
    }

    public IntSimpleList(Integer... list) {
        super(list);
    }

    public IntSimpleList() {
    }
}
