package ch.javarank.recommendation.service;

import ch.javarank.recommendation.data.InputRating;
import ch.javarank.recommendation.exceptions.ModelNotReadyException;
import ch.javarank.recommendation.model.RecommendationMlModel;

import java.util.Collection;
import java.util.concurrent.Callable;

public class RecommendationService {

    private final RecommendationMlModel recommendationMlModel;

    public RecommendationService(Collection<InputRating> ratings) {
        recommendationMlModel = new RecommendationMlModel(ratings);
    }

    public RecommendationService(Callable<Collection<InputRating>> ratings, long retrainTime, long initialDelay) {
        recommendationMlModel = new RecommendationMlModel(ratings, retrainTime, initialDelay);
    }

    public boolean isModelReady() {
        return recommendationMlModel.isModelReady();
    }

    public Double getPrediction(Integer userId, Integer productId) throws ModelNotReadyException {
        return recommendationMlModel.getInterestPrediction(userId, productId);
    }

    public void close() {
        recommendationMlModel.close();
    }

    /**
     * returns the current version of the model (+1 for each new model)
     */
    public Integer getModelNumber() {
        return recommendationMlModel.getModelNumber();
    }
}
