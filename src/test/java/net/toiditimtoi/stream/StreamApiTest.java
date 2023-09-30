package net.toiditimtoi.stream;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamApiTest {
    /**
     * Stream doesn't store the element, it takes the elements to operate from a source
     * The ability to do function programming style, we describe what we want to do, not now to do it
     * The code is more concise and is easier to read
     * Execution is delayed until needed
     * Can easily switch between sequential & parallel stream
     */
    @Test
    public void streamAndIteration() {
        var dataSource = List.of("Hello", "Goodbye", "Nice to see you");
        var count = dataSource.stream()
                .filter(str -> str.length() > 12)
                .count();
        System.out.println(count);
        var manualCount = StreamSupport.stream(dataSource.spliterator(), false)
                .filter(str -> str.length() > 5)
                .peek(System.out::println)
                .count();
        System.out.println("Manual count: " + manualCount);
    }

    @Test
    public void streamCreation() {

        // be careful with this, the compiler might see this as a stream of a single element, and that one element is an array of integer
        var myIntArraySource = new Integer[]{1, 2, 3, 100, 200, 300, 5, 7};
        // using Steam.of which accepts var-arg
        var countFromArray = Stream.of(myIntArraySource)
                .filter(i -> i % 2 == 0)
                .peek(System.out::println) // peek should be used for debugging purpose only
                .count();
        System.out.println("Total element: " + countFromArray);
        var countSplit = Arrays.stream("Cong hoa xa hoi chu nghia Viet Nam".split("\\PL+"))
                .peek(System.out::println)
                .count();
        System.out.println("The sentence contains " + countSplit + " words.");

        // creating a stream from a part of an array
        Arrays.stream(myIntArraySource, 0, 3)
                .filter( i -> i > 0)
                .forEach(System.out::println);
    }

    @Test
    public void infinite_stream_creation() {
        var myRandomGenerator = new Random();
        Stream.generate(myRandomGenerator::nextInt)
                .filter(i -> i % 2 == 1)
                .limit(5)
                .forEach(System.out::println);

        Stream.generate(Math::random)
                .filter(i -> {
                    var lVal= Double.valueOf(i * 1000).longValue();
                    return lVal %7 == 0;
                })
                .limit(10)
                .peek(i -> System.out.println(i * 1000))
                .forEach(System.out::println);

        // to produce sequence
        Stream.iterate(100, i -> i + 1)
                .filter(i-> i %5 == 0)
                .limit(100)
                .forEach(System.out::println);

        // to create finite stream
        Stream.iterate(5000, i -> i < 6000, i -> i + 1)
                .filter( i -> i %7 == 0)
                .forEach(System.out::println);
    }

    @Test
    public void stream_of_nullable() {
        var myNullValue = getInput(true);
        var streamOfNullableCount = Stream.ofNullable(myNullValue)
                .count();
        System.out.println("Size of stream of nullable: " + streamOfNullableCount);

        var myNonNullValue = getInput(false);
        var streamOfNotNullCount = Stream.ofNullable(myNonNullValue)
                .count();
        System.out.println("Size of stream of non-null: " + streamOfNotNullCount);
        //Stream.of(null).count(); // avoid this as this is
    }

    @Test
    public void streamFromIteratorNotCollection() {
        var myIterable = getIterableOfString();
        StreamSupport.stream(myIterable.spliterator(), false);
    }

    @Test
    public void streamFromIterator() {
        var myIterator = getIteratorString();
        StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(myIterator, Spliterator.ORDERED), false
        );
    }

    @Test
    public void modifyingTheBackingCollection() {
        var mySource = Arrays.asList(1, 2, 3, 4, 5);
        var myModifiableSource = new ArrayList<>(mySource);

        var myStream = myModifiableSource.stream();
        // theoretically, before the terminal operation is applied, we can mutate the source
        myModifiableSource.add(8);
        myStream.forEach(System.out::println);

        var myOtherStream = myModifiableSource.stream();
        myOtherStream.forEach(i -> {
            if (i %2 ==0) myModifiableSource.remove(i);
        });
    }

    private Iterator<String> getIteratorString() {
        return null;
    }

    private Iterable<String> getIterableOfString() {
        return null;
    }

    private String getInput(boolean aNullValue) {
        return aNullValue ? null : "You definitely don't want a null value";
    }

    private void foo() {
        "This is my string".lines();
    }
}