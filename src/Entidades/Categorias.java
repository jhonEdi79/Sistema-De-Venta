
package Entidades;

// metodo para obtener y modicar para mapear datos
public class Categorias {
    
    private int id;
    private String nombre;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    } 
    
    // metodo para mostrar el nombre de la categoria en vez de la referencia del objeto en el combobox
    @Override
    public String toString() {
        return nombre;
    }   
}
