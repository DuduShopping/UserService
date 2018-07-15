package com.dudu;

import com.dudu.common.PermissionManager;
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
        System.out.println(permissionManager.isPublicPermitted("Customer", "/resource", "GET"));
        System.out.println(permissionManager.isPublicPermitted("", "/login", "POST"));
        System.out.println(permissionManager.isPublicPermitted("Customer", "/private", "POST"));
        System.out.println(permissionManager.isPublicPermitted("Customer", "/other", "POST"));
    }
}
