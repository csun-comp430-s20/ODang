package Parser.Declarations;

import Parser.Statements.*;
public class MethodDeclaration implements Decl {
    public final Decl header;
    public final Stmt body;

    public MethodDeclaration(final Decl header, final Stmt body) {
        this.header = header;
        this.body = body;
    }

    public boolean equals(Object other) {
        if (other instanceof MethodDeclaration) {
            MethodDeclaration otherMD = (MethodDeclaration) other;
            return header.equals(otherMD.header) && body.equals(otherMD.body);
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + " (" + header + " " + body + ")");
    }
}
