package com.dudu.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PermissionManager {
    private final Logger logger = LogManager.getLogger(PermissionManager.class);
    // ApiEndpointId -> ...
    private Map<Long, ApiEndpoint> apiEndpointMap;

    // Endpoint+Method -> ...
    private Map<String, ApiEndpoint> apiEndpointSignatureMap;

    // ScopeName -> ...
    private Map<String, Scope> scopeMap;

    public PermissionManager(String file) throws Exception {
        try (var in = new FileInputStream(file)) {
            var config = new JSONObject(new JSONTokener(in));

            // loading endpoints
            apiEndpointMap = new HashMap<>();
            var apiEndpointsJson = config.getJSONArray("ApiEndpoints");
            for (int i = 0; i < apiEndpointsJson.length(); i++) {
                var apiEndpointJson = apiEndpointsJson.getJSONObject(i);
                var apiEndpoint = new ApiEndpoint();
                apiEndpoint.setApiEndpointId(apiEndpointJson.getLong("ApiEndpointId"));
                apiEndpoint.setMethod(apiEndpointJson.getString("Method"));
                apiEndpoint.setEndpoint(apiEndpointJson.getString("Endpoint"));
                apiEndpoint.setPrivate(apiEndpointJson.has("IsPrivate") && apiEndpointJson.getBoolean("IsPrivate"));
                apiEndpoint.setPublic(apiEndpointJson.has("IsPublic") && apiEndpointJson.getBoolean("IsPublic"));

                apiEndpointMap.put(apiEndpoint.getApiEndpointId(), apiEndpoint);
            }

            // apiEndpointSignatureMap
            apiEndpointSignatureMap = new HashMap<>();
            for (Long id : apiEndpointMap.keySet()) {
                ApiEndpoint endpoint = apiEndpointMap.get(id);
                apiEndpointSignatureMap.put(signature(endpoint.getEndpoint(), endpoint.getMethod()), endpoint);
            }

            // loading scope
            scopeMap = new HashMap<>();
            var scopes = config.getJSONArray("Scopes");
            for (int i = 0; i < scopes.length(); i++) {
                var scopeJson = scopes.getJSONObject(i);
                var scope = new Scope();

                scope.setScopeName(scopeJson.getString("ScopeName"));
                scope.setEndpoints(new HashSet<>());
                if (scopeJson.has("ApiEndpointIds")) {
                    var apiEndpointIdsJson = scopeJson.getJSONArray("ApiEndpointIds");

                    for (int j = 0; j < apiEndpointIdsJson.length(); j++) {
                        var apiEndpointId = apiEndpointIdsJson.getLong(j);
                        var apiEndpoint = apiEndpointMap.get(apiEndpointId);
                        if (apiEndpoint == null)
                            throw new IllegalArgumentException("ApiEndpoint with id=" + apiEndpointId + " is missing");
                        scope.getEndpoints().add(apiEndpoint);
                    }
                }

                scopeMap.put(scope.getScopeName(), scope);
            }

            logger.info("permission configured successfully");
        }
    }

    /**
     *
     * @param scope scope name
     * @param endpoint
     * @param method
     * @return
     */
    public boolean isPublicPermitted(String scope, String endpoint, String method) {
        var apiEndpoint = apiEndpointSignatureMap.get(signature(endpoint, method));

        if (apiEndpoint == null)
            return false;

        if (apiEndpoint.isPrivate())
            return false;

        if (apiEndpoint.isPublic())
            return true;

        // check scope
        var scopeObj = scopeMap.get(scope);
        if (scopeObj == null)
            return false;

        return scopeObj.getEndpoints().contains(apiEndpoint);
    }

    private String signature(String endpoint, String method) {
        return endpoint + method;
    }

}
