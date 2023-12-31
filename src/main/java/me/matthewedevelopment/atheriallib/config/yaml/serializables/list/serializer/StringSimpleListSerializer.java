package me.matthewedevelopment.atheriallib.config.yaml.serializables.list.serializer;

import me.matthewedevelopment.atheriallib.config.yaml.ConfigSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.SerializeType;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.StringSimpleList;

/**
 * Created by Matthew E on 12/23/2023 at 10:13 PM for the project AtherialLib
 */
public class StringSimpleListSerializer implements ConfigSerializable<StringSimpleList> {
    @Override
    public SerializeType getComplexity() {
        return SerializeType.SIMPLE;
    }
    @Override
    public Object serializeSimple(StringSimpleList object) {

        if (object.isEmpty()){
            return "[]";
        }
        StringBuilder output = new StringBuilder("[");

        for (int i = 0; i < object.getList().size(); i++) {
            output.append(object.getList().get(i));
            if (i < object.getList().size()-1) {
                output.append(",");
            }

        }
        output.append("]");
        return output.toString();
    }
    @Override
    public StringSimpleList deserializeSimple(Object value) {
        StringSimpleList list = new StringSimpleList();

        String textValue = (String) value;
        if (textValue.equals("[]")){
            return list;
        }
        textValue=textValue.replace("[", "").replace("]", "").trim();
        if (textValue.contains(",")) {
            for (String s : textValue.split(",")) {
                list = list.add(s);
            }
        } else {
            list = list.add(textValue);
        }
        return list;
    }
}
