package com.dudu.oauth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.util.*;

public class PermissionManager {
    private final Logger logger = LogManager.getLogger(PermissionManager.class);

    // Path Node is prefix by method
    private static final String URL_TREE_ROOT_VALUE = "dudu";
    private PathNode urlPublicTree;
    private Map<String, PathNode> urlTreesByScope;

    public PermissionManager(String file) throws Exception {
        try (var in = new FileInputStream(file)) {
            var config = new JSONObject(new JSONTokener(in));

            // loading endpoints
            var apiEndpointMap = new HashMap<Long, ApiEndpoint>();
            var apiEndpointsJson = config.getJSONArray("ApiEndpoints");
            for (int i = 0; i < apiEndpointsJson.length(); i++) {
                var apiEndpointJson = apiEndpointsJson.getJSONObject(i);
                var apiEndpoint = new ApiEndpoint();
                apiEndpoint.setApiEndpointId(apiEndpointJson.getLong("ApiEndpointId"));
                apiEndpoint.setMethod(apiEndpointJson.getString("Method"));
                apiEndpoint.setEndpoint(apiEndpointJson.getString("Endpoint"));
                apiEndpoint.setPublic(apiEndpointJson.has("IsPublic") && apiEndpointJson.getBoolean("IsPublic"));

                apiEndpointMap.put(apiEndpoint.getApiEndpointId(), apiEndpoint);
            }

            // scopes
            var scopeList = new ArrayList<Scope>();
            var scopes = config.getJSONArray("Scopes");
            for (int i = 0; i < scopes.length(); i++) {
                var scopeJson = scopes.getJSONObject(i);
                var scope = new Scope();
                scope.setScopeName(scopeJson.getString("ScopeName"));

                if (scopeJson.has("ApiEndpointIds")) {
                    var apiEndpointIdsJson = scopeJson.getJSONArray("ApiEndpointIds");

                    scope.setEndpoints(new HashSet<>());
                    for (int j = 0; j < apiEndpointIdsJson.length(); j++) {
                        var apiEndpointId = apiEndpointIdsJson.getLong(j);
                        scope.getEndpoints().add(apiEndpointMap.get(apiEndpointId));
                    }
                }

                scopeList.add(scope);
            }

            urlTreesByScope = new HashMap<>();
            for (var scope : scopeList) {
                PathNode urlTree = new PathNode(URL_TREE_ROOT_VALUE);
                urlTreesByScope.put(scope.getScopeName(), urlTree);

                for (var apiEndpoint : scope.getEndpoints())
                    addApiEnpoint(urlTree, apiEndpoint);
            }

            urlPublicTree = new PathNode(URL_TREE_ROOT_VALUE);
            for (var apiEndpoint : apiEndpointMap.values()) {
                if (apiEndpoint.isPublic())
                    addApiEnpoint(urlPublicTree, apiEndpoint);
            }

            logger.info("permission configured successfully");
        }
    }

    void addApiEnpoint(PathNode urlTree, ApiEndpoint apiEndpoint) {
        List<String> nodeValueList = breakIntoNodes(apiEndpoint);
        nodeValueList.remove(0);

        var curNode = urlTree;
        for (var nodeValue : nodeValueList) {

            if (curNode.getChildren() == null)
                curNode.setChildren(new HashMap<>());

            PathNode nextNode = curNode.getChildren().get(nodeValue);
            if (nextNode == null) {
                nextNode = new PathNode(nodeValue);
                curNode.getChildren().put(nodeValue, nextNode);
            }

            curNode = nextNode;
        }

        // endpoint node
        curNode.setApiEndpoint(apiEndpoint);
    }

    /**
     *
     * @param scope
     * @param endpoint
     * @param method
     * @return strict mode, not found will return false
     */
    public boolean isPermitted(String scope, String endpoint, String method) {
        var nodeValueList =  breakIntoNodes(endpoint, method);

        // check public tree
        if (match(urlPublicTree, nodeValueList, 0) != null)
            return true;

        // check scope trees
        var urlTree = urlTreesByScope.get(scope);
        if (urlTree == null)
            return false;

        var apiEndpoint = match(urlTree, nodeValueList, 0);
        return apiEndpoint != null;
    }

    /**
     *
     * @param node
     * @param nodeValueList
     * @param pos
     * @return null on not found
     */
    ApiEndpoint match(PathNode node, List<String> nodeValueList, int pos) {
        String nodeValue = nodeValueList.get(pos);
        if (!nodeValue.equals(node.getValue()) && !nodeValue.equals("*"))
            return null;

        if (nodeValueList.size() == pos-1)
            return node.getApiEndpoint(); // found it

        if (node.getChildren() == null)
            return null; // not more child nodes to search

        // continue searching
        for (PathNode nextNode : node.getChildren().values()) {
            var apiEndpoint = match(nextNode, nodeValueList, pos+1);
            if (apiEndpoint != null)
                return apiEndpoint;
        }

        // search exhausted.
        return null;
    }

    private List<String> breakIntoNodes(ApiEndpoint apiEndpoint) {
        return breakIntoNodes(apiEndpoint.getEndpoint(), apiEndpoint.getMethod());
    }

    private List<String> breakIntoNodes(String endpoint, String method) {
        var nodeValueList = new ArrayList<String>();
        nodeValueList.add(URL_TREE_ROOT_VALUE);
        nodeValueList.add(method);
        for (String nodeValue : endpoint.split("/"))
            if (!nodeValue.equals(""))
                nodeValueList.add(nodeValue);

        return nodeValueList;
    }
}
