package com.articulate.sigma.trans;

import com.articulate.sigma.KBmanager;
import com.articulate.sigma.utils.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.command.annotation.Command;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

@Component
@Command
public class SUMOKBtoTPTPKBRunner {
    private final Log log = LogFactory.getLog(getClass());
    private final KBmanager kbManager;

    @Value("${sumokbname}")
    private String sumokbname;

    @Value("${kbDir}")
    private String kbDir;

    public SUMOKBtoTPTPKBRunner(KBmanager kbManager) {
        this.kbManager = kbManager;
    }

    @Command(command = "fof")
    public void tptpFof() {
        SUMOKBtoTPTPKB skbtptpkb = new SUMOKBtoTPTPKB();
        skbtptpkb.kb = kbManager.getKB(sumokbname);

        String filename = kbDir + File.separator + sumokbname + "." + SUMOKBtoTPTPKB.lang;

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)))) {
            String fileWritten = skbtptpkb.writeFile(filename, null, false, pw);
            if (StringUtil.isNonEmptyString(fileWritten))
                log.info("File written: " + fileWritten);
            else
                log.error("Could not write " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Command(command = "tff")
    public void tptpTff() {
        SUMOKBtoTFAKB skbtfakb = new SUMOKBtoTFAKB();
        skbtfakb.kb = kbManager.getKB(sumokbname);
        skbtfakb.initOnce(skbtfakb.kb);
        // this setting has to be *after* initialization, otherwise init
        // tries to write a TPTP file and then sees that tff is set and tries to write tff, but then sorts etc
        // haven't been set
        SUMOformulaToTPTPformula.lang = "tff";
        SUMOKBtoTPTPKB.lang = "tff";
        String kbName = KBmanager.getMgr().getPref("sumokbname");
        String filename = KBmanager.getMgr().getPref("kbDir") + File.separator + kbName + "." + SUMOKBtoTPTPKB.lang;

        try (FileWriter fw = new FileWriter(filename);
             PrintWriter pw = new PrintWriter(fw)) {
            skbtfakb.writeSorts(pw);
            System.out.println("SUMOKBtoTFAKB.main(): completed writing sorts");
            skbtfakb.writeFile(filename, null, false, pw);
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
