package com.example.crossnodeiq.serialization;

public interface Serializer<T> {
    /**
     * Serialize object as byte array.
     * @param T data the object to serialize
     * @return byte[]
     */
    byte[] serialize(T data);

}
