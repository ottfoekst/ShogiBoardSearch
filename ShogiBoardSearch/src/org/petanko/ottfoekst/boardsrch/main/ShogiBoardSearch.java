package org.petanko.ottfoekst.boardsrch.main;

import java.io.File;
import java.nio.file.Path;

import org.petanko.ottfoekst.boardsrch.searcher.BoardDataSearcher;
import org.petanko.ottfoekst.boardsrch.searcher.SearchUtils;

public class ShogiBoardSearch {

	/**
	 * ShogiBoardSearch�̃��C���N���X�B
	 * @param args[0] �C���f�b�N�X���i�[���ꂽ�p�X
	 * @param args[1] �����t�@�C���̃t���p�X
	 * @param args[2] �萔
	 * @param args[3] ��������(0:��v�ǖʌ����A1:�ގ��ǖʌ���)
	 * @param args[4] �ގ��ǖʌ����̃X�R�A臒l(0�`100)
	 */
	public static void main(String[] args) {

		try {
			// �����̕ϊ�
			Path indexDir = new File(args[0]).toPath();
			Path kifuFilePath = new File(args[1]).toPath();
			int tesu = Integer.parseInt(args[2]);
			int searchType = Integer.parseInt(args[3]);
			int similarSearchThreshold = Integer.parseInt(args[4]);

			// ��������
			BoardDataSearcher boardDataSearcher = new BoardDataSearcher(indexDir);
			// ����
			boardDataSearcher.searchBoardData(SearchUtils.convertToPiecePosition(kifuFilePath, tesu), searchType != 1, similarSearchThreshold);

			// �����I��
			boardDataSearcher.shutDown();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
