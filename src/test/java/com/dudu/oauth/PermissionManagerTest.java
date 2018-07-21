package com.dudu.oauth;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import java.net.URL;


public class PermissionManagerTest {
    private PermissionManager permissionManager;

    @Before
    public void setup() throws Exception {
        URL resource = getClass().getResource("/permissions.json");
        Assume.assumeNotNull(resource);
        String conf = resource.getFile();
        System.out.println(conf);
        permissionManager = new PermissionManager(conf);
    }

    @Test
    public void isPublicPermitted() throws Exception {
        System.out.println(permissionManager.isPermitted("Customer", "/resource", "GET"));
        System.out.println(permissionManager.isPermitted("", "/login", "POST"));
        System.out.println(permissionManager.isPermitted("Customer", "/private", "POST"));
        System.out.println(permissionManager.isPermitted("Customer", "/other", "POST"));
        System.out.println(permissionManager.isPermitted("Superman", "/other", "POST"));
        System.out.println(permissionManager.isPermitted("Superman", "/resource", "POST"));
    }

    @Test
    public void addApiEndpoint() throws Exception {
        var tree = new PathNode("root");
        var apiEndpoint = new ApiEndpoint();
        apiEndpoint.setEndpoint("/bar");
        permissionManager.addApiEnpoint(tree, apiEndpoint);

        apiEndpoint = new ApiEndpoint();
        apiEndpoint.setEndpoint("/bar/foo");
        permissionManager.addApiEnpoint(tree, apiEndpoint);

        apiEndpoint = new ApiEndpoint();
        apiEndpoint.setEndpoint("/bar/goo");
        permissionManager.addApiEnpoint(tree, apiEndpoint);

        apiEndpoint = new ApiEndpoint();
        apiEndpoint.setEndpoint("/pong");
        permissionManager.addApiEnpoint(tree, apiEndpoint);

        println("done");
    }

    private void println(String o) {
        System.out.println(o);
    }

}
