package com.articulate.sigma.verbnet;

import com.articulate.sigma.SimpleElement;
import com.articulate.sigma.utils.AVPair;

import java.util.*;

/**
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
public class Frame {

    private final boolean echo = false;
    public String descriptionNum;
    public String primary;
    public String secondary;
    public String xtag;
    public String example;
    public List<Tuple> syntax = new ArrayList<>();
    public List<Tuple> semantics = new ArrayList<>();

    public void readDesc(SimpleElement desc) {

        if (echo) System.out.println("Frame.readDesc()");
        descriptionNum = desc.getAttribute("descriptionNumber");
        if (echo) System.out.println("Desc number: " + descriptionNum);
        primary = desc.getAttribute("primary");
        if (echo) System.out.println("primary: " + primary);
        secondary = desc.getAttribute("secondary");
        if (echo) System.out.println("secondary: " + secondary);
        xtag = desc.getAttribute("xtag");
        if (echo) System.out.println("xtag: " + xtag);
    }

    public void readEx(SimpleElement ex) {

        if (echo) System.out.println("Frame.readEx()");
        for (int i = 0; i < ex.getChildElements().size(); i++) {
            SimpleElement element = ex.getChildElements().get(i);
            if (element.getTagName().equals("EXAMPLE")) {
                if (echo) System.out.println("Example");
                String text = element.getText();
                if (echo) System.out.println("Text: " + text);
                example = text;
            } else {
                System.out.println("Error in Frame.readEx(): unknown tag: " + element);
            }
        }
    }

    public Set<AVPair> readSynrestrs(SimpleElement syn) {

        Set<AVPair> restr = new HashSet<>();
        if (echo) System.out.println("Frame.readSynrestrs()");
        for (int i = 0; i < syn.getChildElements().size(); i++) {
            SimpleElement element = syn.getChildElements().get(i);
            if (element.getTagName().equals("SYNRESTR")) {
                AVPair avp = new AVPair();
                avp.attribute = element.getAttribute("value");
                if (echo) System.out.println("value: " + avp.attribute);
                avp.value = element.getAttribute("type");
                restr.add(avp);
                if (echo) System.out.println("type: " + avp.value);
            } else {
                System.out.println("Error in Frame.readSynrestrs(): unknown tag: " + element);
            }
        }
        return restr;
    }

    public void readSyn(SimpleElement syn) {

        Map<String, String> parts = new HashMap<>();
        if (echo) System.out.println("Frame.readSyn()");
        for (int i = 0; i < syn.getChildElements().size(); i++) {
            Tuple t = new Tuple();
            SimpleElement element = syn.getChildElements().get(i);
            if (element.getTagName().equals("NP")) {
                String val = element.getAttribute("value");
                t.value = val;
                if (echo) System.out.println("NP value: " + val);
                if (!parts.containsKey("nsubj"))
                    parts.put("nsubj", val);
                else if (!parts.containsKey("dobj"))
                    parts.put("dobj", val);
                for (int j = 0; j < element.getChildElements().size(); j++) {
                    SimpleElement element2 = element.getChildElements().get(j);
                    if (element2.getTagName().equals("SYNRESTRS")) {
                        t.restrict.addAll(readSynrestrs(element2));
                    } else {
                        System.out.println("Error in Frame.readSyn(NP): unknown tag: " + element2);
                    }
                }
            } else if (element.getTagName().equals("PREP")) {
                String val = element.getAttribute("value");
                t.value = val;
                if (echo) System.out.println("PREP value: " + val);
                for (int j = 0; j < element.getChildElements().size(); j++) {
                    SimpleElement element2 = element.getChildElements().get(j);
                    if (element2.getTagName().equals("SELRESTRS")) {
                        t.restrict.addAll(Verb.readSelrestrs(element2));
                    } else {
                        System.out.println("Error in Frame.readSyn(PREP): unknown tag: " + element2);
                    }
                }
            } else if (element.getTagName().equals("ADV")) {
                String val = element.getAttribute("value");
                t.value = val;
                if (echo) System.out.println("ADV value: " + val);
                for (int j = 0; j < element.getChildElements().size(); j++) {
                    SimpleElement element2 = element.getChildElements().get(j);
                    if (element2.getTagName().equals("SELRESTRS")) {
                        t.restrict.addAll(Verb.readSelrestrs(element2));
                    } else {
                        System.out.println("Error in Frame.readSyn(ADV): unknown tag: " + element2);
                    }
                }
            } else if (element.getTagName().equals("ADJ")) {
                String val = element.getAttribute("value");
                t.value = val;
                if (echo) System.out.println("ADJ value: " + val);
                for (int j = 0; j < element.getChildElements().size(); j++) {
                    SimpleElement element2 = element.getChildElements().get(j);
                    if (element2.getTagName().equals("SELRESTRS")) {
                        t.restrict.addAll(Verb.readSelrestrs(element2));
                    } else {
                        System.out.println("Error in Frame.readSyn(ADJ): unknown tag: " + element2);
                    }
                }
            } else if (element.getTagName().equals("LEX")) {
                String val = element.getAttribute("value");
                t.value = val;
                if (echo) System.out.println("LEX value: " + val);
            } else if (element.getTagName().equals("VERB")) {
                String val = element.getAttribute("value");
                t.value = val;
                if (echo) System.out.println("VERB value: " + val);
                for (int j = 0; j < element.getChildElements().size(); j++) {
                    SimpleElement element2 = element.getChildElements().get(j);
                    if (element2.getTagName().equals("SYNRESTRS")) {
                        t.restrict.addAll(readSynrestrs(element2));
                    } else {
                        System.out.println("Error in Frame.readSyn(VERB): unknown tag: " + element2);
                    }
                }
            } else {
                System.out.println("Error in Frame.readSyn(): unknown tag: " + element);
            }
            syntax.add(t);
        }
    }

    public Set<AVPair> readArgs(SimpleElement pred) {

        Set<AVPair> result = new HashSet<>();
        if (echo) System.out.println("Frame.readArgs()");
        for (int i = 0; i < pred.getChildElements().size(); i++) {
            SimpleElement element = pred.getChildElements().get(i);
            if (element.getTagName().equals("ARG")) {
                AVPair avp = new AVPair();
                String val = element.getAttribute("value");
                avp.attribute = val;
                if (echo) System.out.println("value: " + val);
                String type = element.getAttribute("type");
                avp.value = type;
                result.add(avp);
                if (echo) System.out.println("type: " + type);
            } else {
                System.out.println("Error in Frame.readPred(): unknown tag: " + element);
            }
        }
        return result;
    }

    public Tuple readPred(SimpleElement pred) {

        Tuple t = new Tuple();
        if (echo) System.out.println("Frame.readPred()");
        String val = pred.getAttribute("value");
        t.value = val;
        if (echo) System.out.println("value: " + val);
        for (int i = 0; i < pred.getChildElements().size(); i++) {
            SimpleElement element = pred.getChildElements().get(i);
            if (element.getTagName().equals("ARGS")) {
                t.restrict.addAll(readArgs(element));
            } else {
                System.out.println("Error in Frame.readPred(): unknown tag: " + element);
            }
        }
        return t;
    }

    public void readSem(SimpleElement sem) {

        if (echo) System.out.println("Frame.readSem()");
        for (int i = 0; i < sem.getChildElements().size(); i++) {
            SimpleElement element = sem.getChildElements().get(i);
            if (element.getTagName().equals("PRED")) {
                readPred(element);
            } else {
                System.out.println("Error in Frame.readSem(): unknown tag: " + element);
            }
        }
    }

    public void readFrame(SimpleElement element) {

        for (int j = 0; j < element.getChildElements().size(); j++) {
            SimpleElement element2 = element.getChildElements().get(j);
            if (element2.getTagName().equals("DESCRIPTION"))
                readDesc(element2);
            else if (element2.getTagName().equals("EXAMPLES"))
                readEx(element2);
            else if (element2.getTagName().equals("SYNTAX"))
                readSyn(element2);
            else if (element2.getTagName().equals("SEMANTICS"))
                readSem(element2);
            else {
                System.out.println("Error in Frame.readThemeRoles(): unknown tag: " + element2);
            }
        }
    }

    public class Tuple {
        String value;
        Set<AVPair> restrict = new HashSet<>();
    }
}
