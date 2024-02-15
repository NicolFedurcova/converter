package sk.upjs.ics.op.entities;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Detail detail = (Detail) o;
        return Objects.equals(info, detail.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(info);
    }
}
