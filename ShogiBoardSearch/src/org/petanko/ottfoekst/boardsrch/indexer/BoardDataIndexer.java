package org.petanko.ottfoekst.boardsrch.indexer;

import java.io.DataOutputStream;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import org.petanko.ottfoekst.boardsrch.util.IoUtils;

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
	/** �����t�@�C�����i�[�����p�X */
	private Path kifuDataPath;
	
	/** ����ID */
	private int kifuId = 0;
	
	/** ����ID�t�@�C���������� */
	private DataOutputStream kifuIdDos;
	/** ����ID�|�C���^�t�@�C���������� */
	private DataOutputStream kifuIdPtrDos;
	/** �ǖʓ]�u�C���f�b�N�X�t�@�C���������� */
	private DataOutputStream[] boardInvDosList;
	/** �ǖʓ]�u�C���f�b�N�X�|�C���^�t�@�C���������� */
	private DataOutputStream[] boardInvPtrDosList;
	
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
				
		try {
			// ����ID�̏�����
			kifuId = 0;
			// �e��DataOutputStream�̏�����
			initializeDosList();
			
			// ��������ɓ]�u�C���f�b�N�X���\�z
			createInvIndexOnMemory(kifuDataPath.toFile());
		}
		finally {
			// �e��DataOutputStream�����
			closeDosList();
		}
	}

	private void initializeDosList() throws Exception {
		kifuIdDos = IoUtils.newDataOutputStream(IndexerUtils.getKifuIdFilePath(indexDir));
		kifuIdPtrDos = IoUtils.newDataOutputStream(IndexerUtils.getKifuIdPtrFilePath(indexDir));
		
		boardInvDosList = new DataOutputStream[50];
		for(int blockNo = 0; blockNo < boardInvDosList.length; blockNo++) {
			boardInvDosList[blockNo] = IoUtils.newDataOutputStream(IndexerUtils.getBoardInvFilePath(indexDir, blockNo));
		}
		
		boardInvPtrDosList = new DataOutputStream[50];
		for(int blockNo = 0; blockNo < boardInvPtrDosList.length; blockNo++) {
			boardInvPtrDosList[blockNo] = IoUtils.newDataOutputStream(IndexerUtils.getBoardInvPtrFilePath(indexDir, blockNo));
		}
	}

	private void createInvIndexOnMemory(File kifuDataFileOrFolder) {
		// �t�H���_�̂Ƃ�
		if(kifuDataFileOrFolder.isDirectory()) {
			File[] files = kifuDataFileOrFolder.listFiles();
			for(File file : files) {
				createInvIndexOnMemory(file);
			}
		}
		// �����t�@�C��(�g���q:kif)�̂Ƃ�
		else if(kifuDataFileOrFolder.toString().endsWith(".kif")) {
			// TODO ����
		}
	}

	private void closeDosList() {
		// �S�Ă�DataOutputStream��A��
		DataOutputStream[] allDosList = Stream.concat(Arrays.stream(new DataOutputStream[]{kifuIdDos, kifuIdPtrDos}), 
				Stream.concat(Arrays.stream(boardInvDosList), Arrays.stream(boardInvPtrDosList))).toArray(size -> new DataOutputStream[size]);
		// Exception���X���[�����ɑS�ĕ���
		IoUtils.closeSilently(allDosList);
	}
}
