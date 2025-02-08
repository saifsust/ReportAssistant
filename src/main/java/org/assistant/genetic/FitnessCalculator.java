package org.assistant.genetic;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Set;

import static org.assistant.constants.AppConstants.PER_COLUMN_WEIGHT_IN_PERCENTAGE_SCALE;

@UtilityClass
public class FitnessCalculator {

    // TODO: more accurate algorithm will be implemented.
    // TODO: if columns are not same. need to implement that algorithm
    public static double getSimilarityInPercentageScaleBetween(
            int column,
            List<String> actualGenerationColumnData,
            List<String> expectedGenerationColumnData
    ) {
        if (expectedGenerationColumnData.get(column).equals(actualGenerationColumnData.get(column))) {
            return (column + 1) * PER_COLUMN_WEIGHT_IN_PERCENTAGE_SCALE;
        }
        return 0.0;
    }

    public static double getFitness(Set<Double> similarityInPercentageScaleBetweenActualAndExpectedGeneration) {
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
