package com.articulate.sigma.nlg;

import com.articulate.sigma.UnitTestBase;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CaseRoleITCase.class,
        //HtmlParaphraseMockITCase.class, TODO: restore tests
        //HtmlParaphraseITCase.class,
        LanguageFormatterStackITCase.class,
        LanguageFormatterITCase.class,
        NLGStringUtilsITCase.class,
        NLGUtilsITCase.class,
        SentenceSimpleITCase.class,
        SumoProcessCollectorSimpleITCase.class,
        SumoProcessCollectorITCase.class,
        SumoProcessEntityPropertySimpleITCase.class,
        VerbPropertiesITCase.class,
})
public class UnitNLGTestSuite extends UnitTestBase {

}