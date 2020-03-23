package Parser.Expressions;

public class PostIncrDecrExp implements  Exp {
    public final Exp postfixExp;
    public final String postOp;

    public PostIncrDecrExp(final Exp postfixExp, final String postOp) {
        this.postfixExp = postfixExp;
        this.postOp = postOp;
    }

    public boolean equals(final Object other) {
        if (other instanceof PostIncrDecrExp) {
            final PostIncrDecrExp otherExp = (PostIncrDecrExp)other;
            return (postfixExp.equals(otherExp.postfixExp) &&
                    postOp.equals(otherExp.postOp));
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + " " + postfixExp + " " + postOp);
    }
}

