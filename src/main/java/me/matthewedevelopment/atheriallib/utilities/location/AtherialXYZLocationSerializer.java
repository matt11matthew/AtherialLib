package me.matthewedevelopment.atheriallib.utilities.location;

import me.matthewedevelopment.atheriallib.config.yaml.ConfigSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.SerializeType;

public class AtherialXYZLocationSerializer implements ConfigSerializable<AtherialXYZLocation> {
    @Override
    public SerializeType getComplexity() {
        return SerializeType.SIMPLE;
    }

    @Override
    public Object serializeSimple(AtherialXYZLocation object) {
        return object.toString();
    }

    @Override
    public AtherialXYZLocation deserializeSimple(Object value) {
        return AtherialXYZLocation.fromString((String) value);
    }
}
