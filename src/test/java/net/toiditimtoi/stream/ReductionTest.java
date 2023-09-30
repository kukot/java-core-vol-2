package net.toiditimtoi.stream;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReductionTest {

    private final List<String> provinces = List.of("Nghe An", "Ha Noi", "Ninh Binh", "Thanh Hoa", "Nam Dinh");

    @Test
    public void minTest() {
        Optional<String> longestName = provinces.stream().max(Comparator.comparing(String::length));
        assertEquals("Ninh Binh", longestName.orElse(null));

        Optional<String> shortestName = provinces.stream().min(Comparator.comparing(String::length));
        assertEquals("Ha Noi", shortestName.orElse(null));
    }

    @Test
    public void findFirstTest() {
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
        var anyStartsWithT = provinces.stream().anyMatch(s -> s.startsWith("T"));
        assertTrue(anyStartsWithT);
    }

    @Test
    public void noneMatchTest() {
        assertTrue(provinces.stream().noneMatch(s -> s.length() > 100));

        var empty = Stream.<String>empty();
        assertTrue(empty.noneMatch(s -> s.length() > 10));
    }

    @Test
    public void allMatchTest() {
        assertTrue(provinces.stream().allMatch(s -> s.length() > 1));

        // an empty stream is special, it always return allMatch a true result
        var emptyStream = Stream.<String>empty();
        assertTrue(emptyStream.allMatch(s -> s.length() > 100));
    }

    @Test
    public void collectingResultUsingIteratorTest() {
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
        provinces.parallelStream()
                .filter(s -> s.length() > 6)
                .forEach(System.out::println); // the strings are printed in arbitrary order

        provinces.parallelStream()
                .filter(s -> s.length() > 6)
                .forEachOrdered(System.out::println);
    }

    // calculate total length of all province names
    @Test
    public void sumAsReduction() {
        var totalLength = provinces.stream()
                .map(String::length)
                .reduce(0, (s1, s2) -> s1 + s2);

        var totalLength2 = provinces.stream()
                .map(String::length)
                .reduce(Integer::sum);

        /**
         * Think about this like this, when we have a stream of type A, but we want a reduction result of type B
         * In this case, the usual reduce will not work because it requires that the reduce result and the stream element
         * type are the same.
         * This version of reduce with 3 parameters provide a solution for that.
         * an identity value
         * an accumulator function that incorporate the next element into the result
         * (calculatedVal, nextEle) -> { }
         * Finally, the combiner that combines two values. It's easier to imagine if you think about it in a parallel stream. We need to combine the
         * temporary results to get the final one.
         */
        var totalLengthWithoutMap = provinces.stream()
                .reduce(
                        0,
                        (sub1, anotherString) -> sub1 + anotherString.length(),
                        Integer::sum
                );
    }

}
