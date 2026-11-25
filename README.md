# Punto de Venta - Tienda de Conveniencia

Sistema de Punto de Venta desarrollado en **JavaFX 21** con arquitectura por capas (**MVC + Repositorio Resiliente + Service Layer**).

---

## 🏛 Arquitectura del Backend Implementada

El proyecto ha sido estructurado para permitir desacoplamiento total entre la interfaz de usuario, la lógica del negocio y la persistencia de datos:

```text
src/
 ├── module-info.java                          # Descriptor de módulos y exportación de paquetes
 └── application/
      ├── Application/
      │    └── Main.java                      # Punto de entrada de la aplicación JavaFX
      ├── Controlador/
      │    ├── VistaControlador.java          # Inyección FXML de componentes visuales de todas las pestañas
      │    └── Controlador.java               # Coordinador de eventos, atajos de teclado y servicios
      ├── modelo/                             # Lógica del Modelo (14 clases: Entidades, Datos y Servicios)
      │    ├── Categoria.java                 # Modelo de categorías de productos
      │    ├── Producto.java                  # Modelo de producto con control de stock y precios
      │    ├── ItemVenta.java                 # Línea del carrito de compras
      │    ├── Venta.java                     # Entidad de venta terminada y ticket
      │    ├── MovimientoInventario.java      # Registro de kardex (entradas, salidas, ajustes)
      │    ├── Devolucion.java                # Encabezado de devolución/reembolso
      │    ├── ItemDevolucion.java            # Detalle de artículos devueltos
      │    ├── datos/                         # Capa de Datos y Conexión JDBC
      │    │    ├── ConexionBD.java           # Administrador de conexiones JDBC (MySQL, SQL Server, etc.)
      │    │    └── RepositorioDatos.java     # CRUD JDBC con respaldo automático en memoria (20 productos)
      │    └── servicio/                      # Capa de Lógica de Negocio
      │         ├── ServicioVentas.java       # Búsqueda por código de barras, totales, IVA, pago y cambio
      │         ├── ServicioProductos.java    # Catálogo, filtros, altas, modificaciones y baja lógica
      │         ├── ServicioInventario.java   # Kardex, ajustes de stock y alertas de existencias críticas
      │         ├── ServicioDevoluciones.java # Búsqueda de tickets, cálculo de reembolsos y reintegro
      │         └── ServicioReportes.java     # Generación y vista previa de reportes de corte y ventas
      └── vista/                              # Recursos de interfaz de usuario
           ├── PuntoDeVenta.fxml              # Vista FXML con las 5 pestañas
           └── estilos.css                    # Hoja de estilos de la interfaz
```

---

## 🗄️ Guía para Conectar la Base de Datos

Todo el backend opera con un **repositorio resiliente** en [`RepositorioDatos.java`](src/application/modelo/datos/RepositorioDatos.java): si la base de datos aún no está conectada, trabaja de manera autónoma en memoria con 20 productos iniciales. Cuando esté lista la base de datos:

1. **Crear las tablas en MySQL / MariaDB / SQL Server:**
   - Ejecutar el script incluido en la raíz del proyecto: [`schema.sql`](schema.sql).
   - Crea automáticamente las tablas: `categorias`, `productos`, `ventas`, `detalle_ventas`, `movimientos_inventario`, `devoluciones` y `detalle_devoluciones`.
2. **Configurar los datos de conexión:**
   - Abrir [`ConexionBD.java`](src/application/modelo/datos/ConexionBD.java).
   - Ajustar `URL`, `USER` y `PASS` (por defecto apunta a `localhost:3306/punto_de_venta` con usuario `root`).
3. **Driver JDBC:**
   - Colocar el conector JAR correspondiente (por ejemplo `mysql-connector-j-8.x.jar`) en la carpeta `lib/` y añadirlo al Build Path.
4. **Sentencias SQL ya implementadas:**
   - Todos los métodos CRUD y transaccionales en [`RepositorioDatos.java`](src/application/modelo/datos/RepositorioDatos.java) ya incluyen `PreparedStatement` para `INSERT`, `UPDATE`, `SELECT` (alineados con `id_categoria` en `schema.sql`) y transacciones.

---

## 🚀 Ejecución en Eclipse

1. Abre **Eclipse IDE**.
2. Si el proyecto no aparece en el *Package Explorer*:
   - Menú **File** > **Import...** > **General** > **Existing Projects into Workspace** y selecciona la carpeta del proyecto.
3. Asegúrate de tener JavaFX 21 en `lib/` (por ejemplo `javafx-base-21.0.2-win.jar`, `javafx-controls-21.0.2-win.jar`, `javafx-fxml-21.0.2-win.jar`, `javafx-graphics-21.0.2-win.jar`) o configurar una User Library de JavaFX 21 en el Build Path.
4. Para ejecutar la aplicación:
   - Haz clic derecho sobre [`PuntoDeVenta.launch`](PuntoDeVenta.launch) > **Run As** > **PuntoDeVenta**, o
   - Haz clic derecho en [`Main.java`](src/application/Application/Main.java) > **Run As** > **Java Application**.
