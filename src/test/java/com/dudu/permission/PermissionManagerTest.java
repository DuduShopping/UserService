package com.dudu.permission;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

public class PermissionManagerTest {
    String conf;

    @Before
    public void setup() {
        URL resource = getClass().getResource("/permissions.json");
        Assume.assumeNotNull(resource);
        conf = resource.getFile();
        System.out.println(conf);
    }

    @Test
    public void init() throws Exception {

    }


    @Test
    public void isPublicPermitted() throws Exception {
        PermissionManager.init(conf);
        System.out.println(PermissionManager.isPublicPermitted("Customer", "/resource", "GET"));
        System.out.println(PermissionManager.isPublicPermitted("", "/login", "POST"));
        System.out.println(PermissionManager.isPublicPermitted("Customer", "/private", "POST"));
        System.out.println(PermissionManager.isPublicPermitted("Customer", "/other", "POST"));
    }
}
