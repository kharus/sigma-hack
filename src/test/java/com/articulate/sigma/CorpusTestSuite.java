package com.articulate.sigma;

import com.articulate.sigma.inference.InferenceITCase;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        InferenceITCase.class,
})
public class CorpusTestSuite extends IntegrationTestBase {

}