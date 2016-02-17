package org.petanko.ottfoekst.boardsrch.indexer;

import java.nio.file.Path;

/**
 * �ǖʃf�[�^�̃C���f�b�N�X�𐶐�����N���X�B
 * @author ottfoekst
 *
 */
public class BoardDataIndexer {

	/** �����f�[�^�̃t�@�C���p�X�̋�؂蕶�� */
	public static final char KIFUDATA_DELIM = 0x00;
	
	/** �C���f�b�N�X���i�[����p�X */
	private Path indexDir;
	/** �����t�@�C�����i�[���ꂽ�f�B���N�g�� */
	private Path kifuDataPath;
	
	/** ����ID */
	private int kifuId = 0;
	
	/**
	 * �R���X�g���N�^�B
	 * @param indexDir �C���f�b�N�X���i�[����p�X
	 * @param kifuDataPath �����t�@�C�����i�[���ꂽ�p�X
	 */
	public BoardDataIndexer(Path indexDir, Path kifuDataPath) {
		this.indexDir = indexDir;
		this.kifuDataPath = kifuDataPath;
	}
	
	/**
	 * �ǖʃf�[�^�̃C���f�b�N�X�𐶐����܂��B
	 * @throws Exception
	 */
	public void generateIndex() throws Exception {
		// TODO ����
	}
}
