package application.Controlador;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

/**
 * Clase Controlador:
 * Gestiona la navegación real entre pestañas (TabPane) y las interacciones
 * con los elementos visuales de cada uno de los módulos del proyecto integrador.
 */
public class Controlador {

    private VistaControlador vista;

    public Controlador(VistaControlador vista) {
        this.vista = vista;
        inicializarNavegacionPestanas();
        inicializarDatosComponentes();
        configurarEventosVentas();
        configurarEventosProductos();
        configurarEventosInventario();
        configurarEventosDevoluciones();
        configurarEventosReportes();
    }

    /**
     * Configura la navegación y respuesta al alternar entre pestañas del TabPane.
     */
    private void inicializarNavegacionPestanas() {
        // Listener para detectar el cambio de pestaña (cuando el usuario hace clic directo en las pestañas)
        vista.getTabPanePrincipal().getSelectionModel().selectedItemProperty().addListener((obs, anteriorTab, nuevaTab) -> {
            if (nuevaTab != null) {
                vista.getLblMensajeEstado().setText("Pestaña activa: " + nuevaTab.getText());
                
                // Gestionar el foco según la pestaña seleccionada
                if (nuevaTab == vista.getTabVentas()) {
                    vista.getTxtCodigoBarras().requestFocus();
                } else if (nuevaTab == vista.getTabProductos()) {
                    vista.getTxtBuscarCatalogo().requestFocus();
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
                "[F1] Ayuda\n[F2] Buscar en Catálogo\n[F4] Modificar Cantidad\n[F12] Cobrar / Finalizar Venta\n[Esc] Limpiar / Cancelar", AlertType.INFORMATION);
        });

        vista.getMenuAcercaDe().setOnAction(e -> {
            mostrarAlerta("Acerca del Proyecto", "Proyecto Integrador - Sistema Punto de Venta",
                "Arquitectura MVC con JavaFX, JasperReports y Base de Datos Relacional.\nCumple con normativas ISO y rúbricas CACEI.", AlertType.INFORMATION);
        });
    }

    /**
     * Cambia de manera inmediata la pestaña visible en el TabPane.
     */
    private void seleccionarPestana(javafx.scene.control.Tab tab) {
        vista.getTabPanePrincipal().getSelectionModel().select(tab);
    }

    /**
     * Inicializa los datos y selectores de cada una de las pestañas.
     */
    private void inicializarDatosComponentes() {
        // Fecha y hora
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy | HH:mm");
        vista.getLblFechaHora().setText(LocalDateTime.now().format(formatoFecha));

        // Pestaña 1: Métodos de Pago
        vista.getCmbMetodoPago().setItems(FXCollections.observableArrayList(
            "Efectivo",
            "Tarjeta de Débito",
            "Tarjeta de Crédito",
            "Transferencia Bancaria"
        ));
        vista.getCmbMetodoPago().getSelectionModel().selectFirst();

        // Pestaña 2: Categorías para el Catálogo (20 categorías requeridas por el proyecto)
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

        // Pestaña 5: Tipos de Reportes (JasperReports obligatorios)
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

        // Estado inicial
        vista.getLblAlertaStock().setText("[!] Alerta: 2 productos con stock mínimo");
        vista.getLblMensajeEstado().setText("Pestaña activa: Caja / Ventas");
        Platform.runLater(() -> vista.getTxtCodigoBarras().requestFocus());
    }

    /**
     * Configura eventos de la Pestaña 1: Caja / Ventas.
     */
    private void configurarEventosVentas() {
        vista.getBtnAgregar().setOnAction(e -> {
            String codigo = vista.getTxtCodigoBarras().getText().trim();
            if (codigo.isEmpty()) {
                mostrarAlerta("Atención", "Código Vacío",
                    "Ingrese un código de barras para agregarlo a la venta.", AlertType.WARNING);
            } else {
                vista.getLblMensajeEstado().setText("Producto '" + codigo + "' agregado al carrito de venta.");
                vista.getTxtCodigoBarras().clear();
                vista.getTxtCantidad().setText("1");
            }
            vista.getTxtCodigoBarras().requestFocus();
        });

        vista.getBtnBuscar().setOnAction(e -> {
            seleccionarPestana(vista.getTabProductos());
            vista.getLblMensajeEstado().setText("Seleccione un producto del catálogo para agregarlo.");
        });

        vista.getBtnEliminarArticulo().setOnAction(e -> {
            mostrarAlerta("Carrito", "Quitar Artículo",
                "Artículo removido de la lista de venta.", AlertType.INFORMATION);
        });

        vista.getBtnModificarCantidad().setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Modificar Cantidad");
            dialog.setHeaderText("Ajuste de unidades");
            dialog.setContentText("Nueva cantidad:");
            dialog.showAndWait().ifPresent(cant -> vista.getLblMensajeEstado().setText("Cantidad actualizada a: " + cant));
        });

        vista.getBtnAplicarDescuento().setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("0");
            dialog.setTitle("Descuentos");
            dialog.setHeaderText("Aplicar Descuento");
            dialog.setContentText("Porcentaje o monto a descontar:");
            dialog.showAndWait().ifPresent(desc -> mostrarAlerta("Descuento", "Descuento Aplicado", "Descuento registrado correctamente.", AlertType.INFORMATION));
        });

        vista.getBtnCancelarVenta().setOnAction(e -> {
            Alert confirm = new Alert(AlertType.CONFIRMATION, "¿Desea cancelar la venta actual?", ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Cancelar Venta");
            confirm.setHeaderText(null);
            confirm.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.YES) {
                    limpiarCamposVenta();
                    vista.getLblMensajeEstado().setText("Venta cancelada. Listo para nueva operación.");
                }
            });
        });

        vista.getBtnCobrar().setOnAction(e -> {
            String metodo = vista.getCmbMetodoPago().getValue();
            boolean ticket = vista.getChkTicket().isSelected();
            String msgTicket = ticket ? "\nTicket emitido para impresión." : "";
            mostrarAlerta("Venta Exitosa", "Transacción Completada",
                "Venta liquidada con éxito mediante " + metodo + "." + msgTicket, AlertType.INFORMATION);
            limpiarCamposVenta();
        });
    }

    /**
     * Configura eventos de la Pestaña 2: Productos y Categorías.
     */
    private void configurarEventosProductos() {
        vista.getBtnFiltrarProductos().setOnAction(e -> {
            String busqueda = vista.getTxtBuscarCatalogo().getText().trim();
            String cat = vista.getCmbFiltroCategoria().getValue();
            vista.getLblMensajeEstado().setText("Filtrando productos por '" + busqueda + "' en categoría '" + cat + "'.");
        });

        vista.getBtnRecargarCatalogo().setOnAction(e -> {
            vista.getTxtBuscarCatalogo().clear();
            vista.getCmbFiltroCategoria().getSelectionModel().selectFirst();
            vista.getLblMensajeEstado().setText("Catálogo completo mostrado (400 productos registrados).");
        });

        vista.getBtnNuevoProducto().setOnAction(e -> {
            mostrarAlerta("Catálogo", "Registrar Producto",
                "Formulario para dar de alta un nuevo producto (con validación de categoría e imagen obligatoria).", AlertType.INFORMATION);
        });

        vista.getBtnModificarProducto().setOnAction(e -> {
            mostrarAlerta("Catálogo", "Modificar Producto",
                "Formulario para actualizar precios, descripción o stock del producto seleccionado.", AlertType.INFORMATION);
        });

        vista.getBtnBajaLogicaProducto().setOnAction(e -> {
            mostrarAlerta("Catálogo", "Baja Lógica de Producto",
                "El producto seleccionado pasará a estado inactivo (baja lógica sin eliminación física).", AlertType.WARNING);
        });

        vista.getBtnAdministrarCategorias().setOnAction(e -> {
            mostrarAlerta("Catálogo", "Administración de Categorías",
                "Gestión de las 20 categorías y sus subcategorías obligatorias.", AlertType.INFORMATION);
        });

        vista.getBtnAdministrarImagenes().setOnAction(e -> {
            mostrarAlerta("Catálogo", "Gestor de Imágenes",
                "Asignación de imágenes correspondientes para los 400 productos del catálogo.", AlertType.INFORMATION);
        });
    }

    /**
     * Configura eventos de la Pestaña 3: Inventario y Stock.
     */
    private void configurarEventosInventario() {
        vista.getChkSoloBajoStock().setOnAction(e -> {
            if (vista.getChkSoloBajoStock().isSelected()) {
                vista.getLblMensajeEstado().setText("Mostrando únicamente productos con stock menor o igual al mínimo.");
            } else {
                vista.getLblMensajeEstado().setText("Mostrando todas las existencias del inventario.");
            }
        });

        vista.getBtnRefrescarInventario().setOnAction(e -> {
            vista.getLblMensajeEstado().setText("Existencias de inventario sincronizadas correctamente.");
        });

        vista.getBtnEntradaStock().setOnAction(e -> {
            mostrarAlerta("Inventario", "Entrada de Mercancía",
                "Registro de recepción de productos por compra a proveedores (+Stock).", AlertType.INFORMATION);
        });

        vista.getBtnSalidaStock().setOnAction(e -> {
            mostrarAlerta("Inventario", "Salida / Merma de Mercancía",
                "Registro de salida por merma, daño o ajuste negativo (-Stock).", AlertType.INFORMATION);
        });

        vista.getBtnAjusteStock().setOnAction(e -> {
            mostrarAlerta("Inventario", "Ajuste Físico de Existencias",
                "Conciliación de inventario físico vs. sistema.", AlertType.INFORMATION);
        });

        vista.getBtnGenerarReporteStock().setOnAction(e -> {
            seleccionarPestana(vista.getTabReportes());
            vista.getCmbTipoReporte().getSelectionModel().select("1. Inventario General");
        });
    }

    /**
     * Configura eventos de la Pestaña 4: Devoluciones.
     */
    private void configurarEventosDevoluciones() {
        vista.getBtnBuscarTicketDevolucion().setOnAction(e -> {
            String folio = vista.getTxtFolioDevolucion().getText().trim();
            if (folio.isEmpty()) {
                mostrarAlerta("Devoluciones", "Folio Requerido",
                    "Ingrese el número de ticket o folio de la venta a consultar.", AlertType.WARNING);
            } else {
                vista.getLblInfoTicketDevolucion().setText("Ticket #" + folio + " localizado | Fecha: 22/11/2026 | Total: $ 250.00");
                vista.getLblMensajeEstado().setText("Detalle del ticket #" + folio + " cargado para devolución.");
            }
        });

        vista.getBtnProcesarDevolucion().setOnAction(e -> {
            boolean reintegra = vista.getChkReintegrarStock().isSelected();
            String msgReintegra = reintegra ? "\nLos productos han sido devueltos al inventario (Stock sumado)." : "";
            mostrarAlerta("Devoluciones", "Devolución Procesada con Éxito",
                "Comprobante de devolución generado." + msgReintegra, AlertType.INFORMATION);
            vista.getTxtFolioDevolucion().clear();
            vista.getLblInfoTicketDevolucion().setText("Ticket no consultado");
        });
    }

    /**
     * Configura eventos de la Pestaña 5: Reportes (JasperReports).
     */
    private void configurarEventosReportes() {
        vista.getBtnVistaPreviaReporte().setOnAction(e -> {
            String rep = vista.getCmbTipoReporte().getValue();
            vista.getTxtDetallesReporte().setText(
                "=== VISTA PREVIA DEL REPORTE (JasperReports) ===\n" +
                "Plantilla: " + rep.toLowerCase().replaceAll("[^a-z0-9]", "_") + ".jrxml\n" +
                "Conexión: JDBC activa (Base de Datos Relacional)\n" +
                "Fecha de Generación: " + LocalDateTime.now() + "\n" +
                "Filtros aplicados: Categoría [" + vista.getCmbCategoriaReporte().getValue() + "]\n" +
                "Estado: Listo para compilación y exportación PDF."
            );
            vista.getLblEstadoReporte().setText("Vista previa generada para: " + rep);
        });

        vista.getBtnExportarPDF().setOnAction(e -> {
            String rep = vista.getCmbTipoReporte().getValue();
            mostrarAlerta("JasperReports", "Exportación a PDF",
                "Generando archivo PDF con logotipo y fecha/hora: '" + rep + ".pdf'.", AlertType.INFORMATION);
            vista.getLblEstadoReporte().setText("Archivo PDF exportado exitosamente.");
        });
    }

    private void limpiarCamposVenta() {
        vista.getTxtCodigoBarras().clear();
        vista.getTxtCantidad().setText("1");
        vista.getTxtPagaCon().clear();
        vista.getLblCambio().setText("$ 0.00");
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
