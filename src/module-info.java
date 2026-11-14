module ProyectoPA_Punto_de_Venta {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;
	
	requires java.sql;
	
	exports application.Application;
	opens application.Application to javafx.graphics;
	opens application.Controlador to javafx.fxml, javafx.graphics;
	opens application.vista to javafx.fxml;
}
