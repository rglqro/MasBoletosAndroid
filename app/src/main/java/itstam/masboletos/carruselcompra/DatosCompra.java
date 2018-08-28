package itstam.masboletos.carruselcompra;

import java.io.Serializable;

public class DatosCompra implements Serializable {
    private String CantBoletos;
    private String Funcion;
    private String Zona;
    private String Seccion, idEvento;

    public String getCantBoletos() {
        return CantBoletos;
    }

    public void setCantBoletos(String cantBoletos) {
        CantBoletos = cantBoletos;
    }

    public String getFuncion() {
        return Funcion;
    }

    public void setFuncion(String funcion) {
        Funcion = funcion;
    }

    public String getZona() {
        return Zona;
    }

    public void setZona(String zona) {
        Zona = zona;
    }

    public String getSeccion() {
        return Seccion;
    }

    public void setSeccion(String seccion) {
        Seccion = seccion;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }
}
