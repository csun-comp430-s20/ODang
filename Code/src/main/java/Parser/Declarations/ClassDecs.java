package Parser.Declarations;

import java.util.ArrayList;
import java.util.List;

public class ClassDecs implements Decl {
    public final List<Decl> classDecs;

    public ClassDecs(final Decl... classDecs) {
        this.classDecs = new ArrayList<>();

        if (classDecs != null) {
            for (final Decl decl : classDecs)
                this.classDecs.add(decl);
        }
    }

    public boolean equals(Object other) {
        if (other instanceof ClassDecs) {
            ClassDecs otherClassDecs = (ClassDecs) other;
            return classDecs.equals(otherClassDecs.classDecs);
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + " " + classDecs);
    }
}
