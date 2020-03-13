package Parser.Expressions;

public class AssignmentExp implements Exp {
    public final Exp exp;

    public AssignmentExp(final Exp exp) {
        this.exp = exp;
    }
}
