package sk.upjs.ics.op;

public class Detail {


    private String info;

    public Detail(String info) {
        this.info = info;
    }

    public Detail() {
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return " {" + info  + "} ";
    }
}
