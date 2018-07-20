package com.dudu.oauth;

import java.util.Map;
import java.util.Set;

/**
 *
 */
public class PathNode {
    // null on it is a leaf
    private Map<String, PathNode> children;

    // path node value. * indicates wildcard.
    private String value;

    // nullable.
    private ApiEndpoint endpoint;

    private Set<String> scopes;

    public Map<String, PathNode> getChildren() {
        return children;
    }

    public void setChildren(Map<String, PathNode> children) {
        this.children = children;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ApiEndpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(ApiEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }

    public PathNode getWildcardChild() {
        if (children == null)
            return null;

        return children.get("*");
    }
}
