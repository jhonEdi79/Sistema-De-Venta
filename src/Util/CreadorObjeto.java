
package Util;


import java.sql.ResultSet;
import java.sql.SQLException;
//se crea una interfaz funcional, hace uso de un generico que especifica una funcion para construir una instancia de un objeto de ese generico
@FunctionalInterface
public interface CreadorObjeto<T> {
    T crearObjeto(ResultSet rs) throws SQLException;
}
