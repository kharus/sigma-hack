package com.articulate.sigma;

import com.articulate.sigma.trans.SUMOformulaToTPTPformulaTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CaseRoleITCase.class,
        FormatITCase.class,
        FormulaPreprocessorAddTypeRestrictionsITCase.class,
        FormulaPreprocessorIntegrationITCase.class,
        KBcacheITCase.class,
        KbIntegrationITCase.class,
        KBmanagerInitIntegrationITCase.class,
        PredVarInstIntegrationITCase.class,
        SUMOformulaToTPTPformulaTest.class,
        //HtmlParaphraseIntegrationITCase.class,
})
public class IntegrationTestSuite extends IntegrationTestBase {

}