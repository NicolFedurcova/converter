package sk.upjs.ics.op.entities;

public class Label {

    private String name;
    private String value;

    public Label(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Label(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return  "{\""+name + "\": \"" + value+"\"}";
    }
}
