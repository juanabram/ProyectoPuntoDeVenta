package application.Controlador;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

public class Controlador {

    private VistaControlador vista;

    public Controlador(VistaControlador vista) {
        this.vista = vista;
        inicializarElementos();
        configurarInteraccionBotones();
        configurarNavegacionMenus();
    }

    private void inicializarElementos() {
        // Cargar opciones en el ComboBox de método de pago
        vista.getCmbMetodoPago().setItems(FXCollections.observableArrayList(
            "Efectivo",
            "Tarjeta de Débito",
            "Tarjeta de Crédito",
            "Vales de Despensa",
            "Transferencia / QR"
        ));
        vista.getCmbMetodoPago().getSelectionModel().selectFirst();

        // Estado inicial de la barra inferior
        vista.getLblMensajeEstado().setText("Listo. Esperando interacción o escaneo de producto.");

        // Posicionar el cursor en el campo de código de barras
        Platform.runLater(() -> vista.getTxtCodigoBarras().requestFocus());
    }

    //Configura la respuesta e interacción de todos los botones de la interfaz.
     
    private void configurarInteraccionBotones() {
        // Captura y Catálogo
        vista.getBtnAgregar().setOnAction(e -> {
            String codigo = vista.getTxtCodigoBarras().getText().trim();
            if (codigo.isEmpty()) {
                mostrarAlerta("Atención", "Campo Vacío",
                    "Ingrese un código de barras para interactuar con la lista.", AlertType.WARNING);
            } else {
                vista.getLblMensajeEstado().setText("Interacción: Producto con código '" + codigo + "' llamado a la vista.");
                vista.getTxtCodigoBarras().clear();
                vista.getTxtCantidad().setText("1");
            }
            vista.getTxtCodigoBarras().requestFocus();
        });

        vista.getBtnBuscar().setOnAction(e -> {
            mostrarAlerta("Navegación", "Catálogo de Productos (F2)",
                "Navegando a la ventana de consulta y búsqueda de artículos del catálogo.", AlertType.INFORMATION);
            vista.getTxtCodigoBarras().requestFocus();
        });

        // Acciones sobre elementos de la lista
        vista.getBtnEliminarArticulo().setOnAction(e -> {
            mostrarAlerta("Interacción", "Eliminar Producto",
                "Llamada a la acción de remover el elemento seleccionado de la tabla.", AlertType.INFORMATION);
        });

        vista.getBtnModificarCantidad().setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Interacción");
            dialog.setHeaderText("Modificar Cantidad (F4)");
            dialog.setContentText("Nueva cantidad a asignar:");
            dialog.showAndWait().ifPresent(cant -> {
                vista.getLblMensajeEstado().setText("Cantidad actualizada a: " + cant);
            });
        });

        vista.getBtnAplicarDescuento().setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Interacción");
            dialog.setHeaderText("Aplicar Promoción / Cupón");
            dialog.setContentText("Ingrese código de promoción:");
            dialog.showAndWait().ifPresent(cupon -> {
                mostrarAlerta("Interacción", "Cupón Registrado",
                    "Llamada al registro del cupón: " + cupon, AlertType.INFORMATION);
            });
        });

        vista.getBtnPonerEnEspera().setOnAction(e -> {
            mostrarAlerta("Navegación", "Venta en Espera (F7)",
                "Ticket actual puesto en espera. Vista despejada para atender al siguiente cliente.", AlertType.INFORMATION);
            limpiarCamposVista();
        });

        vista.getBtnRecuperarVenta().setOnAction(e -> {
            mostrarAlerta("Navegación", "Recuperar Venta",
                "Navegando a la lista de ventas pendientes/pausadas para restaurar en pantalla.", AlertType.INFORMATION);
        });

        vista.getBtnCancelarVenta().setOnAction(e -> {
            Alert confirm = new Alert(AlertType.CONFIRMATION, "¿Desea cancelar y reiniciar la vista actual?", ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirmación");
            confirm.setHeaderText(null);
            confirm.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.YES) {
                    limpiarCamposVista();
                    vista.getLblMensajeEstado().setText("Vista cancelada y restablecida.");
                }
            });
        });

        // Navegación a Servicios Rápidos (Tienda de Conveniencia)
        vista.getBtnRecargas().setOnAction(e -> {
            mostrarAlerta("Navegación", "Módulo de Recargas Telefónicas (F5)",
                "Navegando a la pantalla de tiempo aire (Telcel, Movistar, AT&T).", AlertType.INFORMATION);
        });

        vista.getBtnPagoServicios().setOnAction(e -> {
            mostrarAlerta("Navegación", "Módulo de Pago de Servicios (F6)",
                "Navegando al lector y pago de recibos (Luz, Agua, Gas, Internet).", AlertType.INFORMATION);
        });

        vista.getBtnPines().setOnAction(e -> {
            mostrarAlerta("Navegación", "Módulo de Pines y Tarjetas de Regalo",
                "Navegando al catálogo de pines electrónicos (Netflix, Spotify, Xbox, Amazon).", AlertType.INFORMATION);
        });

        vista.getBtnDepositos().setOnAction(e -> {
            mostrarAlerta("Navegación", "Módulo de Corresponsalía Bancaria",
                "Navegando a la ventana de depósitos y retiros de efectivo.", AlertType.INFORMATION);
        });

        // Cobro y Finalización
        vista.getBtnCobrar().setOnAction(e -> {
            String metodo = vista.getCmbMetodoPago().getValue();
            boolean ticket = vista.getChkTicket().isSelected();
            String detalleTicket = ticket ? " (Impresión de ticket seleccionada)" : "";

            mostrarAlerta("Navegación / Cobro", "Finalizar Transacción (F12)",
                "Llamada a la pantalla de confirmación de cobro con método: " + metodo + detalleTicket + ".", AlertType.INFORMATION);
            limpiarCamposVista();
            vista.getLblMensajeEstado().setText("Transacción finalizada. Listo para nueva venta.");
        });
    }

    //Configura la navegación y acciones a través de la barra de menús superior.
    private void configurarNavegacionMenus() {
        // Menú Venta
        vista.getMenuNuevaVenta().setOnAction(e -> {
            limpiarCamposVista();
            vista.getLblMensajeEstado().setText("Nueva venta iniciada.");
        });
        vista.getMenuPonerEnEspera().setOnAction(e -> vista.getBtnPonerEnEspera().fire());
        vista.getMenuRecuperarVenta().setOnAction(e -> vista.getBtnRecuperarVenta().fire());
        vista.getMenuCancelarVenta().setOnAction(e -> vista.getBtnCancelarVenta().fire());
        vista.getMenuSalir().setOnAction(e -> Platform.exit());

        // Menú Operaciones de Caja
        vista.getMenuEntradaEfectivo().setOnAction(e -> {
            mostrarAlerta("Navegación", "Entrada de Efectivo",
                "Navegando a la ventana de registro de fondo o ingreso a caja.", AlertType.INFORMATION);
        });
        vista.getMenuSalidaEfectivo().setOnAction(e -> {
            mostrarAlerta("Navegación", "Salida de Efectivo",
                "Navegando a la ventana de retiro o egreso de efectivo.", AlertType.INFORMATION);
        });
        vista.getMenuCorteX().setOnAction(e -> {
            mostrarAlerta("Navegación", "Corte de Caja X",
                "Navegando al reporte preliminar de arqueo de turno.", AlertType.INFORMATION);
        });
        vista.getMenuCorteZ().setOnAction(e -> {
            mostrarAlerta("Navegación", "Corte de Caja Z",
                "Navegando al cierre definitivo de caja del día.", AlertType.INFORMATION);
        });
        vista.getMenuHistorialVentas().setOnAction(e -> {
            mostrarAlerta("Navegación", "Historial de Ventas",
                "Navegando al visor de transacciones realizadas en la jornada.", AlertType.INFORMATION);
        });
        vista.getMenuDevoluciones().setOnAction(e -> {
            mostrarAlerta("Navegación", "Devoluciones de Producto",
                "Navegando al módulo de atención y cancelación de ticket por devolución.", AlertType.INFORMATION);
        });

        // Menú Catálogo y Servicios
        vista.getMenuBuscarProducto().setOnAction(e -> vista.getBtnBuscar().fire());
        vista.getMenuListaPrecios().setOnAction(e -> {
            mostrarAlerta("Navegación", "Consulta de Precios",
                "Navegando al verificador de precios y códigos.", AlertType.INFORMATION);
        });
        vista.getMenuPromociones().setOnAction(e -> {
            mostrarAlerta("Navegación", "Promociones Activas",
                "Navegando a la lista de ofertas vigentes en la tienda.", AlertType.INFORMATION);
        });
        vista.getMenuRecargas().setOnAction(e -> vista.getBtnRecargas().fire());
        vista.getMenuPagoServicios().setOnAction(e -> vista.getBtnPagoServicios().fire());
        vista.getMenuPines().setOnAction(e -> vista.getBtnPines().fire());
        vista.getMenuDepositos().setOnAction(e -> vista.getBtnDepositos().fire());

        // Menú Configuración y Ayuda
        vista.getMenuAjustesCaja().setOnAction(e -> {
            mostrarAlerta("Navegación", "Ajustes de Terminal",
                "Navegando a la configuración de la terminal de punto de venta.", AlertType.INFORMATION);
        });
        vista.getMenuImpresora().setOnAction(e -> {
            mostrarAlerta("Navegación", "Configuración de Impresora",
                "Navegando a la selección y calibración de la impresora térmica.", AlertType.INFORMATION);
        });
        vista.getMenuAtajos().setOnAction(e -> {
            mostrarAlerta("Ayuda", "Atajos de Teclado del POS",
                "[F1] Ayuda\n[F2] Buscar en Catálogo\n[F4] Cambiar Cantidad\n[F5] Recargas Telefónicas\n" +
                "[F6] Pago de Servicios\n[F7] Venta en Espera\n[F12] Cobrar / Finalizar\n[Esc] Cancelar Venta", AlertType.INFORMATION);
        });
        vista.getMenuAcercaDe().setOnAction(e -> {
            mostrarAlerta("Acerca de", "Sistema POS - Tienda de Conveniencia",
                "Prototipo de Interfaz Gráfica JavaFX.\nEnfoque en navegación y distribución de controles.", AlertType.INFORMATION);
        });
    }

 
    //Limpia los campos de entrada y restablece el foco.
    private void limpiarCamposVista() {
        vista.getTxtCodigoBarras().clear();
        vista.getTxtCantidad().setText("1");
        vista.getTxtCliente().clear();
        vista.getTxtPagaCon().clear();
        vista.getTxtCodigoBarras().requestFocus();
    }

    //Despliega una alerta estándar de JavaFX para confirmar navegación o llamada al elemento.
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
