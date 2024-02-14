package sk.upjs.ics.op;

import org.json.JSONArray;
import org.json.JSONException;
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

        String title = stripBracketsQuotes(object.getJSONObject("title").toString());
        String details = stripBracketsQuotes(object.getJSONObject("details").toString());
        return  stripBracketsQuotes(title)+ "     " + stripBracketsQuotes(details);


    }

    public static String stripBracketsQuotes(String title){
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
        return  copy ;
    }

    public static String replaceQuotes(String input){
        StringBuilder copy = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if(currentChar=='"'){
                if(i!=0 && input.charAt(i-1)!='\\' ){
                    copy.append("\\\"");
                } else {
                    copy.append(currentChar);
                }
            } else {
                copy.append(currentChar);
            }
        }
        return copy.toString();
        //return input.replace("\"", "\\\"");
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
        int idCounter = 1;

        //make id into mapy kde bude cislo:makeID
        for (int i = 0; i < vertices.length(); i++) {
            Node n = new Node();
            //String topLabel = makeTopLabel(vertices.getJSONObject(i).getJSONObject("title").toString());

            String uniqueID = makeUniqueID(vertices.getJSONObject(i));


            if(mapa.containsKey(uniqueID)){
                n.setId(mapa.get(uniqueID));
            } else {
                if(vertices.getJSONObject(i).getJSONObject("title").getString("kind").equals("namespace")){
                    n.setId(0);
                    mapa.put(uniqueID, 0);
                } else {
                    n.setId(idCounter);
                    mapa.put(uniqueID, idCounter);
                    idCounter++;
                }
            }
            Data data = new Data();
            data.setKind(vertices.getJSONObject(i).getJSONObject("title").getString("kind"));
            data.setName(vertices.getJSONObject(i).getJSONObject("title").getString("name"));

            try{
                System.out.println(vertices.getJSONObject(i).getJSONObject("title").getJSONArray("labels"));
                data.setLabels(convertJsonArray(vertices.getJSONObject(i).getJSONObject("title").getJSONArray("labels")));
            }catch(JSONException eee){
                data.setLabels(null);
            }
            data.setDetail(replaceQuotes(vertices.getJSONObject(i).getJSONObject("details").toString()));
            //data.setDetail((vertices.getJSONObject(i).getJSONObject("details").toString()));

            n.setData(data);
            n.setPosition("position");
            n.setType("objectNode");
            nodes.add(n);
        }

        for (int i = 0; i <edges.length() ; i++) {
            Link l = new Link();
            //String sourceTopLabel = stripBracketsQuotes(edges.getJSONArray(i).getJSONObject(1).getJSONObject("title").toString());
            //String targetTopLabel = stripBracketsQuotes(edges.getJSONArray(i).getJSONObject(0).getJSONObject("title").toString());

            String sourceUniqueId = makeUniqueID(edges.getJSONArray(i).getJSONObject(0));
            String targetUniqueId = makeUniqueID(edges.getJSONArray(i).getJSONObject(1));

            if(mapa.containsKey(sourceUniqueId)){
                l.setSource(mapa.get(sourceUniqueId).toString());
            } else {
                l.setSource(Integer.toString(idCounter));
                mapa.put(sourceUniqueId, idCounter);
                idCounter++;
            }

            if(mapa.containsKey(targetUniqueId)){
                l.setTarget(mapa.get(targetUniqueId).toString());
            } else {
                l.setTarget(Integer.toString(idCounter));
                mapa.put(targetUniqueId, idCounter);
                idCounter++;
            }

            l.setValue(mapa.get(sourceUniqueId)+ "->" + mapa.get(targetUniqueId));
            if(links.contains(l)){
            }else{
                links.add(l);
            }

        }

        graph.setNodes(nodes);
        graph.setLinks(links);
        return graph.toString();
    }

    public static ArrayList<String> convertJsonArray(JSONArray array){
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i <array.length() ; i++) {
            list.add(stripBracketsQuotes(array.get(i).toString()));
        }
        return list;
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
