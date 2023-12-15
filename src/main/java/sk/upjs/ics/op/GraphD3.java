package sk.upjs.ics.op;

import java.util.List;

public class GraphD3 {

    private List<Node> nodes;
    private List<Link> links;

    public GraphD3(List<Node> nodes, List<Link> links) {
        this.nodes = nodes;
        this.links = links;
    }

    public GraphD3() {
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "{ \n" +
                "\"nodes\": " + nodes.toString() + ", \n"+
                "\"links\": " + links.toString() +
                "\n}\n";
    }
}
