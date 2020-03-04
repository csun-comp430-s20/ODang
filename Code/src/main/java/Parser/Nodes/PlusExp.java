package Parser.Nodes;

public class PlusExp implements Exp {
    public final Exp left;
    public final Exp right;

    public PlusExp(final Exp left, final Exp right) {
        this.left = left;
        this.right = right;
    }
}
