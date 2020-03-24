package Parser.Types;

import Parser.Expressions.*;

public class ClassType implements Type {
    public final Exp className;

    public ClassType(final Exp className) {
        this.className = className;
    }
    public boolean equals(Object other) {
       if (other instanceof ClassType) {
           ClassType otherClass = (ClassType) other;
           return className.equals(otherClass.className);
       }
       else return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + " <" + className + ">");
    }
}
