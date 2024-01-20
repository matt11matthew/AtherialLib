package me.matthewedevelopment.atheriallib.utilities.location;

import me.matthewedevelopment.atheriallib.config.yaml.ConfigSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.SerializeType;

public class AtherialLocationSerializer  implements ConfigSerializable<AtherialLocation> {
    @Override
    public SerializeType getComplexity() {
        return SerializeType.SIMPLE;
    }

    @Override
    public Object serializeSimple(AtherialLocation object) {
        return object.toString();
    }

    @Override
    public AtherialLocation deserializeSimple(Object value) {
        return AtherialLocation.fromString((String) value);
    }
}
