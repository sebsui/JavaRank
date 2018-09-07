package recommendation.data;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Used to create create JavaRDDs from collections
 */
public class RDDHelper {

    private static JavaSparkContext jsc;

    public RDDHelper(JavaSparkContext javaSparkContext) {
        jsc = javaSparkContext;
    }

    /**
     * Converts a collection to a JavaRDD
     * @param collection
     * @return The JavaRDD from the same generic type as the provided input
     */
    public <T> JavaRDD<T> getRddFromCollection(Collection<T> collection) {
        return jsc.parallelize(collection.stream().parallel().collect(Collectors.toList())).cache();
    }

    /**
     * Converts a collection(mapped the values before) to a JavaRDD
     * @param collection
     * @param mapper ( i.e. integer -> integer.toString() if you want to get a String RDD from a Integerlist)
     * @return The JavaRDD from the same generic type as the provided input
     */
    public <T> JavaRDD<T> getRddFromCollection(Collection<?> collection, Function<? super Object, ? extends T> mapper) {
        return getRddFromCollection(collection.stream().parallel().map(mapper).collect(Collectors.toList()));
    }
}
