package sk.upjs.ics.op;

public class Link {

    private String source;
    private String target;
    private int value;

    public Link(String source, String target, int value) {
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" +
                "\"source\": " + source + "," +
                "\"target\": " + target + "," +
                "\"value\": " + value + "" +
                "}\n";
    }
}
