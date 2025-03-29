package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.example.JsonFileFlattener.flattenJsonFromFile;


public class DataContext {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private ObjectNode mainData;

    public DataContext(String mainDataFilePath) throws IOException {
        this.mainData = (ObjectNode) objectMapper.readTree(new File(mainDataFilePath)).get("data");
    }
    public JsonNode applyGlobalFilter(JsonNode filter) {
        String namespace = filter.path("field").path("namespace").asText();
        String fieldName = filter.path("field").path("name").asText();
        String comparison = filter.path("comp").asText();
        String value = filter.path("value").asText();

        if (namespace.isEmpty()) {
            return filterAllEntities(fieldName, value, comparison);
        } else {
            JsonNode namespaceNode = mainData.at(parseJsonPath(namespace));
            if (namespaceNode == null || namespaceNode.isMissingNode()) {
                System.out.println("Namespace not found: " + namespace);
                return mainData;
            }
            return filterNamespaceEntities(namespaceNode, namespace, fieldName, value, comparison);
        }
    }
    public JsonNode getEntityData(String entityPath) {
        JsonNode entityNode = mainData.at(parseJsonPath(entityPath));
        if (entityNode == null || entityNode.isMissingNode()) {
            System.out.println("Entity not found: " + entityPath);
            return null;
        }
        return entityNode;
    }

    public List<Map<String, String>> flattenEntity(String entityPath) {
        JsonNode entityNode = mainData.at(parseJsonPath(entityPath));
        List<Map<String, String>> flattenedData = new ArrayList<>();

        if (entityNode == null || entityNode.isMissingNode()) {
            System.out.println("Entity not found: " + entityPath);
            return flattenedData;
        }

        if (entityNode.isArray()) {
            for (JsonNode subNode : entityNode) {
                flattenedData.addAll(flattenJsonNode(subNode, entityPath, new HashMap<>(), new HashMap<>()));
            }
        } else if (entityNode.isObject()) {
            flattenedData.addAll(flattenJsonNode(entityNode, entityPath, new HashMap<>(), new HashMap<>()));
        }

        return flattenedData;
    }

    private List<Map<String, String>> flattenJsonNode(JsonNode node, String parentKey, Map<String, String> parentData, Map<String, String> commonData) {
        List<Map<String, String>> result = new ArrayList<>();

        if (node.isObject()) {
            Map<String, String> currentData = new HashMap<>(parentData);
            boolean hasArray = false;

            for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                String newKey = parentKey.isEmpty() ? entry.getKey() : parentKey + "." + entry.getKey();
                JsonNode value = entry.getValue();

                if (value.isArray()) {
                    hasArray = true;
                    for (JsonNode arrayElement : value) {
                        Map<String, String> clonedCommonData = new HashMap<>(currentData);  // Clone parent data
                        result.addAll(flattenJsonNode(arrayElement, newKey, clonedCommonData, clonedCommonData));
                    }
                } else if (value.isObject()) {
                    result.addAll(flattenJsonNode(value, newKey, currentData, commonData));
                } else {
                    currentData.put(newKey, value.asText());
                }
            }

            if (!hasArray) {
                result.add(new HashMap<>(currentData));  // Add the fully flattened row
            }
        } else {
            parentData.put(parentKey, node.asText());
            result.add(new HashMap<>(parentData));
        }

        return result;
    }


    private JsonNode filterNamespaceEntities(JsonNode namespaceNode, String namespace, String fieldName, String value, String comparison) {
        if (namespaceNode.isArray()) {
            ArrayNode filteredArray = objectMapper.createArrayNode();
            for (JsonNode element : namespaceNode) {
                if (matchesCondition(element, fieldName, value, comparison)) {
                    filteredArray.add(element);
                }
            }
            ((ObjectNode) mainData.at(parseJsonPath(namespace.substring(0, namespace.lastIndexOf('.'))))).set(namespace.substring(namespace.lastIndexOf('.') + 1), filteredArray);
            return mainData;
        } else if (namespaceNode.isObject()) {
            if (matchesCondition(namespaceNode, fieldName, value, comparison)) {
                return mainData;
            }
        }
        return mainData;
    }
    private JsonNode filterAllEntities(String fieldName, String value, String comparison) {
        ArrayNode filteredData = objectMapper.createArrayNode();

        for (Iterator<Map.Entry<String, JsonNode>> it = mainData.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            JsonNode entity = entry.getValue();

            if (entity.isArray()) {
                ArrayNode filteredArray = objectMapper.createArrayNode();
                for (JsonNode element : entity) {
                    if (matchesCondition(element, fieldName, value, comparison)) {
                        filteredArray.add(element);
                    }
                }
                if (filteredArray.size() > 0) {
                    filteredData.add(filteredArray);
                }
            } else if (entity.isObject()) {
                if (matchesCondition(entity, fieldName, value, comparison)) {
                    filteredData.add(entity);
                }
            }
        }

        return filteredData;
    }

    private boolean matchesCondition(JsonNode entity, String fieldName, String value, String comparison) {
        JsonNode fieldNode = entity.get(fieldName);
        if (fieldNode == null || fieldNode.isMissingNode()) {
            return false;
        }

        String fieldValue = fieldNode.asText();
        switch (comparison.toLowerCase()) {
            case "equal to":
                return fieldValue.equals(value);
            case "not equal to":
                return !fieldValue.equals(value);
            case "contains":
                return fieldValue.contains(value);
            default:
                return false;
        }
    }



    private void flattenJsonNodeRecursively(JsonNode node, String parentKey, Map<String, String> flatMap) {
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                String newKey = parentKey.isEmpty() ? entry.getKey() : parentKey + "." + entry.getKey();
                flattenJsonNodeRecursively(entry.getValue(), newKey, flatMap);
            });
        } else if (node.isArray()) {
            int index = 0;
            for (JsonNode arrayElement : node) {
                flattenJsonNodeRecursively(arrayElement, parentKey + "[" + index + "]", flatMap);
                index++;
            }
        } else {
            flatMap.put(parentKey, node.asText());
        }
    }

    private String parseJsonPath(String namespace) {
        return "/" + namespace.replaceAll("\\.", "/");
    }
    public void mergeAdditionalData(String additionalDataFilePath, String configFilePath) throws IOException {
        JsonNode additionalData = objectMapper.readTree(new File(additionalDataFilePath));
        JsonNode config = objectMapper.readTree(new File(configFilePath));

        String dataSourceName = config.path("dataSourceName").asText();
        if (dataSourceName.isEmpty()) {
            dataSourceName = "session"; // Default to "session"
        }

        ObjectNode sessionNode = (ObjectNode) mainData.get(dataSourceName);
        if (sessionNode == null || sessionNode.isMissingNode() || !(sessionNode instanceof ObjectNode)) {
            sessionNode = objectMapper.createObjectNode();
            mainData.set(dataSourceName, sessionNode);
        }

        JsonNode registeredAttributes = config.path("registeredAttributes");
        for (JsonNode attributeGroup : registeredAttributes) {
            String namespace = attributeGroup.path("namespace").asText();
            JsonNode propertyGroups = attributeGroup.path("propertygroups");

            ObjectNode targetNode = namespace.equals(dataSourceName) ? sessionNode : (ObjectNode) sessionNode.get(namespace);
            if (targetNode == null || targetNode.isMissingNode() || !(targetNode instanceof ObjectNode)) {
                targetNode = objectMapper.createObjectNode();
                sessionNode.set(namespace, targetNode);
            }

            if (propertyGroups.isArray() && !propertyGroups.isEmpty()) {
                for (JsonNode group : propertyGroups) {
                    String groupName = group.path("name").asText();
                    String groupJsonPath = group.path("jsonPath").asText();

                    try {
                        JSONArray jsonArray = JsonPath.read(additionalData.toString(), groupJsonPath);
                        ArrayNode arrayNode = objectMapper.createArrayNode();
                        jsonArray.forEach(item -> arrayNode.add(objectMapper.valueToTree(item)));
                        targetNode.set(groupName, arrayNode);
                    } catch (Exception e) {
                        System.err.println("Error reading array from path: " + groupJsonPath);
                    }
                }
            }

            for (JsonNode attribute : attributeGroup.path("attributeList")) {
                String attributeName = attribute.path("attributeName").asText();
                String jsonPath = attribute.path("jsonPath").asText();

                try {
                    Object result = JsonPath.read(additionalData.toString(), jsonPath);
                    if (result != null) {
                        targetNode.set(attributeName, objectMapper.valueToTree(result));
                    }
                } catch (Exception e) {
                    System.err.println("Error reading attribute from path: " + jsonPath);
                }
            }
        }
    }

    public JsonNode getMergedData() {
        return mainData;
    }


    public static void main(String[] args) throws IOException {
        String Path="/Users/ashokraj/Downloads/learn/spring/DataContext/src/main/java/org/example/";

        DataContext context = new DataContext(Path + "data.json");
        context.mergeAdditionalData(Path+"other_data.json", Path+"config.json");

        RuleEngine ruleEngine = new RuleEngine("src/main/java/org/example/data_types.txt");
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode mainData =  context.getMergedData();
        JsonNode rule = objectMapper.readTree(new File("src/main/java/org/example/rule.json"));

        boolean result = ruleEngine.evaluateRule(mainData, rule);
        System.out.println("Rule Evaluation Result: " + result);

        System.out.println("Merged Data: \n" + context.getMergedData().toPrettyString());

      //  JsonNode filter = objectMapper.readTree(filterJson);

        //JsonNode filteredData = context.applyGlobalFilter(filter);
      //  System.out.println("Filtered Data: " + filteredData.toPrettyString());

        String entityPath = "customer";
        JsonNode entityData = context.getEntityData(entityPath);

        if (entityData != null) {
            System.out.println("Data for entity '" + entityPath + "': " + entityData.toPrettyString());
        }

        // Flatten JSON from file
        List<Map<String, Object>> flattenedResults = flattenJsonFromFile(Path + "data.json");

        // Print results
        for (Map<String, Object> result1 : flattenedResults) {
            System.out.println(result1);
        }

        RuleEngine1 ruleEngine1 = new RuleEngine1(Path + "config1.json");
       ruleEngine1.printDataTypeMap();
    }
}


class RuleEngine {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, String> dataTypeMap = new HashMap<>();
    private final List<String> missingAttributesLog = new ArrayList<>();

    public RuleEngine(String dataTypeFilePath) throws IOException {
        loadDataTypes(dataTypeFilePath);
    }

    private void loadDataTypes(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length == 2) {
                String key = parts[0].trim();
                String dataType = parts[1].trim();
                dataTypeMap.put(key, dataType);
            }
        }
    }

    public boolean evaluateRule(JsonNode mainData, JsonNode rule) {
        missingAttributesLog.clear();

        String operator = rule.path("op").asText();
        JsonNode terms = rule.path("terms");

        Map<String, List<JsonNode>> groupedConditions = new HashMap<>();

        for (JsonNode term : terms) {
            if (term.has("op")) {
                if (evaluateRule(mainData, term)) {
                    if (operator.equalsIgnoreCase("or")) return true;
                } else {
                    if (operator.equalsIgnoreCase("and")) return false;
                }
            } else {
                String evaluationGroup = term.path("field").path("evaluation_group").asText();
                if (evaluationGroup.isEmpty()) {
                    evaluationGroup = "default";
                }
                groupedConditions.computeIfAbsent(evaluationGroup, k -> new ArrayList<>()).add(term);
            }
        }

        boolean result = operator.equalsIgnoreCase("and");

        for (Map.Entry<String, List<JsonNode>> group : groupedConditions.entrySet()) {
            boolean groupResult = evaluateGroup(mainData, group.getValue());

            if (operator.equalsIgnoreCase("and") && !groupResult) {
                return false;
            }
            if (operator.equalsIgnoreCase("or") && groupResult) {
                return true;
            }
        }

        return result;
    }

    private boolean evaluateGroup(JsonNode mainData, List<JsonNode> conditions) {
        for (JsonNode condition : conditions) {
            if (!evaluateCondition(mainData, condition)) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluateCondition(JsonNode mainData, JsonNode condition) {
        String datasource = condition.path("field").path("datasource").asText();
        String namespace = condition.path("field").path("namespace").asText();
        String fieldName = condition.path("field").path("name").asText();
        String comparison = condition.path("comp").asText();
        String value = condition.path("value").asText();

        String fullPath = "datasource:" + datasource + "," + namespace + "." + fieldName;
        JsonNode targetNode = mainData.at(parseJsonPath(namespace));

        if (targetNode.isArray()) {
            for (JsonNode element : targetNode) {
                if (evaluateSingleCondition(element, fieldName, value, comparison, fullPath)) {
                    return true;
                }
            }
            return false;
        } else {
            boolean result = evaluateSingleCondition(targetNode, fieldName, value, comparison, fullPath);
            if (!result) {
                logMissingAttribute(datasource, namespace, fieldName);
            }
            return result;
        }
    }

    private boolean evaluateSingleCondition(JsonNode targetNode, String fieldName, String value, String comparison, String fullPath) {
        JsonNode fieldNode = targetNode.get(fieldName);
        if (fieldNode == null || fieldNode.isMissingNode()) {
            return false;
        }

        String expectedType = dataTypeMap.getOrDefault(fullPath, "String");
        return compareValues(fieldNode, value, comparison, expectedType);
    }

    private boolean compareValues(JsonNode targetNode, String value, String comparison, String expectedType) {
        try {
            switch (expectedType.toLowerCase()) {
                case "integer":
                    int intValue = Integer.parseInt(value);
                    return evaluateComparison(targetNode.asInt(), intValue, comparison);
                case "double":
                    double doubleValue = Double.parseDouble(value);
                    return evaluateComparison(targetNode.asDouble(), doubleValue, comparison);
                case "datetime":
                    LocalDateTime dateValue = LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
                    LocalDateTime targetDate = LocalDateTime.parse(targetNode.asText(), DateTimeFormatter.ISO_DATE_TIME);
                    return evaluateComparison(targetDate, dateValue, comparison);
                default:
                    return evaluateComparison(targetNode.asText(), value, comparison);
            }
        } catch (Exception e) {
            return false;
        }
    }

    private <T extends Comparable<T>> boolean evaluateComparison(T targetValue, T comparisonValue, String comparison) {
        return switch (comparison.toLowerCase()) {
            case "equal to" -> targetValue.compareTo(comparisonValue) == 0;
            case "greater than" -> targetValue.compareTo(comparisonValue) > 0;
            case "less than" -> targetValue.compareTo(comparisonValue) < 0;
            case "not equal to" -> targetValue.compareTo(comparisonValue) != 0;
            case "greater than or equal to" -> targetValue.compareTo(comparisonValue) >= 0;
            case "less than or equal to" -> targetValue.compareTo(comparisonValue) <= 0;
            default -> false;
        };
    }

    private void logMissingAttribute(String datasource, String namespace, String fieldName) {
        missingAttributesLog.add("Missing attribute: " + datasource + "," + namespace + "." + fieldName);
    }

    public void printMissingAttributesLog() {
        if (!missingAttributesLog.isEmpty()) {
            System.out.println("\nMissing Attributes Log:");
            missingAttributesLog.forEach(System.out::println);
        }
    }

    private String parseJsonPath(String namespace) {
        return "/" + namespace.replaceAll("\\.", "/");
    }
}
class JsonFileFlattener {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<Map<String, Object>> flattenJsonFromFile(String filePath) throws IOException {
        // Read JSON from file
        Map<String, Object> jsonData = objectMapper.readValue(
                new File(filePath),
                new TypeReference<Map<String, Object>>() {
                }
        );

        return flattenJson(jsonData);
    }

    public static List<Map<String, Object>> flattenJson(Map<String, Object> data) {
        List<Map<String, Object>> results = new ArrayList<>();
        results.add(new HashMap<>());

        flattenRecursive(data, "", results);

        return results;
    }

    private static void flattenRecursive(
            Map<String, Object> obj,
            String prefix,
            List<Map<String, Object>> results
    ) {
        for (Map.Entry<String, Object> entry : obj.entrySet()) {
            String key = prefix.isEmpty()
                    ? entry.getKey()
                    : prefix + "." + entry.getKey();
            Object value = entry.getValue();

            // Handle nested dictionaries
            if (value instanceof Map) {
                flattenRecursive((Map<String, Object>) value, key, results);
            }
            // Handle lists
            else if (value instanceof List) {
                handleListFlattening((List<?>) value, key, results);
            }
            // Handle primitive values
            else {
                for (Map<String, Object> result : results) {
                    result.put(key, value);
                }
            }
        }
    }

    private static void handleListFlattening(
            List<?> list,
            String key,
            List<Map<String, Object>> results
    ) {
        List<Map<String, Object>> newResults = new ArrayList<>();

        for (Map<String, Object> baseResult : results) {
            for (Object item : list) {
                // If list item is a map, flatten it
                if (item instanceof Map) {
                    Map<String, Object> itemMap = (Map<String, Object>) item;
                    Map<String, Object> newResult = new HashMap<>(baseResult);

                    // Create a new set of results for this item
                    List<Map<String, Object>> itemResults = new ArrayList<>();
                    itemResults.add(newResult);

                    flattenRecursive(itemMap, key, itemResults);

                    newResults.addAll(itemResults);
                }
                // If list item is a primitive
                else {
                    Map<String, Object> newResult = new HashMap<>(baseResult);
                    newResult.put(key, item);
                    newResults.add(newResult);
                }
            }
        }

        // Replace original results with new flattened results
        results.clear();
        results.addAll(newResults);
    }
}

class RuleEngine1 {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, String> dataTypeMap = new HashMap<>();
    private final List<String> missingAttributesLog = new ArrayList<>();

    public RuleEngine1(String dataTypeFilePath) throws IOException {
        loadDataTypes(dataTypeFilePath);
    }

    public void loadDataTypes(String filePath) throws IOException {
        if (filePath.endsWith(".json")) {
            JsonNode rootNode = objectMapper.readTree(new File(filePath));
            parseJsonNode(rootNode);
        } else {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String dataType = parts[1].trim();
                    dataTypeMap.put(key, dataType);
                }
            }
        }
    }

    private void parseJsonNode(JsonNode rootNode) {
        if (rootNode.isArray()) {
            for (JsonNode node : rootNode) {
                parseJsonNode(node);
            }
        } else if (rootNode.isObject()) {
            String datasource = rootNode.path("dataSourceName").asText();
            JsonNode registeredAttributes = rootNode.path("registeredAttributes");

            for (JsonNode attributeGroup : registeredAttributes) {
                String namespace = attributeGroup.path("namespace").asText();
                processAttributes(datasource, namespace, attributeGroup);
            }
        }
    }

    private void processAttributes(String datasource, String namespace, JsonNode attributeGroup) {
        JsonNode propertyGroups = attributeGroup.path("propertygroups");
        JsonNode attributeList = attributeGroup.path("attributeList");

        if (propertyGroups.isArray() && !propertyGroups.isEmpty()) {
            for (JsonNode propertyGroup : propertyGroups) {
                String propertyGroupName = propertyGroup.path("name").asText();
                JsonNode attributeListInGroup = propertyGroup.path("attributelist");

                if (attributeListInGroup.isArray()) {
                    for (JsonNode attribute : attributeListInGroup) {
                        addDataTypeMapping(datasource, namespace, propertyGroupName, attribute);
                    }
                }
            }
        }

        if (attributeList.isArray() && !attributeList.isEmpty()) {
            for (JsonNode attribute : attributeList) {
                addDataTypeMapping(datasource, namespace, null, attribute);
            }
        }
    }

    private void addDataTypeMapping(String datasource, String namespace, String propertyGroupName, JsonNode attribute) {
        String attributeName = attribute.path("attributeName").asText();
        String dataType = attribute.path("dataType").asText();
        String jsonPath = attribute.path("JsonPath").asText();

        String fullPath = "datasource:" + datasource + "," + namespace;
        if (propertyGroupName != null) {
            fullPath += "." + propertyGroupName;
        }
        fullPath += "." + attributeName;

        String mappingValue = dataType + ",jsonPath:" + jsonPath;
        dataTypeMap.put(fullPath, mappingValue);
    }

    public void printDataTypeMap() {
        dataTypeMap.forEach((key, value) -> System.out.println(key + " : " + value));
    }

    public Map<String, String> getDataTypeMap() {
        return dataTypeMap;
    }
}


 class GraphQLQueryGenerator {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Map<String, List<String>>> queryPaths = new HashMap<>();
    private final Map<String, String> rootQueries = new HashMap<>();

    public GraphQLQueryGenerator(String schemaFilePath) throws IOException {
        loadSchema(schemaFilePath);
    }

    private void loadSchema(String filePath) throws IOException {
        JsonNode rootNode = objectMapper.readTree(new File(filePath));
        JsonNode glossary = rootNode.path("glossary");

        if (glossary.isArray()) {
            for (JsonNode dataSourceNode : glossary) {
                String dataSource = dataSourceNode.path("dataSourceName").asText();
                String rootQuery = dataSourceNode.path("rootQuery").asText();
                rootQueries.put(dataSource, rootQuery);  // Store root query

                JsonNode registeredAttributes = dataSourceNode.path("registeredAttributes");

                for (JsonNode attributeGroup : registeredAttributes) {
                    String namespace = attributeGroup.path("namespace").asText();
                    JsonNode attributeList = attributeGroup.path("attributeList");

                    if (attributeList.isArray()) {
                        for (JsonNode attribute : attributeList) {
                            String attributeName = attribute.path("attributeName").asText();
                            addQueryPath(dataSource, namespace, attributeName);
                        }
                    }

                    JsonNode propertyGroups = attributeGroup.path("propertygroups");
                    if (propertyGroups.isArray()) {
                        for (JsonNode propertyGroup : propertyGroups) {
                            String propertyGroupName = propertyGroup.path("name").asText();
                            JsonNode propertyAttributes = propertyGroup.path("attributelist");

                            if (propertyAttributes.isArray()) {
                                for (JsonNode attribute : propertyAttributes) {
                                    String attributeName = attribute.path("attributeName").asText();
                                    addQueryPath(dataSource, namespace + "." + propertyGroupName, attributeName);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void addQueryPath(String dataSource, String namespace, String attributeName) {
        queryPaths.computeIfAbsent(dataSource, k -> new HashMap<>())
                .computeIfAbsent(namespace, k -> new ArrayList<>())
                .add(attributeName);
    }

    public String generateQuery(String dataSource, List<String> entityAttributes) {
        if (!queryPaths.containsKey(dataSource)) {
            return "Data source not found.";
        }

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("query {");

        String rootQuery = rootQueries.getOrDefault(dataSource, "");
        if (!rootQuery.isEmpty()) {
            queryBuilder.append("\n  ").append(rootQuery).append(" {");
        }

        Map<String, List<String>> entityMap = queryPaths.get(dataSource);
        Map<String, List<String>> selectedEntities = new HashMap<>();

        for (String attribute : entityAttributes) {
            String[] parts = attribute.split("\\.");
            String namespace = parts.length > 1 ? parts[0] + "." + parts[1] : parts[0];
            String attributeName = parts.length > 2 ? parts[2] : parts[1];

            selectedEntities.computeIfAbsent(namespace, k -> new ArrayList<>()).add(attributeName);
        }

        for (Map.Entry<String, List<String>> entry : selectedEntities.entrySet()) {
            String namespace = entry.getKey();
            List<String> attributes = entry.getValue();

            queryBuilder.append("\n    ").append(namespace).append(" {");
            for (String attribute : attributes) {
                queryBuilder.append(" ").append(attribute);
            }
            queryBuilder.append(" }");
        }

        if (!rootQuery.isEmpty()) {
            queryBuilder.append("\n  }");
        }
        queryBuilder.append("\n}");

        return queryBuilder.toString();
    }

    public static void main(String[] args) {
        try {
            GraphQLQueryGenerator generator = new GraphQLQueryGenerator("src/main/resources/schema.json");

            // Example: Generating query
            List<String> attributes = Arrays.asList(
                    "CASGraphQL.customer.salary",
                    "CASGraphQL.customer.age",
                    "CASGraphQL.bloomAccounts.BloomAccount.bloom_account_nbr"
            );

            String query = generator.generateQuery("CASGraphQL", attributes);
            System.out.println("Generated GraphQL Query:\n" + query);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

