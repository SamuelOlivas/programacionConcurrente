package net.cryptoScam.Objetos;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Clase que representa una cuenta bancaria
 * Permite retirar y depositar dinero de forma segura en entornos concurrentes
 */
public class CuentaBancaria {
    private double saldo;

    // Lock para que solo un hilo pueda acceder a la vez (evitar problemas de concurrencia)
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Constructor de la cuenta bancaria
     * saldoInicial El saldo inicial de la cuenta
     */
    public CuentaBancaria(double saldoInicial) {
        this.saldo = saldoInicial;
    }

    /**
     * Método para retirar dinero de la cuenta
     * cantidad Cantidad a retirar
     * nombreCajero Nombre del cajero que realiza la operación
     * Devuelve true si se pudo retirar, false si no hay suficiente saldo
     */
    public boolean retirar(double cantidad, String nombreCajero) {
        lock.lock(); // Bloqueamos el acceso para que solo este hilo pueda entrar
        try {
            System.out.println("El cajero " + nombreCajero + " intenta retirar " + cantidad + " euros");

            // Comprobamos si hay suficiente saldo
            if (saldo >= cantidad) {
                // Simulamos que tarda un poco en retirar el dinero
                Thread.sleep(1000);
                saldo -= cantidad;
                System.out.println("EXITO: Retirada completada. Saldo actual: " + saldo);
                return true;
            } else {
                System.out.println("ERROR: Saldo insuficiente. Saldo actual: " + saldo);
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            lock.unlock(); // Liberamos el lock para que otros hilos puedan acceder
        }
    }

    /**
     * Método para depositar dinero en la cuenta
     * cantidad Cantidad a depositar
     * nombreCajero Nombre del cajero que realiza la operación
     * Devuelve true si se pudo depositar correctamente
     */
    public boolean depositar(double cantidad, String nombreCajero) {
        lock.lock(); // Bloqueamos el acceso
        try {
            System.out.println("El cajero " + nombreCajero + " deposita " + cantidad + " euros");

            // Simulamos que tarda un poco en depositar el dinero
            Thread.sleep(1000);
            saldo += cantidad;
            System.out.println("EXITO: Deposito completado. Saldo actual: " + saldo);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            lock.unlock(); // Liberamos el lock
        }
    }

    /**
     * Método para consultar el saldo de la cuenta
     * Devuelve el saldo actual
     */
    public double consultarSaldo() {
        lock.lock();
        try {
            return saldo;
        } finally {
            lock.unlock();
        }
    }
}
