package net.cryptoScam.Main;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import net.cryptoScam.Objetos.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== PROGRAMA DE CAJEROS AUTOMÁTICOS CON CONCURRENCIA ===\n");

        // Demostración con Thread sin ExecutorService
        System.out.println("--- DEMOSTRACIÓN CON Thread SIN ExecutorService ---");
        demostracionConThread();

        System.out.println("\n--- EJECUTAR CON RUNNABLE (10 HILOS) ---");
        cuentaCrypto cuentaRunnable = new cuentaCrypto(5000);
        ejecutarConRunnable(cuentaRunnable);

        System.out.println("\n--- EJECUTAR CON CALLABLE Y CompletableFuture (10 HILOS) ---");
        cuentaCrypto cuentaCallable = new cuentaCrypto(5000);
        ejecutarConCallableYCompletable(cuentaCallable);
    }// Fin del Main

    /* Demostración con Thread sin ExecutorService */
    private static void demostracionConThread() {
        cuentaCrypto cuentaThread = new cuentaCrypto(1000);
        Thread[] hilos = new Thread[3];

        // Crear hilos manualmente
        hilos[0] = new Thread(() -> cuentaThread.retirar(100, "ThreadA"));
        hilos[1] = new Thread(() -> cuentaThread.depositar(200, "ThreadB"));
        hilos[2] = new Thread(() -> cuentaThread.retirar(50, "ThreadC"));

        // Ejecutar hilos
        for (Thread hilo : hilos) {
            hilo.start();
        }

        // Esperar a que terminen
        try {
            for (Thread hilo : hilos) {
                hilo.join();
            }
            System.out.println("Saldo final con Thread: " + cuentaThread.consultarSaldo());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /*
     * Este método ejecuta 10 hilos con Runnable
     */
    public static void ejecutarConRunnable(cuentaCrypto cuenta) {
        Queue<Integer> cola = cargarNumeros("Cajero");
        ExecutorService poolHilos = Executors.newFixedThreadPool(10);
        List<Runnable> cajeros = new ArrayList<>();

        // Crear 10 cajeros
        for (int i = 1; i <= 10; i++) {
            Integer cantidad = cola.poll();
            if (cantidad != null) {
                boolean robar = i % 2 == 0; // Alternamos entre retirar y depositar
                cajeros.add(new Cajero(cuenta, "Cajero-" + i, robar, cantidad));
            }
        }

        // Ejecutar todos los cajeros
        for (Runnable cajero : cajeros) {
            poolHilos.submit(cajero);
        }

        poolHilos.shutdown();

        try {
            if (poolHilos.awaitTermination(2, TimeUnit.MINUTES)) {
                System.out.println("Todos los hilos han terminado");
                System.out.println("Dinero restante en cuenta: " + cuenta.consultarSaldo());
            } else {
                System.out.println("No todos los hilos terminaron en el tiempo límite");
            }
        } catch (InterruptedException e) {
            System.err.println("Hilo principal interrumpido");
            Thread.currentThread().interrupt();
        }
    }

    /*
     * Este método ejecuta 10 hilos con Callable usando CompletableFuture
     */
    public static void ejecutarConCallableYCompletable(cuentaCrypto cuenta) {
        Queue<Integer> cola = cargarNumeros("Cajero");
        ExecutorService poolHilos = Executors.newFixedThreadPool(10);
        List<CompletableFuture<String>> resultados = new ArrayList<>();

        // Crear 10 cajeros con Callable
        for (int i = 1; i <= 10; i++) {
            Integer cantidad = cola.poll();
            if (cantidad != null) {
                boolean robar = i % 2 == 0;
                String nombre = "Cajero-" + i;

                // Convertir Callable a CompletableFuture
                CompletableFuture<String> futuro = CompletableFuture.supplyAsync(() -> {
                    try {
                        return new CajeroCallable(cuenta, nombre, robar, cantidad).call();
                    } catch (Exception e) {
                        return "Error en " + nombre + ": " + e.getMessage();
                    }
                }, poolHilos);

                resultados.add(futuro);
            }
        }

        // Combinar todos los CompletableFuture
        CompletableFuture<Void> todosCombinados = CompletableFuture.allOf(
                resultados.toArray(new CompletableFuture[0]));

        try {
            // Esperar a que todos terminen (con timeout de 2 minutos)
            todosCombinados.orTimeout(2, TimeUnit.MINUTES).join();

            System.out.println("Todos los hilos han terminado");
            System.out.println("\nResultados de las operaciones:");
            for (int i = 0; i < resultados.size(); i++) {
                CompletableFuture<String> futuro = resultados.get(i);
                System.out.println("  [" + (i + 1) + "] " + futuro.getNow("Pendiente"));
            }
            System.out.println("\nDinero restante en cuenta: " + cuenta.consultarSaldo());

        } catch (CompletionException e) {
            System.out.println("Error durante la ejecución: " + e.getMessage());
        } finally {
            poolHilos.shutdown();
        }
    }

    /* Método que carga números según la primera letra del nombre del cajero */
    public static Queue<Integer> cargarNumeros(String nombreCajero) {
        Queue<Integer> cola = new ArrayDeque<>();

        try {
            File archivoXML = new File("src/main/resources/numeros.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document documento = builder.parse(archivoXML);

            documento.getDocumentElement().normalize();

            String etiqueta = "";
            char primeraLetra = Character.toUpperCase(nombreCajero.charAt(0));

            // Determinar qué números cargar según la primera letra
            if (primeraLetra >= 'A' && primeraLetra <= 'I') {
                etiqueta = "fibonacci";
            } else if (primeraLetra >= 'J' && primeraLetra <= 'Q') {
                etiqueta = "primos";
            } else if (primeraLetra >= 'R' && primeraLetra <= 'Z') {
                etiqueta = "aleatorios";
            } else {
                etiqueta = "fibonacci"; // Default
            }

            NodeList listaNumeros = documento.getElementsByTagName(etiqueta);

            if (listaNumeros.getLength() > 0) {
                Element elemento = (Element) listaNumeros.item(0);
                String contenido = elemento.getTextContent();
                String[] numeros = contenido.split(",");

                for (String num : numeros) {
                    cola.offer(Integer.parseInt(num.trim()));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cola;
    }// Fin cargarNumeros
}
