package org.assistant.model;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@Builder(toBuilder = true)
public class Chromosome implements Comparable<Chromosome> {
    private Double fitness;
    private int expectedCsvRow;
    private int actualCsvRow;
    private List<Double> columnsSimilarityInPercentageScale = new ArrayList<>();

    @Override
    public int compareTo(Chromosome oldGeneration) {
        return this.fitness.intValue() - oldGeneration.getFitness().intValue();
    }
}
