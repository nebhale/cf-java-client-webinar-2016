/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
