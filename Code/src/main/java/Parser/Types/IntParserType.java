package Parser.Types;

public class IntParserType implements ParserType {
    public boolean equals(Object other) {
        return (other instanceof IntParserType);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
