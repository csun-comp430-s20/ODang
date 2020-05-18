package Parser.Declarations;

import Parser.Types.*;
public class MethodHeader implements Decl {
    public final ParserType resultParserType;
    public final Decl methodDeclarator;

    public MethodHeader(final ParserType resultParserType, final Decl methodDeclarator) {
        this.resultParserType = resultParserType;
        this.methodDeclarator = methodDeclarator;
    }
    public boolean equals(Object other) {
        if (other instanceof MethodHeader) {
            MethodHeader otherMH = (MethodHeader) other;
            return resultParserType.equals(otherMH.resultParserType) && methodDeclarator.equals(otherMH.methodDeclarator);
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + " (" + resultParserType + " " + methodDeclarator + ")");
    }
}
