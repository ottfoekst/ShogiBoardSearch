package org.petanko.ottfoekst.boardsrch.main;

import java.io.File;
import java.nio.file.Path;

import org.petanko.ottfoekst.boardsrch.indexer.BoardDataIndexer;

public class BoardDataIndexerMain {

	/** �����t�@�C�����i�[���ꂽ�p�X�̃f�t�H���g�l */
	public static final String KIFU_FILE_PATH_DEFAULT = "[KIFU_FILE_PATH]";

	/**
	 * �ǖʓ]�u�C���f�b�N�X�𐶐����郁�C���N���X�B
	 * @param args[0] �C���f�b�N�X���i�[����p�X
	 * @param args[1] �����t�@�C�����i�[���ꂽ�p�X
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// �����̕ϊ�
		Path indexDir = new File(args[0]).toPath();

		if(KIFU_FILE_PATH_DEFAULT.equals(args[1])) {
			throw new IllegalArgumentException("�����t�@�C�����i�[���ꂽ�t�H���_�p�X���w�肵�Ă��������B : " + KIFU_FILE_PATH_DEFAULT);
		}
		Path kifuDataPath = new File(args[1]).toPath();

		// �ǖʓ]�u�C���f�b�N�X�̐���
		BoardDataIndexer boardDataIndexer = new BoardDataIndexer(indexDir, kifuDataPath);
		boardDataIndexer.generateIndex();
	}
}
