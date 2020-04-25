package Parser.Declarations;
import Parser.Types.*;
import Parser.Expressions.*;

public class FormalParam implements Decl {
    public final ParserType paramParserType;
    public final Exp paramIdentifier;

    public FormalParam(final ParserType paramParserType, final Exp paramIdentifier){
        this.paramParserType = paramParserType;
        this.paramIdentifier = paramIdentifier;
    }

    public boolean equals(final Object other){
        if (other instanceof FormalParam){
            final FormalParam otherFormalParam = (FormalParam) other;
            return this.paramParserType.equals(otherFormalParam.paramParserType) &&
                    this.paramIdentifier.equals(otherFormalParam.paramIdentifier);
        } else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() +
                " " + paramParserType + " " + paramIdentifier);
    }
}
