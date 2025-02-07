package org.assistant.app;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.assistant.genetic.GenerationProcessor;
import org.assistant.model.Chromosome;
import org.assistant.model.ReportVO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Hello world!
 */
public class ReportAssistantApp {

    public static void main(String[] args) throws IOException {
        List<ImmutablePair<String, List<Chromosome>>> generations = new GenerationProcessor(ReportVO.builder()
                .actualNamingPattern("*.csv")
                .actualReportsInDir("actualReports")
                .encoding(StandardCharsets.UTF_8.name())
                .expectedNamingPattern("*.csv")
                .expectedReportsInDir("expectedReports")
                .build())
                .getGenerations();

        for (int report = 0; report < generations.size(); report++) {
            ImmutablePair<String, List<Chromosome>> reportNameToSimilarResult = generations.get(report);

            System.out.println(reportNameToSimilarResult.getLeft() + ": ");
            reportNameToSimilarResult.getRight()
                    .forEach(chromosome -> System.out.println(chromosome.getColumnsSimilarityInPercentageScale()));

        }
    }
}
