package sk.upjs.ics.op;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CSVTreeJSON {

    private String source;
    private ArrayList<Integer> bezprizorni = new ArrayList<>();
    private Map<Integer, TreeNode> vsetko = new HashMap<>();

    public CSVTreeJSON(String source) {
        this.source = source;
    }

    public void makeCSV(String source, String destination){


        String inputJson = null;
        try {
            inputJson = new String(Files.readAllBytes(Paths.get(source)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(inputJson);

            if (rootNode.isObject()) {
                ArrayNode nodesArray = (ArrayNode) rootNode.get("nodes");

                for (JsonNode node : nodesArray) {
                    if (node.isObject()) {
                        ObjectNode objectNode = (ObjectNode) node;

                        // Extract values
                        int id = objectNode.get("id").asInt();
                        String topLabel = objectNode.get("topLabel").asText();
                        String group = objectNode.get("group").asText();
                        String detail = objectNode.get("detail").toString();

                        System.out.println(objectNode.get("first_parent"));
                        System.out.println(objectNode.get("first_parent").isNull());

                        Integer firstParent;
                        if(objectNode.get("first_parent").isNull()){
                            if(objectNode.get("id").asInt()==85){
                                firstParent = null;
                            }else {
                                bezprizorni.add(id);
                                continue;
                            }

                        }else {
                            firstParent = objectNode.get("first_parent").asInt();
                        }

                        System.out.println("FIRST PARENT: " + firstParent);

                        ArrayList<Integer> listParents = new ArrayList<>();

                        // Check if the JsonNode is an array
                        if (objectNode.get("list_parents").isArray()) {
                            // Iterate through the elements and add them to the ArrayList
                            Iterator<JsonNode> elements = objectNode.get("list_parents").elements();
                            while (elements.hasNext()) {
                                JsonNode element = elements.next();
                                // Assuming the elements are integers in this example
                                if (element.isInt()) {
                                    listParents.add(element.asInt());
                                }
                                // If the elements can be of different types, you need to handle them accordingly
                            }
                        }

                        TreeNode nodeTree = new TreeNode();
                        nodeTree.setId(id);
                        nodeTree.setFirstParent(firstParent);
                        nodeTree.setListParents(listParents);
                        nodeTree.setTopLabel(topLabel);
                        nodeTree.setGroup(group);
                        nodeTree.setDetail(detail);

                        vsetko.put(id, nodeTree);

                    }
                }


                boolean changeWasDone = true;
                while(changeWasDone){
                    changeWasDone = removeBezprizorni(vsetko, bezprizorni, changeWasDone);
                }



                // Create CSV file
                createCSV(destination, vsetko);

            } else {
                System.err.println("Invalid JSON structure.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void createCSV(String destination, Map<Integer, TreeNode> vsetko){
        try (FileWriter csvWriter = new FileWriter(destination)) {
            // Write header
            csvWriter.append("id,parentId,list_parents,topLabel,group,detail\n");
            // Write to CSV
            for (TreeNode n: vsetko.values()) {
                if(n.getFirstParent()==null){
                    csvWriter.append(n.nullToString()).append("\n");
                } else{
                    csvWriter.append(n.toString()).append("\n");
                }

            }

            System.out.println("CSV file created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean removeBezprizorni(Map<Integer, TreeNode> vsetko, ArrayList<Integer> bezprizorni, boolean changeWasDone){
        boolean somethingWasRemoved = false;
        ArrayList<Integer> nodesToRemove = new ArrayList<>();
        //odtsranime takych rodicov co su bezprizorni
        for(Integer id : vsetko.keySet()){
            ArrayList<Integer> parents = vsetko.get(id).getListParents();
            Integer firstParent = vsetko.get(id).getFirstParent();

            ArrayList<Integer> parentsToRemove = new ArrayList<>();
            for (Integer parent:parents) {
                if (bezprizorni.contains(parent)) {
                    parentsToRemove.add(parent);
                    somethingWasRemoved = true;
                }
            }
            for (Integer parentToBeRemoved:parentsToRemove) {
                parents.remove(parentToBeRemoved);
            }

            if(bezprizorni.contains(firstParent)){
                if(vsetko.get(id).getListParents().size()>=1){
                    vsetko.get(id).setFirstParent(vsetko.get(id).getListParents().get(0));
                    vsetko.get(id).getListParents().remove(vsetko.get(id).getListParents().get(0));
                } else{
                    //ma bezprizorneho first parenta a ziadnych inych nema - odstranime ho lebo je tiez bezprizorny
                    bezprizorni.add(id);
                    nodesToRemove.add(id);
                    somethingWasRemoved = true;
                }

            }
        }

        for (Integer idNode:nodesToRemove) {
            vsetko.remove(idNode);
        }

        if(somethingWasRemoved){
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        CSVTreeJSON jj = new CSVTreeJSON("src/main/java/sk/upjs/ics/op/TreeJSON.json");
        jj.makeCSV("src/main/java/sk/upjs/ics/op/TreeJSON.json", "src/main/java/sk/upjs/ics/op/TreeCSV.csv");



    }
}
