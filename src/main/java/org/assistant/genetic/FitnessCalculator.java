package org.assistant.genetic;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

import static org.assistant.constants.AppConstants.PER_COLUMN_WEIGHT_IN_PERCENTAGE_SCALE;

@UtilityClass
public class FitnessCalculator {

    // TODO: more accurate algorithm will be implemented.
    public static List<Double> getSimilarityInPercentageScaleBetween(List<String> actualGenerationColumnData, List<String> expectedGenerationColumnData) {
        List<Double> similarityInPercentageScale = new ArrayList<>();
        for (int column = 0; column < actualGenerationColumnData.size(); column++) {
            if (actualGenerationColumnData.get(column).equals(expectedGenerationColumnData.get(column))) {
                similarityInPercentageScale.add(PER_COLUMN_WEIGHT_IN_PERCENTAGE_SCALE);
            } else {
                //TODO: more accurate algorithm will be implemented
                similarityInPercentageScale.add(0.0);
            }
        }
        return similarityInPercentageScale;
    }

    public static int getFitness(List<Double> similarityInPercentageScaleBetweenActualAndExpectedGeneration) {
        return similarityInPercentageScaleBetweenActualAndExpectedGeneration
                .stream()
                .mapToInt(Double::intValue)
                .sum();
    }
}
