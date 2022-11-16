package com.example.vault;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;

import java.util.Map;

public class VaultConfiguring {
    private static final String VAULT_CONFIG_PREFIX = "vault";
    private static final String VAULT_NAMESPACE_CONFIG = VAULT_CONFIG_PREFIX + ".namespace";
    private static final String VAULT_ADDRESS_CONFIG = VAULT_CONFIG_PREFIX + ".address";
    private static final String VAULT_BACKEND_CONFIG = VAULT_CONFIG_PREFIX + ".backend";
    private static final String VAULT_DEFAULT_CONTEXT_CONFIG = VAULT_CONFIG_PREFIX + ".default-context";

    private static Vault vault;
    private static String backendPath;
    private static String defaultContextPath;

    static {
        backendPath = PropertiesConfiguring.config.getString(VAULT_BACKEND_CONFIG);
        defaultContextPath = PropertiesConfiguring.config.getString(VAULT_DEFAULT_CONTEXT_CONFIG);
        String namespace = PropertiesConfiguring.config.getString(VAULT_NAMESPACE_CONFIG);
        String address = PropertiesConfiguring.config.getString(VAULT_ADDRESS_CONFIG);

        String secretId = System.getProperty("spring.cloud.vault.app-role.secret-id");
        String roleId = System.getProperty("spring.cloud.vault.app-role.role-id");

        final VaultConfig config;
        try {
            config = new VaultConfig()
                    .address(address)
                    .nameSpace(namespace)
                    .build();
        } catch (VaultException e) {
            throw new RuntimeException(e);
        }

        vault = new Vault(config);

        String token;
        try {
            token = vault.auth().loginByAppRole(roleId, secretId).getAuthClientToken();
        } catch (VaultException e) {
            throw new RuntimeException(e);
        }

        vault = new Vault(config.token(token));
    }

    static Map<String, String> getVaultConfigWithDefaults(String appName, String profile){
        Map<String, String> commonData = getVaultConfig(defaultContextPath, profile);
        Map<String, String> serviceData = getVaultConfig(appName, profile);

        commonData.putAll(serviceData);
        return commonData;
    }

    static Map<String, String> getVaultConfig(String path, String profile){
        try {
            return vault.logical().read(backendPath + "/" + path + "/" + profile).getData();
        } catch (VaultException e) {
            throw new RuntimeException(e);
        }
    }
}
