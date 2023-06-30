package com.articulate.sigma.nlg;

import com.articulate.sigma.Formula;
import com.articulate.sigma.KB;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LanguageFormatterStackTest extends SigmaMockTestBase {
    private final KB kb = kbMock;


    @Test
    public void testInsertQuantifier() {
        String stmt = "(exists (?D ?H)\n" +
                "               (and\n" +
                "                   (instance ?D Driving)\n" +
                "                   (instance ?H Human)\n" +
                "                   (agent ?D ?H)))";
        Formula formula = new Formula(stmt);

        LanguageFormatterStack stack = new LanguageFormatterStack();
        stack.pushNew();
        stack.insertFormulaArgs(formula);

        List<StackElement.FormulaArg> formulaArgs = stack.getCurrStackElement().formulaArgs;
        assertThat(formulaArgs.size()).isEqualTo(2);

        assertThat(LanguageFormatterStack.getFormulaArg(formulaArgs, "(?D ?H)").state).isEqualTo(StackElement.StackState.QUANTIFIED_VARS);

        String expectedKey = """
                (and
                                   (instance ?D Driving)
                                   (instance ?H Human)
                                   (agent ?D ?H))""";
        assertThat(LanguageFormatterStack.getFormulaArg(formulaArgs, expectedKey).state).isEqualTo(StackElement.StackState.UNPROCESSED);
    }


    @Test
    public void testInsertAnd() {
        String stmt = "(and\n" +
                "           (instance ?D Driving)\n" +
                "           (instance ?H Human)\n" +
                "           (agent ?D ?H))";
        Formula formula = new Formula(stmt);

        LanguageFormatterStack stack = new LanguageFormatterStack();
        stack.pushNew();
        stack.insertFormulaArgs(formula);

        List<StackElement.FormulaArg> formulaArgs = stack.getCurrStackElement().formulaArgs;
        assertThat(formulaArgs.size()).isEqualTo(3);

        String expectedKey = "(instance ?D Driving)";
        assertThat(LanguageFormatterStack.getFormulaArg(formulaArgs, expectedKey).state).isEqualTo(StackElement.StackState.UNPROCESSED);

        expectedKey = "(instance ?H Human)";
        assertThat(LanguageFormatterStack.getFormulaArg(formulaArgs, expectedKey).state).isEqualTo(StackElement.StackState.UNPROCESSED);

        expectedKey = "(agent ?D ?H)";
        assertThat(LanguageFormatterStack.getFormulaArg(formulaArgs, expectedKey).state).isEqualTo(StackElement.StackState.UNPROCESSED);
    }


    @Test(expected = IllegalStateException.class)
    public void testIllegalTranslatedState() {
        String string1 = "(exists (?D ?H)\n" +
                "               (and\n" +
                "                   (instance ?D Driving)\n" +
                "                   (instance ?H Human)\n" +
                "                   (agent ?D ?H)))";


        LanguageFormatterStack stack = new LanguageFormatterStack();

        // Push two items onto the stack.
        stack.pushNew();
        Formula formula1 = new Formula(string1);
        stack.insertFormulaArgs(formula1);
        stack.pushNew();
        String string2 = formula1.complexArgumentsToArrayListString(1).get(1);
        Formula formula2 = new Formula(string2);
        stack.insertFormulaArgs(formula2);

        // Verify state of bottom element's arg.
        assertThat(LanguageFormatterStack.getFormulaArg(stack.getPrevStackElement().formulaArgs, string2).state).isEqualTo(StackElement.StackState.UNPROCESSED);

        // Set top element's translated state, creating the illegal state.
        stack.getCurrStackElement().setTranslation("", true);

        // Call pushCurrTranslatedStateDown().
        stack.pushCurrTranslatedStateDown(string2);
    }

    @Test
    public void testPushTranslatedYes() {
        String string1 = "(exists (?D ?H)\n" +
                "               (and\n" +
                "                   (instance ?D Driving)\n" +
                "                   (instance ?H Human)\n" +
                "                   (agent ?D ?H)))";


        LanguageFormatterStack stack = new LanguageFormatterStack();

        // Push two items onto the stack.
        stack.pushNew();
        Formula formula1 = new Formula(string1);
        stack.insertFormulaArgs(formula1);
        stack.pushNew();
        String string2 = formula1.complexArgumentsToArrayListString(1).get(1);
        Formula formula2 = new Formula(string2);
        stack.insertFormulaArgs(formula2);

        // Verify state of bottom element's arg.
        assertThat(LanguageFormatterStack.getFormulaArg(stack.getPrevStackElement().formulaArgs, string2).state).isEqualTo(StackElement.StackState.UNPROCESSED);

        // Set top element's translated state.
        stack.getCurrStackElement().setTranslation("a human drives", true);

        // Call pushCurrTranslatedStateDown().
        stack.pushCurrTranslatedStateDown(string2);

        // Verify the state has changed.
        StackElement.FormulaArg formulaArg = LanguageFormatterStack.getFormulaArg(stack.getPrevStackElement().formulaArgs, string2);
        assertThat(formulaArg.translation).isEqualTo("a human drives");
        assertThat(formulaArg.state).isEqualTo(StackElement.StackState.TRANSLATED);
    }

    @Test
    public void testPushTranslatedNo() {
        String string1 = "(exists (?D ?H)\n" +
                "               (and\n" +
                "                   (instance ?D Driving)\n" +
                "                   (instance ?H Human)\n" +
                "                   (agent ?D ?H)))";


        LanguageFormatterStack stack = new LanguageFormatterStack();

        // Push two items onto the stack.
        stack.pushNew();
        Formula formula1 = new Formula(string1);
        stack.insertFormulaArgs(formula1);
        stack.pushNew();
        String string2 = formula1.complexArgumentsToArrayListString(1).get(1);
        Formula formula2 = new Formula(string2);
        stack.insertFormulaArgs(formula2);

        // Verify state of bottom element's arg.
        assertThat(LanguageFormatterStack.getFormulaArg(stack.getPrevStackElement().formulaArgs, string2).state).isEqualTo(StackElement.StackState.UNPROCESSED);

        // Do not modify top element's translated state.
        //stack.getCurrStackElement().setTranslation(true);

        // Call pushCurrTranslatedStateDown().
        stack.pushCurrTranslatedStateDown(string2);

        // Verify the state has changed.
        assertThat(LanguageFormatterStack.getFormulaArg(stack.getPrevStackElement().formulaArgs, string2).state).isEqualTo(StackElement.StackState.UNPROCESSED);
    }


    @Test
    public void testAreFormulaArgsProcessed() {
        String string1 = "(exists (?D ?H)\n" +
                "               (and\n" +
                "                   (instance ?D Driving)\n" +
                "                   (instance ?H Human)\n" +
                "                   (agent ?D ?H)))";


        LanguageFormatterStack stack = new LanguageFormatterStack();

        // Push two items onto the stack.
        stack.pushNew();
        Formula formula1 = new Formula(string1);
        stack.insertFormulaArgs(formula1);
        stack.pushNew();
        String string2 = formula1.complexArgumentsToArrayListString(1).get(1);
        Formula formula2 = new Formula(string2);
        stack.insertFormulaArgs(formula2);

        // Nothing marked as processed except for var quantifier in first element at bottom of stack.
        assertThat(LanguageFormatterStack.areFormulaArgsProcessed(stack.getCurrStackElement())).isFalse();
        assertThat(LanguageFormatterStack.areFormulaArgsProcessed(stack.getPrevStackElement())).isFalse();


        // Set top element's translated state.
        stack.getCurrStackElement().setTranslation("a human drives", true);

        // Call pushCurrTranslatedStateDown().
        stack.pushCurrTranslatedStateDown(string2);

        // Pushing down from curr to bottom of stack has set the state of the second element at bottom of stack.
        // Now both are in a "processed" state.
        assertThat(LanguageFormatterStack.areFormulaArgsProcessed(stack.getCurrStackElement())).isFalse();
        assertThat(LanguageFormatterStack.areFormulaArgsProcessed(stack.getPrevStackElement())).isTrue();
    }


    @Test
    public void testPushCurrSumoProcessDown() {
        String string1 = "(and \n" +
                "               (instance John-1 Human) \n" +
                "               (instance ?event Seeing) \n" +
                "               (instance ?object SelfConnectedObject) \n" +
                "               (experiencer ?event John-1) \n" +
                "               (not \n" +
                "                   (patient ?event ?object)))";


        LanguageFormatterStack stack = new LanguageFormatterStack();

        // Push items onto the stack.
        stack.pushNew();
        Formula formula1 = new Formula(string1);
        stack.insertFormulaArgs(formula1);
        stack.pushNew();
        String string2 = formula1.complexArgumentsToArrayListString(1).get(1);
        Formula formula2 = new Formula(string2);
        stack.insertFormulaArgs(formula2);
        stack.pushNew();
        String string3 = formula2.complexArgumentsToArrayListString(1).get(1);
        Formula formula3 = new Formula(string3);
        stack.insertFormulaArgs(formula3);

        // Nothing marked as processed except for var quantifier in first element at bottom of stack.
        assertThat(stack.getCurrStackElement().getSumoProcessMap().size()).isEqualTo(0);
        assertThat(stack.getPrevStackElement().getSumoProcessMap().size()).isEqualTo(0);

        // Set the Sumo process maps for the top two elements.
        SumoProcessCollector processCollector = new SumoProcessCollector(kb, "experiencer", "Seeing", "John-1");
        stack.getPrevStackElement().getSumoProcessMap().put("?event", processCollector);

        processCollector = new SumoProcessCollector(kb, "patient", "Seeing", "?object");
        stack.getCurrStackElement().getSumoProcessMap().put("?event", processCollector);

        // Set top element's state to negative.
        stack.getCurrStackElement().setProcessPolarity("?event", VerbProperties.Polarity.NEGATIVE);

        // Now the stack elements should be populated.
        assertThat(stack.getCurrStackElement().getSumoProcessMap().size()).isEqualTo(1);
        assertThat(stack.getCurrStackElement().getSumoProcessMap().get("?event").getRolesAndEntities().size()).isEqualTo(1);
        assertThat(stack.getCurrStackElement().getSumoProcessMap().get("?event").getPolarity()).isEqualTo(VerbProperties.Polarity.NEGATIVE);
        assertThat(stack.getPrevStackElement().getSumoProcessMap().size()).isEqualTo(1);
        assertThat(stack.getPrevStackElement().getSumoProcessMap().get("?event").getRolesAndEntities().size()).isEqualTo(1);
        assertThat(stack.getPrevStackElement().getSumoProcessMap().get("?event").getPolarity()).isEqualTo(VerbProperties.Polarity.AFFIRMATIVE);

        // Call pushCurrSumoProcessDown().
        stack.pushCurrSumoProcessDown();

        // Now the second-from-the-top should contain the information in the topmost element. The topmost won't have changed.
        assertThat(stack.getCurrStackElement().getSumoProcessMap().size()).isEqualTo(1);
        assertThat(stack.getCurrStackElement().getSumoProcessMap().get("?event").getRolesAndEntities().size()).isEqualTo(1);
        assertThat(stack.getCurrStackElement().getSumoProcessMap().get("?event").getPolarity()).isEqualTo(VerbProperties.Polarity.NEGATIVE);
        assertThat(stack.getPrevStackElement().getSumoProcessMap().size()).isEqualTo(1);
        assertThat(stack.getPrevStackElement().getSumoProcessMap().get("?event").getRolesAndEntities().size()).isEqualTo(2);
        assertThat(stack.getPrevStackElement().getSumoProcessMap().get("?event").getPolarity()).isEqualTo(VerbProperties.Polarity.NEGATIVE);
    }

}