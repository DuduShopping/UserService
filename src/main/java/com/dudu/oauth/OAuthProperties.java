package com.dudu.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotEmpty;

@ConfigurationProperties("dudu.oauth")
public class OAuthProperties {
    private String privKeyFile;
    private String pubKeyFile;

    @NotEmpty
    private String permissionsFile;

    public String getPrivKeyFile() {
        return privKeyFile;
    }

    public void setPrivKeyFile(String privKeyFile) {
        this.privKeyFile = privKeyFile;
    }

    public String getPubKeyFile() {
        return pubKeyFile;
    }

    public void setPubKeyFile(String pubKeyFile) {
        this.pubKeyFile = pubKeyFile;
    }

    public String getPermissionsFile() {
        return permissionsFile;
    }

    public void setPermissionsFile(String permissionsFile) {
        this.permissionsFile = permissionsFile;
    }
}
