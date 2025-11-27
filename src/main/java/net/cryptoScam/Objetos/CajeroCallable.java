package net.cryptoScam.Objetos;

import java.util.concurrent.Callable;

public class CajeroCallable extends Cajero implements Callable<String> {

    public CajeroCallable(cuentaCrypto cuenta, String nombre, boolean robar, double cantidad) {
        super(cuenta, nombre, robar, cantidad);
    }

    @Override
    public String call() throws Exception {
        String resultado = "";
        if (robar){
            boolean exito = false;
            exito = cuenta.retirar(cantidad, nombre);
            if (exito){
                resultado = "Se ha retirado correctamente la cantidad de " + cantidad;
            }else{
                resultado = "No se ha podido retirar la cantidad de " + cantidad;
            }
        }else{
            cuenta.depositar(cantidad, nombre);
            resultado = "Depositar de " + cantidad + " exitosa";
        }
        return resultado;
    } // Fin call
}
