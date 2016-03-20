package org.petanko.ottfoekst.boardsrch.indexer;

import static org.petanko.ottfoekst.petankoshogi.board.ShogiPiece.ENEMY;
import static org.petanko.ottfoekst.petankoshogi.board.ShogiPiece.SELF;

import java.io.DataOutputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.petanko.ottfoekst.boardsrch.util.IoUtils;
import org.petanko.ottfoekst.petankoshogi.board.PieceMove;
import org.petanko.ottfoekst.petankoshogi.board.PiecePosition;
import org.petanko.ottfoekst.petankoshogi.board.ShogiPiece;
import org.petanko.ottfoekst.petankoshogi.util.ShogiUtils;

/**
 * �ǖʓ]�u�C���f�b�N�X�𐶐�����N���X�B
 * @author ottfoekst
 *
 */
public class BoardDataIndexer {

	/** �C���f�b�N�X���i�[����p�X */
	private Path indexDir;
	/** �����t�@�C�����i�[���ꂽ�p�X */
	private Path kifuDataPath;
	
	/** ����ID */
	private int kifuId = 0;
	
	/** ����ID�t�@�C���������� */
	private DataOutputStream kifuIdDos;
	/** ����ID�|�C���^�t�@�C���������� */
	private DataOutputStream kifuIdPtrDos;
	/** ����ID�t�@�C���̃|�C���^ */
	private long kifuIdFilePtr = 0;
	
	/** 1��O�̈ړ���}�X(to) */
	private int beforeTo = 0;
	
	/** �ǖʓ]�u�C���f�b�N�X�t�@�C���������� */
	private DataOutputStream[] boardInvDosList;
	/** �ǖʓ]�u�C���f�b�N�X�|�C���^�t�@�C���������� */
	private DataOutputStream[] boardInvPtrDosList;
	
	/**�@�ǖʓ]�u�C���f�b�N�X�̏������� */
	private BoardDataIndexWriter[] boardDataIndexWriterList;
	
	/** kif�t�@�C���̓������[�e�B���e�B�N���X */
	private KifFileUtils kifFileUtils = new KifFileUtils();
	
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
			// �e�퐔�l�̏�����
			initializeAllNumList();
			// �e��DataOutputStream��Writer�̏�����
			initializeDosListAndWriterList();
			
			// ��������ɓ]�u�C���f�b�N�X���\�z
			createInvIndexOnMemory(kifuDataPath.toFile());
			// �]�u�C���f�b�N�X�̏����o��
			for(int blockNo = 0; blockNo < 50; blockNo++ ) {
				boardDataIndexWriterList[blockNo].writeToIndexFile(boardInvDosList[blockNo], boardInvPtrDosList[blockNo]);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			// �e��DataOutputStream�����
			closeDosList();
		}
	}

	private void initializeAllNumList() {
		kifuId = 0;
		kifuIdFilePtr = 0;
	}

	private void initializeDosListAndWriterList() throws Exception {
		kifuIdDos = IoUtils.newDataOutputStream(IndexerUtils.getKifuIdFilePath(indexDir));
		kifuIdPtrDos = IoUtils.newDataOutputStream(IndexerUtils.getKifuIdPtrFilePath(indexDir));
		
		boardInvDosList = new DataOutputStream[50];
		boardInvPtrDosList = new DataOutputStream[50];
		boardDataIndexWriterList = new BoardDataIndexWriter[50];
		for(int blockNo = 0; blockNo < boardInvDosList.length; blockNo++) {
			boardInvDosList[blockNo] = IoUtils.newDataOutputStream(IndexerUtils.getBoardInvFilePath(indexDir, blockNo));
			boardInvPtrDosList[blockNo] = IoUtils.newDataOutputStream(IndexerUtils.getBoardInvPtrFilePath(indexDir, blockNo));
			boardDataIndexWriterList[blockNo] = new BoardDataIndexWriter();
		}
	}

	private void createInvIndexOnMemory(File kifuDataFileOrFolder) throws Exception {
		// �t�H���_�̂Ƃ�
		if(kifuDataFileOrFolder.isDirectory()) {
			File[] files = kifuDataFileOrFolder.listFiles();
			for(File file : files) {
				createInvIndexOnMemory(file);
			}
		}
		// �����t�@�C��(�g���q:kif)�̂Ƃ�
		else if(kifuDataFileOrFolder.toString().endsWith(".kif")) {
			// ����ID�|�C���^�t�@�C���ւ̏�������
			kifuIdPtrDos.writeInt(kifuId); // ����ID
			kifuIdPtrDos.writeLong(kifuIdFilePtr); // ����ID�t�@�C���̃|�C���^
			kifuIdPtrDos.flush();
			// ����ID�t�@�C���ւ̏�������
			kifuIdFilePtr += writeKifuIdFile(kifuDataFileOrFolder);
			
			// �����t�@�C����ǂݍ���
			String[] kifuDataList = Files.readAllLines(kifuDataFileOrFolder.toPath(), Charset.forName("MS932")).toArray(new String[0]);
			// �����t�@�C���̓��e�ɏ]���ĔՖʂ𓮂����A�ǖʂ��C���f�b�N�X�ɓo�^
			addBoardDataToInvIndex(kifuDataList);
			
			// ����ID�̃J�E���g�A�b�v
			kifuId++;
		}
	}

	private long writeKifuIdFile(File kifuDataFileOrFolder) throws Exception {
		// �����t�@�C����
		String kifuFileName = kifuDataFileOrFolder.getAbsolutePath();
		// �����t�@�C����������ID�t�@�C���ɏ�������
		kifuIdDos.writeChars(kifuFileName);
		kifuIdDos.flush();
		
		// �����t�@�C�����̒�����Ԃ�
		return kifuFileName.length();
	}

	private void addBoardDataToInvIndex(String[] kifuDataList) {
		// �w���胊�X�g
		PieceMove[] pieceMoveList = kifFileUtils.createPieceMoveListFromKifFile(kifuDataList);
		
		// ����̏����ǖ�
		PiecePosition piecePosition = ShogiUtils.getHiratePiecePosition();
		// �����ǖʂ��C���f�b�N�X�ɓo�^
		IntStream.range(0, 50).forEach(blockNo -> boardDataIndexWriterList[blockNo].add(kifuId, piecePosition.getTesu(), IndexerUtils.calculateBoardTokenId(piecePosition, blockNo)));
		
		for(PieceMove pieceMove : pieceMoveList) {
			// �w����ɏ]���ċǖʂ𓮂���
			piecePosition.movePiecePostion(pieceMove);
			// �e�ǖʃg�[�N�����C���f�b�N�X�ɓo�^
			IntStream.range(0, 50).forEach(blockNo -> boardDataIndexWriterList[blockNo].add(kifuId, piecePosition.getTesu(), IndexerUtils.calculateBoardTokenId(piecePosition, blockNo)));
		}
	}

	private void closeDosList() {
		// �S�Ă�DataOutputStream��A��
		DataOutputStream[] allDosList = Stream.concat(Arrays.stream(new DataOutputStream[]{kifuIdDos, kifuIdPtrDos}), 
				Stream.concat(Arrays.stream(boardInvDosList), Arrays.stream(boardInvPtrDosList))).toArray(size -> new DataOutputStream[size]);
		// Exception���X���[�����ɑS�ĕ���
		IoUtils.closeSilently(allDosList);
	}
		
	/** kif�t�@�C���̓������[�e�B���e�B�N���X */
	private class KifFileUtils {
		
		private PieceMove[] createPieceMoveListFromKifFile(String[] kifuDataList) {
			List<PieceMove> pieceMoveList = new ArrayList<PieceMove>();
			
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
}
