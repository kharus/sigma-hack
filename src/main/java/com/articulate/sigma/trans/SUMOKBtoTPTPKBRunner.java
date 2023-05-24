package com.articulate.sigma.trans;

import com.articulate.sigma.KBmanager;
import com.articulate.sigma.utils.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

@Component
public class SUMOKBtoTPTPKBRunner implements CommandLineRunner {
    private final Log log = LogFactory.getLog(getClass());
    private final KBmanager kbManager;

    @Value("${sumokbname}")
    private String sumokbname;

    @Value("${kbDir}")
    private String kbDir;

    public SUMOKBtoTPTPKBRunner(KBmanager kbManager) {
        this.kbManager = kbManager;
    }

    public void run(String... args) throws Exception {
        SUMOKBtoTPTPKB skbtptpkb = new SUMOKBtoTPTPKB();
        skbtptpkb.kb = kbManager.getKB(sumokbname);

        String filename = kbDir + File.separator + sumokbname + "." + SUMOKBtoTPTPKB.lang;

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)))) {
            String fileWritten = skbtptpkb.writeFile(filename, null, false, pw);
            if (StringUtil.isNonEmptyString(fileWritten))
                log.info("File written: " + fileWritten + " with key: " + SUMOKBtoTPTPKB.axiomKey);
            else
                log.error("Could not write " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
