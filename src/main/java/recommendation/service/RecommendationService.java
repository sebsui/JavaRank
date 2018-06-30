package recommendation.service;

import recommendation.data.InputRating;
import recommendation.exceptions.ModelNotReadyException;
import recommendation.model.RecommendationMlModel;

import java.util.Collection;
import java.util.concurrent.Callable;

public class RecommendationService {

    RecommendationMlModel recommendationMlModel = new RecommendationMlModel();

    public RecommendationService(Collection<InputRating> ratings) {
        recommendationMlModel.createModelOnce(ratings);
    }

    public RecommendationService(Callable<Collection<InputRating>> ratings) throws Exception {
        recommendationMlModel.createModel(ratings);
    }

    public boolean isModelReady() {
        return recommendationMlModel.isModelReady();
    }

    public Double getPrediction(Integer userId, Integer productId) throws ModelNotReadyException {
        return recommendationMlModel.getInterestPrediction(userId, productId);
    }


}
