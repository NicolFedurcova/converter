package sk.upjs.ics.op;

import java.util.List;
import java.util.Set;

public class GraphJSON {

    private Set<VertexObject> vertices;
    private List<List<VertexObject>> edges;

    public GraphJSON(Set<VertexObject> vertices, List<List<VertexObject>> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }

    public GraphJSON(){}

    public Set<VertexObject> getVertices() {
        return vertices;
    }

    public void setVertices(Set<VertexObject>vertices) {
        this.vertices = vertices;
    }

    public List<List<VertexObject>> getEdges() {
        return edges;
    }

    public void setEdges(List<List<VertexObject>> edges) {
        this.edges = edges;
    }

    @Override
    public String toString() {
        return "{" +
                "\n\"vertices\": " + vertices.toString() +
                ", \n\"edges\": " + edges.toString() +
                "\n} \n";
    }
}
