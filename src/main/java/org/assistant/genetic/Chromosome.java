package org.assistant.genetic;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assistant.constants.AppConstants.*;

@RequiredArgsConstructor
public class Chromosome {
    private final int ROW_FITNESS_LENGTH = 2 * ROW_SLOT + FITNESS_SLOT;
    private final String[] chromosome = new String[ROW_FITNESS_LENGTH];
    private final String prevChromosome;


    public int getCsvRowFitness() {
        return mapToInt(prevChromosome.substring(2 * ROW_SLOT).split(""));
    }

    public int getExpectedCsvRow() {
        return mapToInt(prevChromosome.substring(ROW_SLOT, ROW_SLOT).split(""));
    }

    public int getActualCsvRow() {
        return mapToInt(prevChromosome.substring(0, ROW_SLOT).split(""));
    }

    public int mapToInt(String digits[]) {
        AtomicInteger value = new AtomicInteger(0);
        Arrays.stream(digits)
                .mapToInt(Integer::parseInt)
                .forEach(item -> {
                    int mostSignificantDigit = value.get() * 10;
                    value.set(
                            mostSignificantDigit + value.get()
                    );
                });
        return value.get();
    }
}
