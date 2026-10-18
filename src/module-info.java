module PuntoDeVenta {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.sql;
	exports application;
	opens application to javafx.graphics, javafx.fxml;
	/*opens application.Controlador to javafx.fxml;
	opens application.Modelo to javafx.base;*/
}
