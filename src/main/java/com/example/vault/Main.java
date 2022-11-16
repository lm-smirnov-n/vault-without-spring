package com.example.vault;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, String> data = VaultConfiguring.getVaultConfigWithDefaults("as-sp-report-manager", "test");
        String test = PropertiesConfiguring.config.getString("test.addr");
        System.out.println(data);
    }
}
