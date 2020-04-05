package Parser.Declarations;

import java.util.ArrayList;
import java.util.List;

public class ClassBodyDecs implements Decl {
    public final List<Decl> classBodyDecs;

    public ClassBodyDecs(final Decl... classBodyDecs) {
        this.classBodyDecs = new ArrayList<>();

        if (classBodyDecs != null) {
            for (final Decl decl : classBodyDecs)
                this.classBodyDecs.add(decl);
        }
    }

    public boolean equals(Object other) {
        if (other instanceof ClassBodyDecs) {
            ClassBodyDecs otherClassDecs = (ClassBodyDecs) other;
            return classBodyDecs.equals(otherClassDecs.classBodyDecs);
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + " " + classBodyDecs);
    }
}
