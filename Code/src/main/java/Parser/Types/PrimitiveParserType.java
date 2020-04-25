package Parser.Types;


public class PrimitiveParserType implements ParserType {
    public final ParserType parserType;

    public PrimitiveParserType(final ParserType parserType) {
        this.parserType = parserType;
    }

    public boolean equals(Object other) {
        if (other instanceof PrimitiveParserType) {
            final PrimitiveParserType otherType = (PrimitiveParserType) other;
            return this.parserType.equals(otherType.parserType);
        } else
            return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + " type: <" + parserType + ">");
    }
}
