package sk.upjs.ics.op;

public class Node {

    private int id;
    private String topLabel;
    private String group;
    private String detail;

    public Node(int id, String topLabel, String group, String detail) {
        this.id = id;
        this.topLabel = topLabel;
        this.group = group;
        this.detail = detail;
    }

    public Node() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopLabel() {
        return topLabel;
    }

    public void setTopLabel(String topLabel) {
        this.topLabel = topLabel;
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
                "\"topLabel\": " + topLabel + "," +
                "\"group\": \"" + group + "\"," +
                "\"detail\": " + detail + "" +
                "}\n";
    }
}
