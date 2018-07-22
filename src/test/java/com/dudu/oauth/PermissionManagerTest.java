package com.dudu.oauth;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


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
    public void isPermitted() throws Exception {
        assertTrue(permissionManager.isPermitted("Customer", "/resource", "GET"));
        assertTrue(permissionManager.isPermitted("Any_Scope", "/login", "POST"));
        assertTrue(permissionManager.isPermitted("Customer", "/resource/bar/", "GET"));
        assertTrue(permissionManager.isPermitted("Customer", "/resource/foo/", "GET"));
        assertFalse(permissionManager.isPermitted("Customer", "/resource/*", "GET"));
        assertFalse(permissionManager.isPermitted("Customer", "/unknown", "GET"));

        assertTrue(permissionManager.isPermitted("Superman", "/ko/li/1", "GET"));
        assertTrue(permissionManager.isPermitted("Superman", "/ko/dd", "POST"));
        assertFalse(permissionManager.isPermitted("Superman", "/ko/dd", "GET"));
        assertFalse(permissionManager.isPermitted("Superman", "/resource/bar", "GET"));
    }

    @Test
    public void isPublic() throws Exception {
        assertTrue(permissionManager.isPublic("/login", "POST"));
        assertFalse(permissionManager.isPublic("/resource", "POST"));
        assertFalse(permissionManager.isPublic("/resource", "GET"));
    }
}
