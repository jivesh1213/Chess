package pieces;

import Enum.Alliance;
import board.Board;
import board.BoardUtils;
import board.Move;
import board.Tile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Rook extends Piece{

    private final static int[] CANDIDATE_MOVE_COORDINATES = {-8, -1, 1, 8};
    
    public Rook(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance, PieceType.ROOK, true);
    }

    public Rook(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove){
        super(piecePosition, pieceAlliance, PieceType.ROOK, isFirstMove);
    }
    
    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        
        final List<Move> legalMoves = new ArrayList<>();
    
        for(final int candidateCoordinateOffset : CANDIDATE_MOVE_COORDINATES){
            int candidateDestinationCoordinate = this.piecePosition;
            
            while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                 
                if(isFirstColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset) 
                    || isEighthColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset))
                    break;
                
                candidateDestinationCoordinate += candidateCoordinateOffset;
                
                if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                    
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                    
                    if(!candidateDestinationTile.isTileOccupied())
                        legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
                    else{
                        final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                        if(this.pieceAlliance != pieceAlliance)
                            legalMoves.add(new Move.MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                        break;
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }
    
    @Override
    public Rook movePiece(final Move move) {
        return new Rook(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }
    
    @Override
    public String toString(){
        return PieceType.ROOK.toString();
    }
    
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && candidateOffset == -1;
    }
    
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && candidateOffset == 1;
    }
}
    