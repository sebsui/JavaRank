package recommendation.data;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RDDHelper {

    private static JavaSparkContext jsc;

    public RDDHelper(JavaSparkContext javaSparkContext) {
        jsc = javaSparkContext;
    }

    public <T> JavaRDD<T> getRddFromCollection(Collection<T> collection) {
        return jsc.parallelize(collection.stream().parallel().collect(Collectors.toList())).cache();
    }

    public <T> JavaRDD<T> getRddFromCollection(Collection<?> collection, Function<? super Object, ? extends T> mapper) {
        return getRddFromCollection(collection.stream().parallel().map(mapper).collect(Collectors.toList()));
    }
}
