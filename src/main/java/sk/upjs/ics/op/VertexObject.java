package sk.upjs.ics.op;

import java.util.List;
import java.util.Objects;

public class VertexObject { 

    private Title title;
    private Detail details;

    public VertexObject(Title title, Detail details) {
        this.title = title;
        this.details = details;
    }

    public VertexObject(){}

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public Detail getDetails() {
        return details;
    }

    public void setDetails(Detail details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "{" +
                "\n\"title\": " + title +
                ", \n\"details\": " + details +
                "\n} \n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VertexObject that = (VertexObject) o;
        return Objects.equals(title, that.title) && Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, details);
    }
}
