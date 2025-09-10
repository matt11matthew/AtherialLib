package me.matthewedevelopment.atheriallib.config.yaml.serializables.list.serializer;

import me.matthewedevelopment.atheriallib.config.yaml.ConfigSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.SerializeType;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.IntSimpleList;
import me.matthewedevelopment.atheriallib.utilities.number.NumberUtils;

/**
 * Created by Matthew E on 12/23/2023 at 10:13 PM for the project AtherialLib
 */
public class IntSimpleListSerializer implements ConfigSerializable<IntSimpleList> {
    @Override
    public SerializeType getComplexity() {
        return SerializeType.SIMPLE;
    }

    @Override
    public Object serializeSimple(IntSimpleList object) {

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
    public IntSimpleList deserializeSimple(Object value) {
        IntSimpleList list = new IntSimpleList();

        String textValue = (String) value;
        if (textValue.equals("[]")){
            return list;
        }
        textValue=textValue.replaceAll(" ", "").trim().replace("[", "").replace("]", "").trim();
        if (textValue.contains(",")) {
            for (String s : textValue.split(",")) {
                list = list.add(NumberUtils.getInteger(s));
            }
        } else {
            list = list.add(NumberUtils.getInteger(textValue));
        }
        return list;
    }
}
