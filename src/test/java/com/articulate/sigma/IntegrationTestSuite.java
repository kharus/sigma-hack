package com.articulate.sigma;

import com.articulate.sigma.trans.SUMOformulaToTPTPformulaTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CaseRoleTest.class,
        FormatTest.class,
        FormulaPreprocessorAddTypeRestrictionsTest.class,
        FormulaPreprocessorIntegrationTest.class,
        KBcacheTest.class,
        KbIntegrationTest.class,
        KBmanagerInitIntegrationTest.class,
        PredVarInstIntegrationTest.class,
        SUMOformulaToTPTPformulaTest.class,
        //HtmlParaphraseIntegrationTest.class,
})
public class IntegrationTestSuite extends IntegrationTestBase {

}