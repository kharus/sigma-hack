/* This code is copyright Articulate Software (c) 2003-2011.  Some portions
copyright Teknowledge (c) 2003 and reused under the terms of the GNU license.
This software is released under the GNU Public License <http://www.gnu.org/copyleft/gpl.html>.
Users of this code also consent, by use of this code, to credit Articulate Software
and Teknowledge in any writings, briefings, publications, presentations, or
other representations of any software which incorporates, builds on, or uses this
code.  Please cite the following article in any publication with references:

Pease, A., (2003). The Sigma Ontology Development Environment,
in Working Notes of the IJCAI-2003 Workshop on Ontology and Distributed Systems,
August 9, Acapulco, Mexico. See also http://github.com/ontologyportal
 */
package com.articulate.sigma;

import com.articulate.sigma.nlg.NLGUtils;
import com.articulate.sigma.trans.SUMOKBtoTPTPKB;
import com.articulate.sigma.trans.TPTP2SUMO;
import com.articulate.sigma.trans.TPTP3ProofProcessor;
import com.articulate.sigma.trans.TPTPutil;
import com.articulate.sigma.utils.FileUtil;
import com.articulate.sigma.utils.StringUtil;
import com.articulate.sigma.wordnet.WordNetUtilities;
import tptp_parser.TPTPFormula;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class that creates HTML-formatting Strings for various purposes.
 */
public class HTMLformatter {

    // set by BrowseBody.jsp or SimpleBrowseBody.jsp
    public static String language = "EnglishLanguage";

    public static boolean debug = false;

    /**
     * Create the HTML for a kb link.
     */
    public static String createKBHref(String kbName, String language) {

        return createHrefStart() + "/sigma/Browse.jsp?lang=" + language + "&kb=" + kbName;
    }

    /**
     * Create the HTML for a link, taking care of http/https, hostname and port
     */
    public static String createHrefStart() {

        String hostname = KBmanager.getMgr().getPref("hostname");
        if (hostname == null)
            hostname = "localhost";
        String port = KBmanager.getMgr().getPref("port");
        if (port == null)
            port = "8080";
        String https = KBmanager.getMgr().getPref("https");
        //System.out.println("Info in HTMLformatter.createHrefStart(): https is " + https);
        if (https == null || !https.equals("true"))
            https = "http";
        else
            https = "https";
        return https + "://" + hostname + ":" + port;
    }

    /**
     * Create the HTML for a single step in a proof.
     */
    public static String proofTableFormat(String query, TPTPFormula step, String kbName, String language) {

        if (debug) System.out.println("Info in HTMLformatter.proofTableFormat(): " + step);
        StringBuilder result = new StringBuilder();
        Formula f = new Formula();
        KB kb = KBmanager.getMgr().getKB(kbName);
        f.read(step.sumo);
        f.read(Formula.postProcess(f.getFormula()));
        f.read(ProofProcessor.removeNestedAnswerClause(f.getFormula()));
        String kbHref = HTMLformatter.createKBHref(kbName, language);

        if (f.getFormula().equalsIgnoreCase("FALSE")) {        // Successful resolution theorem proving results in a contradiction.
            f.read("true");                           // Change "FALSE" to "True" so it makes more sense to the user.
            result.append("<td valign=\"top\" width=\"50%\">" + "QED" + "</td>");
        } else
            result.append("<td valign=\"top\" width=\"50%\">" + f.htmlFormat(kbHref) + "</td>");
        result.append("<td valign=\"top\" width=\"10%\">");

        if (debug) System.out.println("Info in HTMLformatter.proofTableFormat(): premises : " + step.supports);
        if (step.infRule != null && step.infRule.equals("assume_negation")) {
            result.append("[Negated Query]");
        } else {
            for (int i = 0; i < step.supports.size(); i++) {
                //String stepName = step.supports.get(i);
                result.append(step.intsupports.get(i) + " ");
            }
            if (step.intsupports.size() == 0) {
                if (step.type != null && step.type.equals("conjecture"))
                    result.append("[Query]");
                else if (!StringUtil.emptyString(step.infRule) &&
                        !step.infRule.equals("input") &&
                        !step.infRule.startsWith("kb_")) {
                    if (KBmanager.getMgr().prover == KBmanager.Prover.VAMPIRE)
                        result.append("[<a href=\"VampProofSteps.html\">" + step.infRule + "</a>]");
                    else
                        result.append("[" + step.infRule + "]");
                } else if (f.getFormula().contains("ans0"))
                    result.append("answer literal introduction");
                else {
                    result.append("[KB -");
                    String key = step.infRule;
                    Formula originalF = SUMOKBtoTPTPKB.axiomKey.get(key);
                    if (originalF != null)
                        result.append(originalF.startLine + ":" + FileUtil.noPath(originalF.getSourceFile()));
                    result.append("]");
                }
            } else if (!StringUtil.emptyString(step.infRule))
                result.append("[<a href=\"VampProofSteps.html\">" + step.infRule + "</a>]");
        }
        result.append("</td><td width=\"40%\" valign=\"top\">");
        if (StringUtil.isNonEmptyString(language)) {
            String pph = NLGUtils.htmlParaphrase(kbHref,
                    f.getFormula(),
                    KBmanager.getMgr().getKB(kbName).getFormatMap(language),
                    KBmanager.getMgr().getKB(kbName).getTermFormatMap(language),
                    kb,
                    language);
            if (StringUtil.emptyString(pph))
                pph = "";
            else {
                pph = NLGUtils.upcaseFirstVisibleChar(pph, true, language);
                boolean isArabic = (language.matches(".*(?i)arabic.*")
                        || language.equalsIgnoreCase("ar"));
                if (isArabic)
                    pph = ("<span dir=\"rtl\">" + pph + "</span>");
                // pph = ("&#x202b;" + pph + "&#x202c;");
            }
            result.append(pph);
        }
        result.append("</td>");
        return result.toString();
    }

    /**
     * Show knowledge base statistics
     */
    public static String showStatistics(KB kb) {

        StringBuilder show = new StringBuilder();
        show.append("<b>Knowledge base statistics: </b><br><table>");
        show.append("<tr bgcolor=#eeeeee><td>Total Terms</td><td>Total Axioms</td><td>Total Rules</td><tr><tr align='center'>\n");
        show.append("<td>  ").append(kb.getCountTerms()).append("</td><td> " + kb.getCountAxioms());
        show.append("</td><td> ").append(kb.getCountRules());
        show.append("</td></tr> </table><p>\n");

        show.append("<table><tr><td>Relations: </td><td align=right>" + kb.getCountRelations() + "</td></tr>\n");
        show.append("<tr><td>non-linguistic axioms: </td><td align=right>" + KButilities.getCountNonLinguisticAxioms(kb) + "</td></tr>\n");
        show.append("</table>\n");

        Map<String, Integer> stats = KButilities.countFormulaTypes(kb);
        show.append("<P><table><tr><td>Ground tuples: </td><td align=right>" + stats.get("ground") + "</td></tr>\n");
        show.append("<tr><td>&nbsp;&nbsp;of which are binary: </td><td align=right>" + stats.get("binary") + "</td></tr>\n");
        show.append("<tr><td>&nbsp;&nbsp;of which arity more than binary: </td><td align=right>" + stats.get("higher-arity") + "</td></tr>\n");
        show.append("</table>\n");

        show.append("<P><table><tr><td>Rules: </td><td align=right>" + kb.getCountRules() + "</td></tr>\n");
        show.append("<tr><td>&nbsp;&nbsp;of which are</td><td> horn: </td><td align=right>" + stats.get("horn") + "</td></tr>\n");
        show.append("<tr><td></td><td> first-order: </td><td align=right>" + stats.get("first-order") + "</td></tr>\n");
        show.append("<tr><td></td><td> temporal: </td><td align=right>" + stats.get("temporal") + "</td></tr>\n");
        show.append("<tr><td></td><td>modal: </td><td align=right>" + stats.get("modal") + "</td></tr>\n");
        show.append("<tr><td></td><td>epistemic: </td><td align=right>" + stats.get("epistemic") + "</td></tr>\n");
        show.append("<tr><td></td><td>other higher-order: </td><td align=right>" + stats.get("otherHOL") + "</td></tr>\n");
        show.append("</table><P>\n");
        return show.toString();
    }

    /**
     * Show a hyperlinked list of term mappings from WordNet.
     */
    public static String termMappingsList(String terms, String kbHref) {

        StringBuilder result = new StringBuilder();
        String[] sumoList = terms.split("\\s+");
        result.append("<p><ul><li>\tSUMO Mappings:  ");
        for (int j = 0; j < sumoList.length; j++) {
            String sumoEquivalent = sumoList[j];
            sumoEquivalent = sumoEquivalent.trim();

            Pattern p = Pattern.compile("\\&\\%");
            Matcher m = p.matcher(sumoEquivalent);
            sumoEquivalent = m.replaceFirst("");
            p = Pattern.compile("[\\=\\|\\+\\@]");
            m = p.matcher(sumoEquivalent);
            char symbol = sumoEquivalent.charAt(sumoEquivalent.length() - 1);
            sumoEquivalent = m.replaceFirst("");
            result.append(kbHref);
            result.append(sumoEquivalent + "\">" + sumoEquivalent + "</a>  ");
            String mapping = WordNetUtilities.mappingCharToName(symbol);
            result.append(" (" + mapping + " mapping) ");
        }
        result.append("\n\n</li></ul>");
        return result.toString();
    }


    /**
     * Change spaces to "%20"
     */
    public static String decodeFromURL(String s) {

        return s.replaceAll("%20", " ");
    }

    /**
     * Create HTML formatted output for a TPTP3 proof
     */
    public static String formatTPTP3ProofResult(TPTP3ProofProcessor tpp, String stmt,
                                                String lineHtml, String kbName, String language) {

        StringBuffer html = new StringBuffer();
        System.out.println("INFO in HTMLformatter.formatTPTP3ProofResult(): number of steps: " + tpp.proof.size());
        if (tpp.proof == null || tpp.proof.size() == 0) {
            html.append("Fail with status: " + tpp.status + "<br>\n");
        }
        if (tpp.bindingMap != null && tpp.bindingMap.keySet().size() > 0) { // if an answer predicate appears in the proof, use it
            for (String s : tpp.bindingMap.keySet()) {
                html.append("Answer " + "\n");
                html.append(s + " = ");
                String term = TPTP2SUMO.transformTerm(tpp.bindingMap.get(s));
                String kbHref = HTMLformatter.createKBHref(kbName, language);
                html.append("<a href=\"" + kbHref + "&term=" + term + "\">" + term + "</a>");
                html.append("<br/>");
            }
        } else {
            for (int i = 0; i < tpp.bindings.size(); i++) {
                //if (i != 0)
                //    html.append(lineHtml + "\n");
                html.append("Answer " + "\n");
                html.append(i + 1);
                html.append(". ");
                String term = TPTP2SUMO.transformTerm(tpp.bindings.get(i));
                String kbHref = HTMLformatter.createKBHref(kbName, language);
                html.append("<a href=\"" + kbHref + "&term=" + term + "\">" + term + "</a>");
                html.append("<br/>");
            }
        }
        html.append("<p><table width=\"95%\">" + "\n");
        for (int l = 0; l < tpp.proof.size(); l++) {
            TPTPFormula ps = tpp.proof.get(l);
            //System.out.println("HTMLformatter.formatTPTP3ProofResult(): role: " + ps.role);
            if (ps.role.equals("type"))
                continue; // ignore type definitions in tff proof output
            if (l % 2 == 1)
                html.append("<tr bgcolor=#EEEEEE>" + "\n");
            else
                html.append("<tr>" + "\n");
            html.append("<td valign=\"top\">" + "\n");
            html.append(ps.id + ".");
            html.append("</td>" + "\n");
            html.append(HTMLformatter.proofTableFormat(stmt, tpp.proof.get(l), kbName, language) + "\n");
            html.append("</tr>\n" + "\n");
        }
        html.append("</table>" + "\n");
        return html.toString();
    }

}

