package Parser.Nodes;

public class MinusExp {
    public final Exp left;
    public final Exp right;

    public MinusExp(final Exp left, final Exp right) {
        this.left = left;
        this.right = right;
    }
}
