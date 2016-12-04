package hu.skawa.migrator_maven_plugin;

import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;

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

import com.google.common.collect.Lists;

import hu.skawa.migrator_maven_plugin.model.Server;

/**
 * Transform all dependencies for Bazel. Retrieves relevant information from the POM itself, and
 * uses the {@link ResolutionScope} TEST to scout all dependencies.
 * 
 * @author zmeggyesi
 */
@Mojo(
		name = "servers",
		defaultPhase = LifecyclePhase.PROCESS_SOURCES,
		requiresDependencyResolution = ResolutionScope.TEST)
public class ServerExport extends AbstractMojo {
	
	@Parameter(required = true, defaultValue = "${project}")
	private MavenProject project;
	
	@Parameter(property = "outputFilePrefix")
	private String outputFilePrefix;
	
	public void execute() throws MojoExecutionException {
		List<ArtifactRepository> remoteRepos = project.getRemoteArtifactRepositories();
		List<Server> servers = Lists.newArrayList();
		for (ArtifactRepository repo : remoteRepos) {
			servers.add(new Server(repo.getUrl(), repo.getAuthentication().getUsername(), repo.getAuthentication().getPassword(), repo.getId()));
		}
		
		for (Server s : servers) {
			getLog().info(s.toBazelServer());
		}
	}
}
