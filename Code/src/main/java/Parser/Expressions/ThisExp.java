package Parser.Expressions;

public class ThisExp implements Exp {

    public boolean equals(final Object other) {
        return other instanceof ThisExp;
    }
    public String toString() {
        return String.format(this.getClass().getName());
    }
}
