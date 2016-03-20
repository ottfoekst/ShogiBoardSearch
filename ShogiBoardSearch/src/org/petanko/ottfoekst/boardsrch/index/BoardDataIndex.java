package org.petanko.ottfoekst.boardsrch.index;

import java.io.DataInputStream;
import java.io.EOFException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.petanko.ottfoekst.boardsrch.indexer.IndexerUtils;
import org.petanko.ottfoekst.boardsrch.util.IoUtils;

/**
 * �ǖʓ]�u�C���f�b�N�X�B
 * @author ottfoekst
 *
 */
public class BoardDataIndex {
	
	/** �e�ǖʓ]�u�C���f�b�N�X�ւ̃|�C���^����ǂݍ��ދǖʃg�[�N��ID���B
	 *  ���̐��l���傫���قǃ������g�p�ʂ͗}����܂����A�ǖʃf�[�^�C���f�b�N�X�̓ǂݍ��݂Ɏ��Ԃ�������\��������܂��B */
	public static final int BOARDTOKEN_NUM_EACH_PTR = 50;

	/** �C���f�b�N�X���i�[����p�X */
	private Path indexDir;
	
	/** �ǖʓ]�u�C���f�b�N�X(�I��������) */
	private long[][] boardTokenIdMatrix = new long[50][];
	private long[][] boardInvPtrMatrix = new long[50][];
	
	/**
	 * �R���X�g���N�^�B
	 * @param indexDir�@�C���f�b�N�X���i�[����p�X
	 */
	public BoardDataIndex(Path indexDir) {
		this.indexDir = indexDir;
	}
	
	/**
	 * �ǖʓ]�u�C���f�b�N�X�����[�h���܂��B
	 */
	public void loadIndex() throws Exception {
		for(int blockNo = 0; blockNo < 50; blockNo++) {
			List<Long> boardTokenIdList = new ArrayList<>();
			List<Long> boardInvPtrList = new ArrayList<>();
			
			int skipCount = 0;
			try(DataInputStream boardInvPtrDis = IoUtils.newDataInputStream(IndexerUtils.getBoardInvPtrFilePath(indexDir, blockNo))) {
				while(true) {
					// �ǖʃg�[�N��ID �� �ǖʓ]�u�C���f�b�N�X�̃|�C���^�����X�g�ɓo�^
					boardTokenIdList.add(boardInvPtrDis.readLong());
					boardInvPtrList.add(boardInvPtrDis.readLong());
					
					while(true) {
						if(skipCount >= BOARDTOKEN_NUM_EACH_PTR - 1) {
							skipCount = 0;
							break;
						}
						
						boardInvPtrDis.readLong(); // �ǖʃg�[�N��ID��ǂݔ�΂�
						boardInvPtrDis.readLong(); // �ǖʓ]�u�C���f�b�N�X�̃|�C���^��ǂݔ�΂�
						skipCount++;
					}
				}
			}
			catch(EOFException end) {
				// �ǂݍ��ݏI��
			}
			
			// �ǖʓ]�u�C���f�b�N�X(�I��������)�ɃZ�b�g����
			boardTokenIdMatrix[blockNo] = createArrayWithLongMax(boardTokenIdList);
			boardInvPtrMatrix[blockNo] = createArrayWithLongMax(boardInvPtrList);
		}
	}
	
	private long[] createArrayWithLongMax(List<Long> longList) {
		
		long[] longArray = new long[longList.size() + 1];
		for(int index = 0; index < longList.size(); index++) {
			longArray[index] = longList.get(index);
		}
		longArray[longList.size()] = Long.MAX_VALUE; // �Ԑl
		
		return longArray;
	}
	
	/**
	 * �ǖʓ]�u�C���f�b�N�X���A�����[�h���܂��B
	 */
	public void unloadIndex() {
		boardTokenIdMatrix = null;
		boardInvPtrMatrix = null;
	}
}
