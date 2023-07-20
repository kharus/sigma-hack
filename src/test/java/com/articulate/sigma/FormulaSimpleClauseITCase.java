package com.articulate.sigma;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("com.articulate.sigma.TopOnly")
@ActiveProfiles("TopOnly")
@Import(KBmanagerTestConfiguration.class)
public class FormulaSimpleClauseITCase {

    private KB kb;

    @Autowired
    private KBmanager kbManager;

    @Value("${sumokbname}")
    private String sumokbname;

    @BeforeEach
    void init() {
        kb = kbManager.getKB(sumokbname);
    }

    @Test
    @Disabled
    public void testIsSimpleClauseWithFunctionalTerm() {
        Formula f1 = new Formula();
        f1.read("(part (MarialogicalSumFn ?X) ?Y)");

        assertThat(f1.isSimpleClause(kb)).isTrue();
    }

    @Test
    public void testIsSimpleClause1() {
        Formula f1 = new Formula();
        f1.read("(instance ?X Human)");

        assertThat(f1.isSimpleClause(kb)).isTrue();
    }

    @Test
    @Disabled
    public void testIsSimpleClause2() {
        Formula f1 = new Formula();
        f1.read("(member (SkFn 1 ?X3) ?X3)");

        assertThat(f1.isSimpleClause(kb)).isTrue();
    }

    @Test
    public void testIsSimpleClause3() {
        Formula f1 = new Formula();
        f1.read("(member ?VAR1 Org1-1)");

        assertThat(f1.isSimpleClause(kb)).isTrue();
    }

    @Test
    public void testIsSimpleClause4() {
        Formula f1 = new Formula();
        f1.read("(capability (KappaFn ?HEAR (and (instance ?HEAR Hearing) (agent ?HEAR ?HUMAN) " +
                "(destination ?HEAR ?HUMAN) (origin ?HEAR ?OBJ))) agent ?HUMAN)");

        assertThat(f1.isSimpleClause(kb)).isTrue();
    }

    @Test
    public void testNotSimpleClause1() {
        Formula f1 = new Formula();
        f1.read("(=> (attribute ?Agent Investor) (exists (?Investing) (agent ?Investing ?Agent)))");

        assertThat(f1.isSimpleClause(kb)).isFalse();
    }

    @Test
    public void testNotSimpleClause2() {
        Formula f1 = new Formula();
        f1.read("(not (instance ?X Human))");

        assertThat(f1.isSimpleClause(kb)).isFalse();
    }

}