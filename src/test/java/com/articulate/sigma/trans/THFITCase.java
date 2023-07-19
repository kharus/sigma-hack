/*
 * This software is released under the GNU Public License
 * <http://www.gnu.org/copyleft/gpl.html>.
 */
package com.articulate.sigma.trans;

import com.articulate.sigma.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("com.articulate.sigma.TopLevel")
@ActiveProfiles("TopLevel")
@Import(KBmanagerTestConfiguration.class)
public class THFITCase {

    private KB kb;

    @Autowired
    private KBmanager kbManager;

    private THF thf = new THF();

    @BeforeEach
    void init() {
        kb = kbManager.getKB(kbManager.getPref("sumokbname"));
    }

    public void test(String msg, String f, String expected) {

        System.out.println();
        System.out.println("\n======================== " + msg);
        String result = thf.oneKIF2THF(new Formula(f), false, kb);
        System.out.println("THFtest.test(): result: " + result);
        System.out.println("THFtest.test(): expect: " + expected);
        if (expected.equals(result))
            System.out.println("THFtest.test(): Success!");
        else
            System.out.println("THFtest.test(): fail");
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testTrans1() {

        String f = "(=> (and (instance ?ROW3 Language) (instance ?ROW1 SymbolicString)) " +
                "(=> (synonymousExternalConcept ?ROW1 ?ROW2 ?ROW3) " +
                "(relatedExternalConcept ?ROW1 ?ROW2 ?ROW3)))";
        String msg = "testTrans1";
        String expected = "thf(ax1641,axiom,((! [ROW3: $i,ROW1: $i,ROW2: $i]: " +
                "(((instance_THFTYPE_IiioI @ ROW3 @ lLanguage_THFTYPE_i) & " +
                "(instance_THFTYPE_IiioI @ ROW1 @ lSymbolicString_THFTYPE_i)) =>";
        test(msg, f, expected);
    }

}
