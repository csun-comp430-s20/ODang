package Parser.Types;

@Deprecated
public class ParserVoid implements ParserType {
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
