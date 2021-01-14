package GUI;

import Enum.Alliance;
import GUI.Table.MoveLog;
import board.Move;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import pieces.Piece;

public class TakenPiecesPanel extends JPanel{
    
    private final JPanel northPanel;
    private final JPanel southPanel;
    private static final Color PANEL_COLOR = Color.decode("0xFDF5E6");
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(80, 80);
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    
    public TakenPiecesPanel(){
        super(new BorderLayout());
        setBackground(PANEL_COLOR);
        setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout(8, 2));
        this.southPanel = new JPanel(new GridLayout(8, 2));
        this.northPanel.setBackground(PANEL_COLOR);
        this.southPanel.setBackground(PANEL_COLOR);
        this.add(this.southPanel, BorderLayout.NORTH);
        this.add(this.northPanel, BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECES_DIMENSION);
    }
    
    public void redo(final MoveLog moveLog){
        
        this.northPanel.removeAll();
        this.southPanel.removeAll();

        for (Move move : moveLog.getMoves()) {
            if (move.isAttack()) {
                final Piece killedPiece = move.getAttackedPiece();
                BufferedImage icon = null;
                try {
                    String path = "icons/" + killedPiece.getPieceAlliance().toString().substring(0, 1) +
                            killedPiece.getPieceType().toString() + ".gif";
                    icon = ImageIO.read(new File(path));
                } catch (IOException e) {
                    System.out.println(e);
                }
                if (killedPiece.getPieceAlliance().equals(Alliance.WHITE)) {
                    this.southPanel.add(new JLabel(new ImageIcon(icon)));
                } else {
                    this.northPanel.add(new JLabel(new ImageIcon(icon)));
                }
            }
        }
    }
}