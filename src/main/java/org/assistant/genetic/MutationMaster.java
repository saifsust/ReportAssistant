package org.assistant.genetic;

import lombok.RequiredArgsConstructor;
import org.assistant.model.Chromosome;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class MutationMaster {
    private final List<List<String>> actualCsvAllRowsColumnsData;
    private final List<List<String>> expectedCsvAllRowsColumnsData;

    public List<Chromosome> getCrossMutatedGenerations(Chromosome firstGeneration, Chromosome secondGeneration) {
        if (firstGeneration.getColumn() != secondGeneration.getColumn() || firstGeneration.getComparedActualRows().contains(secondGeneration.getActualCsvRow())) {
            return List.of(firstGeneration, secondGeneration);
        }

        int firstGenerationActualRow = firstGeneration.getActualCsvRow();
        int firstGenerationExpectedRow = firstGeneration.getExpectedCsvRow();

        int secondGenerationActualRow = secondGeneration.getActualCsvRow();
        int secondGenerationExpectedRow = secondGeneration.getExpectedCsvRow();

        double similarityBetweenFirstGenerationActualAndSecondGenerationExpected = FitnessCalculator.getSimilarityInPercentageScaleBetween(
                firstGeneration.getColumn(),
                actualCsvAllRowsColumnsData.get(firstGenerationActualRow),
                expectedCsvAllRowsColumnsData.get(secondGenerationExpectedRow)
        );

        double similarityBetweenSecondGenerationActualAndFirstGenerationExpected = FitnessCalculator.getSimilarityInPercentageScaleBetween(
                secondGeneration.getColumn(),
                actualCsvAllRowsColumnsData.get(secondGenerationActualRow),
                expectedCsvAllRowsColumnsData.get(firstGenerationExpectedRow)
        );

        Set<Double> firstGenerationSimilarity = firstGeneration.getColumnsSimilarityInPercentageScale();
        Set<Double> secondGenerationSimilarity = secondGeneration.getColumnsSimilarityInPercentageScale();

        if (similarityBetweenFirstGenerationActualAndSecondGenerationExpected > 0.0) {
            firstGenerationSimilarity.add(
                    similarityBetweenFirstGenerationActualAndSecondGenerationExpected
            );
        }

        if (similarityBetweenSecondGenerationActualAndFirstGenerationExpected > 0.0) {
            secondGenerationSimilarity.add(similarityBetweenSecondGenerationActualAndFirstGenerationExpected);
        }

        Set<Integer> firstGenerationComparison = firstGeneration.getComparedActualRows();
        Set<Integer> secondGenerationComparison = secondGeneration.getComparedActualRows();

        firstGenerationComparison.add(secondGeneration.getActualCsvRow());
        secondGenerationComparison.add(firstGeneration.getActualCsvRow());

        return List.of(
                firstGeneration.toBuilder()
                        .actualCsvRow(secondGenerationActualRow)
                        .fitness(
                                FitnessCalculator.getFitness(
                                        firstGenerationSimilarity
                                )
                        )
                        .columnsSimilarityInPercentageScale(
                                firstGenerationSimilarity
                        )
                        .comparedActualRows(firstGenerationComparison)
                        .build(),

                secondGeneration.toBuilder()
                        .actualCsvRow(firstGenerationActualRow)
                        .fitness(
                                FitnessCalculator.getFitness(
                                        secondGenerationSimilarity
                                )
                        )
                        .columnsSimilarityInPercentageScale(
                                secondGenerationSimilarity
                        )
                        .comparedActualRows(secondGenerationComparison)
                        .build()
        );
    }

}
