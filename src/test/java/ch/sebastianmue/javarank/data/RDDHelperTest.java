package ch.sebastianmue.javarank.data;

import org.apache.commons.collections.CollectionUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.AfterClass;
import org.junit.Test;
import ch.sebastianmue.javarank.recommendation.data.RDDHelper;

import javax.management.Query;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertTrue;

public class RDDHelperTest {

    private static final JavaSparkContext jsc = new JavaSparkContext("local", "testjsc");
    private final RDDHelper rddHelper = new RDDHelper(jsc);

    @AfterClass
    public static void close() {
        jsc.close();
    }

    @Test
    public void shouldCreateRDDFromCollection() {
        List<Integer> intList = IntStream.range(0, 100).boxed().collect(Collectors.toList());
        JavaRDD intRDD = rddHelper.getRddFromCollection(intList);
        assertTrue(CollectionUtils.isEqualCollection(intRDD.collect(), intList));
        assertThat(intRDD, instanceOf(JavaRDD.class));
    }

    @Test
    public void shouldCreateRDDFromListWithConverter() {
        List<Integer> intList = IntStream.range(0, 3).boxed().collect(Collectors.toList());
        JavaRDD stringRDD = rddHelper.getRddFromCollection(intList, Object::toString);
        assertThat(stringRDD, instanceOf(JavaRDD.class));
        assertTrue(CollectionUtils.isEqualCollection(stringRDD.collect(), Arrays.asList("0", "1", "2")));
    }
    @Test
    public void shouldCreateRDDFromSetWithConverter() {
        Set<Integer> intList = IntStream.range(0, 3).boxed().collect(Collectors.toSet());
        JavaRDD stringRDD = rddHelper.getRddFromCollection(intList, Object::toString);
        assertThat(stringRDD, instanceOf(JavaRDD.class));
        assertTrue(CollectionUtils.isEqualCollection(stringRDD.collect(), Arrays.asList("0", "1", "2")));
    }
    @Test
    public void shouldCreateRDDFromSetWithEmptySet() {
        Set<Integer> intList = new HashSet<>();
        JavaRDD stringRDD = rddHelper.getRddFromCollection(intList, Object::toString);
        assertThat(stringRDD, instanceOf(JavaRDD.class));
        assertTrue(stringRDD.isEmpty());
    }
    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerOnNull() {
        List<Integer> intList = null;
        JavaRDD stringRDD = rddHelper.getRddFromCollection(intList, Object::toString);
    }
}
