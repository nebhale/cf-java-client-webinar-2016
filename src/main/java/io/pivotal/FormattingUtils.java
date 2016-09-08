package io.pivotal;

import org.cloudfoundry.operations.applications.ApplicationSummary;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

final class FormattingUtils {

    private FormattingUtils() {
    }

    static String formatApplication(ApplicationSummary application) {
        return formatApplication(application, null);
    }

    static String formatApplication(ApplicationSummary application, List<String> serviceInstances) {
        StringBuilder sb = new StringBuilder()
            .append(formatName(application.getName()))
            .append(formatRequestedState(application.getRequestedState()))
            .append(formatInstances(application.getRunningInstances(), application.getInstances()))
            .append(formatMemory(application.getMemoryLimit()))
            .append(formatDisk(application.getDiskQuota()))
            .append(formatUrls(application.getUrls()));

        Optional.ofNullable(serviceInstances)
            .ifPresent(sis -> sb.append(formatServiceInstances(sis)));

        return sb.toString();
    }

    private static String formatDisk(Integer disk) {
        return String.format("%-7s", String.format("%dG", disk / 1_024));
    }

    private static String formatInstances(Integer runningInstances, Integer instances) {
        return String.format("%-12s", String.format("%d/%d", runningInstances, instances));
    }

    private static String formatMemory(Integer memory) {
        return String.format("%-9s", String.format("%dM", memory));
    }

    private static String formatName(String name) {
        return String.format("%-34s", name);
    }

    private static String formatRequestedState(String requestedState) {
        return String.format("%-18s", requestedState).toLowerCase();
    }

    private static String formatServiceInstances(List<String> serviceInstances) {
        return serviceInstances.stream().collect(Collectors.joining(","));
    }

    private static String formatUrls(List<String> urls) {
        return String.format("%-48s", urls.stream().collect(Collectors.joining(",")));
    }

}
