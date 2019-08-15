package ch.sebastianmue.javarank.service;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ch.sebastianmue.javarank.recommendation.data.InputRating;
import ch.sebastianmue.javarank.recommendation.exceptions.ModelNotReadyException;
import ch.sebastianmue.javarank.recommendation.service.RecommendationService;

import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertNotEquals;

public class RecommendationServiceRetrainTest {


    private static RecommendationService recommendationService;

    static boolean firstCall = true;

    private static ArrayList<InputRating> dataProvider() {
        ArrayList<InputRating> inputRatings = new ArrayList<>();
        inputRatings.add(new InputRating(1, 1, 1));
        inputRatings.add(new InputRating(1, 2, 0));
        inputRatings.add(new InputRating(1, 3, 2));
        inputRatings.add(new InputRating(2, 1, 1));
        if (firstCall) {
            inputRatings.add(new InputRating(1, 3, 0));
            firstCall = false;
        }
        return inputRatings;
    }

    @BeforeClass
    public static void initModel() {
        recommendationService = new RecommendationService(() -> dataProvider(), 10, 0);
        while (!recommendationService.isModelReady()) {
        }
    }

    @AfterClass
    public static void close() {
        recommendationService.close();
    }

    @Test
    public void shouldDeliverANewPredictionAfterTraining() throws ModelNotReadyException {
        Optional<Double> firstPrediction = recommendationService.getPrediction(2, 3);
        while (recommendationService.getModelNumber() == 1) {
        }
        Optional<Double> secondPrediction = recommendationService.getPrediction(2, 3);
        assertNotEquals(firstPrediction.get(), secondPrediction.get());
        assertThat(firstPrediction.get(), lessThan(secondPrediction.get()));
    }
}
