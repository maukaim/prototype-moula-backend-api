package com.maukaim.blob.plugins.core;

import com.maukaim.blob.plugins.core.model.Plugin;
import com.maukaim.blob.plugins.core.model.PluginStatus;

public interface PluginLifeCycleListener {

    void beforeStatusChange(Plugin plugin, PluginStatus current, PluginStatus next) throws PluginLifeCycleException;
    void beforeDestroy(Plugin plugin) throws PluginLifeCycleException;

    void afterStatusChanged(Plugin plugin, PluginStatus old, PluginStatus current);
    void afterDestroy(Plugin plugin);
}
