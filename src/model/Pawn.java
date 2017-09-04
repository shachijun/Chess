package model;

import controller.DirectionDistance;
import controller.Move.Promotion;
import controller.Outcome;

/**
 * Class for the Pawn.
 *
 * @author William Chen
 * @author Chijun Sha
 */

public class Pawn extends Piece{

    /**
     * @param isWhite Color of piece
     * @param file File of current location
     * @param rank Rank of current location
     * @param cb Chessboard
     */
    public Pawn(boolean isWhite, int file, int rank, Chessboard cb) {
        super(isWhite, file, rank, cb);
        cb.getPieceMap().put(getKey(), this);
    }

    /**
     * @param isWhite Color of piece
     * @param fileRank Input File and Rank
     * @param cb Chessboard
     */
    public Pawn(boolean isWhite, String fileRank, Chessboard cb) {
        super(isWhite, fileRank, cb);
        cb.getPieceMap().put(getKey(), this);
    }


    /**
     * @param targetIndex Target position of a move
     * @param promotion Promotion. Pawn only.
     * @return Outcome of the move.
     */
    @Override
    public Outcome doMove(BoardIndex targetIndex, Promotion promotion) {
        return doMoveInternal(targetIndex, true, false, promotion);
    }


    /**
     * @param targetIndex Target position of a move
     * @return Outcome if the piece can attack target
     */
    public Outcome doAttack(BoardIndex targetIndex) {
    	Promotion promotion;
    	promotion = Promotion.NONE;
        return doMoveInternal(targetIndex, false, false, promotion);
    }


    /**
     * @param targetIndex Target index a piece moves too
     * @param doMove True if piece perform move, false checks if piece can attack target.
     * @param rollback If true, rollback after checking move
     * @param promotion Promotion when pawn reaches correct rank.
     * @return Outcome of the move
     */
    private Outcome doMoveInternal(BoardIndex targetIndex, boolean doMove, boolean rollback, Promotion promotion) {
        Outcome outcome;
        //
        BoardIndex currentIndex = getBoardIndex();
        //
        DirectionDistance dd = new DirectionDistance(currentIndex.fileIndex, currentIndex.rankIndex, targetIndex.fileIndex, targetIndex.rankIndex);
        //
        if (dd.isPawOneStep(isWhite())) {
       		if (isTargetEmpty(targetIndex)) {
       			if (doMove) {
	                boolean isPromotion = dd.isPawnPromotion(isWhite());
	       			boolean isLegal;
	       			// Promotion
	       			if (isPromotion) {
	       				Piece promPiece;
	       				if (promotion == Promotion.Bishop) {
	       					promPiece = new Bishop(isWhite(), targetIndex.fileIndex, targetIndex.rankIndex, chessBoard);
	       				}
	       				else if (promotion == Promotion.Rook) {
	       					promPiece = new Rook(isWhite(), targetIndex.fileIndex, targetIndex.rankIndex, chessBoard);
	       				}
	       				else if (promotion == Promotion.Knight) {
	       					promPiece = new Knight(isWhite(), targetIndex.fileIndex, targetIndex.rankIndex, chessBoard);
	       				}
	       				else {
	       					promPiece = new Queen(isWhite(), targetIndex.fileIndex, targetIndex.rankIndex, chessBoard);
	       				}
	       				//
	       				isLegal = doActualMoveIsPromotion(targetIndex, promPiece, rollback);
	       			}
	       			else {
	       				isLegal = doActualMove(targetIndex, rollback);
	       			}
	       			//
                    outcome = isLegal ? new Outcome(true, "OK") : new Outcome(false, "Illegal move, try again");    // King will be in check
       			}
       			else {
           			outcome = new Outcome(true, "OK");
       			}
       		}
       		else {
       		    // Pawn illegal move one step
       			outcome = new Outcome(false, "Illegal move, try again");
       		}
        }
        else if (dd.isPawnKill(isWhite())) {
       		if (isTargetLegal(targetIndex)) {
       			if (doMove) {
	                boolean isPromotion = dd.isPawnPromotion(isWhite());
	       			boolean isLegal;
	       			//
	       			if (isPromotion) {
	       				Piece promotedPiece;
	       				if (promotion == Promotion.Bishop) {
	       					promotedPiece = new Bishop(isWhite(), targetIndex.fileIndex, targetIndex.rankIndex, chessBoard);
	       				}
	       				else if (promotion == Promotion.Rook) {
	       					promotedPiece = new Rook(isWhite(), targetIndex.fileIndex, targetIndex.rankIndex, chessBoard);
	       				}
	       				else if (promotion == Promotion.Knight) {
	       					promotedPiece = new Knight(isWhite(), targetIndex.fileIndex, targetIndex.rankIndex, chessBoard);
	       				}
	       				else {
	       					promotedPiece = new Queen(isWhite(), targetIndex.fileIndex, targetIndex.rankIndex, chessBoard);
	       				}
	       				//
	       				isLegal = doActualMoveIsPromotion(targetIndex, promotedPiece, rollback);
	       			}
	       			else {
	       				isLegal = doActualMove(targetIndex, rollback);
	       			}
	       			//
                    outcome = isLegal ? new Outcome(true, "OK") : new Outcome(false, "Illegal move, try again");     // King will be in check
       			}
       			else {
           			outcome = new Outcome(true, "OK");
       			}
       		}
       		// En passant
            else if (isTargetEmpty(targetIndex) && dd.isEnPassant(isWhite())) {
       			BoardIndex enPassantIndex = new BoardIndex(targetIndex.fileIndex, currentIndex.rankIndex);
       			if (isTargetLegalEnPassant(enPassantIndex)) {
       				if (doMove) {
       					boolean isLegal = doActualMoveIsEnPassant(targetIndex, enPassantIndex, rollback);
       					if (isLegal) {
       						outcome = new Outcome(true, "OK");
       					}
       					else {
       					    // King will be in check
       						outcome = new Outcome(false, "Illegal move, try again");
       					}
       				}
       				else {
       					outcome = new Outcome(true, "OK");
       				}
       			}
       			else {
       			    // Pawn En passant target illegal
       				outcome = new Outcome(false, "Illegal move, try again");
       			}
            }
       		else {
       		    // Pawn target illegal
       			outcome = new Outcome(false, "Illegal move, try again");
       		}
        }
        else if (dd.isPawnTwoStep(isWhite())) {
       		if (isTargetEmpty(targetIndex) && isTargetEmpty(new BoardIndex(currentIndex.fileIndex, (currentIndex.rankIndex+targetIndex.rankIndex)/2))) {
       			if (doMove) {
	       			boolean isLegal = doActualMove(targetIndex, rollback);
	       			if (isLegal) {
	           			outcome = new Outcome(true, "OK");
	       			}
	       			else {
                        // King will be in check
                        outcome = new Outcome(false, "Illegal move, try again.");
	       			}
       			}
       			else {
           			outcome = new Outcome(true, "OK");
       			}
       		}
       		else {
       		    // A piece is in the way
       			outcome = new Outcome(false, "Illegal move, try again");
       		}
        }
        else {
            outcome = new Outcome(false, "Illegal move, try again");
        }
        //
        return outcome;
    }


    /**
     * @return True if Pawn has legal move.
     */
    @Override
	public boolean hasLegalMove() {
		if (isWhite()) {
			return isLegalMove(0, 1) || isLegalMove(0, 2) || isLegalMove(1, 1) || isLegalMove(-1, 1);
		}
        return isLegalMove(0, -1) || isLegalMove(0, -2) || isLegalMove(1, -1) || isLegalMove(-1, -1);
    }

    /**
     * @param deltaFile Change in file for each step.
     * @param deltaRank Change in rank for each step.
     * @return True if legal move after change, false if illegal move.
     */
    private boolean isLegalMove(int deltaFile, int deltaRank) {
		BoardIndex currentIndex = getBoardIndex();
		boolean isLegalMove = false;
		//
		int file = currentIndex.fileIndex + deltaFile;
		int rank = currentIndex.rankIndex + deltaRank;
		//
		if (file >= 0 && file < 8 && rank >= 0 && rank < 8) {
    		BoardIndex targetIndex = new BoardIndex(file, rank);
    		//
        	Promotion promotion;
        	promotion = Promotion.NONE;
			Outcome outcome = doMoveInternal(targetIndex, true, true, promotion);
			isLegalMove = outcome.isOK();
		}
		//
		return isLegalMove;
	}
    
    
    /**
     * @return The board output for Pawn.
     */
    @Override
    public String toString() {
        return super.toString() + "p";
    }
}