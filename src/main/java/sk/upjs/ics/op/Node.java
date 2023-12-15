package sk.upjs.ics.op;

public class Node {

    private String id;
    private String group;
    private String detail;

    public Node(String id, String group, String detail) {
        this.id = id;
        this.group = group;
        this.detail = detail;
    }

    public Node() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\": " + id + "," +
                "\"group\": \"" + group + "\"," +
                "\"detail\": " + detail + "" +
                "}\n";
    }
}
