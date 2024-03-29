package com.maukaim.blob.plugins.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DestroyResult {
    private boolean destroyed;
    private List<PluginLifeCycleException> exceptions;
}
