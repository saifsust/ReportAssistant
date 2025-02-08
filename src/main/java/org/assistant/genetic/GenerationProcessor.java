package org.assistant.genetic;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.assistant.model.Chromosome;
import org.assistant.model.ReportVO;
import org.assistant.reader.ReportReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.assistant.constants.AppConstants.MAX_GENERATION_EVOLUTION;
import static org.assistant.constants.AppConstants.PER_COLUMN_WEIGHT_IN_PERCENTAGE_SCALE;

@RequiredArgsConstructor
public class GenerationProcessor {
    private final Random random = new Random();
    private final ReportReader reader = new ReportReader();
    private final ReportVO reportVO;

    public List<ImmutablePair<String, List<Chromosome>>> getGenerations() {
        List<ImmutablePair<String, List<Chromosome>>> generations = new ArrayList<>();

        List<ImmutablePair<List<List<String>>, List<List<String>>>> actualToExpectedReports = reader.getAllCsvInDirActualToExpectedCsvColumnDataFromAllRows(
                reportVO
        );

        for (int report = 0; report < actualToExpectedReports.size(); report++) {
            ImmutablePair<List<List<String>>, List<List<String>>> actualToExpected = actualToExpectedReports.get(report);

            List<Chromosome> chromosomes = getPopulation(
                    actualToExpected.getRight()
            );

            MutationMaster master = new MutationMaster(actualToExpected.getLeft(), actualToExpected.getRight());

            List<Chromosome> newlyBornChildren = getCrossover(
                    getCrossover(chromosomes)
            );

            int maxRows = actualToExpected.getRight().size();
            long maxColumns = getColumnsCount(actualToExpected.getRight());
            double hundredPercentFittestWeight = PER_COLUMN_WEIGHT_IN_PERCENTAGE_SCALE * ((maxColumns * 1.0 * (maxColumns + 1)) / 2);

            int evolution = 0;
            int firstChildren = 0;
            double bestFitness = 0.0;
            List<Chromosome> bestFittestGenerations = new ArrayList<>();

            System.out.println(newlyBornChildren);
            while (evolution < MAX_GENERATION_EVOLUTION && (bestFittestGenerations.isEmpty() || bestFittestGenerations.get(firstChildren).getFitness() < hundredPercentFittestWeight)) {
                System.out.println(newlyBornChildren);
                List<Chromosome> mutatedChildren = getMutatedChildren(newlyBornChildren, master);

                newlyBornChildren.clear();
                newlyBornChildren.addAll(
                        getBestFittestPopulationToCompareNextColumn(mutatedChildren, maxRows)
                );

                ++evolution;
                double overallFitness = getOverallFitness(newlyBornChildren);

                if (bestFitness < overallFitness) {
                    bestFittestGenerations.clear();
                    bestFittestGenerations.addAll(newlyBornChildren);
                    bestFitness = overallFitness;
                }
            }

            generations.add(new ImmutablePair<>(String.format("report : %d", report), bestFittestGenerations));

        }

        return generations;
    }

    private double getOverallFitness(List<Chromosome> newlyBornChildren) {
        return newlyBornChildren
                .stream()
                .mapToDouble(Chromosome::getFitness)
                .sum();
    }

    private long getColumnsCount(List<List<String>> actualCsvColumnData) {
        return actualCsvColumnData.stream()
                .findFirst()
                .filter(ObjectUtils::isNotEmpty)
                .stream()
                .flatMap(Collection::stream)
                .count();


    }

    private List<Chromosome> getBestFittestPopulationToCompareNextColumn(List<Chromosome> populations, int maxRows) {
        return populations
                .stream()
                .map(child -> buildAndGetNextColumnChild(child, maxRows))
                .toList();
    }

    private Chromosome buildAndGetNextColumnChild(Chromosome chromosome, int maxRows) {
        int column = chromosome.getColumn();
        if (chromosome.getComparedActualRows().size() == maxRows) {
            chromosome.getComparedActualRows().clear();
            return chromosome.toBuilder()
                    .column(column + 1)
                    .build();
        }
        return chromosome;
    }


    // TODO: if rows are not same  between files. need to add that case.
    private List<Chromosome> getPopulation(List<List<String>> expectedCsvData) {
        List<Chromosome> chromosomes = new ArrayList<>();
        for (int row = 0; row < expectedCsvData.size(); row++) {
            chromosomes.add(
                    Chromosome.builder()
                            .expectedCsvRow(row)
                            .actualCsvRow(row)
                            .fitness(0.0)
                            .columnsSimilarityInPercentageScale(new HashSet<>())
                            .comparedActualRows(new HashSet<>())
                            .column(0)
                            .build()
            );
        }

        return chromosomes;
    }

    private List<Chromosome> getMutatedChildren(List<Chromosome> chromosomes, MutationMaster master) {

        Collections.shuffle(chromosomes);
        List<Chromosome> newGenerations = new ArrayList<>();

        for (int generation = 0; generation < chromosomes.size() - 1; generation += 2) {
            Chromosome firstGeneration = chromosomes.get(generation);
            Chromosome secondGeneration = chromosomes.get(generation + 1);

            newGenerations.addAll(
                    master.getCrossMutatedGenerations(firstGeneration, secondGeneration)
            );
        }

        if (chromosomes.size() % 2 == 1) {
            newGenerations.add(chromosomes.get(chromosomes.size() - 1));
        }

        Collections.sort(newGenerations);
        return newGenerations;
    }

    private List<Chromosome> getCrossover(List<Chromosome> chromosomes) {
        Collections.shuffle(chromosomes);

        List<Chromosome> children = new ArrayList<>();

        for (int parent = 0; parent < chromosomes.size() - 1; parent += 2) {
            Chromosome father = chromosomes.get(parent);
            Chromosome mother = chromosomes.get(parent + 1);
            children.addAll(
                    getChildren(father, mother)
            );
        }

        if (chromosomes.size() % 2 == 1) {
            children.add(chromosomes.get(chromosomes.size() - 1));
        }

        return children;
    }

    private List<Chromosome> getChildren(Chromosome male, Chromosome female) {
        return List.of(
                male.toBuilder()
                        .actualCsvRow(female.getActualCsvRow())
                        .build(),
                female.toBuilder()
                        .actualCsvRow(male.getActualCsvRow())
                        .build()
        );
    }
}
