package org.petanko.ottfoekst.boardsrch.indexer;

import java.nio.file.Path;

public class IndexerUtils {
	
	/** ����ID�t�@�C���� */
	public static final String KIFUID_FILE_NAME = "bs.kifuid";
	/** ����ID�|�C���^�t�@�C���� */
	public static final String KIFUID_PTR_FILE_NAME = "bs.kifuid.ptr";
	/** �ǖʓ]�u�C���f�b�N�X�t�@�C���� */
	public static final String BOARD_INV_FILE_NAME = "bs.board.inv";
	/** �ǖʓ]�u�C���f�b�N�X�|�C���^�t�@�C���� */
	public static final String BOARD_INV_PTR_FILE_NAME = "bs.board.inv.ptr";

	/**
	 * ����ID�t�@�C���̃p�X��Ԃ��܂��B
	 * @param indexDir �C���f�b�N�X���i�[����p�X
	 * @return ����ID�t�@�C���̃p�X
	 */
	public static Path getKifuIdFilePath(Path indexDir) {
		return indexDir.resolve(KIFUID_FILE_NAME);
	}
	
	/**
	 * ����ID�|�C���^�t�@�C���̃p�X��Ԃ��܂��B
	 * @param indexDir �C���f�b�N�X���i�[����p�X
	 * @return ����ID�|�C���^�t�@�C���̃p�X
	 */
	public static Path getKifuIdPtrFilePath(Path indexDir) {
		return indexDir.resolve(KIFUID_PTR_FILE_NAME);
	}
	
	/**
	 * �ǖʓ]�u�C���f�b�N�X�t�@�C���̃p�X��Ԃ��܂��B
	 * @param indexDir �C���f�b�N�X���i�[����p�X
	 * @param blockNo �ǖʃu���b�N
	 * @return �ǖʓ]�u�C���f�b�N�X�t�@�C���̃p�X
	 */
	public static Path getBoardInvFilePath(Path indexDir, int blockNo) {
		return indexDir.resolve(BOARD_INV_FILE_NAME + "." + blockNo);
	}
	
	/**
	 * �ǖʓ]�u�C���f�b�N�X�|�C���^�t�@�C���̃p�X��Ԃ��܂��B
	 * @param indexDir �C���f�b�N�X���i�[����p�X
	 * @param blockNo �ǖʃu���b�N
	 * @return �ǖʓ]�u�C���f�b�N�X�|�C���^�t�@�C���̃p�X
	 */
	public static Path getBoardInvPtrFilePath(Path indexDir, int blockNo) {
		return indexDir.resolve(BOARD_INV_PTR_FILE_NAME + "." + blockNo);
	}
}
