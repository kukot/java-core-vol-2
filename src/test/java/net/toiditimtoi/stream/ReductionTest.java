package net.toiditimtoi.stream;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReductionTest {
    
    @Test
    public void minTest() {
        var provinces = List.of("Nghe An", "Ha Noi", "Ninh Binh", "Thanh Hoa", "Nam Dinh");
        Optional<String> longestName = provinces.stream().max(Comparator.comparing(String::length));
        assertEquals("Ninh Binh", longestName.orElse(null));

        Optional<String> shortestName = provinces.stream().min(Comparator.comparing(String::length));
        assertEquals("Ha Noi", shortestName.orElse(null));
    }

    @Test
    public void findFirstTest() {
        var provinces = List.of("Nghe An", "Ha Noi", "Ninh Binh", "Thanh Hoa", "Nam Dinh");
        var firstProvinceStartsWithN = provinces
                .stream()
                .filter(s -> s.startsWith("N"))
                .sorted()
                .findFirst()
                .orElse(null);
        System.out.println(firstProvinceStartsWithN != null ? firstProvinceStartsWithN : "No province starts with N");
    }

    @Test
    public void findAllTest() {
        var provinces = List.of("Nghe An", "Ha Noi", "Ninh Binh", "Thanh Hoa", "Nam Dinh");
        var aRandomlySelectedProvincesStartsWithN = provinces
                .stream()
                .filter(s -> s.startsWith("N"))
                .findAny() // explicitly nondeterministic operation
                .orElse(null);

        // findAny is aimed to maximize performance in parallel operations, hence may result to different result even for the same source
        System.out.println(aRandomlySelectedProvincesStartsWithN != null ? aRandomlySelectedProvincesStartsWithN : "No province starts with N");
    }

    @Test
    public void anyMatchTest() {
        var provinces = List.of("Nghe An", "Ha Noi", "Ninh Binh", "Thanh Hoa", "Nam Dinh");
        var anyStartsWithT = provinces.stream().anyMatch(s -> s.startsWith("T"));
        assertTrue(anyStartsWithT);
    }

    @Test
    public void noneMatchTest() {
        var provinces = List.of("Nghe An", "Ha Noi", "Ninh Binh", "Thanh Hoa", "Nam Dinh");
        assertTrue(provinces.stream().noneMatch(s -> s.length() > 100));

        var empty = Stream.<String>empty();
        assertTrue(empty.noneMatch(s -> s.length() > 10));
    }

    @Test
    public void allMatchTest() {
        var provinces = List.of("Nghe An", "Ha Noi", "Ninh Binh", "Thanh Hoa", "Nam Dinh");
        assertTrue(provinces.stream().allMatch(s -> s.length() > 1));

        // an empty stream is special, it always return allMatch a true result
        var emptyStream = Stream.<String>empty();
        assertTrue(emptyStream.allMatch(s -> s.length() > 100));
    }

    @Test
    public void collectingResultUsingIteratorTest() {
        var provinces = List.of("Nghe An", "Ha Noi", "Ninh Binh", "Thanh Hoa", "Nam Dinh");
        var iterator = provinces
                .stream()
                .filter(s -> s.length() > 6)
                .iterator();
        while (iterator.hasNext()) {
            var next = iterator.next();
            System.out.println(next + " has " + next.length() + " character(s)");
        }
    }

    @Test
    public void forEachOrdered() {
        var provinces = List.of("Nghe An", "Ha Noi", "Ninh Binh", "Thanh Hoa", "Nam Dinh");
        provinces.parallelStream()
                .filter(s -> s.length() > 6)
                .forEach(System.out::println); // the strings are printed in arbitrary order

        provinces.parallelStream()
                .filter(s -> s.length() > 6)
                .forEachOrdered(System.out::println);

    }

}
