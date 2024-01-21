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

public class CSVTreeJSON {

    private String source;

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

                // Create CSV file
                try (FileWriter csvWriter = new FileWriter(destination)) {
                    // Write header
                        csvWriter.append("id;topLabel;group;detail;parentId;list_parents\n");

                    for (JsonNode node : nodesArray) {
                        if (node.isObject()) {
                            ObjectNode objectNode = (ObjectNode) node;

                            // Extract values
                            int id = objectNode.get("id").asInt();
                            String topLabel = objectNode.get("topLabel").asText();
                            String group = objectNode.get("group").asText();
                            String detail = objectNode.get("detail").toString();
                            int firstParent = objectNode.has("first_parent") ? objectNode.get("first_parent").asInt() : -1;
                            System.out.println("FIRST PARENT: " + firstParent);
                            String listParents = objectNode.has("list_parents") ? objectNode.get("list_parents").toString() : "[]";

                            // Write to CSV
                            csvWriter.append(String.valueOf(id)).append(';');
                            csvWriter.append("\"").append(topLabel).append("\"").append(';');
                            csvWriter.append("\"").append(group).append("\"").append(';');
                            csvWriter.append("\"").append(detail).append("\"").append(';');
                            csvWriter.append(String.valueOf(firstParent)).append(';');
                            System.out.println("xxx" + String.valueOf(firstParent));
                            csvWriter.append("\"").append(listParents).append("\"").append("\n");

                        }
                    }

                    System.out.println("CSV file created successfully!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("Invalid JSON structure.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        CSVTreeJSON jj = new CSVTreeJSON("src/main/java/sk/upjs/ics/op/TreeJSON.json");
        jj.makeCSV("src/main/java/sk/upjs/ics/op/TreeJSON.json", "src/main/java/sk/upjs/ics/op/TreeCSV.csv");



    }
}
