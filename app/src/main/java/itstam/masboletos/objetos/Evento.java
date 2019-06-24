package itstam.masboletos.objetos;

import java.io.Serializable;

public class Evento implements Serializable {
    int idevento;
    String nombrevento,eventogrupo;

    public int getIdevento() {
        return idevento;
    }

    public void setIdevento(int idevento) {
        this.idevento = idevento;
    }

    public String getNombrevento() {
        return nombrevento;
    }

    public void setNombrevento(String nombrevento) {
        this.nombrevento = nombrevento;
    }

    public String getEventogrupo() {
        return eventogrupo;
    }

    public void setEventogrupo(String eventogrupo) {
        this.eventogrupo = eventogrupo;
    }
}
