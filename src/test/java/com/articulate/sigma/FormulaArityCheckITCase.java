/*
 * Copyright 2014-2015 IPsoft
 * <p>
 * Author: Peigen You Peigen.You@ipsoft.com
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program ; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA  02111-1307 USA
 */
package com.articulate.sigma;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import(KBmanagerTestConfiguration.class)
public class FormulaArityCheckITCase {

    private KB kb;

    @Autowired
    private KBmanager topOnlyKBManager;

    @BeforeEach
    void init() {
        kb = topOnlyKBManager.getKB(topOnlyKBManager.getPref("sumokbname"));
    }
    @Test
    public void testArityCheck1() {
        String input = """
                (=>
                   (and
                      (domainSubclass ?REL ?NUMBER ?CLASS1)
                      (domainSubclass ?REL ?NUMBER ?CLASS2))
                   (or
                      (subclass ?CLASS1 ?CLASS2)
                      (subclass ?CLASS2 ?CLASS1)))""";
        Formula f = new Formula();
        f.read(input);
        String output = PredVarInst.hasCorrectArity(f, kb);
        assertThat(output).isNull();
    }

    @Test
    public void testArityCheck2() {
        String input = """
                (=>
                   (and
                      (subrelation ?REL1 ?REL2 Car)
                      (domainSubclass ?REL2 ?NUMBER ?CLASS1))
                   (domainSubclass ?REL1 ?NUMBER ?CLASS1))""";
        Formula f = new Formula();
        f.read(input);
        String output = PredVarInst.hasCorrectArity(f, kb);
        assertThat(output).isNotNull();
    }

}
