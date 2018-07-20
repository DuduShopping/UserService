package com.dudu.oauth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.util.*;

public class PermissionManager {
    private final Logger logger = LogManager.getLogger(PermissionManager.class);

    private PathNode urlGetTree;
    private PathNode urlPostTree;
    private PathNode urlPutTree;
    private PathNode urlDeleteTree;

    public PermissionManager(String file) throws Exception {
        try (var in = new FileInputStream(file)) {
            var config = new JSONObject(new JSONTokener(in));

            // loading endpoints
            List<ApiEndpoint> apiEndpoints = new ArrayList<>();
            var apiEndpointsJson = config.getJSONArray("ApiEndpoints");
            for (int i = 0; i < apiEndpointsJson.length(); i++) {
                var apiEndpointJson = apiEndpointsJson.getJSONObject(i);
                var apiEndpoint = new ApiEndpoint();
                apiEndpoint.setApiEndpointId(apiEndpointJson.getLong("ApiEndpointId"));
                apiEndpoint.setMethod(apiEndpointJson.getString("Method"));
                apiEndpoint.setEndpoint(apiEndpointJson.getString("Endpoint"));
                apiEndpoint.setPrivate(apiEndpointJson.has("IsPrivate") && apiEndpointJson.getBoolean("IsPrivate"));
                apiEndpoint.setPublic(apiEndpointJson.has("IsPublic") && apiEndpointJson.getBoolean("IsPublic"));

                apiEndpoints.add(apiEndpoint);
            }

            // scopes
            Map<Long, Set<String>> apiEndpointScopes = new HashMap<>();
            var scopes = config.getJSONArray("Scopes");
            for (int i = 0; i < scopes.length(); i++) {
                var scopeJson = scopes.getJSONObject(i);
                var scope = new Scope();

                var scopeName = scopeJson.getString("ScopeName");
                if (scopeJson.has("ApiEndpointIds")) {
                    var apiEndpointIdsJson = scopeJson.getJSONArray("ApiEndpointIds");

                    for (int j = 0; j < apiEndpointIdsJson.length(); j++) {
                        var apiEndpointId = apiEndpointIdsJson.getLong(j);

                        apiEndpointScopes.computeIfAbsent(apiEndpointId, id -> new HashSet<>());
                        apiEndpointScopes.get(apiEndpointId).add(scopeName);
                    }
                }
            }

            // building urltree
            urlGetTree = new PathNode();
            urlPostTree = new PathNode();
            urlPutTree = new PathNode();
            urlDeleteTree = new PathNode();

            for (var apiEndpoint : apiEndpoints) {
                PathNode curNode;

                if (apiEndpoint.getMethod().equals("GET"))
                    curNode = urlGetTree;
                else if (apiEndpoint.getMethod().equals("POST"))
                    curNode = urlPostTree;
                else if (apiEndpoint.getMethod().equals("PUT"))
                    curNode = urlPutTree;
                else if (apiEndpoint.getMethod().equals("DELETE"))
                    curNode = urlDeleteTree;
                else
                    throw new IllegalStateException("Unknown ApiEndpoint method: " + apiEndpoint.getMethod());

                for (var nodeValue : apiEndpoint.getEndpoint().split("/")) {
                    if (nodeValue.equals(""))
                        continue; // skip empty node value

                    if (curNode.getChildren() == null)
                        curNode.setChildren(new HashMap<>());

                    PathNode nextNode = curNode.getChildren().get(nodeValue);
                    if (nextNode == null) {
                        nextNode = new PathNode();
                        nextNode.setValue(nodeValue);
                        curNode.getChildren().put(nodeValue, nextNode);
                    }

                    curNode = nextNode;
                }

                // endpoint node
                curNode.setEndpoint(apiEndpoint);
                var scopeSet = apiEndpointScopes.get(apiEndpoint.getApiEndpointId());
                if (scopeSet == null)
                    curNode.setScopes(new HashSet<>());
                else
                    curNode.setScopes(new HashSet<>(scopeSet));
            }

            logger.info("permission configured successfully");
        }
    }

    /**
     *
     * @param root
     * @param endpoint
     * @return null on not found
     */
    private PathNode getEndpointNode(PathNode root, String endpoint) {
        PathNode endpointNode = root;
        for (var nodeValue : endpoint.split("/")) {
            if (nodeValue.equals(""))
                continue;

            PathNode next = null;
            if (endpointNode.getChildren() != null)
                next = endpointNode.getChildren().get(nodeValue);

            if (next == null)
                next = endpointNode.getWildcardChild(); // try wildcard

            if (next == null)
                return null;

            endpointNode = next;
        }

        return endpointNode;
    }

    /**
     *
     * @param scope
     * @param endpoint
     * @param method
     * @return null on endpoint not found
     */
    public Boolean isPermitted(String scope, String endpoint, String method) {
        PathNode endpointNode = null;
        if (method.equals("GET"))
            endpointNode = getEndpointNode(urlGetTree, endpoint);
        else if (method.equals("POST"))
            endpointNode = getEndpointNode(urlPostTree, endpoint);
        else if (method.equals("PUT"))
            endpointNode = getEndpointNode(urlPutTree, endpoint);
        else if (method.equals("DELETE"))
            endpointNode = getEndpointNode(urlDeleteTree, endpoint);

        if (endpointNode == null)
            return null;

        var apiEndpoint = endpointNode.getEndpoint();
        if (apiEndpoint == null)
            return null;

        if (apiEndpoint.isPublic())
            return true;

        var scopes = endpointNode.getScopes();
        return scopes.contains(scope);
    }

}
