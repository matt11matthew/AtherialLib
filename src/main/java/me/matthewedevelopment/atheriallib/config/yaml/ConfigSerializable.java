package me.matthewedevelopment.atheriallib.config.yaml;

import java.util.Map;

/*
public interface ConfigSerializable<T> {
    Map<String, Object> serialize(T object);
    T deserialize(Map<String, Object> map);


}*/
public interface ConfigSerializable<T> {
    // Used for complex types
    default Map<String, Object> serializeComplex(T object) {
        throw new UnsupportedOperationException("Complex serialization not supported for this type.");
    }

    // Used for single-value types
    default Object serializeSimple(T object) {
        throw new UnsupportedOperationException("Simple serialization not supported for this type.");
    }

    SerializeType getComplexity();

    // Deserialize from complex types
    default T deserializeComplex(Map<String, Object> map) {
        throw new UnsupportedOperationException("Complex deserialization not supported for this type.");
    }

    // Deserialize from single-value types
    default T deserializeSimple(Object value) {
        throw new UnsupportedOperationException("Simple deserialization not supported for this type.");
    }

    default T serialize(T o) {
        switch (getComplexity()){
            case SIMPLE:
                return (T) serializeSimple(o);
            case COMPLEX:
                return (T) serializeComplex(o);
        }
        return null;
    }
}
