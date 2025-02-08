package org.assistant.model;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@ToString
@Builder(toBuilder = true)
public class Chromosome implements Comparable<Chromosome> {
    private Double fitness;
    private int expectedCsvRow;
    private int actualCsvRow;
    private int column;
    private Set<Integer> comparedActualRows = new HashSet<>();
    private Set<Double> columnsSimilarityInPercentageScale = new HashSet<>();

    @Override
    public int compareTo(Chromosome oldGeneration) {
        return this.fitness.intValue() - oldGeneration.getFitness().intValue();
    }
}
