package Parser.Types;

public class ClassType implements Type {

    public boolean equals(Object other) {
       return (other instanceof ClassType);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
