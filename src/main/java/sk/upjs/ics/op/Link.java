package sk.upjs.ics.op;

import java.util.Objects;

public class Link {

    private String source;
    private String target;
    private String value;

    public Link(String source, String target, String value) {
        this.source = source;
        this.target = target;
        this.value = value;
    }
    public Link() {
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\": \"" + value + "\"" + ", " +
                "\"source\": \"" + source + "\"" + ", " +
                "\"target\": \"" + target + "\"" +
                "}\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return Objects.equals(source, link.source) && Objects.equals(target, link.target) && Objects.equals(value, link.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target, value);
    }
}