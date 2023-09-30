package net.toiditimtoi.stream;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TransformationTest {

    @Test
    public void testFlatMap() {
        Arrays.stream("Toi la nguoi Viet Nam"
                        .split("\\PL"))
                .flatMap(this::fromStringToCharStream)
                .forEach(System.out::println);
    }

    @Test
    public void funWithCodePoint() {
        var src = "Xin Chào Việt Nam 🌐";
        Pattern.compile("\\PL")
                .splitAsStream(src)
                .peek(System.out::println)
                .flatMap(s -> s.codePoints().boxed())
                .forEach(System.out::println);
        Arrays.stream(src.split(" "))
                .peek(System.out::println)
                .flatMap(s -> s.codePoints().boxed())
                .forEach(System.out::println);

        System.out.println(src);
    }

    @Test
    public void playWithUnicodeIcon() {
        var src = "Hello, I am from Viet Nam 🌐";
        src.codePoints()
                .mapToObj(cp -> new String(new int[]{cp}, 0, 1))
                .forEach(System.out::println);
    }

    @Test
    public void codePointToString() {
        var myCodePoints = new int[]{222, 293, 369, 269};
        var stringRes = new String(myCodePoints, 0, 4);
        System.out.println(stringRes);
    }

    @Test
    public void mapMultiTest() {
        var src = "My name is Phuc, I am from Viet Nam";
        List<String> words = Arrays.asList(src.split("\\PL"));
        var anotherRes = words.stream()
                .mapMulti((s, collector) -> {
                    int i = 0;
                    while (i < s.length()) {
                        var codePoint = s.codePointAt(i);
                        collector.accept(codePoint);
                        if (Character.isSupplementaryCodePoint(codePoint)) {
                            i += 2;
                        } else {
                            i++;
                        }
                    }
                });
        anotherRes.forEach(System.out::println);
    }

    private Stream<Character> fromStringToCharStream(String s) {
        return s.chars()
                .mapToObj(c -> (char) c);
    }

    @Test
    public void limitStream() {
        var provinces = List.of("Nghe An", "Ha Noi", "Ninh Binh", "Thanh Hoa", "Nam Dinh", "Tay Ninh", "Dong Nai");
        provinces.stream()
                .limit(3)
                .forEach(System.out::println);// print the first 3 elements

        provinces.stream().skip(2) // skip the first 2 elements and work with the rest
                .forEach(System.out::println);
    }

    @Test
    public void takeWhileAndStop() {
        var provinces = List.of("Nghe An", "NHa Noi", "Ninh Binh", "Thanh Hoa", "Nam Dinh", "Tay Ninh", "Dong Nai");
        provinces.stream()
                .takeWhile(s -> s.startsWith("T")) // collect element until seeing one that starts with letter T then stop
                .forEach(System.out::println);
    }

    @Test
    public void dropWhileTest() {
        var provinces = List.of("Nghe An", "Ha Noi", "Ninh Binh", "Thanh Hoa", "Nam Dinh", "Tay Ninh", "Dong Nai");
        provinces.stream().dropWhile(s -> s.startsWith("T"))
                .forEach(System.out::println);
    }

    @Test
    public void concatenationTest() {
        var myFirstStream = List.of("A", "B", "C").stream();
        var mySecondStream = List.of("D", "E", "F").stream();
        var myCombinedStream = Stream.concat(myFirstStream, mySecondStream);
        myCombinedStream.forEach(System.out::println);
    }

    @Test
    public void distinctTest() {
        var myString = List.of("A", " ", "B", "A", "C", "A", "D", "A");
        myString
                .stream()
                .filter(s -> !s.isBlank())
                .distinct()
                .forEach(System.out::println);
    }

    @Test
    public void sortingTest() {
        var myString = List.of("A", "E", "Z", " ", "B", "A", "C", "A", "DE", "A", "AF");
        // sorting using natural ordering
        myString.stream()
                .sorted() // this class forms the item in this stream must implement Comparable (natural ordering)
                .forEach(System.out::println);

        // sorting using custom ordering
        myString.stream()
                .sorted(Comparator.comparingInt(String::length))
                .forEach(System.out::println);

        myString.stream()
                .sorted(Comparator.comparing(String::length))
                .forEach(System.out::println);
    }
}
