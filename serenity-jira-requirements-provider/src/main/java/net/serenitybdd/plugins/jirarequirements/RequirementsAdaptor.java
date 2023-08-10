package net.serenitybdd.plugins.jirarequirements;

import net.serenitybdd.plugins.jira.domain.IssueSummary;
import net.thucydides.model.requirements.model.Requirement;
import net.thucydides.model.util.EnvironmentVariables;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequirementsAdaptor {

    private final EnvironmentVariables environmentVariables;

    public RequirementsAdaptor(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public Requirement requirementFrom(IssueSummary issue) {

        Requirement baseRequirement = Requirement.named(issue.getSummary())
                .withOptionalCardNumber(issue.getKey())
                .withType(issue.getType())
                .withNarrative(narrativeTextFrom(issue))
                .withReleaseVersions(issue.getFixVersions());

        for (String fieldName : definedCustomFields()) {
            if (issue.customField(fieldName).isPresent()) {
                String value = issue.customField(fieldName).get().asString();
                String renderedValue = issue.getRendered().customField(fieldName).get();
                baseRequirement = baseRequirement.withCustomField(fieldName).setTo(value, renderedValue);
            }
        }
        return baseRequirement;
    }

    private String narrativeTextFrom(IssueSummary issue) {
        Optional<String> customFieldName = Optional.ofNullable(environmentVariables.getProperty(JIRARequirementsConfiguration.JIRA_CUSTOM_NARRATIVE_FIELD.getName()));
        if (customFieldName.isPresent()) {
            return customFieldNameFor(issue, customFieldName.get()).orElse(ObjectUtils.firstNonNull(issue.getRendered().getDescription(), ""));
        } else {
            return issue.getRendered().getDescription();
        }

    }

    private List<String> definedCustomFields() {
        List<String> customFields = new ArrayList<>();
        int customFieldIndex = 1;
        while (addCustomFieldIfDefined(environmentVariables, customFields, customFieldNumber(customFieldIndex++))) ;
        return customFields;
    }


    private Optional<String> customFieldNameFor(IssueSummary issue, String customFieldName) {
        if (issue.customField(customFieldName).isPresent()) {
            return Optional.of(issue.customField(customFieldName).get().asString());
        } else {
            return Optional.empty();
        }
    }

    private boolean addCustomFieldIfDefined(EnvironmentVariables environmentVariables,
                                            List<String> customFields,
                                            String customField) {
        String customFieldName = environmentVariables.getProperty(customField);
        if (StringUtils.isNotEmpty(customFieldName)) {
            customFields.add(customFieldName);
            return true;
        }
        return false;
    }

    private String customFieldNumber(int customFieldIndex) {
        return JIRARequirementsConfiguration.JIRA_CUSTOM_FIELD.getName() + "." + customFieldIndex;
    }

}
