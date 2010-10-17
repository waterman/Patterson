package org.patterson.util;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * Test for {@link FilePath}
 * 
 * @author waterman
 * 
 */
public class FilePathTest {

	@Test
	public void testFrom() throws Exception {
		String tempPath = "lala" + File.separatorChar + "la.txt";
		FilePath tempResult = FilePath.from(tempPath);
		assertEquals("la.txt", tempResult.getFilename());
		assertEquals(1, tempResult.getFolders().size());
		assertEquals("lala", tempResult.getFolders().get(0));

		tempPath = File.separatorChar + "lala" + File.separatorChar + "la.txt";
		tempResult = FilePath.from(tempPath);
		assertEquals("la.txt", tempResult.getFilename());
		assertEquals(1, tempResult.getFolders().size());
		assertEquals("lala", tempResult.getFolders().get(0));

		tempPath = File.separatorChar + "lala" + File.separatorChar + "test"
				+ File.separatorChar + "la.txt";
		tempResult = FilePath.from(tempPath);
		assertEquals("la.txt", tempResult.getFilename());
		assertEquals(2, tempResult.getFolders().size());
		assertEquals("lala", tempResult.getFolders().get(0));
		assertEquals("test", tempResult.getFolders().get(1));

		tempPath = "lala" + File.separatorChar + "test" + File.separatorChar
				+ "la.txt";
		tempResult = FilePath.from(tempPath);
		assertEquals("la.txt", tempResult.getFilename());
		assertEquals(2, tempResult.getFolders().size());
		assertEquals("lala", tempResult.getFolders().get(0));
		assertEquals("test", tempResult.getFolders().get(1));
	}

	@Test
	public void testStripCommonLeadingFolders() throws Exception {
		FilePath tempPath1 = FilePath.from("C:" + File.separator + "lala"
				+ File.separator + "test" + File.separator + "my.txt");
		FilePath tempPath2 = FilePath.from("C:" + File.separator + "lala"
				+ File.separator + "0815" + File.separator + "this.log");
		FilePath tempResult = tempPath1.stripCommonLeadingFolders(tempPath2);
		assertEquals("my.txt", tempResult.getFilename());
		assertEquals(1, tempResult.getFolders().size());
		assertEquals("test", tempResult.getFolders().get(0));

		tempPath1 = FilePath.from("C:" + File.separator + "lala"
				+ File.separator + "test" + File.separator + "my.txt");
		tempPath2 = FilePath.from("C:" + File.separator + "lala"
				+ File.separator + "test" + File.separator + "this.log");
		tempResult = tempPath1.stripCommonLeadingFolders(tempPath2);
		assertEquals("my.txt", tempResult.getFilename());
		assertEquals(0, tempResult.getFolders().size());

		tempPath1 = FilePath.from("C:" + File.separator + "lala"
				+ File.separator + "test" + File.separator + "my.txt");
		tempPath2 = FilePath.from("D:" + File.separator + "lala"
				+ File.separator + "0815" + File.separator + "this.log");
		tempResult = tempPath1.stripCommonLeadingFolders(tempPath2);
		assertEquals("my.txt", tempResult.getFilename());
		assertEquals(3, tempResult.getFolders().size());
		assertEquals("C:", tempResult.getFolders().get(0));
		assertEquals("lala", tempResult.getFolders().get(1));
		assertEquals("test", tempResult.getFolders().get(2));

		tempPath1 = FilePath.from("C:" + File.separator + "test"
				+ File.separator + "my.txt");
		tempPath2 = FilePath.from("C:" + File.separator + "my.txt");
		tempResult = tempPath1.stripCommonLeadingFolders(tempPath2);
		assertEquals("my.txt", tempResult.getFilename());
		assertEquals(1, tempResult.getFolders().size());
		assertEquals("test", tempResult.getFolders().get(0));
	}
}
