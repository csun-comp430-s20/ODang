package Parser.Nodes;

import Parser.Nodes.Exp;

public class IntegerExp implements Exp {
    public final int value;

    public IntegerExp(final int value) {
        this.value = value;
    }
}
