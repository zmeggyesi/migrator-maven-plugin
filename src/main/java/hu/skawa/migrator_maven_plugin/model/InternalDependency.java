package hu.skawa.migrator_maven_plugin.model;

import com.google.common.base.CharMatcher;

public class InternalDependency {
	private String groupId;
	private String artifactId;
	private String version;
	private String bazelName;
	private String bazelArtifact;
	
	public InternalDependency() {
	}
	
	public InternalDependency(String groupId, String artifactId, String version) {
		super();
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}
	
	public String getArtifactId() {
		return artifactId;
	}
	
	public String getGroupId() {
		return groupId;
	}

	public String getVersion() {
		return version;
	}
	
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Group: ");
		sb.append(this.groupId);
		sb.append("\n");
		sb.append("Artifact: ");
		sb.append(this.artifactId);
		sb.append("\n");
		sb.append("Version: ");
		sb.append(this.version);
		sb.append("\n");
		return sb.toString();
	}
	
	private String sanitize(CharSequence input) {
		return CharMatcher.javaLetterOrDigit().or(CharMatcher.is('_')).negate().replaceFrom(input, "_");
	}
	
	public String toBazelDirective() {
		StringBuilder sb = new StringBuilder("maven_jar(\n");
		sb.append("\tname = \"");
		
		// sanitize name
		this.bazelName = sanitize(this.groupId + "." + this.artifactId);
		sb.append(this.bazelName);
		
		sb.append("\",\n\t");
		sb.append("artifact = \"");
		this.bazelArtifact = this.groupId+ ":" + this.artifactId + ":" + this.version;
		sb.append(this.bazelArtifact);
		sb.append("\",\n");
		sb.append(")");
		return sb.toString();
	}

	public String getBazelName() {
		return bazelName;
	}

	public String getBazelArtifact() {
		return bazelArtifact;
	}
}
