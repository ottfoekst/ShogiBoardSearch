package org.petanko.ottfoekst.boardsrch.index;

import java.io.DataInputStream;
import java.io.EOFException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.petanko.ottfoekst.boardsrch.indexer.IndexerUtils;
import org.petanko.ottfoekst.boardsrch.util.ArrayUtils;
import org.petanko.ottfoekst.boardsrch.util.IoUtils;

/**
 * ����ID�C���f�b�N�X(����ID�Ɗ����t�@�C���p�X�̕R�t����ێ�)�B
 * @author ottfoekst
 *
 */
public class KifuIdIndex {
	
	/** �e����ID�t�@�C���ւ̃|�C���^����ǂݍ��ފ���ID���B
	 *  ���̐��l���傫���قǃ������g�p�ʂ͗}����܂����A����ID�t�@�C���̓ǂݍ��݂Ɏ��Ԃ�������\��������܂��B */
	public static final int KIFUID_NUM_EACH_PTR = 50;
	
	/** �C���f�b�N�X���i�[����p�X */
	private Path indexDir;
	
	/** ����ID�C���f�b�N�X(�I��������) */
	private int[] kifuIdArray;
	private long[] kifuIdPtrArray;

	/**
	 * �R���X�g���N�^�B
	 * @param indexDir �C���f�b�N�X���i�[����p�X
	 */
	public KifuIdIndex(Path indexDir) {
		this.indexDir = indexDir;
	}
	
	/**
	 * ����ID�C���f�b�N�X�����[�h���܂��B
	 */
	public void loadIndex() throws Exception {
		List<Integer> kifuIdList = new ArrayList<>();
		List<Long> kifuIdPtrList = new ArrayList<>();
		
		int skipCount = 0;
		try(DataInputStream kifuIdDis = IoUtils.newDataInputStream(IndexerUtils.getKifuIdPtrFilePath(indexDir))) {
			while(true) {
				// ����ID �� ����ID�t�@�C���ւ̃|�C���^�����X�g�ɓo�^
				kifuIdList.add(kifuIdDis.readInt());
				kifuIdPtrList.add(kifuIdDis.readLong());
				
				while(true) {
					if(skipCount >= KIFUID_NUM_EACH_PTR - 1){
						skipCount = 0;
						break;
					}
					
					kifuIdDis.readInt(); // ����ID��ǂݔ�΂�
					kifuIdDis.readLong(); // ����ID�t�@�C���ւ̃|�C���^��ǂݔ�΂�
					skipCount++;
				}
			}
		}
		catch(EOFException end) {
			// �ǂݍ��ݏI��
		}
		
		// ����ID�C���f�b�N�X(�I��������)�ɃZ�b�g����B
		kifuIdArray = ArrayUtils.createArrayWithIntMax(kifuIdList);
		kifuIdPtrArray = ArrayUtils.createArrayWithLongMax(kifuIdPtrList);
	}
	
	/**
	 * ����ID�C���f�b�N�X���A�����[�h���܂��B
	 */
	public void unloadIndex() {
		kifuIdArray = null;
		kifuIdPtrArray = null;
	}
}
