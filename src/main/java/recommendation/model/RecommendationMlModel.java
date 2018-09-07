package recommendation.model;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import recommendation.data.InputRating;
import recommendation.data.RDDHelper;
import recommendation.exceptions.ErrorInDataSourceException;
import recommendation.exceptions.ModelNotReadyException;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class RecommendationMlModel {

    private static final String SPARK_APP_NAME = "Recommendation Engine";
    private static final String SPARK_MASTER = "local";
    private static final long RETRAIN_TIME_IN_SECONDS = 20;
    private static final long INITIAL_DELAY_IN_SECONDS = 20;

    private ALS als = new ALS();
    private MatrixFactorizationModel model;
    private ReentrantLock trainingLock = new ReentrantLock();
    private JavaSparkContext javaSparkContext = new JavaSparkContext(SPARK_MASTER, SPARK_APP_NAME);


    private RDDHelper rddHelper = new RDDHelper(javaSparkContext);

    private ReentrantReadWriteLock mutex = new ReentrantReadWriteLock();

    private volatile boolean modelIsReady = false;


    public RecommendationMlModel(Callable<Collection<InputRating>> inputRatings) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> asyncTrainModel(inputRatings), INITIAL_DELAY_IN_SECONDS, RETRAIN_TIME_IN_SECONDS, TimeUnit.SECONDS);
    }

    public RecommendationMlModel(Collection<InputRating> inputRatings) {
        asyncTrainModel(inputRatings);
    }

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

    public void close() {
        javaSparkContext.close();
    }

    private void asyncTrainModel(Callable<Collection<InputRating>> inputRatings) {
        try {
            asyncTrainModel(inputRatings.call());
        } catch (Exception e) {
            throw new ErrorInDataSourceException(e);
        }
    }


    private void asyncTrainModel(Collection<InputRating> inputRatings) {
        Thread thread = new Thread(() -> {
            if (trainingLock.isLocked())
                return;
            trainingLock.lock();
            trainModel(inputRatings);
            trainingLock.unlock();
        });
        thread.start();
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
                .map(ir -> new Rating(ir.getUserId(), ir.getProductId(), ir.getRating()))
                .collect(Collectors.toList());
    }
}
