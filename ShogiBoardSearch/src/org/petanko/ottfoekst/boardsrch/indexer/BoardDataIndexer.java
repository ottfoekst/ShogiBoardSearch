package org.petanko.ottfoekst.boardsrch.indexer;

import java.io.DataOutputStream;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.petanko.ottfoekst.boardsrch.util.IoUtils;
import org.petanko.ottfoekst.boardsrch.util.KifFileUtils;
import org.petanko.ottfoekst.petankoshogi.board.PieceMove;
import org.petanko.ottfoekst.petankoshogi.board.PiecePosition;
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
			
			// �����t�@�C���̓��e�ɏ]���ĔՖʂ𓮂����A�ǖʂ��C���f�b�N�X�ɓo�^
			addBoardDataToInvIndex(kifuDataFileOrFolder);
			
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


	private void addBoardDataToInvIndex(File kifuFile) throws Exception {
		// �w���胊�X�g
		PieceMove[] pieceMoveList = kifFileUtils.createPieceMoveListFromKifFile(kifuFile.toPath());
		
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
}
