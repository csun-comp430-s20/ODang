package Parser.Expressions;

public class ClassInstance implements Exp {
    final Exp className;
    final ArgumentList argList;

    public ClassInstance(final Exp className, final ArgumentList argList) {
        this.className = className;
        this.argList = argList;
    }
    @Override
    public boolean equals(final Object other) {
        if (other instanceof ClassInstance) {
            final ClassInstance otherClass = (ClassInstance) other;
            return className.equals(otherClass.className) &&
                    argList.equals(otherClass.argList);

        } else
            return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() +
                "<className: " + className + " argList: " + argList + ">");
    }
}
