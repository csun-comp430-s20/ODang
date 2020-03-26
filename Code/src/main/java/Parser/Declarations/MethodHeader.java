package Parser.Declarations;

import Parser.Types.*;
public class MethodHeader implements Decl {
    public final Type resultType;
    public final Decl methodDeclarator;

    public MethodHeader(final Type resultType, final Decl methodDeclarator) {
        this.resultType = resultType;
        this.methodDeclarator = methodDeclarator;
    }
    public boolean equals(Object other) {
        if (other instanceof MethodHeader) {
            MethodHeader otherMH = (MethodHeader) other;
            return resultType.equals(otherMH.resultType) && methodDeclarator.equals(otherMH.methodDeclarator);
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + " (" + resultType + " " + methodDeclarator + ")");
    }
}
