package Parser.Types;

import Parser.Expressions.*;

public class ClassParserType implements ParserType {
    public final Exp className;

    public ClassParserType(final Exp className) {
        this.className = className;
    }
    public boolean equals(Object other) {
       if (other instanceof ClassParserType) {
           ClassParserType otherClass = (ClassParserType) other;
           return className.equals(otherClass.className);
       }
       else return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + " <" + className + ">");
    }
}
