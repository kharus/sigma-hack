package com.articulate.sigma;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Tag("com.articulate.sigma.TopOnly")
@Import(KBmanagerTestConfiguration.class)
public class FormulaLogicalEqualityITCase {

    private static final String TEST_FILE_NAME = "formula_logical_equality_tests.json";

    @Autowired
    FormulaDeepEqualsService deepEqualsService;


    @ParameterizedTest
    @ArgumentsSource(JsonArgumentsProvider.class)
    public void test(Formula f1, Formula f2, boolean areEqual) {

        boolean comparisonResult = f1.logicallyEquals(f2);

        if (areEqual) {
            assertThat(comparisonResult)
                    .as("%s\n should be equal:\n%s", f1.getFormula(), f2.getFormula())
                    .isTrue();
        } else {
            assertThat(comparisonResult)
                    .as("%s\n should not be equal:\n%s", f1.getFormula(), f2.getFormula())
                    .isFalse();
        }
    }

    public static class JsonArgumentsProvider implements ArgumentsProvider {
        public JsonArgumentsProvider() {
        }

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            File jsonTestFile = new File(UnitTestBase.CONFIG_FILE_DIR, TEST_FILE_NAME);
            JSONParser parser = new JSONParser();
            List<Object[]> result = new ArrayList<Object[]>();

            try {
                Object obj = parser.parse(new FileReader(jsonTestFile.getAbsolutePath()));
                JSONArray jsonObject = (JSONArray) obj;
                for (JSONObject jo : (Iterable<JSONObject>) jsonObject) {
                    String f1Text = (String) jo.get("f1");
                    String f2Text = (String) jo.get("f2");
                    Formula f1 = new Formula();
                    f1.read(f1Text);
                    Formula f2 = new Formula();
                    f2.read(f2Text);
                    boolean equal = (boolean) jo.get("equal");
                    result.add(new Object[]{f1, f2, equal});
                }
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }

            return result.stream().map(Arguments::of);
        }
    }
}
