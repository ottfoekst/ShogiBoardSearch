package org.petanko.ottfoekst.boardsrch.indexer;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * �ǖʓ]�u�C���f�b�N�X�̏����o�����s���N���X�B
 * @author ottfoekst
 *
 */
public class BoardDataIndexWriter {
	
	/**�@�ǖʃg�[�N��ID�̃��X�g�@*/
	private List<Long> boardTokenIdList = new ArrayList<>();
	
	/**�@�e�ǖʃg�[�N��ID�̃|�X�e�B���O���X�g��ێ�����}�b�v�@*/
	private Map<Long, List<String>> postingListMap = new HashMap<>();
	
	/**
	 * �R���X�g���N�^�B
	 */
	public BoardDataIndexWriter() {
		
	}
	
	/**
	 * �|�X�e�B���O����������̓]�u�C���f�b�N�X�ɒǉ�����B
	 * @param kifuId ����ID
	 * @param tesu �萔
	 * @param boardTokenId�@�ǖʃg�[�N��ID
	 */
	public void add(int kifuId, int tesu, long boardTokenId) {
		// ���߂ēo�^����ǖʃg�[�N��ID�̂Ƃ�
		if(!postingListMap.containsKey(boardTokenId)) {
			List<String> postingList = new ArrayList<>();
			postingList.add(String.valueOf(kifuId) + "," + String.valueOf(tesu));
			// �|�X�e�B���O���X�g�A�ǖʃg�[�N��ID���X�g�ɐV�K�o�^
			postingListMap.put(boardTokenId, postingList);
			boardTokenIdList.add(boardTokenId);
		}
		// ����܂łɓo�^�������Ƃ̂���ǖʃg�[�N��ID�̂Ƃ�
		else {
			postingListMap.get(boardTokenId).add(String.valueOf(kifuId) + "," + String.valueOf(tesu));
		}
	}
	
	/**
	 * �ǖʓ]�u�C���f�b�N�X���������ށB
	 * @param boardInvDos �ǖʓ]�u�C���f�b�N�X�������ݗpDataOutputStream
	 * @param boardInvPtrDos �ǖʓ]�u�C���f�b�N�X�|�C���^�t�@�C���������ݗpDataOutputStream
	 */
	public void writeToIndexFile(DataOutputStream boardInvDos, DataOutputStream boardInvPtrDos) throws Exception {
		
		// �ǖʓ]�u�C���f�b�N�X�̃|�C���^
		long boardInvPtr = 0;
		
		// �ǖʃg�[�N��ID�̏����ɕ��ёւ��A���̏��ŃC���f�b�N�X�t�@�C���ɏ�������
		Collections.sort(boardTokenIdList);
		for(long boardTokenId : boardTokenIdList) {
			// �ǖʓ]�u�C���f�b�N�X�|�C���^�t�@�C���ւ̏�������
			boardInvPtrDos.writeLong(boardTokenId); // �ǖʃg�[�N��ID
			boardInvPtrDos.writeLong(boardInvPtr); // �ǖʓ]�u�C���f�b�N�X�̃|�C���^
			boardInvPtrDos.flush();
			
			// �ǖʓ]�u�C���f�b�N�X�t�@�C���ւ̏�������
			boardInvPtr += writeBoardInvFile(boardInvDos, boardTokenId);
		}
		
		// �㏈��
		boardTokenIdList.clear();
		boardTokenIdList = null;
		postingListMap.clear();
		postingListMap = null;
	}

	private long writeBoardInvFile(DataOutputStream boardInvDos, long boardTokenId) throws Exception {
		// �������݃T�C�Y
		long writeSize = 0;
		
		// �ǖʃg�[�N��ID�̏�������
		boardInvDos.writeLong(boardTokenId);
		writeSize += 8;
		
		// �ǖʃg�[�N��ID�̃|�X�e�B���O���X�g���擾
		List<String> postingList = postingListMap.get(boardTokenId);
		// �o���������̏�������
		boardInvDos.writeInt(getKifuCount(postingList));
		writeSize += 4;
		
		// �|�X�e�B���O���X�g�̏�������
		int postingIndex = 0;
		int beforeKifuId = -1;
		while(postingIndex < postingList.size()) {
			// �萔���X�g�̐���
			List<Integer> tesuList = new ArrayList<>();
			int kifuId = 0;
			while(postingIndex < postingList.size()) {
				kifuId = Integer.parseInt(postingList.get(postingIndex).split(",")[0]);
				// ���� or 1�O�Ɠ�������ID�̂Ƃ�
				if(kifuId == -1 || kifuId == beforeKifuId) {
					// �萔���X�g�ɒǉ�
					tesuList.add(Integer.parseInt(postingList.get(postingIndex).split(",")[1]));
					postingIndex++;
				}
				// �قȂ����ID���o�Ă����Ƃ�
				else {
					beforeKifuId = kifuId;
					// ���[�v�𔲂���
					break;
				}
			}
			
			// ����ID�̏o��
			boardInvDos.writeInt(kifuId);
			writeSize += 4;
			// �o���萔�̏�������
			boardInvDos.writeInt(tesuList.size());
			writeSize += 4;
			// �萔���X�g�̏�������
			for(int tesu : tesuList) {
				boardInvDos.writeInt(tesu);
				writeSize += 4;
			}
			
			postingIndex++;
		}
		
		return writeSize;
	}

	// ������ƌ����������ǎd���Ȃ�
	private int getKifuCount(List<String> postingList) {
		// �o��������
		int kifuCount = 0;
		
		// 1�O�̏o������ID
		int beforeKifuId = -1;
		for(String posting : postingList) {
			int kifuId = Integer.parseInt(posting.split(",")[0]);
			if(kifuId != beforeKifuId) {
				kifuCount++;
				beforeKifuId = kifuId;
			}
		}
		return kifuCount;
	}
}
