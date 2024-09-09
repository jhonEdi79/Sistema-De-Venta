package Modelos;

import Entidades.Producto;
import Entidades.VentaDetalles;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

public class TablaModeloVenta extends AbstractTableModel {

    private final  ArrayList<VentaDetalles> ventaDetalles = new ArrayList<>();

    String[] columnas = {
        "Producto",
        "Precio",
        "Cantidad",
        "Total"
    };

    public void agregarProductoACarrito(Producto p, int cantidad) {
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(null, "No puede comprar 0 o menos cantidades");
            return;
        }

        if (cantidad > p.getStok()) {
            JOptionPane.showMessageDialog(null, "No hay suficiente cantidad");
            return;
        }

        VentaDetalles vi = new VentaDetalles();
        vi.setProducto(p);
        vi.setCantidad(cantidad);
        ventaDetalles.add(vi);
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        if (column < 0 || column >= columnas.length) {
            return null;
        }
        return columnas[column];
    }

    @Override
    public int getRowCount() {
        return ventaDetalles.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        VentaDetalles vi = ventaDetalles.get(rowIndex);

        return switch (columnIndex) {
            case 0 ->
                vi.getProducto(); // devuelve el objeto producto
            case 1 ->
                vi.getProducto().getPrecio_venta(); // precio venta
            case 2 ->
                vi.getCantidad(); // cantidad de venta
            case 3 ->
                vi.getProducto().getPrecio_venta()* vi.getCantidad();
            default ->
                null;
        };
    } 

    public ArrayList<VentaDetalles> getVentaDetalles() {
        return ventaDetalles;
    }
    
    
}
