package org.assistant.genetic;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.assistant.model.Chromosome;
import org.assistant.model.ReportVO;
import org.assistant.reader.ReportReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

            List<Chromosome> chromosomes = getChromosomes(actualToExpected.getLeft());
            List<Chromosome> newBornChildren = getSortedCrossedChildren(chromosomes);

            MutationMaster master = new MutationMaster(actualToExpected.getLeft(), actualToExpected.getRight());

            List<Chromosome> hundredPercentFittestChildren = new ArrayList<>();

            int noHundredPercentFittestChild = newBornChildren.size() - 1;
            double hundredPercentFittestWeight = PER_COLUMN_WEIGHT_IN_PERCENTAGE_SCALE * getColumnsCount(actualToExpected.getLeft());

            while (!newBornChildren.isEmpty()) {
                if (newBornChildren.get(noHundredPercentFittestChild).getFitness() == hundredPercentFittestWeight) {
                    hundredPercentFittestChildren.add(
                            newBornChildren.remove(noHundredPercentFittestChild)
                    );
                    --noHundredPercentFittestChild;
                }

                List<Chromosome> mutatedChildren = getMutatedChildren(newBornChildren, master);

                newBornChildren.clear();

                newBornChildren.addAll(
                        mutatedChildren
                );
            }

            generations.add(new ImmutablePair<>(String.format("report : %d", report), hundredPercentFittestChildren));

        }

        return generations;
    }

    private long getColumnsCount(List<List<String>> actualCsvColumnData) {
        return actualCsvColumnData.stream()
                .findFirst()
                .filter(ObjectUtils::isNotEmpty)
                .stream()
                .count();


    }

    // TODO: if rows are not same  between files. need to add that case.
    private List<Chromosome> getChromosomes(List<List<String>> actualCsvColumnData) {
        List<Chromosome> chromosomes = new ArrayList<>();
        for (int column = 0; column < actualCsvColumnData.size(); column++) {
            chromosomes.add(
                    Chromosome.builder()
                            .expectedCsvRow(column)
                            .actualCsvRow(column)
                            .fitness(0)
                            .columnsSimilarityInPercentageScale(new ArrayList<>())
                            .build()
            );
        }

        return chromosomes;
    }

    private List<Chromosome> getMutatedChildren(List<Chromosome> chromosomes, MutationMaster master) {
        if (chromosomes.size() == 1) {
            return chromosomes;
        }

        List<Chromosome> children = new ArrayList<>();
        for (int generation = 0; generation < chromosomes.size(); generation++) {
            Chromosome firstGeneration = chromosomes.get(random.nextInt(chromosomes.size()));
            Chromosome secondGeneration = chromosomes.get(random.nextInt(chromosomes.size()));
            children.addAll(
                    master.getCrossMutatedGenerations(firstGeneration, secondGeneration)
            );
        }
        return children;
    }

    private List<Chromosome> getSortedCrossedChildren(List<Chromosome> chromosomes) {
        List<Chromosome> children = new ArrayList<>();

        for (int generation = 0; generation < chromosomes.size(); generation++) {
            Chromosome male = chromosomes.get(random.nextInt(chromosomes.size()));
            Chromosome female = chromosomes.get(random.nextInt(chromosomes.size()));
            children.addAll(
                    getCrossBetween(male, female)
            );
        }

        Collections.sort(children);

        return children;
    }

    private List<Chromosome> getCrossBetween(Chromosome male, Chromosome female) {
        int firstGenerationExpectedRow = male.getExpectedCsvRow();
        int secondGenerationExpectedRow = female.getExpectedCsvRow();

        return List.of(
                male.toBuilder()
                        .expectedCsvRow(secondGenerationExpectedRow)
                        .build(),

                female.toBuilder()
                        .expectedCsvRow(firstGenerationExpectedRow)
                        .build()
        );
    }


}
