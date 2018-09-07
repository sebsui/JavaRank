package recommendation.service;

import recommendation.data.InputRating;
import recommendation.exceptions.ModelNotReadyException;
import recommendation.model.RecommendationMlModel;

import java.util.Collection;
import java.util.concurrent.Callable;

public class RecommendationService {

    private RecommendationMlModel recommendationMlModel;

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


}
