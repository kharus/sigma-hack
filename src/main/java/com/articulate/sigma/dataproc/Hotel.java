/**
 * This code is copyrighted by Rearden Commerce (c) 2011.  It is
 * released under the GNU Public License &lt;http://www.gnu.org/copyleft/gpl.html&gt;."\""
 * <p>
 * Users of this code also consent, by use of this code, to credit
 * Articulate Software in any writings, briefings, publications,
 * presentations, or other representations of any software which
 * incorporates, builds on, or uses this code.  Please cite the following
 * article in any publication with references:
 * <p>
 * Pease, A., (2003). The Sigma Ontology Development Environment, in Working
 * Notes of the IJCAI-2003 Workshop on Ontology and Distributed Systems,
 * August 9, Acapulco, Mexico.  See also http://sigmakee.sourceforge.net.
 */
package com.articulate.sigma.dataproc;

import com.articulate.sigma.DB;
import com.articulate.sigma.KBmanager;
import com.articulate.sigma.utils.AVPair;
import com.articulate.sigma.utils.StringUtil;
import com.articulate.sigma.wordnet.WordNet;
import com.articulate.sigma.wordnet.WordNetUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hotel {

    public static int level = 0;
    public String oID = "";
    public String nID = "";
    public String taID = "";
    public String name = "";
    public String address = "";
    public String address2 = "";
    public String city = "";
    public String stateProv = "";
    public String country = "United States";
    public String postCode = "";
    public String tel = "";
    public String fax = "";
    public String email = "";
    public String url = "";
    public List<String> reviews = new ArrayList<String>();
    // a map of the sense (or term) and the number of appearances
    public Map<String, Integer> senses = new HashMap<String, Integer>();
    public Map<String, Integer> SUMO = new HashMap<String, Integer>();
    // a numerical assessment against arbitrary labels
    public TreeMap<String, Float> buckets = new TreeMap<String, Float>();
    public List<String> feedData = new ArrayList<String>();
    // overall sentiment for the hotel's reviews
    public int sentiment = 0;
    // Concept key and sentiment value reflecting the sentiment of each sentence
    // and the concepts in that sentence - an approximate association
    public Map<String, Integer> conceptSentiment = new HashMap<String, Integer>();
    public Map<String, String> values = new HashMap<String, String>();

    /**
     * @param h is a DOM element for one hotel
     * @return a map of column name and column value
     */
    public static Hotel processOneOXMLHotel(Element h) {

        Hotel result = new Hotel();
        int maxString = 30;
        NodeList features = h.getChildNodes();
        for (int i = 0; i < features.getLength(); i++) {
            if (features.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element feature = (Element) features.item(i);
                //System.out.println(feature.toString());
                if (feature.getTagName().equals("COUNTRY")) {
                    String text = feature.getTextContent();
                    if (!StringUtil.emptyString(text) && text.length() > maxString)
                        text = text.substring(0, maxString);
                    result.country = text;
                } else if (feature.getTagName().equals("CITY")) {
                    String text = feature.getTextContent();
                    if (!StringUtil.emptyString(text) && text.length() > maxString)
                        text = text.substring(0, maxString);
                    result.city = text;
                } else if (feature.getTagName().equals("STATE")) {
                    String text = feature.getTextContent();
                    if (!StringUtil.emptyString(text) && text.length() > maxString)
                        text = text.substring(0, maxString);
                    result.stateProv = text;
                } else if (feature.getTagName().equals("ADDRESS")) {
                    String text = feature.getTextContent();
                    if (!StringUtil.emptyString(text) && text.length() > maxString)
                        text = text.substring(0, maxString);
                    result.address = text;
                } else if (feature.getTagName().equals("Review")) {
                    NodeList fs = feature.getChildNodes();
                    for (int j = 0; j < fs.getLength(); j++) {
                        if (fs.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element reviewFields = (Element) fs.item(j);
                            if (reviewFields.getTagName().equals("TEXT"))
                                result.reviews.add(reviewFields.getTextContent());
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * This routine adds keys and values to the parameter.  There are
     * three possibilities:
     * - string key : string value
     * - string key : integer value
     * - string key : [list]
     *
     * @return the string index
     */
    public static int parseJSONPair(String s, int ind, JSONElement js) {

        //System.out.println("INFO in parseJSONPair(): index: " + ind + " " + s.substring(ind));
        int index = ind;
        Hotel h = new Hotel();
        if (s.charAt(index) != '"') {
            System.out.println("Error in parseJSONPair(): test for quote: Bad character " + s.charAt(index) + " at character " + index);
            return index;
        }
        index++;
        int end = s.indexOf('"', index);
        String key = s.substring(index, end);
        index = end;
        index++;
        if (s.charAt(index) != ':') {
            System.out.println("Error in parseJSONPair(): test for colon: Bad character " + s.charAt(index) + " at character " + index);
            System.out.println("INFO in parseJSONPair(): key " + key);
            return index;
        }
        index++;

        if (s.charAt(index) == '"') {
            index++;
            int start = index;
            while (s.charAt(s.indexOf('"', index) - 1) == '\\')  // skip over escaped quotes
                index = s.indexOf('"', index) + 1;
            end = s.indexOf('"', index);
            String value = s.substring(start, end);
            index = end;
            index++;
            JSONElement jsNew = h.new JSONElement();
            jsNew.key = key;
            jsNew.value = value;
            js.subelements.add(jsNew);
            //System.out.println("INFO in parseJSONPair(): key,value " + key + "," + value);
            return index;
        } else if (Character.isDigit(s.charAt(index))) {
            int start = index;
            while (Character.isDigit(s.charAt(index)) || s.charAt(index) == '.')
                index++;
            String value = s.substring(start, index);
            JSONElement jsNew = h.new JSONElement();
            jsNew.key = key;
            jsNew.value = value;
            js.subelements.add(jsNew);
            //System.out.println("INFO in parseJSONPair(): key,value " + key + "," + value);
            return index;
        } else if (s.charAt(index) == '[') {
            Hotel.level++;
            JSONElement jsNew = h.new JSONElement();
            jsNew.key = key;
            index++;
            index = parseJSONElement(s, index, jsNew);
            //System.out.println("INFO in parseJSONPair(): returning " + jsNew);
            js.subelements.add(jsNew);
            return index;
        } else if (s.startsWith("null", index)) {
            index = index + 4;
            return index;
        } else {
            System.out.println("Error in parseJSONPair(): Bad character " + s.charAt(index) + " at character " + index);
            System.out.println(s.substring(index, index + 4));
            return index;
        }
    }

    /**
     * This routine adds elements to the parameter
     *
     * @return the string index
     */
    public static int parseJSONElement(String s, int ind, JSONElement js) {

        //System.out.println("INFO in Hotel.parseJSONElement(): index: " + ind + " " + s.substring(ind));
        //System.out.println("INFO in Hotel.parseJSONElement(): character " + s.charAt(ind));
        //System.out.println("INFO in Hotel.parseJSONElement(): level " + Hotel.level);
        int index = ind;
        Hotel h = new Hotel();
        while (index < s.length()) {
            //System.out.println("INFO in Hotel.parseJSONElement(): testing, equals quote? " + ((s.charAt(index)) == '"'));
            if (s.charAt(index) == '}' || s.charAt(index) == ']') {
                Hotel.level--;
                //System.out.println("INFO in Hotel.parseJSONElement(): it's a close brace or bracket");
                //System.out.println("INFO in Hotel.parseJSONElement(): returning, level: " + Hotel.level);
                //System.out.println(js);
                index++;
                return index;
            } else if (s.charAt(index) == '{') {
                Hotel.level++;
                //System.out.println("INFO in Hotel.parseJSONElement(): it's an open brace");
                index++;
                JSONElement jsNew = h.new JSONElement();
                index = parseJSONElement(s, index, jsNew);
                //System.out.println("INFO in Hotel.parseJSONElement(): returning " + jsNew);
                //System.out.println("INFO in Hotel.parseJSONElement(): character " + s.charAt(index));
                js.subelements.add(jsNew);
            } else if (s.charAt(index) == '"') {
                //System.out.println("INFO in Hotel.parseJSONElement(): it's a quote");
                index = parseJSONPair(s, index, js);
            } else if (s.charAt(index) == ',') {
                //System.out.println("INFO in Hotel.parseJSONElement(): it's a comma");
                index++;
            } else {
                System.out.println("Error in parseJSONElement(): Bad character " + s.charAt(index) + " at character " + index);
                return index;
            }
            //index++;
        }
        return index;
    }

    public static Hotel convertJSON2Hotel(JSONElement js) {

        Hotel result = new Hotel();
        JSONElement jsNew = js.subelements.get(0);
        result.name = jsNew.getElementValue("name");
        result.address = jsNew.getElementValue("address");
        result.taID = jsNew.getElementValue("id");
        result.stateProv = jsNew.getElementValue("state");
        result.city = jsNew.getElementValue("city");
        JSONElement reviews = jsNew.getElement("reviews");
        if (reviews != null) {
            for (int i = 0; i < reviews.subelements.size(); i++) {
                String review = reviews.subelements.get(i).getElementValue("review");
                result.reviews.add(review);
            }
        }
        return result;
    }

    public static Hotel parseOneJSONReviewFile(String fname) {

        Hotel h = new Hotel();
        LineNumberReader lnr = null;
        try {
            File fin = new File(fname);
            FileReader fr = new FileReader(fin);
            if (fr != null) {
                lnr = new LineNumberReader(fr);
                String line = null;
                boolean done = false;
                while ((line = lnr.readLine()) != null) {
                    line = line.trim();
                    JSONElement js = h.new JSONElement();
                    parseJSONElement(line, 0, js);
                    h = convertJSON2Hotel(js);
                    //System.out.println("---------------------------");
                    //System.out.println(h);
                    //System.out.println(js);

                }
            }
        } catch (Exception e) {
            System.out.println("Error in parseOneJSONReviewFile(): File error reading " + fname + ": " + e.getMessage());
            return null;
        } finally {
            try {
                if (lnr != null) lnr.close();
            } catch (Exception e) {
                System.out.println("Exception in parseOneJSONReviewFile()" + e.getMessage());
            }
        }
        return h;
    }

    public static String normalizeSentiment(String value) {

        try {
            float val = Integer.parseInt(value);
            if (val < 0)
                return "0";
            val = 1 - ((50 + val) / (50 + val * val));
            return Float.toString(val);
        } catch (NumberFormatException n) {
            System.out.println("Error in Hotel.normalizeSentiment(): bad input: " + value);
            return "0";
        }
    }

    public static void writeHotelAsXML(Hotel h, PrintWriter pw) {

        try {
            pw.println("\t<hotel>");
            pw.println("\t\t<taID value=\"" + StringUtil.encode(h.taID) + "\"/>");
            pw.println("\t\t<name value=\"" + StringUtil.encode(h.name) + "\"/>");
            pw.println("\t\t<address value=\"" + StringUtil.encode(h.address) + "\"/>");
            pw.println("\t\t<address2 value=\"" + StringUtil.encode(h.address2) + "\"/>");
            pw.println("\t\t<city value=\"" + StringUtil.encode(h.city) + "\"/>");
            pw.println("\t\t<stateProv value=\"" + StringUtil.encode(h.stateProv) + "\"/>");
            pw.println("\t\t<country value=\"" + StringUtil.encode(h.country) + "\"/>");
            pw.println("\t\t<postCode value=\"" + StringUtil.encode(h.postCode) + "\"/>");
            pw.println("\t\t<tel value=\"" + StringUtil.encode(h.tel) + "\"/>");
            pw.println("\t\t<sentiment>");
            Iterator<String> it = h.conceptSentiment.keySet().iterator();
            while (it.hasNext()) {
                String concept = it.next();
                String value = h.conceptSentiment.get(concept).toString();
                concept = WordNetUtilities.getBareSUMOTerm(concept);
                System.out.println(concept);
                //value = normalizeSentiment(value);
                pw.println("\t\t\t<sent concept=\"" + concept + "\" value=\"" + value + "\"/>");
            }
            pw.println("\t\t</sentiment>");
            pw.println("\t</hotel>");
        } catch (Exception e) {
            System.out.println("Error in Hotel.writeHotelAsXML(): Error writing " + pw + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param writeIncremental means that each hotel review will be
     *                         processed and each spreadsheet line will be written after reading
     *                         each hotel.
     */
    public static List<Hotel> readJSONHotels(String dir, boolean writeIncremental) {

        System.out.println("INFO in readJSONHotels()");
        KBmanager.getMgr().initializeOnce();
        System.out.println("INFO in readJSONHotels(): completed KB initialization");
        System.out.println("INFO in readJSONHotels(): complete reading WordNet files");

        long t1 = System.currentTimeMillis();
        List<Hotel> result = new ArrayList<Hotel>();
        LineNumberReader lnr = null;
        PrintWriter pw = null;
        try {
            File fin = new File(dir);
            File outfile = new File(dir + File.separator + "hotelSentiment.xml");
            pw = new PrintWriter(outfile);
            if (writeIncremental)
                pw.println("<hotels>");
            String[] children = fin.list();
            if (children == null || children.length == 0)
                System.out.println("Error in Hotel.readJSONHotels(): dir: " + dir + " does not exist or is empty.");
            else {
                System.out.println("INFO in readJSONHotels(): " + children.length + " files.");
                for (int i = 0; i < children.length; i++) {
                    // Get filename of file or directory
                    String filename = children[i];
                    //System.out.println("INFO in readJSONHotels(): filename: " + filename);
                    String qualifiedFilename = dir + File.separator + filename;
                    if (!StringUtil.emptyString(filename) && filename.endsWith("json")) {
                        Hotel h = parseOneJSONReviewFile(qualifiedFilename);
                        if (writeIncremental) {
                            oneHotelAmenitySentiment(h);
                            writeHotelAsXML(h, pw);
                        } else
                            result.add(h);
                    }
                    if (i % 10 == 0)
                        System.out.print('.');
                }
                System.out.println("INFO in readJSONHotels(): Completed reading reviews.");
            }
            if (writeIncremental)
                pw.println("</hotels>");
        } catch (Exception e) {
            System.out.println("Error in Hotel.readJSONHotels(): File error reading/writing " + dir + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (lnr != null) lnr.close();
                if (pw != null) pw.close();
            } catch (Exception e) {
                System.out.println("Exception in readJSONHotels()" + e.getMessage());
            }
        }
        System.out.println("INFO in Hotel.readJSONHotels(): done reading reviews in " + ((System.currentTimeMillis() - t1) / 1000.0) + " seconds");
        return result;
    }

    /**
     * Compute concept sentiment and store as a side effect.
     */
    public static void oneHotelAmenitySentiment(Hotel h) {

        //System.out.println("======== " + h.name + " ========");
        for (int j = 0; j < h.reviews.size(); j++) {
            String review = h.reviews.get(j);
            //System.out.println(review);
            Map<String, Integer> conceptSent = DB.computeConceptSentiment(review);
            //System.out.println("=== " + conceptSent + " ===");
            h.addConceptSentiment(conceptSent);
        }
    }

    public static void execJSON(String path) {

        //List<Hotel> hotels = readJSONHotels(path,false);
        List<Hotel> hotels = readJSONHotels(path, true);
        long t1 = System.currentTimeMillis();
        //System.out.println(DB.writeSpreadsheet(Hotel.hotelReviewSUMOSentimentAsSparseMatrix(hotels,true),true));
        System.out.println("INFO in Hotel.execJSON(): done computing sentiment in " + ((System.currentTimeMillis() - t1) / 1000.0) + " seconds");
    }

    public static void main(String[] args) {

        if (args[0].equals("-help") || StringUtil.emptyString(args[0])) {
            System.out.println("usage:");
            System.out.println(">java -classpath . com.articulate.sigma.dataProc.Hotel -js /home/me/data");
        }
        if (args[0].equals("-js")) {
            String path = ".";
            if (!StringUtil.emptyString(args[1]))
                path = args[1];
            execJSON(path);
        }
    }

    public String toString() {

        StringBuffer result = new StringBuffer();
        result.append("name: " + name + "\n");
        result.append("address: " + address + "\n");
        if (!StringUtil.emptyString(address2))
            result.append("address2: " + address2 + "\n");
        result.append("city: " + city + "\n");
        result.append("stateProv: " + stateProv + "\n");
        result.append("country: " + country + "\n");

        Iterator<String> it = reviews.iterator();
        while (it.hasNext()) {
            String S = it.next();
            result.append("\"" + S + "\"\n");
        }
        result.append("\n\n");
        return result.toString();
    }

    public void addConceptSentiment(Map<String, Integer> conceptSent) {

        Iterator<String> it = conceptSent.keySet().iterator();
        while (it.hasNext()) {
            String term = it.next();
            int val = conceptSent.get(term).intValue();
            int oldVal = 0;
            if (conceptSentiment.containsKey(term)) {
                oldVal = conceptSentiment.get(term).intValue();
                val = val + oldVal;
            }
            conceptSentiment.put(term, Integer.valueOf(val));
        }
    }

    public void addAllSenses(Map<String, Integer> wnsenses) {

        Iterator<String> it = wnsenses.keySet().iterator();
        while (it.hasNext()) {
            String sense = it.next();
            if (senses.containsKey(sense))
                senses.put(sense, Integer.valueOf(wnsenses.get(sense).intValue() + senses.get(sense).intValue()));
            else
                senses.put(sense, wnsenses.get(sense));
        }
    }

    public class JSONElement {

        String key = ""; // empty key signifies root element
        String value = "";
        List<JSONElement> subelements = new ArrayList<JSONElement>();

        public String toString() {

            StringBuffer sb = new StringBuffer();
            if (!StringUtil.emptyString(key))
                sb.append(key + ":");
            if (!StringUtil.emptyString(value))
                sb.append(value);
            else {
                if (!StringUtil.emptyString(key))
                    sb.append("[");
                else
                    sb.append("{");
                for (int i = 0; i < subelements.size(); i++) {
                    sb.append(subelements.get(i).toString());
                    if (i < subelements.size() - 1)
                        sb.append(",");
                }
                if (!StringUtil.emptyString(key))
                    sb.append("]");
                else
                    sb.append("}");
            }
            return sb.toString();
        }

        public JSONElement getElement(String key) {

            for (int i = 0; i < subelements.size(); i++) {
                if (subelements.get(i).key.equals(key)) {
                    //System.out.println("INFO in Hotel.JSONElement.getElement(): " + subelements.get(i).key);
                    return subelements.get(i);
                }
            }
            return null;
        }

        public String getElementValue(String key) {

            JSONElement js = getElement(key);
            if (js == null)
                return "";
            if (!StringUtil.emptyString(js.value))
                return js.value;
            return "";
        }
    }
}