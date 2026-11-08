package py.edu.uc.lp3exceptions;

/**
 * Base de la jerarquía de excepciones del dominio CS2.
 * Es no comprobada (RuntimeException): una regla del juego incumplida no es algo que
 * cada método intermedio pueda resolver, así que viaja hasta el handler HTTP sin
 * ensuciar las firmas con "throws", y Spring revierte la transacción por defecto.
 */
public abstract class Cs2Exception extends RuntimeException {

    protected Cs2Exception(String mensaje) {
        super(mensaje);
    }

    /** Código estable para que el cliente distinga el error sin leer el mensaje. */
    public abstract String codigo();
}
