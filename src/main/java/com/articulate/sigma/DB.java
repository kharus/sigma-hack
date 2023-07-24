/**
 * This code is copyrighted by Articulate Software (c) 2007.  It is
 * released under the GNU Public License &lt;http://www.gnu.org/copyleft/gpl.html&gt;."\""
 * Users of this code also consent, by use of this code, to credit
 * Articulate Software in any writings, briefings, publications,
 * presentations, or other representations of any software which
 * incorporates, builds on, or uses this code.  Please cite the following
 * article in any publication with references:
 * Pease, A., (2003). The Sigma Ontology Development Environment, in Working
 * Notes of the IJCAI-2003 Workshop on Ontology and Distributed Systems,
 * August 9, Acapulco, Mexico.  See also http://sigmakee.sourceforge.net.
 */
/**/
package com.articulate.sigma;

import com.articulate.sigma.dataproc.Hotel;
import com.articulate.sigma.utils.AVPair;
import com.articulate.sigma.utils.StringUtil;
import com.articulate.sigma.wordnet.WSD;
import com.articulate.sigma.wordnet.WordNet;
import com.articulate.sigma.wordnet.WordNetUtilities;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class to interface with databases and database-like formats,
 * such as spreadsheets.
 */
public class DB {
    // a map of word keys, broken down by POS, listing whether it's a positive or negative word
    // keys are pre-defined as type, POS, stemmed, polarity
    public static Map<String, Map<String, String>> sentiment = new HashMap<String, Map<String, String>>();

    /**
     * Parse the input from a Reader for a CSV file into an List
     * of Lists.  If lineStartTokens is a non-empty list, all
     * lines not starting with one of the String tokens it contains
     * will be concatenated.  ';' denotes a comment line and will be skipped
     *
     * @param inReader        A reader for the file to be processed
     * @param lineStartTokens If a List containing String tokens, all
     *                        lines not starting with one of the tokens will be concatenated
     * @param quote           signifies whether to retain quotes in elements
     * @return An List of Lists
     */
    public static List<List<String>> readSpreadsheet(Reader inReader, List<String> lineStartTokens, boolean quote, char delimiter) {

        long t1 = System.currentTimeMillis();
        System.out.println("ENTER DB.readSpreadsheet(" + inReader + ", " + lineStartTokens + ")");
        List<List<String>> rows = new ArrayList<List<String>>();
        String line = null;
        StringBuilder cell = null;
        String cellVal = null;
        try {
            LineNumberReader lr = new LineNumberReader(inReader);
            List<String> textrows = new ArrayList<String>();
            int trLen = 0;
            boolean areTokensListed = ((lineStartTokens != null) && !lineStartTokens.isEmpty());
            boolean skippingHeader = true;
            while ((line = lr.readLine()) != null) {
                if (skippingHeader) {
                    skippingHeader = line.startsWith(";") || line.trim().isEmpty();
                }
                if (!skippingHeader) {
                    try {
                        if (StringUtil.containsNonAsciiChars(line))
                            System.out.println("\nINFO in DB.readSpreadsheet(): NonASCII char near line " + lr.getLineNumber() + ": " + line + "\n");
                        line += " ";
                        // concatenate lines not starting with one of the
                        // tokens in lineStartTokens.
                        boolean concat = false;
                        if (areTokensListed) {
                            String unquoted = StringUtil.unquote(line);
                            String token = null;
                            for (Iterator<String> it = lineStartTokens.iterator(); it.hasNext(); ) {
                                token = it.next();
                                if (unquoted.startsWith(token)) {
                                    concat = true;
                                    break;
                                }
                            }
                        }
                        if (concat && !textrows.isEmpty()) {
                            trLen = textrows.size();
                            String previousLine = textrows.get(trLen - 1);
                            line = previousLine + line;
                            textrows.remove(trLen - 1);
                            textrows.add(line);
                        } else
                            textrows.add(line);
                    } catch (Exception ex1) {
                        System.out.println("ERROR in ENTER DB.readSpreadsheet(" + inReader + ", " + lineStartTokens + ")");
                        System.out.println("  approx. line # == " + lr.getLineNumber());
                        System.out.println("  line == " + line);
                        ex1.printStackTrace();
                    }
                }
            }
            try {
                if (lr != null) {
                    lr.close();
                } // Close the input stream.
            } catch (Exception lre) {
                lre.printStackTrace();
            }
            cell = new StringBuilder();
            for (Iterator<String> itr = textrows.iterator(); itr.hasNext(); ) {
                // parse comma delimited cells into an List
                line = itr.next();
                int linelen = line.length();
                cell.setLength(0);
                List<String> row = new ArrayList<String>();
                boolean inString = false;
                for (int j = 0; j < linelen; j++) {
                    if ((line.charAt(j) == delimiter) && !inString) {
                        cellVal = cell.toString();
                        // cellVal = cellVal.trim()
                        if (cellVal.matches(".*\\w+.*"))
                            cellVal = cellVal.trim();
                        if (!quote)
                            cellVal = StringUtil.removeEnclosingQuotes(cellVal);
                        row.add(cellVal);
                        cell.setLength(0);
                        // cell = new StringBuilder();
                    } else {
                        if ((line.charAt(j) == '"') && ((j == 0) || (line.charAt(j - 1) != '\\')))
                            inString = !inString;
                        cell.append(line.charAt(j));
                    }
                }
                cellVal = cell.toString();
                // cellVal = cellVal.trim();
                if (cellVal.matches(".*\\w+.*"))
                    cellVal = cellVal.trim();
                if (!quote)
                    cellVal = StringUtil.removeEnclosingQuotes(cellVal);
                row.add(cellVal);
                rows.add(row);
            }
        } catch (Exception e) {
            System.out.println("ERROR in DB.readSpreadsheet(" + inReader + ", " + lineStartTokens + ")");
            System.out.println("  line == " + line);
            System.out.println("  cell == " + cell.toString());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        System.out.println("EXIT DB.readSpreadsheet(" + inReader + ", " + lineStartTokens + ")");
        System.out.println("  rows == [list of " + rows.size() + " rows]");
        System.out.println("  " + ((System.currentTimeMillis() - t1) / 1000.0) + " seconds elapsed time");
        return rows;
    }

    /**
     * Parse a CSV file into an List of Lists.  If
     * lineStartTokens is a non-empty list, all lines not starting
     * with one of the String tokens it contains will be concatenated.
     *
     * @param fname           The pathname of the CSV file to be processed
     * @param lineStartTokens If a List containing String tokens, all
     *                        lines not starting with one of the tokens will be concatenated
     * @param quote           signifies whether to retain quotes in elements
     * @return An List of Lists
     */
    public static List<List<String>> readSpreadsheet(String fname, List<String> lineStartTokens,
                                                     boolean quote, char delimiter) {

        System.out.println("ENTER DB.readSpreadsheet(" + fname + ", " + lineStartTokens + ")");
        List<List<String>> rows = new ArrayList<>();
        try {
            FileReader fr = new FileReader(fname);
            rows = readSpreadsheet(fr, lineStartTokens, quote, delimiter);
        } catch (Exception e) {
            System.out.println("Error in DB.readSpreadsheet()");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        System.out.println("EXIT DB.readSpreadsheet(" + fname + ", " + lineStartTokens + ")");
        return rows;
    }

    public static List<List<String>> readSpreadsheet(String fname, List<String> lineStartTokens,
                                                     boolean quote) {

        return readSpreadsheet(fname, lineStartTokens, quote, ',');
    }

    private static boolean isInteger(String input) {

        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param quote signifies whether to quote entries from the spreadsheet
     */
    public static String writeSpreadsheetLine(List<String> al, boolean quote) {

        StringBuffer result = new StringBuffer();
        for (int j = 0; j < al.size(); j++) {
            String s = al.get(j);
            if (quote && !isInteger(s))
                result.append("\"" + s + "\"");
            else
                result.append(s);
            if (j < al.size())
                result.append(",");
        }
        result.append("\n");
        return result.toString();
    }

    /**
     * @param quote signifies whether to quote entries from the spreadsheet
     */
    public static String writeSpreadsheet(List<List<String>> values, boolean quote) {

        StringBuffer result = new StringBuffer();
        for (int i = 0; i < values.size(); i++) {
            List<String> al = values.get(i);
            result.append(writeSpreadsheetLine(al, quote));
        }
        return result.toString();
    }

    public static boolean emptyString(String input) {
        return StringUtil.emptyString(input);
    }

    public static List<String> fill(String value, int count) {

        List<String> line = new ArrayList<String>();
        for (int i = 0; i < count; i++)
            line.add(value);
        return line;
    }

    /**
     * Fill out from a CSV file a map of word keys, and values broken down by POS,
     * listing whether it's a positive or negative word interior hash map keys are
     * type, POS, stemmed, polarity
     *
     * @return void side effect on static variable "sentiment"
     */
    public static void readSentimentArray(Path kbDir) {

        if (sentiment.size() > 0) {
            System.out.println("Error in DB.readSentimentArray(): file previously read.");
            return;
        }

        List<List<String>> f = DB.readSpreadsheet(kbDir +
                File.separator + "WordNetMappings" + File.separator + "sentiment.csv", null, false);
        for (List<String> al : f) {
            Map<String, String> entry = new HashMap<String, String>();
            entry.put("type", al.get(0));   // weak, strong
            entry.put("POS", al.get(2));    // noun,verb,adj,adverb,anypos
            entry.put("stemmed", al.get(3));   // y,n
            entry.put("polarity", al.get(4));  // positive, negative
            sentiment.put(al.get(1), entry);
        }
    }

    /**
     * Calculate an integer sentiment value for a string of words.
     */
    public static int computeSentiment(String input) {

        String description = WordNet.wn.removeStopWords(input.trim());
        description = StringUtil.removePunctuation(description);
        String[] words = description.split(" ");
        int total = 0;
        for (int i = 0; i < words.length; i++)
            total = total + computeSentimentForWord(words[i]);
        return total;
    }

    /**
     * Find the sentiment value for a given word, after finding the root
     * form of the word.
     */
    public static int computeSentimentForWord(String word) {

        //System.out.println("INFO in DB.computeSentimentForWord() word: " + word);
        if (sentiment.keySet().size() < 1) {
            System.out.println("Error in DB.computeSentimentForWord() sentiment list not loaded.");
            return 0;
        }
        String nounroot = WordNet.wn.nounRootForm(word, word.toLowerCase());
        String verbroot = WordNet.wn.verbRootForm(word, word.toLowerCase());
        Map<String, String> hm = null;
        if (sentiment.containsKey(word))
            hm = sentiment.get(word);
        else if (!word.equals(verbroot) && sentiment.containsKey(verbroot))
            hm = sentiment.get(verbroot);
        else if (!word.equals(nounroot) && sentiment.containsKey(nounroot))
            hm = sentiment.get(nounroot);
        if (hm != null) {
            int multiplier = 0;
            if (hm.get("type").equals("weak"))
                multiplier = 1;
            if (hm.get("type").equals("strong"))
                multiplier = 5;
            if (hm.get("polarity").equals("neutral"))
                multiplier = 0;
            if (hm.get("polarity").equals("positive"))
                return multiplier;
            else
                return -multiplier;
        }
        return 0;
    }

    /**
     * Add new scores to existing scores.  Note the side effect on scores.
     *
     * @return a map of concept keys and integer sentiment score values
     */
    public static Map<String, Integer> addConceptSentimentScores(Map<String, Integer> scores,
                                                                 String SUMOs, int total) {

        String[] terms = SUMOs.split(" ");
        for (int i = 0; i < terms.length; i++) {
            String term = terms[i].trim();
            if (!StringUtil.emptyString(term)) {
                int newTotal = total;
                if (scores.containsKey(term))
                    newTotal = total + scores.get(term).intValue();
                scores.put(term, Integer.valueOf(newTotal));
            }
        }
        return scores;
    }

    /**
     * Associate individual concepts with a sentiment score
     *
     * @return a map of concept keys and integer sentiment score values
     */
    public static Map<String, Integer> computeConceptSentiment(String input) {

        System.out.println("INFO in DB.computeConceptSentiment(): " + input);
        Map<String, Integer> result = new HashMap<String, Integer>();
        String paragraph = WordNet.wn.removeStopWords(input.trim());
        paragraph = StringUtil.removeHTML(paragraph);
        String[] sentences = paragraph.split("[\\.\\/\\!]");
        for (int i = 0; i < sentences.length; i++) {  // look at each sentence
            String sentence = StringUtil.removePunctuation(sentences[i]);
            String[] words = sentence.split(" ");
            int total = 0;
            for (int j = 0; j < words.length; j++)    // look at each word
                total = total + computeSentimentForWord(words[j]);
            List<String> SUMOal = WSD.collectSUMOFromWords(sentence);
            String SUMOs = StringUtil.ListToSpacedString(SUMOal);
            System.out.println("INFO in DB.computeConceptSentiment(): done collecting SUMO terms: " + SUMOs + " from input: " + sentence);
            result = addConceptSentimentScores(result, SUMOs, total);
        }
        return result;
    }

}
