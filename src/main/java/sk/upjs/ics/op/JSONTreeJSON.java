package sk.upjs.ics.op;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONTreeJSON {

    private String filePath;

    public JSONTreeJSON(String file) {
        this.filePath = file;
    }

    public String makeTreeJSON(String inputFile){
        try {
            String inputJson = new String(Files.readAllBytes(Paths.get(inputFile)));


            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode rootNode = mapper.readTree(inputJson);

                if (rootNode.isObject()) {
                    ObjectNode resultNode = (ObjectNode) rootNode;
                    ArrayNode nodesArray = (ArrayNode) resultNode.get("nodes");
                    ArrayNode linksArray = (ArrayNode) resultNode.get("links");

                    for (JsonNode node : nodesArray) {
                        boolean hasAnyParent = false;
                        if (node.isObject()) {
                            ObjectNode objectNode = (ObjectNode) node;
                            int nodeId = objectNode.get("id").asInt();

                            // Find the first occurrence in links
                            for (JsonNode link : linksArray) {
                                if (link.isObject()) {
                                    ObjectNode linkObject = (ObjectNode) link;
                                    int source = linkObject.get("source").asInt();
                                    int target = linkObject.get("target").asInt();

                                    if (source == nodeId) {
                                        objectNode.put("first_parent", target);
                                        System.out.println("FIRST PARENT: " + target);
                                        hasAnyParent=true;
                                        break;
                                    }
                                }
                            }

                            // Find other occurrences in links
                            ArrayNode listParents = mapper.createArrayNode();
                            for (JsonNode link : linksArray) {
                                if (link.isObject()) {
                                    ObjectNode linkObject = (ObjectNode) link;
                                    int source = linkObject.get("source").asInt();
                                    int target = linkObject.get("target").asInt();

                                    if (source == nodeId && target != objectNode.get("first_parent").asInt()) {
                                        listParents.add(target);
                                    }
                                }
                            }

                            objectNode.set("list_parents", listParents);
                            if(!hasAnyParent){
                                objectNode.putNull("first_parent");
                                System.out.println("FIRST PARENT: " + "null");
                            }

                            // Add all previous parameters of the node
                            objectNode.put("id", nodeId);
                            objectNode.put("topLabel", objectNode.get("topLabel").asText());
                            objectNode.put("group", objectNode.get("group").asText());
                            objectNode.set("detail", objectNode.get("detail"));
                        }
                    }

                    String outputJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultNode);
                    return outputJson;
                    //System.out.println(outputJson);
                } else {
                    System.err.println("Invalid JSON structure.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }


    public static void main(String[] args) {
        JSONTreeJSON jj = new JSONTreeJSON("src/main/java/sk/upjs/ics/op/graphD3.json");
        String result = jj.makeTreeJSON("src/main/java/sk/upjs/ics/op/graphD3.json");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/sk/upjs/ics/op/TreeJSON.json"))) {
            writer.write(result);
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }

    }

}
