package org.assistant.genetic;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

import static org.assistant.constants.AppConstants.PER_COLUMN_WEIGHT_IN_PERCENTAGE_SCALE;

@UtilityClass
public class FitnessCalculator {

    // TODO: more accurate algorithm will be implemented.
    // TODO: if columns are not same. need to implement that algorithm
    public static List<Double> getSimilarityInPercentageScaleBetween(List<String> actualGenerationColumnData, List<String> expectedGenerationColumnData) {
        List<Double> similarityInPercentageScale = new ArrayList<>();
        for (int column = 0; column < expectedGenerationColumnData.size(); column++) {
            if (expectedGenerationColumnData.get(column).equals(actualGenerationColumnData.get(column))) {
                similarityInPercentageScale.add(PER_COLUMN_WEIGHT_IN_PERCENTAGE_SCALE);
            } else {
                //TODO: more accurate algorithm will be implemented
                similarityInPercentageScale.add(
                        0.0
                );
            }
        }
        return similarityInPercentageScale;
    }

    public static double getFitness(List<Double> similarityInPercentageScaleBetweenActualAndExpectedGeneration) {
        return similarityInPercentageScaleBetweenActualAndExpectedGeneration
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private double lcm(String actualColumn, String expectedColumn) {
        int expected = expectedColumn.length();
        int actual = actualColumn.length();

        int memory[][] = new int[actual + 1][expected + 1];

        for (int i = 0; i <= expected; i++) {
            memory[0][i] = 0;
        }
        for (int i = 0; i <= actual; i++) {
            memory[i][0] = 0;
        }

        for (int i = 1; i <= actual; i++) {
            for (int j = 1; j <= expected; j++) {
                if (actualColumn.charAt(i - 1) == expectedColumn.charAt(j - 1)) {
                    memory[i][j] = memory[i - 1][j - 1] + 1;
                } else {
                    memory[i][j] = Math.max(memory[i - 1][j], memory[i][j - 1]);
                }
            }
        }

        double longestCommonSeq = memory[actual][expected] * 1.00;
        return (longestCommonSeq / actual) * PER_COLUMN_WEIGHT_IN_PERCENTAGE_SCALE;
    }
}
