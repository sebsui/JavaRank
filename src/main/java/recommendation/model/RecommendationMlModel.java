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

/**
 * Model Class which provides the predictions
 */
public class RecommendationMlModel {

    private static final String SPARK_APP_NAME = "Recommendation Engine";
    private static final String SPARK_MASTER = "local";
    private final ReentrantLock trainingLock = new ReentrantLock();
    private final ReentrantReadWriteLock mutex = new ReentrantReadWriteLock();
    private final JavaSparkContext javaSparkContext = new JavaSparkContext(SPARK_MASTER, SPARK_APP_NAME);
    private final ALS als = new ALS();
    private final RDDHelper rddHelper = new RDDHelper(javaSparkContext);
    private MatrixFactorizationModel model;

    private volatile Integer modelNumber = 0;
    private volatile boolean modelIsReady = false;

    /**
     * Constructor, which allows to retrain and replace the model.
     * The given Callable will be used to get the data for the model.
     *
     * @param inputRatings callable to get the Input ratings for the Model
     * @param retrainTime  Time to wait before training a new model
     * @param initialDelay Time to wait before training the first model
     */
    public RecommendationMlModel(Callable<Collection<InputRating>> inputRatings, long retrainTime, long initialDelay) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> asyncTrainModel(inputRatings), initialDelay, retrainTime, TimeUnit.SECONDS);
    }

    /**
     * Constructor, which trains the model once. provide a callable, if you want your model to improve periodically.
     *
     * @param inputRatings
     */
    public RecommendationMlModel(Collection<InputRating> inputRatings) {
        asyncTrainModel(inputRatings);
    }

    /**
     * @return true if a model is ready. Just can be false, if the module was new init and the first model is not ready yet.
     */
    public boolean isModelReady() {
        return modelIsReady;
    }

    /**
     * Provides a prediction for the given parameters
     *
     * @param userId
     * @param eventId
     * @return the prediction, which rating the used is likely to give
     * @throws ModelNotReadyException
     */
    public Double getInterestPrediction(Integer userId, Integer eventId) throws ModelNotReadyException {
        if (!modelIsReady)
            throw new ModelNotReadyException();
        mutex.readLock().lock();
        Double prediction = model.predict(userId, eventId);
        mutex.readLock().unlock();
        return prediction;
    }

    /**
     * Close the sparkContext and set the resources free
     */
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
        modelNumber++;
        modelIsReady = true;

    }


    private List<Rating> createSparkRating(Collection<InputRating> inputRatings) {
        return inputRatings
                .stream()
                .map(ir -> new Rating(ir.getUserId(), ir.getProductId(), ir.getRating()))
                .collect(Collectors.toList());
    }

    public Integer getModelNumber() {
        return modelNumber;
    }
}
