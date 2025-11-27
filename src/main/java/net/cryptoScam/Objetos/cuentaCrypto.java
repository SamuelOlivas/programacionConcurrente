package net.cryptoScam.Objetos;

import java.lang.ref.SoftReference;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class cuentaCrypto {
    private double saldo;

    // Lock para gestionar el acceso al recurso delicado compartido
    private final ReentrantLock lock = new ReentrantLock();

    public cuentaCrypto(double saldoInicial) {
        this.saldo = saldoInicial;
    }

    public boolean retirar(double cantidad, String nombreCajero) {
        lock.lock();
        try {
            System.out.println("El cajero " + nombreCajero + " retira " + cantidad);
            if (saldo >= cantidad) {
                // Simular retirar dinero
                Thread.sleep(1000);
                saldo -= cantidad;
                //System.out.println("Se ha retirado " + cantidad);
                return true;
            } else {
                //System.out.println("SALDO INSUFICIENTE");
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            lock.unlock();
        }
    }// Fin Retirar

    public boolean depositar(double cantidad, String nombreCajero) {
        lock.lock();
        try {
            System.out.println("El cajero " + nombreCajero + " deposita " + cantidad);
            // Simular retirar dinero
            Thread.sleep(1000);
            saldo += cantidad;
            //System.out.println("Se ha depositado " + cantidad);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            lock.unlock();
        }
    }// Fin Depositar

    public double consultarSaldo() {
        lock.lock();
        try {
            return saldo;
        } finally {
            lock.unlock();
        }
    }// Fin consultarSaldo
}// Fin de la Clase
