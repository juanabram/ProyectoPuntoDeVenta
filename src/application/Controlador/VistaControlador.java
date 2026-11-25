package application.Controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

// Referencias FXML de los controles de la ventana principal
public class VistaControlador {

    // Menu superior
    @FXML private MenuItem menuNuevaVenta;
    @FXML private MenuItem menuCancelarVenta;
    @FXML private MenuItem menuSalir;
    @FXML private MenuItem menuAtajos;
    @FXML private MenuItem menuAcercaDe;

    // Encabezado
    @FXML private Label lblSesion;
    @FXML private Label lblFechaHora;

    // Pestanas
    @FXML private TabPane tabPanePrincipal;
    @FXML private Tab tabVentas;
    @FXML private Tab tabProductos;
    @FXML private Tab tabInventario;
    @FXML private Tab tabDevoluciones;
    @FXML private Tab tabReportes;

    // Ventas
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

    // Productos y categorias
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

    // Inventario
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

    // Devoluciones
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

    // Reportes
    @FXML private ComboBox<String> cmbTipoReporte;
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private ComboBox<String> cmbCategoriaReporte;
    @FXML private Button btnVistaPreviaReporte;
    @FXML private Button btnExportarPDF;
    @FXML private Label lblEstadoReporte;
    @FXML private TextArea txtDetallesReporte;

    // Barra inferior
    @FXML private Label lblAlertaStock;
    @FXML private Separator sepAlertaStock;
    @FXML private Label lblMensajeEstado;

    // Getters y setters

    public MenuItem getMenuNuevaVenta() { return menuNuevaVenta; }
    public MenuItem getMenuCancelarVenta() { return menuCancelarVenta; }
    public MenuItem getMenuSalir() { return menuSalir; }

    public MenuItem getMenuAtajos() { return menuAtajos; }
    public MenuItem getMenuAcercaDe() { return menuAcercaDe; }

    public Label getLblSesion() { return lblSesion; }
    public Label getLblFechaHora() { return lblFechaHora; }

    public TabPane getTabPanePrincipal() { return tabPanePrincipal; }
    public Tab getTabVentas() { return tabVentas; }
    public Tab getTabProductos() { return tabProductos; }
    public Tab getTabInventario() { return tabInventario; }
    public Tab getTabDevoluciones() { return tabDevoluciones; }
    public Tab getTabReportes() { return tabReportes; }

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
    @SuppressWarnings("rawtypes") public TableColumn getColCatCodigo() { return colCatCodigo; }
    @SuppressWarnings("rawtypes") public TableColumn getColCatNombre() { return colCatNombre; }
    @SuppressWarnings("rawtypes") public TableColumn getColCatCategoria() { return colCatCategoria; }
    @SuppressWarnings("rawtypes") public TableColumn getColCatSubcategoria() { return colCatSubcategoria; }
    @SuppressWarnings("rawtypes") public TableColumn getColCatPrecio() { return colCatPrecio; }
    @SuppressWarnings("rawtypes") public TableColumn getColCatStock() { return colCatStock; }
    @SuppressWarnings("rawtypes") public TableColumn getColCatStockMin() { return colCatStockMin; }
    @SuppressWarnings("rawtypes") public TableColumn getColCatEstado() { return colCatEstado; }
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
    @SuppressWarnings("rawtypes") public TableColumn getColInvCodigo() { return colInvCodigo; }
    @SuppressWarnings("rawtypes") public TableColumn getColInvProducto() { return colInvProducto; }
    @SuppressWarnings("rawtypes") public TableColumn getColInvCategoria() { return colInvCategoria; }
    @SuppressWarnings("rawtypes") public TableColumn getColInvExistencias() { return colInvExistencias; }
    @SuppressWarnings("rawtypes") public TableColumn getColInvStockMinimo() { return colInvStockMinimo; }
    @SuppressWarnings("rawtypes") public TableColumn getColInvEstado() { return colInvEstado; }
    @SuppressWarnings("rawtypes") public TableColumn getColInvUltimoMovimiento() { return colInvUltimoMovimiento; }
    public Button getBtnEntradaStock() { return btnEntradaStock; }
    public Button getBtnSalidaStock() { return btnSalidaStock; }
    public Button getBtnAjusteStock() { return btnAjusteStock; }
    public Button getBtnGenerarReporteStock() { return btnGenerarReporteStock; }

    // Devoluciones
    public TextField getTxtFolioDevolucion() { return txtFolioDevolucion; }
    public Button getBtnBuscarTicketDevolucion() { return btnBuscarTicketDevolucion; }
    public Label getLblInfoTicketDevolucion() { return lblInfoTicketDevolucion; }
    @SuppressWarnings("rawtypes") public TableView getTblDevoluciones() { return tblDevoluciones; }
    @SuppressWarnings("rawtypes") public TableColumn getColDevCodigo() { return colDevCodigo; }
    @SuppressWarnings("rawtypes") public TableColumn getColDevDescripcion() { return colDevDescripcion; }
    @SuppressWarnings("rawtypes") public TableColumn getColDevPrecio() { return colDevPrecio; }
    @SuppressWarnings("rawtypes") public TableColumn getColDevCantComprada() { return colDevCantComprada; }
    @SuppressWarnings("rawtypes") public TableColumn getColDevCantDevolver() { return colDevCantDevolver; }
    @SuppressWarnings("rawtypes") public TableColumn getColDevSubtotalReembolso() { return colDevSubtotalReembolso; }
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

    // Barra inferior
    public Label getLblAlertaStock() { return lblAlertaStock; }
    public Separator getSepAlertaStock() { return sepAlertaStock; }
    public Label getLblMensajeEstado() { return lblMensajeEstado; }
}
