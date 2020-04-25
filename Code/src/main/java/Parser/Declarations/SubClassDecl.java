package Parser.Declarations;

import Parser.Expressions.Exp;
import Parser.Types.ParserType;

@Deprecated
public class SubClassDecl extends ClassDecl {

    public final ParserType classParserType;

    public SubClassDecl(final Exp identifier, final ParserType classParserType, final Decl classBody) {
        super(identifier, classBody);
        this.classParserType = classParserType;
    }

    public boolean equals(Object other) {
        if (other instanceof SubClassDecl) {
            SubClassDecl otherSCL = (SubClassDecl) other;
            return identifier.equals(otherSCL.identifier) &&
                    classBody.equals(otherSCL.classBody) &&
                    classParserType.equals(otherSCL.classParserType);
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSuperclass().getSimpleName() +
                "(" + identifier +  " extends " + classParserType + " {" + classBody + "})");
    }
}
