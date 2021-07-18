package GUI;

import board.Board;
import board.BoardUtils;
import board.Move;
import board.Tile;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;
import static javax.swing.SwingUtilities.invokeLater;
import pieces.Piece;
import player.AI.Minimax;
import player.AI.MoveStrategy;
import player.MoveTransition;

public class Table extends Observable{
    
    private final BoardPanel boardPanel;
    private Board chessBoard;
    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;
    
    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;
    private boolean highlightLegalMoves;
    
    private Move computerMove;
    
    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(800, 800);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(500, 450);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private static String defaultPieceImagesPath = "icons/";
    
    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");
    
    private static final Table INSTANCE = new Table();
    
    private Table(){
        this.gameFrame = new JFrame("Chess");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard = Board.createStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAIWatcher());
        this.gameSetup = new GameSetup(this.gameFrame, true);
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMoves = false;
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.setVisible(true);
        this.gameFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static Table get(){
        return INSTANCE;
    }
    
    public void show(){
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }
    
    private GameSetup getGameSetup(){
        return this.gameSetup;
    }
    
    private Board getGameBoard(){
        return this.chessBoard;
    }
    
    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferenceMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener((ActionEvent e) -> {
            System.out.println("Open up that pgn file!");
        });
        
        fileMenu.add(openPGN);
        final JMenuItem exitMenuItem  = new JMenuItem("Exit");
        
        exitMenuItem.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });
        
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }
    
    private JMenu createPreferenceMenu(){
        
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener((ActionEvent e) -> {
            boardDirection = boardDirection.opposite();
            boardPanel.drawBoard(chessBoard);
        });
        preferencesMenu.add(flipBoardMenuItem);
        preferencesMenu.addSeparator();
        
        final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Highlight legal Moves", false);
        
        legalMoveHighlighterCheckbox.addActionListener((ActionEvent e) -> {
            highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected();
        });
        
        preferencesMenu.add(legalMoveHighlighterCheckbox);
        return preferencesMenu;
    }
    
    private JMenu createOptionsMenu(){
        
        final JMenu optionsMenu = new JMenu("Options");
        final JMenuItem setupGameMenuItem = new JMenuItem("SetupGame");
        setupGameMenuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });
        
        optionsMenu.add(setupGameMenuItem);
        
        return optionsMenu;
    }
    
    private void setupUpdate(final GameSetup gameSetup){
        setChanged();
        notifyObservers(gameSetup);
    }
    
    private static class TableGameAIWatcher implements Observer{

        @Override
        public void update(Observable o, Object arg) {
            
            if(Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
                    !Table.get().getGameBoard().currentPlayer().isInCheckMate() &&
                    !Table.get().getGameBoard().currentPlayer().isInStaleMate()){
                
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }
            
            if(Table.get().getGameBoard().currentPlayer().isInCheckMate()){
                //JOptionPane.sho
                System.out.println("game over, " + Table.get().getGameBoard().currentPlayer() + " is in checkmate!");
            }
            
            if(Table.get().getGameBoard().currentPlayer().isInStaleMate()){
                System.out.println("game over, " + Table.get().getGameBoard().currentPlayer() + " is in stalemate!");
            }
        }
    }
    
    public void updateComputerMove(Move bestMove){
        this.computerMove = bestMove;
    }
    
    public void updateGameBoard(Board board){
        this.chessBoard = board;
    }
    
    private MoveLog getMoveLog(){
        return this.moveLog;
    }
    
    private  GameHistoryPanel getGameHistoryPanel(){
        return this.gameHistoryPanel;
    }
    
    private TakenPiecesPanel getTakenPiecesPanel(){
        return this.takenPiecesPanel;
    }
    
    private BoardPanel getBoardPanel(){
        return this.boardPanel;
    }
    
    private void moveMadeUpdate(final PlayerType playerType){
        setChanged();
        notifyObservers(playerType);
    }
    
    private static class AIThinkTank extends SwingWorker<Move, String>{
        
        private AIThinkTank(){
            
        }
        
        @Override
        protected Move doInBackground() throws Exception{
            final MoveStrategy miniMax = new Minimax(4);
            final Move bestMove = miniMax.execute(Table.get().getGameBoard());
            
            return bestMove;
        }
        
        @Override
        public void done(){
            
            try{
                final Move bestMove = get();
                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public enum BoardDirection{
        NORMAL{
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles){
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED{

            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                Collections.reverse(boardTiles);
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
            
        };
    
        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    
    }

    private class BoardPanel extends JPanel{
        final List<TilePanel> boardTiles;
        
        BoardPanel(){
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<>();
            for(int i = 0; i < 64; i++){
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }
        
        public void drawBoard(final Board board){
            removeAll();
            for(final TilePanel tilePanel : boardDirection.traverse(boardTiles)){
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }
    
    public static class MoveLog{
        
        private final List<Move> moves;
        
        MoveLog(){
            this.moves = new ArrayList<>();
        }
        
        public List<Move> getMoves(){
            return this.moves;
        }
        
        public void addMove(Move move){
            this.moves.add(move);
        }
        
        public int size(){
            return this.moves.size();
        }
        
        public void clear(){
            this.moves.clear();
        }
        
        public Move removeMove(int index){
            return this.moves.remove(index);
        }
        
        public boolean removeMove(final Move move){
            return this.moves.remove(move);
        }
    }

    enum PlayerType{
        HUMAN, COMPUTER
    }
    
    private class TilePanel extends JPanel{
        
        private final int tileId;
        
        TilePanel(final BoardPanel boardPanel, final int tileId){
            super(new GridBagLayout());
            
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);
            
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if(isRightMouseButton(e)){
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    }else if(isLeftMouseButton(e)){
                        if(sourceTile == null){
                            // first click
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if(humanMovedPiece == null){
                                sourceTile = null;
                            }
                        }else{
                            //second click
                            destinationTile = chessBoard.getTile(tileId);
                            final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if(transition.getMoveStatus().isDone()){
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        invokeLater(() -> {
                            gameHistoryPanel.redo(chessBoard, moveLog);
                            takenPiecesPanel.redo(moveLog);
                            
                            if(gameSetup.isAIPlayer(chessBoard.currentPlayer())){
                                Table.get().moveMadeUpdate(PlayerType.HUMAN);
                            }
                            boardPanel.drawBoard(chessBoard);
                        });
                    }
                }
                
                @Override
                public void mousePressed(final MouseEvent e) {
                    
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                    
                }

                @Override
                public void mouseEntered(final MouseEvent e) {
                    
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    
                }
            });
            
            validate();
        }   

        public void drawTile(final Board board){
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegals(board);
            validate();
            repaint();
        }
        
        private void highlightLegals(final Board board){
            if(highlightLegalMoves){
                for(final Move move : pieceLegalMoves(board)){
                    if(move.getDestinationCoordinate() == this.tileId){
                        try{
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("icons/green_dot.png")))));
                        }catch(Exception ex){
                            System.out.println(ex);
                        }
                    }
                }
            }
        }
        
        private Collection<Move> pieceLegalMoves(final Board board){
            if(humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()){
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }
        
        private void assignTilePieceIcon(final Board board){
            this.removeAll();
            if(board.getTile(this.tileId).isTileOccupied()){
                try {
                    final BufferedImage image = ImageIO.read(new File(defaultPieceImagesPath + board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0, 1) + board.getTile(this.tileId).getPiece().toString() + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException ex) {
                    Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        private void assignTileColor() {
            if(BoardUtils.FIRST_ROW[this.tileId] || BoardUtils.THIRD_ROW[this.tileId] || BoardUtils.FIFTH_ROW[this.tileId] || BoardUtils.SEVENTH_ROW[this.tileId]){
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            }
            else if(BoardUtils.SECOND_ROW[this.tileId] || BoardUtils.FOURTH_ROW[this.tileId] || BoardUtils.SIXTH_ROW[this.tileId] || BoardUtils.EIGHTH_ROW[this.tileId]){
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }
    }
}