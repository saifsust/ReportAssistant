package org.assistant.model;


import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(toBuilder = true)
public class Chromosome implements Comparable<Chromosome> {
    private int actualCsvRow;
    private int expectedCsvRow;
    private Integer fitness;
    private List<Double> columnsSimilarityInPercentageScale = new ArrayList<>();

    @Override
    public int compareTo(Chromosome oldGeneration) {
        return this.fitness - oldGeneration.getFitness();
    }
}
