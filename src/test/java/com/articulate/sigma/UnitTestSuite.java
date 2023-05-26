/*
 * This software is released under the GNU Public License
 * <http://www.gnu.org/copyleft/gpl.html>.
 * Copyright 2019 Infosys
 * adam.pease@infosys.com
 */
package com.articulate.sigma;

import com.articulate.sigma.nlg.UnitNLGTestSuite;
import com.articulate.sigma.trans.SUMOformulaToTPTPformulaTest;
import com.articulate.sigma.trans.TPTP3ProofProcTest;
import com.articulate.sigma.wordnet.MultiWordsTest;
import com.articulate.sigma.wordnet.WordNetTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        FormulaArityCheckITCase.class,
        FormulaDeepEqualsTest.class,
        FormulaLogicalEqualityTest.class,
        FormulaPreprocessorComputeVariableTypesTest.class,
        FormulaPreprocessorFindExplicitTypesTest.class,
        FormulaPreprocessorTest.class,
        FormulaTest.class,
        FormulaUtilTest.class,
        FormulaUnificationTest.class,
        KBcacheUnitTest.class,
        KBmanagerInitTest.class,
        KBTest.class,
        MultiWordsTest.class,
        PredVarInstTest.class,
        RowVarTest.class,
        SUMOformulaToTPTPformulaTest.class,
        TPTP3ProofProcTest.class,
        UnitNLGTestSuite.class,
        WordNetTest.class,
})
public class UnitTestSuite extends UnitTestBase {

}
