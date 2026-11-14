package application.Controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class VistaControlador {

    // MENÚ PRINCIPAL
    @FXML private MenuItem menuNuevaVenta;
    @FXML private MenuItem menuPonerEnEspera;
    @FXML private MenuItem menuRecuperarVenta;
    @FXML private MenuItem menuCancelarVenta;
    @FXML private MenuItem menuSalir;
    
    @FXML private MenuItem menuEntradaEfectivo;
    @FXML private MenuItem menuSalidaEfectivo;
    @FXML private MenuItem menuCorteX;
    @FXML private MenuItem menuCorteZ;
    @FXML private MenuItem menuHistorialVentas;
    @FXML private MenuItem menuDevoluciones;
    
    @FXML private MenuItem menuBuscarProducto;
    @FXML private MenuItem menuListaPrecios;
    @FXML private MenuItem menuPromociones;
    
    @FXML private MenuItem menuRecargas;
    @FXML private MenuItem menuPagoServicios;
    @FXML private MenuItem menuPines;
    @FXML private MenuItem menuDepositos;
    
    @FXML private MenuItem menuAjustesCaja;
    @FXML private MenuItem menuImpresora;
    
    @FXML private MenuItem menuAtajos;
    @FXML private MenuItem menuAcercaDe;

    // DATOS DE SESIÓN / ENCABEZADO
    @FXML private Label lblCajero;
    @FXML private Label lblEstadoCaja;
    @FXML private Label lblFechaHora;

    // CAPTURA RÁPIDA DE ARTÍCULOS
    @FXML private TextField txtCodigoBarras;
    @FXML private TextField txtCantidad;
    @FXML private Button btnAgregar;
    @FXML private Button btnBuscar;

    // TABLA PRINCIPAL DE PRODUCTOS
    @SuppressWarnings("rawtypes")
    @FXML private TableView tblProductos;
    @SuppressWarnings("rawtypes")
    @FXML private TableColumn colNumero;
    @SuppressWarnings("rawtypes")
    @FXML private TableColumn colCodigo;
    @SuppressWarnings("rawtypes")
    @FXML private TableColumn colDescripcion;
    @SuppressWarnings("rawtypes")
    @FXML private TableColumn colPrecio;
    @SuppressWarnings("rawtypes")
    @FXML private TableColumn colCantidad;
    @SuppressWarnings("rawtypes")
    @FXML private TableColumn colUnidad;
    @SuppressWarnings("rawtypes")
    @FXML private TableColumn colDescuento;
    @SuppressWarnings("rawtypes")
    @FXML private TableColumn colImporte;

    // BOTONES DE ACCIÓN DEL CARRITO
    @FXML private Button btnEliminarArticulo;
    @FXML private Button btnModificarCantidad;
    @FXML private Button btnAplicarDescuento;
    @FXML private Button btnPonerEnEspera;
    @FXML private Button btnRecuperarVenta;
    @FXML private Button btnCancelarVenta;

    // SERVICIOS RÁPIDOS (TIENDA DE CONVENIENCIA)
    @FXML private Button btnRecargas;
    @FXML private Button btnPagoServicios;
    @FXML private Button btnPines;
    @FXML private Button btnDepositos;

    // CLIENTE / PUNTOS
    @FXML private TextField txtCliente;
    @FXML private Label lblPuntos;

    // RESUMEN DE TOTALES
    @FXML private Label lblNumArticulos;
    @FXML private Label lblSubtotal;
    @FXML private Label lblDescuento;
    @FXML private Label lblIva;
    @FXML private Label lblTotal;

    // LIQUIDACIÓN / COBRO
    @FXML private ComboBox<String> cmbMetodoPago;
    @FXML private TextField txtPagaCon;
    @FXML private Label lblCambio;
    @FXML private CheckBox chkTicket;
    @FXML private Button btnCobrar;

    // BARRA DE ESTADO INFERIOR
    @FXML private Label lblMensajeEstado;

    
    // MÉTODOS DE ACCESO (GETTERS Y SETTERS)

    public MenuItem getMenuNuevaVenta() { return menuNuevaVenta; }
    public void setMenuNuevaVenta(MenuItem menuNuevaVenta) { this.menuNuevaVenta = menuNuevaVenta; }

    public MenuItem getMenuPonerEnEspera() { return menuPonerEnEspera; }
    public void setMenuPonerEnEspera(MenuItem menuPonerEnEspera) { this.menuPonerEnEspera = menuPonerEnEspera; }

    public MenuItem getMenuRecuperarVenta() { return menuRecuperarVenta; }
    public void setMenuRecuperarVenta(MenuItem menuRecuperarVenta) { this.menuRecuperarVenta = menuRecuperarVenta; }

    public MenuItem getMenuCancelarVenta() { return menuCancelarVenta; }
    public void setMenuCancelarVenta(MenuItem menuCancelarVenta) { this.menuCancelarVenta = menuCancelarVenta; }

    public MenuItem getMenuSalir() { return menuSalir; }
    public void setMenuSalir(MenuItem menuSalir) { this.menuSalir = menuSalir; }

    public MenuItem getMenuEntradaEfectivo() { return menuEntradaEfectivo; }
    public void setMenuEntradaEfectivo(MenuItem menuEntradaEfectivo) { this.menuEntradaEfectivo = menuEntradaEfectivo; }

    public MenuItem getMenuSalidaEfectivo() { return menuSalidaEfectivo; }
    public void setMenuSalidaEfectivo(MenuItem menuSalidaEfectivo) { this.menuSalidaEfectivo = menuSalidaEfectivo; }

    public MenuItem getMenuCorteX() { return menuCorteX; }
    public void setMenuCorteX(MenuItem menuCorteX) { this.menuCorteX = menuCorteX; }

    public MenuItem getMenuCorteZ() { return menuCorteZ; }
    public void setMenuCorteZ(MenuItem menuCorteZ) { this.menuCorteZ = menuCorteZ; }

    public MenuItem getMenuHistorialVentas() { return menuHistorialVentas; }
    public void setMenuHistorialVentas(MenuItem menuHistorialVentas) { this.menuHistorialVentas = menuHistorialVentas; }

    public MenuItem getMenuDevoluciones() { return menuDevoluciones; }
    public void setMenuDevoluciones(MenuItem menuDevoluciones) { this.menuDevoluciones = menuDevoluciones; }

    public MenuItem getMenuBuscarProducto() { return menuBuscarProducto; }
    public void setMenuBuscarProducto(MenuItem menuBuscarProducto) { this.menuBuscarProducto = menuBuscarProducto; }

    public MenuItem getMenuListaPrecios() { return menuListaPrecios; }
    public void setMenuListaPrecios(MenuItem menuListaPrecios) { this.menuListaPrecios = menuListaPrecios; }

    public MenuItem getMenuPromociones() { return menuPromociones; }
    public void setMenuPromociones(MenuItem menuPromociones) { this.menuPromociones = menuPromociones; }

    public MenuItem getMenuRecargas() { return menuRecargas; }
    public void setMenuRecargas(MenuItem menuRecargas) { this.menuRecargas = menuRecargas; }

    public MenuItem getMenuPagoServicios() { return menuPagoServicios; }
    public void setMenuPagoServicios(MenuItem menuPagoServicios) { this.menuPagoServicios = menuPagoServicios; }

    public MenuItem getMenuPines() { return menuPines; }
    public void setMenuPines(MenuItem menuPines) { this.menuPines = menuPines; }

    public MenuItem getMenuDepositos() { return menuDepositos; }
    public void setMenuDepositos(MenuItem menuDepositos) { this.menuDepositos = menuDepositos; }

    public MenuItem getMenuAjustesCaja() { return menuAjustesCaja; }
    public void setMenuAjustesCaja(MenuItem menuAjustesCaja) { this.menuAjustesCaja = menuAjustesCaja; }

    public MenuItem getMenuImpresora() { return menuImpresora; }
    public void setMenuImpresora(MenuItem menuImpresora) { this.menuImpresora = menuImpresora; }

    public MenuItem getMenuAtajos() { return menuAtajos; }
    public void setMenuAtajos(MenuItem menuAtajos) { this.menuAtajos = menuAtajos; }

    public MenuItem getMenuAcercaDe() { return menuAcercaDe; }
    public void setMenuAcercaDe(MenuItem menuAcercaDe) { this.menuAcercaDe = menuAcercaDe; }

    public Label getLblCajero() { return lblCajero; }
    public void setLblCajero(Label lblCajero) { this.lblCajero = lblCajero; }

    public Label getLblEstadoCaja() { return lblEstadoCaja; }
    public void setLblEstadoCaja(Label lblEstadoCaja) { this.lblEstadoCaja = lblEstadoCaja; }

    public Label getLblFechaHora() { return lblFechaHora; }
    public void setLblFechaHora(Label lblFechaHora) { this.lblFechaHora = lblFechaHora; }

    public TextField getTxtCodigoBarras() { return txtCodigoBarras; }
    public void setTxtCodigoBarras(TextField txtCodigoBarras) { this.txtCodigoBarras = txtCodigoBarras; }

    public TextField getTxtCantidad() { return txtCantidad; }
    public void setTxtCantidad(TextField txtCantidad) { this.txtCantidad = txtCantidad; }

    public Button getBtnAgregar() { return btnAgregar; }
    public void setBtnAgregar(Button btnAgregar) { this.btnAgregar = btnAgregar; }

    public Button getBtnBuscar() { return btnBuscar; }
    public void setBtnBuscar(Button btnBuscar) { this.btnBuscar = btnBuscar; }

    @SuppressWarnings("rawtypes")
    public TableView getTblProductos() { return tblProductos; }
    @SuppressWarnings("rawtypes")
    public void setTblProductos(TableView tblProductos) { this.tblProductos = tblProductos; }

    @SuppressWarnings("rawtypes")
    public TableColumn getColNumero() { return colNumero; }
    @SuppressWarnings("rawtypes")
    public void setColNumero(TableColumn colNumero) { this.colNumero = colNumero; }

    @SuppressWarnings("rawtypes")
    public TableColumn getColCodigo() { return colCodigo; }
    @SuppressWarnings("rawtypes")
    public void setColCodigo(TableColumn colCodigo) { this.colCodigo = colCodigo; }

    @SuppressWarnings("rawtypes")
    public TableColumn getColDescripcion() { return colDescripcion; }
    @SuppressWarnings("rawtypes")
    public void setColDescripcion(TableColumn colDescripcion) { this.colDescripcion = colDescripcion; }

    @SuppressWarnings("rawtypes")
    public TableColumn getColPrecio() { return colPrecio; }
    @SuppressWarnings("rawtypes")
    public void setColPrecio(TableColumn colPrecio) { this.colPrecio = colPrecio; }

    @SuppressWarnings("rawtypes")
    public TableColumn getColCantidad() { return colCantidad; }
    @SuppressWarnings("rawtypes")
    public void setColCantidad(TableColumn colCantidad) { this.colCantidad = colCantidad; }

    @SuppressWarnings("rawtypes")
    public TableColumn getColUnidad() { return colUnidad; }
    @SuppressWarnings("rawtypes")
    public void setColUnidad(TableColumn colUnidad) { this.colUnidad = colUnidad; }

    @SuppressWarnings("rawtypes")
    public TableColumn getColDescuento() { return colDescuento; }
    @SuppressWarnings("rawtypes")
    public void setColDescuento(TableColumn colDescuento) { this.colDescuento = colDescuento; }

    @SuppressWarnings("rawtypes")
    public TableColumn getColImporte() { return colImporte; }
    @SuppressWarnings("rawtypes")
    public void setColImporte(TableColumn colImporte) { this.colImporte = colImporte; }

    public Button getBtnEliminarArticulo() { return btnEliminarArticulo; }
    public void setBtnEliminarArticulo(Button btnEliminarArticulo) { this.btnEliminarArticulo = btnEliminarArticulo; }

    public Button getBtnModificarCantidad() { return btnModificarCantidad; }
    public void setBtnModificarCantidad(Button btnModificarCantidad) { this.btnModificarCantidad = btnModificarCantidad; }

    public Button getBtnAplicarDescuento() { return btnAplicarDescuento; }
    public void setBtnAplicarDescuento(Button btnAplicarDescuento) { this.btnAplicarDescuento = btnAplicarDescuento; }

    public Button getBtnPonerEnEspera() { return btnPonerEnEspera; }
    public void setBtnPonerEnEspera(Button btnPonerEnEspera) { this.btnPonerEnEspera = btnPonerEnEspera; }

    public Button getBtnRecuperarVenta() { return btnRecuperarVenta; }
    public void setBtnRecuperarVenta(Button btnRecuperarVenta) { this.btnRecuperarVenta = btnRecuperarVenta; }

    public Button getBtnCancelarVenta() { return btnCancelarVenta; }
    public void setBtnCancelarVenta(Button btnCancelarVenta) { this.btnCancelarVenta = btnCancelarVenta; }

    public Button getBtnRecargas() { return btnRecargas; }
    public void setBtnRecargas(Button btnRecargas) { this.btnRecargas = btnRecargas; }

    public Button getBtnPagoServicios() { return btnPagoServicios; }
    public void setBtnPagoServicios(Button btnPagoServicios) { this.btnPagoServicios = btnPagoServicios; }

    public Button getBtnPines() { return btnPines; }
    public void setBtnPines(Button btnPines) { this.btnPines = btnPines; }

    public Button getBtnDepositos() { return btnDepositos; }
    public void setBtnDepositos(Button btnDepositos) { this.btnDepositos = btnDepositos; }

    public TextField getTxtCliente() { return txtCliente; }
    public void setTxtCliente(TextField txtCliente) { this.txtCliente = txtCliente; }

    public Label getLblPuntos() { return lblPuntos; }
    public void setLblPuntos(Label lblPuntos) { this.lblPuntos = lblPuntos; }

    public Label getLblNumArticulos() { return lblNumArticulos; }
    public void setLblNumArticulos(Label lblNumArticulos) { this.lblNumArticulos = lblNumArticulos; }

    public Label getLblSubtotal() { return lblSubtotal; }
    public void setLblSubtotal(Label lblSubtotal) { this.lblSubtotal = lblSubtotal; }

    public Label getLblDescuento() { return lblDescuento; }
    public void setLblDescuento(Label lblDescuento) { this.lblDescuento = lblDescuento; }

    public Label getLblIva() { return lblIva; }
    public void setLblIva(Label lblIva) { this.lblIva = lblIva; }

    public Label getLblTotal() { return lblTotal; }
    public void setLblTotal(Label lblTotal) { this.lblTotal = lblTotal; }

    public ComboBox<String> getCmbMetodoPago() { return cmbMetodoPago; }
    public void setCmbMetodoPago(ComboBox<String> cmbMetodoPago) { this.cmbMetodoPago = cmbMetodoPago; }

    public TextField getTxtPagaCon() { return txtPagaCon; }
    public void setTxtPagaCon(TextField txtPagaCon) { this.txtPagaCon = txtPagaCon; }

    public Label getLblCambio() { return lblCambio; }
    public void setLblCambio(Label lblCambio) { this.lblCambio = lblCambio; }

    public CheckBox getChkTicket() { return chkTicket; }
    public void setChkTicket(CheckBox chkTicket) { this.chkTicket = chkTicket; }

    public Button getBtnCobrar() { return btnCobrar; }
    public void setBtnCobrar(Button btnCobrar) { this.btnCobrar = btnCobrar; }

    public Label getLblMensajeEstado() { return lblMensajeEstado; }
    public void setLblMensajeEstado(Label lblMensajeEstado) { this.lblMensajeEstado = lblMensajeEstado; }
}
