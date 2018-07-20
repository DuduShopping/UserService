package com.dudu;

import com.dudu.oauth.PermissionManager;
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
}
