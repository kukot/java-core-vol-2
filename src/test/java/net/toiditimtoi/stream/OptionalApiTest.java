package net.toiditimtoi.stream;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.stream.Stream;

public class OptionalApiTest {
    @Test
    public void optionalTest() {
        Optional.empty().orElseGet(this::processFurther);
        Optional.empty().orElseThrow();
        Optional.empty().orElseThrow(() -> new IllegalStateException("How come!"));
    }

    private String processFurther() {
        return "";
    }

    private Optional<String> optionallyString() {
        var myNumber = 1 + (int) (Math.random() * 10);
        return myNumber % 2 == 0 ? Optional.of("Even") : Optional.empty();
    }

    @Test
    public void ifPresentTest() {
        var optStr = optionallyString();
        optStr.ifPresent(System.out::println);
    }

    @Test
    public void ifPresentOrElseTest() {
        var optStr = optionallyString();
        optStr.ifPresentOrElse(System.out::println, () -> System.out.println("Nothing"));
    }

    @Test
    public void mappingWithOptional() {
        var optStr = optionallyString();
        optStr
                .map(String::toUpperCase)
                .ifPresent(System.out::println);
    }

    @Test
    public void filteringWithOptional() {
        var optStr = optionallyString();
        optStr
                .filter(s -> s.startsWith("E"))
                .map(s -> s.toUpperCase())
                .ifPresent(System.out::println);
    }

    @Test
    public void optionalOr() {
        var optStr = optionallyString();
        optStr.or(() -> Optional.of("Empty"))
                .map(String::toUpperCase)
                .ifPresent(System.out::println);
    }

    static class MyType {
        int age;
        public Optional<String> ofDescription() {
            return age > 0 ? Optional.of("Born more than a year") : Optional.empty();
        }

        public static Optional<MyType> randomlyGenerating() {
            var myNumber = 1 + (int) (Math.random() * 10);
            if (myNumber %2 == 0) return Optional.of(new MyType());
            return Optional.empty();
        }
    }

    // the flatMap allows chaining optional return type calls
    @Test
    public void composingOptionalTest() {
        var desc = MyType.randomlyGenerating()
                .flatMap(myType -> myType.ofDescription());
        desc.ifPresent(System.out::println);
    }

    @Test
    public void optionalAndStream() {
        var myPresentingOptionals = Stream.generate(() -> MyType.randomlyGenerating())
                .limit(5)
                .flatMap(Optional::stream)
                .toList();
    }
}
