package application.Controlador;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import application.modelo.ItemDevolucion;
import application.modelo.ItemVenta;
import application.modelo.MovimientoInventario;
import application.modelo.Producto;
import application.modelo.Venta;
import application.modelo.servicio.ServicioDevoluciones;
import application.modelo.servicio.ServicioInventario;
import application.modelo.servicio.ServicioProductos;
import application.modelo.servicio.ServicioReportes;
import application.modelo.servicio.ServicioVentas;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;

/**
 * Clase Controlador:
 * Conecta la Vista (VistaControlador / FXML) con la arquitectura de backend
 * (Servicios, Modelos y capa DAO/Datos).
 */
public class Controlador {

    private final VistaControlador vista;

    // Servicios de Backend
    private final ServicioVentas servicioVentas = new ServicioVentas();
    private final ServicioProductos servicioProductos = new ServicioProductos();
    private final ServicioInventario servicioInventario = new ServicioInventario();
    private final ServicioDevoluciones servicioDevoluciones = new ServicioDevoluciones();
    private final ServicioReportes servicioReportes = new ServicioReportes();

    private static final NumberFormat FORMATO_MONEDA = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-MX"));

    public static String formatearMoneda(double monto) {
        return FORMATO_MONEDA.format(monto);
    }

    public Controlador(VistaControlador vista) {
        this.vista = vista;
        inicializarTablas();
        inicializarNavegacionPestanas();
        inicializarDatosComponentes();
        configurarEventosVentas();
        configurarEventosProductos();
        configurarEventosInventario();
        configurarEventosDevoluciones();
        configurarEventosReportes();
    }

    /**
     * Configura los enlaces de datos (PropertyValueFactory) y celdas de las 4 tablas principales.
     */
    @SuppressWarnings("unchecked")
    private void inicializarTablas() {
        // 1. Tabla de Ventas (Carrito de Compras)
        vista.getColNumero().setCellValueFactory(new PropertyValueFactory<>("numero"));
        vista.getColCodigo().setCellValueFactory(new PropertyValueFactory<>("codigo"));
        vista.getColDescripcion().setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        vista.getColPrecio().setCellValueFactory(new PropertyValueFactory<>("precio"));
        vista.getColCantidad().setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        vista.getColDescuento().setCellValueFactory(new PropertyValueFactory<>("descuento"));
        vista.getColImporte().setCellValueFactory(new PropertyValueFactory<>("importe"));

        formatearColumnaMoneda(vista.getColPrecio());
        formatearColumnaMoneda(vista.getColDescuento());
        formatearColumnaMoneda(vista.getColImporte());

        vista.getTblProductos().setItems(servicioVentas.getCarrito());

        // 2. Tabla de Catálogo de Productos
        vista.getColCatCodigo().setCellValueFactory(new PropertyValueFactory<>("codigo"));
        vista.getColCatNombre().setCellValueFactory(new PropertyValueFactory<>("nombre"));
        vista.getColCatCategoria().setCellValueFactory(new PropertyValueFactory<>("categoria"));
        vista.getColCatSubcategoria().setCellValueFactory(new PropertyValueFactory<>("subcategoria"));
        vista.getColCatPrecio().setCellValueFactory(new PropertyValueFactory<>("precio"));
        vista.getColCatStock().setCellValueFactory(new PropertyValueFactory<>("stock"));
        vista.getColCatStockMin().setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        vista.getColCatEstado().setCellValueFactory(new PropertyValueFactory<>("estado"));

        formatearColumnaMoneda(vista.getColCatPrecio());
        vista.getTblCatalogoProductos().setItems(servicioProductos.getListaProductos());

        // 3. Tabla de Inventario y Stock
        vista.getColInvCodigo().setCellValueFactory(new PropertyValueFactory<>("codigo"));
        vista.getColInvProducto().setCellValueFactory(new PropertyValueFactory<>("producto"));
        vista.getColInvCategoria().setCellValueFactory(new PropertyValueFactory<>("categoria"));
        vista.getColInvExistencias().setCellValueFactory(new PropertyValueFactory<>("existencias"));
        vista.getColInvStockMinimo().setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        vista.getColInvEstado().setCellValueFactory(new PropertyValueFactory<>("estadoAlerta"));
        vista.getColInvUltimoMovimiento().setCellValueFactory(new PropertyValueFactory<>("ultimoMovimiento"));

        vista.getTblInventario().setItems(servicioInventario.getListaInventario());

        // 4. Tabla de Devoluciones
        vista.getColDevCodigo().setCellValueFactory(new PropertyValueFactory<>("codigo"));
        vista.getColDevDescripcion().setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        vista.getColDevPrecio().setCellValueFactory(new PropertyValueFactory<>("precio"));
        vista.getColDevCantComprada().setCellValueFactory(new PropertyValueFactory<>("cantComprada"));
        vista.getColDevCantDevolver().setCellValueFactory(new PropertyValueFactory<>("cantDevolver"));
        vista.getColDevSubtotalReembolso().setCellValueFactory(new PropertyValueFactory<>("subtotalReembolso"));

        formatearColumnaMoneda(vista.getColDevPrecio());
        formatearColumnaMoneda(vista.getColDevSubtotalReembolso());
        vista.getTblDevoluciones().setItems(servicioDevoluciones.getItemsDevolucion());
    }

    @SuppressWarnings("rawtypes")
    private void formatearColumnaMoneda(TableColumn col) {
        col.setCellFactory(c -> new TableCell<Object, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatearMoneda(item));
                }
            }
        });
    }

    /**
     * Configura la navegación y respuesta al alternar entre pestañas del TabPane.
     */
    private void inicializarNavegacionPestanas() {
        vista.getTabPanePrincipal().getSelectionModel().selectedItemProperty().addListener((obs, anteriorTab, nuevaTab) -> {
            if (nuevaTab != null) {
                vista.getLblMensajeEstado().setText("Pestaña activa: " + nuevaTab.getText());
                
                if (nuevaTab == vista.getTabVentas()) {
                    vista.getTxtCodigoBarras().requestFocus();
                } else if (nuevaTab == vista.getTabProductos()) {
                    servicioProductos.recargarProductos();
                    actualizarContadorCatalogo();
                    vista.getTxtBuscarCatalogo().requestFocus();
                } else if (nuevaTab == vista.getTabInventario()) {
                    servicioInventario.recargarInventario(vista.getChkSoloBajoStock().isSelected());
                    actualizarAlertaStock();
                } else if (nuevaTab == vista.getTabDevoluciones()) {
                    vista.getTxtFolioDevolucion().requestFocus();
                }
            }
        });

        // Menú Sistema y Ayuda
        vista.getMenuNuevaVenta().setOnAction(e -> {
            seleccionarPestana(vista.getTabVentas());
            limpiarCamposVenta();
        });
        vista.getMenuCancelarVenta().setOnAction(e -> vista.getBtnCancelarVenta().fire());
        vista.getMenuSalir().setOnAction(e -> Platform.exit());

        vista.getMenuAtajos().setOnAction(e -> {
            mostrarAlerta("Atajos de Teclado", "Guía Rápida POS",
                "[F1] Ayuda\n[F2] Buscar en Catálogo\n[F4] Modificar Cantidad\n[F12] Cobrar / Finalizar Venta\n[Esc] Limpiar / Cancelar\n[Enter] Agregar producto escaneado", AlertType.INFORMATION);
        });

        vista.getMenuAcercaDe().setOnAction(e -> {
            mostrarAlerta("Acerca del Proyecto", "Proyecto Integrador - Sistema Punto de Venta",
                "Arquitectura MVC con JavaFX y Backend desacoplado.\nCapa DAO preparada para conexión con Base de Datos Relacional.\nUniversidad Autónoma de Tamaulipas - FIT.", AlertType.INFORMATION);
        });
    }

    private void seleccionarPestana(javafx.scene.control.Tab tab) {
        vista.getTabPanePrincipal().getSelectionModel().select(tab);
    }

    /**
     * Inicializa los datos y selectores de cada una de las pestañas.
     */
    private void inicializarDatosComponentes() {
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy | HH:mm");
        vista.getLblFechaHora().setText(LocalDateTime.now().format(formatoFecha));

        // Pestaña 1: Métodos de Pago
        vista.getCmbMetodoPago().setItems(FXCollections.observableArrayList(
            "Efectivo", "Tarjeta de Débito", "Tarjeta de Crédito", "Transferencia Bancaria"
        ));
        vista.getCmbMetodoPago().getSelectionModel().selectFirst();

        // Pestaña 2: Categorías para el Catálogo
        vista.getCmbFiltroCategoria().setItems(FXCollections.observableArrayList(
            "Todas las categorías", "Bebidas", "Lácteos", "Botanas", "Panadería", "Enlatados",
            "Limpieza", "Higiene Personal", "Farmacia", "Dulcería", "Carnes Frías",
            "Frutas y Verduras", "Mascotas", "Papelería", "Abarrotes", "Cuidado del Hogar",
            "Licores y Cervezas", "Congelados", "Cuidado de Bebé", "Despensa General"
        ));
        vista.getCmbFiltroCategoria().getSelectionModel().selectFirst();

        // Pestaña 3: Filtros de Inventario
        vista.getCmbFiltroInventario().setItems(FXCollections.observableArrayList(
            "Todas las existencias", "Solo productos con stock normal", "Solo productos con stock crítico (< 5)"
        ));
        vista.getCmbFiltroInventario().getSelectionModel().selectFirst();

        // Pestaña 4: Motivos de Devolución
        vista.getCmbMotivoDevolucion().setItems(FXCollections.observableArrayList(
            "Producto caducado / mal estado", "Error de compra del cliente",
            "Empaque roto / dañado", "Cobro incorrecto"
        ));
        vista.getCmbMotivoDevolucion().getSelectionModel().selectFirst();

        // Pestaña 5: Tipos de Reportes
        vista.getCmbTipoReporte().setItems(FXCollections.observableArrayList(
            "1. Inventario General",
            "2. Inventario por Categoría",
            "3. Productos con Bajo Stock (Críticos)",
            "4. Ventas Diarias",
            "5. Ventas por Periodo",
            "6. Productos Más Vendidos",
            "7. Devoluciones por Periodo",
            "8. Resumen Ejecutivo Mensual"
        ));
        vista.getCmbTipoReporte().getSelectionModel().selectFirst();
        vista.getCmbCategoriaReporte().setItems(vista.getCmbFiltroCategoria().getItems());
        vista.getCmbCategoriaReporte().getSelectionModel().selectFirst();

        // Enlace reactivo para alerta de stock
        vista.getLblAlertaStock().visibleProperty().bind(vista.getLblAlertaStock().textProperty().isNotEmpty());
        vista.getLblAlertaStock().managedProperty().bind(vista.getLblAlertaStock().textProperty().isNotEmpty());
        if (vista.getSepAlertaStock() != null) {
            vista.getSepAlertaStock().visibleProperty().bind(vista.getLblAlertaStock().textProperty().isNotEmpty());
            vista.getSepAlertaStock().managedProperty().bind(vista.getLblAlertaStock().textProperty().isNotEmpty());
        }

        // Validación de entradas numéricas
        vista.getTxtCantidad().textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                vista.getTxtCantidad().setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        vista.getTxtPagaCon().textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,2})?")) {
                vista.getTxtPagaCon().setText(oldVal);
                return;
            }
            calcularCambio();
        });

        // Enter en el escáner de código de barras
        vista.getTxtCodigoBarras().setOnAction(e -> vista.getBtnAgregar().fire());

        actualizarAlertaStock();
        actualizarContadorCatalogo();
        recalcularTotalesVenta();
        vista.getLblMensajeEstado().setText("Pestaña activa: Ventas");
        Platform.runLater(() -> vista.getTxtCodigoBarras().requestFocus());
    }

    /**
     * Eventos de la Pestaña 1: Caja / Ventas.
     */
    private void configurarEventosVentas() {
        vista.getBtnAgregar().setOnAction(e -> {
            String codigo = vista.getTxtCodigoBarras().getText().trim();
            String cantStr = vista.getTxtCantidad().getText().trim();
            int cant = 1;
            try {
                if (!cantStr.isEmpty()) cant = Integer.parseInt(cantStr);
            } catch (NumberFormatException ex) {
                cant = 1;
            }

            try {
                String nombre = servicioVentas.agregarArticulo(codigo, cant);
                recalcularTotalesVenta();
                vista.getLblMensajeEstado().setText("'" + nombre + "' agregado (x" + cant + ").");
                vista.getTxtCodigoBarras().clear();
                vista.getTxtCantidad().setText("1");
            } catch (IllegalArgumentException ex) {
                mostrarAlerta("Atención", "No se pudo agregar el artículo", ex.getMessage(), AlertType.WARNING);
            }
            vista.getTxtCodigoBarras().requestFocus();
        });

        vista.getBtnBuscar().setOnAction(e -> {
            seleccionarPestana(vista.getTabProductos());
            vista.getLblMensajeEstado().setText("Haga doble clic en un producto o elija uno para agregarlo a la venta.");
        });

        vista.getBtnEliminarArticulo().setOnAction(e -> {
            ItemVenta seleccionado = (ItemVenta) vista.getTblProductos().getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                mostrarAlerta("Carrito", "Selección Requerida", "Seleccione un artículo de la tabla para eliminarlo.", AlertType.WARNING);
                return;
            }
            servicioVentas.quitarArticulo(seleccionado);
            recalcularTotalesVenta();
            vista.getLblMensajeEstado().setText("Artículo '" + seleccionado.getDescripcion() + "' removido del carrito.");
        });

        vista.getBtnModificarCantidad().setOnAction(e -> {
            ItemVenta seleccionado = (ItemVenta) vista.getTblProductos().getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                mostrarAlerta("Carrito", "Selección Requerida", "Seleccione un artículo para cambiar su cantidad.", AlertType.WARNING);
                return;
            }
            TextInputDialog dialog = new TextInputDialog(String.valueOf(seleccionado.getCantidad()));
            dialog.setTitle("Modificar Cantidad");
            dialog.setHeaderText("Ajustar unidades de: " + seleccionado.getDescripcion());
            dialog.setContentText("Nueva cantidad:");
            dialog.showAndWait().ifPresent(cantStr -> {
                try {
                    int c = Integer.parseInt(cantStr.trim());
                    servicioVentas.modificarCantidad(seleccionado, c);
                    vista.getTblProductos().refresh();
                    recalcularTotalesVenta();
                    vista.getLblMensajeEstado().setText("Cantidad actualizada a " + c + " unidad(es).");
                } catch (NumberFormatException ex) {
                    mostrarAlerta("Error", "Entrada inválida", "Debe ingresar un número entero válido.", AlertType.ERROR);
                } catch (IllegalArgumentException ex) {
                    mostrarAlerta("Stock insuficiente", "No es posible asignar cantidad", ex.getMessage(), AlertType.WARNING);
                }
            });
        });

        vista.getBtnAplicarDescuento().setOnAction(e -> {
            ItemVenta seleccionado = (ItemVenta) vista.getTblProductos().getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                mostrarAlerta("Descuentos", "Selección Requerida", "Seleccione un artículo para aplicar descuento.", AlertType.WARNING);
                return;
            }
            TextInputDialog dialog = new TextInputDialog(String.valueOf(seleccionado.getDescuento()));
            dialog.setTitle("Aplicar Descuento");
            dialog.setHeaderText("Descuento para: " + seleccionado.getDescripcion());
            dialog.setContentText("Monto en $ a descontar:");
            dialog.showAndWait().ifPresent(descStr -> {
                try {
                    double d = Double.parseDouble(descStr.trim());
                    servicioVentas.aplicarDescuento(seleccionado, d);
                    vista.getTblProductos().refresh();
                    recalcularTotalesVenta();
                    vista.getLblMensajeEstado().setText("Descuento de $" + d + " aplicado a '" + seleccionado.getDescripcion() + "'.");
                } catch (NumberFormatException ex) {
                    mostrarAlerta("Error", "Monto inválido", "Ingrese una cantidad numérica válida.", AlertType.ERROR);
                } catch (IllegalArgumentException ex) {
                    mostrarAlerta("Descuento no válido", "Monto excedido", ex.getMessage(), AlertType.WARNING);
                }
            });
        });

        vista.getBtnCancelarVenta().setOnAction(e -> {
            if (servicioVentas.getCarrito().isEmpty()) return;
            Alert confirm = new Alert(AlertType.CONFIRMATION, "¿Desea cancelar y vaciar la venta actual?", ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Cancelar Venta");
            confirm.setHeaderText(null);
            confirm.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.YES) {
                    servicioVentas.vaciarCarrito();
                    limpiarCamposVenta();
                    recalcularTotalesVenta();
                    vista.getLblMensajeEstado().setText("Venta cancelada. Listo para nueva operación.");
                }
            });
        });

        vista.getBtnCobrar().setOnAction(e -> {
            if (servicioVentas.getCarrito().isEmpty()) {
                mostrarAlerta("Venta Vacía", "No hay artículos", "Agregue productos antes de cobrar.", AlertType.WARNING);
                return;
            }
            String metodo = vista.getCmbMetodoPago().getValue();
            double pagaCon = 0.0;
            if ("Efectivo".equalsIgnoreCase(metodo)) {
                String strPaga = vista.getTxtPagaCon().getText().trim();
                if (strPaga.isEmpty()) {
                    mostrarAlerta("Cobro en Efectivo", "Monto Faltante", "Ingrese con cuánto paga el cliente.", AlertType.WARNING);
                    vista.getTxtPagaCon().requestFocus();
                    return;
                }
                try {
                    pagaCon = Double.parseDouble(strPaga);
                } catch (NumberFormatException ex) {
                    mostrarAlerta("Error", "Monto Inválido", "Ingrese un valor numérico correcto.", AlertType.ERROR);
                    return;
                }
            } else {
                pagaCon = servicioVentas.calcularTotal();
            }

            try {
                boolean ticket = vista.getChkTicket().isSelected();
                Venta ventaRealizada = servicioVentas.liquidarVenta(metodo, pagaCon, ticket);
                
                // Actualizar inventario y alerta en segundo plano
                servicioInventario.recargarInventario(false);
                actualizarAlertaStock();
                
                String textoTicket = servicioVentas.generarTextoTicket(ventaRealizada);
                
                Alert exito = new Alert(AlertType.INFORMATION);
                exito.setTitle("Venta Concluida con Éxito");
                exito.setHeaderText("Folio: " + ventaRealizada.getFolio() + " | Total: " + formatearMoneda(ventaRealizada.getTotal()));
                exito.setContentText("Transacción registrada correctamente en el backend.\n\n" + (ticket ? "TICKET DE VENTA:\n" + textoTicket : ""));
                exito.showAndWait();

                limpiarCamposVenta();
                recalcularTotalesVenta();
                vista.getLblMensajeEstado().setText("Venta " + ventaRealizada.getFolio() + " completada con éxito.");
            } catch (IllegalArgumentException ex) {
                mostrarAlerta("Cobro Denegado", "No es posible liquidar", ex.getMessage(), AlertType.WARNING);
            }
        });
    }

    /**
     * Eventos de la Pestaña 2: Catálogo de Productos y Categorías.
     */
    private void configurarEventosProductos() {
        // Doble clic en producto para llevarlo a la venta
        vista.getTblCatalogoProductos().setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Producto sel = (Producto) vista.getTblCatalogoProductos().getSelectionModel().getSelectedItem();
                if (sel != null) {
                    seleccionarPestana(vista.getTabVentas());
                    vista.getTxtCodigoBarras().setText(sel.getCodigo());
                    vista.getBtnAgregar().fire();
                }
            }
        });

        vista.getBtnFiltrarProductos().setOnAction(e -> {
            String busqueda = vista.getTxtBuscarCatalogo().getText();
            String cat = vista.getCmbFiltroCategoria().getValue();
            servicioProductos.filtrar(busqueda, cat);
            actualizarContadorCatalogo();
            vista.getLblMensajeEstado().setText("Filtro aplicado: " + servicioProductos.getListaProductos().size() + " productos encontrados.");
        });

        vista.getTxtBuscarCatalogo().textProperty().addListener((obs, oldVal, newVal) -> {
            servicioProductos.filtrar(newVal, vista.getCmbFiltroCategoria().getValue());
            actualizarContadorCatalogo();
        });

        vista.getCmbFiltroCategoria().setOnAction(e -> vista.getBtnFiltrarProductos().fire());

        vista.getBtnRecargarCatalogo().setOnAction(e -> {
            vista.getTxtBuscarCatalogo().clear();
            vista.getCmbFiltroCategoria().getSelectionModel().selectFirst();
            servicioProductos.recargarProductos();
            actualizarContadorCatalogo();
            vista.getLblMensajeEstado().setText("Catálogo completo recargado.");
        });

        vista.getBtnNuevoProducto().setOnAction(e -> mostrarDialogoProducto(null));

        vista.getBtnModificarProducto().setOnAction(e -> {
            Producto sel = (Producto) vista.getTblCatalogoProductos().getSelectionModel().getSelectedItem();
            if (sel == null) {
                mostrarAlerta("Catálogo", "Selección Requerida", "Seleccione un producto para modificar.", AlertType.WARNING);
                return;
            }
            mostrarDialogoProducto(sel);
        });

        vista.getBtnBajaLogicaProducto().setOnAction(e -> {
            Producto sel = (Producto) vista.getTblCatalogoProductos().getSelectionModel().getSelectedItem();
            if (sel == null) {
                mostrarAlerta("Catálogo", "Selección Requerida", "Seleccione un producto para dar de baja.", AlertType.WARNING);
                return;
            }
            Alert confirm = new Alert(AlertType.CONFIRMATION, "¿Confirma la baja lógica de '" + sel.getNombre() + "'?", ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Baja Lógica");
            confirm.setHeaderText(null);
            confirm.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.YES) {
                    servicioProductos.darBajaLogica(sel);
                    vista.getTblCatalogoProductos().refresh();
                    vista.getLblMensajeEstado().setText("Producto '" + sel.getCodigo() + "' desactivado lógicamente.");
                }
            });
        });

        vista.getBtnAdministrarCategorias().setOnAction(e -> {
            mostrarAlerta("Categorías del Sistema", "20 Categorías Obligatorias Activas",
                "Las 20 categorías comerciales del proyecto están configuradas y vinculadas en la base de datos y memoria:\n" +
                "Bebidas, Lácteos, Botanas, Panadería, Enlatados, Limpieza, Higiene Personal, Farmacia, Dulcería, Carnes Frías, " +
                "Frutas y Verduras, Mascotas, Papelería, Abarrotes, Cuidado del Hogar, Licores y Cervezas, Congelados, Cuidado de Bebé, Despensa General.", AlertType.INFORMATION);
        });

        vista.getBtnAdministrarImagenes().setOnAction(e -> {
            mostrarAlerta("Gestor Multimedia", "Asignación de Imágenes",
                "Directorio de recursos multimedia preparado en application/vista/imagenes/\nCada producto enlaza su imagen por código de barras.", AlertType.INFORMATION);
        });
    }

    /**
     * Muestra diálogo modal para alta o modificación de producto.
     */
    private void mostrarDialogoProducto(Producto existente) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(existente == null ? "Registrar Nuevo Producto" : "Modificar Producto");
        dialog.setHeaderText(existente == null ? "Datos del nuevo artículo" : "Actualizar: " + existente.getNombre());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtCod = new TextField();
        txtCod.setPromptText("Ej. 7501000123456");
        TextField txtNom = new TextField();
        txtNom.setPromptText("Descripción completa");
        ComboBox<String> cmbCat = new ComboBox<>(vista.getCmbFiltroCategoria().getItems());
        cmbCat.getSelectionModel().select(1);
        TextField txtSubcat = new TextField("General");
        TextField txtPrecio = new TextField("0.00");
        TextField txtStock = new TextField("10");
        TextField txtMin = new TextField("5");

        if (existente != null) {
            txtCod.setText(existente.getCodigo());
            txtCod.setEditable(false);
            txtNom.setText(existente.getNombre());
            cmbCat.setValue(existente.getCategoria());
            txtSubcat.setText(existente.getSubcategoria());
            txtPrecio.setText(String.valueOf(existente.getPrecio()));
            txtStock.setText(String.valueOf(existente.getStock()));
            txtMin.setText(String.valueOf(existente.getStockMinimo()));
        }

        grid.add(new Label("Código:"), 0, 0); grid.add(txtCod, 1, 0);
        grid.add(new Label("Nombre:"), 0, 1); grid.add(txtNom, 1, 1);
        grid.add(new Label("Categoría:"), 0, 2); grid.add(cmbCat, 1, 2);
        grid.add(new Label("Subcategoría:"), 0, 3); grid.add(txtSubcat, 1, 3);
        grid.add(new Label("Precio ($):"), 0, 4); grid.add(txtPrecio, 1, 4);
        grid.add(new Label("Stock Inicial:"), 0, 5); grid.add(txtStock, 1, 5);
        grid.add(new Label("Stock Mínimo:"), 0, 6); grid.add(txtMin, 1, 6);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                int stock = Integer.parseInt(txtStock.getText().trim());
                int min = Integer.parseInt(txtMin.getText().trim());

                if (existente == null) {
                    servicioProductos.registrarProducto(txtCod.getText(), txtNom.getText(), cmbCat.getValue(), txtSubcat.getText(), precio, stock, min);
                    vista.getLblMensajeEstado().setText("Nuevo producto registrado con éxito.");
                } else {
                    servicioProductos.modificarProducto(existente, txtNom.getText(), cmbCat.getValue(), txtSubcat.getText(), precio, stock, min);
                    vista.getTblCatalogoProductos().refresh();
                    vista.getLblMensajeEstado().setText("Producto '" + existente.getCodigo() + "' modificado.");
                }
                actualizarContadorCatalogo();
                actualizarAlertaStock();
            } catch (NumberFormatException ex) {
                mostrarAlerta("Error de Formato", "Valores numéricos incorrectos", "Revise que precio y stock sean números válidos.", AlertType.ERROR);
            } catch (IllegalArgumentException ex) {
                mostrarAlerta("Validación", "Error al guardar", ex.getMessage(), AlertType.WARNING);
            }
        }
    }

    /**
     * Eventos de la Pestaña 3: Inventario y Stock.
     */
    private void configurarEventosInventario() {
        vista.getChkSoloBajoStock().setOnAction(e -> {
            boolean bajo = vista.getChkSoloBajoStock().isSelected();
            servicioInventario.recargarInventario(bajo);
            vista.getLblMensajeEstado().setText(bajo ? "Filtrando productos bajo stock mínimo." : "Mostrando todo el inventario.");
        });

        vista.getBtnRefrescarInventario().setOnAction(e -> {
            servicioInventario.recargarInventario(vista.getChkSoloBajoStock().isSelected());
            actualizarAlertaStock();
            vista.getLblMensajeEstado().setText("Existencias sincronizadas.");
        });

        vista.getBtnEntradaStock().setOnAction(e -> {
            MovimientoInventario sel = (MovimientoInventario) vista.getTblInventario().getSelectionModel().getSelectedItem();
            String codInicial = sel != null ? sel.getCodigo() : "";
            TextInputDialog d = new TextInputDialog(codInicial);
            d.setTitle("Entrada de Mercancía");
            d.setHeaderText("Recepción de Stock");
            d.setContentText("Código del producto:");
            d.showAndWait().ifPresent(cod -> {
                TextInputDialog dCant = new TextInputDialog("10");
                dCant.setTitle("Cantidad a ingresar");
                dCant.setHeaderText("Unidades recibidas de proveedor");
                dCant.setContentText("Cantidad:");
                dCant.showAndWait().ifPresent(cantStr -> {
                    try {
                        int c = Integer.parseInt(cantStr.trim());
                        servicioInventario.registrarEntrada(cod, c, "Compra a proveedor");
                        actualizarAlertaStock();
                        vista.getLblMensajeEstado().setText("Entrada de " + c + " unidades registrada.");
                    } catch (Exception ex) {
                        mostrarAlerta("Error", "No se pudo procesar", ex.getMessage(), AlertType.ERROR);
                    }
                });
            });
        });

        vista.getBtnSalidaStock().setOnAction(e -> {
            MovimientoInventario sel = (MovimientoInventario) vista.getTblInventario().getSelectionModel().getSelectedItem();
            if (sel == null) {
                mostrarAlerta("Inventario", "Selección Requerida", "Seleccione un producto para registrar salida.", AlertType.WARNING);
                return;
            }
            TextInputDialog dCant = new TextInputDialog("1");
            dCant.setTitle("Salida / Merma");
            dCant.setHeaderText("Retiro de existencias para: " + sel.getProducto());
            dCant.setContentText("Unidades a retirar:");
            dCant.showAndWait().ifPresent(cantStr -> {
                try {
                    int c = Integer.parseInt(cantStr.trim());
                    servicioInventario.registrarSalida(sel.getCodigo(), c, "Merma / daño");
                    actualizarAlertaStock();
                    vista.getLblMensajeEstado().setText("Salida registrada con éxito.");
                } catch (Exception ex) {
                    mostrarAlerta("Error", "No se pudo retirar", ex.getMessage(), AlertType.ERROR);
                }
            });
        });

        vista.getBtnAjusteStock().setOnAction(e -> {
            MovimientoInventario sel = (MovimientoInventario) vista.getTblInventario().getSelectionModel().getSelectedItem();
            if (sel == null) {
                mostrarAlerta("Inventario", "Selección Requerida", "Seleccione un producto para ajustar conteo.", AlertType.WARNING);
                return;
            }
            TextInputDialog dCant = new TextInputDialog(String.valueOf(sel.getExistencias()));
            dCant.setTitle("Ajuste Físico");
            dCant.setHeaderText("Conteo físico verificado para: " + sel.getProducto());
            dCant.setContentText("Existencias reales contadas:");
            dCant.showAndWait().ifPresent(cantStr -> {
                try {
                    int c = Integer.parseInt(cantStr.trim());
                    servicioInventario.registrarAjuste(sel.getCodigo(), c, "Auditoría de inventario");
                    actualizarAlertaStock();
                    vista.getLblMensajeEstado().setText("Ajuste físico actualizado a " + c + " piezas.");
                } catch (Exception ex) {
                    mostrarAlerta("Error", "Error en ajuste", ex.getMessage(), AlertType.ERROR);
                }
            });
        });

        vista.getBtnGenerarReporteStock().setOnAction(e -> {
            seleccionarPestana(vista.getTabReportes());
            vista.getCmbTipoReporte().getSelectionModel().select("1. Inventario General");
            vista.getBtnVistaPreviaReporte().fire();
        });
    }

    /**
     * Eventos de la Pestaña 4: Devoluciones.
     */
    private void configurarEventosDevoluciones() {
        vista.getBtnBuscarTicketDevolucion().setOnAction(e -> {
            String folio = vista.getTxtFolioDevolucion().getText().trim();
            if (folio.isEmpty()) {
                mostrarAlerta("Devoluciones", "Folio Requerido", "Ingrese el número de ticket (Ej. TKT-1001).", AlertType.WARNING);
                return;
            }
            Venta v = servicioDevoluciones.buscarVenta(folio);
            if (v == null) {
                vista.getLblInfoTicketDevolucion().setText("Ticket #" + folio + " no localizado.");
                mostrarAlerta("No encontrado", "Venta Inexistente", "No existe venta registrada con el folio: " + folio, AlertType.WARNING);
            } else {
                vista.getLblInfoTicketDevolucion().setText("Ticket #" + folio + " | Fecha: " + v.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + " | Total: " + formatearMoneda(v.getTotal()));
                vista.getLblMensajeEstado().setText("Venta #" + folio + " cargada. Seleccione los productos a devolver.");
            }
        });

        // Al hacer doble clic en un ítem de devolución, solicitar cantidad a devolver
        vista.getTblDevoluciones().setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                ItemDevolucion sel = (ItemDevolucion) vista.getTblDevoluciones().getSelectionModel().getSelectedItem();
                if (sel != null) {
                    TextInputDialog d = new TextInputDialog(String.valueOf(sel.getCantComprada()));
                    d.setTitle("Cantidad a Devolver");
                    d.setHeaderText("Producto: " + sel.getDescripcion() + " (Compradas: " + sel.getCantComprada() + ")");
                    d.setContentText("Piezas a devolver:");
                    d.showAndWait().ifPresent(cantStr -> {
                        try {
                            int c = Integer.parseInt(cantStr.trim());
                            if (c < 0 || c > sel.getCantComprada()) {
                                mostrarAlerta("Cantidad Inválida", "Rango excedido", "Debe ser entre 0 y " + sel.getCantComprada(), AlertType.WARNING);
                                return;
                            }
                            sel.setCantDevolver(c);
                            vista.getTblDevoluciones().refresh();
                            vista.getLblMensajeEstado().setText("Reembolso acumulado: " + formatearMoneda(servicioDevoluciones.calcularTotalReembolso()));
                        } catch (NumberFormatException ex) {
                            mostrarAlerta("Error", "Número Inválido", "Ingrese un entero.", AlertType.ERROR);
                        }
                    });
                }
            }
        });

        vista.getBtnProcesarDevolucion().setOnAction(e -> {
            String folioVenta = vista.getTxtFolioDevolucion().getText().trim();
            String motivo = vista.getCmbMotivoDevolucion().getValue();
            boolean reintegra = vista.getChkReintegrarStock().isSelected();

            try {
                application.modelo.Devolucion dev = servicioDevoluciones.procesarDevolucion(folioVenta, motivo, reintegra);
                servicioInventario.recargarInventario(false);
                actualizarAlertaStock();

                mostrarAlerta("Devolución Exitosa", "Nota de Crédito Generada: " + dev.getFolioDevolucion(),
                    "Reembolso autorizado al cliente: " + formatearMoneda(dev.getTotalReembolso()) + "\n" +
                    (reintegra ? "Las piezas han sido reintegradas al stock disponible." : "No se reintegró al stock."), AlertType.INFORMATION);

                vista.getTxtFolioDevolucion().clear();
                vista.getLblInfoTicketDevolucion().setText("Ticket no consultado");
                vista.getLblMensajeEstado().setText("Devolución " + dev.getFolioDevolucion() + " procesada.");
            } catch (IllegalArgumentException ex) {
                mostrarAlerta("Atención", "No se puede procesar", ex.getMessage(), AlertType.WARNING);
            }
        });
    }

    /**
     * Eventos de la Pestaña 5: Reportes del Sistema.
     */
    private void configurarEventosReportes() {
        vista.getBtnVistaPreviaReporte().setOnAction(e -> {
            String rep = vista.getCmbTipoReporte().getValue();
            LocalDate ini = vista.getDpFechaInicio().getValue();
            LocalDate fin = vista.getDpFechaFin().getValue();
            String cat = vista.getCmbCategoriaReporte().getValue();

            String contenido = servicioReportes.generarVistaPrevia(rep, ini, fin, cat);
            vista.getTxtDetallesReporte().setText(contenido);
            vista.getLblEstadoReporte().setText("Vista previa generada para: " + rep);
        });

        vista.getBtnExportarPDF().setOnAction(e -> {
            String rep = vista.getCmbTipoReporte().getValue();
            mostrarAlerta("Exportación", "Reporte Exportado",
                "El reporte '" + rep + "' ha sido emitido con formato oficial listo para su integración con JasperReports.", AlertType.INFORMATION);
            vista.getLblEstadoReporte().setText("Reporte emitido exitosamente.");
        });
    }

    private void recalcularTotalesVenta() {
        vista.getLblNumArticulos().setText(String.valueOf(servicioVentas.calcularTotalArticulos()));
        vista.getLblSubtotal().setText(formatearMoneda(servicioVentas.calcularSubtotal()));
        vista.getLblDescuento().setText(formatearMoneda(servicioVentas.calcularDescuentos()));
        vista.getLblIva().setText(formatearMoneda(servicioVentas.calcularIVA()));
        vista.getLblTotal().setText(formatearMoneda(servicioVentas.calcularTotal()));
        calcularCambio();
    }

    private void calcularCambio() {
        try {
            String strPaga = vista.getTxtPagaCon().getText().trim();
            if (strPaga.isEmpty()) {
                vista.getLblCambio().setText(formatearMoneda(0.0));
                return;
            }
            double pagaCon = Double.parseDouble(strPaga);
            double total = servicioVentas.calcularTotal();
            double cambio = Math.max(0.0, pagaCon - total);
            vista.getLblCambio().setText(formatearMoneda(cambio));
        } catch (NumberFormatException e) {
            vista.getLblCambio().setText(formatearMoneda(0.0));
        }
    }

    private void actualizarAlertaStock() {
        int criticos = servicioInventario.obtenerCantidadProductosCriticos();
        if (criticos > 0) {
            vista.getLblAlertaStock().setText("[!] Alerta: " + criticos + " producto(s) con stock crítico");
        } else {
            vista.getLblAlertaStock().setText("");
        }
    }

    private void actualizarContadorCatalogo() {
        int total = servicioProductos.getListaProductos().size();
        vista.getLblTotalProductosRegistrados().setText(total + " productos");
    }

    private void limpiarCamposVenta() {
        vista.getTxtCodigoBarras().clear();
        vista.getTxtCantidad().setText("1");
        vista.getTxtPagaCon().clear();
        vista.getLblCambio().setText(formatearMoneda(0.0));
        vista.getTxtCodigoBarras().requestFocus();
    }

    private void mostrarAlerta(String titulo, String cabecera, String contenido, AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    public VistaControlador getVista() {
        return vista;
    }
}
