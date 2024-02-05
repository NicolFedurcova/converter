package sk.upjs.ics.op;

import java.util.ArrayList;
import java.util.Objects;

public class TreeNode {

    private Integer id;
    private Integer firstParent;
    private ArrayList<Integer> listParents;
    private String topLabel;
    private String group;
    private String detail;

    public TreeNode() {
    }

    public TreeNode(Integer id, Integer firstParent, ArrayList<Integer> listParents, String topLabel, String group, String detail) {
        this.id = id;
        this.firstParent = firstParent;
        this.listParents = listParents;
        this.topLabel = topLabel;
        this.group = group;
        this.detail = detail;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFirstParent() {
        return firstParent;
    }

    public void setFirstParent(Integer firstParent) {
        this.firstParent = firstParent;
    }

    public ArrayList<Integer> getListParents() {
        return listParents;
    }

    public void setListParents(ArrayList<Integer> listParents) {
        this.listParents = listParents;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeNode treeNode = (TreeNode) o;
        return Objects.equals(id, treeNode.id) && Objects.equals(firstParent, treeNode.firstParent) && Objects.equals(listParents, treeNode.listParents) && Objects.equals(topLabel, treeNode.topLabel) && Objects.equals(group, treeNode.group) && Objects.equals(detail, treeNode.detail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstParent, listParents, topLabel, group, detail);
    }

    @Override
    public String toString() {
        return id +","+ firstParent +","+
                "\"" + listParents +"\""+","+
                "\"" + topLabel + "\"" +","+
                "\"" + group + "\"" +","+
                "\"" + detail + "\"" +",";

    }

    public String nullToString() {
        return id +",,"+
                "\"" + listParents +"\""+","+
                "\"" + topLabel + "\"" +","+
                "\"" + group + "\"" +","+
                "\"" + detail + "\"" +",";

    }
}
