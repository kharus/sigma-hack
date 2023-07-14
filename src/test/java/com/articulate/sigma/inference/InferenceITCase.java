package com.articulate.sigma.inference;

import com.articulate.sigma.InferenceTestSuite;
import com.articulate.sigma.KB;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class InferenceITCase {
    private static KB kb;
    @Parameterized.Parameter(value = 0)
    public String fInput;
    @Value("${sumokbname}")
    private String sumokbname;

    @Parameterized.Parameters(name = "{0}")
    public static <T> Collection<T> prepare() {

        String testDataDirectoryPath = "test/corpus/java/resources/InferenceITCaseData";
        boolean enableIncludeTestsList = false;   // If enableIncludeTestsList=true, only run test files in includeTestsList
        boolean enableExcludeTestsList = false;   // If enableIncludeTestsList=false & enableExcludeTestsList=true, only run test files NOT in excludeTestsLists
        // If enableIncludeTestsList=false & enableExcludeTestsList=false, run all test files in InferenceITCaseData
        List<String> includeTestsList = Lists.newArrayList("QA1");
        List<String> excludeTestsList = Lists.newArrayList("TQG2", "TQG4", "TQG10");
        return getTestFiles(testDataDirectoryPath, includeTestsList, enableIncludeTestsList,
                excludeTestsList, enableExcludeTestsList);
    }

    public static <T> Collection<T> getTestFiles(String testDataDirectoryPath,
                                                 List<String> includeTestsList, boolean enableIncludeTestsList,
                                                 List<String> excludeTestsList, boolean enableExcludeTestsList) {

        Collection<T> result = Lists.newArrayList();
        File folder = new File(testDataDirectoryPath);
        try {
            for (File fileEntry : folder.listFiles()) {
                if (fileEntry.getName().endsWith(".kif.tq")) {
                    String filename = fileEntry.getName().substring(0, fileEntry.getName().indexOf(".kif.tq"));
                    if (enableIncludeTestsList) {       // only consider files in includeTestsList
                        if (includeTestsList.contains(filename)) {
                            String path = fileEntry.getCanonicalPath();
                            result.add((T) new Object[]{path});
                        }
                    } else if (enableExcludeTestsList) {  // only consider files NOT in excludeTestsList
                        if (!excludeTestsList.contains(filename)) {
                            String path = fileEntry.getCanonicalPath();
                            result.add((T) new Object[]{path});
                        }
                    } else {                              // consider all files in InferenceITCaseData directory
                        String path = fileEntry.getCanonicalPath();
                        result.add((T) new Object[]{path});
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error in InferenceITCase.getTestFiles(): using path: " + testDataDirectoryPath);
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Test
    public void test() {

        System.out.println("InferenceITCase.test(): " + fInput);
        List<String> expectedAnswers = new ArrayList<>();
        List<String> actualAnswers = new ArrayList<>();
        InferenceTestSuite its = new InferenceTestSuite();
        InferenceTestSuite.InfTestData itd = its.inferenceUnitTest(fInput, kb);
        System.out.println("expected: " + itd.expectedAnswers);
        System.out.println("actual: " + itd.actualAnswers);
        if (itd.inconsistent)
            System.out.println("Failure (**inconsistent**) in " + fInput);
        else if (itd.expectedAnswers.equals(itd.actualAnswers))
            System.out.println("Success in " + fInput);
        else
            System.out.println("Failure in " + fInput);
        System.out.println("\n\n");
        assertThat(itd.actualAnswers).isEqualTo(itd.expectedAnswers);
    }
}
