package sk.upjs.ics.op;

import java.util.Objects;

public class Node {

    private int id;
    private Data data;
    private String position;
    private String type;

    public Node(){

    }

    public Node(int id, Data data, String position, String type) {
        this.id = id;
        this.data = data;
        this.position = position;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id && Objects.equals(data, node.data) && Objects.equals(position, node.position) && Objects.equals(type, node.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, data, position, type);
    }

    @Override
    public String toString() {
        return "{" +
                "id: \"" + id + "\""+
                ", data: " + data +
                ", " + position +
                ", type: \"" + type +"\""+
                "}\n";
    }


}
