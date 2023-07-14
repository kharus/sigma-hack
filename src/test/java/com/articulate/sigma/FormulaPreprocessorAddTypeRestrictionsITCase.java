package com.articulate.sigma;

import com.articulate.sigma.trans.SUMOformulaToTPTPformula;
import com.articulate.sigma.utils.StringUtil;
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

/**
 * Created by qingqingcai on 3/9/15.
 * <p>
 * requires
 * <constituent filename="Merge.kif" />
 * <constituent filename="Mid-level-ontology.kif" />
 * <constituent filename="FinancialOntology.kif" />
 */
@SpringBootTest
@Tag("com.articulate.sigma.MidLevel")
@ActiveProfiles("MidLevel")
@Import(KBmanagerTestConfiguration.class)
public class FormulaPreprocessorAddTypeRestrictionsITCase {
    @Value("${sumokbname}")
    private String sumokbname;
    private KB kb;

    @Autowired
    private KBmanager kbManager;

    @BeforeEach
    void init() {
        kb = kbManager.getKB(sumokbname);
    }

    public void test(String label, String stmt, String expected) {

        System.out.println("=============================");
        System.out.println("FormulaPreprocessorAddTypeRestrictionsITCase: " + label);
        System.out.println();
        FormulaPreprocessor fp = new FormulaPreprocessor();
        Formula f = new Formula(stmt);
        Formula actualF = fp.addTypeRestrictions(f, kb);
        String actualTPTP = SUMOformulaToTPTPformula.tptpParseSUOKIFString(actualF.getFormula(), false);

        Formula expectedF = new Formula(expected);
        String expectedTPTP = SUMOformulaToTPTPformula.tptpParseSUOKIFString(expectedF.getFormula(), false);

        System.out.println("actual: " + actualTPTP);
        System.out.println("expected: " + expectedTPTP);
        if (!StringUtil.emptyString(actualTPTP) && actualTPTP.equals(expectedTPTP))
            System.out.println(label + " : Success");
        else
            System.out.println(label + " : fail!");
        assertThat(actualTPTP).isEqualTo(expectedTPTP);
    }

    @Test
    public void testAddTypeRestrictions1() {

        String stmt = """
                (<=>
                   (instance ?GRAPH PseudoGraph)
                   (exists (?LOOP)
                      (and
                         (instance ?LOOP GraphLoop)
                         (graphPart ?LOOP ?GRAPH))))""";

        String expected = """
                (<=>
                   (instance ?GRAPH PseudoGraph)
                   (exists (?LOOP)
                      (and
                         (instance ?LOOP GraphLoop)
                         (graphPart ?LOOP ?GRAPH))))""";
        test("testAddTypeRestrictions1", stmt, expected);
    }

    @Test
    @Disabled
    public void testAddTypeRestrictions2() {

        String stmt = """
                (=>
                  (and
                    (graphMeasure ?G ?M)
                    (instance ?AN GraphNode)
                    (instance ?AA GraphArc)
                    (abstractCounterpart ?AN ?PN)
                    (abstractCounterpart ?AA ?PA)
                    (arcWeight ?AA (MeasureFn ?N ?M)))
                  (measure ?PA (MeasureFn ?N ?M)))""";

        String expected = """
                (=>
                  (and
                    (instance ?PA Physical)
                    (instance ?G Graph)
                    (instance ?PN Physical)
                    (instance ?M UnitOfMeasure)
                    (instance ?N RealNumber) )
                    (=>
                      (and
                        (graphMeasure ?G ?M)
                        (instance ?AN GraphNode)
                        (instance ?AA GraphArc)
                        (abstractCounterpart ?AN ?PN)
                        (abstractCounterpart ?AA ?PA)
                        (arcWeight ?AA (MeasureFn ?N ?M)) )
                      (measure ?PA (MeasureFn ?N ?M)) ))""";

        test("testAddTypeRestrictions2", stmt, expected);
    }

    @Test
    @Disabled
    public void testAddTypeRestrictions3() {

        String stmt = """
                (=>
                   (instance ?CLOUD WaterCloud)
                   (forall (?PART)
                      (=>
                         (and
                            (part ?PART ?CLOUD)
                            (not (instance ?PART Water)))
                         (exists (?WATER)
                            (and
                               (instance ?WATER Water)
                               (part ?WATER ?CLOUD)
                               (measure ?WATER (MeasureFn ?MEASURE1 ?UNIT))
                               (measure ?PART (MeasureFn ?MEASURE2 ?UNIT))
                               (greaterThan ?MEASURE1 ?MEASURE2))))))""";

        String expected = "(=> \n" +
                "  (and \n" +
                "    (instance ?MEASURE1 RealNumber)\n" +
                "    (instance ?MEASURE2 RealNumber)\n" +
                "    (instance ?UNIT UnitOfMeasure) )\n" +
                "  (=>\n" +
                "    (instance ?CLOUD WaterCloud)\n" +
                "    (forall (?PART)\n" +
                "      (=>\n" +
                "        (instance ?PART Object)\n" +
                "        (=>\n" +
                "          (and\n" +
                "            (part ?PART ?CLOUD)\n" +
                "            (not (instance ?PART Water) ))\n" +
                "          (exists (?WATER)\n" +
                "            (and\n" +
                "              (instance ?WATER Water)\n" +
                "              (part ?WATER ?CLOUD)\n" +
                "              (measure ?WATER (MeasureFn ?MEASURE1 ?UNIT))\n" +
                "              (measure ?PART (MeasureFn ?MEASURE2 ?UNIT))\n" +
                "              (greaterThan ?MEASURE1 ?MEASURE2) )))))))";

        test("testAddTypeRestrictions3()", stmt, expected);
    }

    @Test
    public void testAddTypeRestrictions4() {

        String stmt = """
                (=>
                   (and
                      (instance ?MIXTURE Mixture)
                      (part ?SUBSTANCE ?MIXTURE)
                      (not (instance ?SUBSTANCE Mixture)))
                   (instance ?SUBSTANCE PureSubstance))""";

        String expected = """
                (=>
                (instance ?SUBSTANCE Object)
                (=>
                  (and
                    (instance ?MIXTURE Mixture)
                    (part ?SUBSTANCE ?MIXTURE)
                    (not (instance ?SUBSTANCE Mixture) ))
                  (instance ?SUBSTANCE PureSubstance) ))""";

        test("testAddTypeRestrictions4()", stmt, expected);
    }

    @Test
    public void testAddTypeRestrictions5() {

        String stmt = """
                (=>
                  (axis ?AXIS ?OBJ)
                  (exists (?R)
                    (and
                      (instance ?R Rotating)
                      (part ?AXIS ?OBJ)
                      (experiencer ?R ?OBJ)
                      (not
                        (exists (?R2)
                          (and
                            (instance ?R2 Rotating)
                            (subProcess ?R2 ?R)
                            (experiencer ?R2 ?AXIS)))))))""";

        String expected = """
                (=>
                  (and
                    (instance ?OBJ AutonomousAgent)
                    (instance ?AXIS AutonomousAgent) )
                  (=>
                    (axis ?AXIS ?OBJ)
                    (exists (?R)
                      (and
                        (instance ?R Rotating)
                        (part ?AXIS ?OBJ)
                        (experiencer ?R ?OBJ)
                        (not
                          (exists (?R2)
                            (and
                              (instance ?R2 Rotating)
                              (subProcess ?R2 ?R)
                              (experiencer ?R2 ?AXIS) )))))))""";

        test("testAddTypeRestrictions5", stmt, expected);
    }

    @Disabled  // serviceFee is in Financial ontology not merge or MILO
    @Test
    public void testAddTypeRestrictions6() {

        String stmt = """
                (=>
                    (serviceFee ?Bank ?Action ?Amount)
                    (exists (?Fee)
                        (and
                            (instance ?Fee ChargingAFee)
                            (agent ?Fee ?Bank)
                            (causes ?Action ?Fee)
                            (amountCharged ?Fee ?Amount))))""";

        String expected = """
                (=>
                  (and
                    (instance ?Amount CurrencyMeasure)
                    (instance ?Action FinancialTransaction)
                    (instance ?Bank FinancialOrganization) )
                  (=>
                    (serviceFee ?Bank ?Action ?Amount)
                    (exists (?Fee)
                      (and
                        (instance ?Fee ChargingAFee)
                        (agent ?Fee ?Bank)
                        (causes ?Action ?Fee)
                        (amountCharged ?Fee ?Amount) ))))""";

        test("testAddTypeRestrictions6", stmt, expected);
    }

    @Test
    public void testAddTypeRestrictions7() {

        String stmt = """
                (=>
                    (forall (?ELEMENT)
                        (<=>
                            (element ?ELEMENT ?SET1)
                            (element ?ELEMENT ?SET2)))
                    (equal ?SET1 ?SET2))""";

        String expected = """
                (=>
                  (and
                    (instance ?SET1 Set)
                    (instance ?SET2 Set) )
                  (=>
                    (forall (?ELEMENT)
                        (<=>
                          (element ?ELEMENT ?SET1)
                          (element ?ELEMENT ?SET2)) )
                    (equal ?SET1 ?SET2) ))""";

        test("testAddTypeRestrictions7", stmt, expected);
    }

    @Disabled
    @Test
    public void testAddTypeRestrictions8() {

        String stmt = """
                (=>
                    (and
                        (typicalPart ?PART ?WHOLE)
                        (instance ?X ?PART)
                        (equal ?PARTPROB
                            (ProbabilityFn
                                (exists (?Y)
                                    (and
                                        (instance ?Y ?WHOLE)
                                        (part ?X ?Y)))))
                        (equal ?NOTPARTPROB
                            (ProbabilityFn
                                (not
                                    (exists (?Z)
                                        (and
                                            (instance ?Z ?WHOLE)
                                            (part ?X ?Z)))))))
                    (greaterThan ?PARTPROB ?NOTPARTPROB))""";

        String expected = """
                (=>
                  (and
                    (subclass ?WHOLE Object)
                    (instance ?NOTPARTPROB RealNumber)
                    (instance ?PARTPROB RealNumber)
                    (subclass ?PART Object)
                    (instance ?PART Class))
                  (=>
                    (and
                      (typicalPart ?PART ?WHOLE)
                      (instance ?X ?PART)
                      (equal ?PARTPROB
                        (ProbabilityFn
                          (exists (?Y)
                            (and
                              (instance ?Y Object)
                              (instance ?Y ?WHOLE)
                              (part ?X ?Y)))))
                      (equal ?NOTPARTPROB
                        (ProbabilityFn
                          (not
                            (exists (?Z)
                              (and
                                (instance ?Z Object)
                                (instance ?Z ?WHOLE)
                                (part ?X ?Z)))))))
                    (greaterThan ?PARTPROB ?NOTPARTPROB)))""";

        test("testAddTypeRestrictions8", stmt, expected);
    }

    @Disabled
    @Test
    public void testAddTypeRestrictions9() {

        String stmt = """
                (<=>
                  (instance ?PHYS Physical)
                  (exists (?LOC ?TIME)
                    (and
                      (located ?PHYS ?LOC)
                      (time ?PHYS ?TIME))))""";

        String expected = """
                (<=>
                  (instance ?PHYS Physical)
                  (exists (?LOC ?TIME)
                    (and
                      (instance ?LOC Object)
                      (instance ?TIME TimePosition)
                      (located ?PHYS ?LOC)
                      (time ?PHYS ?TIME))))""";

        test("testAddTypeRestrictions9", stmt, expected);
    }

    @Test
    public void testAddTypeRestrictions10() {

        String stmt = """
                (=>
                  (instance ?GROUP BeliefGroup)
                  (exists (?BELIEF)
                    (forall (?MEMB)
                      (=>
                        (member ?MEMB ?GROUP)
                        (believes ?MEMB ?BELIEF)))))""";

        String expected = """
                (=>
                  (instance ?GROUP BeliefGroup)
                  (exists (?BELIEF)
                    (and
                      (instance ?BELIEF Formula)
                      (forall (?MEMB)
                        (=>
                          (instance ?MEMB CognitiveAgent)
                          (=>
                            (member ?MEMB ?GROUP)
                            (believes ?MEMB ?BELIEF) ))))))""";

        test("testAddTypeRestrictions10", stmt, expected);
    }

    @Test
    public void testAddTypeRestrictions11() {

        String stmt = """
                (<=>
                  (instance ?OBJ SelfConnectedObject)
                  (forall (?PART1 ?PART2)
                  (=>
                    (equal ?OBJ
                      (MereologicalSumFn ?PART1 ?PART2))
                    (connected ?PART1 ?PART2))))""";

        String expected = """
                (<=>
                  (instance ?OBJ SelfConnectedObject)
                  (forall (?PART1 ?PART2)
                    (=>
                      (and
                        (instance ?PART1 Object)
                        (instance ?PART2 Object))
                      (=>
                        (equal ?OBJ
                          (MereologicalSumFn ?PART1 ?PART2))
                        (connected ?PART1 ?PART2) ))))""";

        test("testAddTypeRestrictions11", stmt, expected);
    }

    @Disabled
    @Test
    public void testAddTypeRestrictions12() {

        SUMOformulaToTPTPformula.debug = true;
        String stmt = """
                (=>
                  (and
                    (instance ?S ?C)
                    (subclass ?C Seafood))
                  (exists (?X ?SEA)
                    (and
                      (meatOfAnimal ?C ?ANIMAL)
                      (instance ?X ?ANIMAL)
                      (instance ?SEA BodyOfWater)
                      (inhabits ?X ?SEA))))""";

        String expected = """
                (=>
                  (and
                    (instance ?S Meat)
                    (subclass ?ANIMAL Animal)
                    (subclass ?C Meat)
                    (instance ?ANIMAL Class))
                  (=>
                  (and
                    (instance ?S ?C)
                    (subclass ?C Seafood))
                  (exists (?X ?SEA)
                    (and
                      (meatOfAnimal ?C ?ANIMAL)
                      (instance ?X ?ANIMAL)
                      (instance ?SEA BodyOfWater)
                      (inhabits ?X ?SEA)))))""";

        test("testAddTypeRestrictions12", stmt, expected);
    }
}
