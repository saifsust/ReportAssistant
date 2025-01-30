package org.assistant.app;

import org.assistant.reader.ReportReader;

import java.io.IOException;

/**
 * Hello world!
 */
public class ReportAssistantApp {
    private static final ReportReader REPORT_READER = new ReportReader();

    public static void main(String[] args) throws IOException {
           REPORT_READER.getReports("*.csv", "actualReports");
    }
}
