package ch.javarank.service;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ch.javarank.recommendation.data.InputRating;
import ch.javarank.recommendation.exceptions.ModelNotReadyException;
import ch.javarank.recommendation.service.RecommendationService;

import java.util.ArrayList;

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
        Double firstPrediction = recommendationService.getPrediction(2, 3);
        while (recommendationService.getModelNumber() == 1) {
        }
        Double secondPrediction = recommendationService.getPrediction(2, 3);
        assertNotEquals(firstPrediction, secondPrediction);
        assertThat(firstPrediction, lessThan(secondPrediction));
    }
}
