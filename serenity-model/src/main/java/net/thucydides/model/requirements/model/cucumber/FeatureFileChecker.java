package net.thucydides.model.requirements.model.cucumber;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.thucydides.model.requirements.model.cucumber.FeatureFileAnaysisErrors.DUPLICATE_FEATURE_NAME;

/**
 * Check whether a stream of files contains valid feature files, and throw an InvalidFeatureFileException if one is either not valid Gherkin syntax,
 * or contains errors or inconsistencies such as empty or duplicate scenario names
 */
public class FeatureFileChecker {

    CucumberParser cucumberParser = new CucumberParser();

    public void check(Stream<File> files, boolean allowDuplicateFeatureNames) {

        List<String> featureFileNames = new ArrayList<>();
        // Features can have duplicate names but a feature file name and parent directory name should be unique
        ConcurrentHashMap<String, List<File>> pathNamesToFeatureFiles = new ConcurrentHashMap<>();

        List<String> errorMessages = files
                .filter(File::isFile)
                .map(featureFile -> {
                    try {
                        Optional<AnnotatedFeature> loadedFeature = cucumberParser.loadFeature(featureFile);
                        loadedFeature.ifPresent(
                                annotatedFeature -> {
                                    recordFeaturePath(pathNamesToFeatureFiles,
                                            featureFile,
                                            annotatedFeature);
                                    featureFileNames.add(annotatedFeature.getFeature().getName());
                                }
                        );
                        return Optional.empty();
                    } catch (Throwable invalidFeatureFile) {
                        invalidFeatureFile.printStackTrace();
                        return Optional.of("* Error found in feature file: " + featureFile.getAbsolutePath()
                                + System.lineSeparator()
                                + "    " + invalidFeatureFile + ":" + invalidFeatureFile.getMessage());
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Object::toString)
                .collect(Collectors.toList());

        // Check for duplicate feature names
        // Feature file names should be unique, or unique within a folder
        if (!allowDuplicateFeatureNames) {
            errorMessages.addAll(
                    checkForDuplicateFeatureNames(featureFileNames)
            );
        }

        if (!errorMessages.isEmpty()) {
            throw new InvalidFeatureFileException(errorMessages.stream().collect(Collectors.joining(System.lineSeparator())));
        }
    }

    private Collection<String> checkForDuplicateFeatureNames(List<String> featureFileNames) {
        // Return the list of duplicate feature names in featureFileNames
        return featureFileNames.stream()
                .collect(Collectors.groupingBy(name -> name))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() > 1)
                .map(entry -> "Duplicate feature name found: '" + entry.getKey() + "'")
                .collect(Collectors.toList());
    }

    private String duplicateFeaturePathsError(String key, List<File> value) {
        String featureFilesWithDuplicates = value.stream()
                .map(file -> "      - " + file.getPath())
                .collect(Collectors.joining(System.lineSeparator()));

        return String.format("* " + DUPLICATE_FEATURE_NAME,key, featureFilesWithDuplicates);
    }

    private static void recordFeaturePath(ConcurrentHashMap<String, List<File>> pathNamesToFeatureFiles,
                                          File featureFile,
                                          AnnotatedFeature loadedFeature) {
        String featureName = loadedFeature.getFeature().getName();
        String parentName = new File(featureFile.getParent()).getName();
        String localFeaturePath = parentName + "/" + featureName;

        if (pathNamesToFeatureFiles.containsKey(localFeaturePath)) {
            pathNamesToFeatureFiles.get(localFeaturePath).add(featureFile);
        } else {
            List<File> featureFiles = new ArrayList<>();
            featureFiles.add(featureFile);
            pathNamesToFeatureFiles.put(localFeaturePath, featureFiles);
        }
    }
}
