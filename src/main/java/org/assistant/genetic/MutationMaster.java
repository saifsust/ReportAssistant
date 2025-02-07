package org.assistant.genetic;

import lombok.RequiredArgsConstructor;
import org.assistant.model.Chromosome;

import java.util.List;

@RequiredArgsConstructor
public class MutationMaster {
    private final List<List<String>> actualCsvAllRowsColumnsData;
    private final List<List<String>> expectedCsvAllRowsColumnsData;

    public List<Chromosome> getCrossMutatedGenerations(Chromosome firstGeneration, Chromosome secondGeneration) {
        int firstGenerationActualRow = firstGeneration.getActualCsvRow();
        int firstGenerationExpectedRow = firstGeneration.getExpectedCsvRow();

        int secondGenerationActualRow = secondGeneration.getActualCsvRow();
        int secondGenerationExpectedRow = secondGeneration.getExpectedCsvRow();

        List<Double> similarityBetweenFirstGenerationActualAndSecondGenerationExpected = FitnessCalculator.getSimilarityInPercentageScaleBetween(
                actualCsvAllRowsColumnsData.get(firstGenerationActualRow),
                expectedCsvAllRowsColumnsData.get(secondGenerationExpectedRow)
        );

        List<Double> similarityBetweenSecondGenerationActualAndFirstGenerationExpected = FitnessCalculator.getSimilarityInPercentageScaleBetween(
                actualCsvAllRowsColumnsData.get(secondGenerationActualRow),
                expectedCsvAllRowsColumnsData.get(firstGenerationExpectedRow)
        );

        return List.of(
                firstGeneration.toBuilder()
                        .actualCsvRow(secondGenerationActualRow)
                        .fitness(
                                FitnessCalculator.getFitness(
                                        similarityBetweenFirstGenerationActualAndSecondGenerationExpected
                                )
                        )
                        .columnsSimilarityInPercentageScale(
                                similarityBetweenFirstGenerationActualAndSecondGenerationExpected
                        )
                        .build(),

                secondGeneration.toBuilder()
                        .actualCsvRow(firstGenerationActualRow)
                        .fitness(
                                FitnessCalculator.getFitness(
                                        similarityBetweenSecondGenerationActualAndFirstGenerationExpected
                                )
                        )
                        .columnsSimilarityInPercentageScale(
                                similarityBetweenSecondGenerationActualAndFirstGenerationExpected
                        )
                        .build()
        );
    }

}
