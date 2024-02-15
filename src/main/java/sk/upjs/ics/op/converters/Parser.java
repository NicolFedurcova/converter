package sk.upjs.ics.op.converters;

import org.json.*;
import sk.upjs.ics.op.entities.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Parser {
    //
    private String inputString;
    private String filePath;
    private Set<VertexObject> volumesPom = new HashSet<>();

    public Parser() {
    }

    public Parser(String filePath) {
        this.filePath = filePath;
    }

    public String parse() {
        try {
            this.inputString = new String(Files.readAllBytes(Paths.get(this.filePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject jsonObject = new JSONObject(inputString);
        List<String> simpleObjects = new ArrayList<>(Arrays.asList("endpoints", "services", "configMaps", "networkPolicies"));
        List<String> complexObjects = new ArrayList<>(Arrays.asList("deployments", "statefulSets", "replicaSets"));

        Map<String, List<String>> simpleObjectLabels = getLabels(jsonObject, simpleObjects);
        Map<String, List<String>> complexObjectLabels = getLabels(jsonObject, complexObjects);

        Set<VertexObject> vertices = new HashSet<>();
        List<List<VertexObject>> edges = new ArrayList<>();


        for (String simpleObjectName : simpleObjects) {
            JSONArray simpleObjectJSONList = jsonObject.getJSONArray(simpleObjectName);
            List<String> simpleLabels = simpleObjectLabels.get(simpleObjectName);
            for (int i = 0; i < simpleObjectJSONList.length(); i++) {
                GraphJSON g = convertSimpleObject(simpleObjectJSONList.getJSONObject(i), simpleLabels);
                vertices.addAll(g.getVertices());
                edges.addAll(g.getEdges());
            }
        }

        for (String complexObjectName : complexObjects) {
            JSONArray complexObjectJSONList = jsonObject.getJSONArray(complexObjectName);
            List<String> complexLabels = complexObjectLabels.get(complexObjectName);
            for (int j = 0; j < complexObjectJSONList.length(); j++) {
                GraphJSON gg = convertComplexObject(complexObjectJSONList.getJSONObject(j), complexLabels);
                vertices.addAll(gg.getVertices());
                edges.addAll(gg.getEdges());
            }
        }

        JSONArray podsJSONList = jsonObject.getJSONArray("pods");
        Map<String, List<String>>  labelsPods = getLabels(jsonObject, new ArrayList<>(Arrays.asList("pods")));
        List<String> podsLabels = labelsPods.get("pods");
        for (int j = 0; j < podsJSONList.length(); j++) {
            GraphJSON gg = convertPod(podsJSONList.getJSONObject(j), podsLabels);
            vertices.addAll(gg.getVertices());
            edges.addAll(gg.getEdges());
        }

        JSONArray serviceAccountsJSONList = jsonObject.getJSONArray("serviceAccounts");
        Map<String, List<String>>  labelsServiceAccounts = getLabels(jsonObject, new ArrayList<>(Arrays.asList("serviceAccounts")));
        List<String> serviceAccountsLabels = labelsServiceAccounts.get("serviceAccounts");
        for (int k = 0; k < serviceAccountsJSONList.length(); k++) {
            GraphJSON gg = convertServiceAccount(serviceAccountsJSONList.getJSONObject(k), serviceAccountsLabels);
            vertices.addAll(gg.getVertices());
            edges.addAll(gg.getEdges());
        }


        GraphJSON finalGraphJSON = new GraphJSON();
        finalGraphJSON.setVertices(vertices);
        finalGraphJSON.setEdges(edges);
        return finalGraphJSON.toString();

    }

    private String stripLabel(String label) {
        return label.substring(1, label.length() - 1);
    }

    private String stripKind(String kind) {
        return kind.substring(0, kind.length() - 1);
    }

    private String stripDetail(String detail) {
        return detail.substring(1, detail.length() - 1);
    }



    private Set<VertexObject> convertContainer(JSONArray containers) {
        Set<VertexObject> vertices = new HashSet<>();
        //JSONArray containers = object.getJSONObject("spec").getJSONObject("template").getJSONObject("spec").getJSONArray("containers");
        for (int i = 0; i < containers.length(); i++) {
            VertexObject vo = new VertexObject();
            Title title = new Title();
            title.setKind("container");
            title.setName(containers.getJSONObject(i).getString("name"));
            containers.getJSONObject(i).remove("name");
            title.setLabels(null);
            Detail detail = new Detail();
            detail.setInfo(stripDetail(containers.getJSONObject(i).toString()));
            vo.setDetails(detail);
            vo.setTitle(title);
            vertices.add(vo);
        }
        return vertices;

    }

    private GraphJSON convertVolume(JSONArray volumes) {
        GraphJSON g = new GraphJSON();
        Set<VertexObject> vertices = new HashSet<>();
        List<List<VertexObject>> edges = new ArrayList<>();
        try {
            //JSONArray volumes = object.getJSONObject("spec").getJSONObject("template").getJSONObject("spec").getJSONArray("volumes");
            for (int i = 0; i < volumes.length(); i++) {
                VertexObject vo = new VertexObject();
                Title title = new Title();
                title.setKind("volume");
                title.setName(volumes.getJSONObject(i).getString("name"));
                volumes.getJSONObject(i).remove("name");
                title.setLabels(null);

                try {
                    VertexObject secret = convertSecret(volumes.getJSONObject(i).getJSONObject("secret"));
                    vertices.add(secret);
                    edges.add(new ArrayList<>(Arrays.asList(vo, secret)));
                    volumes.getJSONObject(i).remove("secret");
                } catch (JSONException e) {
                }
                try {
                    VertexObject configMap = convertConfigMap(volumes.getJSONObject(i).getJSONObject("configMap"));
                    vertices.add(configMap);
                    edges.add(new ArrayList<>(Arrays.asList(vo, configMap)));
                    volumes.getJSONObject(i).remove("configMap");
                } catch (JSONException ee) {
                }
                try {
                    JSONArray sources = volumes.getJSONObject(i).getJSONObject("projected").getJSONArray("sources");
                    for (int z = 0; z < sources.length();z++) {
                        try {
                            VertexObject configMap = convertConfigMap(sources.getJSONObject(z).getJSONObject("configMap"));
                            vertices.add(configMap);
                            edges.add(new ArrayList<>(Arrays.asList(vo, configMap)));
                            sources.getJSONObject(z).remove("configMap");
                            volumes.getJSONObject(i).getJSONObject("projected").getJSONArray("sources").getJSONObject(z).remove("configMap");


                        } catch (JSONException eeee){

                        }
                    }
                } catch (JSONException eee) {
                }

                Detail detail = new Detail();
                detail.setInfo(stripDetail(volumes.getJSONObject(i).toString()));
                vo.setDetails(detail);
                vo.setTitle(title);
                vertices.add(vo);
                volumesPom.add(vo);

            }
        } catch (JSONException e) {

        }
        g.setEdges(edges);
        g.setVertices(vertices);
        return g;
    }

    private VertexObject convertConfigMap(JSONObject object) {
        VertexObject vo = new VertexObject();
        Title title = new Title();
        title.setKind("configMap");
        title.setName(object.getString("name"));
        object.remove("name");
        title.setLabels(null);
        Detail detail = new Detail();
        detail.setInfo(stripDetail(object.toString()));
        vo.setDetails(detail);
        vo.setTitle(title);
        return vo;
    }

    private VertexObject convertSecret(JSONObject object) {
        VertexObject vo = new VertexObject();
        Title title = new Title();
        title.setKind("secret");
        try {
            title.setName(object.getString("name"));
            object.remove("name");
        } catch (JSONException e) {
        }
        try {
            title.setName(object.getString("secretName"));
            object.remove("secretName");
        } catch (JSONException e) {
        }
        title.setLabels(null);
        Detail detail = new Detail();
        detail.setInfo(stripDetail(object.toString()));
        vo.setDetails(detail);
        vo.setTitle(title);
        return vo;
    }


    private GraphJSON convertComplexObject(JSONObject object, List<String> listLabelNames) {
        GraphJSON graphJSON = new GraphJSON();
        Set<VertexObject> vertices = new HashSet<>();
        List<List<VertexObject>> edges = new ArrayList<>();

        VertexObject vo = new VertexObject();
        List<Label> labelsList = new ArrayList<>();

        for (String labelName : listLabelNames) {
            Label label = new Label();
            label.setName(labelName);
            try {
                label.setValue(object.getJSONObject("metadata").getJSONObject("labels").getString(labelName));
                labelsList.add(label);
            } catch (JSONException e) {
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

        VertexObject namespace = createNamespace(object.getJSONObject("metadata").getString("namespace"));
        object.getJSONObject("metadata").remove("namespace");

        try {
            Set<VertexObject> containers = convertContainer(object.getJSONObject("spec").getJSONObject("template").getJSONObject("spec").getJSONArray("containers"));
            vertices.addAll(containers);
            for (VertexObject container : containers) {
                edges.add(new ArrayList<>(Arrays.asList(vo, container)));
            }
            object.getJSONObject("spec").getJSONObject("template").getJSONObject("spec").remove("containers");
        } catch (JSONException e) {

        }

        try {
            GraphJSON graphVolumes = convertVolume(object.getJSONObject("spec").getJSONObject("template").getJSONObject("spec").getJSONArray("volumes"));
            Set<VertexObject> volumesSecretsConfigMaps = graphVolumes.getVertices();
            vertices.addAll(volumesSecretsConfigMaps);
            for (VertexObject volume : volumesPom) {
                edges.add(new ArrayList<>(Arrays.asList(vo, volume)));
            }
            edges.addAll(graphVolumes.getEdges());
            volumesPom = new HashSet<>();
            object.getJSONObject("spec").getJSONObject("template").getJSONObject("spec").remove("volumes");
        } catch (JSONException e) {
        }


        Detail detail = new Detail();
        detail.setInfo(stripDetail(object.toString()));
        vo.setDetails(detail);
        vo.setTitle(title);


        vertices.add(vo);
        vertices.add(namespace);
        edges.add(new ArrayList<>(Arrays.asList(namespace, vo)));
        graphJSON.setEdges(edges);
        graphJSON.setVertices(vertices);
        return graphJSON;
    }

    private GraphJSON convertSimpleObject(JSONObject object, List<String> listLabelNames) {
        GraphJSON graphJSON = new GraphJSON();
        Set<VertexObject> vertices = new HashSet<>();
        List<List<VertexObject>> edges = new ArrayList<>();

        VertexObject vo = new VertexObject();
        List<Label> labelsList = new ArrayList<>();
        for (String labelName : listLabelNames) {
            Label label = new Label();
            label.setName(labelName);
            try {
                label.setValue(object.getJSONObject("metadata").getJSONObject("labels").getString(labelName));
                labelsList.add(label);
            } catch (JSONException e) {
            }

        }

        Title title = new Title();
        title.setKind(object.getString("kind"));
        object.remove("kind");
        title.setName(object.getJSONObject("metadata").getString("name"));
        object.getJSONObject("metadata").remove("name");
        title.setLabels(labelsList);
        object.getJSONObject("metadata").remove("labels");
        VertexObject namespace = createNamespace(object.getJSONObject("metadata").getString("namespace"));
        object.getJSONObject("metadata").remove("namespace");

        Detail detail = new Detail();
        detail.setInfo(stripDetail(object.toString()));
        vo.setDetails(detail);
        vo.setTitle(title);

        vertices.add(vo);
        vertices.add(namespace);
        edges.add(new ArrayList<>(Arrays.asList(namespace, vo)));
        graphJSON.setEdges(edges);
        graphJSON.setVertices(vertices);
        return graphJSON;
    }

    public GraphJSON  convertPod(JSONObject object, List<String> listLabelNames) {

        GraphJSON graphJSON = new GraphJSON();
        Set<VertexObject> vertices = new HashSet<>();
        List<List<VertexObject>> edges = new ArrayList<>();

        VertexObject vo = new VertexObject();
        List<Label> labelsList = new ArrayList<>();

        for (String labelName : listLabelNames) {
            Label label = new Label();
            label.setName(labelName);
            try {
                label.setValue(object.getJSONObject("metadata").getJSONObject("labels").getString(labelName));
                labelsList.add(label);
            } catch (JSONException e) {
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
        VertexObject namespace = createNamespace(object.getJSONObject("metadata").getString("namespace"));
        object.getJSONObject("metadata").remove("namespace");

        try {
            Set<VertexObject> containers = convertContainer(object.getJSONObject("spec").getJSONArray("containers"));
            vertices.addAll(containers);
            for (VertexObject container : containers) {
                edges.add(new ArrayList<>(Arrays.asList(vo, container)));
            }
            object.getJSONObject("spec").remove("containers");
        } catch (JSONException e) {
        }

        try {
            GraphJSON graphVolumes = convertVolume(object.getJSONObject("spec").getJSONArray("volumes"));
            Set<VertexObject> volumesSecretsConfigMaps = graphVolumes.getVertices();
            vertices.addAll(volumesSecretsConfigMaps);
            for (VertexObject volume : volumesPom) {
                edges.add(new ArrayList<>(Arrays.asList(vo, volume)));
            }
            edges.addAll(graphVolumes.getEdges());
            volumesPom = new HashSet<>();
            object.getJSONObject("spec").remove("volumes");
        } catch (JSONException e) {

        }


        Detail detail = new Detail();
        detail.setInfo(stripDetail(object.toString()));
        vo.setDetails(detail);
        vo.setTitle(title);


        vertices.add(vo);
        vertices.add(namespace);
        edges.add(new ArrayList<>(Arrays.asList(namespace, vo)));
        graphJSON.setEdges(edges);
        graphJSON.setVertices(vertices);
        return graphJSON;

    }

    public GraphJSON convertServiceAccount(JSONObject object, List<String> listLabelNames) {

        GraphJSON graphJSON = new GraphJSON();
        Set<VertexObject> vertices = new HashSet<>();
        List<List<VertexObject>> edges = new ArrayList<>();

        VertexObject vo = new VertexObject();
        List<Label> labelsList = new ArrayList<>();

        for (String labelName : listLabelNames) {
            Label label = new Label();
            label.setName(labelName);
            try {
                label.setValue(object.getJSONObject("metadata").getJSONObject("labels").getString(labelName));
                labelsList.add(label);
            } catch (JSONException e) {
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
        VertexObject namespace = createNamespace(object.getJSONObject("metadata").getString("namespace"));
        object.getJSONObject("metadata").remove("namespace");

        try {
            JSONArray secrets = object.getJSONArray("secrets");
            for (int i = 0; i <secrets.length() ; i++) {
                VertexObject secret = convertSecret(secrets.getJSONObject(i));
                vertices.add(secret);
                edges.add(new ArrayList<>(Arrays.asList(vo, secret)));
            }
            object.remove("secrets");
        } catch (JSONException e) {

        }


        Detail detail = new Detail();
        detail.setInfo(stripDetail(object.toString()));
        vo.setDetails(detail);
        vo.setTitle(title);


        vertices.add(vo);
        vertices.add(namespace);
        edges.add(new ArrayList<>(Arrays.asList(namespace, vo)));
        graphJSON.setEdges(edges);
        graphJSON.setVertices(vertices);
        return graphJSON;

    }



    public VertexObject createNamespace(String name) {
        VertexObject vo = new VertexObject();
        Title t = new Title();
        t.setLabels(null);
        t.setKind("namespace");
        t.setName(name);
        Detail d = new Detail();
        d.setInfo("");
        vo.setTitle(t);
        vo.setDetails(d);
        return vo;
    }


    public static Map<String, List<String>> getLabels(JSONObject topLevelObject, List<String> objectKinds) {
        Map<String, List<String>> objectLabelNames = new HashMap<>();
        for (String objectKind : objectKinds) {
            Set labels = new HashSet<>();
            JSONArray objects = topLevelObject.getJSONArray(objectKind);

            for (int i = 0; i < objects.length(); i++) {
                try {
                    for (String key : objects.getJSONObject(i).getJSONObject("metadata").getJSONObject("labels").keySet()) {
                        labels.add(key);
                    }
                } catch (JSONException e1) {

                }
            }
            objectLabelNames.put(objectKind, new ArrayList<String>(labels));
        }
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
