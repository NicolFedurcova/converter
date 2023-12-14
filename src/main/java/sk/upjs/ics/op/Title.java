package sk.upjs.ics.op;

import java.util.List;

public class Title {

    private String kind;
    private String name;
    private List<Label> labels;

    public Title() {
    }

    public Title(String kind, String name, List<Label> labels) {
        this.kind = kind;
        this.name = name;
        this.labels = labels;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    @Override
    public String toString() {
        return "{" +
                "\"kind\": \"" + kind + '\"' +
                ", \"name\": \"" + name + '\"' +
                ", \"labels\": " + labels +
                "}";
    }
}
