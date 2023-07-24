package com.articulate.sigma;

import com.articulate.sigma.tp.EProver;
import com.articulate.sigma.tp.LEO;
import com.articulate.sigma.tp.Vampire;
import com.articulate.sigma.trans.SUMOKBtoTPTPKB;
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
@Command(command = "kbmain")
public class KBRunner {
    private final Log log = LogFactory.getLog(getClass());
    private final KBmanager kbManager;

    @Value("${sumokbname}")
    private String sumokbname;

    @Value("${kbDir}")
    private String kbDir;

    private KB kb;

    public KBRunner(KBmanager kbManager) throws IOException {
        this.kbManager = kbManager;
        this.kb = kbManager.getKB(sumokbname);
    }

    @Command(command = "help")
    public void kbHelp() {
        KB.showHelp();
    }

    @Command(command = "t")
    public void kbTest() {
        KB.test();
    }

    @Command(command = "c")
    public void kbCompare(String[] args) {
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
    }

    @Command(command = "t")
    public void kbAsk(
            @Option(shortNames = 'p') boolean p,
            @Option(shortNames = 'x') boolean x,
            @Option(shortNames = 'e') boolean prover,
            @Option(shortNames = 'o', defaultValue = "30") int timeout,
            @Option(shortNames = 'q') String query,
            @Option(shortNames = 'l', defaultValue = "1") int level,
            String proverArgs
    ) throws IOException {
        String[] pa = proverArgs.split("\\w");
        TPTP3ProofProcessor tpp = null;
        if (p)
            TPTP3ProofProcessor.tptpProof = true;
        if (x) {
            KB.contradictionHelp(kb, pa, timeout);
        } else if (KBmanager.getMgr().prover == KBmanager.Prover.EPROVER) {
            kb.loadEProver();
            EProver eprover = kb.askEProver(query, timeout, 1);
            System.out.println("KB.main(): completed Eprover query with result: " + StringUtil.ListToCRLFString(eprover.output));
            tpp = new TPTP3ProofProcessor();
            tpp.parseProofOutput(eprover.output, query, kb, eprover.qlist);
        } else if (KBmanager.getMgr().prover == KBmanager.Prover.VAMPIRE) {
            kb.loadVampire();
            Vampire vamp = kb.askVampire(query, timeout, 1);
            System.out.println("KB.main(): completed Vampire query with result: " + StringUtil.ListToCRLFString(vamp.output));
            tpp = new TPTP3ProofProcessor();
            tpp.parseProofOutput(vamp.output, query, kb, vamp.qlist);
        } else if (KBmanager.getMgr().prover == KBmanager.Prover.LEO) {
            LEO leo = kb.askLeo(query, timeout, 1);
            System.out.println("KB.main(): completed LEO query with result: " + StringUtil.ListToCRLFString(leo.output));
            tpp = new TPTP3ProofProcessor();
            tpp.parseProofOutput(leo.output, query, kb, leo.qlist);
        }
        if (tpp != null)
            tpp.createProofDotGraph();
        if (!x) {
            System.out.println("KB.main(): proof with level " + level);
            System.out.println("KB.main(): axiom key size " + SUMOKBtoTPTPKB.axiomKey.size());
            tpp.printProof(level);
        }
    }


    @Command(command = "s")
    public void kbStats() {
        System.out.println("KB.main(): show statistics");
        System.out.println(HTMLformatter.showStatistics(kb));
    }

}
