package com.articulate.sigma.nlg;

import com.articulate.sigma.KB;
import com.articulate.sigma.KBmanager;
import com.articulate.sigma.KBmanagerTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Tests on SumoProcess require KBs be loaded.
@SpringBootTest
@Tag("com.articulate.sigma.TopOnly")
@ActiveProfiles("TopOnly")
@Import(KBmanagerTestConfiguration.class)
public class SumoProcessCollectorITCase {
    @Value("${sumokbname}")
    private String sumokbname;
    @Autowired
    private KBmanager kbManager;

    private KB knowledgeBase;

    @BeforeEach
    void init() {
        knowledgeBase = kbManager.getKB(sumokbname);
    }

    @Test
    public void testAddInvalidRole() {
        SumoProcessCollector process = new SumoProcessCollector(knowledgeBase, "agent", "Driving", "Mark");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            process.addRole("goal", "HospitalBuilding");
        });
    }

    @Test
    public void testNaturalLanguageDrivingPatient() {
        SumoProcessCollector process = new SumoProcessCollector(knowledgeBase, "agent", "Driving", "Mark");
        process.addRole("patient", "Human");

        String actual = process.toNaturalLanguage();
        String expected = "Mark drives a human";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testNaturalLanguageDrivingPatientNegative() {
        SumoProcessCollector process = new SumoProcessCollector(knowledgeBase, "agent", "Driving", "Mark");
        process.addRole("patient", "Human");
        process.setPolarity(VerbProperties.Polarity.NEGATIVE);

        String actual = process.toNaturalLanguage();
        String expected = "Mark doesn't drive a human";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Disabled
    public void testNaturalLanguageDrivingDestination() {
        SumoProcessCollector process = new SumoProcessCollector(knowledgeBase, "agent", "Driving", "Mark");
        process.addRole("destination", "HospitalBuilding");

        String actual = process.toNaturalLanguage();
        String expected = "Mark drives to HospitalBuilding";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Disabled
    public void testNaturalLanguageDrivingDestinationNegative() {
        SumoProcessCollector process = new SumoProcessCollector(knowledgeBase, "agent", "Driving", "Mark");
        process.addRole("destination", "HospitalBuilding");
        process.setPolarity(VerbProperties.Polarity.NEGATIVE);

        String actual = process.toNaturalLanguage();
        String expected = "Mark doesn't drive to HospitalBuilding";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testNaturalLanguagePerformsIntentionalProcess() {
        SumoProcessCollector process = new SumoProcessCollector(knowledgeBase, "agent", "IntentionalProcess", "Mark");

        String actual = process.toNaturalLanguage();
        String expected = "Mark performs an intentional process";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testNaturalLanguagePerformsIntentionalProcessNegative() {
        SumoProcessCollector process = new SumoProcessCollector(knowledgeBase, "agent", "IntentionalProcess", "Mark");
        process.setPolarity(VerbProperties.Polarity.NEGATIVE);

        String actual = process.toNaturalLanguage();
        String expected = "Mark doesn't perform an intentional process";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testNaturalLanguagePerformIntentionalProcessPluralNegative() {
        SumoProcessCollector process = new SumoProcessCollector(knowledgeBase, "agent", "IntentionalProcess", "Mark");
        process.addRole("agent", "Julie");
        process.setPolarity(VerbProperties.Polarity.NEGATIVE);

        String actual = process.toNaturalLanguage();
        String expected = "Julie and Mark don't perform an intentional process";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Disabled
    public void testNaturalLanguageSeeingNegative() {
        SumoProcessCollector process = new SumoProcessCollector(knowledgeBase, "agent", "Seeing", "Mark");
        process.addRole("patient", "HospitalBuilding");
        process.setPolarity(VerbProperties.Polarity.NEGATIVE);

        String actual = process.toNaturalLanguage();
        String expected = "Mark doesn't see HospitalBuilding";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Disabled
    public void testNaturalLanguageSeeingPluralNegative() {
        SumoProcessCollector process = new SumoProcessCollector(knowledgeBase, "agent", "Seeing", "Mark");
        process.addRole("agent", "Julie");
        process.addRole("patient", "HospitalBuilding");
        process.setPolarity(VerbProperties.Polarity.NEGATIVE);

        String actual = process.toNaturalLanguage();
        String expected = "Julie and Mark don't see HospitalBuilding";
        assertThat(actual).isEqualTo(expected);
    }

}