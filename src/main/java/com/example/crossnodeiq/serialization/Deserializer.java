package com.example.crossnodeiq.serialization;

public interface Deserializer<T> {
    /**
     * Deserialize object from a byte array.
     * @param Class<? extends T> clazz the expected class for the deserialized object
     * @param byte[] data the byte array
     * @return T object instance
     */
    T deserialize(Class<? extends T> clazz, byte[] data);
}
