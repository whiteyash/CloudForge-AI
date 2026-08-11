package ai.cloudforge.api.deployment.adapter;

public interface DeploymentAdapter {

    String getAdapterType();

    boolean executeDeployment(String targetName, String strategy, String artifactKey);

    boolean executeRollback(String targetName, String previousArtifactKey);
}
