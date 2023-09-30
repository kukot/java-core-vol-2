package net.toiditimtoi.serialization;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertNull;

public class ObjectSerializationWithParentTest {

    static class Person {
        private String name;

        public Person() {}

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    static class ExtendedPerson extends Person implements Serializable {
        public ExtendedPerson(String s) {
            super(s);
            System.out.println("Extended person constructor called");
            if (s == null || s.isBlank()) {
                throw new IllegalArgumentException("Name cannot be blank");
            }
        }
    }

    @Test
    public void serializationWithParentNotSerializableTest() throws Exception {
        var extPer = new ExtendedPerson("Kevin");
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("kevin.ser"))) {
            oos.writeObject(extPer);
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("kevin.ser"))) {
            ExtendedPerson getBack = (ExtendedPerson) ois.readObject();
            System.out.println(getBack);
            assertNull(getBack.getName());
        }
    }
}
