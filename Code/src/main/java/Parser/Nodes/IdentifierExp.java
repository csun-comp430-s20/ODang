package Parser.Nodes;

import Parser.Nodes.Exp;

public class IdentifierExp implements Exp {
    public final String name;

    public IdentifierExp(final String name) {
        this.name = name;
    }
}
