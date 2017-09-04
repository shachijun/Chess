package model;

import controller.DirectionDistance;
import controller.Move.Promotion;
import controller.Outcome;

import java.util.Map;

/**
 * Parent Class of all the pieces.
 *
 * @author William Chen
 * @author Chijun Sha
 */

public abstract class Piece {

    /**
     * Position of a piece.
     */
    private BoardIndex 				boardIndex 	= null;

    /**
     * Color of a piece.
     */
    private boolean 				isWhite 	= false;

    /**
     * Checks if piece is previously moved. Needed for castling.
     */
    private boolean 				isMoved		= false;

    /**
     * Chessboard the piece belongs too.
     */
    protected Chessboard 			chessBoard	= null;


    /**
     * @param _isWhite True if piece is white
     * @param file File
     * @param rank Rank
     * @param cb Chessboard
     */
    protected Piece(boolean _isWhite, int file, int rank, Chessboard cb) {
        boardIndex = new BoardIndex(file, rank);
	    isWhite = _isWhite;
	    chessBoard = cb;
    }

    /**
     * @param _isWhite True if piece is white
     * @param fileRank Input File and Rank
     * @param cb Chessboard
     */
    protected Piece(boolean _isWhite, String fileRank, Chessboard cb) {
        char charFile = fileRank.toUpperCase().trim().charAt(0);
        char charRank = fileRank.toUpperCase().trim().charAt(1);
        //
        boardIndex = new BoardIndex(charFile-'A', charRank - '1');
	    isWhite = _isWhite;
	    chessBoard = cb;
    }


    /**
     * @return True if piece is previously moved.
     */
    public boolean isMoved() {
        return isMoved;
    }

    
    /**
     * @return True if piece is white.
     */
    public boolean isWhite() {
        return isWhite;
    }


    /**
     * @return Position of a piece.
     */
    public BoardIndex getBoardIndex() {
        return boardIndex;
    }


    /**
     * @param input Position of a piece
     */
    protected void setBoardIndex(BoardIndex input) {
        boardIndex = input;
    }


    /**
     * @param targetIndex Target position of a move
     * @param promotion Promotion. Pawn only.
     * @return Outcome of the move
     */
    abstract public Outcome doMove(BoardIndex targetIndex, Promotion promotion);


    /**
     * @param targetIndex Target position of a move
     * @return If this piece can attack target.
     */
    abstract public Outcome doAttack(BoardIndex targetIndex);


    /**
     * @return True if piece has legal move
     */
    abstract public boolean hasLegalMove();


    /**
     * @param targetIndex Target position of a move
     * @param rollback If true, rollback after checking move. Used for checking checkmate and stalemate.
     * @return True if piece can move. False if move is illegal as King is in check after move.
     */
    protected boolean doActualMove(BoardIndex targetIndex, boolean rollback) {
        Map<String, Piece> pieceMap = chessBoard.getPieceMap();
    	//
        BoardIndex sourceIndex = getBoardIndex();
    	Piece target = pieceMap.get(targetIndex.getKey());
    	//
        pieceMap.remove(getKey());
        setBoardIndex(targetIndex);
        pieceMap.put(getKey(), this);
        //
        Piece king =  isWhite() ? chessBoard.getWhiteKing() : chessBoard.getBlackKing();
        boolean isKingUnderAttack = chessBoard.isUnderAttack(isWhite(), king.getBoardIndex());
        //
        if (isKingUnderAttack || rollback) {
            pieceMap.remove(getKey());
            setBoardIndex(sourceIndex);
            pieceMap.put(getKey(), this);
            //
            if (target!=null) {
                pieceMap.put(target.getKey(), target);
            }
        }
        else if (!isMoved) {
            isMoved = true;
        }
        //
        return !isKingUnderAttack;
    }


    /**
     * @param targetIndex Target position of a move
     * @param rookSourceIndex Current position of rook
     * @param rookTargetIndex Target position of rook
     * @return True if King and Rook can castle. Always return true as King will not be under attack when castling begins.
     */
    protected boolean doActualMoveCastling(BoardIndex targetIndex, BoardIndex rookSourceIndex, BoardIndex rookTargetIndex) {
        Map<String, Piece> pieceMap = chessBoard.getPieceMap();
        //
        pieceMap.remove(getKey());
        setBoardIndex(targetIndex);
        pieceMap.put(getKey(), this);
        //
        Piece rook = pieceMap.remove(rookSourceIndex.getKey());
        rook.setBoardIndex(rookTargetIndex);
        pieceMap.put(rook.getKey(), rook);
        //
        return true;
    }


    /**
     * @param targetIndex Target position of a move
     * @param enPassantIndex Index of pawn to be removed after en passant.
     * @param rollback True if rollback is needed
     * @return True if pawn can en passant
     */
    protected boolean doActualMoveIsEnPassant(BoardIndex targetIndex, BoardIndex enPassantIndex, boolean rollback) {
        Map<String, Piece> pieceMap = chessBoard.getPieceMap();
    	//
        BoardIndex sourceIndex = getBoardIndex();
        //
        pieceMap.remove(getKey());
        setBoardIndex(targetIndex);
        pieceMap.put(getKey(), this);
        //
        Piece killedPaw = pieceMap.get(enPassantIndex.getKey());
        pieceMap.remove(enPassantIndex.getKey());
        //
        Piece king = isWhite() ? chessBoard.getWhiteKing() : chessBoard.getBlackKing();
        boolean isKingUnderAttack = chessBoard.isUnderAttack(isWhite(), king.getBoardIndex());
        //
        if (isKingUnderAttack || rollback) {
            pieceMap.remove(getKey());
            setBoardIndex(sourceIndex);
            pieceMap.put(getKey(), this);
            //
            pieceMap.put(killedPaw.getKey(), killedPaw);
        }
        else if (!isMoved) {
            isMoved = true;
        }
        //
        return !isKingUnderAttack;
    }


    /**
     * @param targetIndex Target position of a move
     * @param promToPiece Promoted piece
     * @param rollback True if rollback is needed
     * @return True if promotion is successful
     */
    protected boolean doActualMoveIsPromotion(BoardIndex targetIndex, Piece promToPiece, boolean rollback) {
        Map<String, Piece> pieceMap = chessBoard.getPieceMap();
    	//
    	//
        Piece target = pieceMap.get(targetIndex.getKey());
    	//
        pieceMap.remove(getKey());
        pieceMap.put(promToPiece.getKey(), promToPiece);
        //
        Piece king = isWhite() ? chessBoard.getWhiteKing() : chessBoard.getBlackKing();
        boolean isKingUnderAttack = chessBoard.isUnderAttack(isWhite(), king.getBoardIndex());
        //
        if (isKingUnderAttack || rollback) {
            pieceMap.put(getKey(), this);
            //
            if (target!=null) {
                pieceMap.put(target.getKey(), target);
            }
            else {
            	pieceMap.remove(targetIndex.getKey());
            }
        }
        else if (!isMoved) {
            isMoved = true;
        }
        //
        return !isKingUnderAttack;
    }

    /**
     * @return Board output for white and black pieces.
     */
    @Override
    public String toString() {
        return isWhite ? "w" : "b";
    }

    /**
     * @return Key for the map of pieces.
     */
    public String getKey() {
        return "" + boardIndex.fileIndex + "-" + boardIndex.rankIndex;
    }

    /**
     * @param targetIndex Target position of a move
     * @return True if target is empty or an opponent piece
     */
    public boolean isTargetEmptyOrLegal(BoardIndex targetIndex) {
        Piece targetPiece = chessBoard.getPieceMap().get(targetIndex.getKey());
        //
        // Target square has no piece or has piece of opposite color
        return (targetPiece == null) || (isWhite()==!targetPiece.isWhite());
    }


    /**
     * @param targetIndex Target position of a move
     * @return True if target is empty
     */
    public boolean isTargetEmpty(BoardIndex targetIndex) {
        Piece targetPiece = chessBoard.getPieceMap().get(targetIndex.getKey());
        //
        // Target square has no piece
        return targetPiece == null;
    }

    /**
     * @param targetIndex Target position of a move
     * @return True if target is an opponent piece
     */
    public boolean isTargetLegal(BoardIndex targetIndex) {
        Piece targetPiece = chessBoard.getPieceMap().get(targetIndex.getKey());
        //
        // Target square has no piece or has piece of opposite color
        return (targetPiece != null) && (isWhite()==!targetPiece.isWhite());
    }


    /**
     * @param targetIndex Target position of a move
     * @return True if en passant target is present
     */
    public boolean isTargetLegalEnPassant(BoardIndex targetIndex) {
        Piece targetPiece = chessBoard.getPieceMap().get(targetIndex.getKey());
        //
        Piece LastTwoStepMovePaw = chessBoard.getLastTwoStepMovePawn();
        //
        return (targetPiece != null) && targetPiece==LastTwoStepMovePaw;
    }


    /**
     * @param dd Direction and Distance
     * @return True if no pieces are between this piece and target
     */
    protected boolean isNoneInBetween(DirectionDistance dd) {
        Map<String, Piece> pieceMap = chessBoard.getPieceMap();
    	boolean out = true;
    	//
        for (int i = 1; i < dd.getDistance(); i++) {
        	int deltaFile = dd.getDeltaFile();
        	int deltaRank = dd.getDeltaRank();
            BoardIndex oneIndex = new BoardIndex(boardIndex.fileIndex + i * deltaFile, boardIndex.rankIndex + i * deltaRank);
            //
            Piece onePiece = pieceMap.get(oneIndex.getKey());
            if (onePiece!=null) {
            	out = false;
                break;
            }
        }
    	//
    	return out;
    }

    /**
     * @param deltaFile Change in file for each step
     * @param deltaRank Change in rank for each step
     * @return True if there is at least 1 legal move along 1 direction
     */
    protected boolean hasLegalMoveOneDirection(int deltaFile, int deltaRank) {
    	BoardIndex sourceIndex = getBoardIndex();
    	//
        Map<String, Piece> pieceMap = chessBoard.getPieceMap();
		boolean hasLegalMove = false;
		//
    	for (int file = sourceIndex.fileIndex + deltaFile, rank = sourceIndex.rankIndex + deltaRank; (file >= 0 && file < 8 && rank >= 0 && rank < 8); file = file + deltaFile, rank = rank + deltaRank) {
    		BoardIndex targetIndex = new BoardIndex(file, rank);
    		//
    		Piece targetPiece = pieceMap.get(targetIndex.getKey());
    		if (targetPiece != null && targetPiece.isWhite() == isWhite()) {
    			break;
    		}
            boolean isLegalMove = doActualMove(targetIndex, true);
            //
            if (isLegalMove) {
                hasLegalMove = true;
                break;
            }
        }
    	//
    	return hasLegalMove;
    }


    /**
     * @param deltaFile Change in file for each step
     * @param deltaRank Change in rank for each step
     * @return True if is legal for 1 step in 1 direction
     */
    protected boolean hasLegalMoveOneDirectionOneStep(int deltaFile, int deltaRank) {
    	BoardIndex sourceIndex = getBoardIndex();
    	//
        Map<String, Piece> pieceMap = chessBoard.getPieceMap();
		boolean hasLegalMove = false;
		//
    	for (int file=sourceIndex.fileIndex+deltaFile, rank=sourceIndex.rankIndex+deltaRank; (file>=0 && file<8 && rank>=0 && rank<8); file=100, rank=-100) {
    		BoardIndex targetIndex = new BoardIndex(file, rank);
    		//
    		Piece targetPiece = pieceMap.get(targetIndex.getKey());
    		if (targetPiece != null && targetPiece.isWhite() == isWhite()) {
    			break;
    		}
            boolean isLegalMove = doActualMove(targetIndex, true);
            //
            if (isLegalMove) {
                hasLegalMove = true;
                break;
            }
        }
    	//
    	return hasLegalMove;
    }
}