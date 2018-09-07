package service;

import org.junit.BeforeClass;
import org.junit.Test;
import recommendation.data.InputRating;
import recommendation.exceptions.ModelNotReadyException;
import recommendation.service.RecommendationService;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertNotEquals;

/**
 * The testing was keepen simple, as sparkml is already tested. The testing focus is on the new features.
 * This test just verifies, that sparkml was correctly called
 */
public class RecommendationServiceTest {


    private static RecommendationService recommendationService;

    @BeforeClass
    public static void initModel() {
        ArrayList<InputRating> inputRatings = new ArrayList<>();
        inputRatings.add(new InputRating(1, 1, 1));
        inputRatings.add(new InputRating(1, 2, 0));
        inputRatings.add(new InputRating(1, 3, 2));

        inputRatings.add(new InputRating(2, 1, 1));
        recommendationService = new RecommendationService(inputRatings);
        while (!recommendationService.isModelReady()) {
        }
    }

    @Test
    public void shouldNotForgetKnownRating() throws ModelNotReadyException {
        assertThat(recommendationService.getPrediction(1, 2), lessThan(recommendationService.getPrediction(1, 1)));
        assertNotEquals(0.0, recommendationService.getPrediction(1, 1));
    }

    @Test
    public void shouldPredictFromOtherUser() throws ModelNotReadyException {
        assertThat(recommendationService.getPrediction(2, 2), lessThan(recommendationService.getPrediction(1, 3)));
    }

}
