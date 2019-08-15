package ch.sebastianmue.javarank.service;

import ch.sebastianmue.javarank.recommendation.data.InputRating;
import ch.sebastianmue.javarank.recommendation.exceptions.ModelNotReadyException;
import ch.sebastianmue.javarank.recommendation.service.RecommendationService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class RecommendationServiceModelNotReadyTest {


    private static RecommendationService recommendationService;

    @Before
    public void initModel() {
        ArrayList<InputRating> inputRatings = new ArrayList<>();
        inputRatings.add(new InputRating(1, 1, 1));
        recommendationService = new RecommendationService(inputRatings);
    }

    @After
    public void destroyModel() {
        recommendationService.close();
    }


    @AfterClass
    public static void close() {
        recommendationService.close();
    }

    @Test(expected = ModelNotReadyException.class)
    public void shouldNotForgetKnownRating() throws ModelNotReadyException {
        recommendationService.getPrediction(1, 1);
    }

}
