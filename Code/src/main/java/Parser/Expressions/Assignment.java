package Parser.Expressions;

public class Assignment implements Exp  {
    public final Exp left;
    public final String assignmentOp;
    public final Exp assignmentExp;

    public Assignment(final Exp left, final String assignmentOp, final Exp assignmentExp) {
        this.left = left;
        this.assignmentOp = assignmentOp;
        this.assignmentExp = assignmentExp;
    }

    public boolean equals(final Object other) {
        if (other instanceof Assignment) {
            final Assignment otherExp = (Assignment)other;
            return (left.equals(otherExp.left) &&
                    assignmentOp.equals(otherExp.assignmentOp) &&
                    assignmentExp.equals(otherExp.assignmentExp));
        }
        else return false;
    }
    public String toString() {
        return String.format(this.getClass().getName() + "< " + left + assignmentOp + assignmentExp + " >");
    }
}
