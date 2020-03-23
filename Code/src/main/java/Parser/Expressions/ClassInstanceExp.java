package Parser.Expressions;

public class ClassInstanceExp implements Exp {
    public final Exp className;
    public final ArgumentList argList;

    public ClassInstanceExp(final Exp className, final ArgumentList argList) {
        this.className = className;
        this.argList = argList;
    }
    @Override
    public boolean equals(final Object other) {
        if (other instanceof ClassInstanceExp) {
            final ClassInstanceExp otherClass = (ClassInstanceExp) other;
            return className.equals(otherClass.className) &&
                    argList.equals(otherClass.argList);

        } else
            return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() +
                " className: " + className + " argList: (" + argList + ")");
    }
}
