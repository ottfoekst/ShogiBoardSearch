package org.petanko.ottfoekst.boardsrch.util;

import static org.petanko.ottfoekst.petankoshogi.board.ShogiPiece.ENEMY;
import static org.petanko.ottfoekst.petankoshogi.board.ShogiPiece.SELF;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.petanko.ottfoekst.petankoshogi.board.PieceMove;
import org.petanko.ottfoekst.petankoshogi.board.PiecePosition;
import org.petanko.ottfoekst.petankoshogi.board.ShogiPiece;
import org.petanko.ottfoekst.petankoshogi.util.ShogiUtils;

/**
 * kif�t�@�C���̃��[�e�B���e�B�N���X�B
 * @author ottfoekst
 *
 */
public class KifFileUtils {
	
	/** 1��O�̈ړ���}�X(to) */
	private int beforeTo = 0;
	
	/**
	 * kif�t�@�C����ǂݍ��݁A�w���胊�X�g��Ԃ��܂��Bkif�t�@�C���̕����R�[�h��Shift_JIS�Œ�ł��B
	 * @param kifuFilePath kif�t�@�C���̃p�X
	 * @return �w���胊�X�g(PieceMove[])
	 * @throws Exception
	 */
	public PieceMove[] createPieceMoveListFromKifFile(Path kifuFilePath) throws Exception {
		List<PieceMove> pieceMoveList = new ArrayList<PieceMove>();
		
		// �����t�@�C����ǂݍ���
		String[] kifuDataList = Files.readAllLines(kifuFilePath, Charset.forName("MS932")).toArray(new String[0]);
		// ����̏����ǖ�
		PiecePosition piecePosition = ShogiUtils.getHiratePiecePosition();
		// �萔
		int tesu = 1;
		for(String kifuData : kifuDataList) {
			String[] kifuAndFrom;
			// �����̏����ꂽ�s�̂Ƃ�
			if(kifuData.length() > 0 && kifuData.charAt(0) == (' ') && !kifuData.contains("����") && !kifuData.contains("���f")) {
				// �������擾
				kifuAndFrom = 
						new String[]{kifuData.substring(5, kifuData.indexOf('(')).trim(), kifuData.substring(kifuData.indexOf("(") + 1, kifuData.indexOf(")"))};
				// PieceMove�ɕϊ����ă��X�g�ɒǉ�
				PieceMove pieceMove = convertToPieceMove(kifuAndFrom, piecePosition, tesu);
				pieceMoveList.add(pieceMove);
				// PieceMove�ɏ]���ċǖʂ������߂�
				piecePosition.movePiecePostion(pieceMove);
				// �萔��1���₷
				tesu++;
			}
		}
		return pieceMoveList.toArray(new PieceMove[0]);
	}

	private PieceMove convertToPieceMove(String[] kifuAndFrom, PiecePosition piecePosition, int tesu) {
		// �A���r�A�������g���������ɕϊ�
		kifuAndFrom = convertToArabicNumKifu(kifuAndFrom);
		
		int from = kifuAndFrom[0].contains("��") ? 0 : Character.getNumericValue(kifuAndFrom[1].charAt(0)) * 0x10 + Character.getNumericValue(kifuAndFrom[1].charAt(1));
		int to = getToPosFromKifu(kifuAndFrom[0]);
		int piece = (from == 0) ? getDroppedPiece(kifuAndFrom[0], tesu) : piecePosition.getBoard()[from];
		int capturePiece = piecePosition.getBoard()[to];
		boolean isPromote = kifuAndFrom[0].endsWith("��");
		
		// beforeTo�̒u������
		beforeTo = (to / 0x10) * 10 + to % 0x10;
		
		return new PieceMove(from, to, piece, capturePiece, isPromote);
	}

	private String[] convertToArabicNumKifu(String[] kifuAndFrom) {
		return new String[]{convertToArabicNumKifu(kifuAndFrom[0]), kifuAndFrom[1]};
	}
	
	private String convertToArabicNumKifu(String kifu) {
		StringBuilder buf = new StringBuilder();
		int arabicNum = -1;
		for(int charIndex = 0; charIndex < kifu.length(); charIndex++) {
			char c = kifu.charAt(charIndex);
			// �u���v�̂Ƃ�
			if(c == '��') {
				buf.append(beforeTo);
			}
			// �S�p���� or �������̂Ƃ�
			else if((arabicNum = ShogiUtils.getArabicNum(c)) != -1) {
				buf.append(arabicNum);
			}
			// �X�y�[�X�̂Ƃ�
			else if(c == ' ' || c == '�@') {
				continue;
			}
			// ����ȊO
			else {
				buf.append(c);
			}
		}
		return buf.toString();
	}
	
	private int getToPosFromKifu(String kifu) {
		return Character.getNumericValue(kifu.charAt(0)) * 0x10 + Character.getNumericValue(kifu.charAt(1));
	}

	private int getDroppedPiece(String kifu, int tesu) {
		int piece = 0;
		for(int index = 0; index < ShogiPiece.komaStrForKind.length; index++) {
			if(kifu.indexOf(ShogiPiece.komaStrForKind[index]) != -1) {
				piece = index;
				break;
			}
		}
		int turn = (tesu % 2 == 1) ? SELF : ENEMY;
		
		return (piece&~SELF&~ENEMY)|turn;
	}
}
