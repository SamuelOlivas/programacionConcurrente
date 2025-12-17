package net.cryptoScam.Objetos;

/**
 * Clase Cajero que implementa Runnable para ejecutarse en un hilo
 * Simula un cajero automático que puede retirar o depositar dinero
 */
public class Cajero implements Runnable {
    protected final CuentaBancaria cuenta;
    protected final String nombre;
    protected final boolean esRetirada; // true si es retirada, false si es depósito
    protected final double cantidad;

    /**
     * Constructor del cajero
     * cuenta La cuenta bancaria sobre la que operará
     * nombre Nombre del cajero
     * esRetirada true para retirar dinero, false para depositar
     * cantidad Cantidad de dinero a operar
     */
    public Cajero(CuentaBancaria cuenta, String nombre, boolean esRetirada, double cantidad) {
        this.cuenta = cuenta;
        this.nombre = nombre;
        this.esRetirada = esRetirada;
        this.cantidad = cantidad;
    }

    /**
     * Método run que se ejecuta cuando el hilo arranca
     * Aquí es donde el cajero realiza su operación
     */
    @Override
    public void run() {
        if (esRetirada) {
            // Si es retirada, llamamos al método retirar
            cuenta.retirar(cantidad, nombre);
        } else {
            // Si no, depositamos
            cuenta.depositar(cantidad, nombre);
        }
    }
}
