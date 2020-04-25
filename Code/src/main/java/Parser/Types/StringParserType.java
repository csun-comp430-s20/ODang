package Parser.Types;

public class StringParserType implements ParserType {
    public boolean equals(Object other) {
        return (other instanceof StringParserType);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
