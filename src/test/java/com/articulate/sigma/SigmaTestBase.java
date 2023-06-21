package com.articulate.sigma;

import com.articulate.sigma.nlg.NLGUtils;
import com.articulate.sigma.wordnet.WordNet;
import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SigmaTestBase {

    static final String SIGMA_HOME = System.getenv("SIGMA_HOME");
    protected static final String KB_PATH = (new File(SIGMA_HOME, "KBs")).getAbsolutePath();

    protected static KB kb;

    /**
     * Performs the KB load.
     *
     * @param reader
     */
    protected static void doSetUp(BufferedReader reader) {

        SimpleElement configuration = null;
        if (!KBmanager.initialized) {
            try {
                SimpleDOMParser sdp = new SimpleDOMParser();
                configuration = sdp.parse(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }

            KBmanager.getMgr().setDefaultAttributes();
            KBmanager.getMgr().setConfiguration(configuration);
            KBmanager.initialized = true;
        }
        kb = KBmanager.getMgr().getKB(KBmanager.getMgr().getPref("sumokbname"));
        checkConfiguration();
    }

    public static void checkConfiguration() {

        List<String> problemList = Lists.newArrayList();
        if (NLGUtils.getKeywordMap() == null || NLGUtils.getKeywordMap().isEmpty()) {
            problemList.add("LanguageFormatter.keywordMap is empty.");
        }
        if (WordNet.wn.synsetsToWords.isEmpty()) {
            problemList.add("WordNet mappings are empty.");
        }
        List kbnames = Arrays.asList("english_format.kif", "domainEnglishFormat.kif",
                "Merge.kif", "Mid-level-ontology.kif", "ArabicCulture.kif", "Cars.kif",
                "Catalog.kif", "Communications.kif", "CountriesAndRegions.kif", "Dining.kif",
                "Economy.kif", "engineering.kif", "FinancialOntology.kif", "Food.kif",
                "Geography.kif", "Government.kif", "Hotel.kif", "Justice.kif", "Languages.kif",
                "Media.kif", "MilitaryDevices.kif", "Military.kif", "MilitaryPersons.kif",
                "MilitaryProcesses.kif", "Music.kif", "naics.kif", "People.kif",
                "QoSontology.kif", "Sports.kif", "TransnationalIssues.kif", "Transportation.kif",
                "TransportDetail.kif", "VirusProteinAndCellPart.kif", "WMD.kif");

        if (KBmanager.getMgr().getKBnames().containsAll(kbnames)) {
            problemList.add("KB missing one or more files. Expected: " + kbnames +
                    " actual:" + KBmanager.getMgr().getKBnames());
        }
        if (!problemList.isEmpty()) {
            StringBuilder sBuild = new StringBuilder();
            for (String problem : problemList) {
                final String NEWLINE_AND_SPACES = "\n   ";
                sBuild.append(NEWLINE_AND_SPACES).append(problem);
            }
            System.out.println("Configuration failed. Problems:" + sBuild);
            throw new IllegalStateException("Configuration failed. Problems:" + sBuild);
        }
    }

    /**
     * Gets a BufferedReader for the xml file that is this test's configuration.
     *
     * @param path
     * @param theClass
     * @return
     */
    protected static BufferedReader getXmlReader(String path, Class theClass) {

        BufferedReader xmlReader = null;
        try {
            //URI uri = theClass.getClassLoader().getResource(path).toURI();
            //URI uri = theClass.getResource(path).toURI();
            //File configFile = new File(uri);
            // String contents = StringUtil.getContents(configFile);
            //contents = contents.replaceAll("\\$SIGMA_HOME", SIGMA_HOME);
            //xmlReader = new BufferedReader(new StringReader(path));
            xmlReader = new BufferedReader(new FileReader(path, StandardCharsets.UTF_8));
            //xmlReader = new BufferedReader(new InputStreamReader(theClass.getResourceAsStream(path)));
        } catch (Exception ex) {
            //try {
            //URI uri = theClass.getClassLoader().getResource(".").toURI();
            //URI uri = theClass.getResource(".").toURI();
            //String msg = "Could not find " + path + " in " + uri.toString();
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            System.out.println("SigmaTestBase.getXmlReader(): Could not find " + path);
            //throw new IllegalStateException(msg);
            //}
            //catch (URISyntaxException e) {
            //    e.printStackTrace();
            //}
        }
        return xmlReader;
    }

}
