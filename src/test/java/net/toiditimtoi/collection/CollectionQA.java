package net.toiditimtoi.collection;

import org.junit.jupiter.api.Test;

import java.util.Set;

public class CollectionQA {
    @Test
    public void callingSetOfWithDuplicateElement() {
        var mySet = Set.of(1, 2, 3, 3);
    }
}
