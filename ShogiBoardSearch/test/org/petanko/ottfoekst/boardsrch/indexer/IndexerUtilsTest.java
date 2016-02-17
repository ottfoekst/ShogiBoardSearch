package org.petanko.ottfoekst.boardsrch.indexer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Random;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class IndexerUtilsTest {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();
	
	@Test
	public void getKifuIdFilePath_Test() throws Exception {
		File expected = tmpFolder.newFile(IndexerUtils.KIFUID_FILE_NAME);
		assertEquals(expected.getAbsolutePath(), IndexerUtils.getKifuIdFilePath(tmpFolder.getRoot().toPath()).toString());
	}
	
	@Test
	public void getBoardInvFilePath_Test() throws Exception {
		int blockNo = new Random().nextInt(10);
		File expected = tmpFolder.newFile(IndexerUtils.BOARD_INV_FILE_NAME + "." + blockNo);
		assertEquals(expected.getAbsolutePath(), IndexerUtils.getBoardInvFilePath(tmpFolder.getRoot().toPath(), blockNo).toString());
	}
}
