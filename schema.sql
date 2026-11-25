-- =============================================================================
-- PROYECTO INTEGRADOR: PUNTO DE VENTA (TIENDA DE CONVENIENCIA)
-- SCRIPT DE DEFINICIÓN DE BASE DE DATOS (ESQUEMA RELACIONAL Y DATOS INICIALES)
-- =============================================================================

CREATE DATABASE IF NOT EXISTS punto_de_venta;
USE punto_de_venta;

-- 1. TABLA: CATEGORIAS
CREATE TABLE IF NOT EXISTS categorias (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- 2. TABLA: PRODUCTOS
CREATE TABLE IF NOT EXISTS productos (
    codigo VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    id_categoria INT,
    subcategoria VARCHAR(100),
    precio DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 5,
    estado VARCHAR(20) NOT NULL DEFAULT 'Activo',
    imagen VARCHAR(255) DEFAULT 'default.png',
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
);

-- 3. TABLA: VENTAS
CREATE TABLE IF NOT EXISTS ventas (
    folio VARCHAR(50) PRIMARY KEY,
    fecha_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
    cajero VARCHAR(100) NOT NULL DEFAULT 'Cajero Activo',
    subtotal DECIMAL(10, 2) NOT NULL,
    descuento DECIMAL(10, 2) DEFAULT 0.00,
    iva DECIMAL(10, 2) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,
    paga_con DECIMAL(10, 2) NOT NULL,
    cambio DECIMAL(10, 2) NOT NULL
);

-- 4. TABLA: DETALLE_VENTAS
CREATE TABLE IF NOT EXISTS detalle_ventas (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    folio_venta VARCHAR(50) NOT NULL,
    numero_item INT NOT NULL,
    codigo_producto VARCHAR(50) NOT NULL,
    descripcion VARCHAR(150) NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    cantidad INT NOT NULL,
    descuento DECIMAL(10, 2) DEFAULT 0.00,
    importe DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_detalle_venta FOREIGN KEY (folio_venta) REFERENCES ventas(folio),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (codigo_producto) REFERENCES productos(codigo)
);

-- 5. TABLA: MOVIMIENTOS_INVENTARIO
CREATE TABLE IF NOT EXISTS movimientos_inventario (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    codigo_producto VARCHAR(50) NOT NULL,
    tipo VARCHAR(50) NOT NULL, -- Entrada, Salida, Ajuste
    cantidad INT NOT NULL,
    existencias_previas INT NOT NULL,
    existencias_resultantes INT NOT NULL,
    motivo VARCHAR(255),
    fecha_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mov_producto FOREIGN KEY (codigo_producto) REFERENCES productos(codigo)
);

-- 6. TABLA: DEVOLUCIONES
CREATE TABLE IF NOT EXISTS devoluciones (
    folio_devolucion VARCHAR(50) PRIMARY KEY,
    folio_venta_original VARCHAR(50) NOT NULL,
    fecha_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
    motivo VARCHAR(150) NOT NULL,
    reintegrar_stock BOOLEAN DEFAULT TRUE,
    total_reembolso DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_dev_venta FOREIGN KEY (folio_venta_original) REFERENCES ventas(folio)
);

-- 7. TABLA: DETALLE_DEVOLUCIONES
CREATE TABLE IF NOT EXISTS detalle_devoluciones (
    id_detalle_dev INT AUTO_INCREMENT PRIMARY KEY,
    folio_devolucion VARCHAR(50) NOT NULL,
    codigo_producto VARCHAR(50) NOT NULL,
    descripcion VARCHAR(150) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    cantidad_comprada INT NOT NULL,
    cantidad_devuelta INT NOT NULL,
    subtotal_reembolso DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_det_dev FOREIGN KEY (folio_devolucion) REFERENCES devoluciones(folio_devolucion)
);

-- =============================================================================
-- INSERCIÓN DE DATOS INICIALES (20 CATEGORÍAS OBLIGATORIAS DEL SISTEMA)
-- =========================================================================
INSERT INTO categorias (nombre) VALUES
('Bebidas'), ('Lácteos'), ('Botanas'), ('Panadería'), ('Enlatados'),
('Limpieza'), ('Higiene Personal'), ('Farmacia'), ('Dulcería'), ('Carnes Frías'),
('Frutas y Verduras'), ('Mascotas'), ('Papelería'), ('Abarrotes'), ('Cuidado del Hogar'),
('Licores y Cervezas'), ('Congelados'), ('Cuidado de Bebé'), ('Despensa General'), ('Snacks y Confitería');
