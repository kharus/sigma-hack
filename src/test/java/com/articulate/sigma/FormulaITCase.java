package com.articulate.sigma;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("com.articulate.sigma.TopOnly")
public class FormulaITCase {

    @Test
    public void testFormulaRead() {

        String stmt = "(domain date 1 Physical)";
        Formula f = new Formula(stmt);
        assertThat(f.getFormula()).isEqualTo(stmt);

        stmt = "(=> (and (instance ?REL ObjectAttitude) (?REL ?AGENT ?THING)) (instance ?THING Physical))";
        f = new Formula();
        f.read(stmt);
        assertThat(f.getFormula()).isEqualTo(stmt);

        stmt = "aabbc";
        f = new Formula();
        f.read(stmt);
        assertThat(f.getFormula()).isEqualTo(stmt);

    }

    @Test
    public void testRecursiveCdrSimple() {

        String stmt = "(exists (?M))";
        Formula f = new Formula(stmt);

        String car = f.car();
        assertThat(car).isEqualTo("exists");
        Formula cdrF = f.cdrAsFormula();
        assertThat(cdrF.getFormula()).isEqualTo("((?M))");

        car = cdrF.car();
        assertThat(car).isEqualTo("(?M)");
        cdrF = cdrF.cdrAsFormula();
        assertThat(cdrF.getFormula()).isEqualTo("()");

        car = cdrF.car();
        assertThat(car).isEqualTo("");
        cdrF = cdrF.cdrAsFormula();
        assertThat(cdrF.getFormula()).isEqualTo("()");

        car = cdrF.car();
        assertThat(car).isEqualTo("");
        cdrF = cdrF.cdrAsFormula();
        assertThat(cdrF.getFormula()).isEqualTo("()");
    }

    @Test
    public void testRecursiveCdrComplex() {

        System.out.println("============= testRecursiveCdrComplex ==================");
        String stmt = "(time JohnsBirth (MonthFn ?M (YearFn 2000)))";
        Formula f = new Formula(stmt);

        String car = f.car();
        assertThat(car).isEqualTo("time");
        Formula cdrF = f.cdrAsFormula();
        assertThat(cdrF.getFormula()).isEqualTo("(JohnsBirth (MonthFn ?M (YearFn 2000)))");

        car = cdrF.car();
        assertThat(car).isEqualTo("JohnsBirth");
        cdrF = cdrF.cdrAsFormula();
        assertThat(cdrF.getFormula()).isEqualTo("((MonthFn ?M (YearFn 2000)))");

        String functionStr = cdrF.car();
        assertThat(functionStr).isEqualTo("(MonthFn ?M (YearFn 2000))");
        cdrF = cdrF.cdrAsFormula();
        assertThat(cdrF.getFormula()).isEqualTo("()");

        System.out.println("testRecursiveCdrComplex(): functionStr: " + functionStr);
        //assertThat(Formula.isFunctionalTerm(functionStr)).isTrue();

        f = new Formula();
        f.read(functionStr);

        car = f.car();
        assertThat(car).isEqualTo("MonthFn");
        cdrF = f.cdrAsFormula();
        assertThat(cdrF.getFormula()).isEqualTo("(?M (YearFn 2000))");

        car = cdrF.car();
        assertThat(car).isEqualTo("?M");
        cdrF = cdrF.cdrAsFormula();
        assertThat(cdrF.getFormula()).isEqualTo("((YearFn 2000))");

        functionStr = cdrF.car();
        assertThat(functionStr).isEqualTo("(YearFn 2000)");
        cdrF = cdrF.cdrAsFormula();
        assertThat(cdrF.getFormula()).isEqualTo("()");

        //assertThat(Formula.isFunctionalTerm(functionStr)).isTrue();

        f = new Formula();
        f.read(functionStr);

        car = f.car();
        assertThat(car).isEqualTo("YearFn");
        cdrF = f.cdrAsFormula();
        assertThat(cdrF.getFormula()).isEqualTo("(2000)");

        car = cdrF.car();
        assertThat(car).isEqualTo("2000");
        cdrF = cdrF.cdrAsFormula();
        assertThat(cdrF.getFormula()).isEqualTo("()");

        car = cdrF.car();
        assertThat(car).isEqualTo("");
        cdrF = cdrF.cdrAsFormula();
        assertThat(cdrF.getFormula()).isEqualTo("()");
    }

    /*
    @Test
    public void testIsSimpleClauseWithFunctionalTerm() {
        Formula f1 = new Formula();
        f1.read("(part (MarialogicalSumFn ?X) ?Y)");

        assertThat(f1.isSimpleClause()).isTrue();
    }

    @Test
    public void testIsSimpleClause1() {
        Formula f1 = new Formula();
        f1.read("(instance ?X Human)");

        assertThat(f1.isSimpleClause()).isTrue();
    }

    @Test
    public void testIsSimpleClause2() {
        Formula f1 = new Formula();
        f1.read("(member (SkFn 1 ?X3) ?X3)");

        assertThat(f1.isSimpleClause()).isTrue();
    }

    @Test
    public void testIsSimpleClause3() {
        Formula f1 = new Formula();
        f1.read("(member ?VAR1 Org1-1)");

        assertThat(f1.isSimpleClause()).isTrue();
    }

    @Test
    public void testIsSimpleClause4() {
        Formula f1 = new Formula();
        f1.read("(capability (KappaFn ?HEAR (and (instance ?HEAR Hearing) (agent ?HEAR ?HUMAN) " +
                "(destination ?HEAR ?HUMAN) (origin ?HEAR ?OBJ))) agent ?HUMAN)");

        assertThat(f1.isSimpleClause()).isTrue();
    }

    @Test
    public void testNotSimpleClause1() {
        Formula f1 = new Formula();
        f1.read("(=> (attribute ?Agent Investor) (exists (?Investing) (agent ?Investing ?Agent)))");

        assertThat(f1.isSimpleClause()).isFalse();
    }

    @Test
    public void testNotSimpleClause2() {
        Formula f1 = new Formula();
        f1.read("(not (instance ?X Human))");

        assertThat(f1.isSimpleClause()).isFalse();
    }
*/

    @Test
    public void testCollectQuantifiedVariables() {

        Set<String> expected = new HashSet<>(Arrays.asList("?T", "?Z"));
        Formula f1 = new Formula();
        f1.read("(=> " +
                "  (and " +
                "    (attribute ?H Muslim) " +
                "    (equal " +
                "      (WealthFn ?H) ?W)) " +
                "(modalAttribute " +
                "  (exists (?Z ?T) " +
                "    (and " +
                "      (instance ?Z Zakat) " +
                "      (instance ?Y Year) " +
                "      (during ?Y " +
                "        (WhenFn ?H)) " +
                "      (holdsDuring ?Y " +
                "        (attribute ?H FullyFormed)) " +
                "      (agent ?Z ?H) " +
                "      (patient ?Z ?T) " +
                "      (monetaryValue ?T ?C) " +
                "      (greaterThan ?C " +
                "        (MultiplicationFn ?W 0.025)))) Obligation)) ");
        System.out.println("testCollectQuantifiedVariables(): f1: " + f1);
        System.out.println("testCollectQuantifiedVariables(): expected: " + expected);
        Set<String> result = f1.collectQuantifiedVariables();
        System.out.println("testCollectQuantifiedVariables(): result: " + result);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testCollectAllVariables() {

        Set<String> expected = Sets.newHashSet("?C", "?T", "?H", "?W", "?Y", "?Z");

        Formula f1 = new Formula();
        f1.read("(=> " +
                "  (and " +
                "    (attribute ?H Muslim) " +
                "    (equal " +
                "      (WealthFn ?H) ?W)) " +
                "(modalAttribute " +
                "  (exists (?Z ?T) " +
                "    (and " +
                "      (instance ?Z Zakat) " +
                "      (instance ?Y Year) " +
                "      (during ?Y " +
                "        (WhenFn ?H)) " +
                "      (holdsDuring ?Y " +
                "        (attribute ?H FullyFormed)) " +
                "      (agent ?Z ?H) " +
                "      (patient ?Z ?T) " +
                "      (monetaryValue ?T ?C) " +
                "      (greaterThan ?C " +
                "        (MultiplicationFn ?W 0.025)))) Obligation)) ");

        assertThat(f1.collectAllVariables()).isEqualTo(expected);
    }

    @Test
    public void testUnquantifiedVariables() {

        Set<String> expected = new HashSet<>(Arrays.asList("?C", "?W", "?H", "?Y"));
        Formula f1 = new Formula();
        f1.read("(=> " +
                "  (and " +
                "    (attribute ?H Muslim) " +
                "    (equal " +
                "      (WealthFn ?H) ?W)) " +
                "(modalAttribute " +
                "  (exists (?Z ?T) " +
                "    (and " +
                "      (instance ?Z Zakat) " +
                "      (instance ?Y Year) " +
                "      (during ?Y " +
                "        (WhenFn ?H)) " +
                "      (holdsDuring ?Y " +
                "        (attribute ?H FullyFormed)) " +
                "      (agent ?Z ?H) " +
                "      (patient ?Z ?T) " +
                "      (monetaryValue ?T ?C) " +
                "      (greaterThan ?C " +
                "        (MultiplicationFn ?W 0.025)))) Obligation)) ");

        assertThat(f1.collectUnquantifiedVariables()).isEqualTo(expected);
    }

    @Test
    public void testTerms() {

        Set<String> expected = Sets.newHashSet("holdsDuring", "MultiplicationFn", "WealthFn", "?T", "Muslim", "?W",
                "Obligation", "attribute", "?Y", "equal", "?Z", "agent", "and", "Year", "patient", "=>", "modalAttribute",
                "during", "?C", "monetaryValue", "FullyFormed", "greaterThan", "exists", "?H", "Zakat", "instance",
                "0.025", "WhenFn");

        Formula f1 = new Formula();
        f1.read("(=> " +
                "  (and " +
                "    (attribute ?H Muslim) " +
                "    (equal " +
                "      (WealthFn ?H) ?W)) " +
                "(modalAttribute " +
                "  (exists (?Z ?T) " +
                "    (and " +
                "      (instance ?Z Zakat) " +
                "      (instance ?Y Year) " +
                "      (during ?Y " +
                "        (WhenFn ?H)) " +
                "      (holdsDuring ?Y " +
                "        (attribute ?H FullyFormed)) " +
                "      (agent ?Z ?H) " +
                "      (patient ?Z ?T) " +
                "      (monetaryValue ?T ?C) " +
                "      (greaterThan ?C " +
                "        (MultiplicationFn ?W 0.025)))) Obligation)) ");

        assertThat(f1.collectTerms()).isEqualTo(expected);
    }

    @Test
    public void testReplaceVar() {

        Formula expected = new Formula();
        expected.read("(<=> (instance part TransitiveRelation) (forall (?INST1 ?INST2 ?INST3) (=> (and (part ?INST1 ?INST2) (part ?INST2 ?INST3)) (part ?INST1 ?INST3))))");

        Formula f1 = new Formula();
        f1.read("(<=> (instance ?REL TransitiveRelation) (forall (?INST1 ?INST2 ?INST3) " +
                " (=> (and (?REL ?INST1 ?INST2) (?REL ?INST2 ?INST3)) (?REL ?INST1 ?INST3))))");

        assertThat(f1.replaceVar("?REL", "part")).isEqualTo(expected);
    }

    @Test
    public void testComplexVarsDuringWhenFn1() {

        List<String> expected = Lists.newArrayList("?Y", "(WhenFn ?H)");

        Formula f1 = new Formula();
        f1.read("(during ?Y (WhenFn ?H))");

        assertThat(f1.complexArgumentsToArrayListString(1)).isEqualTo(expected);
    }

    @Test
    public void testComplexVarsDuringWhenFn2() {

        List<String> expected = Lists.newArrayList("(WhenFn ?H)");

        Formula f1 = new Formula();
        f1.read("(during ?Y (WhenFn ?H))");

        assertThat(f1.complexArgumentsToArrayListString(2)).isEqualTo(expected);
    }

    @Test
    public void testComplexVarsInstance1() {

        List<String> expected = Lists.newArrayList("?DRIVE", "Driving");

        Formula f1 = new Formula();
        f1.read("(instance ?DRIVE Driving)");

        assertThat(f1.complexArgumentsToArrayListString(1)).isEqualTo(expected);
    }

    @Test
    public void testComplexVarsInstance2() {

        List<String> expected = Lists.newArrayList("Driving");

        Formula f1 = new Formula();
        f1.read("(instance ?DRIVE Driving)");

        assertThat(f1.complexArgumentsToArrayListString(2)).isEqualTo(expected);
    }

    @Test
    public void testComplexVarsInstanceGovernmentFn1() {

        List<String> expected = Lists.newArrayList("(GovernmentFn ?Place)", "StateGovernment)");

        Formula f1 = new Formula();
        f1.read("(instance (GovernmentFn ?Place) StateGovernment))");

        assertThat(f1.complexArgumentsToArrayListString(1)).isEqualTo(expected);
    }

    @Test
    public void testComplexVarsInstanceGovernmentFn2() {

        List<String> expected = Lists.newArrayList("StateGovernment)");

        Formula f1 = new Formula();
        f1.read("(instance (GovernmentFn ?Place) StateGovernment))");

        assertThat(f1.complexArgumentsToArrayListString(2)).isEqualTo(expected);
    }

    @Test
    public void testBigArgs() {

        String expected = "";

        Formula f1 = new Formula();
        f1.read("(=> (instance ?AT AutomobileTransmission) (hasPurpose ?AT (exists (?C ?D ?A ?R1 ?N1 ?R2 ?R3 ?R4 ?N2 ?N3)" +
                " (and (instance ?C Crankshaft) (instance ?D Driveshaft) (instance ?A Automobile) (part ?D ?A) (part ?AT ?A)" +
                " (part ?C ?A) (connectedEngineeringComponents ?C ?AT) (connectedEngineeringComponents ?D ?AT) (instance ?R1 Rotating)" +
                " (instance ?R2 Rotating) (instance ?R3 Rotating) (instance ?R4 Rotating) (patient ?R1 ?C) (patient ?R2 ?C) (patient ?R3 ?D)" +
                " (patient ?R4 ?D) (causes ?R1 ?R3) (causes ?R2 ?R4) (not (equal ?R1 ?R2)) (holdsDuring ?R1 (measure ?C (RotationFn ?N1 MinuteDuration)))" +
                " (holdsDuring ?R2 (measure ?C (RotationFn ?N1 MinuteDuration))) (holdsDuring ?R3 (measure ?D (RotationFn ?N2 MinuteDuration))) (holdsDuring ?R4" +
                " (measure ?D (RotationFn ?N3 MinuteDuration))) (not (equal ?N2 ?N3))))))");

        assertThat(f1.validArgs()).isEqualTo(expected);
    }

    @Test
    public void testArgumentsToArrayListGivenComplex0() {

        String stmt = "(exists (?D ?H)\n" +
                "               (and\n" +
                "                   (instance ?D Driving)\n" +
                "                   (instance ?H Human)\n" +
                "                   (agent ?D ?H)))";

        Formula f = new Formula(stmt);

        List<String> actual = f.argumentsToArrayListString(0);

        assertThat(actual).isNull();
    }

    @Test
    public void testArgumentsToArrayListGivenComplex1() {

        String stmt = "(exists (?D ?H)\n" +
                "               (and\n" +
                "                   (instance ?D Driving)\n" +
                "                   (instance ?H Human)\n" +
                "                   (agent ?D ?H)))";

        Formula f = new Formula(stmt);

        List<String> actual = f.argumentsToArrayListString(1);

        assertThat(actual).isNull();
    }

    @Test
    public void testArgumentsToArrayListAnd0() {

        String stmt = "(and\n" +
                "(instance ?D Driving)\n" +
                "(instance ?H Human)\n" +
                "(agent ?D ?H)))";

        Formula f = new Formula(stmt);

        List<String> actual = f.argumentsToArrayListString(0);

        assertThat(actual).isNull();
    }

    @Test
    public void testArgumentsToArrayInstance0() {

        String stmt = "(instance ?D Driving)";

        Formula f = new Formula(stmt);

        List<String> actual = f.argumentsToArrayListString(0);
        List<String> expected = Lists.newArrayList("instance", "?D", "Driving");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testComplexArgumentsToArrayListDriving0() {

        String stmt = "(exists (?D ?H)\n" +
                "               (and\n" +
                "                   (instance ?D Driving)\n" +
                "                   (instance ?H Human)\n" +
                "                   (agent ?D ?H)))";

        Formula f = new Formula(stmt);

        List<String> actual = f.complexArgumentsToArrayListString(0);
        String temp = "(and\n" +
                "                   (instance ?D Driving)\n" +
                "                   (instance ?H Human)\n" +
                "                   (agent ?D ?H))";
        List<String> expected = Lists.newArrayList("exists", "(?D ?H)", temp);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testComplexArgumentsToArrayListDriving1() {

        String stmt = "(exists (?D ?H)\n" +
                "               (and\n" +
                "                   (instance ?D Driving)\n" +
                "                   (instance ?H Human)\n" +
                "                   (agent ?D ?H)))";

        Formula f = new Formula(stmt);

        List<String> actual = f.complexArgumentsToArrayListString(1);
        String temp = "(and\n" +
                "                   (instance ?D Driving)\n" +
                "                   (instance ?H Human)\n" +
                "                   (agent ?D ?H))";
        List<String> expected = Lists.newArrayList("(?D ?H)", temp);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testComplexArgumentsToArrayListDriving2() {

        String stmt = "(exists (?D ?H)\n" +
                "               (and\n" +
                "                   (instance ?D Driving)\n" +
                "                   (instance ?H Human)\n" +
                "                   (agent ?D ?H)))";

        Formula f = new Formula(stmt);

        List<String> actual = f.complexArgumentsToArrayListString(2);
        String temp = "(and\n" +
                "                   (instance ?D Driving)\n" +
                "                   (instance ?H Human)\n" +
                "                   (agent ?D ?H))";
        List<String> expected = Lists.newArrayList(temp);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testComplexArgumentsToArrayListDriving3() {

        String stmt = "(exists (?D ?H)\n" +
                "               (and\n" +
                "                   (instance ?D Driving)\n" +
                "                   (instance ?H Human)\n" +
                "                   (agent ?D ?H)))";

        Formula f = new Formula(stmt);

        List<String> actual = f.complexArgumentsToArrayListString(3);

        assertThat(actual).isNull();
    }

    @Test
    public void testComplexArgumentsToArrayListAnd0() {

        String stmt = "(and\n" +
                "           (instance ?D Driving)\n" +
                "           (instance ?H Human)\n" +
                "           (agent ?D ?H))";

        Formula f = new Formula(stmt);

        List<String> actual = f.complexArgumentsToArrayListString(0);
        List<String> expected = Lists.newArrayList("and", "(instance ?D Driving)", "(instance ?H Human)", "(agent ?D ?H)");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testComplexArgumentsToArrayListAnd1() {

        String stmt = "(and\n" +
                "           (instance ?D Driving)\n" +
                "           (instance ?H Human)\n" +
                "           (agent ?D ?H))";

        Formula f = new Formula(stmt);

        List<String> actual = f.complexArgumentsToArrayListString(1);
        List<String> expected = Lists.newArrayList("(instance ?D Driving)", "(instance ?H Human)", "(agent ?D ?H)");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testComplexArgumentsToArrayListAnd2() {

        String stmt = "(and\n" +
                "           (instance ?D Driving)\n" +
                "           (instance ?H Human)\n" +
                "           (agent ?D ?H))";

        Formula f = new Formula(stmt);

        List<String> actual = f.complexArgumentsToArrayListString(2);
        List<String> expected = Lists.newArrayList("(instance ?H Human)", "(agent ?D ?H)");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testComplexArgumentsToArrayListAnd3() {

        String stmt = "(and\n" +
                "           (instance ?D Driving)\n" +
                "           (instance ?H Human)\n" +
                "           (agent ?D ?H))";

        Formula f = new Formula(stmt);

        List<String> actual = f.complexArgumentsToArrayListString(3);
        List<String> expected = Lists.newArrayList("(agent ?D ?H)");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testComplexArgumentsToArrayListAnd4() {

        String stmt = "(and\n" +
                "           (instance ?D Driving)\n" +
                "           (instance ?H Human)\n" +
                "           (agent ?D ?H))";

        Formula f = new Formula(stmt);

        List<String> actual = f.complexArgumentsToArrayListString(4);

        assertThat(actual).isNull();
    }

    @Test
    public void testComplexArgumentsToArrayListAbsolute() {

        String stmt = "(equal\n" +
                "  (AbsoluteValueFn ?NUMBER1) ?NUMBER2)";
        Formula f = new Formula(stmt);
        String expected = "[(AbsoluteValueFn ?NUMBER1), ?NUMBER2]";
        List<String> actual = f.complexArgumentsToArrayListString(1);
        System.out.println("testComplexArgumentsToArrayListAbsolute(): actual: " + actual);
        System.out.println("testComplexArgumentsToArrayListAbsolute(): expected: " + expected);
        assertThat(actual.toString()).isEqualTo(expected);
    }

    @Test
    public void testComplexArgumentsToArrayList2() {

        String stmt = "(termFormat EnglishLanguage WestMakianLanguage \"west makian language\")";
        Formula f = new Formula(stmt);
        String expected = "";
        List<Formula> l = f.complexArgumentsToArrayList(1);
        System.out.println("testComplexArgumentsToArrayList2(): actual: " + l.size());
        System.out.println("testComplexArgumentsToArrayList2(): expected: " + 3);
        assertThat(3).isEqualTo(l.size());
    }

    @Test
    public void testGetArg() {

        List<String> expected = Lists.newArrayList("during", "?Y", "(WhenFn ?H)");
        List<String> actual = new ArrayList<>();
        Formula f1 = new Formula();
        f1.read("(during ?Y (WhenFn ?H))");
        for (int i = 0; i < 3; i++) {
            String arg = f1.getArgument(i).getFormula();
            System.out.println("testGetArg(): adding: " + arg);
            actual.add(arg);
        }
        System.out.println("testGetArg(): actual: " + actual);
        System.out.println("testGetArg(): expected: " + expected);
        Formula a = f1.getArgument(1);  // test caching of argument list
        String e = "?Y";
        System.out.println("testGetArg(): a: " + a.getFormula());
        System.out.println("testGetArg(): e: " + e);
        assertThat(actual).isEqualTo(expected);
        assertThat(a.toString()).isEqualTo(e);
    }

    @Test
    public void testGetArg2() {

        Formula expected = null;
        Formula f1 = new Formula();
        f1.read("(during ?Y (WhenFn ?H))");
        Formula actual = f1.getArgument(3);
        System.out.println("testGetArg(): actual: " + actual);
        System.out.println("testGetArg(): expected: " + expected);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testGetArgString() {

        List<String> expected = Lists.newArrayList("during", "?Y", "(WhenFn ?H)");
        List<String> actual = new ArrayList<>();
        Formula f1 = new Formula();
        f1.read("(during ?Y (WhenFn ?H))");
        for (int i = 0; i < 3; i++) {
            String arg = f1.getStringArgument(i);
            System.out.println("testGetArgString(): adding: " + arg);
            actual.add(arg);
        }
        System.out.println("testGetArgString(): actual: " + actual);
        System.out.println("testGetArgString(): expected: " + expected);
        assertThat(actual).isEqualTo(expected);
        String a = f1.getStringArgument(1); // test caching of argument list
        String e = "?Y";
        System.out.println("testGetArgString(): a: " + a);
        System.out.println("testGetArgString(): e: " + e);
        assertThat(a).isEqualTo(e);
    }

    @Test
    public void testGetArgString2() {

        String expected = "";
        Formula f1 = new Formula();
        f1.read("(during ?Y (WhenFn ?H))");
        String actual = f1.getStringArgument(3);
        System.out.println("testGetArg(): actual: " + actual);
        System.out.println("testGetArg(): expected: " + expected);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testReplaceQuantifierVars() throws Exception {

        String stmt = "(exists (?X)\n" +
                "        (and\n" +
                "                (instance ?X Organism)\n" +
                "        (part Bio18-1 ?X)))";

        String expected = "(exists (Drosophila)\n" +
                "        (and\n" +
                "                (instance Drosophila Organism)\n" +
                "        (part Bio18-1 Drosophila)))";
        Formula f = new Formula(stmt);
        Formula exp = new Formula(expected);

        List<String> vars = new ArrayList<>();
        vars.add("Drosophila");
        Formula actual = f.replaceQuantifierVars(Formula.EQUANT, vars);
        assertThat(actual.logicallyEquals(exp)).isTrue();

        stmt = "(exists (?JOHN ?KICKS ?CART)\n" +
                "  (and\n" +
                "    (instance ?JOHN Human)\n" +
                "    (instance ?KICKS Kicking)\n" +
                "    (instance ?CART Wagon)\n" +
                "    (patient ?KICKS ?CART)\n" +
                "    (agent ?KICKS ?JOHN)))\n";

        expected = "(exists (Doyle Kick_2 Cart_1)\n" +
                "  (and\n" +
                "    (instance Doyle Human)\n" +
                "    (instance Kick_2 Kicking)\n" +
                "    (instance Cart_1 Wagon)\n" +
                "    (patient Kick_2 Cart_1)\n" +
                "    (agent Kick_2 Doyle)))\n";
        f = new Formula(stmt);
        exp = new Formula(expected);

        vars = new ArrayList<>();
        vars.add("Doyle");
        vars.add("Kick_2");
        vars.add("Cart_1");
        actual = f.replaceQuantifierVars(Formula.EQUANT, vars);
        assertThat(actual.logicallyEquals(exp)).isTrue();

        stmt = "(exists (?ENTITY)\n" +
                "         (and \n" +
                "           (subclass ?ENTITY Animal) \n" +
                "           (subclass ?ENTITY CognitiveAgent)\n" +
                "           (equal ?ENTITY Human)))";

        expected = "(exists (Ent_1)\n" +
                "         (and \n" +
                "           (subclass Ent_1 Animal) \n" +
                "           (subclass Ent_1 CognitiveAgent)\n" +
                "           (equal Ent_1 Human)))";
        f = new Formula(stmt);
        exp = new Formula(expected);

        vars = new ArrayList<>();
        vars.add("Ent_1");
        actual = f.replaceQuantifierVars(Formula.EQUANT, vars);
        assertThat(actual.logicallyEquals(exp)).isTrue();

        stmt = "(exists (?ENTITY)\n" +
                "         (and \n" +
                "           (subclass ?ENTITY ?TEST) \n" +
                "           (subclass ?ENTITY CognitiveAgent)\n" +
                "           (equal ?ENTITY Human)))";

        expected = "(exists (Ent_1)\n" +
                "         (and \n" +
                "           (subclass Ent_1 Ent_1) \n" +
                "           (subclass Ent_1 CognitiveAgent)\n" +
                "           (equal Ent_1 Human)))";
        f = new Formula(stmt);
        exp = new Formula(expected);

        vars = new ArrayList<>();
        vars.add("Ent_1");
        actual = f.replaceQuantifierVars(Formula.EQUANT, vars);
        assertThat(actual.logicallyEquals(exp))
                .as(actual + "\n should not be logically equal to \n" + expected)
                .isFalse();
    }
}