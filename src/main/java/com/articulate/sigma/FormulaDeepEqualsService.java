/*
 * This code is copyright Articulate Software (c) 2003.  Some
 * portions copyright Teknowledge (c) 2003 and reused under the terms of
 * the GNU license.  This software is released under the GNU Public
 * License <http://www.gnu.org/copyleft/gpl.html>.  Users of this code
 * also consent, by use of this code, to credit Articulate Software and
 * Teknowledge in any writings, briefings, publications, presentations,
 * or other representations of any software which incorporates, builds
 * on, or uses this code.  Please cite the following article in any
 * publication with references:
 * <p>
 * Pease, A., (2003). The Sigma Ontology Development Environment, in
 * Working Notes of the IJCAI-2003 Workshop on Ontology and Distributed
 * Systems, August 9, Acapulco, Mexico. See also http://github.com/ontologyportal
 * <p>
 * Authors:
 * Adam Pease
 * Infosys LTD.
 * <p>
 * Formula is an important class that contains information and operations
 * about individual SUO-KIF formulas.
 */
package com.articulate.sigma;

import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Handle operations on an individual formula.  This includes
 * formatting for presentation as well as pre-processing for sending
 * to the inference engine.
 */
@Component
public class FormulaDeepEqualsService {

    private final KBmanager kBManager;

    public FormulaDeepEqualsService(KBmanager kBManager) {
        this.kBManager = kBManager;
    }

    /**
     * Test if the contents of the formula are equal to the argument.
     */
    public boolean deepEquals(Formula f1, Formula f2) {

        //null and simple string equality tests
        if (f2 == null) {
            return false;
        }
        // if the strings are equal or any of the formula strings are null, there is no point on comparing deep
        boolean stringsEqual = Objects.equals(f1.getFormula(), f2.getFormula());
        if (stringsEqual || (f1.getFormula() == null || f2.getFormula() == null)) {
            return stringsEqual;
        }

        Formula cf1 = Clausifier.clausify(f1);
        Formula cf2 = Clausifier.clausify(f2);


        //the normalizeParameterOrder method should be moved to Clausifier
        KB kb = kBManager.getKB(kBManager.getPref("sumokbname"));

        String normalized1 = Formula.normalizeParameterOrder(cf1.getFormula(), kb, true);
        String normalized2 = Formula.normalizeParameterOrder(cf2.getFormula(), kb, true);

        Formula nf1 = new Formula(normalized1);
        Formula nf2 = new Formula(normalized2);


        normalized1 = Clausifier.normalizeVariables(nf1.getFormula(), true); // renumber skolems too
        normalized2 = Clausifier.normalizeVariables(nf2.getFormula(), true);

        return normalized1.equals(normalized2);
    }

    /**
     * Tests if this is logically equal with the parameter formula. It
     * employs three equality tests starting with the
     * fastest and finishing with the slowest:
     * <p>
     * - string comparisons: if the strings of the two formulae are
     * equal return true as the formulae are also equal,
     * otherwise try comparing them by more complex means
     * <p>
     * - compare the predicate structure of the formulae (deepEquals(...)):
     * this comparison only checks if the two formulae
     * have an equal structure of predicates disregarding variable
     * equivalence. Example:
     * (and (instance ?A Human) (instance ?A Mushroom)) according
     * to deepEquals(...) would be equal to
     * (and (instance ?A Human) (instance ?B Mushroom)) even though
     * the first formula uses only one variable
     * but the second one uses two, and as such they are not logically
     * equal. This method generates false positives, but
     * only true negatives. If the result of the comparison is false,
     * we return false, otherwise keep trying.
     * <p>
     * - try to logically unify the formulae by matching the predicates
     * and the variables
     *
     * @param f1, f2
     * @return
     */
    public boolean logicallyEquals(Formula f1, Formula f2) {

        boolean equalStrings = f1.equals(f2);
        if (equalStrings) {
            return true;
        } else if (!this.deepEquals(f1, f2)) {
            return false;
        } else {
            return f1.unifyWith(f2);
        }
    }
}

