package com.articulate.sigma;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FormulaPreprocessor tests not focused on findExplicitTypes( ), but requiring that the KBs be loaded.
 */
@SpringBootTest
@Tag("com.articulate.sigma.MidLevel")
@ActiveProfiles("MidLevel")
@Import(KBmanagerTestConfiguration.class)
public class FormulaPreprocessorIntegrationITCase {
    @Value("${sumokbname}")
    private String sumokbname;

    private KB kb;

    @Autowired
    private KBmanager kbManager;

    @BeforeEach
    void init() {
        kb = kbManager.getKB(sumokbname);
    }

    /**
     * NOTE: If this test fails, you need to load Mid-level-ontology.kif. One way to do this would be to edit
     * your config.xml file by putting this line under "<kb name="SUMO" >":
     * <constituent filename=".../Mid-level-ontology.kif" />
     */
    @Test
    public void testComputeVariableTypesTypicalPart() {

        String stmt = "(=> " +
                "(typicalPart ?X ?Y) " +
                "(subclass ?Y Object))";

        Formula f = new Formula();
        f.read(stmt);

        FormulaPreprocessor formulaPre = new FormulaPreprocessor();
        Map<String, Set<String>> actual = formulaPre.computeVariableTypes(f, kb);

        Map<String, Set<String>> expected = Maps.newHashMap();
        Set<String> set1 = Sets.newHashSet("Class", "Object+");
        expected.put("?Y", set1);
        expected.put("?X", Sets.newHashSet("Object+"));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testFindTypes2() {

        Map<String, Set<String>> expected = Maps.newHashMap();
        expected.put("?NOTPARTPROB", Sets.newHashSet("RealNumber"));
        expected.put("?PART", Sets.newHashSet("Class", "Object+"));
        expected.put("?PARTPROB", Sets.newHashSet("RealNumber"));
        expected.put("?X", Sets.newHashSet("Entity"));
        expected.put("?WHOLE", Sets.newHashSet("Object+"));

        String strf = "(=> (and (typicalPart ?PART ?WHOLE) (instance ?X ?PART) " +
                "(equal ?PARTPROB (ProbabilityFn (exists (?Y) (and " +
                "(instance ?Y ?WHOLE) (part ?X ?Y))))) (equal ?NOTPARTPROB " +
                "(ProbabilityFn (not (exists (?Z) (and (instance ?Z ?WHOLE) " +
                "(part ?X ?Z))))))) (greaterThan ?PARTPROB ?NOTPARTPROB))";
        Formula f = new Formula();
        f.read(strf);
        FormulaPreprocessor fp = new FormulaPreprocessor();

        Map<String, Set<String>> actualMap = fp.computeVariableTypes(f, kb);

        assertThat(actualMap).isEqualTo(expected);
    }

    @Disabled
    @Test
    public void testAddTypes3() {

        String strf = "(=> (and (typicalPart ?PART ?WHOLE) (instance ?X ?PART) " +
                "(equal ?PARTPROB (ProbabilityFn (exists (?Y) (and " +
                "(instance ?Y ?WHOLE) (part ?X ?Y))))) (equal (?NOTPARTPROB " +
                "(ProbabilityFn (not (exists (?Z) (and (instance ?Z ?WHOLE) " +
                "(part ?X ?Z))))))) (greaterThan ?PARTPROB ?NOTPARTPROB))";
        Formula f = new Formula();
        f.read(strf);
        FormulaPreprocessor fp = new FormulaPreprocessor();

        Formula expected = new Formula();
        String expectedString = "(=> (and (instance ?PART Class) (subclass ?PART Object) (instance ?PARTPROB Entity) (instance ?X Object) (instance ?WHOLE Class) (subclass ?WHOLE Object) (instance ?Y Object)) " +
                "(=> (and (typicalPart ?PART ?WHOLE) (instance ?X ?PART) " +
                "(equal ?PARTPROB (ProbabilityFn (exists (?Y) (and (instance ?Y ?WHOLE) (part ?X ?Y)))))" +
                "(equal (?NOTPARTPROB (ProbabilityFn (not (exists (?Z) (and (instance ?Z ?WHOLE) (part ?X ?Z))))))) " +
                "(greaterThan ?PARTPROB ?NOTPARTPROB))) ";
        expected.read(expectedString);
        Formula actual = fp.addTypeRestrictions(f, kb);
        //assertThat("expected: " + expected.toString() + ", but was: " + actual.toString(), expected.equals(actual)).isTrue();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testComputeVariableTypesPlaintiff() {

        String stmt = """
                (exists (?P ?H)
                           (and
                               (instance ?P LegalAction)
                               (instance ?H Human)
                               (plaintiff ?P ?H)))""";
        Formula f = new Formula();
        f.read(stmt);

        FormulaPreprocessor formulaPre = new FormulaPreprocessor();
        Map<String, Set<String>> actual = formulaPre.computeVariableTypes(f, kb);

        Map<String, Set<String>> expected = Maps.newHashMap();
        Set<String> set1 = Sets.newHashSet("CognitiveAgent");
        expected.put("?H", set1);
        Set<String> set2 = Sets.newHashSet("LegalAction");
        expected.put("?P", set2);

        assertThat(actual).isEqualTo(expected);
    }

}