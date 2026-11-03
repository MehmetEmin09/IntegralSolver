package integral.math;

import integral.ast.FuncKind;
import integral.ast.Node;

import static integral.ast.Node.*;

public final class Evaluator {

    private Evaluator() {}

    // Returns NaN where the expression is undefined at x.
    public static double evaluate(Node node, double x) {
        return switch (node) {
            case Num n -> n.value().numerator().doubleValue()
                    / n.value().denominator().doubleValue();
            case Var v -> x;
            case Const c -> switch (c.kind()) {
                case PI -> Math.PI;
                case E -> Math.E;
            };
            case Add a -> evaluate(a.left(), x) + evaluate(a.right(), x);
            case Mul m -> evaluate(m.left(), x) * evaluate(m.right(), x);
            case Pow p -> power(evaluate(p.base(), x), evaluate(p.exponent(), x));
            case Neg ng -> -evaluate(ng.operand(), x);
            case Func f -> applyFunc(f.kind(), evaluate(f.arg(), x));
        };
    }

    // Math.pow(0, negative) gives Infinity; treat it as undefined.
    private static double power(double base, double exponent) {
        double result = Math.pow(base, exponent);
        return Double.isInfinite(result) ? Double.NaN : result;
    }

    private static double applyFunc(FuncKind kind, double u) {
        return switch (kind) {
            case SIN -> Math.sin(u);
            case COS -> Math.cos(u);
            case TAN -> Math.tan(u);
            case SEC -> 1.0 / Math.cos(u);
            case CSC -> 1.0 / Math.sin(u);
            case COT -> 1.0 / Math.tan(u);
            case LN -> u > 0 ? Math.log(u) : Double.NaN;
            case ASIN -> Math.asin(u);
            case ACOS -> Math.acos(u);
            case ATAN -> Math.atan(u);
            case ABS -> Math.abs(u);
        };
    }
}