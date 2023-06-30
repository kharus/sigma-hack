package com.articulate.sigma;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Derivation implements Serializable {
    public String operator = "input";
    public List<Formula> parents = new ArrayList<>();

    public Derivation() {
    }

    public Derivation(String op, List<Formula> par) {

        operator = op;
        if (par != null)
            parents.addAll(par);
    }

    public Derivation deepCopy() {

        Derivation result = new Derivation();
        result.operator = this.operator;
        result.parents = new ArrayList<Formula>();
        if (parents != null) {
            for (Formula f : parents)
                result.parents.add(f.deepCopy());
        }
        return result;
    }

    /**
     * Return a string for the derivation
     */
    public String toString() {

        if (operator.equals("input"))
            return "input";
        else {
            StringBuffer sb = new StringBuffer();
            sb.append("inference(" + operator + ", ");
            for (Formula f : parents) {
                sb.append(f.getFormula());
                sb.append(":" + f.derivation.toString() + ")");
            }
            return sb.toString();
        }
    }

    /**
     * Return a list of all derived objects that are used in this
     * derivation.
     */
    public List<Formula> getParents() {

        if (operator.equals("input"))
            return new ArrayList<>();
        else {
            List<Formula> res = new ArrayList<>();
            for (Formula p : parents)
                res.addAll(p.derivation.parents);
            return res;
        }
    }
}
