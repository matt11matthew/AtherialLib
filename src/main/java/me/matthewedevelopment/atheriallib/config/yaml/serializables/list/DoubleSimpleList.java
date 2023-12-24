package me.matthewedevelopment.atheriallib.config.yaml.serializables.list;

import java.util.List;

/**
 * Created by Matthew E on 12/23/2023 at 10:09 PM for the project AtherialLib
 */
public class DoubleSimpleList extends SimpleList<Double, DoubleSimpleList> {
    public DoubleSimpleList(List<Double> list) {
        super(list);
    }

    public DoubleSimpleList(Double... list) {
        super(list);
    }

    public DoubleSimpleList() {
    }
}
