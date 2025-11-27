package net.cryptoScam.Objetos;

public class Cajero implements Runnable{
    protected final cuentaCrypto cuenta;
    protected final String nombre;
    protected final boolean robar;
    protected final double cantidad;

    public Cajero(cuentaCrypto cuenta, String nombre, boolean robar, double cantidad){
        this.cuenta = cuenta;
        this.nombre = nombre;
        this.robar = robar;
        this.cantidad = cantidad;
    }

    @Override
    public void run() {
        if (robar){
            cuenta.retirar(cantidad, nombre);
        }else{
            cuenta.depositar(cantidad, nombre);
        }
    }// Fin del run
}
