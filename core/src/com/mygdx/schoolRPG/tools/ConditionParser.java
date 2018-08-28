package com.mygdx.schoolRPG.tools;

import com.mygdx.schoolRPG.NPC;
import com.mygdx.schoolRPG.Player;
import javafx.util.Pair;

import java.sql.Array;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ConditionParser {

    ArrayList<NPC> npcs;
    public Player player;
    int mainCharId;
    ArrayList<Pair<String, Integer>> specialVars;

    public ConditionParser(ArrayList<NPC> npcs, Player player, int mainCharId) {
        this.npcs = npcs;
        this.player = player;
        this.mainCharId = mainCharId;
    }

    private int evalVar(String var) {
        boolean hasLetters = false;
        for (int i = 0; i < var.length(); ++i) {
            if (Character.isLetter(var.charAt(i))) {
                hasLetters = true;
                break;
            }
        }
        int val = 0;
        boolean valSet = false;
        if (var.startsWith("i_")) {
            String params[] = var.split("_");
            int ownerId = Integer.parseInt(params[1]);
            String itemName = params[2];
            if (ownerId == 0) {
                int itemsCountFound = 0;
                for (int z = 0; z < player.inventory.size(); ++z) {
                    if (player.inventory.get(z).fileName.equals(itemName)) {
                        itemsCountFound += player.inventory.get(z).stack;
                    }
                }
                val = itemsCountFound;
                valSet = true;
            } else {
                for (int j = 0; j < npcs.size(); ++j) {
                    if (npcs.get(j).charId == ownerId) {
                        int itemsCountFound = 0;
                        for (int z = 0; z < npcs.get(j).inventory.size(); ++z) {
                            if (npcs.get(j).inventory.get(z).fileName.equals(itemName)) {
                                itemsCountFound += npcs.get(j).inventory.get(z).stack;
                            }
                        }
                        val = itemsCountFound;
                        valSet = true;
                        break;
                    }
                }
            }
        } else if (var.startsWith("r_")) {
            String params[] = var.split("_");
            int minVal = Integer.parseInt(params[1]);
            int maxVal = Integer.parseInt(params[2]);
            val = minVal + (int)Math.ceil(Math.random() * maxVal);
            valSet = true;
        } else if (specialVars != null) {
            for (Pair<String, Integer> specialVar : specialVars) {
                if (specialVar.getKey().equals(var)) {
                    val = specialVar.getValue();
                    valSet = true;
                    break;
                }
            }
        }
        if (!valSet) {
            if (hasLetters) {
                if (mainCharId == 0) {
                    int index = player.world.varNames.indexOf(var);
                    val = index == -1 ? 0 : player.world.vars.get(index);
                } else {
                    for (int j = 0; j < npcs.size(); ++j) {
                        if (npcs.get(j).charId == mainCharId) {
                            int varId = npcs.get(j).varNames.indexOf(var);
                            if (varId == -1) continue;
                            val = npcs.get(j).vars.get(varId);
                            break;
                        }
                    }
                }
            } else {
                if (var.length() > 0) {
                    val = Integer.parseInt(var);
                }
            }
        }

        return val;
    }

    private String evalBinaryOperator(String expr, int offset) {
        int leftOpStart = Math.max(offset - 1, 0);
        int rightOpEnd = Math.min(offset + 1, expr.length()-1);
        while (leftOpStart > 0 && (Character.isLetter(expr.charAt(leftOpStart)) || Character.isDigit(expr.charAt(leftOpStart)) || expr.charAt(leftOpStart) == '_')) leftOpStart--;
        boolean invertLeft = false;
        if (expr.charAt(leftOpStart) == '-') invertLeft = true;
        if (expr.charAt(rightOpEnd) == '-') rightOpEnd++;
        while (rightOpEnd < expr.length()-1 && (Character.isLetter(expr.charAt(rightOpEnd)) || Character.isDigit(expr.charAt(rightOpEnd)) || expr.charAt(rightOpEnd) == '_')) rightOpEnd++;
        String leftOp = expr.substring(leftOpStart, offset);
        String rightOp = expr.substring(offset + 1, rightOpEnd + 1);
        int leftOpVal;
        if (offset - leftOpStart == 0) {
            leftOpVal = 0;
        } else {
            leftOpVal = evalVar(leftOp);
        }
        int rightOpVal = evalVar(rightOp);
        int val = 0;
        int leftSign = 1;
        if (invertLeft) leftSign = -1;
        if (expr.charAt(offset) == '%') {
            if (rightOpVal == 0) val = -0;
            else val = (leftSign * leftOpVal) % rightOpVal;
        } else if (expr.charAt(offset) == '^') {
            if ((leftSign * leftOpVal) == 0 && rightOpVal == 0) val = -0;
            else val = (int)Math.pow((leftSign * leftOpVal), rightOpVal);
        } else if (expr.charAt(offset) == '*') {
            val = leftSign * leftOpVal * rightOpVal;
        } else if (expr.charAt(offset) == '/') {
            if (rightOpVal == 0) val = -0;
            else val = leftSign * leftOpVal / rightOpVal;
        } else if (expr.charAt(offset) == '-') {
            val = leftSign * leftOpVal - rightOpVal;
        } else if (expr.charAt(offset) == '+') {
            val = leftSign * leftOpVal + rightOpVal;
        }

        return expr.substring(0, leftOpStart) + val + expr.substring(rightOpEnd + 1, expr.length());
    }

    public String evalVars(String expr) {
        ArrayList<Integer> invertedVals = new ArrayList<Integer>();
        for (int i = 0; i < expr.length(); ++i) {
            if (expr.charAt(i) == '-' && (i == 0 || (!Character.isDigit(expr.charAt(i-1)) && !Character.isLetter(expr.charAt(i-1)) && expr.charAt(i-1) != '_'))) {
                int length = 1;
                while (length < i + expr.length() && (Character.isDigit(expr.charAt(i+length)) || Character.isLetter(expr.charAt(i+length)) || expr.charAt(i+length) == '_')) {
                    length++;
                }
                String left = expr.substring(0, i);
                String right = expr.substring(i + length, expr.length());
                invertedVals.add(evalVar(expr.substring(i, i+length)));
                expr = left + ("$" + (invertedVals.size()-1)) + right;
                i = 0;
            }
        }
        String vars[] = expr.split("[\\%\\+\\-\\*\\/\\^]");
        String result = expr;
        for (int i = 0; i < vars.length; ++i) {
            if (vars[i].startsWith("$")) continue;
            result = result.replace(vars[i], Integer.toString(evalVar(vars[i])));
        }
        for (int i = 0; i < invertedVals.size(); ++i) {
            result = result.replace("$" + i, "" + invertedVals.get(i));
        }
        return result;
    }

    public int evalVal(String expr, ArrayList<Pair<String, Integer>> specialVars) {
        if (expr.length() == 0) return 0;
        if (specialVars != null) {
            this.specialVars = specialVars;
            specialVars = null;
        }
        if (expr.contains("(")) {
            for (int i = 0; i < expr.length(); ++i) {
                if (expr.charAt(i) == '(') {
                    int openCount = 1;
                    for (int j = i + 1; j < expr.length(); ++j) {
                        if (expr.charAt(j) == '(') openCount++;
                        else if (expr.charAt(j) == ')') openCount--;
                        if (expr.charAt(j) == ')' && openCount == 0) {
                            String subStr = expr.substring(i + 1, j);
                            int out = evalVal(subStr, null);
                            String split[] = expr.split(Pattern.quote(subStr));
                            String left = split[0].substring(0, split[0].length() - 1);
                            String right = split[1].substring(1);
                            expr = left + out + right;
                            i = 0;
                            break;
                        }
                    }
                }
            }
        }
        if (expr.contains("%")) {
            for (int i = 0; i < expr.length(); ++i) {
                if (expr.charAt(i) == '%') {
                    expr = evalBinaryOperator(expr, i);
                    i = 0;
                }
            }
        }
        if (expr.contains("^")) {
            for (int i = 0; i < expr.length(); ++i) {
                if (expr.charAt(i) == '^') {
                    expr = evalBinaryOperator(expr, i);
                    i = 0;
                }
            }
        }
        if (expr.contains("*") || expr.contains("/")) {
            for (int i = 0; i < expr.length(); ++i) {
                if (expr.charAt(i) == '*' || expr.charAt(i) == '/') {
                    expr = evalBinaryOperator(expr, i);
                    i = 0;
                }
            }
        }
        if (expr.contains("+") || expr.contains("-")) {
            for (int i = 0; i < expr.length(); ++i) {
                if (expr.charAt(i) == '+' || (expr.charAt(i) == '-' && (i < expr.length()-1 || Character.isDigit(expr.charAt(i+1))))) {
                    expr = evalBinaryOperator(expr, i);
                    i = 0;
                }
            }
        }
        expr = evalVars(expr);
        return Integer.parseInt(expr);
    }

    public boolean parseCondition(String condition, ArrayList<Pair<String, Integer>> specialVars) {
        if (condition.equals("")) return true;
        if (specialVars != null) {
            this.specialVars = specialVars;
            specialVars = null;
        }
        String nodes[] = condition.split(";");
        if (condition.contains("LEFT")) {
            for (int i = 0; i < nodes.length; ++i) {
                if (nodes[i].equals("LEFT")) {
                    int openCount = 1;
                    for (int j = i + 1; j < nodes.length; ++j) {
                        if (nodes[j].equals("LEFT")) openCount++;
                        else if (nodes[j].equals("RIGHT")) openCount--;
                        if (nodes[j].equals("RIGHT") && openCount == 0) {
                            String subStr = "";
                            for (int z = i+1; z < j; ++z) {
                                subStr += nodes[z];
                                if (z < j-1) subStr += ";";
                            }
                            boolean out = parseCondition(subStr, specialVars);
                            int openCount2;
                            int saveJ = j;
                            if (out) {
                                while (j < nodes.length - 2 && nodes[j+1].equals("OR")) {
                                    if (nodes[j+2].equals("LEFT")) {
                                        nodes[j+1] = nodes[j+2] = "TRUE";
                                        j+=3;
                                        openCount2 = 1;
                                        do {
                                            if (nodes[j].equals("RIGHT")) openCount2--;
                                            else if (nodes[j].equals("LEFT")) openCount2++;
                                            nodes[j] = "TRUE";
                                            ++j;
                                        } while (j < nodes.length && openCount2 > 0);
                                        --j;
                                    } else {
                                        nodes[j+1] = nodes[j+2] = "TRUE";
                                        j += 2;
                                    }
                                }
                            } else {
                                while (j < nodes.length - 2 && nodes[j+1].equals("AND")) {
                                    if (nodes[j+2].equals("LEFT")) {
                                        nodes[j+1] = nodes[j+2] = "FALSE";
                                        j+=3;
                                        openCount2 = 1;
                                        do {
                                            if (nodes[j].equals("RIGHT")) openCount2--;
                                            else if (nodes[j].equals("LEFT")) openCount2++;
                                            nodes[j] = "FALSE";
                                            ++j;
                                        } while (j < nodes.length && openCount2 > 0);
                                        --j;
                                    } else {
                                        nodes[j+1] = nodes[j+2] = "FALSE";
                                        j += 2;
                                    }
                                }
                            }
                            String split[] = condition.split(Pattern.quote(subStr));
                            String left;
                            if (split[0].length() > 5) {
                                left = split[0].substring(0, split[0].length() - 6);
                                if (left.charAt(left.length()-1) != ';') left += ';';
                            } else {
                                left = "";
                            }
                            String right = "";
                            for (int z = saveJ; z < nodes.length; ++z) {
                                right += ";" + nodes[z];
                            }
                            if (split[1].length() > 5) {
                                right = right.substring(6, right.length());
                            } else {
                                right = "";
                            }
                            if (out) {
                                condition = left + "TRUE" + right;
                            } else {
                                condition = left + "FALSE" + right;
                            }
                            nodes = condition.split(";");
                            i = 0;
                            break;
                        }
                    }
                }
            }
        }
        int b = 119;
        for (int i = 0; i < nodes.length; ++i) {
            String arguments[] = nodes[i].split(" ");
            if (arguments.length == 1 && (arguments[0].equals("TRUE") || arguments[0].equals("FALSE"))) continue;
            boolean val = false;
            int actValLeft;
            if (arguments[0].charAt(0) == '!') {
                actValLeft = evalVal(arguments[0].substring(1), null);
            } else {
                actValLeft = evalVal(arguments[0], null);
            }
            if (arguments.length < 3) {
                val = actValLeft != 0;
                if (arguments[0].charAt(0) == '!') {
                    val = !val;
                }
            } else if (arguments.length == 3) {
                int actValRight = evalVal(arguments[2], null);
                if (arguments[1].equals(">"))
                    val = actValLeft > actValRight;
                else if (arguments[1].equals("<"))
                    val = actValLeft < actValRight;
                else if (arguments[1].equals(">="))
                    val = actValLeft >= actValRight;
                else if (arguments[1].equals("<="))
                    val = actValLeft <= actValRight;
                else if (arguments[1].equals("=="))
                    val = actValLeft == actValRight;
                else if (arguments[1].equals("!="))
                    val = actValLeft != actValRight;
            }
            if (val) {
                nodes[i] = "TRUE";
                while (i < nodes.length-2 && nodes[i+1].equals("OR")) {
                    nodes[i+1] = nodes[i+2] = "TRUE";
                    i += 2;
                }
            } else {
                nodes[i] = "FALSE";
                while (i < nodes.length-2 && nodes[i+1].equals("AND")) {
                    nodes[i+1] = nodes[i+2] = "FALSE";
                    i += 2;
                }
            }
        }
        for (int i = 0; i < nodes.length-2; ++i) {
            if (i >= nodes.length) break;
            if (nodes[i+1].equals("AND")) {
                if (nodes[i].equals("TRUE") && nodes[i+2].equals("TRUE")) {
                    nodes[i] = nodes[i+1] = nodes[i+2] = "TRUE";
                } else {
                    nodes[i] = nodes[i+1] = nodes[i+2] = "FALSE";
                }
            }
        }
        for (int i = 0; i < nodes.length-2; ++i) {
            if (i >= nodes.length) break;
            if (nodes[i+1].equals("OR")) {
                if (nodes[i].equals("TRUE") || nodes[i+2].equals("TRUE")) {
                    nodes[i] = nodes[i+1] = nodes[i+2] = "TRUE";
                } else {
                    nodes[i] = nodes[i+1] = nodes[i+2] = "FALSE";
                }
            }
        }
        return nodes[nodes.length-1].equals("TRUE");
    }
}
