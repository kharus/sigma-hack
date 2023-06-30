package com.articulate.sigma.nlg;

import com.articulate.sigma.TopOnly;
import com.articulate.sigma.UnitTestBase;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.assertj.core.api.Assertions.assertThat;

// Tests requiring that KBs be loaded.
@Category(TopOnly.class)
public class CaseRoleITCase extends UnitTestBase {

    @Test
    public void testCommonCaseRoles() {
        CaseRole caseRole = CaseRole.toCaseRole("agent", kb);
        assertThat(caseRole).isEqualTo(CaseRole.AGENT);

        caseRole = CaseRole.toCaseRole("attends", kb);
        assertThat(caseRole).isEqualTo(CaseRole.ATTENDS);

        caseRole = CaseRole.toCaseRole("destination", kb);
        assertThat(caseRole).isEqualTo(CaseRole.DESTINATION);

        caseRole = CaseRole.toCaseRole("direction", kb);
        assertThat(caseRole).isEqualTo(CaseRole.DIRECTION);

        caseRole = CaseRole.toCaseRole("eventPartlyLocated", kb);
        assertThat(caseRole).isEqualTo(CaseRole.EVENTPARTLYLOCATED);

        caseRole = CaseRole.toCaseRole("experiencer", kb);
        assertThat(caseRole).isEqualTo(CaseRole.EXPERIENCER);

        caseRole = CaseRole.toCaseRole("instrument", kb);
        assertThat(caseRole).isEqualTo(CaseRole.INSTRUMENT);

        caseRole = CaseRole.toCaseRole("moves", kb);
        assertThat(caseRole).isEqualTo(CaseRole.MOVES);

        caseRole = CaseRole.toCaseRole("origin", kb);
        assertThat(caseRole).isEqualTo(CaseRole.ORIGIN);

        caseRole = CaseRole.toCaseRole("patient", kb);
        assertThat(caseRole).isEqualTo(CaseRole.PATIENT);

        caseRole = CaseRole.toCaseRole("path", kb);
        assertThat(caseRole).isEqualTo(CaseRole.PATH);

        caseRole = CaseRole.toCaseRole("resource", kb);
        assertThat(caseRole).isEqualTo(CaseRole.RESOURCE);

        caseRole = CaseRole.toCaseRole("other", kb);
        assertThat(caseRole).isEqualTo(CaseRole.OTHER);

        caseRole = CaseRole.toCaseRole("blahblah", kb);
        assertThat(caseRole).isEqualTo(CaseRole.OTHER);
    }

    /**
     * Test less common case roles that are subrelations of a common case role.
     */
    @Test
    public void testSubrelationCaseRoles() {
        CaseRole caseRole = CaseRole.toCaseRole("result", kb);
        assertThat(caseRole).isEqualTo(CaseRole.PATIENT);

        caseRole = CaseRole.toCaseRole("eventLocated", kb);
        assertThat(caseRole).isEqualTo(CaseRole.EVENTPARTLYLOCATED);

        caseRole = CaseRole.toCaseRole("changesLocation", kb);
        assertThat(caseRole).isEqualTo(CaseRole.MOVES);

        // partly located is not a case role
        caseRole = CaseRole.toCaseRole("partlyLocated", kb);
        assertThat(caseRole).isEqualTo(CaseRole.OTHER);
    }

}