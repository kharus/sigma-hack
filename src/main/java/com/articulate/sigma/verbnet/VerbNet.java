/*
 * This code is copyright Infosys 2019.
 * This software is released under the GNU Public License <http://www.gnu.org/copyleft/gpl.html>.
 * <p>
 * Pease, A., (2003). The Sigma Ontology Development Environment,
 * in Working Notes of the IJCAI-2003 Workshop on Ontology and Distributed Systems,
 * August 9, Acapulco, Mexico. See also http://github.com/ontologyportal
 * <p>
 * Authors:
 * Adam Pease
 * Infosys LTD.
 */
package com.articulate.sigma.verbnet;

import com.articulate.sigma.KBmanager;
import com.articulate.sigma.SimpleDOMParser;
import com.articulate.sigma.SimpleElement;
import com.articulate.sigma.utils.StringUtil;
import com.articulate.sigma.wordnet.WordNet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * Created by apease on 7/23/18.
 */
public class VerbNet {

    private static final boolean echo = false;
    private static final Map<String, SimpleElement> verbFiles = new HashMap<>();
    public static int verbcount = 0;
    public static int syncount = 0;
    // a mapping of a WordNet key to a VerbNet pair of VerbID\tmember-word-name
    public static Map<String, String> wnMapping = new HashMap<>();
    // verb ID keys and Verb values
    public static Map<String, Verb> verbs = new HashMap<>();
    public static boolean disable = false;

    public static void initOnce() {
        readVerbFiles();
    }

    public static void readVerbFiles() {

        SimpleElement configuration = null;
        try {
            String dirStr = KBmanager.getMgr().getPref("verbnet");
            System.out.println("VerbNet.readVerbFiles(): loading files from: " + dirStr);
            File dir = new File(dirStr);
            if (!dir.exists()) {
                System.out.println("VerbNet.readVerbFiles(): no such dir: " + dirStr);
                return;
            }
            try {
                File folder = new File(dirStr);
                for (File fileEntry : folder.listFiles()) {
                    if (!fileEntry.toString().endsWith(".xml"))
                        continue;
                    BufferedReader br = new BufferedReader(new FileReader(fileEntry.toString()));
                    SimpleDOMParser sdp = new SimpleDOMParser();
                    verbFiles.put(fileEntry.toString(), sdp.parse(br));
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error in VerbNet.readVerbFiles(): " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Error in VerbNet.readVerbFiles(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void processVerbs() {

        for (String fname : verbFiles.keySet()) {
            if (echo) System.out.println("\n==================");
            if (echo) System.out.println("VerbNet.processVerbs(): " + fname);
            SimpleElement verb = verbFiles.get(fname);
            String name = verb.getAttribute("ID");
            verbcount++;
            Verb v = new Verb();
            v.ID = name;
            v.readVerb(verb);
            verbs.put(name, v);
        }
    }

    private static String formatForSynset(String synset) {

        StringBuffer result = new StringBuffer();
        String verb = VerbNet.wnMapping.get(synset);
        if (StringUtil.emptyString(verb) || !verb.contains("|"))
            return "";
        String ID = verb.substring(0, verb.indexOf("|"));
        String link = "<a href=\"http://verbs.colorado.edu/verb-index/vn/" + ID + ".php\">" + verb + "</a>, ";
        if (!result.toString().contains(verb))
            result.append(link);
        return result.toString();
    }

    /**
     * @param tm Map of words with their corresponding synset numbers
     */
    public static String formatVerbsList(TreeMap<String, List<String>> tm) {

        StringBuffer result = new StringBuffer();
        int count = 0;
        Iterator<String> it = tm.keySet().iterator();
        while (it.hasNext() && count < 50) {
            String word = it.next();
            List<String> synsetList = tm.get(word);
            for (int i = 0; i < synsetList.size(); i++) {
                String synset = synsetList.get(i);
                String res = formatForSynset(synset);
                if (StringUtil.emptyString(res))
                    continue;
                if (StringUtil.emptyString(result.toString()))
                    result.append("VerbNet: ");
                result.append(res);
                count++;
            }
        }
        if (it.hasNext() && count >= 50)
            result.append("...");
        return result.toString();
    }

    /**
     * @param tm Map of words with their corresponding synset numbers
     */
    public static String formatVerbs(TreeMap<String, String> tm) {

        StringBuffer result = new StringBuffer();
        int count = 0;
        Iterator<String> it = tm.keySet().iterator();
        while (it.hasNext() && count < 50) {
            String word = it.next();
            String synset = tm.get(word);
            String res = formatForSynset(synset);
            if (StringUtil.emptyString(res))
                continue;
            if (StringUtil.emptyString(result.toString()))
                result.append("VerbNet: ");
            result.append(res);
            count++;
        }
        if (it.hasNext() && count >= 50)
            result.append("...");
        return result.toString();
    }

    public static void main(String[] args) {

        KBmanager.getMgr().initializeOnce();
        System.out.println("VerbNet.main()");
        initOnce();
        processVerbs();
        System.out.println("# of verbs: " + verbcount);
        System.out.println("# of mapped synsets: " + syncount);
        System.out.println("VerbNet.main(): get vb for wn 200686447: " + VerbNet.wnMapping.get("200686447"));
    }
}