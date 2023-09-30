package net.toiditimtoi.serialization;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrimitiveSerializationTest {

    private final long originalValue = 123456789L;

    private final String PRIMITIVE_FILE_NAME = "primitive-long.ser";
    private final String WRAPPER_FILE_NAME = "wrapper-long.ser";
    @Test
    @Order(0)
    public void longSerializationTest() throws Exception {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PRIMITIVE_FILE_NAME))) {
            oos.writeLong(originalValue);
        }

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(WRAPPER_FILE_NAME))) {
            oos.writeObject(originalValue);
        }
    }

    @Order(1)
    @Test
    public void deserializationTest() throws Exception {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PRIMITIVE_FILE_NAME))) {
            var getBack = ois.readLong();
            assertEquals(originalValue, getBack);
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(WRAPPER_FILE_NAME))) {
            var getBack = ois.readObject();
            assertEquals(originalValue, getBack);
        }
    }
}
