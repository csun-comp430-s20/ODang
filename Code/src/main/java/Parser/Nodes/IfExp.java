package Parser.Nodes;

import Parser.Nodes.Exp;

public class IfExp implements Exp {
    public final Exp guard;
    public final Exp trueBranch;
    public final Exp falseBranch;

    public IfExp(final Exp guard, final Exp trueBranch, final Exp falseBranch) {
        this.guard = guard;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }
}
