package com.example.platform.scheduler.interfaces.rest.request;

import com.example.platform.scheduler.application.dto.NodeInfoDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * Request DTO for registering a new executor in the cluster.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterExecutorRequest {

    @NotBlank(message = "Executor ID is required")
    private String executorId;

    @NotNull(message = "Node information is required")
    @Valid
    private NodeInfoDTO nodeInfo;

    @NotNull(message = "Capabilities information is required")
    private Map<String, Object> capabilities;

    /**
     * Gets the executor ID.
     * 
     * @return Executor ID
     */
    public String getExecutorId() {
        return executorId;
    }

    /**
     * Sets the executor ID.
     * 
     * @param executorId Executor ID
     */
    public void setExecutorId(String executorId) {
        this.executorId = executorId;
    }

    /**
     * Gets the node information.
     * 
     * @return Node information
     */
    public NodeInfoDTO getNodeInfo() {
        return nodeInfo;
    }

    /**
     * Sets the node information.
     * 
     * @param nodeInfo Node information
     */
    public void setNodeInfo(NodeInfoDTO nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    /**
     * Gets the executor capabilities.
     * 
     * @return Capabilities as a map
     */
    public Map<String, Object> getCapabilities() {
        return capabilities;
    }

    /**
     * Sets the executor capabilities.
     * 
     * @param capabilities Capabilities as a map
     */
    public void setCapabilities(Map<String, Object> capabilities) {
        this.capabilities = capabilities;
    }
}