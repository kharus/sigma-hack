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
import java.util.Arrays;

@Component
@Command
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
    public void run(String rest) throws IOException {
        String[] args = rest.split("\\s", 2);
        kb = kbManager.getKB(sumokbname);

        log.debug("in KB.main()");
        String command = args[0];
        if (args != null && args.length > 0 && command.equals("-h"))
            KB.showHelp();
        else {
            KBmanager.prefOverride.put("loadLexicons", "false");
            log.info("KB.main(): Note! Not loading lexicons.");

            if (args != null)
                log.info("KB.main(): args: " + Arrays.toString(args));
            if (args != null && command.contains("c")) {
                String[] terms = args[1].split("\\s", 1);
                if (!kb.containsTerm(terms[1]))
                    log.debug("Error in KB.main() no such term: " + terms[1]);
                if (!kb.containsTerm(terms[2]))
                    log.debug("Error in KB.main() no such term: " + terms[2]);
                int eqrel = kb.compareTermDepth(terms[1], terms[2]);
                String eqText = KButilities.eqNum2Text(eqrel);
                log.debug("KB.main() term depth of " + terms[1] + " : " + kb.termDepth(terms[1]));
                log.debug("KB.main() term depth of " + terms[2] + " : " + kb.termDepth(terms[2]));
                log.debug("KB.main() eqrel " + eqrel);
                log.debug("KB.main() " + terms[1] + " " + eqText + " " + terms[2]);
            }
            if (args != null && args.length > 0 && command.contains("t"))
                KB.test();
            if (args != null && args.length > 1 && command.contains("v")) {
                log.info("set prover to vampire");
                KBmanager.getMgr().prover = KBmanager.Prover.VAMPIRE;
            }
            if (args != null && args.length > 1 && command.contains("e")) {
                KBmanager.getMgr().prover = KBmanager.Prover.EPROVER;
            }
            if (args != null && args.length > 1 && command.contains("L")) {
                KBmanager.getMgr().prover = KBmanager.Prover.LEO;
            }
            if (args != null && args.length > 0 && command.contains("l")) {
                log.debug("KB.main(): Normal completion");
            }
            if (args != null && args.length > 0 && command.contains("f")) {
                log.debug("set to TFF language");
                SUMOformulaToTPTPformula.lang = "tff";
                SUMOKBtoTPTPKB.lang = "tff";
            }
            if (args != null && args.length > 0 && command.contains("r")) {
                log.debug("KB.main(): set to FOF language");
                SUMOformulaToTPTPformula.lang = "fof";
                SUMOKBtoTPTPKB.lang = "fof";
            }
            if (args != null && args.length > 0 && command.contains("s")) {
                log.debug("KB.main(): show statistics");
                log.debug(HTMLformatter.showStatistics(kb));
            }
            int timeout = 30;
            if (args != null && args.length > 2 && command.contains("o")) {
                try {
                    timeout = Integer.parseInt(args[1]);
                } catch (NumberFormatException nfe) {
                    timeout = Integer.parseInt(args[2]);
                }
                log.debug("KB.main(): set timeout to: " + timeout);
            }
            if (args != null && args.length > 1 && command.contains("a")) {
                TPTP3ProofProcessor tpp = null;
                if (command.contains("p"))
                    TPTP3ProofProcessor.tptpProof = true;
                if (command.contains("x")) {
                    KB.contradictionHelp(kb, args, timeout);
                } else if (KBmanager.getMgr().prover == KBmanager.Prover.EPROVER) {
                    kb.loadEProver();
                    EProver eprover = kb.askEProver(args[1], timeout, 1);
                    log.debug("KB.main(): completed Eprover query with result: " + StringUtil.ListToCRLFString(eprover.output));
                    tpp = new TPTP3ProofProcessor();
                    tpp.parseProofOutput(eprover.output, args[1], kb, eprover.qlist);
                } else if (KBmanager.getMgr().prover == KBmanager.Prover.VAMPIRE) {
                    kb.loadVampire();
                    Vampire vamp = kb.askVampire(args[1], timeout, 1);
                    log.debug("KB.main(): completed Vampire query with result: " + StringUtil.ListToCRLFString(vamp.output));
                    tpp = new TPTP3ProofProcessor();
                    tpp.parseProofOutput(vamp.output, args[1], kb, vamp.qlist);
                } else if (KBmanager.getMgr().prover == KBmanager.Prover.LEO) {
                    LEO leo = kb.askLeo(args[1], timeout, 1);
                    log.debug("KB.main(): completed LEO query with result: " + StringUtil.ListToCRLFString(leo.output));
                    tpp = new TPTP3ProofProcessor();
                    tpp.parseProofOutput(leo.output, args[1], kb, leo.qlist);
                }
                String link = null;
                if (tpp != null)
                    tpp.createProofDotGraph();
                if (!command.contains("x")) {
                    log.debug("KB.main(): binding map: " + tpp.bindingMap);
                    int level = 1;
                    if (command.contains("2") || command.contains("3")) {
                        if (command.contains("2"))
                            level = 2;
                        if (command.contains("3"))
                            level = 3;
                    }
                    log.debug("KB.main(): proof with level " + level);
                    log.debug("KB.main(): axiom key size " + SUMOKBtoTPTPKB.axiomKey.size());
                    tpp.printProof(level);
                }
            }
        }
    }


}
