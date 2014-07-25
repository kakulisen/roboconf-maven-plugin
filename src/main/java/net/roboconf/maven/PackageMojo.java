/**
 * Copyright 2014 Linagora, Université Joseph Fourier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.roboconf.maven;

import java.io.File;
import java.io.IOException;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;

/**
 * The <strong>package</strong> mojo.
 * @author Vincent Zurczak - Linagora
 */
@Mojo( name="package", defaultPhase = LifecyclePhase.PACKAGE )
public class PackageMojo extends AbstractMojo {

	@Parameter( defaultValue = "${project}", readonly = true )
	private MavenProject project;

	@Parameter( defaultValue = "${session}", readonly = true )
	private MavenSession session;

	@Component( role = Archiver.class, hint = "jar" )
	private JarArchiver jarArchiver;


	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		// Check the target file
		File modelDir = new File( this.project.getBuild().getOutputDirectory());
		if( ! modelDir.exists())
			throw new MojoExecutionException( "The model could not be found. " + modelDir );

		File targetDir = new File( this.project.getBuild().getDirectory());
	    String archiveName = this.project.getBuild().getFinalName() + ".zip";
	    File archiveFile = new File( targetDir, archiveName );

	    // Configure the archive creation
	    MavenArchiver archiver = new MavenArchiver();
	    archiver.setArchiver( this.jarArchiver );
	    archiver.setOutputFile( archiveFile );

	    // Add the application directories
		try {
			archiver.getArchiver().addDirectory( modelDir );

			MavenArchiveConfiguration conf = new MavenArchiveConfiguration();
			conf.setCompress( true );

			archiver.createArchive(  this.session, this.project, conf );
			this.project.getArtifact().setFile( archiveFile );

		} catch( ArchiverException e ) {
			throw new MojoExecutionException( "Exception while packaging.", e );

		} catch( IOException e ) {
			throw new MojoExecutionException( "Exception while packaging.", e );

		} catch( ManifestException e ) {
			throw new MojoExecutionException( "Exception while packaging.", e );

		} catch( DependencyResolutionRequiredException e ) {
			throw new MojoExecutionException( "Exception while packaging.", e );
		}
	}
}
