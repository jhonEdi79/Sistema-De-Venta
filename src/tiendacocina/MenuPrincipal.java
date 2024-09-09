package tiendacocina;

// Importamos las Librerias, Packages, Utilidades y Complementos Necesarios.
import Entidades.Categorias;
import Entidades.Cliente;
import Entidades.Producto;
import Entidades.Proveedor;
import Entidades.VentaDetalles;
import Modelos.TablaModeloVenta;
import Reportes.Excel;
import Util.CreadorObjeto;
import Util.SetterStatement;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableModel;

public final class MenuPrincipal extends javax.swing.JFrame {

    // Creamos el array de la venta
    ArrayList<VentaDetalles> ventas = new ArrayList();

    // Variable global Privada
    private int total = 0;

    // Instanciamos el modelo de la tabla venta
    private final TablaModeloVenta modelo = new TablaModeloVenta();

    public MenuPrincipal() {
        initComponents();

        // Quitamos el aumento de pantalla
        setResizable(false);

        // inicializamos los metodos para mostrar los datos en las tablas
        mostrarTablaClientes();
        mostrarTablaCategorias();
        mostrarCategoriascbx();
        mostrarTablaProductos();
        mostrarTablaProductosGeneral();
        mostrarTablaArticulo();
        mostrarTablaUsuarios();
        mostrarTablaDetalleProductos();
        mostrarTablaProveedor();
        mostrarTablaCompras();

        // Titulo de la Pagina
        setTitle("Punto de Venta - La Tiendita de Jhon ");

        // Ubica al centro de pantalla al Abrir
        setLocationRelativeTo(null);

        // ocultar el calendario
        jCalendar1.setVisible(false);
        jCalendar2.setVisible(false);

        // colocamos el modelo de tabla de [TablaModeloVentas] a la tabla [TablaListaProductos
        tablaListaProductos.setModel(modelo);
        menu.setUI(new BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
                return 0;
            }
        });
    }

// Metodos get para obtener la seleccion de los jCheckBox
    public JCheckBox getCBVentas() {
        return CBVentas;
    }

    public JCheckBox getCBCompras() {
        return CBCompras;
    }

    public JCheckBox getCBInventario() {
        return CBInventario;
    }

    public JTextField getTxtobtenerFechaDesde() {
        return txtobtenerFechaDesde;
    }

    public JTextField getTxtobtenerFechaHasta() {
        return txtobtenerFechaHasta;
    }

    public JCheckBox getCBCliente() {
        return CBClientes;
    }

    // Metodo para calcular el total de la venta
    private void calcularTotal() {
        total = 0;
        for (int i = 0; i < tablaListaProductos.getRowCount(); i++) {
            total += ((int) tablaListaProductos.getValueAt(i, 3));
        }
        totalVenta.setText("$ " + total);
    }

    // Metodo para actualizar los productos
    private void actualizarCantidadTotalProductos() {
        conteoListaProductos.setText(String.valueOf(tablaListaProductos.getRowCount()));
    }

    // metodo para mostrar las categorias en el combobox 
    private void mostrarCategoriascbx() {
        mostrarDatoscbx("SELECT * FROM categoria", cbxcategoria, this::crearCategoria);
    }

    // Metodo que crea las categorias 
    private Categorias crearCategoria(ResultSet rs) {
        try {
            Categorias c = new Categorias();
            c.setId(rs.getInt(1));
            c.setNombre(rs.getString(2));
            return c;
        } catch (SQLException ex) {
            Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //Metodo para mostrar datos en la lista
    public static void mostrarDatosLista(String sql, JList jList, SetterStatement setter, CreadorObjeto creador) {
        DefaultListModel modelo = new DefaultListModel();
        jList.setModel(modelo);

        try (PreparedStatement stm = ConectorBD.obtenerConexion().prepareStatement(sql)) {
            setter.settearDatosStatement(stm);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                Object obj = creador.crearObjeto(rs);
                modelo.addElement(obj);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Metodo para Mostrar datos en el combobox
    private void mostrarDatoscbx(String sql, JComboBox comboBox, CreadorObjeto creador) {
        comboBox.removeAllItems();

        try (Statement stm = ConectorBD.obtenerConexion().createStatement()) {
            var rs = stm.executeQuery(sql);

            while (rs.next()) {
                Object obj = creador.crearObjeto(rs);
                comboBox.addItem(obj);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Metodos para Mostrar los Datos en las Tablas  
    private void mostrarTablaCategorias() {
        mostrar("SELECT * FROM categoria", new String[]{"ID", "Nombre de la categoria"}, tablaAdministrador, new Class[]{Integer.class, String.class});
    }

    private void mostrarTablaClientes() {
        mostrar("SELECT * FROM cliente", new String[]{"Documento", "Nombre", "Direccion", "Celular"}, tablaClientes, new Class[]{String.class, String.class, String.class, String.class});
    }

    private void mostrarTablaProductos() {
        mostrar("SELECT * FROM view_producto", new String[]{"id_producto", "nombre", "precio_venta", "stok", "categoria"}, tablaAdministrador, new Class[]{String.class, String.class, String.class, Integer.class, String.class});
    }

    private void mostrarTablaArticulo() {
        mostrar("SELECT * FROM articulo", new String[]{"id", "nombre", "medida"}, tablaAdministrador, new Class[]{String.class, String.class, String.class});
    }

    private void mostrarTablaUsuarios() {
        mostrar("SELECT * FROM usuarios", new String[]{"id", "nombres", "permiso"}, tablaAdministrador, new Class[]{String.class, String.class, String.class});
    }

    private void mostrarTablaDetalleProductos() {
        mostrar("SELECT * FROM producto_detalle", new String[]{"id", "codigo", "producto", "articulo", "cantidad"}, tablaAdministrador, new Class[]{String.class, String.class, String.class, String.class, String.class});
    }

    private void mostrarTablaProveedor() {
        mostrar("SELECT * FROM proveedor", new String[]{"      Nombre - Razon Social ", "    Tipo", "       Documento", "    telefono"}, tablaAdministrador, new Class[]{String.class, String.class, String.class, String.class});
    }

    private void mostrarTablaCompras() {
        mostrar("SELECT * FROM compra", new String[]{"   Id", "      Nombre  ", "    Cantidad ", "       precio Unidad ", "    Precio Total"}, tablaAdministrador, new Class[]{String.class, String.class, Integer.class, String.class, String.class, String.class, String.class, String.class});
    }

    private void mostrarTablaProductosGeneral() {
        mostrar("SELECT * FROM view_producto", new String[]{"id_producto", "nombre", "precio_venta", "stok", "categoria"}, tablaProductosGeneral, new Class[]{String.class, String.class, String.class, Integer.class, String.class});
    }

    // metodo para facilitar el mostrar datos en la tabla 
    public void mostrar(String sqlQuery, String[] columnas, JTable tabla, Class[] tiposColumnas) {
        var modeloMostrar = new DefaultTableModel();
        for (String col : columnas) {
            modeloMostrar.addColumn(col);
        }
        tabla.setModel(modeloMostrar);
        Object datos[] = new Object[columnas.length];
        try {
            var st = ConectorBD.obtenerConexion().createStatement();
            var rs = st.executeQuery(sqlQuery);
            while (rs.next()) {
                for (int i = 0; i < columnas.length; i++) {
                    if (tiposColumnas[i] == Integer.class) {
                        datos[i] = rs.getInt(i + 1);
                    } else if (tiposColumnas[i] == String.class) {
                        datos[i] = rs.getString(i + 1);
                    }
                }

                modeloMostrar.addRow(datos);
            }
        } catch (SQLException ex) {
        }
    }

    // metodo para mostrar datos en la tabla venta
    public void mostrarVentas(ArrayList<VentaDetalles> ventas, JTable tabla) {
        // creamos un nuev modelo
        var modeloVenta = new DefaultTableModel();
        // nombramos las columna e indicamos cuantas columnas hay
        modeloVenta.addColumn("Producto");
        modeloVenta.addColumn("Cantidad");
        modeloVenta.addColumn("Precio");
        modeloVenta.addColumn("Total");
        // agregamos el modelo a la tabla
        tabla.setModel(modeloVenta);
        // array donde se almacenan los datos
        Object datos[] = new Object[4];
        // recorremos el array ventas y obtenemos los datos 
        for (VentaDetalles v : ventas) {
            datos[0] = v.getProducto().getNombre();
            datos[1] = v.getCantidad();
            datos[2] = v.getProducto().getPrecio_venta();
            datos[3] = v.getProducto().getPrecio_venta() * v.getCantidad();
            // añadimos la fila al modelo
            modeloVenta.addRow(datos);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnClientes = new javax.swing.JButton();
        btnProducto = new javax.swing.JButton();
        btnVentas = new javax.swing.JButton();
        btnAdmin = new javax.swing.JButton();
        btnReportes = new javax.swing.JButton();
        btnSoporte = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        menu = new javax.swing.JTabbedPane();
        menuCliente = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtCliente = new javax.swing.JTextField();
        btnBuscarCliente = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaClientes = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtDocumentoCliente = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtNombresCliente = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtDireccionCliente = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtCelularCliente = new javax.swing.JTextField();
        btnGuardarCliente = new javax.swing.JButton();
        btnLimpiarCamposCliente = new javax.swing.JButton();
        btnEliminarCliente = new javax.swing.JButton();
        menuProducto = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel55 = new javax.swing.JLabel();
        txtCliente2 = new javax.swing.JTextField();
        btnBuscarCliente3 = new javax.swing.JButton();
        jPanel20 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tablaProductosGeneral = new javax.swing.JTable();
        menuVentas = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaListaProductos = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtBuscarProductoVenta = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        btnAñadirProductoVenta = new javax.swing.JButton();
        btnLimpiarCamposVenta = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        listaProductoVenta = new javax.swing.JList();
        sprCantidadVenta = new javax.swing.JSpinner();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        totalVenta = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        totalCambio = new javax.swing.JLabel();
        txtCambio = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        btnRegistrarVentaGeneral = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        BuscarClienteVenta = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        listaClienteVenta = new javax.swing.JList();
        cbxmetodoPagoVenta = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        conteoListaProductos = new javax.swing.JLabel();
        menuAdministrador = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        txtCliente1 = new javax.swing.JTextField();
        btnBuscarCliente2 = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        tablaAdministrador = new javax.swing.JTable();
        tbbAdministrador = new javax.swing.JTabbedPane();
        panelCategoria = new javax.swing.JPanel();
        btnGuardarCategoria = new javax.swing.JButton();
        btnLimpiarCamposCategoria = new javax.swing.JButton();
        btnCancelarRegistroCliente1 = new javax.swing.JButton();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        txtcategoria = new javax.swing.JTextField();
        panelProducto = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        txtcodigoProducto = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        txtnombreProducto = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        txtprecioVenta = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        cbxcategoria = new javax.swing.JComboBox<>();
        btnBorrarProducto = new javax.swing.JButton();
        btnLimpiarCamposProducto = new javax.swing.JButton();
        btnGuardarProducto = new javax.swing.JButton();
        panelProveedores = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        txtRazonSocial = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        txtNumeroDocumentoProveedor = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        txtTelefonoProveedor = new javax.swing.JTextField();
        btnLimpiarCamposProveedores1 = new javax.swing.JButton();
        btnEliminarProveedores1 = new javax.swing.JButton();
        btnGuardarProveedores1 = new javax.swing.JButton();
        cbxProveedores = new javax.swing.JComboBox<>();
        jLabel54 = new javax.swing.JLabel();
        panelCompras = new javax.swing.JPanel();
        btnGuardarCompra1 = new javax.swing.JButton();
        btnCancelarRegistroCompra1 = new javax.swing.JButton();
        btnLimpiarCamposCompra1 = new javax.swing.JButton();
        txtBuscarProveedorCompra = new javax.swing.JTextField();
        jLabel72 = new javax.swing.JLabel();
        cbxTipoIngreso = new javax.swing.JComboBox<>();
        jLabel71 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        txtNumeroComprobante = new javax.swing.JTextField();
        cbxComprobante = new javax.swing.JComboBox<>();
        jLabel69 = new javax.swing.JLabel();
        totalSumatoriaCompraPreductos = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        txtPrecioCompra = new javax.swing.JTextField();
        txtCantidadProducto = new javax.swing.JTextField();
        jLabel63 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        txtBuscarProductoCompra = new javax.swing.JTextField();
        jScrollPane7 = new javax.swing.JScrollPane();
        ListaProductosCompra = new javax.swing.JList();
        jScrollPane5 = new javax.swing.JScrollPane();
        listaProveedores = new javax.swing.JList();
        btnBuscarProveedorCompra = new javax.swing.JButton();
        panelUsuario = new javax.swing.JPanel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        txtidUsuario = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        txtnombresUsuario = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        contraseñaUsuario = new javax.swing.JPasswordField();
        cbxComboPermisos = new javax.swing.JComboBox<>();
        btnGuardarUsuario = new javax.swing.JButton();
        btnLimpiarCamposUsuario = new javax.swing.JButton();
        btnEliminarUsuario = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        menuReportes = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        txtobtenerFechaDesde = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jCalendar1 = new com.toedter.calendar.JCalendar();
        txtobtenerFechaHasta = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jCalendar2 = new com.toedter.calendar.JCalendar();
        jLabel38 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        btnGenerarPDF = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        CBVentas = new javax.swing.JCheckBox();
        CBCompras = new javax.swing.JCheckBox();
        CBInventario = new javax.swing.JCheckBox();
        CBClientes = new javax.swing.JCheckBox();
        jLabel13 = new javax.swing.JLabel();
        menuSoporte = new javax.swing.JPanel();
        jButton8 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar1.setRollover(true);

        btnClientes.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        btnClientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/002-personas.png"))); // NOI18N
        btnClientes.setText("Clientes");
        btnClientes.setFocusable(false);
        btnClientes.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClientes.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClientesActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClientes);

        btnProducto.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        btnProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/009-agregar-producto.png"))); // NOI18N
        btnProducto.setText("Producto");
        btnProducto.setFocusable(false);
        btnProducto.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnProducto.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductoActionPerformed(evt);
            }
        });
        jToolBar1.add(btnProducto);

        btnVentas.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        btnVentas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/006-de-venta.png"))); // NOI18N
        btnVentas.setText("Ventas");
        btnVentas.setFocusable(false);
        btnVentas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVentas.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVentasActionPerformed(evt);
            }
        });
        jToolBar1.add(btnVentas);

        btnAdmin.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        btnAdmin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/001-consultor.png"))); // NOI18N
        btnAdmin.setText("Administrador");
        btnAdmin.setFocusable(false);
        btnAdmin.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAdmin.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdminActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAdmin);

        btnReportes.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        btnReportes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/002-reporte-de-negocios.png"))); // NOI18N
        btnReportes.setText("Reportes");
        btnReportes.setFocusable(false);
        btnReportes.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnReportes.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnReportes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportesActionPerformed(evt);
            }
        });
        jToolBar1.add(btnReportes);

        btnSoporte.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        btnSoporte.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/001-servicio-al-cliente.png"))); // NOI18N
        btnSoporte.setText("Soporte Tecnico");
        btnSoporte.setFocusable(false);
        btnSoporte.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSoporte.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSoporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSoporteActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSoporte);

        btnSalir.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/005-salida.png"))); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.setFocusable(false);
        btnSalir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSalir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSalir);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        menu.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel1.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel1.setText("Cliente");

        btnBuscarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/006-grafico.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addComponent(btnBuscarCliente))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jScrollPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        tablaClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tablaClientes);

        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel2.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel2.setText("Ingrese la Informacion del Nuevo Cliente");

        jLabel3.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel3.setText("Documento");

        jLabel4.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel4.setText("Nombre Completo");

        jLabel5.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel5.setText("Direccion");

        jLabel6.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel6.setText("Celular");

        btnGuardarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/001-salvado.png"))); // NOI18N
        btnGuardarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarClienteActionPerformed(evt);
            }
        });

        btnLimpiarCamposCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/003-escoba.png"))); // NOI18N
        btnLimpiarCamposCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarCamposClienteActionPerformed(evt);
            }
        });

        btnEliminarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/004-boton-x.png"))); // NOI18N
        btnEliminarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDocumentoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(txtNombresCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtDireccionCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtCelularCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnGuardarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnLimpiarCamposCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnEliminarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel2)
                .addGap(31, 31, 31)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDocumentoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombresCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDireccionCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCelularCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGuardarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimpiarCamposCliente, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(361, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout menuClienteLayout = new javax.swing.GroupLayout(menuCliente);
        menuCliente.setLayout(menuClienteLayout);
        menuClienteLayout.setHorizontalGroup(
            menuClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuClienteLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(menuClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(menuClienteLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1487, Short.MAX_VALUE)))
                .addContainerGap())
        );
        menuClienteLayout.setVerticalGroup(
            menuClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(menuClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );

        menu.addTab("cliente", menuCliente);

        jPanel14.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel55.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel55.setText("Productos");

        btnBuscarCliente3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/001-buscar.png"))); // NOI18N
        btnBuscarCliente3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarCliente3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel55)
                .addGap(18, 18, 18)
                .addComponent(txtCliente2, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBuscarCliente3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1365, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCliente2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel55))
                    .addComponent(btnBuscarCliente3))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jScrollPane8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        tablaProductosGeneral.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane8.setViewportView(tablaProductosGeneral);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane8)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout menuProductoLayout = new javax.swing.GroupLayout(menuProducto);
        menuProducto.setLayout(menuProductoLayout);
        menuProductoLayout.setHorizontalGroup(
            menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuProductoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        menuProductoLayout.setVerticalGroup(
            menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuProductoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(239, Short.MAX_VALUE))
        );

        menu.addTab("producto", menuProducto);

        jPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel7.setFont(new java.awt.Font("Verdana", 1, 24)); // NOI18N
        jLabel7.setText("Ventas");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel7)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jScrollPane2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        tablaListaProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tablaListaProductos);

        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel11.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel11.setText("Ingrese Informacion de la Venta");

        jLabel15.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel15.setText("Producto");

        txtBuscarProductoVenta.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtBuscarProductoVentaCaretUpdate(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel16.setText("Cantidad");

        btnAñadirProductoVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/003-agregar-1.png"))); // NOI18N
        btnAñadirProductoVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAñadirProductoVentaActionPerformed(evt);
            }
        });

        btnLimpiarCamposVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/004-boton-x.png"))); // NOI18N
        btnLimpiarCamposVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarCamposVentaActionPerformed(evt);
            }
        });

        jScrollPane4.setViewportView(listaProductoVenta);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(0, 52, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnAñadirProductoVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnLimpiarCamposVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(76, 76, 76))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addGap(18, 18, 18)
                                .addComponent(sprCantidadVenta))
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                    .addComponent(jLabel15)
                                    .addGap(18, 18, 18)
                                    .addComponent(txtBuscarProductoVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(19, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtBuscarProductoVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(sprCantidadVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLimpiarCamposVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAñadirProductoVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(269, Short.MAX_VALUE))
        );

        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jPanel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel8.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 255));
        jLabel8.setText("Total a Pagar");

        totalVenta.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        totalVenta.setForeground(new java.awt.Color(255, 102, 102));
        totalVenta.setText("$ . ");

        jLabel10.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 51, 255));
        jLabel10.setText("Recibido");

        jLabel12.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(51, 51, 255));
        jLabel12.setText("Cambio");

        totalCambio.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        totalCambio.setForeground(new java.awt.Color(255, 102, 102));
        totalCambio.setText("$ . ");

        txtCambio.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtCambioCaretUpdate(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(49, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(totalVenta)
                    .addComponent(jLabel10)
                    .addComponent(jLabel12)
                    .addComponent(totalCambio)
                    .addComponent(txtCambio, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(totalVenta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCambio, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalCambio)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jLabel18.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel18.setText("Facturacion");

        jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        btnRegistrarVentaGeneral.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/001-caja-registradora.png"))); // NOI18N
        btnRegistrarVentaGeneral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarVentaGeneralActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/003-escoba.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/002-eliminar.png"))); // NOI18N

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(51, 51, 255));
        jLabel14.setText("Cliente");

        BuscarClienteVenta.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                BuscarClienteVentaCaretUpdate(evt);
            }
        });

        jScrollPane3.setViewportView(listaClienteVenta);

        cbxmetodoPagoVenta.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        cbxmetodoPagoVenta.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "SELECCIONAR", "EFECTIVO", "CREDITO", "DEBITO", "NFC", " " }));

        jLabel19.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 51, 255));
        jLabel19.setText("Metodos de Pago");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BuscarClienteVenta))
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(btnRegistrarVentaGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 139, Short.MAX_VALUE))
                            .addComponent(cbxmetodoPagoVenta, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BuscarClienteVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbxmetodoPagoVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRegistrarVentaGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(411, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(22, 22, 22))
        );

        jLabel17.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel17.setText("LISTA DE PRODUCTOS");

        jLabel9.setText("Cantidad Articulos :");

        conteoListaProductos.setText("0");

        javax.swing.GroupLayout menuVentasLayout = new javax.swing.GroupLayout(menuVentas);
        menuVentas.setLayout(menuVentasLayout);
        menuVentasLayout.setHorizontalGroup(
            menuVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuVentasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(menuVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuVentasLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(menuVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(menuVentasLayout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(conteoListaProductos)
                                .addGap(11, 11, 11)))
                        .addContainerGap())
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        menuVentasLayout.setVerticalGroup(
            menuVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuVentasLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(menuVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(menuVentasLayout.createSequentialGroup()
                        .addGroup(menuVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(menuVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel9)
                                .addComponent(conteoListaProductos))
                            .addComponent(jLabel17))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(165, Short.MAX_VALUE))
        );

        menu.addTab("ventas", menuVentas);

        jPanel12.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel39.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel39.setText("Administracion");

        btnBuscarCliente2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/006-grafico.png"))); // NOI18N
        btnBuscarCliente2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarCliente2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel39)
                .addGap(18, 18, 18)
                .addComponent(txtCliente1, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBuscarCliente2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCliente1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel39))
                    .addComponent(btnBuscarCliente2))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jScrollPane6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        tablaAdministrador.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        tablaAdministrador.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane6.setViewportView(tablaAdministrador);

        tbbAdministrador.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        tbbAdministrador.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        tbbAdministrador.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbbAdministradorMouseClicked(evt);
            }
        });

        btnGuardarCategoria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/001-salvado.png"))); // NOI18N
        btnGuardarCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarCategoriaActionPerformed(evt);
            }
        });

        btnLimpiarCamposCategoria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/003-escoba.png"))); // NOI18N
        btnLimpiarCamposCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarCamposCategoriaActionPerformed(evt);
            }
        });

        btnCancelarRegistroCliente1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/002-eliminar.png"))); // NOI18N
        btnCancelarRegistroCliente1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarRegistroCliente1ActionPerformed(evt);
            }
        });

        jLabel45.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel45.setText("Nuevo Categoria");

        jLabel46.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel46.setText("Nombre");

        javax.swing.GroupLayout panelCategoriaLayout = new javax.swing.GroupLayout(panelCategoria);
        panelCategoria.setLayout(panelCategoriaLayout);
        panelCategoriaLayout.setHorizontalGroup(
            panelCategoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCategoriaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCategoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtcategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45)
                    .addComponent(jLabel46)
                    .addGroup(panelCategoriaLayout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addComponent(btnGuardarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnLimpiarCamposCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelarRegistroCliente1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(273, Short.MAX_VALUE))
        );
        panelCategoriaLayout.setVerticalGroup(
            panelCategoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCategoriaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel45)
                .addGap(54, 54, 54)
                .addComponent(jLabel46)
                .addGap(18, 18, 18)
                .addComponent(txtcategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(74, 74, 74)
                .addGroup(panelCategoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGuardarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimpiarCamposCategoria, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelarRegistroCliente1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(493, Short.MAX_VALUE))
        );

        tbbAdministrador.addTab("Categoria", panelCategoria);

        jLabel47.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel47.setText("Nuevo Producto");

        jLabel48.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel48.setText("Codigo");

        jLabel49.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel49.setText("Nombre del Producto");

        jLabel50.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel50.setText("Precio de Venta");

        jLabel51.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel51.setText("Categoria");

        cbxcategoria.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        cbxcategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxcategoriaActionPerformed(evt);
            }
        });

        btnBorrarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/002-eliminar.png"))); // NOI18N
        btnBorrarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarProductoActionPerformed(evt);
            }
        });

        btnLimpiarCamposProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/003-escoba.png"))); // NOI18N
        btnLimpiarCamposProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarCamposProductoActionPerformed(evt);
            }
        });

        btnGuardarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/001-salvado.png"))); // NOI18N
        btnGuardarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarProductoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelProductoLayout = new javax.swing.GroupLayout(panelProducto);
        panelProducto.setLayout(panelProductoLayout);
        panelProductoLayout.setHorizontalGroup(
            panelProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProductoLayout.createSequentialGroup()
                .addGroup(panelProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelProductoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(cbxcategoria, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(panelProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(panelProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtnombreProducto, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtcodigoProducto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                                    .addComponent(jLabel47)
                                    .addComponent(jLabel48)
                                    .addComponent(jLabel49)
                                    .addComponent(jLabel50)
                                    .addComponent(txtprecioVenta))
                                .addComponent(jLabel51))))
                    .addGroup(panelProductoLayout.createSequentialGroup()
                        .addGap(79, 79, 79)
                        .addComponent(btnGuardarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnLimpiarCamposProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnBorrarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(276, Short.MAX_VALUE))
        );
        panelProductoLayout.setVerticalGroup(
            panelProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProductoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel47)
                .addGap(18, 18, 18)
                .addComponent(jLabel48)
                .addGap(18, 18, 18)
                .addComponent(txtcodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel49)
                .addGap(18, 18, 18)
                .addComponent(txtnombreProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel50)
                .addGap(18, 18, 18)
                .addComponent(txtprecioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel51)
                .addGap(18, 18, 18)
                .addComponent(cbxcategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGuardarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimpiarCamposProducto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBorrarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(315, Short.MAX_VALUE))
        );

        tbbAdministrador.addTab("Productos", panelProducto);

        jLabel40.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel40.setText("Ingrese Informacion del Proveedor");

        jLabel41.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel41.setText("Nombre - Razon Social");

        jLabel43.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel43.setText("Telefono");

        jLabel52.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel52.setText("Tipo de Documento");

        jLabel53.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel53.setText("Numero de Documento");

        btnLimpiarCamposProveedores1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/003-escoba.png"))); // NOI18N
        btnLimpiarCamposProveedores1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarCamposProveedores1ActionPerformed(evt);
            }
        });

        btnEliminarProveedores1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/002-eliminar.png"))); // NOI18N

        btnGuardarProveedores1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/001-salvado.png"))); // NOI18N
        btnGuardarProveedores1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarProveedores1ActionPerformed(evt);
            }
        });

        cbxProveedores.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        cbxProveedores.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECCIONAR", "CEDULA", "NIT", "OTRO..." }));

        jLabel54.setFont(new java.awt.Font("Verdana", 1, 24)); // NOI18N
        jLabel54.setText("proveedor");

        javax.swing.GroupLayout panelProveedoresLayout = new javax.swing.GroupLayout(panelProveedores);
        panelProveedores.setLayout(panelProveedoresLayout);
        panelProveedoresLayout.setHorizontalGroup(
            panelProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProveedoresLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelProveedoresLayout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addContainerGap(312, Short.MAX_VALUE))
                    .addGroup(panelProveedoresLayout.createSequentialGroup()
                        .addGroup(panelProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelProveedoresLayout.createSequentialGroup()
                                .addComponent(btnGuardarProveedores1, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnLimpiarCamposProveedores1, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnEliminarProveedores1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel41)
                            .addComponent(jLabel43)
                            .addComponent(jLabel52)
                            .addComponent(jLabel53)
                            .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNumeroDocumentoProveedor)
                            .addComponent(cbxProveedores, 0, 407, Short.MAX_VALUE)
                            .addComponent(txtRazonSocial)
                            .addComponent(txtTelefonoProveedor))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        panelProveedoresLayout.setVerticalGroup(
            panelProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProveedoresLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel54, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel40)
                .addGap(18, 18, 18)
                .addComponent(jLabel41)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRazonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel52)
                .addGap(18, 18, 18)
                .addComponent(cbxProveedores, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel53)
                .addGap(18, 18, 18)
                .addComponent(txtNumeroDocumentoProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel43)
                .addGap(18, 18, 18)
                .addComponent(txtTelefonoProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 170, Short.MAX_VALUE)
                .addGroup(panelProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGuardarProveedores1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimpiarCamposProveedores1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminarProveedores1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        tbbAdministrador.addTab("Proveedores", panelProveedores);

        btnGuardarCompra1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/001-salvado.png"))); // NOI18N
        btnGuardarCompra1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarCompra1ActionPerformed(evt);
            }
        });

        btnCancelarRegistroCompra1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/002-eliminar.png"))); // NOI18N
        btnCancelarRegistroCompra1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarRegistroCompra1ActionPerformed(evt);
            }
        });

        btnLimpiarCamposCompra1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/003-escoba.png"))); // NOI18N
        btnLimpiarCamposCompra1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarCamposCompra1ActionPerformed(evt);
            }
        });

        jLabel72.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel72.setText("Proveedor");

        cbxTipoIngreso.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        cbxTipoIngreso.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECCIONAR", "COMPRA", "CONSUMO INTERNO", "AJUSTE INVENTARIO", "OTRO" }));

        jLabel71.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel71.setText("Tipo Ingreso");

        jLabel70.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel70.setText("Numero");

        cbxComprobante.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        cbxComprobante.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECCIONAR", "FACTURA", "RECIBO", "CUFE" }));
        cbxComprobante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxComprobanteActionPerformed(evt);
            }
        });

        jLabel69.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel69.setText("Comprobante");

        totalSumatoriaCompraPreductos.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        totalSumatoriaCompraPreductos.setText("00.00");

        jLabel67.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel67.setText("Total");

        jLabel66.setFont(new java.awt.Font("Verdana", 3, 12)); // NOI18N
        jLabel66.setText("Ud - Unidad");

        jLabel65.setFont(new java.awt.Font("Verdana", 3, 12)); // NOI18N
        jLabel65.setText("Ud - Unidad");

        jLabel64.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel64.setText("Precio");

        txtPrecioCompra.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtPrecioCompraCaretUpdate(evt);
            }
        });

        txtCantidadProducto.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtCantidadProductoCaretUpdate(evt);
            }
        });

        jLabel63.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel63.setText("Cantidad");

        jLabel62.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel62.setText("Producto");

        jLabel61.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel61.setText("Ingrese Informacion de la Compra");

        txtBuscarProductoCompra.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtBuscarProductoCompraCaretUpdate(evt);
            }
        });
        txtBuscarProductoCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarProductoCompraActionPerformed(evt);
            }
        });

        ListaProductosCompra.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                ListaProductosCompraComponentResized(evt);
            }
        });
        jScrollPane7.setViewportView(ListaProductosCompra);

        jScrollPane5.setViewportView(listaProveedores);

        btnBuscarProveedorCompra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/006-grafico.png"))); // NOI18N
        btnBuscarProveedorCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarProveedorCompraActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelComprasLayout = new javax.swing.GroupLayout(panelCompras);
        panelCompras.setLayout(panelComprasLayout);
        panelComprasLayout.setHorizontalGroup(
            panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelComprasLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnGuardarCompra1, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnLimpiarCamposCompra1, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCancelarRegistroCompra1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(89, 89, 89))
            .addGroup(panelComprasLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelComprasLayout.createSequentialGroup()
                        .addComponent(jLabel61)
                        .addContainerGap())
                    .addGroup(panelComprasLayout.createSequentialGroup()
                        .addGroup(panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel72)
                            .addComponent(jLabel63)
                            .addComponent(jLabel64)
                            .addComponent(jLabel67)
                            .addComponent(jLabel69)
                            .addComponent(jLabel70)
                            .addComponent(jLabel71)
                            .addComponent(jLabel62))
                        .addGap(18, 18, 18)
                        .addGroup(panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelComprasLayout.createSequentialGroup()
                                .addGroup(panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtNumeroComprobante, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbxComprobante, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cbxTipoIngreso, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 526, Short.MAX_VALUE)
                                    .addComponent(txtBuscarProductoCompra)
                                    .addGroup(panelComprasLayout.createSequentialGroup()
                                        .addGroup(panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelComprasLayout.createSequentialGroup()
                                                .addGroup(panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(txtCantidadProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                                                    .addComponent(txtPrecioCompra))
                                                .addGap(35, 35, 35)
                                                .addGroup(panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel65)
                                                    .addComponent(jLabel66)))
                                            .addComponent(totalSumatoriaCompraPreductos, javax.swing.GroupLayout.Alignment.LEADING))
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addGap(31, 31, 31))
                            .addGroup(panelComprasLayout.createSequentialGroup()
                                .addGroup(panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelComprasLayout.createSequentialGroup()
                                        .addComponent(txtBuscarProveedorCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnBuscarProveedorCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
        );
        panelComprasLayout.setVerticalGroup(
            panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelComprasLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel61)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel62)
                    .addComponent(txtBuscarProductoCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 114, Short.MAX_VALUE)
                .addGroup(panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelComprasLayout.createSequentialGroup()
                        .addGroup(panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCantidadProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel65)
                            .addComponent(jLabel63))
                        .addGap(12, 12, 12)
                        .addGroup(panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPrecioCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel66))
                        .addGap(18, 18, 18)
                        .addGroup(panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(totalSumatoriaCompraPreductos)
                            .addComponent(jLabel67))
                        .addGap(18, 18, 18)
                        .addGroup(panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbxComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel69))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNumeroComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel70))
                        .addGap(18, 18, 18)
                        .addGroup(panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbxTipoIngreso, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel71))
                        .addGap(19, 19, 19)
                        .addGroup(panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnBuscarProveedorCompra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtBuscarProveedorCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel72)))
                    .addGroup(panelComprasLayout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel64)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGuardarCompra1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimpiarCamposCompra1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelarRegistroCompra1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tbbAdministrador.addTab("Compras", panelCompras);

        jLabel56.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel56.setText("Nuevo Usuario");

        jLabel57.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel57.setText("ID");

        jLabel58.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel58.setText("Nombres");

        jLabel59.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel59.setText("Contraseña");

        jLabel60.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel60.setText("Permisos");

        cbxComboPermisos.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        cbxComboPermisos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECCIONAR", "VENDEDOR", "ADMINISTRADOR" }));

        btnGuardarUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/001-salvado.png"))); // NOI18N
        btnGuardarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarUsuarioActionPerformed(evt);
            }
        });

        btnLimpiarCamposUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/003-escoba.png"))); // NOI18N
        btnLimpiarCamposUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarCamposUsuarioActionPerformed(evt);
            }
        });

        btnEliminarUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/002-eliminar.png"))); // NOI18N

        javax.swing.GroupLayout panelUsuarioLayout = new javax.swing.GroupLayout(panelUsuario);
        panelUsuario.setLayout(panelUsuarioLayout);
        panelUsuarioLayout.setHorizontalGroup(
            panelUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUsuarioLayout.createSequentialGroup()
                .addGroup(panelUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelUsuarioLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(cbxComboPermisos, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtidUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                            .addComponent(jLabel56, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel57, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtnombresUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                            .addComponent(jLabel58, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel59, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel60, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(contraseñaUsuario)))
                    .addGroup(panelUsuarioLayout.createSequentialGroup()
                        .addGap(78, 78, 78)
                        .addComponent(btnGuardarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnLimpiarCamposUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnEliminarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(276, Short.MAX_VALUE))
        );
        panelUsuarioLayout.setVerticalGroup(
            panelUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUsuarioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel56)
                .addGap(18, 18, 18)
                .addComponent(jLabel57)
                .addGap(18, 18, 18)
                .addComponent(txtidUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel58)
                .addGap(18, 18, 18)
                .addComponent(txtnombresUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel59)
                .addGap(18, 18, 18)
                .addComponent(contraseñaUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel60)
                .addGap(18, 18, 18)
                .addComponent(cbxComboPermisos, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addGroup(panelUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGuardarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimpiarCamposUsuario, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(300, Short.MAX_VALUE))
        );

        tbbAdministrador.addTab("Usuarios", panelUsuario);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 676, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 773, Short.MAX_VALUE)
        );

        tbbAdministrador.addTab("Clientes", jPanel8);

        javax.swing.GroupLayout menuAdministradorLayout = new javax.swing.GroupLayout(menuAdministrador);
        menuAdministrador.setLayout(menuAdministradorLayout);
        menuAdministradorLayout.setHorizontalGroup(
            menuAdministradorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuAdministradorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(menuAdministradorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(menuAdministradorLayout.createSequentialGroup()
                        .addGap(0, 6, Short.MAX_VALUE)
                        .addComponent(tbbAdministrador, javax.swing.GroupLayout.PREFERRED_SIZE, 678, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 1187, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        menuAdministradorLayout.setVerticalGroup(
            menuAdministradorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuAdministradorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(menuAdministradorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbbAdministrador)
                    .addComponent(jScrollPane6))
                .addContainerGap())
        );

        menu.addTab("administrador", menuAdministrador);

        jLabel34.setFont(new java.awt.Font("Verdana", 3, 36)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(102, 102, 255));
        jLabel34.setText("Reportes Generales");

        txtobtenerFechaDesde.setEditable(false);
        txtobtenerFechaDesde.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/001-calendario.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jCalendar1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jCalendar1PropertyChange(evt);
            }
        });

        txtobtenerFechaHasta.setEditable(false);
        txtobtenerFechaHasta.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtobtenerFechaHasta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtobtenerFechaHastaActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/001-calendario.png"))); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jCalendar2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jCalendar2PropertyChange(evt);
            }
        });

        jLabel38.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel38.setText("Hasta");

        jLabel37.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel37.setText("Desde");

        btnGenerarPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/003-archivo-1.png"))); // NOI18N
        btnGenerarPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarPDFActionPerformed(evt);
            }
        });

        CBVentas.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        CBVentas.setText("Ventas");

        CBCompras.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        CBCompras.setText("Compras");

        CBInventario.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        CBInventario.setText("Inventario");

        CBClientes.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        CBClientes.setText("Clientes");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setForeground(new java.awt.Color(51, 51, 51));
        jLabel13.setText("Nota : ---Para Generar Reportes de Venta y Compra es necesario agregar fecha---");

        javax.swing.GroupLayout menuReportesLayout = new javax.swing.GroupLayout(menuReportes);
        menuReportes.setLayout(menuReportesLayout);
        menuReportesLayout.setHorizontalGroup(
            menuReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuReportesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addGap(469, 469, 469))
            .addGroup(menuReportesLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(menuReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuReportesLayout.createSequentialGroup()
                        .addGroup(menuReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(menuReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(menuReportesLayout.createSequentialGroup()
                                    .addGap(447, 447, 447)
                                    .addComponent(btnGenerarPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 1005, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(603, 603, 603))
                    .addGroup(menuReportesLayout.createSequentialGroup()
                        .addGroup(menuReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CBVentas)
                            .addComponent(jLabel34)
                            .addComponent(CBCompras)
                            .addComponent(CBInventario)
                            .addComponent(CBClientes)
                            .addGroup(menuReportesLayout.createSequentialGroup()
                                .addGroup(menuReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel37)
                                    .addGroup(menuReportesLayout.createSequentialGroup()
                                        .addComponent(txtobtenerFechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(262, 262, 262)
                                .addGroup(menuReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuReportesLayout.createSequentialGroup()
                                        .addComponent(txtobtenerFechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel38)
                                    .addComponent(jCalendar2, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        menuReportesLayout.setVerticalGroup(
            menuReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuReportesLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(CBVentas)
                .addGap(28, 28, 28)
                .addComponent(CBCompras)
                .addGap(37, 37, 37)
                .addComponent(CBInventario)
                .addGap(34, 34, 34)
                .addComponent(CBClientes)
                .addGap(36, 36, 36)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addGroup(menuReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuReportesLayout.createSequentialGroup()
                        .addGroup(menuReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtobtenerFechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(menuReportesLayout.createSequentialGroup()
                                .addComponent(jLabel38)
                                .addGap(12, 12, 12)
                                .addComponent(jButton4)))
                        .addGap(18, 18, 18)
                        .addComponent(jCalendar2, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(menuReportesLayout.createSequentialGroup()
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(menuReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtobtenerFechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jCalendar1, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(btnGenerarPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel13)
                .addGap(48, 48, 48))
        );

        menu.addTab("reportes", menuReportes);

        jButton8.setText("Conectar a la base de datos ");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout menuSoporteLayout = new javax.swing.GroupLayout(menuSoporte);
        menuSoporte.setLayout(menuSoporteLayout);
        menuSoporteLayout.setHorizontalGroup(
            menuSoporteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuSoporteLayout.createSequentialGroup()
                .addGap(226, 226, 226)
                .addComponent(jButton8)
                .addContainerGap(1492, Short.MAX_VALUE))
        );
        menuSoporteLayout.setVerticalGroup(
            menuSoporteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuSoporteLayout.createSequentialGroup()
                .addGap(356, 356, 356)
                .addComponent(jButton8)
                .addContainerGap(511, Short.MAX_VALUE))
        );

        menu.addTab("soporte tecnico", menuSoporte);

        getContentPane().add(menu, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLimpiarCamposClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarCamposClienteActionPerformed
        // Limpiamos los campos del cliente
        txtDocumentoCliente.setText("");
        txtNombresCliente.setText("");
        txtDireccionCliente.setText("");
        txtCelularCliente.setText("");
    }//GEN-LAST:event_btnLimpiarCamposClienteActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void btnRegistrarVentaGeneralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarVentaGeneralActionPerformed

        // obtenemos la conexion, preparamos la sentencias,result set de las llaves generadas y obtenemos los datos de la venta 
        Connection conn = null;
        PreparedStatement pps = null;
        ResultSet rsGenKeys = null;
        PreparedStatement stmInsert = null;

        try {
            // Obtenemos la Conexion
            conn = ConectorBD.obtenerConexion();
            pps = conn.prepareStatement(
                    "INSERT INTO venta (id_cliente, forma_pago, cantidad_articulos, total_pagado) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            // Verificación de valores nulos
            Cliente cliente = (Cliente) listaClienteVenta.getSelectedValue();
            if (cliente == null) {
                JOptionPane.showMessageDialog(null, "Seleccione un cliente", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String metodoPago = (String) cbxmetodoPagoVenta.getSelectedItem();
            if (metodoPago == null) {
                JOptionPane.showMessageDialog(null, "Seleccione un método de pago", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            pps.setInt(1, cliente.getId_cliente());
            pps.setString(2, metodoPago);
            pps.setInt(3, Integer.parseInt(conteoListaProductos.getText()));

            // Manejo de posibles errores en la conversión de texto a entero
            try {
                pps.setInt(4, Integer.parseInt(totalVenta.getText().split(" ")[1]));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Formato de total de venta incorrecto", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // 
            int affectedRows = pps.executeUpdate();

            // Verifica si se afectaron filas 
            if (affectedRows > 0) {
                rsGenKeys = pps.getGeneratedKeys();
                // obtenemos la llave generada de la venta
                if (rsGenKeys.next()) {
                    int idVentaGenerada = rsGenKeys.getInt(1);
                    // Genera una lista de valores para insertar
                    List<String> strGeneradosInsert = ventas
                            .stream()
                            .map(v -> "(" + idVentaGenerada + "," + v.getProducto().getId_producto() + "," + v.getCantidad() + "," + v.getProducto().getPrecio_venta() * v.getCantidad() + ")")
                            .toList();
                    // contruimos la consulta sql para insertar datos
                    String sqlInsertProductos = "INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio) VALUES ";
                    String insertVals = String.join(",", strGeneradosInsert);
                    sqlInsertProductos += insertVals + ";";
                    // ejecuta la insercion en la base de datos
                    stmInsert = conn.prepareStatement(sqlInsertProductos);
                    stmInsert.executeUpdate();
                }
            }

            // Mensaje que confirma que la venta fue exitosa
            JOptionPane.showMessageDialog(null, "VENTA REGISTRADA CON EXITO!", "Mensaje de Informacion", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, "Error al registrar la venta", ex);
        } finally {
            // Cerrar recursos para evitar fugas
            try {
                if (rsGenKeys != null) {
                    rsGenKeys.close();
                }
                if (pps != null) {
                    pps.close();
                }
                if (stmInsert != null) {
                    stmInsert.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, "Error al cerrar recursos", ex);
            }
        }
        mostrarTablaProductosGeneral();
        mostrarTablaProductos();
    }//GEN-LAST:event_btnRegistrarVentaGeneralActionPerformed


    private void btnAñadirProductoVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAñadirProductoVentaActionPerformed
// Definimos nuevas variables y se consiguen los datos de la interfaz 
        Producto producto = (Producto) listaProductoVenta.getSelectedValue();
        int cantidad = (int) sprCantidadVenta.getValue();

// Condicional para verificar si hay inventario 
        if (producto.getStok() < cantidad) {
            JOptionPane.showMessageDialog(null, "No hay suficiente cantidad en el inventario");
            return;
// Condicional para verificar que la cantidad sea superior a 0
        } else if (cantidad < 1) {
            JOptionPane.showMessageDialog(null, "Debe comprar al menos 1 producto");
            return;
        }

// Verificar si el producto ya está en la lista de ventas
        boolean productoExistente = false;
        for (VentaDetalles venta : ventas) {
            if (venta.getProducto().equals(producto)) {
                // Si el producto ya está en la lista, sumamos la cantidad
                int nuevaCantidad = venta.getCantidad() + cantidad;
                if (producto.getStok() < nuevaCantidad) {
                    JOptionPane.showMessageDialog(null, "No hay suficiente cantidad en el inventario para agregar más de este producto");
                    return;
                }
                venta.setCantidad(nuevaCantidad);
                productoExistente = true;
                break;
            }
        }

// Si el producto no está en la lista, lo agregamos
        if (!productoExistente) {
            VentaDetalles nuevaVenta = new VentaDetalles();
            nuevaVenta.setProducto(producto);
            nuevaVenta.setCantidad(cantidad);
            ventas.add(nuevaVenta);
        }

// Mostramos los productos
        mostrarVentas(ventas, tablaListaProductos);
        calcularTotal();

// calcularCambio();
        actualizarCantidadTotalProductos();
    }//GEN-LAST:event_btnAñadirProductoVentaActionPerformed

    private void btnLimpiarCamposVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarCamposVentaActionPerformed
        // Obtener el producto seleccionado de la lista
        int selectedIndex = tablaListaProductos.getSelectedRow();

        // Verificar si hay un producto seleccionado
        if (selectedIndex != -1) {

            // Eliminar el producto de la lista de ventas
            ventas.remove(selectedIndex);

            // Actualizar la tabla de productos
            mostrarVentas(ventas, tablaListaProductos);
            calcularTotal();
            actualizarCantidadTotalProductos();
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un producto para eliminar");
        }
        txtBuscarProductoVenta.setText("");
        sprCantidadVenta.setValue(0);
    }//GEN-LAST:event_btnLimpiarCamposVentaActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void jCalendar1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jCalendar1PropertyChange
        // Agrega un PropertyChangeListener al JCalendar
        jCalendar1.getDayChooser().addPropertyChangeListener("day", e -> {
            // Verifica si existe un valor antiguo
            if (e.getOldValue() != null) {
                // Crea un formato para la fecha
                SimpleDateFormat fechaDesde = new SimpleDateFormat("YYYY-MM-dd");
                // Establecer el texto en un JTextField nombrado (txtobtenerFechaDesde)                
                txtobtenerFechaDesde.setText(fechaDesde.format(jCalendar1.getCalendar().getTime()));
                // Ocultar el calendario después de seleccionar la fecha
                jCalendar1.setVisible(false);
            }
        });
    }//GEN-LAST:event_jCalendar1PropertyChange

    private void jCalendar2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jCalendar2PropertyChange
        // Agrega un PropertyChangeListener al JCalendar
        jCalendar2.getDayChooser().addPropertyChangeListener("day", e -> {
            // Verifica si existe un valor antiguo
            if (e.getOldValue() != null) {
                // Crea un formato para la fecha
                SimpleDateFormat fechaHasta = new SimpleDateFormat("YYYY-MM-dd");
                // Establecer el texto en un JTextField nombrado (txtobtenerFechaDesde)
                txtobtenerFechaHasta.setText(fechaHasta.format(jCalendar2.getCalendar().getTime()));
                // Ocultar el calendario después de seleccionar la fecha
                jCalendar2.setVisible(false);
            }
        });
    }//GEN-LAST:event_jCalendar2PropertyChange

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // Hacer visible o invisible el Calendario
        jCalendar2.setVisible(!jCalendar2.isVisible());
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // Hacer visible o invisible el Calendario
        jCalendar1.setVisible(!jCalendar1.isVisible());
    }//GEN-LAST:event_jButton3ActionPerformed

    private void btnClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientesActionPerformed
        // Cambia la selección del índice del componente "menu" al índice 0.
        menu.setSelectedIndex(0);
    }//GEN-LAST:event_btnClientesActionPerformed

    private void txtobtenerFechaHastaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtobtenerFechaHastaActionPerformed

    }//GEN-LAST:event_txtobtenerFechaHastaActionPerformed

    private void btnProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductoActionPerformed
        // Cambia la selección del índice del componente "menu" al índice 1.
        menu.setSelectedIndex(1);
    }//GEN-LAST:event_btnProductoActionPerformed

    private void btnVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVentasActionPerformed
        // Cambia la selección del índice del componente "menu" al índice 2.
        menu.setSelectedIndex(2);
    }//GEN-LAST:event_btnVentasActionPerformed

    private void btnAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdminActionPerformed
        // Cambia la selección del índice del componente "menu" al índice 5.
        menu.setSelectedIndex(3);
    }//GEN-LAST:event_btnAdminActionPerformed

    private void btnReportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportesActionPerformed
        // Cambia la selección del índice del componente "menu" al índice 6.
        menu.setSelectedIndex(4);
    }//GEN-LAST:event_btnReportesActionPerformed

    private void btnSoporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSoporteActionPerformed
        menu.setSelectedIndex(5);
    }//GEN-LAST:event_btnSoporteActionPerformed

    private void btnGuardarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarClienteActionPerformed
// Método para guardar cliente en la base de datos
        try {
            // Obtenemos la conexión
            var conectar = ConectorBD.obtenerConexion();

            // Obtener y procesar la entrada
            String documento = txtDocumentoCliente.getText().replaceAll("\\s+", "");
            String nombres = txtNombresCliente.getText().trim().replaceAll("\\s+", " ");
            String direccion = txtDireccionCliente.getText().trim().replaceAll("\\s+", " ");
            String celular = txtCelularCliente.getText().trim().replaceAll("\\s+", "");

            // Condicional para verificar si el campo están vacío
            if (documento.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese el Documento");
                return;
            }
            // Validar que el documento solo contenga números
            if (!documento.matches("\\d+")) {
                JOptionPane.showMessageDialog(null, "El Documento solo puede contener números");
                return;
            }
            // Validar que el Número de Documento ingresado sea entre 1 y 10
            if (!documento.matches("\\d{1,10}")) {
                JOptionPane.showMessageDialog(null, "Demasiados Números Ingresados");
                return;
            }

            // Condicional para verificar si el campo están vacío
            if (nombres.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese los Nombres");
                return;
            }
            // Validar que los nombres solo contengan letras y espacios
            if (!nombres.matches("[a-zA-Z ]+")) {
                JOptionPane.showMessageDialog(null, "Los Nombres solo pueden contener letras y espacios");
                return;
            }
            // Validar que el nombre ingresado sea entre 1 y 50 letras
            if (!nombres.matches("[a-zA-Z0-9 ]{1,50}")) {
                JOptionPane.showMessageDialog(null, "Nombre Demasiado Largo");
                return;
            }
            // Condicional para verificar si el campo están vacío
            if (direccion.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese la Dirección");
                return;
            }

            // Validar que la dirección solo contenga letras, números y espacios
            if (!direccion.matches("[a-zA-Z0-9 ]+")) {
                JOptionPane.showMessageDialog(null, "La Dirección solo puede contener letras, números y espacios");
                return;
            }

            // Validar que la dirección Ingresada sea entre 1 y 50 caracteres
            if (!direccion.matches("[a-zA-Z0-9 ]{1,50}")) {
                JOptionPane.showMessageDialog(null, "Dirección demasiado larga");
                return;
            }

            // Condicional para verificar si el campo están vacío
            if (celular.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese el Celular");
                return;
            }

            // Validar que el celular solo contenga números
            if (!celular.matches("\\d+")) {
                JOptionPane.showMessageDialog(null, "El Celular solo puede contener números");
                return;
            }

            // Validar que el celular solo contenga números
            if (!celular.matches("\\d{1,10}")) {
                JOptionPane.showMessageDialog(null, "Número demasiado largo");
                return;
            }

            // Verificar si el documento ya está registrado
            PreparedStatement verificar = conectar.prepareStatement("SELECT * FROM cliente WHERE id_cliente = ?");
            verificar.setString(1, documento);
            ResultSet rs = verificar.executeQuery();
            if (rs.next()) {
                // Si el documento ya está registrado, preguntar si desea actualizar
                int confirmacion = JOptionPane.showConfirmDialog(null, "El documento ya está registrado. ¿Desea actualizar los datos?", "Confirmación de Actualización", JOptionPane.YES_NO_OPTION);
                if (confirmacion == JOptionPane.YES_OPTION) {
                    // Preparar el query para actualizar
                    PreparedStatement actualizar = conectar.prepareStatement("UPDATE cliente SET nombre = ?, direccion = ?, telefono = ? WHERE id_cliente = ?");
                    actualizar.setString(1, nombres);
                    actualizar.setString(2, direccion);
                    actualizar.setString(3, celular);
                    actualizar.setString(4, documento);
                    // Ejecutar la actualización
                    actualizar.executeUpdate();
                    JOptionPane.showMessageDialog(null, "DATOS DEL CLIENTE ACTUALIZADOS", "Mensaje de Información", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                // Preparar el query para insertar
                PreparedStatement pps = conectar.prepareStatement("INSERT INTO cliente (id_cliente, nombre, direccion, telefono) VALUES (?, ?, ?, ?)");
                pps.setString(1, documento);
                pps.setString(2, nombres);
                pps.setString(3, direccion);
                pps.setString(4, celular);
                // Ejecutar la inserción
                pps.executeUpdate();
                JOptionPane.showMessageDialog(null, "DATOS DEL CLIENTE GUARDADOS", "Mensaje de Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Limpiar los campos
        txtDocumentoCliente.setText("");
        txtNombresCliente.setText("");
        txtDireccionCliente.setText("");
        txtCelularCliente.setText("");

        // Método para mostrar los datos en la tabla
        mostrarTablaClientes();
    }//GEN-LAST:event_btnGuardarClienteActionPerformed

    private void btnEliminarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarClienteActionPerformed
        // Método para eliminar un cliente
        int fila = tablaClientes.getSelectedRow();
        // Seleccionamos la fila del cliente a eliminar
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione Un Cliente", "Mensaje de Información", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Obtenemos el id del cliente 
        int id = Integer.parseInt((String) tablaClientes.getValueAt(fila, 0));
        // Preguntamos si confirma la eliminación del cliente
        int confirmacion = JOptionPane.showConfirmDialog(null, "¿Confirma la eliminación del cliente?", "Confirmación de Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            // Query para eliminar el cliente de la base de datos 
            try (PreparedStatement stm = ConectorBD.obtenerConexion().prepareStatement("DELETE FROM cliente WHERE id_cliente=?")) {
                stm.setInt(1, id);
                // Ejecutamos la eliminación
                stm.executeUpdate();
                JOptionPane.showMessageDialog(null, "El Cliente ha sido eliminado", "Mensaje de Información", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Llamamos la tabla actualizada 
            mostrarTablaClientes();
        }
    }//GEN-LAST:event_btnEliminarClienteActionPerformed

    private void tbbAdministradorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbbAdministradorMouseClicked
        // metodo para pasar los paneles de administrador 
        var selectedIndex = tbbAdministrador.getSelectedIndex();
        // se mostrara un panel segun la seleccion del usuario llamando un metodo [ mostrar ] para cada caso
        switch (selectedIndex) {
            case 0 ->
                mostrarTablaCategorias();
            case 1 ->
                mostrarTablaProductos();
            case 2 ->
                mostrarTablaProveedor();
            case 3 ->
                mostrarTablaCompras();
            case 4 ->
                mostrarTablaUsuarios();
        }
    }//GEN-LAST:event_tbbAdministradorMouseClicked

    private void btnLimpiarCamposUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarCamposUsuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLimpiarCamposUsuarioActionPerformed

    private void btnGuardarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarUsuarioActionPerformed
        // obtenemos la conexion con la bd y los datos ingresados por el usuario
        try {
            PreparedStatement pps = ConectorBD.obtenerConexion().prepareStatement("INSERT INTO usuarios (id,nombres,contraseña,permiso)VALUES (?,?,?,?)");
            pps.setString(1, txtidUsuario.getText());
            pps.setString(2, txtnombresUsuario.getText());
            pps.setString(3, String.valueOf(contraseñaUsuario.getPassword()));
            pps.setString(4, ((String) cbxComboPermisos.getSelectedItem()));

            // condicinal para verificar si los campos estan vacios 
            if (txtidUsuario.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese id de Usuario");
                return;
            }

            if (txtnombresUsuario.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese los Nombres Completos");
                return;
            }

            if (String.valueOf(contraseñaUsuario.getPassword()).isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese la Contraseña");
                return;
            }

            if (cbxComboPermisos.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(null, "Seleccione los Permisos ");
                return;
            } else {
            }
            // actualizamos y mostramos al usuario que los datos fueron guardados con exit
            pps.executeUpdate();
            JOptionPane.showMessageDialog(null, "USUARIO GUARDADO", "Mensaje de Informacion", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Limpiamos los campos 
        txtidUsuario.setText("");
        txtnombresUsuario.setText("");
        contraseñaUsuario.setText("");
        mostrarTablaUsuarios();
    }//GEN-LAST:event_btnGuardarUsuarioActionPerformed

    private void btnGuardarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarProductoActionPerformed
// Obtenemos la conexión con la base de datos y los datos ingresados por el usuario
        try {
            // Eliminamos espacios iniciales y finales
            String codigoProducto = txtcodigoProducto.getText().trim().replaceAll("\\s+", "");
            String nombreProducto = txtnombreProducto.getText().trim().replaceAll("\\s+", " ");
            String precioVenta = txtprecioVenta.getText().trim().replaceAll("\\s+", "");

            // Verificamos si los campos están vacíos antes de preparar la consulta
            if (codigoProducto.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese el código del Producto");
                return;
            }
            // Verificamos que el código sea solo números y no más de 10 dígitos
            if (!codigoProducto.matches("\\d{1,10}")) {
                JOptionPane.showMessageDialog(null, "El código del producto debe contener solo números y no más de 10 dígitos");
                return;
            }

            if (nombreProducto.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese el Nombre del Producto");
                return;
            }
            // Verificamos que el nombre del producto sea solo letras y no más de 20 caracteres
            if (!nombreProducto.matches("[a-zA-Z\\s]+{1,20}")) {
                JOptionPane.showMessageDialog(null, "El nombre del producto debe contener solo letras y no más de 20 caracteres");
                return;
            }
            if (precioVenta.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese el Precio de Venta");
                return;
            }
            // Verificamos que el precio de venta sea solo números y no más de 7 dígitos
            if (!precioVenta.matches("\\d{1,7}")) {
                JOptionPane.showMessageDialog(null, "El precio de venta debe contener solo números y no más de 7 dígitos");
                return;
            }

            if (cbxcategoria.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(null, "Seleccione la Categoría");
                return;
            }

            // Verificamos si el código del producto ya está registrado en la base de datos
            PreparedStatement checkStmt = ConectorBD.obtenerConexion().prepareStatement(
                    "SELECT COUNT(*) FROM producto WHERE id_producto = ?"
            );
            checkStmt.setString(1, codigoProducto);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                int opcion = JOptionPane.showConfirmDialog(null, "El código del producto ya está registrado. ¿Desea actualizar el producto?", "Código Duplicado", JOptionPane.YES_NO_OPTION);
                if (opcion == JOptionPane.YES_OPTION) {

                    // Preparamos la consulta SQL para actualizar el producto
                    PreparedStatement updateStmt = ConectorBD.obtenerConexion().prepareStatement(
                            "UPDATE producto SET nombre = ?, precio_venta = ?, categoria = ? WHERE id_producto = ?"
                    );
                    updateStmt.setString(1, nombreProducto);
                    updateStmt.setInt(2, Integer.parseInt(precioVenta));
                    updateStmt.setInt(3, ((Categorias) cbxcategoria.getSelectedItem()).getId());
                    updateStmt.setString(4, codigoProducto);

                    // Ejecutamos la actualización y mostramos un mensaje de éxito al usuario
                    updateStmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "PRODUCTO ACTUALIZADO", "Mensaje de Información", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    return;
                }
            } else {
                // Preparamos la consulta SQL para insertar el producto
                PreparedStatement pps = ConectorBD.obtenerConexion().prepareStatement(
                        "INSERT INTO producto (id_producto, nombre, precio_venta, categoria, stok) VALUES (?, ?, ?, ?, 0)"
                );
                pps.setString(1, codigoProducto);
                pps.setString(2, nombreProducto);
                pps.setInt(3, Integer.parseInt(precioVenta));
                pps.setInt(4, ((Categorias) cbxcategoria.getSelectedItem()).getId());

                // Ejecutamos la actualización y mostramos un mensaje de éxito al usuario
                pps.executeUpdate();
                JOptionPane.showMessageDialog(null, "PRODUCTO GUARDADO", "Mensaje de Información", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Limpiamos los campos
        txtcodigoProducto.setText("");
        txtnombreProducto.setText("");
        txtprecioVenta.setText("");
        cbxcategoria.setSelectedIndex(-1); // Reseteamos la selección de la categoría
        mostrarTablaProductos();

    }//GEN-LAST:event_btnGuardarProductoActionPerformed

    private void btnLimpiarCamposProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarCamposProductoActionPerformed
        // limpiamos los campos 
        txtcodigoProducto.setText("");
        txtNombresCliente.setText("");
        txtDireccionCliente.setText("");
    }//GEN-LAST:event_btnLimpiarCamposProductoActionPerformed

    private void btnBorrarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarProductoActionPerformed
        // metodo para eliminar un Producto
        int fila = tablaAdministrador.getSelectedRow();

        // Seleccionamos la fila que Queremos Eliminar
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione Un Producto", "Mensaje de Infromacion", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Query para eliminar Producto por id
        int id = Integer.parseInt((String) tablaAdministrador.getValueAt(fila, 0));
        try (PreparedStatement stm = ConectorBD.obtenerConexion().prepareStatement("DELETE FROM producto WHERE id_producto=?")) {
            stm.setInt(1, id);
            // Actualizamos 
            stm.executeUpdate();
            JOptionPane.showMessageDialog(null, "El Producto a Sido Eliminado", "Mensaje de Informacion", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
        }
        // Llamamos el metodo actualizado
        mostrarTablaProductos();
    }//GEN-LAST:event_btnBorrarProductoActionPerformed

    private void cbxcategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxcategoriaActionPerformed

    }//GEN-LAST:event_cbxcategoriaActionPerformed

    private void btnCancelarRegistroCliente1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarRegistroCliente1ActionPerformed
        // metodo para eliminar un Producto
        int fila = tablaAdministrador.getSelectedRow();
        // selecionamos la fila 
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione Una Categoria", "Mensaje de Infromacion", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // eliminamos la fila del producto por id
        int id = ((int) tablaAdministrador.getValueAt(fila, 0));
        try (PreparedStatement stm = ConectorBD.obtenerConexion().prepareStatement("DELETE FROM categoria WHERE id=?")) {
            stm.setInt(1, id);
            // Actualizamos 
            stm.executeUpdate();
            JOptionPane.showMessageDialog(null, "La Categoria A Sido Eliminada", "Mensaje de Informacion", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
        }
        // llamamos al metodo actualizado
        mostrarTablaCategorias();
    }//GEN-LAST:event_btnCancelarRegistroCliente1ActionPerformed

    private void btnLimpiarCamposCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarCamposCategoriaActionPerformed
        txtcategoria.setText("");
    }//GEN-LAST:event_btnLimpiarCamposCategoriaActionPerformed

    private void btnGuardarCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarCategoriaActionPerformed
// metodo para guardar Categorias
        try {
            // Obtenemos la conexion
            var conectar = ConectorBD.obtenerConexion();
            // Preparamos el Query
            PreparedStatement pps = conectar.prepareStatement("INSERT INTO categoria (nombre) VALUES (?)");

            // Obtener y procesar la entrada
            String categoria = txtcategoria.getText().trim();
            String[] palabras = categoria.split("\\s+");
            String categoriaProcesada = String.join(" ", palabras);

            if (categoriaProcesada.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese el Nombre de la Categoria");
                return;
            }
            // Validar que solo se ingresen letras y espacios
            if (!categoriaProcesada.matches("[a-zA-Z ]+")) {
                JOptionPane.showMessageDialog(null, "Ingrese solo letras y espacios para el Nombre de la Categoria");
                return;
            }
            // Validar que el nombre ingresado sea entre 1 y 50 caracteres
            if (categoriaProcesada.length() > 50) {
                JOptionPane.showMessageDialog(null, "Nombre Demasiado Largo");
                return;
            } else {
                pps.setString(1, categoriaProcesada);
                pps.executeUpdate();
                JOptionPane.showMessageDialog(null, "CATEGORIA GUARDADA", "Mensaje de Informacion", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
        txtcategoria.setText("");
        mostrarTablaCategorias();
        mostrarCategoriascbx();

    }//GEN-LAST:event_btnGuardarCategoriaActionPerformed

    private void btnLimpiarCamposProveedores1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarCamposProveedores1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLimpiarCamposProveedores1ActionPerformed

    private void btnGuardarProveedores1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarProveedores1ActionPerformed
// Método para guardar proveedores en la base de datos
        try {
            // Eliminamos espacios iniciales y finales y ajustamos los campos
            String razonSocial = txtRazonSocial.getText().trim().replaceAll("\\s+", " ");
            String numeroDocumento = txtNumeroDocumentoProveedor.getText().trim().replaceAll("\\s+", "");
            String telefonoProveedor = txtTelefonoProveedor.getText().trim().replaceAll("\\s+", "");

            // Verificamos si los campos están vacíos
            if (razonSocial.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese la Razón Social");
                return;
            }
            // Verificamos que Razón Social y Nombre del Proveedor sean solo letras y no más de 50 caracteres
            if (!razonSocial.matches("[a-zA-Z\\s0-9]+{1,50}")) {
                JOptionPane.showMessageDialog(null, "La Razón Social debe contener solo letras y no más de 50 caracteres");
                return;
            }
            // Verificamos si los campos están vacíos
            if (numeroDocumento.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese el Número de Documento");
                return;
            }
            // Verificamos que Número de Documento solo sea números y solo 10 dígitos
            if (!numeroDocumento.matches("\\d{1,10}")) {
                JOptionPane.showMessageDialog(null, "El Número de Documento debe contener solo números y solo 10 dígitos");
                return;
            }
            // Verificamos si los campos están vacíos
            if (telefonoProveedor.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese el Teléfono");
                return;
            }
            if (!telefonoProveedor.matches("\\d{1,10}+")) {
                JOptionPane.showMessageDialog(null, "El Teléfono debe contener solo números y solo 10 dígitos");
                return;
            }

            // Obtenemos la conexión
            var conectar = ConectorBD.obtenerConexion();

            // Verificamos si el número de documento ya existe en la base de datos
            PreparedStatement verificarPps = conectar.prepareStatement("SELECT COUNT(*) FROM proveedor WHERE numero_documento = ?");
            verificarPps.setString(1, numeroDocumento);
            ResultSet rs = verificarPps.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                int opcion = JOptionPane.showConfirmDialog(null, "El número de documento ya existe. ¿Desea actualizar la información del proveedor?", "Actualizar Proveedor", JOptionPane.YES_NO_OPTION);
                if (opcion == JOptionPane.YES_OPTION) {
                    // Actualizamos la información del proveedor
                    PreparedStatement actualizarPps = conectar.prepareStatement("UPDATE proveedor SET nombre = ?, tipo_documento = ?, telefono = ? WHERE numero_documento = ?");
                    actualizarPps.setString(1, razonSocial);
                    actualizarPps.setString(2, (String) cbxProveedores.getSelectedItem());
                    actualizarPps.setString(3, telefonoProveedor);
                    actualizarPps.setString(4, numeroDocumento);
                    actualizarPps.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Información del proveedor actualizada", "Mensaje de Información", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                // Preparamos el query para insertar un nuevo proveedor
                PreparedStatement pps = conectar.prepareStatement("INSERT INTO proveedor (nombre, tipo_documento, numero_documento, telefono) VALUES (?, ?, ?, ?)");
                pps.setString(1, razonSocial);
                pps.setString(2, (String) cbxProveedores.getSelectedItem());
                pps.setString(3, numeroDocumento);
                pps.setString(4, telefonoProveedor);
                pps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Datos del proveedor guardados", "Mensaje de Información", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Limpiamos los campos
        txtRazonSocial.setText("");
        txtNumeroDocumentoProveedor.setText("");
        txtTelefonoProveedor.setText("");
        cbxProveedores.setSelectedIndex(-1); // Reseteamos la selección de la categoría
        mostrarTablaProveedor();

    }//GEN-LAST:event_btnGuardarProveedores1ActionPerformed

    private void cbxComprobanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxComprobanteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbxComprobanteActionPerformed

    private void btnLimpiarCamposCompra1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarCamposCompra1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLimpiarCamposCompra1ActionPerformed

    private void btnGuardarCompra1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarCompra1ActionPerformed
        // Validación de datos
        if (!txtCantidadProducto.getText().matches("\\d{1,7}+") || txtCantidadProducto.getText().contains(" ") || Integer.parseInt(txtCantidadProducto.getText()) == 0) {
            JOptionPane.showMessageDialog(null, "El campo 'Cantidad' debe ser numérico, no contener espacios y no puede ser cero.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!txtPrecioCompra.getText().matches("\\d{1,7}+") || txtPrecioCompra.getText().contains(" ") || Integer.parseInt(txtPrecioCompra.getText()) == 0) {
            JOptionPane.showMessageDialog(null, "El campo 'Precio' debe ser numérico, no contener espacios y no puede ser cero.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!txtNumeroComprobante.getText().matches("\\d{1,10}+") || txtNumeroComprobante.getText().contains(" ")) {
            JOptionPane.showMessageDialog(null, "El campo 'Número Comprobante' debe ser numérico y no contener espacios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtenemos el ID del producto seleccionado
        int idProducto = ((Producto) ListaProductosCompra.getSelectedValue()).getId_producto();
        int precioCompra = Integer.parseInt(txtPrecioCompra.getText());

        // Consultamos el precio de venta del producto en la base de datos
        int precioVenta = 0;
        try {
            PreparedStatement pps = ConectorBD.obtenerConexion().prepareStatement("SELECT precio_venta FROM producto WHERE id_producto = ?");
            pps.setInt(1, idProducto);
            ResultSet rs = pps.executeQuery();
            if (rs.next()) {
                precioVenta = rs.getInt("precio_venta");
                // JOptionPane.showMessageDialog(null, "El precio de venta actual del producto es: " + precioVenta, "Información del Producto", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Producto no encontrado en la base de datos.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        // Verificamos que el precio de compra no sea inferior ni igual al precio de venta y Mostrar el precio de venta actual al usuario
        if (precioCompra <= precioVenta) {
            JOptionPane.showMessageDialog(null, "El precio de compra no puede ser inferior ni igual al precio de venta.\nEl precio de venta actual del producto consultado es: " + precioVenta, "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // obtenemos la conexion y obtenemos los datos de la compra 
        try {
            PreparedStatement pps = ConectorBD.obtenerConexion().prepareStatement("INSERT INTO compra (producto, cantidad, precio_unidad, precio_total_compra, comprobante, numero_comprobante, tipo_ingreso, proveedor)VALUES (?,?,?,?,?,?,?,?)");
            pps.setInt(1, ((Producto) ListaProductosCompra.getSelectedValue()).getId_producto());
            pps.setInt(2, Integer.parseInt(txtCantidadProducto.getText()));
            pps.setInt(3, Integer.parseInt(txtPrecioCompra.getText()));
            pps.setInt(4, Integer.parseInt(totalSumatoriaCompraPreductos.getText()));
            pps.setString(5, (String) cbxComprobante.getSelectedItem());
            pps.setInt(6, Integer.parseInt(txtNumeroComprobante.getText()));
            pps.setString(7, (String) cbxTipoIngreso.getSelectedItem());
            pps.setInt(8, ((Proveedor) listaProveedores.getSelectedValue()).getNumero_documento());
            // Actualizamos los productos en la BD 
            pps.executeUpdate();
            // Mensaje que Confirma que los productos ya Fueron Guardados
            JOptionPane.showMessageDialog(null, "PRODUCTO COMPRADO CON EXITO!", "Mensaje de Informacion", JOptionPane.INFORMATION_MESSAGE);
            mostrarTablaProductosGeneral();
        } catch (SQLException ex) {
            Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Limpiamos los campos
        txtBuscarProductoCompra.setText("");
        txtCantidadProducto.setText("");
        txtPrecioCompra.setText("");
        totalSumatoriaCompraPreductos.setText("");
        txtNumeroComprobante.setText("");
        txtBuscarProveedorCompra.setText("");

        mostrarTablaCompras();

    }//GEN-LAST:event_btnGuardarCompra1ActionPerformed

    private void txtCantidadProductoCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtCantidadProductoCaretUpdate
        mostrarTotal();
    }//GEN-LAST:event_txtCantidadProductoCaretUpdate

    private void txtPrecioCompraCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtPrecioCompraCaretUpdate
        mostrarTotal();
    }//GEN-LAST:event_txtPrecioCompraCaretUpdate

    private void txtBuscarProductoCompraCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtBuscarProductoCompraCaretUpdate
        String producto = txtBuscarProductoCompra.getText();

        if (producto.isEmpty() || producto.isBlank()) {
            return;
        }

        String sql = "SELECT id_producto, nombre FROM producto WHERE nombre LIKE ?";
        SetterStatement setter = (stm) -> {
            stm.setString(1, "%" + producto + "%");
        };

        CreadorObjeto creador = (rs) -> {
            Producto p = new Producto();
            p.setId_producto(rs.getInt(1));
            p.setNombre(rs.getString(2));
            return p;
        };

        mostrarDatosLista(sql, ListaProductosCompra, setter, creador);
    }//GEN-LAST:event_txtBuscarProductoCompraCaretUpdate

    private void ListaProductosCompraComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_ListaProductosCompraComponentResized
        // TODO add your handling code here:
    }//GEN-LAST:event_ListaProductosCompraComponentResized

    private void btnBuscarProveedorCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarProveedorCompraActionPerformed

        String proveedor = txtBuscarProveedorCompra.getText();

        if (proveedor.isEmpty() || proveedor.isBlank()) {
            return;
        }

        String sql = "SELECT numero_documento, nombre FROM proveedor WHERE nombre LIKE ?";
        SetterStatement setter = (stm) -> {
            stm.setString(1, "%" + proveedor + "%");
        };

        CreadorObjeto creador = (rs) -> {
            Proveedor p = new Proveedor();
            p.setNumero_documento(rs.getInt(1));
            p.setNombre(rs.getString(2));
            return p;
        };

        mostrarDatosLista(sql, listaProveedores, setter, creador);
    }//GEN-LAST:event_btnBuscarProveedorCompraActionPerformed

    private void btnBuscarCliente2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarCliente2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBuscarCliente2ActionPerformed

    private void btnBuscarCliente3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarCliente3ActionPerformed

    }//GEN-LAST:event_btnBuscarCliente3ActionPerformed

    private void txtBuscarProductoCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarProductoCompraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarProductoCompraActionPerformed

    private void txtBuscarProductoVentaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtBuscarProductoVentaCaretUpdate
        //Metodo para mostrar los productos ingresados en un jtextList
        // Obtenemos el producto ingresado y lo almacenamos en una variable < "producto" >
        String producto = txtBuscarProductoVenta.getText();
        // Condicional para verificar si el jtextField esta vacio o en blanco
        if (producto.isEmpty() || producto.isBlank()) {
            return;
        }
        // Guardamos el Query de consulta en la variable sql y establecemos su argumento 
        String sql = "SELECT id_producto, nombre, precio_venta, stok FROM producto WHERE nombre LIKE ?";
        SetterStatement setter = (stm) -> {
            stm.setString(1, "%" + producto + "%");
        };
        // se hace uso de la interfaz funcional creador objeto 
        CreadorObjeto creador = (rs) -> {
            // Instanciamos producto, obtenemos los datos y los almacenamos en p
            Producto p = new Producto();
            p.setId_producto(rs.getInt(1));
            p.setNombre(rs.getString(2));
            p.setPrecio_venta(rs.getInt(3));
            p.setStok(rs.getInt(4));

            return p;
        };
        // mostramos el todo en la tabla lista de productos venta 
        mostrarDatosLista(sql, listaProductoVenta, setter, creador);
    }//GEN-LAST:event_txtBuscarProductoVentaCaretUpdate

    private void BuscarClienteVentaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_BuscarClienteVentaCaretUpdate
// Método para buscar al cliente en la venta
        String cliente = BuscarClienteVenta.getText();

// Si el campo está vacío o en blanco, no retorna nada
        if (cliente.isEmpty() || cliente.isBlank()) {
            return;
        }

// Consulta al cliente en la base de datos
        String sql = "SELECT id_cliente, nombre FROM cliente WHERE nombre LIKE ?";

// Configuración del parámetro de la consulta
        SetterStatement setter = (stm) -> {
            stm.setString(1, "%" + cliente + "%");
        };

// Creación del objeto Cliente a partir del resultado de la consulta
        CreadorObjeto creador = (rs) -> {
            Cliente c = new Cliente();
            c.setId_cliente(rs.getInt(1));
            c.setNombre(rs.getString(2));
            return c;
        };
        mostrarDatosLista(sql, listaClienteVenta, setter, creador);
    }//GEN-LAST:event_BuscarClienteVentaCaretUpdate

    private void txtCambioCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtCambioCaretUpdate
        // Metodo para calcular el cambio 
        // Obtenemos el dinero ingresado 
        String cambioStr = txtCambio.getText();
        // condicional para verificar si el campo esta vacio o en blanco 
        if (cambioStr.isEmpty() || cambioStr.isBlank()) {
            // Modificamos el Cambio 
            totalCambio.setText("$ .");
            return;
        }
        // obtenemos la venta total, Partimos con el split la Cadena y nos quedamos con el 2do dato
        int ventaTotal = Integer.parseInt(totalVenta.getText().split(" ")[1]);
        // casteamos el cambio a int 
        int cambio = Integer.parseInt(cambioStr);
        // Restamos el cambio a la venta
        int totalc = cambio - ventaTotal;
        // el resultado del cambio lo agrregamos al label totalCambio
        totalCambio.setText("$ " + totalc);


    }//GEN-LAST:event_txtCambioCaretUpdate

    private void btnGenerarPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarPDFActionPerformed
        Excel excel1 = new Excel();
        excel1.reporte();
    }//GEN-LAST:event_btnGenerarPDFActionPerformed

    private void btnCancelarRegistroCompra1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarRegistroCompra1ActionPerformed
// Código para eliminar productos seleccionados

        int selectedRow = tablaAdministrador.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar el producto seleccionado?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int productId = (int) tablaAdministrador.getValueAt(selectedRow, 0);
                    PreparedStatement pps = ConectorBD.obtenerConexion().prepareStatement("DELETE FROM productos WHERE id_producto = ?");
                    pps.setInt(1, productId);
                    pps.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Producto eliminado con éxito.", "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                    // Actualizar la tabla después de la eliminación
                    mostrarTablaProductos();
                } catch (SQLException ex) {
                    Logger.getLogger(MenuPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un producto para eliminar.", "Error de Selección", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCancelarRegistroCompra1ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // Obtenemos una conexion a la base de datos
        var conectar = ConectorBD.obtenerConexion();
        // comprobamos la conexion
        if (conectar == null) {
            JOptionPane.showMessageDialog(null, "no conectado");
        } else {
            JOptionPane.showMessageDialog(null, "CONECTADO");
        }
    }//GEN-LAST:event_jButton8ActionPerformed

// Metodo para mostrar el total de la compra
    public void mostrarTotal() {
        if (txtPrecioCompra.getText().isEmpty() || txtPrecioCompra.getText().isBlank()) {
            return;
        } else if (txtCantidadProducto.getText().isEmpty() || txtCantidadProducto.getText().isBlank()) {
            return;
        }

        int precio = Integer.parseInt(txtPrecioCompra.getText());
        int cantidad = Integer.parseInt(txtCantidadProducto.getText());
        int res = precio * cantidad;

        totalSumatoriaCompraPreductos.setText(String.valueOf(res));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
            * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField BuscarClienteVenta;
    private javax.swing.JCheckBox CBClientes;
    private javax.swing.JCheckBox CBCompras;
    private javax.swing.JCheckBox CBInventario;
    private javax.swing.JCheckBox CBVentas;
    private javax.swing.JList ListaProductosCompra;
    private javax.swing.JButton btnAdmin;
    private javax.swing.JButton btnAñadirProductoVenta;
    private javax.swing.JButton btnBorrarProducto;
    private javax.swing.JButton btnBuscarCliente;
    private javax.swing.JButton btnBuscarCliente2;
    private javax.swing.JButton btnBuscarCliente3;
    private javax.swing.JButton btnBuscarProveedorCompra;
    private javax.swing.JButton btnCancelarRegistroCliente1;
    private javax.swing.JButton btnCancelarRegistroCompra1;
    private javax.swing.JButton btnClientes;
    private javax.swing.JButton btnEliminarCliente;
    private javax.swing.JButton btnEliminarProveedores1;
    private javax.swing.JButton btnEliminarUsuario;
    private javax.swing.JButton btnGenerarPDF;
    private javax.swing.JButton btnGuardarCategoria;
    private javax.swing.JButton btnGuardarCliente;
    private javax.swing.JButton btnGuardarCompra1;
    private javax.swing.JButton btnGuardarProducto;
    private javax.swing.JButton btnGuardarProveedores1;
    private javax.swing.JButton btnGuardarUsuario;
    private javax.swing.JButton btnLimpiarCamposCategoria;
    private javax.swing.JButton btnLimpiarCamposCliente;
    private javax.swing.JButton btnLimpiarCamposCompra1;
    private javax.swing.JButton btnLimpiarCamposProducto;
    private javax.swing.JButton btnLimpiarCamposProveedores1;
    private javax.swing.JButton btnLimpiarCamposUsuario;
    private javax.swing.JButton btnLimpiarCamposVenta;
    private javax.swing.JButton btnProducto;
    private javax.swing.JButton btnRegistrarVentaGeneral;
    private javax.swing.JButton btnReportes;
    private javax.swing.JButton btnSalir;
    private javax.swing.JButton btnSoporte;
    private javax.swing.JButton btnVentas;
    private javax.swing.JComboBox<String> cbxComboPermisos;
    private javax.swing.JComboBox<String> cbxComprobante;
    private javax.swing.JComboBox<String> cbxProveedores;
    private javax.swing.JComboBox<String> cbxTipoIngreso;
    private javax.swing.JComboBox<String> cbxcategoria;
    private javax.swing.JComboBox cbxmetodoPagoVenta;
    private javax.swing.JLabel conteoListaProductos;
    private javax.swing.JPasswordField contraseñaUsuario;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton8;
    private com.toedter.calendar.JCalendar jCalendar1;
    private com.toedter.calendar.JCalendar jCalendar2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JList listaClienteVenta;
    private javax.swing.JList listaProductoVenta;
    private javax.swing.JList listaProveedores;
    private javax.swing.JTabbedPane menu;
    private javax.swing.JPanel menuAdministrador;
    private javax.swing.JPanel menuCliente;
    private javax.swing.JPanel menuProducto;
    private javax.swing.JPanel menuReportes;
    private javax.swing.JPanel menuSoporte;
    private javax.swing.JPanel menuVentas;
    private javax.swing.JPanel panelCategoria;
    private javax.swing.JPanel panelCompras;
    private javax.swing.JPanel panelProducto;
    private javax.swing.JPanel panelProveedores;
    private javax.swing.JPanel panelUsuario;
    private javax.swing.JSpinner sprCantidadVenta;
    private javax.swing.JTable tablaAdministrador;
    private javax.swing.JTable tablaClientes;
    private javax.swing.JTable tablaListaProductos;
    private javax.swing.JTable tablaProductosGeneral;
    private javax.swing.JTabbedPane tbbAdministrador;
    private javax.swing.JLabel totalCambio;
    private javax.swing.JLabel totalSumatoriaCompraPreductos;
    private javax.swing.JLabel totalVenta;
    private javax.swing.JTextField txtBuscarProductoCompra;
    private javax.swing.JTextField txtBuscarProductoVenta;
    private javax.swing.JTextField txtBuscarProveedorCompra;
    private javax.swing.JTextField txtCambio;
    private javax.swing.JTextField txtCantidadProducto;
    private javax.swing.JTextField txtCelularCliente;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtCliente1;
    private javax.swing.JTextField txtCliente2;
    private javax.swing.JTextField txtDireccionCliente;
    private javax.swing.JTextField txtDocumentoCliente;
    private javax.swing.JTextField txtNombresCliente;
    private javax.swing.JTextField txtNumeroComprobante;
    private javax.swing.JTextField txtNumeroDocumentoProveedor;
    private javax.swing.JTextField txtPrecioCompra;
    private javax.swing.JTextField txtRazonSocial;
    private javax.swing.JTextField txtTelefonoProveedor;
    private javax.swing.JTextField txtcategoria;
    private javax.swing.JTextField txtcodigoProducto;
    private javax.swing.JTextField txtidUsuario;
    private javax.swing.JTextField txtnombreProducto;
    private javax.swing.JTextField txtnombresUsuario;
    private javax.swing.JTextField txtobtenerFechaDesde;
    private javax.swing.JTextField txtobtenerFechaHasta;
    private javax.swing.JTextField txtprecioVenta;
    // End of variables declaration//GEN-END:variables

}
