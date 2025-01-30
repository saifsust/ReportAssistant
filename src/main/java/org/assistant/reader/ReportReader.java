package org.assistant.reader;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assistant.constants.AppConstants.RESOURCE_SUB_PATH;

public class ReportReader {


    public List<File> getReports(String namingPattern, String reportInDir) throws IOException {
        FileFilter filter = getReportInsensitiveFilter(namingPattern);

        return Files.walk(Path.of(mapToRelativePath(String.format("%s/%s", getResourcesDir(), reportInDir))))
                .filter(Files::isRegularFile)
                .map(Path::getParent)
                .map(Path::toString)
                .flatMap(parentDir -> Arrays.stream(
                        Objects.requireNonNull(new File(parentDir).listFiles(filter))
                        ).filter(File::isFile)
                )
                .toList();
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
