-- =============================================================================
-- ESQUEMA DE BASE DE DATOS PARA MICROSOFT ACCESS (PuntoDeVenta.accdb)
-- Compatible con UCanAccess 5.0.1 y Microsoft Access (DDL / SQL)
-- =============================================================================

CREATE TABLE Categorias (
    Id          LONG PRIMARY KEY,
    Nombre      VARCHAR(100) NOT NULL,
    Descripcion VARCHAR(255)
);

CREATE TABLE Productos (
    Codigo        VARCHAR(50) PRIMARY KEY,
    Nombre        VARCHAR(150) NOT NULL,
    Categoria     VARCHAR(100),
    Subcategoria  VARCHAR(100),
    Precio        DOUBLE,
    Stock         LONG,
    StockMinimo   LONG,
    Estado        VARCHAR(20),
    Imagen        VARCHAR(255),
    FechaRegistro VARCHAR(25)
);

CREATE TABLE Ventas (
    Folio      VARCHAR(20) PRIMARY KEY,
    FechaHora  VARCHAR(25),
    Cajero     VARCHAR(100),
    Subtotal   DOUBLE,
    Descuento  DOUBLE,
    Iva        DOUBLE,
    Total      DOUBLE,
    MetodoPago VARCHAR(50),
    PagaCon    DOUBLE,
    Cambio     DOUBLE
);

CREATE TABLE DetalleVentas (
    Id          COUNTER PRIMARY KEY,
    Folio       VARCHAR(20),
    NumeroItem  LONG,
    Codigo      VARCHAR(50),
    Descripcion VARCHAR(150),
    Precio      DOUBLE,
    Cantidad    LONG,
    Descuento   DOUBLE,
    Importe     DOUBLE
);

CREATE TABLE MovimientosInventario (
    Id                     COUNTER PRIMARY KEY,
    Codigo                 VARCHAR(50),
    Producto               VARCHAR(150),
    Tipo                   VARCHAR(20),
    Cantidad               LONG,
    ExistenciasPrevias     LONG,
    ExistenciasResultantes LONG,
    Motivo                 VARCHAR(255),
    FechaHora              VARCHAR(25)
);

CREATE TABLE Devoluciones (
    FolioDevolucion VARCHAR(20) PRIMARY KEY,
    FolioVenta      VARCHAR(20),
    FechaHora       VARCHAR(25),
    Motivo          VARCHAR(150),
    ReintegrarStock VARCHAR(5),
    TotalReembolso  DOUBLE
);

CREATE TABLE DetalleDevoluciones (
    Id                COUNTER PRIMARY KEY,
    FolioDevolucion   VARCHAR(20),
    Codigo            VARCHAR(50),
    Descripcion       VARCHAR(150),
    Precio            DOUBLE,
    CantidadComprada  LONG,
    CantidadDevuelta  LONG,
    SubtotalReembolso DOUBLE
);

-- =============================================================================
-- DATOS INICIALES: 20 CATEGORIAS
-- =============================================================================
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (1, 'Bebidas', 'Categoría de Bebidas');
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (2, 'Lácteos', 'Categoría de Lácteos');
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (3, 'Botanas', 'Categoría de Botanas');
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (4, 'Panadería', 'Categoría de Panadería');
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (5, 'Enlatados', 'Categoría de Enlatados');
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (6, 'Limpieza', 'Categoría de Limpieza');
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (7, 'Higiene Personal', 'Categoría de Higiene Personal');
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (8, 'Farmacia', 'Categoría de Farmacia');
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (9, 'Dulcería', 'Categoría de Dulcería');
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (10, 'Carnes Frías', 'Categoría de Carnes Frías');
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (11, 'Frutas y Verduras', 'Categoría de Frutas y Verduras');
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (12, 'Mascotas', 'Categoría de Mascotas');
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (13, 'Papelería', 'Categoría de Papelería');
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (14, 'Abarrotes', 'Categoría de Abarrotes');
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (15, 'Cuidado del Hogar', 'Categoría de Cuidado del Hogar');
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (16, 'Licores y Cervezas', 'Categoría de Licores y Cervezas');
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (17, 'Congelados', 'Categoría de Congelados');
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (18, 'Cuidado de Bebé', 'Categoría de Cuidado de Bebé');
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (19, 'Despensa General', 'Categoría de Despensa General');
INSERT INTO Categorias (Id, Nombre, Descripcion) VALUES (20, 'Snacks y Confitería', 'Categoría de Snacks y Confitería');
