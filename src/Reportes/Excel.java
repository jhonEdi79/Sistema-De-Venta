package Reportes;
// Importaciones necesarias para el funcionamiento del programa
import Util.MenuPrincipalSingleton;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import tiendacocina.ConectorBD;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tiendacocina.MenuPrincipal;

public class Excel {
    // Obtenemos la Instancia del menu principal atraves de la clase singleton para obtener los metodos get de los jCheckBox
    private final MenuPrincipal menuPrincipal = MenuPrincipalSingleton.getInstance();
    //crea un nuevo libro de trabajo de Excel en blanco
    private final Workbook reportesGenerales = new XSSFWorkbook();

    public void reporte() {
        try {
// Obtenemos la conexion
            Connection conn = ConectorBD.obtenerConexion();
            String fechaDesde = menuPrincipal.getTxtobtenerFechaDesde().getText();
            String fechaHasta = menuPrincipal.getTxtobtenerFechaHasta().getText();

// Condicionales en caso de que se selccionen uno o varios jCheckBox
            if (menuPrincipal.getCBInventario().isSelected()) {
                generarReporteInventario(conn);
            }
            if (menuPrincipal.getCBCompras().isSelected()) {
                if (fechaDesde.isEmpty() || fechaHasta.isEmpty()) {
                    // Muestra un mensaje en caso de no slecionar fecha, no retorna nada
                    JOptionPane.showMessageDialog(null, "Por favor ingrese las fechas para el reporte de Compras.");
                    return;
                } else {
                    generarReporteCompras(conn, fechaDesde, fechaHasta);
                }
            }
            if (menuPrincipal.getCBVentas().isSelected()) {
                if (fechaDesde.isEmpty() || fechaHasta.isEmpty()) {
                    // Muestra un mensaje en caso de no slecionar fecha, no retorna nada
                    JOptionPane.showMessageDialog(null, "Por favor ingrese las fechas para el reporte de Ventas.");
                    return;
                } else {
                    generarReporteVentas(conn, fechaDesde, fechaHasta);
                    generarReporteVentasDetalladas(conn);
                }
            }
            if (menuPrincipal.getCBCliente().isSelected()) {
                generarReporteClientes(conn);
            }
            // Nombre del Libro/Documento
            String fileName = "Reporte";
            // Formato y Ruta donde se guardara el reporte
            String home = System.getProperty("user.home");
            File file = new File(home + "/Desktop/" + fileName + ".xlsx");
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                reportesGenerales.write(fileOut);
            }
            // Mensaje confirmando la generacion del reporte antes de abrir el archivo
            JOptionPane.showMessageDialog(null, "Reporte Generado");
            Desktop.getDesktop().open(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Excel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | SQLException ex) {
            Logger.getLogger(Excel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
// Metodo que genera el reporte Inventario
    private void generarReporteInventario(Connection conn) throws SQLException, IOException {
        Sheet hojaInventario = reportesGenerales.createSheet("Inventario");
        agregarImagen(hojaInventario, reportesGenerales, "/icons/003-agregar-1.png");
        CellStyle tituloEstilo = createEstiloTitulo(reportesGenerales);
        CellStyle encabezadoEstilo = creaEstiloEncabezado(reportesGenerales);
        CellStyle datosEstilo = CreaDatosStilos(reportesGenerales);
        String[] encabezadosInventario = new String[]{"Código", "Nombre", "Precio", "Stock"};
        configurarHoja(hojaInventario, "Reporte de Inventario", encabezadosInventario, tituloEstilo, encabezadoEstilo);
        PreparedStatement ps = conn.prepareStatement("SELECT id_producto, nombre, precio_venta, stok FROM producto");
        ResultSet rs = ps.executeQuery();
        LlenarHojaConDatos(hojaInventario, rs, datosEstilo, 5);
    }

// Metodo que genera el reporte Compras
    private void generarReporteCompras(Connection conn, String fechaDesde, String fechaHasta) throws SQLException, IOException {
        Sheet hojaCompra = reportesGenerales.createSheet("Compras");
        agregarImagen(hojaCompra, reportesGenerales, "/icons/003-agregar-1.png");
        CellStyle tituloEstilo = createEstiloTitulo(reportesGenerales);
        CellStyle encabezadoEstilo = creaEstiloEncabezado(reportesGenerales);
        CellStyle datosEstilo = CreaDatosStilos(reportesGenerales);
        String[] encabexadoCompras = new String[]{"Id", "Producto", "Cantidad", "Precio Total", "Proveedor", "Tipo Ingreso", "Fecha Compra"};
        configurarHoja(hojaCompra, "Reporte de Compras", encabexadoCompras, tituloEstilo, encabezadoEstilo);
        PreparedStatement ps = conn.prepareStatement("SELECT id, producto, cantidad, precio_total_compra, proveedor, tipo_ingreso, fecha_compra FROM compra WHERE fecha_compra BETWEEN ? AND ?");
        ps.setString(1, fechaDesde);
        ps.setString(2, fechaHasta);
        ResultSet rs = ps.executeQuery();
        LlenarHojaConDatos(hojaCompra, rs, datosEstilo, 5);
    }

// Metodo que genera el reporte Ventas
    private void generarReporteVentas(Connection conn, String fechaDesde, String fechaHasta) throws SQLException, IOException {
        Sheet hojaVentas = reportesGenerales.createSheet("Ventas");
        agregarImagen(hojaVentas, reportesGenerales, "/icons/003-agregar-1.png");
        CellStyle tituloEstilo = createEstiloTitulo(reportesGenerales);
        CellStyle encabezadoEstilo = creaEstiloEncabezado(reportesGenerales);
        CellStyle datosEstilo = CreaDatosStilos(reportesGenerales);
        String[] encabexzadoVentas = new String[]{"Id Venta", "Cliente", "Forma de Pago", "Cantidad de Articulos Comprados", "Total Cancelado", "Fecha Venta"};
        configurarHoja(hojaVentas, "Reporte de Ventas", encabexzadoVentas, tituloEstilo, encabezadoEstilo);
        PreparedStatement ps = conn.prepareStatement("SELECT id_venta, id_cliente, forma_pago, cantidad_articulos, total_pagado, fecha_venta FROM venta WHERE fecha_venta BETWEEN ? AND ?");
        ps.setString(1, fechaDesde);
        ps.setString(2, fechaHasta);
        ResultSet rs = ps.executeQuery();
        LlenarHojaConDatos(hojaVentas, rs, datosEstilo, 5);
    }

// Metodo que genera el detalle de ventas
    private void generarReporteVentasDetalladas(Connection conn) throws SQLException, IOException {
        Sheet hojaDetalleVentas = reportesGenerales.createSheet("Venta Detallada");
        agregarImagen(hojaDetalleVentas, reportesGenerales, "/icons/003-agregar-1.png");
        CellStyle tituloEstilo = createEstiloTitulo(reportesGenerales);
        CellStyle encabezadoEstilo = creaEstiloEncabezado(reportesGenerales);
        CellStyle datosEstilo = CreaDatosStilos(reportesGenerales);
        String[] encabezadoVentaDetallada = new String[]{"Id Venta", "Producto", "Cantidad", "Precio"};
        configurarHoja(hojaDetalleVentas, "Reporte de Venta Detallada", encabezadoVentaDetallada, tituloEstilo, encabezadoEstilo);
        PreparedStatement ps = conn.prepareStatement("SELECT id_venta, id_producto, cantidad, precio FROM detalle_venta");
        ResultSet rs = ps.executeQuery();
        LlenarHojaConDatos(hojaDetalleVentas, rs, datosEstilo, 5);
    }

// Metodo que genera el reporte Clientes
    private void generarReporteClientes(Connection conn) throws SQLException, IOException {
        Sheet hojaClientes = reportesGenerales.createSheet("Clientes");
        agregarImagen(hojaClientes, reportesGenerales, "/icons/003-agregar-1.png");
        CellStyle tituloEstilo = createEstiloTitulo(reportesGenerales);
        CellStyle encabezadoEstilo = creaEstiloEncabezado(reportesGenerales);
        CellStyle datosEstilo = CreaDatosStilos(reportesGenerales);
        String[] encabezadoClientes = new String[]{"Identificacion", "Nombre", "Direccion", "Telefono"};
        configurarHoja(hojaClientes, "Reporte de Clientes", encabezadoClientes, tituloEstilo, encabezadoEstilo);
        PreparedStatement ps = conn.prepareStatement("SELECT id_cliente, nombre, direccion, telefono FROM cliente");
        ResultSet rs = ps.executeQuery();
        LlenarHojaConDatos(hojaClientes, rs, datosEstilo, 5);
    }

    // Método para agregar una imagen a una hoja.
    private void agregarImagen(Sheet sheet, Workbook book, String imagePath) throws IOException {
        // Agregar imágenes a cada hoja
        int pictureIdx;
        try (InputStream inputStream = getClass().getResourceAsStream(imagePath)) {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            pictureIdx = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
        }
        CreationHelper helper = book.getCreationHelper();
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = helper.createClientAnchor();

        // Configurar la posición de la imagen
        anchor.setCol1(0); // Primera columna
        anchor.setRow1(1); // Segunda fila
        anchor.setCol2(1); // Primera columna (ancho de una columna)
        anchor.setRow2(3); // Tercera fila (alto de dos filas)

        Picture pict = drawing.createPicture(anchor, pictureIdx);

        // Redimensionar la imagen a 50x50 px
        pict.resize(1.0, 1.4); // Ajustar el tamaño de la imagen

        // Combinar celdas de la primera columna, filas 2 y 3
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 0));
    }

    // Metodo para darle estilo al titulo
    private static CellStyle createEstiloTitulo(Workbook book) {
        CellStyle tituloEstilo = book.createCellStyle();
        tituloEstilo.setAlignment(HorizontalAlignment.CENTER);
        tituloEstilo.setVerticalAlignment(VerticalAlignment.CENTER);
        Font fuenteTitulo = book.createFont();
        fuenteTitulo.setFontName("Arial");
        fuenteTitulo.setBold(true);
        fuenteTitulo.setFontHeightInPoints((short) 14);
        tituloEstilo.setFont(fuenteTitulo);
        return tituloEstilo;
    }

    // metodo para darle estilo  los encabesados
    private static CellStyle creaEstiloEncabezado(Workbook book) {
        CellStyle headerStyle = book.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        Font font = book.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 12);
        headerStyle.setFont(font);
        return headerStyle;
    }

    // metodo para darle estilo a los datos 
    private static CellStyle CreaDatosStilos(Workbook book) {
        CellStyle datosEstilo = book.createCellStyle();
        datosEstilo.setBorderBottom(BorderStyle.THIN);
        datosEstilo.setBorderLeft(BorderStyle.THIN);
        datosEstilo.setBorderRight(BorderStyle.THIN);
        datosEstilo.setBorderTop(BorderStyle.THIN);
        return datosEstilo;
    }

    // Configura el estilo de los titulos de cada hoja 
    private static void configurarHoja(Sheet sheet, String title, String[] headers, CellStyle tituloEstilo, CellStyle headerStyle) {
        Row filaTitulo = sheet.createRow(1);
        Cell celdaTitulo = filaTitulo.createCell(1);
        celdaTitulo.setCellStyle(tituloEstilo);
        celdaTitulo.setCellValue(title);
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 3));

        Row filaEncabezados = sheet.createRow(4);
        for (int i = 0; i < headers.length; i++) {
            Cell celdaEncabezado = filaEncabezados.createCell(i);
            celdaEncabezado.setCellStyle(headerStyle);
            celdaEncabezado.setCellValue(headers[i]);
        }
    }

    // Este metodo llena las hojas con los datos de la base de datos
    private static void LlenarHojaConDatos(Sheet sheet, ResultSet rs, CellStyle datosEstilo, int startRow) throws SQLException {
        int numCol = rs.getMetaData().getColumnCount();
        int numFilaDatos = startRow;
        while (rs.next()) {
            Row filaDatos = sheet.createRow(numFilaDatos);
            for (int a = 0; a < numCol; a++) {
                Cell celdaDatos = filaDatos.createCell(a);
                celdaDatos.setCellStyle(datosEstilo);
                celdaDatos.setCellValue(rs.getString(a + 1));
            }
            numFilaDatos++;
        }
        for (int i = 0; i < numCol; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
