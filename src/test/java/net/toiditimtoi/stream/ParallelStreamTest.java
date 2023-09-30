package net.toiditimtoi.stream;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelStreamTest {
    record City(String name, Area area, int population) {}

    enum Area {
        MIDDLE, SOUTH, NORTH
    }

    City hanoi = new City("Ha Noi", Area.NORTH , 7_000_000);

    City hue = new City("Hua", Area.MIDDLE, 1_500_000);
    City bacNinh = new City("Bac Ninh", Area.NORTH , 1_000_000);
    City hcmc = new City("Ho Chi Minh", Area.SOUTH,  15_000_000);

    City hauGiang = new City("Hau Giang", Area.SOUTH,  1_000_000);
    City daNang= new City("Da Nang", Area.MIDDLE, 2_500_000);
    City vinh = new City("Vinh", Area.MIDDLE, 2_000_000);

    City canTho = new City("Can Tho", Area.SOUTH,  3_000_000);
    List<City> allCities = List.of(hanoi, hue, bacNinh, hcmc, daNang, canTho, vinh, hauGiang);

    @Test
    public void parallelStreamCreationTest() {
        var aParallelStream = allCities.parallelStream();
        aParallelStream.forEach(System.out::println);

        var anotherParallelStr = allCities.stream().parallel();
        anotherParallelStr.forEach(System.out::println);
    }
    
    @Test
    public void sequentialParallelBackAndForth() {
        var sequentialFirst = allCities.stream();
        sequentialFirst
                .parallel()
                .filter(city -> city.area() == Area.MIDDLE)
                .sequential() // the last one to switch between the two wins
                .map(city -> city.name)
                .forEach(System.out::println);
    }

    /**
     * A parallel stream will arrange the execution on multiple thread
     * It is the developer to ensure that the operations inside the terminal operation are thread-safe
     */
    @Test
    public void parallelStreamAndConcurrentSafetyTest() {
        Map<Area, Integer> areaAndShortNameCity = new HashMap<>();
        allCities.parallelStream()
                .forEach(city -> {
                    if (city.name.length() < 6) {
                        areaAndShortNameCity.merge(city.area, 1, Integer::sum);
                    }
                }); // this is not thread-safe and should be avoided
        areaAndShortNameCity.forEach((a, c) -> System.out.println("Area: " + a + ", count: " + c));
    }

    @Test
    public void parallelStreamSafetyManner() {
        Map<Area, Long> areaAndShortNameCity = allCities.parallelStream()
                .collect(Collectors.groupingBy(City::area, Collectors.filtering(city -> city.name.length() < 6, Collectors.counting())));
        areaAndShortNameCity.forEach((a, c) -> System.out.println("Area: " + a + ", count: " + c));
    }
    
    @Test
    public void orderingAndEfficiency() {
        var myOriginalCities = new ArrayList<>(allCities);
        myOriginalCities.add(hanoi);
        myOriginalCities.add(3, vinh);
        myOriginalCities.add(hcmc);
        var distinctCities = myOriginalCities.stream().unordered().distinct().collect(Collectors.toList());
        System.out.println(distinctCities);

        myOriginalCities.stream().unordered().limit(3)
                .forEach(System.out::println);
    }

    @Test
    public void runningWithACustomPool() {
        ForkJoinPool levelTwoParaForkJoinPool = new ForkJoinPool(5);
        IntStream myStream = IntStream.rangeClosed(1, 100);
        levelTwoParaForkJoinPool.submit(() ->
                myStream.parallel().forEach(i -> System.out.println(i + " using thread: " + Thread.currentThread().getName()))
        );
    }

    // grouping concurrently means that the order of the downstream will be non-deterministic
    @Test
    public void downStreamGroupingByConcurrent() {
        Map<Area, Long> areaAndCityCount = allCities.parallelStream()
                .collect(Collectors.groupingByConcurrent(City::area, Collectors.counting()));
    }

}
