/**
 * This code is copyright Articulate Software (c) 2003.  Some
 * portions copyright Teknowledge (c) 2003 and reused under the termsof the GNU
 * license.  This software is released under the GNU Public License
 * <http://www.gnu.org/copyleft/gpl.html>.  Users of this code also consent,
 * by use of this code, to credit Articulate Software and Teknowledge in any
 * writings, briefings, publications, presentations, or other representations
 * of any software which incorporates, builds on, or uses this code.  Please
 * cite the following article in any publication with references:
 * <p>
 * Pease, A., (2003). The Sigma Ontology Development Environment, in Working
 * Notes of the IJCAI-2003 Workshop on Ontology and Distributed Systems,
 * August 9, Acapulco, Mexico. see also
 * http://sigmakee.sourceforge.net
 */

/**/
package com.articulate.sigma;

import com.articulate.sigma.dataproc.Infrastructure;
import com.articulate.sigma.nlg.NLGUtils;
import com.articulate.sigma.trans.SUMOtoTFAform;
import com.articulate.sigma.utils.MapUtils;
import com.articulate.sigma.utils.StringUtil;
import com.articulate.sigma.wordnet.WordNet;
import com.google.common.collect.Sets;
import org.json.simple.JSONAware;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains utility methods for KBs
 */
public class KButilities {

    public static boolean debug = false;

    /**
     * Errors found during processing formulas
     */
    public static TreeSet<String> errors = new TreeSet<String>();

    public static boolean isFunction(KB kb, String term) {
        return kb.isInstanceOf(term, "Function");
    }

    public static boolean isAttribute(KB kb, String term) {
        return kb.isInstanceOf(term, "Attribute");
    }

    public static boolean hasCorrectTypes(KB kb, Formula f) {

        SUMOtoTFAform.initOnce(kb);
        SUMOtoTFAform.varmap = SUMOtoTFAform.fp.findAllTypeRestrictions(f, kb);
        if (debug) System.out.println("hasCorrectTypes() varmap: " + SUMOtoTFAform.varmap);
        Map<String, Set<String>> explicit = SUMOtoTFAform.fp.findExplicitTypes(kb, f);
        if (debug) System.out.println("hasCorrectTypes() explicit: " + explicit);
        KButilities.mergeToMap(SUMOtoTFAform.varmap, explicit, kb);
        if (SUMOtoTFAform.inconsistentVarTypes()) {
            String error = "inconsistent types in " + SUMOtoTFAform.varmap;
            System.out.println("hasCorrectTypes(): " + SUMOtoTFAform.errors);
            errors.addAll(SUMOtoTFAform.errors);
            return false;
        }
        if (SUMOtoTFAform.typeConflict(f)) {
            String error = "Type conflict: " + SUMOtoTFAform.errors;
            System.out.println("hasCorrectTypes(): " + SUMOtoTFAform.errors);
            errors.addAll(SUMOtoTFAform.errors);
            return false;
        }
        if (debug) System.out.println("hasCorrectTypes() no conflicts in: " + f);
        return true;
    }

    public static boolean isValidFormula(KB kb, String form) {

        SUMOtoTFAform.initOnce(kb);
        String result = "";
        try {
            KIF kif = new KIF();
            result = kif.parseStatement(form);
        } catch (Exception e) {
            e.printStackTrace();
            result = "";
        }
        if (!StringUtil.emptyString(result)) {
            System.out.println("isValidFormula(): Error: " + result);
            return false;
        }
        Formula f = new Formula(form);
        String term = PredVarInst.hasCorrectArity(f, kb);
        if (!StringUtil.emptyString(term)) {
            String error = "Formula rejected due to arity error of predicate " + term
                    + " in formula: \n" + f.getFormula();
            errors.add(error);
            System.out.println("isValidFormula(): Error: " + error);
            return false;
        }
        if (!hasCorrectTypes(kb, f))
            return false;
        if (debug) System.out.println("isValidFormula() valid formula: " + form);
        return true;
    }

    public static boolean isVariableArity(KB kb, String term) {

        return kb.isInstanceOf(term, "VariableArityRelation");
    }

    /**
     * Get count of all the termFormat strings for the given language
     */
    public static int getCountTermFormats(KB kb, String lang) {

        List<Formula> forms = kb.askWithRestriction(0, "termFormat", 1, lang);
        return forms.size();
    }

    /**
     * Get count of all the termFormat strings for unique SUMO terms
     * for the given language.  So if a term has more than one
     * termFormat, only count one
     */
    public static int getCountUniqueTermFormats(KB kb, String lang) {

        List<Formula> forms = kb.askWithRestriction(0, "termFormat", 1, lang);
        Set<String> terms = new HashSet<>();
        for (Formula f : forms) {
            String s = f.getStringArgument(2);
            terms.add(s);
        }
        return terms.size();
    }


    /**
     * Get count of all the different kinds of formulas as to their
     * logical expressivity
     */
    public static Map<String, Integer> countFormulaTypes(KB kb) {

        Map<String, Integer> result = new HashMap<>();
        for (Formula f : kb.formulaMap.values()) {
            if (f.isRule()) {
                MapUtils.addToFreqMap(result, "rules", 1);
                if (f.isHorn(kb))
                    MapUtils.addToFreqMap(result, "horn", 1);
                if (f.isHigherOrder(kb)) {
                    if (f.isModal(kb))
                        MapUtils.addToFreqMap(result, "modal", 1);
                    if (f.isEpistemic(kb))
                        MapUtils.addToFreqMap(result, "epistemic", 1);
                    if (f.isTemporal(kb))
                        MapUtils.addToFreqMap(result, "temporal", 1);
                    if (f.isOtherHOL(kb))
                        MapUtils.addToFreqMap(result, "otherHOL", 1);
                } else
                    MapUtils.addToFreqMap(result, "first-order", 1);
            } else {
                if (f.isGround())
                    MapUtils.addToFreqMap(result, "ground", 1);
                if (f.isBinary())
                    MapUtils.addToFreqMap(result, "binary", 1);
                else
                    MapUtils.addToFreqMap(result, "higher-arity", 1);
            }
        }
        return result;
    }

    /**
     * convert the numerical result of compare() to text
     */
    public static String eqNum2Text(int val) {

        switch (val) {
            case -1:
                return "is shallower than";
            case 0:
                return "is equal to";
            case 1:
                return "is deeper than";
        }
        return "error bad value from KButilities.eqNum2Text()";
    }

    /**
     * Get all formulas that contain both terms.
     */
    public static List<Formula> termIntersection(KB kb, String term1, String term2) {

        List<Formula> ant1 = kb.ask("ant", 0, term1);
        List<Formula> ant2 = kb.ask("ant", 0, term2);
        List<Formula> cons1 = kb.ask("cons", 0, term1);
        List<Formula> cons2 = kb.ask("cons", 0, term2);
        Set<Formula> hrule1 = new HashSet<Formula>();
        hrule1.addAll(ant1);
        hrule1.addAll(cons1);
        Set<Formula> hrule2 = new HashSet<Formula>();
        hrule2.addAll(ant2);
        hrule2.addAll(cons2);
        List<Formula> result = new ArrayList<Formula>();
        result.addAll(hrule1);
        result.retainAll(hrule2);
        List<Formula> stmt1 = kb.ask("stmt", 0, term1);
        List<Formula> stmt2 = kb.ask("stmt", 0, term2);
        stmt1.retainAll(stmt2);
        result.addAll(stmt1);
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (j != i) {
                    List<Formula> stmt = kb.askWithRestriction(i, term1, j, term2);
                    result.addAll(stmt);
                }
            }
        }
        return result;
    }

    public static boolean isCacheFile(String filename) {

        if (StringUtil.emptyString(filename))
            return false;
        return filename.endsWith("_Cache.kif");
    }

    public static int getCountNonLinguisticAxioms(KB kb) {

        Set<String> rels = new HashSet<>();
        rels.add("documentation");
        rels.add("termFormat");
        rels.add("format");
        int counter = 0;
        Set<Formula> forms = new HashSet<>();
        forms.addAll(kb.formulaMap.values());
        for (Formula f : forms) {
            if (!rels.contains(f.getArgument(0)))
                counter++;
        }
        return counter;
    }

    /**
     * utility method to merge two Maps of String keys and a values
     * of an Set of Strings.  Note that parent classes in the set of
     * classes will be removed
     */
    public static Map<String, Set<String>> mergeToMap(Map<String, Set<String>> map1,
                                                      Map<String, Set<String>> map2, KB kb) {

        Map<String, Set<String>> result = new HashMap<String, Set<String>>(map1);

        for (String key : map2.keySet()) {
            Set<String> value = new HashSet<String>();
            if (result.containsKey(key)) {
                value = result.get(key);
            }
            value.addAll(map2.get(key));
            value = kb.removeSuperClasses(value);
            result.put(key, Sets.newHashSet(value));
        }
        return result;
    }

    public static void showHelp() {

        System.out.println("KButilities class");
        System.out.println("  options:");
        System.out.println("  -h - show this help screen");
        System.out.println("  -c <fname> - generate external links from file fname");
        System.out.println("  -s - count strings and processes");
        System.out.println("  -d - generate semantic network as .dot");
        System.out.println("  -j - generate semantic network as JSON");
        System.out.println("  -o - generate semantic network as another JSON format");
        System.out.println("  -q - generate semantic network as SQL");
        System.out.println("  -r - generate semantic network as |-delimited tripls");
        System.out.println("  -n - generate NL for every formula");
        System.out.println("  -f - list formulas for every documentation string term");
        System.out.println("  -v - is formula valid");
        System.out.println("  -a \"<formula>\" - show all attributes of a SUO-KIF formula");
        System.out.println("  -t - generate a table of termFormat(s)");
    }

}

