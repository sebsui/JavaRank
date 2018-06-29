package recommendation.model;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import recommendation.data.InputRating;
import recommendation.data.RDDHelper;
import recommendation.exceptions.ModelNotReadyException;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class RecommendationMlModel {

    public static final String SPARK_APP_NAME = "Recommendation Engine";
    public static final String SPARK_MASTER = "local";

    private ALS als = new ALS();
    private MatrixFactorizationModel model;

    private RDDHelper rddHelper = new RDDHelper(new JavaSparkContext(SPARK_MASTER, SPARK_APP_NAME));

    private ReentrantReadWriteLock mutex = new ReentrantReadWriteLock();

    private boolean modelIsReady = false;

    public boolean isModelReady() {
        return modelIsReady;
    }

    public Double getInterestPrediction(Integer userId, Integer eventId) throws ModelNotReadyException {
        if (!modelIsReady)
            throw new ModelNotReadyException();
        mutex.readLock().lock();
        Double prediction = model.predict(userId, eventId);
        mutex.readLock().unlock();
        return prediction;
    }

    public void createModel(Collection<InputRating> ratings) {
        trainModel(ratings);
    }

    private void trainModel(Collection<InputRating> ratings) {
        JavaRDD<Rating> ratingRDD = rddHelper.getRddFromCollection(createSparkRating(ratings)).cache();
        if (ratingRDD.isEmpty())
            return;
        mutex.writeLock().lock();
        model = als.setRank(10).setIterations(10).run(ratingRDD);
        mutex.writeLock().unlock();
        modelIsReady = true;
    }


    private List<Rating> createSparkRating(Collection<InputRating> inputRatings) {
        return inputRatings
                .stream()
                .map(ir -> new Rating(ir.getUserId(), ir.getProductId(), ir.getProductId()))
                .collect(Collectors.toList());
    }
}
