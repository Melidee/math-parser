import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class MathEval {
    private static ArrayList<Object> tokenize(String input) {
        String numBuf = "";
        ArrayList<Object> tokens = new ArrayList<Object>();
        for (char c : input.toCharArray()) {
            if (c == ' ') {
                continue;
            } else if ("+-*/^()".indexOf(c) != -1) {
                if (numBuf.length() > 0) {
                    try {
                        tokens.add(Double.parseDouble(numBuf));
                    } catch (NumberFormatException e) {}
                    numBuf = "";
                }
                tokens.add(c);
            } else {
                numBuf += c;
            }
        }
        if (numBuf != "") {
            tokens.add(Double.parseDouble(numBuf));
        }
        return tokens;
    }

    private static Double parse(ArrayList<Object> tokens) {
        boolean unnested = true;
        int parenCounter = 0;
        ArrayList<Object> expression = new ArrayList<Object>();
        ArrayList<Object> nestedExpression = new ArrayList<Object>();
        for (Object t : tokens) {
            if (t instanceof Double) {
                Double n = (Double) t;
                if (parenCounter != 0) {
                    nestedExpression.add(n);
                } else {
                    expression.add(n);
                }
            } else if (t instanceof Character) {
                Character c = (Character) t;
                if (c == '(') {
                    parenCounter++;
                    if (parenCounter != 1) {
                        nestedExpression.add(c);
                    } else {
                        unnested = false;
                    }
                } else if (c == ')') {
                    parenCounter--;
                    if (parenCounter == 0 && !unnested) {
                        double a = MathEval.parse(nestedExpression);
                        expression.add(a);
                        nestedExpression.clear();
                    } else if (parenCounter == 0 && unnested) {
                        expression.add(evalTokens(nestedExpression));
                    }
                } else {
                    if (parenCounter != 0) {
                        nestedExpression.add(c);
                    } else {
                        expression.add(c);
                    }
                }
            }
        }
        // Here we can assume `expression` contains only numbers and operators
        return evalTokens(expression);
    }

    private static Double evalTokens(ArrayList<Object> tokens) {
        String[] sweeps = { "^", "*/", "+-" };
        for (String sweep : sweeps) {
            for (int i = 0; i < tokens.size(); i++) {
                if (tokens.get(i) instanceof Character) {
                    Character c = (Character) tokens.get(i);
                    if (sweep.indexOf(c) != -1) {
                        Double a = (Double) tokens.get(i - 1);
                        Double b = (Double) tokens.get(i + 1);
                        Double result = null;
                        if (c == '^') {
                            result = Math.pow(a, b);
                        } else if (c == '/') {
                            result = a / b;
                        } else if (c == '*') {
                            result = a * b;
                        } else if (c == '-') {
                            result = a - b;
                        } else if (c == '+') {
                            result = a + b;
                        }
                        tokens.set(i - 1, null);
                        tokens.set(i, null);
                        tokens.set(i + 1, result);
                    }
                }
            }
            tokens.removeAll(Collections.singleton(null));
        }
        return (Double) tokens.get(0);
    }

    private static Double evaluate(String input) {
        ArrayList<Object> tokens = tokenize(input);
        return parse(tokens);
    }

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("Enter an equation to parse (exit to quit): ");
            while (true) {
                String input = sc.nextLine();
                if (input == "exit") {
                    break;
                }
                System.out.println(evaluate(input));
                System.out.println("");
            }
        }
    }
}
