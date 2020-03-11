package Parser.Nodes;

public class BooleanExp implements Exp {
    public final Exp left;
    public final Exp right;

    public BooleanExp(final Exp left, final Exp right) {
        this.left = left;
        this.right = right;
    }
}
