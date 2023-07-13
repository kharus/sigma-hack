package com.articulate.sigma.nlg;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("com.articulate.sigma.TopOnly")
public class VerbPropertiesITCase {

    private final VerbPropertiesSimpleImpl verbPropertiesSimple = new VerbPropertiesSimpleImpl();

    @Test
    public void testPrepositionDefault() {
        String inputVerb = "blahblah";

        List<String> expected = Lists.newArrayList("");
        List<String> actual = verbPropertiesSimple.getPrepositionForCaseRole(inputVerb, CaseRole.AGENT);
        assertThat(actual).isEqualTo(expected);

        expected = Lists.newArrayList("to");
        actual = verbPropertiesSimple.getPrepositionForCaseRole(inputVerb, CaseRole.DESTINATION);
        assertThat(actual).isEqualTo(expected);

        expected = Lists.newArrayList("toward");
        actual = verbPropertiesSimple.getPrepositionForCaseRole(inputVerb, CaseRole.DIRECTION);
        assertThat(actual).isEqualTo(expected);

        expected = Lists.newArrayList("in");
        actual = verbPropertiesSimple.getPrepositionForCaseRole(inputVerb, CaseRole.EVENTPARTLYLOCATED);
        assertThat(actual).isEqualTo(expected);

        expected = Lists.newArrayList("");
        actual = verbPropertiesSimple.getPrepositionForCaseRole(inputVerb, CaseRole.EXPERIENCER);
        assertThat(actual).isEqualTo(expected);

        expected = Lists.newArrayList("with");
        actual = verbPropertiesSimple.getPrepositionForCaseRole(inputVerb, CaseRole.INSTRUMENT);
        assertThat(actual).isEqualTo(expected);

        expected = Lists.newArrayList("");
        actual = verbPropertiesSimple.getPrepositionForCaseRole(inputVerb, CaseRole.MOVES);
        assertThat(actual).isEqualTo(expected);

        expected = Lists.newArrayList("from");
        actual = verbPropertiesSimple.getPrepositionForCaseRole(inputVerb, CaseRole.ORIGIN);
        assertThat(actual).isEqualTo(expected);

        expected = Lists.newArrayList("along");
        actual = verbPropertiesSimple.getPrepositionForCaseRole(inputVerb, CaseRole.PATH);
        assertThat(actual).isEqualTo(expected);

        expected = Lists.newArrayList("");
        actual = verbPropertiesSimple.getPrepositionForCaseRole(inputVerb, CaseRole.PATIENT);
        assertThat(actual).isEqualTo(expected);

        expected = Lists.newArrayList("out of", "from");
        actual = verbPropertiesSimple.getPrepositionForCaseRole(inputVerb, CaseRole.RESOURCE);
        assertThat(actual).isEqualTo(expected);

        expected = Lists.newArrayList("");
        actual = verbPropertiesSimple.getPrepositionForCaseRole(inputVerb, CaseRole.OTHER);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testCaseRoleDefault() {
        String inputVerb = "blahblah";

        List<CaseRole> expected = Lists.newArrayList(CaseRole.AGENT, CaseRole.EXPERIENCER, CaseRole.MOVES);
        List<CaseRole> actual = verbPropertiesSimple.getCaseRolesForGrammarRole(inputVerb, SVOElement.SVOGrammarPosition.SUBJECT);
        assertThat(actual).isEqualTo(expected);

        expected = Lists.newArrayList(CaseRole.PATIENT, CaseRole.MOVES);
        actual = verbPropertiesSimple.getCaseRolesForGrammarRole(inputVerb, SVOElement.SVOGrammarPosition.DIRECT_OBJECT);
        assertThat(actual).isEqualTo(expected);

        expected = Lists.newArrayList(CaseRole.DIRECTION, CaseRole.PATH, CaseRole.ORIGIN, CaseRole.DESTINATION,
                CaseRole.EVENTPARTLYLOCATED, CaseRole.INSTRUMENT, CaseRole.RESOURCE);
        actual = verbPropertiesSimple.getCaseRolesForGrammarRole(inputVerb, SVOElement.SVOGrammarPosition.INDIRECT_OBJECT);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testCaseRoleBurn() {
        String inputVerb = "burn";

        List<CaseRole> expected = Lists.newArrayList(CaseRole.AGENT, CaseRole.PATIENT);
        List<CaseRole> actual = verbPropertiesSimple.getCaseRolesForGrammarRole(inputVerb, SVOElement.SVOGrammarPosition.SUBJECT);
        assertThat(actual).isEqualTo(expected);

        // Falls back to default values for direct object.
        expected = Lists.newArrayList(CaseRole.PATIENT, CaseRole.MOVES);
        actual = verbPropertiesSimple.getCaseRolesForGrammarRole(inputVerb, SVOElement.SVOGrammarPosition.DIRECT_OBJECT);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testCaseRoleFall() {
        String inputVerb = "fall";

        List<CaseRole> expected = Lists.newArrayList(CaseRole.EXPERIENCER, CaseRole.PATIENT);
        List<CaseRole> actual = verbPropertiesSimple.getCaseRolesForGrammarRole(inputVerb, SVOElement.SVOGrammarPosition.SUBJECT);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testCaseRoleSee() {
        String inputVerb = "see";

        List<CaseRole> expected = Lists.newArrayList(CaseRole.EXPERIENCER);
        List<CaseRole> actual = verbPropertiesSimple.getCaseRolesForGrammarRole(inputVerb, SVOElement.SVOGrammarPosition.SUBJECT);
        assertThat(actual).isEqualTo(expected);
    }
}
