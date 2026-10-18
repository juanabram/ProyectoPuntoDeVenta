package application.Lista;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ListaCRUD<T> {
	
	private ObservableList<T> datos;
	
	public ListaCRUD() {
		//crea la lista
        datos = FXCollections.observableArrayList();
    }
	
	// CREATE regresa false si ya existe y sino true y lo añade
    public boolean agregar(T objeto) {
    	//pregunta si ya existe
        if(datos.contains(objeto)) {
            return false;
        }

        return datos.add(objeto);
    }
    
    // READ si existe regresa el objeto, sino te regresa un null
    public T buscar(T objeto) {
    	//busca la posición del elemento, si existe te regresa su posición, sino regresa un -1
        int posicion = datos.indexOf(objeto);
        //pregunta si la posición es -1
        if(posicion == -1) {
            return null;
        }

        return datos.get(posicion);
    }

    public ObservableList<T> obtenerTodos() {
        return datos;
    }

    // UPDATE sustituye el objecto con nuevo
    public boolean modificar(T objeto, T nuevo) {

        int posicion = datos.indexOf(objeto);

        if(posicion == -1) {
            return false;
        }

        datos.set(posicion, nuevo);

        return true;
    }

    // DELETE elimina objeto de la lista si este existe y regresa un true, sino regresa un false
    public boolean eliminar(T objeto) {

        return datos.remove(objeto);
    }
    //regresa cuantos elementos tiene la lista
    public int cantidad() {
        return datos.size();
    }

}
