package model;

import controller.DirectionDistance;
import controller.Move.Promotion;
import controller.Outcome;

/**
 * Class for the Rook.
 *
 * @author William Chen
 * @author Chijun Sha
 */

public class Rook extends Piece {

    /**
     * @param isWhite Color of piece.
     * @param file File of current location.
     * @param rank Rank of current location.
     * @param cb Chessboard
     */
    public Rook(boolean isWhite, int file, int rank, Chessboard cb) {
        super(isWhite, file, rank, cb);
        cb.getPieceMap().put(getKey(), this);
    }

    /**
     * @param isWhite Color of piece
     * @param fileRank Input File and Rank
     * @param cb Chessboard
     */
    public Rook(boolean isWhite, String fileRank, Chessboard cb) {
        super(isWhite, fileRank, cb);
        cb.getPieceMap().put(getKey(), this);
    }


    /**
     * @param targetIndex Target index a piece moves too.
     * @param promotion Promotion. Only valid for pawn.
     * @return Outcome of the move.
     */
    public Outcome doMove(BoardIndex targetIndex, Promotion promotion) {
        return doMoveInternal(targetIndex, true);
    }

    /**
     * @param targetIndex Target index a piece moves too.
     * @return Outcome if the piece can attack target
     */
    public Outcome doAttack(BoardIndex targetIndex) {
        return doMoveInternal(targetIndex, false);
    }

    /**
     * @param targetIndex Target index a piece moves too.
     * @param doMove True if piece perform move. False checks if piece can attack target.
     * @return Outcome of the move.
     */
    private Outcome doMoveInternal(BoardIndex targetIndex, boolean doMove) {
        Outcome outcome;
        //
        BoardIndex currentIndex = getBoardIndex();
        //
        DirectionDistance dd = new DirectionDistance(currentIndex.fileIndex, currentIndex.rankIndex, targetIndex.fileIndex, targetIndex.rankIndex);
        //
        if (dd.isParallel()) {
            if (isTargetEmptyOrLegal(targetIndex)) {
                if (isNoneInBetween(dd)) {
                    if (doMove) {
                        boolean isLegal = doActualMove(targetIndex, false);
                        if (isLegal) {
                            outcome = new Outcome(true, "OK");
                        }
                        else {
                            // King in check, try again
                            outcome = new Outcome(false, "Illegal move, try again");
                        }
                    }
                    else {
                        outcome = new Outcome(true, "OK");
                    }
                }
                else {
                    // Piece(s) in between, try again
                    outcome = new Outcome(false, "Illegal move, try again");
                }
            }
            else {
                // Wrong target, try again
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
     * @return True if Rook has legal move. For checkmate and stalemate.
     */
    public boolean hasLegalMove() {
		return hasLegalMoveOneDirection(0, 1)
                || hasLegalMoveOneDirection(0, -1)
                || hasLegalMoveOneDirection(1, 0)
                || hasLegalMoveOneDirection(-1, 0);
    }
    
    
    /**
     * @return The board output for the Rook.
     */
    @Override
    public String toString() {
        return super.toString() + "R";
    }
}