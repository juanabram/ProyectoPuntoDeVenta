package application.Controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Clase VistaControlador:
 * Contiene las declaraciones de los elementos visuales (@FXML) de todas las pestañas
 * y sus métodos de acceso (getters y setters).
 */
public class VistaControlador {

    // ==========================================
    // MENÚ SUPERIOR
    // ==========================================
    @FXML private MenuItem menuNuevaVenta;
    @FXML private MenuItem menuCancelarVenta;
    @FXML private MenuItem menuSalir;
    
    @FXML private MenuItem menuNavVentas;
    @FXML private MenuItem menuNavProductos;
    @FXML private MenuItem menuNavInventario;
    @FXML private MenuItem menuNavDevoluciones;
    @FXML private MenuItem menuNavReportes;
    @FXML private MenuItem menuNavIA;
    
    @FXML private MenuItem menuAtajos;
    @FXML private MenuItem menuAcercaDe;

    // ==========================================
    // TOOLBAR DE NAVEGACIÓN
    // ==========================================
    @FXML private Button btnNavVentas;
    @FXML private Button btnNavProductos;
    @FXML private Button btnNavInventario;
    @FXML private Button btnNavDevoluciones;
    @FXML private Button btnNavReportes;
    @FXML private Button btnNavIA;
    
    @FXML private Label lblSesion;
    @FXML private Label lblFechaHora;

    // ==========================================
    // TABPANE PRINCIPAL Y PESTAÑAS
    // ==========================================
    @FXML private TabPane tabPanePrincipal;
    @FXML private Tab tabVentas;
    @FXML private Tab tabProductos;
    @FXML private Tab tabInventario;
    @FXML private Tab tabDevoluciones;
    @FXML private Tab tabReportes;
    @FXML private Tab tabIA;

    // ==========================================
    // PESTAÑA 1: CAJA / VENTAS
    // ==========================================
    @FXML private TextField txtCodigoBarras;
    @FXML private TextField txtCantidad;
    @FXML private Button btnAgregar;
    @FXML private Button btnBuscar;

    @SuppressWarnings("rawtypes") @FXML private TableView tblProductos;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colNumero;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colCodigo;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colDescripcion;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colPrecio;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colCantidad;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colDescuento;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colImporte;

    @FXML private Button btnEliminarArticulo;
    @FXML private Button btnModificarCantidad;
    @FXML private Button btnAplicarDescuento;
    @FXML private Button btnCancelarVenta;

    @FXML private Label lblNumArticulos;
    @FXML private Label lblSubtotal;
    @FXML private Label lblDescuento;
    @FXML private Label lblIva;
    @FXML private Label lblTotal;

    @FXML private ComboBox<String> cmbMetodoPago;
    @FXML private TextField txtPagaCon;
    @FXML private Label lblCambio;
    @FXML private CheckBox chkTicket;
    @FXML private Button btnCobrar;

    // ==========================================
    // PESTAÑA 2: PRODUCTOS Y CATEGORÍAS
    // ==========================================
    @FXML private TextField txtBuscarCatalogo;
    @FXML private ComboBox<String> cmbFiltroCategoria;
    @FXML private Button btnFiltrarProductos;
    @FXML private Button btnRecargarCatalogo;

    @SuppressWarnings("rawtypes") @FXML private TableView tblCatalogoProductos;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colCatCodigo;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colCatNombre;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colCatCategoria;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colCatSubcategoria;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colCatPrecio;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colCatStock;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colCatStockMin;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colCatEstado;

    @FXML private Button btnNuevoProducto;
    @FXML private Button btnModificarProducto;
    @FXML private Button btnBajaLogicaProducto;
    @FXML private Button btnAdministrarCategorias;
    @FXML private Button btnAdministrarImagenes;
    @FXML private Label lblTotalProductosRegistrados;

    // ==========================================
    // PESTAÑA 3: INVENTARIO Y STOCK
    // ==========================================
    @FXML private ComboBox<String> cmbFiltroInventario;
    @FXML private CheckBox chkSoloBajoStock;
    @FXML private Button btnRefrescarInventario;

    @SuppressWarnings("rawtypes") @FXML private TableView tblInventario;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colInvCodigo;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colInvProducto;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colInvCategoria;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colInvExistencias;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colInvStockMinimo;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colInvEstado;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colInvUltimoMovimiento;

    @FXML private Button btnEntradaStock;
    @FXML private Button btnSalidaStock;
    @FXML private Button btnAjusteStock;
    @FXML private Button btnGenerarReporteStock;

    // ==========================================
    // PESTAÑA 4: DEVOLUCIONES
    // ==========================================
    @FXML private TextField txtFolioDevolucion;
    @FXML private Button btnBuscarTicketDevolucion;
    @FXML private Label lblInfoTicketDevolucion;

    @SuppressWarnings("rawtypes") @FXML private TableView tblDevoluciones;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colDevCodigo;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colDevDescripcion;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colDevPrecio;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colDevCantComprada;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colDevCantDevolver;
    @SuppressWarnings("rawtypes") @FXML private TableColumn colDevSubtotalReembolso;

    @FXML private ComboBox<String> cmbMotivoDevolucion;
    @FXML private CheckBox chkReintegrarStock;
    @FXML private Button btnProcesarDevolucion;

    // ==========================================
    // PESTAÑA 5: REPORTES (JASPERREPORTS)
    // ==========================================
    @FXML private ComboBox<String> cmbTipoReporte;
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private ComboBox<String> cmbCategoriaReporte;
    @FXML private Button btnVistaPreviaReporte;
    @FXML private Button btnExportarPDF;
    @FXML private Label lblEstadoReporte;
    @FXML private TextArea txtDetallesReporte;

    // ==========================================
    // PESTAÑA 6: ASISTENTE IA
    // ==========================================
    @FXML private ComboBox<String> cmbFuncionIA;
    @FXML private Button btnEjecutarIA;
    @FXML private TextArea txtResultadoIA;
    @FXML private Button btnAplicarRecomendacionIA;

    // ==========================================
    // BARRA INFERIOR (ESTADO Y ALERTAS)
    // ==========================================
    @FXML private Label lblAlertaStock;
    @FXML private Label lblMensajeEstado;

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public MenuItem getMenuNuevaVenta() { return menuNuevaVenta; }
    public MenuItem getMenuCancelarVenta() { return menuCancelarVenta; }
    public MenuItem getMenuSalir() { return menuSalir; }

    public MenuItem getMenuNavVentas() { return menuNavVentas; }
    public MenuItem getMenuNavProductos() { return menuNavProductos; }
    public MenuItem getMenuNavInventario() { return menuNavInventario; }
    public MenuItem getMenuNavDevoluciones() { return menuNavDevoluciones; }
    public MenuItem getMenuNavReportes() { return menuNavReportes; }
    public MenuItem getMenuNavIA() { return menuNavIA; }

    public MenuItem getMenuAtajos() { return menuAtajos; }
    public MenuItem getMenuAcercaDe() { return menuAcercaDe; }

    public Button getBtnNavVentas() { return btnNavVentas; }
    public Button getBtnNavProductos() { return btnNavProductos; }
    public Button getBtnNavInventario() { return btnNavInventario; }
    public Button getBtnNavDevoluciones() { return btnNavDevoluciones; }
    public Button getBtnNavReportes() { return btnNavReportes; }
    public Button getBtnNavIA() { return btnNavIA; }

    public Label getLblSesion() { return lblSesion; }
    public Label getLblFechaHora() { return lblFechaHora; }

    public TabPane getTabPanePrincipal() { return tabPanePrincipal; }
    public Tab getTabVentas() { return tabVentas; }
    public Tab getTabProductos() { return tabProductos; }
    public Tab getTabInventario() { return tabInventario; }
    public Tab getTabDevoluciones() { return tabDevoluciones; }
    public Tab getTabReportes() { return tabReportes; }
    public Tab getTabIA() { return tabIA; }

    // Ventas
    public TextField getTxtCodigoBarras() { return txtCodigoBarras; }
    public TextField getTxtCantidad() { return txtCantidad; }
    public Button getBtnAgregar() { return btnAgregar; }
    public Button getBtnBuscar() { return btnBuscar; }

    @SuppressWarnings("rawtypes") public TableView getTblProductos() { return tblProductos; }
    @SuppressWarnings("rawtypes") public TableColumn getColNumero() { return colNumero; }
    @SuppressWarnings("rawtypes") public TableColumn getColCodigo() { return colCodigo; }
    @SuppressWarnings("rawtypes") public TableColumn getColDescripcion() { return colDescripcion; }
    @SuppressWarnings("rawtypes") public TableColumn getColPrecio() { return colPrecio; }
    @SuppressWarnings("rawtypes") public TableColumn getColCantidad() { return colCantidad; }
    @SuppressWarnings("rawtypes") public TableColumn getColDescuento() { return colDescuento; }
    @SuppressWarnings("rawtypes") public TableColumn getColImporte() { return colImporte; }

    public Button getBtnEliminarArticulo() { return btnEliminarArticulo; }
    public Button getBtnModificarCantidad() { return btnModificarCantidad; }
    public Button getBtnAplicarDescuento() { return btnAplicarDescuento; }
    public Button getBtnCancelarVenta() { return btnCancelarVenta; }

    public Label getLblNumArticulos() { return lblNumArticulos; }
    public Label getLblSubtotal() { return lblSubtotal; }
    public Label getLblDescuento() { return lblDescuento; }
    public Label getLblIva() { return lblIva; }
    public Label getLblTotal() { return lblTotal; }

    public ComboBox<String> getCmbMetodoPago() { return cmbMetodoPago; }
    public TextField getTxtPagaCon() { return txtPagaCon; }
    public Label getLblCambio() { return lblCambio; }
    public CheckBox getChkTicket() { return chkTicket; }
    public Button getBtnCobrar() { return btnCobrar; }

    // Productos
    public TextField getTxtBuscarCatalogo() { return txtBuscarCatalogo; }
    public ComboBox<String> getCmbFiltroCategoria() { return cmbFiltroCategoria; }
    public Button getBtnFiltrarProductos() { return btnFiltrarProductos; }
    public Button getBtnRecargarCatalogo() { return btnRecargarCatalogo; }
    @SuppressWarnings("rawtypes") public TableView getTblCatalogoProductos() { return tblCatalogoProductos; }
    public Button getBtnNuevoProducto() { return btnNuevoProducto; }
    public Button getBtnModificarProducto() { return btnModificarProducto; }
    public Button getBtnBajaLogicaProducto() { return btnBajaLogicaProducto; }
    public Button getBtnAdministrarCategorias() { return btnAdministrarCategorias; }
    public Button getBtnAdministrarImagenes() { return btnAdministrarImagenes; }
    public Label getLblTotalProductosRegistrados() { return lblTotalProductosRegistrados; }

    // Inventario
    public ComboBox<String> getCmbFiltroInventario() { return cmbFiltroInventario; }
    public CheckBox getChkSoloBajoStock() { return chkSoloBajoStock; }
    public Button getBtnRefrescarInventario() { return btnRefrescarInventario; }
    @SuppressWarnings("rawtypes") public TableView getTblInventario() { return tblInventario; }
    public Button getBtnEntradaStock() { return btnEntradaStock; }
    public Button getBtnSalidaStock() { return btnSalidaStock; }
    public Button getBtnAjusteStock() { return btnAjusteStock; }
    public Button getBtnGenerarReporteStock() { return btnGenerarReporteStock; }

    // Devoluciones
    public TextField getTxtFolioDevolucion() { return txtFolioDevolucion; }
    public Button getBtnBuscarTicketDevolucion() { return btnBuscarTicketDevolucion; }
    public Label getLblInfoTicketDevolucion() { return lblInfoTicketDevolucion; }
    @SuppressWarnings("rawtypes") public TableView getTblDevoluciones() { return tblDevoluciones; }
    public ComboBox<String> getCmbMotivoDevolucion() { return cmbMotivoDevolucion; }
    public CheckBox getChkReintegrarStock() { return chkReintegrarStock; }
    public Button getBtnProcesarDevolucion() { return btnProcesarDevolucion; }

    // Reportes
    public ComboBox<String> getCmbTipoReporte() { return cmbTipoReporte; }
    public DatePicker getDpFechaInicio() { return dpFechaInicio; }
    public DatePicker getDpFechaFin() { return dpFechaFin; }
    public ComboBox<String> getCmbCategoriaReporte() { return cmbCategoriaReporte; }
    public Button getBtnVistaPreviaReporte() { return btnVistaPreviaReporte; }
    public Button getBtnExportarPDF() { return btnExportarPDF; }
    public Label getLblEstadoReporte() { return lblEstadoReporte; }
    public TextArea getTxtDetallesReporte() { return txtDetallesReporte; }

    // IA
    public ComboBox<String> getCmbFuncionIA() { return cmbFuncionIA; }
    public Button getBtnEjecutarIA() { return btnEjecutarIA; }
    public TextArea getTxtResultadoIA() { return txtResultadoIA; }
    public Button getBtnAplicarRecomendacionIA() { return btnAplicarRecomendacionIA; }

    // Barra inferior
    public Label getLblAlertaStock() { return lblAlertaStock; }
    public Label getLblMensajeEstado() { return lblMensajeEstado; }
}
