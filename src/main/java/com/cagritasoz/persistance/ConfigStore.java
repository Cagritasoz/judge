package com.cagritasoz.persistance;

import com.cagritasoz.model.Configuration;

import java.util.List;
import java.util.UUID;

public class ConfigStore { // Manage all configurations.

    private List<Configuration> sampleConfigs;

    private List<Configuration> customConfigs;
    private String customConfigsDir;



    public Configuration findById(UUID uuid) {
        return null;
    }
}
