package com.articulate.sigma.nlg;

import com.articulate.sigma.*;
import com.articulate.sigma.utils.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LanguageFormatter tests specifically targeted toward the htmlParaphrase( ) method.
 */
@SpringBootTest
@Tag("com.articulate.sigma.MidLevel")
@ActiveProfiles("MidLevel")
@Import(KBmanagerTestConfiguration.class)
public class HtmlParaphraseIntegrationITCase {

    private KB kb;

    @Autowired
    private KBmanager kbManager;

    @BeforeEach
    void init() {
        kb = kbManager.getKB(kbManager.getPref("sumokbname"));
    }
    
    /**
     * Ideal: "The oldest customer enters an invalid card."
     */
    @Test
    @Disabled("Doesn't work")
    public void testOldestCustomerEntersCard() {
        String stmt = """
                (exists
                  (?card ?customer ?event ?salesperson)
                  (and
                    (forall
                      (?X)
                      (=>
                        (and
                          (instance ?X customer)
                          (not
                            (equal ?X ?customer)))
                        (and
                          (greaterThan ?val1 ?val2)
                          (age ?customer ?val1)
                          (age ?X ?val2))))
                    (attribute ?card Incorrect)
                    (instance ?card BankCard)
                    (instance ?customer CognitiveAgent)
                    (instance ?event Motion)
                    (instance ?salesperson CognitiveAgent)
                    (patient ?event ?card)
                    (agent ?event ?customer)
                    (customer ?customer ?salesperson)))""";

        String expectedResult = "there exist an object, a cognitive agent, , , a process and another cognitive agent such that for all another object if the other object is an instance of customer and the other object is not equal to the cognitive agent, then a time duration is greater than another time duration and the age of the cognitive agent is the time duration and the age of the other object is the other time duration and Incorrect is an attribute of the object and the object is an instance of bank card and the cognitive agent is an instance of cognitive agent and the process is an instance of motion and the other cognitive agent is an instance of cognitive agent and the object is a patient of the process and the cognitive agent is an agent of the process and the other cognitive agent is a customer of the cognitive agent";
        String actualResult = NLGUtils.htmlParaphrase("", stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }

    /**
     * Ideal: "Bell created the telephone."; also "The telephone was created by Bell."
     */
    @Test
    @Disabled
    public void testHtmlParaphraseBellCreateTelephone() {
        String stmt = """
                (exists
                              (?event ?telephone)
                              (and
                                (instance Bell Human)
                                (agent ?event Bell)
                                (instance ?event Making)
                                (instance ?telephone Telephone)
                                (patient ?event ?telephone)))""";

        String expectedResult = "Bell processes a telephone";
        String actualResult = NLGUtils.htmlParaphrase("", stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }

    /**
     * Ideal: "Bell created the telephone."; also "The telephone was created by Bell."
     */
    @Test
    public void testHtmlParaphraseBlankenshipCreateTelephone() {
        String stmt = """
                (exists
                  (?event ?telephone)
                  (and
                    (instance Blankenship Human)
                    (agent ?event Blankenship)
                    (instance ?event Making)
                    (instance ?telephone Telephone)
                    (patient ?event ?telephone)))""";

        LanguageFormatter languageFormatter = new LanguageFormatter(stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");
        languageFormatter.setDoInformalNLG(false);
        String expectedResult = "there exist a process and an entity such that Blankenship is an instance of human and Blankenship is an agent of the process and the process is an instance of making and the entity is an instance of telephone and the entity is a patient of the process";
        String actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }

    @Test
    @Disabled("Informal NLG Doesn't work")
    public void testHtmlParaphraseBlankenshipCreateTelephoneInformal() {
        String stmt = """
                (exists
                  (?event ?telephone)
                  (and
                    (instance Blankenship Human)
                    (agent ?event Blankenship)
                    (instance ?event Making)
                    (instance ?telephone Telephone)
                    (patient ?event ?telephone)))""";

        LanguageFormatter languageFormatter = new LanguageFormatter(stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");

        languageFormatter.setDoInformalNLG(true);
        String expectedResult = "Blankenship makes a telephone";
        String actualResult = NLGUtils.htmlParaphrase("", stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }

    @Test
    public void testHtmlParaphraseBlankenshipProcessTelephone() {
        String stmt = """
                (exists
                  (?event ?telephone)
                  (and
                    (instance Blankenship Human)
                    (agent ?event Blankenship)
                    (instance ?event Process)
                    (instance ?telephone Telephone)
                    (patient ?event ?telephone)))""";

        LanguageFormatter languageFormatter = new LanguageFormatter(stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");
        languageFormatter.setDoInformalNLG(false);
        String expectedResult = "there exist a process and an entity such that Blankenship is an instance of human and Blankenship is an agent of the process and the process is an instance of process and the entity is an instance of telephone and the entity is a patient of the process";
        String actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }

    @Test
    @Disabled("Informal language doesn't work")
    public void testHtmlParaphraseBlankenshipProcessTelephoneInformal() {
        String stmt = """
                (exists
                  (?event ?telephone)
                  (and
                    (instance Blankenship Human)
                    (agent ?event Blankenship)
                    (instance ?event Process)
                    (instance ?telephone Telephone)
                    (patient ?event ?telephone)))""";

        LanguageFormatter languageFormatter = new LanguageFormatter(stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");


        languageFormatter.setDoInformalNLG(true);
        String expectedResult = "Blankenship processes a telephone";
        String actualResult = NLGUtils.htmlParaphrase("", stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }
    /**
     * Ideal: "If Mary gives John a book then he reads it."
     */
    @Test
    public void testHtmlParaphraseIfMaryGivesBookJohnThenHeReads() {
        String stmt = """
                (forall
                  (?book ?event1)
                  (=>
                    (and
                      (attribute John-1 Male)
                      (attribute Mary-1 Female)
                      (instance John-1 Human)
                      (instance Mary-1 Human)
                      (instance ?book Book)
                      (agent ?event1 Mary-1)
                      (destination ?event1 John-1)
                      (instance ?event1 Giving)
                      (patient ?event1 ?book))
                    (exists
                      (?event2)
                      (and
                           (attribute John-1 Male)
                           (instance John-1 Human)
                           (instance ?book Object)
                           (agent ?event2 John-1)
                           (instance ?event2 Reading)
                           (patient ?event2 ?book)))))""";


        LanguageFormatter languageFormatter = new LanguageFormatter(stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");

        languageFormatter.setDoInformalNLG(false);
        String expectedResult = "for all an entity and a process if male is an attribute of John-1 and female is an attribute of Mary-1 and John-1 is an instance of human and Mary-1 is an instance of human and the entity is an instance of book and Mary-1 is an agent of the process and the process ends up at John-1 and the process is an instance of giving and the entity is a patient of the process, then there exists another process such that male is an attribute of John-1 and John-1 is an instance of human and the entity is an instance of object and John-1 is an agent of the other process and the other process is an instance of reading and the entity is a patient of the other process";
        String actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);

        languageFormatter.setDoInformalNLG(true);
        expectedResult = "if female Mary-1 gives a book to male John-1, then male John-1 reads the book";
        actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }

    /**
     * Ideal: "An old, tall, hungry and thirsty man went to the shop."
     */
    @Test
    public void testHtmlParaphraseManGoToShop() {
        String stmt = """
                (exists
                  (?event ?man ?shop)
                  (and
                    (instance ?event Transportation)
                    (attribute ?man Hungry)
                    (attribute ?man Old)
                    (attribute ?man Tall)
                    (attribute ?man Thirsty)
                    (instance ?man Man)
                    (instance ?shop RetailStore)
                    (agent ?event ?man)
                    (destination ?event ?shop)))""";

        LanguageFormatter languageFormatter = new LanguageFormatter(stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");

        languageFormatter.setDoInformalNLG(false);
        String expectedResult = "there exist a process, an agent and an entity such that the process is an instance of transportation and hungry is an attribute of the agent and Old is an attribute of the agent and Tall is an attribute of the agent and thirsty is an attribute of the agent and the agent is an instance of man and the entity is an instance of retail store and the agent is an agent of the process and the process ends up at the entity";
        String actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);

        languageFormatter.setDoInformalNLG(true);
        expectedResult = "a thirsty Tall Old hungry man performs a transportation to a retail store";
        actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }

    /**
     * Ideal: "The waiter pours soup into the bowl."
     */
    @Test
    @Disabled
    public void testWaiterPoursSoupBowl() {
        String stmt = """
                (exists
                              (?bowl ?event ?soup ?waiter)
                              (and
                                (instance ?bowl Artifact)
                                (instance ?event Pouring)
                                (instance ?soup LiquidFood)
                                (attribute ?waiter ServicePosition)
                                (destination ?event ?bowl)
                                (patient ?event ?soup)
                                (agent ?event ?waiter)))""";

        LanguageFormatter languageFormatter = new LanguageFormatter(stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");

        languageFormatter.setDoInformalNLG(false);
        String expectedResult = "there exist an entity, a process, , , another entity and an agent such that the entity is an instance of artifact and the process is an instance of pouring and the other entity is an instance of liquid food and service position is an attribute of the agent and the process ends at the entity and the other entity is a patient of the process and the agent is an agent of the process";
        String actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);

        languageFormatter.setDoInformalNLG(true);
        expectedResult = "a waiter pours liquid food into a bowl";
        actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }

    /**
     * Ideal: "The man John arrives."
     */
    @Test
    public void testManJohnArrives() {
        String stmt = """
                (exists (?event)
                              (and
                               (instance ?event Arriving)
                               (attribute John-1 Male)
                               (instance John-1 Human)
                               (agent ?event John-1)))""";

        LanguageFormatter languageFormatter = new LanguageFormatter(stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");

        languageFormatter.setDoInformalNLG(false);
        String expectedResult = "there exists a process such that the process is an instance of arriving and male is an attribute of John-1 and John-1 is an instance of human and John-1 is an agent of the process";
        String actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);

        languageFormatter.setDoInformalNLG(true);
        expectedResult = "male John-1 arrives";
        actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }

    /**
     * Ideal: "John arrives."
     */
    @Test
    public void testJohnArrives() {
        String stmt = """
                (exists (?event)
                              (and
                               (instance ?event Arriving)
                               (instance John-1 Human)
                               (agent ?event John-1)))""";

        LanguageFormatter languageFormatter = new LanguageFormatter(stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");

        languageFormatter.setDoInformalNLG(false);
        String expectedResult = "there exists a process such that the process is an instance of arriving and John-1 is an instance of human and John-1 is an agent of the process";
        String actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);

        languageFormatter.setDoInformalNLG(true);
        expectedResult = "John-1 arrives";
        actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }

    @Test
    public void testFishingFishIf() {
        String stmt = """
                (=>
                           (and
                               (instance ?FISHING Fishing)
                               (patient ?FISHING ?TARGET)
                               (instance ?TARGET Animal))
                           (instance ?TARGET Fish))""";


        String expectedResult = "if a process is an instance of fishing and an entity is a patient of the process and the entity is an instance of animal, then the entity is an instance of fish";
        String actualResult = NLGUtils.htmlParaphrase("", stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }

    /**
     * Ideal: FoodForFn animal is an industry product type of food manufacturing
     */
    @Test
    public void testFoodManufacturing() {
        String stmt = "(industryProductType FoodManufacturing\n" +
                "           (FoodForFn Animal))";


        String expectedResult = "food for animal is an industry product type of food manufacturing";
        String actualResult = NLGUtils.htmlParaphrase("", stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }

    @Test
    public void testAnimalShellIf() {
        String stmt = """
                (=>
                           (and
                               (instance ?A Animal)
                               (instance ?S AnimalShell)
                               (part ?S ?A))
                           (or
                               (instance ?A Invertebrate)
                               (instance ?A Reptile)))""";


        String expectedResult = "if an object is an instance of animal and another object is an instance of animal shell and the other object is a part of the object, then the object is an instance of invertebrate or the object is an instance of reptile";
        String actualResult = NLGUtils.htmlParaphrase("", stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }

    @Test
    public void testPlaintiff() {
        String stmt = """
                (exists (?P ?H)
                   (and
                       (instance ?P LegalAction)
                       (instance ?H Human)
                       (plaintiff ?P ?H)))""";

        LanguageFormatter languageFormatter = new LanguageFormatter(stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");
        languageFormatter.setDoInformalNLG(false);
        String expectedResult = "there exist a legal action and a cognitive agent such that the legal action is an instance of legal action and the cognitive agent is an instance of human and the cognitive agent is the plaintiff in the legal action";
        String actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);

        languageFormatter.setDoInformalNLG(true);
        expectedResult = "a human performs a legal action";
        actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }

    @Test
    public void testFlyingAircraft() {
        String stmt = """
                (=>
                           (instance ?FLY FlyingAircraft)
                           (exists (?CRAFT)
                               (and
                                   (instance ?CRAFT Aircraft)
                                   (patient ?FLY ?CRAFT))))""";

        LanguageFormatter languageFormatter = new LanguageFormatter(stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");
        languageFormatter.setDoInformalNLG(false);
        String expectedResult = "if a process is an instance of flying, then there exists an entity such that the entity is an instance of aircraft and the entity is a patient of the process";
        String actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);

        languageFormatter.setDoInformalNLG(true);
        expectedResult = "if someone flies, then an aircraft experiences a flying";
        actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }

    @Test
    public void testWadingWater() {
        String stmt = """
                (=>
                   (instance ?P Wading)
                   (exists (?W)
                       (and
                           (instance ?W WaterArea)
                           (eventLocated ?P ?W))))""";

        LanguageFormatter languageFormatter = new LanguageFormatter(stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");
        languageFormatter.setDoInformalNLG(false);
        String expectedResult = "if a process is an instance of wading, then there exists an object such that the object is an instance of water area and the process is located at the object";
        String actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }

    @Test
    @Disabled("Informal doesn't work")
    public void testWadingWaterInformal() {
        String stmt = """
                (=>
                   (instance ?P Wading)
                   (exists (?W)
                       (and
                           (instance ?W WaterArea)
                           (eventLocated ?P ?W))))""";

        LanguageFormatter languageFormatter = new LanguageFormatter(stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");

        languageFormatter.setDoInformalNLG(true);
        String expectedResult = "if a process is an instance of wading, then there exists an entity such that the entity is an instance of water area and event located the process and the entity";
        String actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }
    @Test
    public void testAnimalBathes() {
        String stmt = """
                (=>
                           (and
                               (instance ?B Bathing)
                               (patient ?B ?A))
                           (instance ?A Animal))""";

        LanguageFormatter languageFormatter = new LanguageFormatter(stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");
        languageFormatter.setDoInformalNLG(false);
        String expectedResult = "if a process is an instance of bathing and an entity is a patient of the process, then the entity is an instance of animal";
        String actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);

        languageFormatter.setDoInformalNLG(true);
        //expectedResult = "if something is bathing, then it's an animal";
        expectedResult = "if a process is an instance of bathing and an entity is a patient of the process, then the entity is an instance of animal";
        actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }

    /**
     * Ideal: "A clean city was built."
     */
    @Test
    public void testHtmlParaphraseCleanCityBeBuilt() {
        String stmt = """
                (exists
                  (?agent ?city ?event)
                  (and
                    (instance ?agent Agent)
                    (instance ?city City)
                    (instance ?event Making)
                    (agent ?event ?agent)
                    (patient ?event ?city)
                    (attribute ?city Clean)))""";

        LanguageFormatter languageFormatter = new LanguageFormatter(stmt, kb.getFormatMap("EnglishLanguage"),
                kb.getTermFormatMap("EnglishLanguage"),
                kb, "EnglishLanguage");

        languageFormatter.setDoInformalNLG(false);
        String expectedResult = "there exist an agent, an object and a process such that the agent is an instance of Agent and the object is an instance of city and the process is an instance of making and the agent is an agent of the process and the object is a patient of the process and clean is an attribute of the object";
        String actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);

        languageFormatter.setDoInformalNLG(true);
        expectedResult = "an Agent makes a clean city";
        actualResult = languageFormatter.htmlParaphrase("");
        assertThat(StringUtil.filterHtml(actualResult)).isEqualTo(expectedResult);
    }

}