package player.AI;

import board.Board;
import board.Move;

public interface MoveStrategy {
    
    Move execute(Board board);
}
