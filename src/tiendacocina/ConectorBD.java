
package tiendacocina;


// [ conexion a la base de datos ] 
// descargamos la ultima version del mysql conector.jar
// importamos toda la libreria de sql 
import java.sql.*;
import javax.swing.JOptionPane;


// creamos la clase conectorBD
public class ConectorBD {
    
    // creamos un metodo estatico que obtiene y devuelve la conexion a mysql
    public static Connection obtenerConexion() {
        try {
           // inicializa los Drives jdbc
           Class.forName("com.mysql.cj.jdbc.Driver");
           // obtenemos la conexion desde driver manager.getconnection   //"jdbc:mysql://localhost:3306/jhon_tienda_cocina", "root", ""
           Connection conectar=DriverManager.getConnection("jdbc:mysql://localhost:3306/jhon_tienda_cocina", "root", "");  
           // retornamos la conexion
           return conectar;
           // en caso de error muestra un mensaje
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "No Conectado","Error de Coneccion", JOptionPane.ERROR_MESSAGE);
        }      
        return null;
    }      
}
