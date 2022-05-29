
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kevin
 */
public class BeachAtlasConfig {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            gui.JSONSelection selection = new gui.JSONSelection();
            selection.setLocationRelativeTo(null);
            selection.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            selection.setVisible(true);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BeachAtlasConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(BeachAtlasConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BeachAtlasConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(BeachAtlasConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
