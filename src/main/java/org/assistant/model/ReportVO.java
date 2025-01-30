package org.assistant.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportVO {
    private String actualNamingPattern;
    private String expectedNamingPattern;
    private String actualReportsInDir;
    private String expectedReportsInDir;
    private String encoding;
}
