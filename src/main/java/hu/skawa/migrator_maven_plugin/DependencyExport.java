package hu.skawa.migrator_maven_plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.artifact.Artifact;

/*
 * Copyright 2001-2005 The Apache Software Foundation. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import hu.skawa.migrator_maven_plugin.model.InternalDependency;

/**
 * Transform all dependencies for Bazel. Retrieves relevant information from the POM itself, and
 * uses the {@link ResolutionScope} TEST to scout all dependencies.
 * 
 * @author zmeggyesi
 */
@Mojo(
		name = "dependencies",
		defaultPhase = LifecyclePhase.PROCESS_SOURCES,
		requiresDependencyResolution = ResolutionScope.TEST)
public class DependencyExport extends AbstractMojo {
	
	@Parameter(required = true, defaultValue = "${project}")
	private MavenProject project;
	
	@Parameter(property = "outputFilePrefix")
	private String outputFilePrefix;
	
	@Parameter(property = "outputDirectives")
	private Boolean outputDirectives;
	
	@Parameter(property = "outputReferences")
	private Boolean outputReferences;
	
	@Parameter(property = "addHashes", defaultValue = "false")
	private Boolean addHashes;
	
	@Parameter(property = "addServers", defaultValue = "false")
	private Boolean addServers;

	private List<InternalDependency> allDependencies = new ArrayList<InternalDependency>();

	private Pattern jarPattern = Pattern.compile("^.+?\\.[^javadoc]\\.jar\\>(.+?)\\=$", Pattern.MULTILINE);
	@SuppressWarnings("unused")
	private Pattern pomPattern = Pattern.compile("^.+?pom\\>(.+?)\\=$", Pattern.MULTILINE);
	
	public void execute() throws MojoExecutionException {
		Set<Artifact> artifacts = project.getArtifacts();
		for (Artifact arti : artifacts) {
			File file = arti.getFile();
			String hash = "";
			try {
				byte[] contents = Files.toByteArray(file);
				hash = Hashing.sha1().hashBytes(contents).toString();
			} catch (IOException e) {
				throw new MojoExecutionException("Dependency could not be hashed!", e);
			}
			InternalDependency id = new InternalDependency(arti.getGroupId(), arti
					.getArtifactId(), arti.getVersion(), hash);
			File remotes = new File(file.getParent() + File.separator + "_remote.repositories");
			try {
				String remoteDescriptorContent = Files.toString(remotes, StandardCharsets.UTF_8);
				getLog().debug(remoteDescriptorContent);
					Matcher jarServerMatcher = jarPattern.matcher(remoteDescriptorContent);
					while (jarServerMatcher.find()) {
						String server = jarServerMatcher.group(1);
						if (server != null) {
							id.setJarServer(server);
						} else {
							id.setJarServer("");
						}
					}
			} catch (IOException e) {
				getLog().warn("Could not locate repo");
			}
			allDependencies.add(id);
		}
		
		try {
			for (InternalDependency dep : allDependencies) {
				if (outputFilePrefix != null) {
					if (outputDirectives) {
						File directives = new File(outputFilePrefix + "-directives");
						FileWriter directiveWriter = new FileWriter(directives);
						directiveWriter.write(dep.toBazelDirective(addHashes, addServers));
						directiveWriter.write("\n");
						directiveWriter.close();
					}
					if (outputReferences) {
						File references = new File(outputFilePrefix + "-references");
						FileWriter referenceWriter = new FileWriter(references);
						referenceWriter
								.write(dep.getArtifactId() + ": @" + dep.getBazelName() + "//jar");
						referenceWriter.write("\n");
						referenceWriter.close();
					}
				} else {
					getLog().info(dep.toBazelDirective(addHashes, addServers));
				}
			}
		} catch (IOException e) {
			getLog().error(e);
		}
	}
}
