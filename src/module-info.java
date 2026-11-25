module ProyectoPuntoDeVenta {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;
	
	requires java.sql;
	
	exports application.Application;
	exports application.modelo;
	exports application.modelo.datos;
	exports application.modelo.servicio;
	exports application.Controlador;

	opens application.Application to javafx.graphics;
	opens application.Controlador to javafx.fxml, javafx.graphics;
	opens application.vista to javafx.fxml;
	opens application.modelo to javafx.base;
}
