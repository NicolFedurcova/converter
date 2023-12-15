package sk.upjs.ics.op;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConvertJson {

    public static String makeId(String title){
        /*
        String copy = title;
        copy.replace("\"", " ");
        copy.replace("{", " ");
        copy.replace("}", " ");

        System.out.println("ID: " + copy);
        return copy;
         */
        String copy = "";
        for(int i = 0; i<title.length(); i++){
            if(title.charAt(i)=='"'){
                copy = copy + " ";
            }
            else if(title.charAt(i)=='{'){
                copy = copy + "";
            }
            else if(title.charAt(i)=='}'){
                copy = copy + "";
            } else{
                copy = copy + title.charAt(i);
            }
        }
        //System.out.println("ID: " + "\"" + copy + "\"");
        return "\"" + copy + "\"";
    }

    public static String makeIntoGraphD3(String filePath){
        String inputString = "";
        try {
            inputString = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GraphD3 graph = new GraphD3();
        List<Node> nodes = new ArrayList<>();
        List<Link> links = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(inputString);
        JSONArray vertices = jsonObject.getJSONArray("vertices");
        JSONArray edges = jsonObject.getJSONArray("edges");

        for (int i = 0; i < vertices.length(); i++) {
            Node n = new Node();
            n.setId(makeId(vertices.getJSONObject(i).getJSONObject("title").toString()));
            n.setGroup(vertices.getJSONObject(i).getJSONObject("title").getString("kind"));
            n.setDetail(vertices.getJSONObject(i).getJSONObject("details").toString());
            nodes.add(n);
        }

        for (int i = 0; i <edges.length() ; i++) {
            Link l = new Link();
            l.setSource(makeId(edges.getJSONArray(i).getJSONObject(0).getJSONObject("title").toString()));
            l.setTarget(makeId(edges.getJSONArray(i).getJSONObject(1).getJSONObject("title").toString()));
            l.setValue(3);
            links.add(l);
        }

        graph.setNodes(nodes);
        graph.setLinks(links);
        return graph.toString();
    }

    public static void main(String[] args) {

        Parser parser = new Parser("src/main/java/sk/upjs/ics/op/input.json");
        String result = parser.parse();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/sk/upjs/ics/op/output.json"))) {
            writer.write(result);
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }

        String graph = makeIntoGraphD3("src/main/java/sk/upjs/ics/op/output.json");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/sk/upjs/ics/op/graphD3.json"))) {
            writer.write(graph);
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }


    }
}
