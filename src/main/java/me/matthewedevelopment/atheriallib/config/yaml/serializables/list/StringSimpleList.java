package me.matthewedevelopment.atheriallib.config.yaml.serializables.list;

import java.util.List;

/**
 * Created by Matthew E on 12/23/2023 at 10:09 PM for the project AtherialLib
 */
public class StringSimpleList extends SimpleList<String, StringSimpleList> {
    public StringSimpleList(List<String> list) {
        super(list);
    }

    public StringSimpleList(String... list) {
        super(list);
    }

    public StringSimpleList() {
    }
}
