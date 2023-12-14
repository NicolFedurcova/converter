package sk.upjs.ics.op;

import org.json.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Parser {

    String inputString;
    String filePath;

    private Parser(){
    }
    private Parser(String filePath){
        this.filePath = filePath;
    }

    public String parse(){
        try {
            this.inputString = new String(Files.readAllBytes(Paths.get(this.filePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject jsonObject =new JSONObject(inputString);
        Map<String, List<String>> objectLabels = getLabels(jsonObject);
        List<VertexObject> objects = new ArrayList<>();
        List<String> unusualObjects = new ArrayList<>(Arrays.asList("volumes", "containers", "secrets"));


        for(String objectName : objectLabels.keySet()){
            System.out.println("TYP: " + objectName);
            JSONArray objectList = jsonObject.getJSONArray(objectName);
            List<String> labels = objectLabels.get(objectName);
            for(int i=0; i<objectList.length(); i++){
                objects.add(convertCommonObject(objectList.getJSONObject(i), labels));
                /*
                if(i==3){
                    break;
                }*/
            }
        }

        for(String unusualObjectName: unusualObjects) {
            System.out.println("TYP: " + unusualObjectName);
            JSONArray unusualObjectList = jsonObject.getJSONArray(unusualObjectName);
            for (int j = 0; j < unusualObjectList.length(); j++) {
                objects.add(convertUnusualObject(unusualObjectList.getJSONObject(j), unusualObjectName));
            }

        }

        System.out.println("OBJECTS CREATED: " + objects);
        return objects.toString();

    }

    private String stripLabel(String label){
        System.out.println(label + " " + label.substring(1,label.length()-1));
        return label.substring(1,label.length()-1);
    }

    private String stripKind(String kind){
        System.out.println("POVODNY KIND: " + kind + " STRIPNUTY: " + kind.substring(0,-1));
        return kind.substring(0,kind.length()-1);
    }

    private String stripDetail(String detail){
        return detail.substring(1,detail.length()-1);
    }

    private VertexObject convertUnusualObject(JSONObject object, String kind){
        VertexObject vo = new VertexObject();

        Title title = new Title();
        title.setKind(stripKind(kind));
        title.setName(object.getString("name"));
        object.remove("name");
        title.setLabels(null);

        Detail detail = new Detail();
        detail.setInfo(stripDetail(object.toString()));

        vo.setDetails(detail);
        vo.setTitle(title);
        return vo;
    }

    private VertexObject convertCommonObject(JSONObject object, List<String> listLabelNames){
        VertexObject vo = new VertexObject();
        List<Label> labelsList = new ArrayList<>();

        for (String labelName:listLabelNames) {
            Label label = new Label();
            label.setName(labelName);

            try {
                label.setValue(object.getJSONObject("metadata").getJSONObject("labels").getString(labelName));
                labelsList.add(label);
            } catch (JSONException e){
                continue;
            }

        }

        Title title = new Title();
        title.setKind(object.getString("kind"));
        object.remove("kind");
        title.setName(object.getJSONObject("metadata").getString("name"));
        object.getJSONObject("metadata").remove("name");
        title.setLabels(labelsList);
        object.getJSONObject("metadata").remove("labels");
        Detail detail = new Detail();
        detail.setInfo(stripDetail(object.toString()));

        vo.setDetails(detail);
        vo.setTitle(title);
        return vo;
    }

    public static Map<String, List<String>> getLabels(JSONObject topLevelObject){
        Map<String, List<String>> objectLabelNames = new HashMap<>();
        List<String> objectKinds = new ArrayList<>(Arrays.asList("services","pods", "deployments", "configMaps", "daemonSets"));
        for (String objectKind:objectKinds) {
            System.out.println("OBJECT KIND: " + objectKind);
            Set labels = new HashSet<>();
            JSONArray objects = topLevelObject.getJSONArray(objectKind);

            for(int i = 0; i<objects.length(); i++) {
                try {
                    for (String key : objects.getJSONObject(i).getJSONObject("metadata").getJSONObject("labels").keySet()) {
                        labels.add(key);
                    }
                }catch (JSONException e1){

                }
            }
            objectLabelNames.put(objectKind, new ArrayList<String>(labels));
        }
        System.out.println(objectLabelNames.toString());
        return objectLabelNames;
    }
    public static void main(String[] args) {

        Parser parser = new Parser("src/main/java/sk/upjs/ics/op/input.json");
        String result = parser.parse();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/sk/upjs/ics/op/output.json"))) {
            writer.write(result);
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }


    }


}
