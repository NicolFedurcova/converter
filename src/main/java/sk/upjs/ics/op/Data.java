package sk.upjs.ics.op;

import java.util.ArrayList;
import java.util.Objects;

public class Data {
    private String kind;
    private String name;
    private ArrayList<String> labels;
    private String detail;

    public Data() {
    }

    public Data(String kind, String name, ArrayList<String> labels, String detail) {
        this.kind = kind;
        this.name = name;
        this.labels = labels;
        this.detail = detail;
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

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return Objects.equals(kind, data.kind) && Objects.equals(name, data.name) && Objects.equals(labels, data.labels) && Objects.equals(detail, data.detail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, name, labels, detail);
    }

    public String labelToString(ArrayList<String> labels){
        if(labels!=null){
            String result = "[ ";
            boolean first = true;
            for (String label:labels) {
                if(first){
                    result = result +"\"" +label+"\"";
                    first = false;
                }else{
                    result = result + ", " +"\"" +label+"\"";
                }
            }
            result = result + " ]";
            return result;
        } else {
            return "[ ]";
        }
    }

    @Override
    public String toString() {
        return "{ " +
                "\"kind\": \"" + kind + "\", " +
                "\"name\": \"" + name + "\", " +
                "\"labels\": " + labelToString(labels) + ", " +
                //"detail: \'" + detail + " \'" +
                "\"detail\": " + detail  +
                '}';
    }
}
