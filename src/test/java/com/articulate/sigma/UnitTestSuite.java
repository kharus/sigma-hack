/*
 * This software is released under the GNU Public License
 * <http://www.gnu.org/copyleft/gpl.html>.
 * Copyright 2019 Infosys
 * adam.pease@infosys.com
 */
package com.articulate.sigma;

import com.articulate.sigma.nlg.UnitNLGTestSuite;
import com.articulate.sigma.trans.SUMOformulaToTPTPformulaITCase;
import com.articulate.sigma.trans.TPTP3ProofProcITCase;
import com.articulate.sigma.wordnet.MultiWordsITCase;
import com.articulate.sigma.wordnet.WordNetITCase;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        FormulaArityCheckITCase.class,
        FormulaDeepEqualsITCase.class,
        FormulaLogicalEqualityITCase.class,
        FormulaPreprocessorComputeVariableTypesITCase.class,
        FormulaPreprocessorFindExplicitTypesITCase.class,
        FormulaPreprocessorITCase.class,
        FormulaITCase.class,
        FormulaUtilITCase.class,
        FormulaUnificationITCase.class,
        KBcacheUnitITCase.class,
        KBmanagerInitITCase.class,
        KBITCase.class,
        MultiWordsITCase.class,
        PredVarInstITCase.class,
        RowVarITCase.class,
        SUMOformulaToTPTPformulaITCase.class,
        TPTP3ProofProcITCase.class,
        UnitNLGTestSuite.class,
        WordNetITCase.class,
})
public class UnitTestSuite extends UnitTestBase {

}
