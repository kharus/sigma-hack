package com.articulate.sigma;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("com.articulate.sigma.MidLevel")
@ActiveProfiles("MidLevel")
@Import(KBmanagerTestConfiguration.class)
public class KbIntegrationITCase {

    private KB kb;

    @Autowired
    private KBmanager kbManager;

    @BeforeEach
    void init() {
        kb = kbManager.getKB(kbManager.getPref("sumokbname"));
    }
    @Test
    public void testIsChildOf3() {

        KBcache cache = kb.kbCache;
        System.out.println("parents of Shirt (as instance): " + cache.getParentClassesOfInstance("Shirt"));
        System.out.println("parents of Shirt: " + cache.parents.get("subclass").get("Shirt"));
        System.out.println("KBcache.childOfP(subclass, WearableItem, Shirt): " + cache.childOfP("subclass", "WearableItem", "Shirt"));
        System.out.println("kb.isChildOf(Shirt, WearableItem): " + kb.isChildOf("Shirt", "WearableItem"));
        System.out.println("kb.childOf(Shirt, WearableItem): " + kb.childOf("Shirt", "WearableItem"));
        assertThat(kb.isSubclass("Shirt", "WearableItem")).isTrue();
    }

    @Test
    public void testAskWithTwoRestrictionsDirect2() {
        List<Formula> actual = kb.askWithTwoRestrictions(0, "subclass", 1, "Boy", 2, "Man");
        assertThat(actual.size()).isNotEqualTo(0);
    }

    @Test
    public void testIsSubclass1() {

        KBcache cache = kb.kbCache;
        System.out.println("parents of Boy (as instance): " + cache.getParentClassesOfInstance("Boy"));
        System.out.println("parents of Boy: " + cache.parents.get("Boy"));
        System.out.println("childOfP(\"Boy\", \"Entity\"): " + cache.childOfP("subclass", "Entity", "Boy"));
        System.out.println("kb.isChildOf(\"Boy\", \"Entity\"): " + kb.isChildOf("Boy", "Entity"));
        assertThat(kb.isSubclass("Boy", "Entity")).isTrue();
    }

    @Test
    public void testIsHigherOrder() {

        String stmt;
        stmt = "(=> (and (instance ?GUN Gun) (effectiveRange ?GUN ?LM) " +
                "(distance ?GUN ?O ?LM1) (instance ?O Organism) (not (exists (?O2) " +
                "(between ?O ?O2 ?GUN))) (lessThanOrEqualTo ?LM1 ?LM)) " +
                "(capability (KappaFn ?KILLING (and (instance ?KILLING Killing) " +
                "(patient ?KILLING ?O))) instrument ?GUN))";
        Formula f = new Formula(stmt);
        assertThat(f.isHigherOrder(kb)).isTrue();
    }

    @Test
    public void testIsHigherOrder2() {

        String stmt;
        stmt = "(and\n" +
                "  (instance Tunnel1 Tunnel)\n" +
                "  (equal ?P (AfternoonFn Tunnel))\n" +  // should be TransitFn but that's not in Merge.kif
                "  (holeMouth M1 Tunnel1)\n" +
                "  (holeMouth M2 Tunnel1)\n" +
                "  (not\n" +
                "    (equal M1 M2))\n" +
                "  (not\n" +
                "    (connected M1 M2))\n" +
                "  (located Jane M1)\n" +
                "  (origin ?P M1)\n" +
                "  (destination ?P M2)\n" +
                "  (agent ?P John)\n" +
                "  (length Tunnel1 L))";
        Formula f = new Formula(stmt);
        System.out.println("testIsHigherOrder2: " + f);
        System.out.println("isHigherOrder: " + f.isHigherOrder(kb));
        assertThat(f.isHigherOrder(kb)).isFalse();
    }

    @Test
    public void testIsHigherOrder3() {

        String stmt = "(=>\n" +
                "  (and\n" +
                "    (instance ?F Function)\n" +
                "    (rangeSubclass ?F ?C)\n" +
                "    (instance ?I (?F ?X)))\n" +
                "  (instance ?I ?C))";
        Formula f = new Formula(stmt);
        System.out.println("testIsHigherOrder3: " + f);
        System.out.println("testIsHigherOrder3: results: " + f.isHigherOrder(kb));
        System.out.println("testIsHigherOrder3(): expected: " + false);
        if (!f.isHigherOrder(kb))
            System.out.println("testIsHigherOrder3(): success!");
        else
            System.out.println("testIsHigherOrder3(): failure");
        assertThat(f.isHigherOrder(kb)).isFalse();
    }

    @Test
    public void testIsHigherOrder4() {

        String stmt = "(=> " +
                "(instance ?N SCNuclearSilo) " +
                "(hasPurpose ?N " +
                "  (exists (?M ?W) " +
                "    (and " +
                "      (instance ?M Manufacture) " +
                "      (located ?M ?N) " +
                "      (instance ?W NuclearWeapon) " +
                "      (result ?M ?W))))";
        Formula f = new Formula(stmt);
        System.out.println("testIsHigherOrder4: " + f);
        System.out.println("testIsHigherOrder4: results: " + f.isHigherOrder(kb));
        System.out.println("testIsHigherOrder4(): expected: " + true);
        if (f.isHigherOrder(kb))
            System.out.println("testIsHigherOrder4(): success!");
        else
            System.out.println("testIsHigherOrder4(): failure");
        assertThat(f.isHigherOrder(kb)).isTrue();
    }
}