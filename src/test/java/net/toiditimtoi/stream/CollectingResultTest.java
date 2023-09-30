package net.toiditimtoi.stream;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CollectingResultTest {

    private final List<String> provinces = List.of("Nghe An", "Ha Noi", "Ninh Binh", "Thanh Hoa", "Nam Dinh");

    @Test
    public void traditionalForEach() {
        var provinces = List.of("Nghe An", "Ha Noi", "Ninh Binh", "Thanh Hoa", "Nam Dinh");
        provinces.stream()
                .filter(s -> s.length() > 7)
                .forEach(System.out::println);
    }

    @Test
    public void usingIterator() {
        var finalResultIterator = provinces.stream().filter(s -> s.length() > 7).iterator();
        while (finalResultIterator.hasNext()) {
            System.out.println(finalResultIterator.next());
        }
    }

    @Test
    public void collectingToArray() {
        class Hello {
        }

        Object[] longProvinceName = provinces.stream()
                .filter(s -> s.length() > 8)
                .toArray(); // this is an array of object because during runtime, there is no way to see the generic type of all element in the array

        String[] longProvinceStringName = provinces.stream()
                .filter(s -> s.length() > 8)
                .toArray(String[]::new);

        Hello[] gonnaThrowException = provinces.stream()
                .filter(s -> s.length() > 7)
                .toArray(Hello[]::new);

    }

    @Test
    public void collectElementsIntoAnotherCollection() {
        // there is no guarantee on the actual type, mutability, serializability or thread safety of the List
        var finalResult = provinces.stream().filter(s -> s.length() > 7)
                .collect(Collectors.toList());

        // there is no guarantee on the actual type, mutability, serializability or thread safety of the Set being return
        var setResult = provinces.stream()
                .filter(s -> s.length() > 7)
                .collect(Collectors.toSet());

        var java16ToList = provinces.stream()
                .filter(s -> s.length() > 7)
                .toList();

        //to convert to other collection type, when we want specific characteristic of the result, eg: thread safety, immutability...
        var shouldBeHashSet = provinces.stream()
                .filter(s -> s.length() > 7)
                .collect(Collectors.toCollection(HashSet::new));
        assertTrue(shouldBeHashSet.getClass().isAssignableFrom(HashSet.class));

        var shouldBeConcurrentSkipListSet = provinces.stream()
                .filter(s -> s.length() > 7)
                .collect(Collectors.toCollection(ConcurrentSkipListSet::new));

        assertTrue(shouldBeConcurrentSkipListSet.getClass().isAssignableFrom(ConcurrentSkipListSet.class));
    }

    @Test
    public void joiningString() {
        var joinedString = provinces.stream()
                .filter(s -> s.length() > 7)
                .collect(Collectors.joining(" - "));
        assertEquals("Ninh Binh - Thanh Hoa - Nam Dinh", joinedString);
    }

    @Test
    public void statisticOfNumberStream() {
        // suppose we want to get some statistic of the province name
        IntSummaryStatistics summary = provinces.stream()
                .filter(s -> s.length() > 7)
                .collect(Collectors.summarizingInt(String::length));

        assertEquals(9, summary.getMax());
        assertEquals(8, summary.getMin());
        assertEquals((double) (8 + 9 + 9) / 3, summary.getAverage());
    }

    @Test
    public void collectToMap() {
        Map<String, Character> tmp = provinces.stream()
                .collect(Collectors.toMap(s -> s, s -> s.charAt(0)));

        tmp.forEach((key, val) -> System.out.println("Key: " + key + ", value: " + val));
    }

    @Test
    public void collectWithIdentity() {
        // this will throw an exception because the same key is mapped to more than one value (for letter N)
        assertThrows(Exception.class,
                () -> provinces.stream()
                        .collect(Collectors.toMap(s -> s.charAt(0), s -> s))
        );


    // this does not throw an exception because we provide a resolver function to deal with the collision
        Map<Character, String> firstCharToProvinceName = assertDoesNotThrow(
                () -> provinces.stream()
                        .collect(Collectors.toMap(
                                s -> s.charAt(0),
                                Function.identity(),
                                (existing, newVal) -> String.join(" - ", existing, newVal)
                        ))
        );
        firstCharToProvinceName.forEach((k, v) -> System.out.println("Key: " + k + ", value: " + v));

        // suppose we want the result of a specific type
        Map<Character, String> treeMapFirstCharToProvinceName = assertDoesNotThrow(
                () -> provinces.stream()
                        .collect(Collectors.toMap(
                                s -> s.charAt(0),
                                Function.identity(),
                                (existing, newVal) -> String.join(" - ", existing, newVal),
                                TreeMap::new
                        ))
        );
        assertTrue(treeMapFirstCharToProvinceName.getClass().isAssignableFrom(TreeMap.class));
    }

    /**
     * Quick refresh on Locale
     * a Locale consists of a language and a country code
     * - 1 language may be widely used in many countries
     * - 1 country may have more than 1 official language, hence, a country can have multiple locales
     */

    @Test
    public void grouping() {
        Map<String, List<Locale>> countryAndLocales = Arrays.stream(Locale.getAvailableLocales())
                .collect(Collectors.groupingBy(Locale::getDisplayCountry));
        countryAndLocales.get("Vietnam")
                .forEach(System.out::println);
    }

    // when the classifier is a Predicate, that is returns either true or false, the original stream will be split into
    // two groups. In this case, we better use the partitioningBy function
    @Test
    public void partitioningBy() {
        Map<Boolean, List<Locale>> englishAndOther = Stream.of(Locale.getAvailableLocales())
                .collect(Collectors.partitioningBy(locale -> locale.getLanguage().equals("en")));
        englishAndOther.get(false).forEach(l -> System.out.println(l.getDisplayCountry()));
    }

    /**
     * Downstream collector
     * the previous partitioning by only gives us a single choice of the return collection
     * If we want to process the partitions further, simply supplying a downstream collector
     */

    @Test
    public void partitioningByToAnotherCollectionType() {
        Map<Boolean, Set<Locale>> englishAndOther = Arrays.stream(Locale.getAvailableLocales())
                .collect(Collectors.partitioningBy(locale -> locale.getLanguage().equals("en"), Collectors.toCollection(HashSet::new)));
    }

    enum Area {
        NORTH, SOUTH, MIDDLE, OTHER
    }

    record City(String name, Area area, int population) {}

    City hanoi = new City("Ha Noi", Area.NORTH , 7_000_000);
    City bacNinh = new City("Bac Ninh", Area.NORTH , 1_000_000);
    City hcmc = new City("Ho Chi Minh", Area.SOUTH,  15_000_000);
    City canTho = new City("Can Tho", Area.SOUTH,  3_000_000);

    City daNang= new City("Da Nang", Area.MIDDLE, 2_500_000);
    City vinh = new City("Vinh", Area.MIDDLE, 2_000_000);
    City[] allCities = new City[] { hanoi, bacNinh, hcmc, canTho, vinh, daNang };

    @Test
    public void partitioningAndCounting() {
        Map<Boolean, Long> theNorthAndOthersCount = Arrays.stream(allCities)
                        .collect(Collectors.partitioningBy(
                                city -> city.area() == Area.NORTH,
                                Collectors.counting()
                                )
                        );
        System.out.println("Citi in the north: " + theNorthAndOthersCount.get(true));
        System.out.println("Citi in other areas: " + theNorthAndOthersCount.get(false));
    }

    // count the number of cities in each area
    @Test
    public void groupingAndCounting() {
        Map<Area, Long> numberOfCitiesInEachArea = Stream.of(allCities)
                .collect(Collectors.groupingBy(City::area, Collectors.counting()));
        numberOfCitiesInEachArea.forEach((area, count) -> System.out.println(area + ": " + count));
    }

    // calculating the average population of each Cities the North and of each other

    @Test
    public void partitioningAndAveraging() {
        Map<Boolean, Double> theNorthAndOtherAverage = Arrays.stream(allCities)
                .collect(Collectors.partitioningBy(
                        city -> city.area == Area.NORTH,
                        Collectors.averagingInt(City::population)
                ));
        System.out.println("The average population of cities in the north is: " + theNorthAndOtherAverage.get(true));
        System.out.println("The average population of cities in other areas is: " + theNorthAndOtherAverage.get(false));
    }

    @Test
    public void groupingAndAveraging() {
        Map<Area, Double> areaAndAveragePopulationInEachCity = Stream.of(allCities)
                .collect(Collectors.groupingBy(City::area, Collectors.averagingInt(City::population)));
        areaAndAveragePopulationInEachCity.forEach((area, avgPopInEachCity) -> System.out.println(area + ": " + avgPopInEachCity));
    }

    @Test
    public void partitioningAndMinMaxInEachGroup() {
        Map<Boolean, Optional<City>> minPopulationInEachPartition = Arrays.stream(allCities)
                .collect(Collectors.partitioningBy(
                        city -> city.area == Area.NORTH,
                        Collectors.minBy(Comparator.comparingInt(City::population))
                ));
        System.out.println("City with least population in the North: " + minPopulationInEachPartition.get(true).orElse(null));
        System.out.println("City with least population in other areas: " + minPopulationInEachPartition.get(false).orElse(null));

        Map<Boolean, Optional<City>> maxPopulationInEachPartition = Arrays.stream(allCities)
                .collect(Collectors.partitioningBy(
                        city -> city.area == Area.NORTH,
                        Collectors.maxBy(Comparator.comparingInt(City::population))
                ));

        System.out.println("City with most population in the North: " + maxPopulationInEachPartition.get(true).map(City::name).orElse(""));
        System.out.println("City with most population in other areas: " + maxPopulationInEachPartition.get(false).map(City::name).orElse(""));
    }

    @Test
    public void groupingAndMinMaxInEachGroup() {
        Map<Area, Optional<City>> minPopulationInEachArea = Stream.of(allCities)
                .collect(Collectors.groupingBy(City::area, Collectors.minBy(Comparator.comparingInt(City::population))));
        minPopulationInEachArea.forEach((area, optCity) -> {
            System.out.println("City with least population in the " + area + " is: " + optCity.map(City::name).orElse(""));
        });

        Map<Area, Optional<City>> maxPopInEachArea = Stream.of(allCities)
                .collect(Collectors.groupingBy(City::area, Collectors.maxBy(Comparator.comparingInt(City::population))));

        maxPopInEachArea.forEach((area, optCity) -> {
            System.out.println("City with most population in the " + area + " is: " + optCity.map(City::name).orElse(""));
        });
    }

    // count distinct cities in each group
    @Test
    public void collectingAndThen() {
        var randomCityFromThirdParty = new City("Ha Noi", Area.NORTH, 7_000_000);
        var aggregatedCities = Arrays.copyOf(allCities, allCities.length + 1);
        aggregatedCities[aggregatedCities.length - 1] = randomCityFromThirdParty;
        Map<Boolean, Integer> northAndOtherCountDistinct = Arrays.stream(aggregatedCities)
                .collect(Collectors.partitioningBy(city -> city.area == Area.NORTH,
                        Collectors.collectingAndThen(Collectors.toCollection(HashSet::new), Set::size)
                        ));
        assertEquals(2, northAndOtherCountDistinct.get(true));
        assertEquals(4, northAndOtherCountDistinct.get(false));
        System.out.println("Number of cities in the North: " + northAndOtherCountDistinct.get(true));
        System.out.println("Number of cities in other area: " + northAndOtherCountDistinct.get(false));


        Map<Area, Integer> areaAndCountDistinct = Stream.of(aggregatedCities)
                .collect(Collectors.groupingBy(City::area, Collectors.collectingAndThen(
                        Collectors.toCollection(HashSet::new), HashSet::size
                )));
        areaAndCountDistinct.forEach((area, count) -> {
            System.out.println("Number of cities in " + area + " is: " + count);
        });
    }
    
    @Test
    public void mappingBeforePassingToDownstream() {
        Map<Area, List<Integer>> areaAndCityNameLength = Stream.of(allCities)
                .collect(Collectors.groupingBy(City::area, Collectors.mapping(city -> city.name.length(), Collectors.toCollection(ArrayList::new))));

        // figure out all the languages officially used in every country
        Map<String, Set<String>> countryAndLanguages = Stream.of(Locale.getAvailableLocales())
                .collect(Collectors.filtering(locale -> !locale.getDisplayCountry().isBlank(),
                        Collectors.groupingBy(Locale::getDisplayCountry,
                                Collectors.mapping(locale -> locale.getDisplayLanguage(new Locale("vi")),
                                        Collectors.toSet()
                                )
                        )
                ));

        countryAndLanguages.forEach((country, languages) -> System.out.println(country + " speaks: " + languages));
    }

    @Test
    public void summarizingWithNumber() {
        Map<Area, IntSummaryStatistics> areaAndPopulationStatistic = Stream.of(allCities)
                .collect(Collectors.groupingBy(City::area, Collectors.summarizingInt(City::population)));

        areaAndPopulationStatistic.forEach((area, intSummaryStatistics) -> {
            var summary = """
                    Area: %s
                    Max population: %d
                    Min population: %d
                    Average population: %f
                    """.formatted(area, intSummaryStatistics.getMax(), intSummaryStatistics.getMin(), intSummaryStatistics.getAverage());
            System.out.println(summary);
        });
    }

    // count the number of cities in each region having name longer than 6 characters, also, calculate total population
    // of that area
    record Pair<T, S> (T first, S second) {}

    @Test
    public void multipleOperationsWithTee() {
        Map<Area, Pair<Integer, Long>> regionsLongNameCount = Stream.of(allCities)
                .collect(Collectors.groupingBy(City::area, Collectors.teeing(
                        Collectors.summingInt(City::population),
                        Collectors.filtering(city -> city.name.length() > 6, Collectors.counting()),
                        Pair::new
                )));
        var template = """
                Area: %s
                Total population: %d
                Number of cities with long name: %d
                """;
        regionsLongNameCount.forEach((area, pair) -> System.out.println(template.formatted(area, pair.first, pair.second)));
    }
}
