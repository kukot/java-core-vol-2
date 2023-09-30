package net.toiditimtoi.stream;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrimitiveTypesStreamTest {

    private IntStream createStream(int[] source) {
        return Arrays.stream(source);
    }
    @Test
    public void creatingIntStream() {
        var myIntArr = new int[] { 1, 3, 5, 7, 9 };

        IntStream myIntStream = createStream(myIntArr);
        var total = createStream(myIntArr).sum();
        var totalOtherWay = createStream(myIntArr).reduce((Integer::sum)).orElse(0);
        assertEquals(total, totalOtherWay);

        IntStream.iterate(0, i -> i < 100, i -> i + 5)
                        .forEach(System.out::println);
        IntStream.generate(() -> (int)(Math.random() * 1000))
                .limit(100)
                .forEach(System.out::println);

        IntStream myOtherIntStr = IntStream.of(1, 2, 3, 5, 7, 9);
        assertEquals(1, myOtherIntStr.min().orElse(0));
    }

    @Test
    public void creatingIntStreamWithRange() {
        IntStream fromOneToFortyNine= IntStream.range(1, 50);
        assertEquals(49, fromOneToFortyNine.count());

        IntStream fromOneToFifty = IntStream.rangeClosed(1, 50);
        assertEquals(50, fromOneToFifty.count());
    }

    List<String> myClassMates = List.of("Daniel", "Tommy", "McLaren", "Bugati");

    @Test
    public void transformingToIntStream() {
        var longestClassmateName = myClassMates.stream().mapToInt(String::length)
                .max().orElse(-1);
        assertEquals(7, longestClassmateName);
    }

    @Test
    public void toArrayOfPrimitiveTest() {
        int[] classMateNames = myClassMates.stream().mapToInt(String::length).toArray();
    }

    @Test
    public void fromPrimitivesToObject() {
        Stream<Integer> integerStream = myClassMates.stream()
                .mapToInt(String::length)
                .boxed();
    }

    @Test
    public void specialMethodForIntStream() {
        var fibonacci = new int[] { 1, 2, 3, 5, 8, 13, 21 };
        var fiboStreamSum = createStream(fibonacci).sum();
        OptionalInt fiboStrMax = createStream(fibonacci).max();
        OptionalInt fiboStrMin = createStream(fibonacci).min();
        OptionalDouble fiboAvg = createStream(fibonacci).average();

        var statisticInt = createStream(fibonacci).summaryStatistics();
        assertEquals(fiboStrMin.orElse(0), statisticInt.getMin());
        assertEquals(fiboStrMax.orElse(0), statisticInt.getMax());
        assertEquals(fiboAvg.orElse(0), statisticInt.getAverage());
    }
}
