package sk.upjs.ics.op;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvertJson {

    public static String makeUniqueID(JSONObject object){

        String title = makeTopLabel(object.getJSONObject("title").toString());
        String details = makeTopLabel(object.getJSONObject("details").toString());
        return  makeTopLabel(title)+ "     " + makeTopLabel(details);


    }

    public static String makeTopLabel(String title){
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

    public static String removeDelimiter(String title){

        String copy = "";
        for(int i = 0; i<title.length(); i++) {
            if (title.charAt(i) == ';') {
                copy = copy + " ";
            } else {
                if(title.charAt(i) == '\\'){
                    System.out.println("TOTOOOO " + title.charAt(i) + title.charAt(i+1));
                    copy = copy + " ";
                } else {
                    copy = copy + title.charAt(i) ;
                }


            }
        }

        return copy;

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

        Map<String, Integer> mapa = new HashMap<>();
        int idCounter = 0;

        //make id into mapy kde bude cislo:makeID
        for (int i = 0; i < vertices.length(); i++) {
            Node n = new Node();
            String topLabel = makeTopLabel(vertices.getJSONObject(i).getJSONObject("title").toString());

            String uniqueID = makeUniqueID(vertices.getJSONObject(i));


            if(mapa.containsKey(uniqueID)){
                n.setId(mapa.get(uniqueID));
            } else {
                n.setId(idCounter);
                mapa.put(uniqueID, idCounter);
                idCounter++;
            }
            n.setTopLabel(removeDelimiter(topLabel));
            n.setGroup(removeDelimiter(vertices.getJSONObject(i).getJSONObject("title").getString("kind")));
            n.setDetail(removeDelimiter(vertices.getJSONObject(i).getJSONObject("details").toString()));
            nodes.add(n);
        }

        for (int i = 0; i <edges.length() ; i++) {
            Link l = new Link();
            String sourceTopLabel = makeTopLabel(edges.getJSONArray(i).getJSONObject(0).getJSONObject("title").toString());
            String targetTopLabel = makeTopLabel(edges.getJSONArray(i).getJSONObject(1).getJSONObject("title").toString());

            String sourceUniqueId = makeUniqueID(edges.getJSONArray(i).getJSONObject(0));
            String targetUniqueId = makeUniqueID(edges.getJSONArray(i).getJSONObject(1));


            //l.setSource(mapa.get(sourceTopLabel));
            //l.setTarget(mapa.get(targetTopLabel));

            if(mapa.containsKey(sourceUniqueId)){
                l.setSource(mapa.get(sourceUniqueId));
            } else {
                l.setSource(idCounter);
                mapa.put(sourceUniqueId, idCounter);
                idCounter++;
            }

            if(mapa.containsKey(targetUniqueId)){
                l.setTarget(mapa.get(targetUniqueId));
            } else {
                l.setTarget(idCounter);
                mapa.put(targetUniqueId, idCounter);
                idCounter++;
            }

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
