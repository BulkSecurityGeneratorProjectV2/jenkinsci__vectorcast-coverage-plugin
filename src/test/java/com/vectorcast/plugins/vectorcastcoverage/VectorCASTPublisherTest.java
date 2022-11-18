package com.vectorcast.plugins.vectorcastcoverage;

import com.vectorcast.plugins.vectorcastcoverage.VectorCASTPublisher;
import hudson.FilePath;

import junit.framework.Assert;

import java.io.File;
import java.nio.file.Files;

/**
 * 
 * @autor manuel_carrasco
 */
public class VectorCASTPublisherTest extends AbstractVectorCASTTestBase {
	
	public void testLocateReports() throws Exception {

		// Create a temporary workspace in the system 
		File w = Files.createTempDirectory("workspace" + ".test").toFile();
		w.deleteOnExit();
		FilePath workspace = new FilePath(w);

		// Create 4 files in the workspace
		File f1 = Files.createTempFile(w.toPath(), "coverage", ".xml").toFile();
		f1.deleteOnExit();
		File f2 = Files.createTempFile(w.toPath(), "anyname", ".xml").toFile();
		f2.deleteOnExit();
		File f3 = Files.createTempFile(w.toPath(), "coverage", ".xml").toFile();
		f3.deleteOnExit();
		File f4 = Files.createTempFile(w.toPath(), "anyname", ".xml").toFile();
		f4.deleteOnExit();


		// Create a folder and move there 2 files
		File d1 = new File(workspace.child("subdir").getRemote());
		d1.mkdir();
		d1.deleteOnExit();

		File f5 = new File(workspace.child(d1.getName()).child(f3.getName()).getRemote());
		File f6 = new File(workspace.child(d1.getName()).child(f4.getName()).getRemote());
		f3.renameTo(f5);
		f4.renameTo(f6);
		f5.deleteOnExit();
		f6.deleteOnExit();
		
		// Look for files in the entire workspace recursively without providing 
		// the includes parameter
		FilePath[] reports = VectorCASTPublisher.locateCoverageReports(workspace, "**/coverage*.xml");
		Assert.assertEquals(2 , reports.length);

		// Generate a includes string and look for files 
		String includes = f1.getName() + "; " + f2.getName() + "; " + d1.getName();
		reports = VectorCASTPublisher.locateCoverageReports(workspace, includes);
		Assert.assertEquals(3, reports.length);

		// Save files in local workspace
		FilePath local = workspace.child("coverage_localfolder");
		VectorCASTPublisher.saveCoverageReports(local, reports);
		Assert.assertEquals(3, local.list().size());
		local.deleteRecursive();

	}

}
