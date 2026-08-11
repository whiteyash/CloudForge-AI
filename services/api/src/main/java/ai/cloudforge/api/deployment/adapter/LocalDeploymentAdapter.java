package ai.cloudforge.api.deployment.adapter;

import org.springframework.stereotype.Component;

@Component
public class LocalDeploymentAdapter implements DeploymentAdapter {

    @Override
    public String getAdapterType() {
        return "LOCAL";
    }

    @Override
    public boolean executeDeployment(String targetName, String strategy, String artifactKey) {
        // Local deployment execution simulation
        return true;
    }

    @Override
    public boolean executeRollback(String targetName, String previousArtifactKey) {
        // Local rollback execution simulation
        return true;
    }
}
