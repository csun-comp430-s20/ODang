package Parser.Types;

public class Void implements ParserType {
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
