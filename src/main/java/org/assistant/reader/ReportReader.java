package org.assistant.reader;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.assistant.model.ReportVO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assistant.constants.AppConstants.RESOURCE_SUB_PATH;

public class ReportReader {

    public List<ImmutablePair<List<List<String>>, List<List<String>>>> getAllCsvInDirActualToExpectedCsvColumnDataFromAllRows(
            ReportVO reportVO
    ) {
        return getAllCsvInDirActualToExpectedCsvLines(
                reportVO
        )
                .stream()
                .map(actualToExpectedCsvLines -> new ImmutablePair<List<List<String>>, List<List<String>>>(
                        getColumnDataFromCsvRow(actualToExpectedCsvLines.getLeft()),
                        getColumnDataFromCsvRow(actualToExpectedCsvLines.getRight())
                ))
                .toList();
    }

    private List<List<String>> getColumnDataFromCsvRow(List<String> csvRows) {
        return csvRows
                .stream()
                .map(csvRow -> getColumnValues(csvRow))
                .toList();
    }

    private List<String> getColumnValues(String csvRow) {
        return Arrays.stream(csvRow.split(",")).map(String::trim).toList();
    }

    private List<ImmutablePair<List<String>, List<String>>> getAllCsvInDirActualToExpectedCsvLines(ReportVO reportVO) {
        return getActualToExpectedReader(reportVO)
                .stream()
                .map(actualToExpectedReader -> new ImmutablePair<>(
                                readLines(actualToExpectedReader.getLeft()),
                                readLines(actualToExpectedReader.getRight())
                        )
                )
                .toList();
    }

    private List<String> readLines(BufferedReader reader) {
        List<String> lines = new ArrayList<>();

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines.stream().filter(ObjectUtils::isNotEmpty).toList();
    }

    private List<ImmutablePair<BufferedReader, BufferedReader>> getActualToExpectedReader(ReportVO reportVO) {
        List<File> actualReports = getReports(reportVO.getActualNamingPattern(), reportVO.getActualReportsInDir());
        Map<String, File> expectedFileNameToReport = getFileNameToFile(getReports(reportVO.getExpectedNamingPattern(), reportVO.getExpectedReportsInDir()));

        if (ObjectUtils.isEmpty(actualReports) || ObjectUtils.isEmpty(expectedFileNameToReport)) {
            throw new RuntimeException("Reports are missing");
        }

        return actualReports
                .stream()
                .map(actualReport -> getActualToExpectedReportReader(
                                actualReport,
                                expectedFileNameToReport.get(actualReport.getName()),
                                reportVO.getEncoding()
                        )
                )
                .toList();
    }

    private ImmutablePair<BufferedReader, BufferedReader> getActualToExpectedReportReader(
            File actualReport, File expectedReport, String encoding
    ) {
        if (ObjectUtils.isEmpty(actualReport) || ObjectUtils.isEmpty(expectedReport) || ObjectUtils.isEmpty(encoding)) {
            throw new RuntimeException("No reports or encoding found");
        }
        return new ImmutablePair<>(
                mapToBufferReader(actualReport, encoding),
                mapToBufferReader(expectedReport, encoding)
        );
    }

    private Map<String, File> getFileNameToFile(List<File> reports) {
        return reports
                .stream()
                .collect(
                        Collectors.toMap(file -> file.getName(), Function.identity())
                );
    }

    private String getParentDirName(Path path) {
        return new File(path.getParent().toUri()).getName();
    }

    private BufferedReader mapToBufferReader(File report, String encoding) {
        try {
            return new BufferedReader(new InputStreamReader(new FileInputStream(report), encoding));
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private List<File> getReports(String namingPattern, String reportInDir) {
        FileFilter filter = getReportInsensitiveFilter(namingPattern);
        try {
            return Files.walk(Path.of(mapToRelativePath(String.format("%s/%s", getResourcesDir(), reportInDir))))
                    .filter(Files::isRegularFile)
                    .map(Path::getParent)
                    .map(Path::toString)
                    .flatMap(parentDir -> Arrays.stream(
                                    Objects.requireNonNull(new File(parentDir).listFiles(filter))
                            ).filter(File::isFile)
                    )
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private WildcardFileFilter getReportInsensitiveFilter(String namingPattern) {
        return WildcardFileFilter.builder()
                .setIoCase(IOCase.INSENSITIVE)
                .setWildcards(namingPattern)
                .get();
    }

    private String mapToRelativePath(String path) {
        return path.replace("\\", "/");
    }

    private String getResourcesDir() {
        return String.format("%s%s", getAppDir(), RESOURCE_SUB_PATH);
    }

    private String getAppDir() {
        return System.getProperty("user.dir");
    }
}
