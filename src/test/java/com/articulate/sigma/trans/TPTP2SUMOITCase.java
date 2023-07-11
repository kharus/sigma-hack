package com.articulate.sigma.trans;

import com.articulate.sigma.Formula;
import com.articulate.sigma.KBmanager;
import com.articulate.sigma.KBmanagerTestConfiguration;
import com.articulate.sigma.TopOnly;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("com.articulate.sigma.TopOnly")
@ActiveProfiles("TopOnly")
@Import(KBmanagerTestConfiguration.class)
public class TPTP2SUMOITCase {

    @Disabled
    @Test
    public void testPartition() {

        System.out.println("TPTP2SUMOITCase.testPartition()");
        String input = "fof(f658,plain,(" +
                "sQ4_eqProxy(s__BobTheGolfer,s__JohnTheGolfer) | " +
                "sQ4_eqProxy(s__JohnsGolfGame,s__BobsGolfGame) | " +
                "~s__instance(s__JohnTheGolfer,s__Human) | " +
                "~s__instance(s__JohnsGolfGame,s__Golf) | " +
                "~ans0(s__BobTheGolfer,s__JohnsGolfGame,s__BobsGolfGame,s__JohnTheGolfer))).";
        try {
            // kif = TPTP2SUMO.convert(reader, false);
            tptp_parser.TPTPVisitor sv = new tptp_parser.TPTPVisitor();
            sv.parseString(input);
            Map<String, tptp_parser.TPTPFormula> hm = sv.result;
            for (String s : hm.keySet()) {
                System.out.println(hm.get(s));
                System.out.println("\t" + hm.get(s).sumo + "\n");
                System.out.println(TPTP2SUMO.collapseConnectives(new Formula(hm.get(s).sumo)));
            }
        } catch (Exception e) {
            System.out.println("e: " + e);
        }
    }

    @Disabled
    @Test
    public void testCollapse() {

        System.out.println("TPTP2SUMOITCase.testPartition()");
        Formula f = new Formula("(and (and (foo A B) (foo B B)) (bar C))");
        String result = TPTP2SUMO.collapseConnectives(f).toString();
        String expected = """
                (and
                  (foo A B)
                  (foo B B)
                  (bar C))""";
        System.out.println("result: " + result);
        assertThat(result).isEqualTo(expected);
    }

    @Disabled
    @Test
    public void testCollapse2() {

        System.out.println("TPTP2SUMOITCase.testPartition2()");
        Formula f = new Formula("(=> (and (and (foo A B) (foo B B)) (bar C)) (blah F G))");
        String result = TPTP2SUMO.collapseConnectives(f).toString();
        String expected = """
                (=>
                  (and
                    (foo A B)
                    (foo B B)
                    (bar C))
                  (blah F G))""";
        System.out.println("result: " + result);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testCollapse3() {

        Formula f = new Formula("""
                (forall (?X155 ?X156 ?X157 ?X158)
                  (=>
                    (and
                      (instance ?X158 Organism)
                      (instance ?X155 Organism))
                    (=>
                      (and
                        (and
                          (and
                            (and
                              (and
                                (and
                                  (and
                                    (instance ?X157 Golf)
                                    (instance ?X156 Golf))
                                  (not
                                    (equal ?X155 ?X158)))
                                (not
                                  (equal ?X156 ?X157)))
                              (plays ?X157 ?X155))
                            (plays ?X156 ?X158))
                          (inhabits ?X155 UnitedKingdom))
                        (inhabits ?X158 UnitedKingdom))
                      (exists (?X159)
                        (and
                          (and
                            (and
                              (and
                                (plays ?X159 ?X155)
                                (plays ?X159 ?X158))
                              (instance ?X159 Golf))
                            (located ?X159 UnitedKingdom))
                          (instance ?X159 TournamentSport))))))""");
        String result = TPTP2SUMO.collapseConnectives(f).toString();
        String expected = """
                (forall (?A1 ?G1 ?G2 ?A2)
                  (=>
                    (and
                      (instance ?A2 Organism)
                      (instance ?A1 Organism))
                (=>
                  (and
                    (inhabits ?A1 UnitedKingdom)
                    (inhabits ?A2 UnitedKingdom)
                    (plays ?G1 ?A1)
                    (plays ?G2 ?A2)
                    (not\s
                      (equal ?G1 ?G2))
                    (not\s
                      (equal ?A1 ?A2))
                    (instance ?G1 Golf)
                    (instance ?G2 Golf))
                  (exists (?G3)
                    (and
                      (instance ?G3 TournamentSport)
                      (located ?G3 UnitedKingdom)
                      (instance ?G3 Golf)
                      (plays ?G3 ?A1)
                      (plays ?G3 ?A2))))))""";
        Formula fresult = new Formula(result);
        Formula fexpected = new Formula(expected);
        assertThat(fexpected.deepEquals(fresult)).isTrue();
    }
}
