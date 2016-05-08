package org.petanko.ottfoekst.boardsrch.index;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.RandomAccessFile;
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
	
	/**
	 * �����t�@�C���p�X�̊Ԃ̃f���~�^�B
	 */
	public static final char KIFU_FILEPATH_DELIM = 0x00;
	
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
	 * �w�肵������ID���������t�@�C���̃p�X��Ԃ��܂��B
	 * @param kifuId ����ID
	 * @return �����t�@�C���̃p�X
	 * @throws Exception 
	 */
	public String getKifuFilePath(int kifuId) throws Exception {
		
		long kifuIdPtr = getKifuIdPtr(kifuId);
		// �|�C���^�����݂��Ȃ��Ƃ�
		if(kifuIdPtr == -1) {
			// �󕶎���Ԃ�
			return "";
		}
		
		// ����ID�C���f�b�N�X�ǂݍ���
		StringBuilder buf = new StringBuilder();
		try(RandomAccessFile kifuIdRaf = new RandomAccessFile(IndexerUtils.getKifuIdFilePath(indexDir).toFile(), "r")) {
			kifuIdRaf.seek(kifuIdPtr);
			
			char c;
			while((c = kifuIdRaf.readChar()) != KifuIdIndex.KIFU_FILEPATH_DELIM) {
				buf.append(c);
			}
		}
		
		return buf.toString();
	}
	
	private long getKifuIdPtr(int kifuId) throws Exception {
		
		/** 1. �ǂ̋ǖʓ]�u�C���f�b�N�X(�I��������)����ǂ݂��߂΂悢����T������ */
		int kifuIdIndex = 0;
		while(kifuIdIndex < kifuIdArray.length) {
			if(kifuId == kifuIdArray[kifuIdIndex]) {
				return kifuIdPtrArray[kifuIdIndex];
			}
			else if(kifuId < kifuIdArray[kifuIdIndex]) {
				// �C���f�b�N�X��1�O�ɖ߂�
				kifuIdIndex = kifuIdIndex - 1;
				break;
			}
			kifuIdIndex++;
		}
		
		/** 2. boardTokenId�ɊY������]�u�C���f�b�N�X�̃|�C���^��T������ */
		long kifuIdPtr = -1;
		try(RandomAccessFile kifuIdPtrRaf = new RandomAccessFile(IndexerUtils.getKifuIdPtrFilePath(indexDir).toFile(), "r")) {
			kifuIdPtrRaf.seek((4 + 8) * KIFUID_NUM_EACH_PTR * kifuIdIndex);
			// �ǖʓ]�u�C���f�b�N�X�̃|�C���^���擾
			int readCount = 0;
			while(readCount < KIFUID_NUM_EACH_PTR) {
				long currentKifuId = kifuIdPtrRaf.readInt();
				long currentKifuIdPtr = kifuIdPtrRaf.readLong();
				
				if(kifuId == currentKifuId) {
					kifuIdPtr = currentKifuIdPtr;
					break;
				}
				readCount++;
			}
		}
		catch(EOFException e) {
			// �t�@�C�������ɓ��B
		}
		
		return kifuIdPtr;
	}

	/**
	 * ����ID�C���f�b�N�X���A�����[�h���܂��B
	 */
	public void unloadIndex() {
		kifuIdArray = null;
		kifuIdPtrArray = null;
	}
}
