package sk.upjs.ics.op;

public class Link {

    private int source;
    private int target;
    private int value;

    public Link(int source, int target, int value) {
        this.source = source;
        this.target = target;
        this.value = value;
    }

    public Link() {
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
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
