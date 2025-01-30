package org.assistant.app;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.assistant.model.ReportVO;
import org.assistant.reader.ReportReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Hello world!
 */
public class ReportAssistantApp {
    private static final ReportReader REPORT_READER = new ReportReader();

    public static void main(String[] args) throws IOException {

        List<ImmutablePair<List<String>, List<String>>> actualToExpected = REPORT_READER.getActualToExpected(
                ReportVO.builder()
                        .actualNamingPattern("*.csv")
                        .actualReportsInDir("actualReports")
                        .encoding(StandardCharsets.UTF_8.name())
                        .expectedNamingPattern("*.csv")
                        .expectedReportsInDir("expectedReports")
                        .build()
        );

        System.out.println(actualToExpected);

    }
}
