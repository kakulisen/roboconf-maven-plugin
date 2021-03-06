/**
 * Copyright 2014-2015 Linagora, Université Joseph Fourier, Floralis
 *
 * The present code is developed in the scope of the joint LINAGORA -
 * Université Joseph Fourier - Floralis research program and is designated
 * as a "Result" pursuant to the terms and conditions of the LINAGORA
 * - Université Joseph Fourier - Floralis research program. Each copyright
 * holder of Results enumerated here above fully & independently holds complete
 * ownership of the complete Intellectual Property rights applicable to the whole
 * of said Results, and may freely exploit it in any manner which does not infringe
 * the moral rights of the other copyright holders.
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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Vincent Zurczak - Linagora
 */
public class ValidateProjectMojoTest {

	private static final String GOAL = "validate-project";

	@Rule
	public MojoRule rule = new MojoRule();

	@Rule
	public TestResources resources = new TestResources();



	@Test( expected = MojoFailureException.class )
	public void testInvalidStructure() throws Exception {

		findMojo( "project--invalid", GOAL ).execute();
	}


	@Test
	public void testValidStructure() throws Exception {

		findMojo( "project--invalid-app", GOAL ).execute();
		findMojo( "project--valid-with-warnings", GOAL ).execute();
		findMojo( "project--valid", GOAL ).execute();
	}


	protected AbstractMojo findMojo( String projectName, String goalName ) throws Exception {

		// Find the project
		File baseDir = this.resources.getBasedir( projectName );
		Assert.assertNotNull( baseDir );
		Assert.assertTrue( baseDir.exists());
		Assert.assertTrue( baseDir.isDirectory());

		File pom = new File( baseDir, "pom.xml" );
		AbstractMojo mojo = (AbstractMojo) this.rule.lookupMojo( goalName, pom );
		Assert.assertNotNull( mojo );

		// Create the Maven project by hand (...)
		final MavenProject mvnProject = new MavenProject() ;
        mvnProject.setFile( pom ) ;

        this.rule.setVariableValueToObject( mojo, "project", mvnProject );
		Assert.assertNotNull( this.rule.getVariableValueFromObject( mojo, "project" ));

		// Initialize the project
		InitializeMojo initMojo = new InitializeMojo();
		initMojo.setProject( mvnProject );
		initMojo.execute();

		return mojo;
	}
}
