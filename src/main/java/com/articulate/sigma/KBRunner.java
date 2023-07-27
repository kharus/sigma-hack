package com.articulate.sigma;

import com.articulate.sigma.tp.EProver;
import com.articulate.sigma.tp.LEO;
import com.articulate.sigma.tp.Vampire;
import com.articulate.sigma.trans.SUMOKBtoTPTPKB;
import com.articulate.sigma.trans.SUMOformulaToTPTPformula;
import com.articulate.sigma.trans.TPTP3ProofProcessor;
import com.articulate.sigma.utils.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class KBRunner {
    private final Log log = LogFactory.getLog(getClass());
    private final KBmanager kbManager;

    @Value("${sumokbname}")
    private String sumokbname;

    @Value("${kbDir}")
    private String kbDir;

    private KB kb;

    public KBRunner(KBmanager kbManager) {
        this.kbManager = kbManager;
        this.kb = kbManager.getKB(sumokbname);
    }

    @Command(command = "kb")
    public void run(String[] args) throws IOException {

        System.out.println("INFO in KB.main()");
        if (args != null && args.length > 0 && args[0].equals("-h"))
            KB.showHelp();
        else {
            KBmanager.prefOverride.put("loadLexicons", "false");
            System.out.println("KB.main(): Note! Not loading lexicons.");
            KBmanager.getMgr().initializeOnce();
            String kbName = KBmanager.getMgr().getPref("sumokbname");
            KB kb = KBmanager.getMgr().getKB(kbName);
            if (args != null)
                System.out.println("KB.main(): args[0]: " + args[0]);
            if (args != null && args.length > 2 && args[0].contains("c")) {
                if (!kb.containsTerm(args[1]))
                    System.out.println("Error in KB.main() no such term: " + args[1]);
                if (!kb.containsTerm(args[2]))
                    System.out.println("Error in KB.main() no such term: " + args[2]);
                int eqrel = kb.compareTermDepth(args[1], args[2]);
                String eqText = KButilities.eqNum2Text(eqrel);
                System.out.println("KB.main() term depth of " + args[1] + " : " + kb.termDepth(args[1]));
                System.out.println("KB.main() term depth of " + args[2] + " : " + kb.termDepth(args[2]));
                System.out.println("KB.main() eqrel " + eqrel);
                System.out.println("KB.main() " + args[1] + " " + eqText + " " + args[2]);
            }
            if (args != null && args.length > 0 && args[0].contains("t"))
                KB.test();
            if (args != null && args.length > 1 && args[0].contains("v")) {
                KBmanager.getMgr().prover = KBmanager.Prover.VAMPIRE;
            }
            if (args != null && args.length > 1 && args[0].contains("e")) {
                KBmanager.getMgr().prover = KBmanager.Prover.EPROVER;
            }
            if (args != null && args.length > 1 && args[0].contains("L")) {
                KBmanager.getMgr().prover = KBmanager.Prover.LEO;
            }
            if (args != null && args.length > 0 && args[0].contains("l")) {
                System.out.println("KB.main(): Normal completion");
            }
            if (args != null && args.length > 0 && args[0].contains("f")) {
                System.out.println("KB.main(): set to TFF language");
                SUMOformulaToTPTPformula.lang = "tff";
                SUMOKBtoTPTPKB.lang = "tff";
            }
            if (args != null && args.length > 0 && args[0].contains("r")) {
                System.out.println("KB.main(): set to FOF language");
                SUMOformulaToTPTPformula.lang = "fof";
                SUMOKBtoTPTPKB.lang = "fof";
            }
            if (args != null && args.length > 0 && args[0].contains("s")) {
                System.out.println("KB.main(): show statistics");
                System.out.println(HTMLformatter.showStatistics(kb));
            }
            int timeout = 30;
            if (args != null && args.length > 2 && args[0].contains("o")) {
                try {
                    timeout = Integer.parseInt(args[1]);
                } catch (NumberFormatException nfe) {
                    timeout = Integer.parseInt(args[2]);
                }
                System.out.println("KB.main(): set timeout to: " + timeout);
            }
            if (args != null && args.length > 1 && args[0].contains("a")) {
                TPTP3ProofProcessor tpp = null;
                if (args[0].contains("p"))
                    TPTP3ProofProcessor.tptpProof = true;
                if (args[0].contains("x")) {
                    KB.contradictionHelp(kb, args, timeout);
                } else if (KBmanager.getMgr().prover == KBmanager.Prover.EPROVER) {
                    kb.loadEProver();
                    EProver eprover = kb.askEProver(args[1], timeout, 1);
                    System.out.println("KB.main(): completed Eprover query with result: " + StringUtil.ListToCRLFString(eprover.output));
                    tpp = new TPTP3ProofProcessor();
                    tpp.parseProofOutput(eprover.output, args[1], kb, eprover.qlist);
                } else if (KBmanager.getMgr().prover == KBmanager.Prover.VAMPIRE) {
                    kb.loadVampire();
                    Vampire vamp = kb.askVampire(args[1], timeout, 1);
                    System.out.println("KB.main(): completed Vampire query with result: " + StringUtil.ListToCRLFString(vamp.output));
                    tpp = new TPTP3ProofProcessor();
                    tpp.parseProofOutput(vamp.output, args[1], kb, vamp.qlist);
                } else if (KBmanager.getMgr().prover == KBmanager.Prover.LEO) {
                    LEO leo = kb.askLeo(args[1], timeout, 1);
                    System.out.println("KB.main(): completed LEO query with result: " + StringUtil.ListToCRLFString(leo.output));
                    tpp = new TPTP3ProofProcessor();
                    tpp.parseProofOutput(leo.output, args[1], kb, leo.qlist);
                }
                String link = null;
                if (tpp != null)
                    tpp.createProofDotGraph();
                if (!args[0].contains("x")) {
                    System.out.println("KB.main(): binding map: " + tpp.bindingMap);
                    int level = 1;
                    if (args[0].contains("2") || args[0].contains("3")) {
                        if (args[0].contains("2"))
                            level = 2;
                        if (args[0].contains("3"))
                            level = 3;
                    }
                    System.out.println("KB.main(): proof with level " + level);
                    System.out.println("KB.main(): axiom key size " + SUMOKBtoTPTPKB.axiomKey.size());
                    tpp.printProof(level);
                }
            }
        }
    }


}
