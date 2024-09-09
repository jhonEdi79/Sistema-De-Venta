package Principal;
// Importaciones necesarias para el funcionamiento del programa
import Util.MenuPrincipalSingleton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import tiendacocina.MenuPrincipal;

// Clase Singletow para obtener los metodos get de los jCheckBox

public class Main {
    // Metodo Main
    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        // Establece el look and feel Nimbus para la interfaz gráfica
        UIManager.setLookAndFeel(new NimbusLookAndFeel());
        // Obtiene la instancia única de MenuPrincipal a través del Singleton
        MenuPrincipal menuPrincipal = MenuPrincipalSingleton.getInstance();
        // Hacemos visible el menuPrincipal
        menuPrincipal.setVisible(true);   
    }
}
