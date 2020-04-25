package Parser.Types;

public class BooleanParserType implements ParserType {

    public boolean equals(Object other) {
        return (other instanceof BooleanParserType);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
