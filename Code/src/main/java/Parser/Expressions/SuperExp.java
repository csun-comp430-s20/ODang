package Parser.Expressions;

//TODO might move ?
public class SuperExp implements Exp {

    public boolean equals(final Object other) {
        return other instanceof SuperExp;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
