package net.cryptoScam.Objetos;

import java.util.concurrent.Callable;

/**
 * Clase CajeroCallable que implementa Callable para poder devolver un resultado
 * Extiende de Cajero para reutilizar los atributos
 */
public class CajeroCallable extends Cajero implements Callable<String> {

    /**
     * Constructor del cajero callable
     * cuenta La cuenta bancaria sobre la que operará
     * nombre Nombre del cajero
     * esRetirada true para retirar dinero, false para depositar
     * cantidad Cantidad de dinero a operar
     */
    public CajeroCallable(CuentaBancaria cuenta, String nombre, boolean esRetirada, double cantidad) {
        super(cuenta, nombre, esRetirada, cantidad);
    }

    /**
     * Método call que se ejecuta cuando el hilo arranca
     * Devuelve un String con el resultado de la operación
     * Devuelve un mensaje con el resultado de la operación
     */
    @Override
    public String call() throws Exception {
        String resultado = "";

        if (esRetirada) {
            // Intentamos retirar y guardamos si tuvo éxito
            boolean exito = cuenta.retirar(cantidad, nombre);

            if (exito) {
                resultado = nombre + ": Retirada de " + cantidad + " euros EXITOSA";
            } else {
                resultado = nombre + ": Retirada de " + cantidad + " euros FALLIDA (saldo insuficiente)";
            }
        } else {
            // Depositamos dinero (siempre tiene éxito)
            cuenta.depositar(cantidad, nombre);
            resultado = nombre + ": Depósito de " + cantidad + " euros EXITOSO";
        }

        return resultado;
    }
}
