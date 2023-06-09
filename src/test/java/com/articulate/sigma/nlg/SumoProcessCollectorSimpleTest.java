package com.articulate.sigma.nlg;

import com.articulate.sigma.KB;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.junit.Rule;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Tests on SumoProcess that do not require KBs be loaded.

public class SumoProcessCollectorSimpleTest extends SigmaMockTestBase {

    private final KB knowledgeBase = this.kbMock;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    // Testing for null/empty parameters.
    @Test
    public void testNullKB() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new SumoProcessCollector(null, "agent", "Process", "Human");
        });
    }

    @Test
    public void testEmptyRole() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new SumoProcessCollector(knowledgeBase, "", "Process", "Human");
        });
    }

    @Test
    public void testEmptyProcess() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new SumoProcessCollector(knowledgeBase, "agent", "", "Human");
        });
    }

    @Test
    public void testEmptyEntity() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new SumoProcessCollector(knowledgeBase, "agent", "Process", "");
        });
    }

    /**
     * role parameter must be a known role.
     */
    @Test
    public void testInvalidRole() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new SumoProcessCollector(knowledgeBase, "invalidRole", "hi", "there");
        });
        assertThat(exception.getMessage()).isEqualTo("Invalid role: role = invalidRole; process = hi; entity = there.");
    }

    /**
     * process parameter must be a known process
     */
    @Test
    public void testInvalidProcess() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new SumoProcessCollector(knowledgeBase, "agent", "EatingBadTastingOatmeal", "John");
        });
        assertThat(exception.getMessage())
                .isEqualTo("Process parameter is not a Process: role = agent; process = EatingBadTastingOatmeal; entity = John.");
    }

    @Test
    public void testBasicSumoProcessFunctionality() {
        // Test constructor.
        SumoProcessCollector process = new SumoProcessCollector(knowledgeBase, "agent", "Driving", "Human");
        Multimap<CaseRole, String> roleScratchPad = process.createNewRoleScratchPad();
        assertThat(process.getRolesAndEntities().size()).isEqualTo(1);
        assertThat(Sentence.getRoleEntities(CaseRole.AGENT, roleScratchPad).size()).isEqualTo(1);
        assertThat(Sentence.getRoleEntities(CaseRole.PATIENT, roleScratchPad).size()).isEqualTo(0);

        //Test agent getters and setters.
        process.addRole("agent", "Tom");
        roleScratchPad = process.createNewRoleScratchPad();

        Set<String> expectedAgents = Sets.newTreeSet(Sets.newHashSet("Human", "Tom"));

        Set<String> actualAgents = Sentence.getRoleEntities(CaseRole.AGENT, roleScratchPad);
        //Collections.sort(actualAgents);

        assertThat(actualAgents).isEqualTo(expectedAgents);

        // Test patient getters and setters.
        process.addRole("patient", "Automobile");
        roleScratchPad = process.createNewRoleScratchPad();

        Set<String> expectedPatients = Sets.newTreeSet(Sets.newHashSet("Automobile"));
        Set<String> actualPatients = Sentence.getRoleEntities(CaseRole.PATIENT, roleScratchPad);

        assertThat(actualPatients).isEqualTo(expectedPatients);

        // Test toString().
        String expected = "agent Driving Human\n" +
                "agent Driving Tom\n" +
                "patient Driving Automobile\n";
        assertThat(process.toString()).isEqualTo(expected);
    }

    /**
     * Ignoring test till we figure out how to do benefactive/benefits in SUMO.
     */
    @Disabled
    @Test
    public void testIsValidFalse() {
        SumoProcessCollector process = new SumoProcessCollector(knowledgeBase, "benefactive", "Driving", "Sally");
        process.addRole("goal", "HospitalBuilding");
    }

    @Test
    public void testAddMultipleRoles() {
        SumoProcessCollector process = new SumoProcessCollector(knowledgeBase, "agent", "Driving", "Human");
        process.addRole("patient", "Sally");
        process.addRole("destination", "HospitalBuilding");

        String expected = "agent Driving Human\n" +
                "destination Driving HospitalBuilding\n" +
                "patient Driving Sally\n";
        assertThat(process.toString()).isEqualTo(expected);
    }

    @Test
    public void testAddMultipleRolesWithVariables() {
        SumoProcessCollector process = new SumoProcessCollector(knowledgeBase, "agent", "Driving", "?H");
        process.addRole("patient", "?C");
        process.addRole("destination", "?P");

        String expected = "agent Driving ?H\n" +
                "destination Driving ?P\n" +
                "patient Driving ?C\n";
        assertThat(process.toString()).isEqualTo(expected);
        expected = "?H drives ?C to ?P";
        assertThat(process.toNaturalLanguage()).isEqualTo(expected);
    }

    /**
     * Verify that repeated, identical roles are ignored.
     */
    @Test
    public void testNoIdenticalRoleParticipants() {
        SumoProcessCollector process = new SumoProcessCollector(knowledgeBase, "agent", "Driving", "Mark");
        Multimap<CaseRole, String> roleScratchPad = process.createNewRoleScratchPad();
        assertThat(Sentence.getRoleEntities(CaseRole.AGENT, roleScratchPad).size()).isEqualTo(1);

        process.addRole("agent", "Mark");
        roleScratchPad = process.createNewRoleScratchPad();
        assertThat(Sentence.getRoleEntities(CaseRole.AGENT, roleScratchPad).size()).isEqualTo(1);
    }

    /**
     * Verify that when you ask for a copy of the roles, you can't use it to change the object's copy.
     */
    @Test
    public void testDeepCopies() {
        SumoProcessCollector process = new SumoProcessCollector(knowledgeBase, "agent", "Driving", "Mark");
        process.addRole("destination", "HospitalBuilding");
        Multimap<CaseRole, String> roleScratchPad = process.createNewRoleScratchPad();

        assertThat(Sentence.getRoleEntities(CaseRole.AGENT, roleScratchPad).size()).isEqualTo(1);
        assertThat(process.getRolesAndEntities().size()).isEqualTo(2);

        // Get local copy of agents, and change that.
        Set<String> agents = Sentence.getRoleEntities(CaseRole.AGENT, roleScratchPad);
        agents.add("Sally");
        assertThat(Sentence.getRoleEntities(CaseRole.AGENT, roleScratchPad).size()).isEqualTo(1);

        // Get local copy of all roles, and change that.
        Multimap<CaseRole, String> allRoles = process.getRolesAndEntities();
        allRoles.put(CaseRole.DESTINATION, "House");
        assertThat(process.getRolesAndEntities().size()).isEqualTo(2);
    }

    /**
     * Verify that createNewRoleScratchPad( ) returns a defensive copy.
     */
    @Test
    public void testCreateNewRoleScratchPad() {
        SumoProcessCollector process = new SumoProcessCollector(knowledgeBase, "agent", "Transportation", "Maria");

        Multimap<CaseRole, String> originalMap = process.createNewRoleScratchPad();
        assertThat(originalMap.size()).isEqualTo(1);
        originalMap.clear();
        assertThat(originalMap.size()).isEqualTo(0);

        Multimap<CaseRole, String> actualMap = process.createNewRoleScratchPad();

        assertThat(actualMap.size()).isEqualTo(1);
    }

    /**
     * Throws IllegalArgumentException because the events of the two processes don't match.
     */
    @Test
    public void testMergeMultipleRolesFail() {
        SumoProcessCollector process1 = new SumoProcessCollector(knowledgeBase, "agent", "Driving", "?H");

        SumoProcessCollector process2 = new SumoProcessCollector(knowledgeBase, "agent", "Eating", "?H");
        process2.addRole("patient", "?C");

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> process1.merge(process2));

        assertThat(exception.getMessage())
                .isEqualTo("Cannot merge because the objects do not have identical processes: process1 = Driving; process2 = Eating");
    }

    @Test
    public void testMergeMultipleRolesNoIntersection() {
        SumoProcessCollector process1 = new SumoProcessCollector(knowledgeBase, "agent", "Driving", "?H");
        process1.addRole("patient", "?C");

        SumoProcessCollector process2 = new SumoProcessCollector(knowledgeBase, "patient", "Driving", "?D");
        process2.addRole("destination", "?P");

        process1.merge(process2);

        String expected = "agent Driving ?H\n" +
                "destination Driving ?P\n" +
                "patient Driving ?C\n" +
                "patient Driving ?D\n";
        assertThat(process1.toString()).isEqualTo(expected);
        expected = "?H drives ?C and ?D to ?P";
        assertThat(process1.toNaturalLanguage()).isEqualTo(expected);
    }

    @Test
    public void testMergeMultipleRolesAgentIntersection() {
        SumoProcessCollector process1 = new SumoProcessCollector(knowledgeBase, "agent", "Driving", "?H");
        process1.addRole("patient", "?C");

        SumoProcessCollector process2 = new SumoProcessCollector(knowledgeBase, "agent", "Driving", "?H");
        process2.addRole("patient", "?D");
        process2.addRole("destination", "?P");

        process1.merge(process2);

        String expected = "agent Driving ?H\n" +
                "destination Driving ?P\n" +
                "patient Driving ?C\n" +
                "patient Driving ?D\n";
        assertThat(process1.toString()).isEqualTo(expected);
        expected = "?H drives ?C and ?D to ?P";
        assertThat(process1.toNaturalLanguage()).isEqualTo(expected);
    }

    @Test
    public void testMergeMultipleRolesAgentIntersectionNegative() {
        SumoProcessCollector process1 = new SumoProcessCollector(knowledgeBase, "agent", "Driving", "?H");
        process1.addRole("patient", "?C");

        SumoProcessCollector process2 = new SumoProcessCollector(knowledgeBase, "agent", "Driving", "?H");
        process2.addRole("patient", "?D");
        process2.addRole("destination", "?P");
        process2.setPolarity(VerbProperties.Polarity.NEGATIVE);

        // Verify polarity before the merge.
        assertThat(process1.getPolarity()).isEqualTo(VerbProperties.Polarity.AFFIRMATIVE);
        assertThat(process2.getPolarity()).isEqualTo(VerbProperties.Polarity.NEGATIVE);

        process1.merge(process2);

        assertThat(process1.getPolarity()).isEqualTo(VerbProperties.Polarity.NEGATIVE);

        String expected = "agent Driving ?H\n" +
                "destination Driving ?P\n" +
                "patient Driving ?C\n" +
                "patient Driving ?D\n";
        assertThat(process1.toString()).isEqualTo(expected);
        expected = "?H doesn't drive ?C and ?D to ?P";
        assertThat(process1.toNaturalLanguage()).isEqualTo(expected);
    }

}