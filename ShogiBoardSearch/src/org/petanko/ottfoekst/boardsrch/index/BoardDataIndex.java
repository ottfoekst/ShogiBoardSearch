package org.petanko.ottfoekst.boardsrch.index;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.petanko.ottfoekst.boardsrch.indexer.IndexerUtils;
import org.petanko.ottfoekst.boardsrch.util.ArrayUtils;
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
			boardTokenIdMatrix[blockNo] = ArrayUtils.createArrayWithLongMax(boardTokenIdList);
			boardInvPtrMatrix[blockNo] = ArrayUtils.createArrayWithLongMax(boardInvPtrList);
		}
	}
	
	/**
	 * ������blockNo�AboardTokenId�ɊY������ǖʓ]�u�C���f�b�N�X��Ԃ��܂��B
	 * @param blockNo �Ֆʃf�[�^�̂ǂ̕�����\�������������l{@see BoardDataToken}
	 * @param boardTokenId �ǖʃg�[�N��ID
	 * @return �ǖʓ]�u�C���f�b�N�X
	 * @throws Exception
	 */
	public Map<Integer, List<Integer>> getBoardDataIndex(int blockNo, long boardTokenId) throws Exception {
		Map<Integer, List<Integer>> boardDataIndexMap = new TreeMap<>((left, right) -> Integer.compare(left, right));
		
		long boardInvPtr = getBoardInvPtr(blockNo, boardTokenId);
		// �|�C���^�����݂��Ȃ��Ƃ�
		if(boardInvPtr == -1) {
			// ��̋ǖʓ]�u�C���f�b�N�X��Ԃ�
			return boardDataIndexMap;
		}
		
		// �ǖʓ]�u�C���f�b�N�X�ǂݍ���
		try(RandomAccessFile boardInvRaf = new RandomAccessFile(IndexerUtils.getBoardInvFilePath(indexDir, blockNo).toFile(), "r")) {
			boardInvRaf.seek(boardInvPtr);
			boardInvRaf.readLong(); // �ǖʃg�[�N��ID��ǂݔ�΂�
			
			int kifuCount = boardInvRaf.readInt();
			for(int kifuCountIndex = 0; kifuCountIndex < kifuCount; kifuCountIndex++) {
				int kifuId = boardInvRaf.readInt();
				int tesuNum = boardInvRaf.readInt();
				
				List<Integer> tesuList = new ArrayList<>();
				for(int tesuIndex = 0; tesuIndex < tesuNum; tesuIndex++) {
					tesuList.add(boardInvRaf.readInt());
				}
				
				boardDataIndexMap.put(kifuId, tesuList);
			}
		}
		
		return boardDataIndexMap;
	}

	private long getBoardInvPtr(int blockNo, long boardTokenId) throws Exception {
		
		/** 1. �ǂ̋ǖʓ]�u�C���f�b�N�X(�I��������)����ǂ݂��߂΂悢����T������ */
		int boardTokenIdIndex = 0;
		while(boardTokenIdIndex < boardTokenIdMatrix[blockNo].length) {
			if(boardTokenId == boardTokenIdMatrix[blockNo][boardTokenIdIndex]) {
				return boardInvPtrMatrix[blockNo][boardTokenIdIndex];
			}
			else if(boardTokenId < boardTokenIdMatrix[blockNo][boardTokenIdIndex]) {
				// �C���f�b�N�X��1�O�ɖ߂�
				boardTokenIdIndex = boardTokenIdIndex - 1;
				break;
			}
			boardTokenIdIndex++;
		}
		
		/** 2. boardTokenId�ɊY������]�u�C���f�b�N�X�̃|�C���^��T������ */
		long boardInvPtr = -1;
		try(RandomAccessFile boardInvPtrRaf = new RandomAccessFile(IndexerUtils.getBoardInvPtrFilePath(indexDir, blockNo).toFile(), "r")) {
			boardInvPtrRaf.seek(8 * 2 * BOARDTOKEN_NUM_EACH_PTR * boardTokenIdIndex);
			// �ǖʓ]�u�C���f�b�N�X�̃|�C���^���擾
			int readCount = 0;
			while(readCount < BOARDTOKEN_NUM_EACH_PTR) {
				long currentBoardTokenId = boardInvPtrRaf.readLong();
				long currentBoardInvPtr = boardInvPtrRaf.readLong();
				
				if(boardTokenId == currentBoardTokenId) {
					boardInvPtr = currentBoardInvPtr;
					break;
				}
				readCount++;
			}
		}
		catch(EOFException e) {
			// �t�@�C�������ɓ��B
		}
		
		return boardInvPtr;
	}
	
	/**
	 * �ǖʓ]�u�C���f�b�N�X���A�����[�h���܂��B
	 */
	public void unloadIndex() {
		boardTokenIdMatrix = null;
		boardInvPtrMatrix = null;
	}
}
