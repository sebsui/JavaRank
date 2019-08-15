package ch.sebastianmue.javarank.service;

import org.junit.BeforeClass;
import org.junit.Test;
import ch.sebastianmue.javarank.recommendation.data.InputRating;
import ch.sebastianmue.javarank.recommendation.exceptions.ModelNotReadyException;
import ch.sebastianmue.javarank.recommendation.service.RecommendationService;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * The testing was keepn simple, as sparkml is already tested. The testing focus is on the new features.
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
        assertThat(recommendationService.getPrediction(1, 2).get(), lessThan(recommendationService.getPrediction(1, 1).get()));
        assertNotEquals(0.0, recommendationService.getPrediction(1, 1));
    }

    @Test
    public void shouldPredictFromOtherUser() throws ModelNotReadyException {
        assertThat(recommendationService.getPrediction(2, 2).get(), lessThan(recommendationService.getPrediction(1, 3).get()));
    }
    @Test
    public void shouldHandleUnknownUser() throws ModelNotReadyException {
        assertTrue(recommendationService.getPrediction(3,2).isEmpty());
    }

    @Test
    public void shouldHandleUnknownProduct() throws ModelNotReadyException {
        assertTrue(recommendationService.getPrediction(2,4).isEmpty());
    }

}
