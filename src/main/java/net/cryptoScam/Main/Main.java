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

/**
 * Clase principal del programa de cajeros automáticos
 * Ejercicio de PSP sobre concurrencia con hilos
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("PROGRAMA DE CAJEROS AUTOMÁTICOS CON CONCURRENCIA");

        // Ejecutamos con Runnable (10 hilos)
        System.out.println("EJECUTAR CON RUNNABLE (10 HILOS)");
        CuentaBancaria cuentaRunnable = new CuentaBancaria(5000);
        ejecutarConRunnable(cuentaRunnable);

        // Ejecutamos con Callable (10 hilos)
        System.out.println("EJECUTAR CON CALLABLE (10 HILOS)");
        CuentaBancaria cuentaCallable = new CuentaBancaria(5000);
        ejecutarConCallable(cuentaCallable);

        System.out.println("PROGRAMA FINALIZADO");
    }

    /**
     * Método que ejecuta 10 hilos con Runnable
     * Cada cajero retira o deposita dinero según los números cargados del XML
     */
    public static void ejecutarConRunnable(CuentaBancaria cuenta) {
        // Cargamos los números del XML en una cola
        Queue<Integer> cola = cargarNumeros("Cajero");

        // Creamos un pool de 10 hilos para ejecutar los cajeros
        ExecutorService poolHilos = Executors.newFixedThreadPool(10);
        List<Runnable> cajeros = new ArrayList<>();

        System.out.println("Saldo inicial: " + cuenta.consultarSaldo() + " euros\n");

        // Creamos 10 cajeros
        for (int i = 1; i <= 10; i++) {
            Integer cantidad = cola.poll(); // Sacamos un número de la cola
            if (cantidad != null) {
                // Los pares retiran, los impares depositan
                boolean esRetirada = i % 2 == 0;
                cajeros.add(new Cajero(cuenta, "Cajero-" + i, esRetirada, cantidad));
            }
        }

        // Ejecutamos todos los cajeros en el pool de hilos
        for (Runnable cajero : cajeros) {
            poolHilos.submit(cajero);
        }

        // Cerramos el pool (no acepta más tareas)
        poolHilos.shutdown();

        try {
            // Esperamos a que todos los hilos terminen (máximo 2 minutos)
            if (poolHilos.awaitTermination(2, TimeUnit.MINUTES)) {
                System.out.println("\nTodos los hilos han terminado correctamente");
                System.out.println("================================================");
                System.out.println("  DINERO RESTANTE EN CUENTA: " + cuenta.consultarSaldo() + " euros");
                System.out.println("================================================");
            } else {
                System.out.println("Timeout: No todos los hilos terminaron a tiempo");
            }
        } catch (InterruptedException e) {
            System.err.println("Error: Hilo principal interrumpido");
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Método que ejecuta 10 hilos con Callable
     * Callable permite devolver un resultado y capturar excepciones
     */
    public static void ejecutarConCallable(CuentaBancaria cuenta) {
        // Cargamos los números del XML en una cola
        Queue<Integer> cola = cargarNumeros("Cajero");

        // Creamos un pool de 10 hilos
        ExecutorService poolHilos = Executors.newFixedThreadPool(10);
        List<Future<String>> resultados = new ArrayList<>();

        System.out.println("Saldo inicial: " + cuenta.consultarSaldo() + " euros\n");

        // Creamos 10 cajeros con Callable
        for (int i = 1; i <= 10; i++) {
            Integer cantidad = cola.poll();
            if (cantidad != null) {
                // Los pares retiran, los impares depositan
                boolean esRetirada = i % 2 == 0;
                String nombre = "Cajero-" + i;

                // Creamos el cajero callable y lo enviamos al pool
                CajeroCallable cajero = new CajeroCallable(cuenta, nombre, esRetirada, cantidad);
                // Hacemos cast a Callable para que Java sepa qué método submit usar
                Future<String> futuro = poolHilos.submit((Callable<String>) cajero);
                resultados.add(futuro);
            }
        }

        // Cerramos el pool
        poolHilos.shutdown();

        try {
            // Esperamos a que todos terminen
            if (poolHilos.awaitTermination(2, TimeUnit.MINUTES)) {
                System.out.println("\nTodos los hilos han terminado correctamente");
                System.out.println("\n================================================");
                System.out.println("  RESULTADOS DE LAS OPERACIONES:");
                System.out.println("================================================");

                // Mostramos los resultados de cada operación
                for (int i = 0; i < resultados.size(); i++) {
                    Future<String> futuro = resultados.get(i);
                    System.out.println("  [" + (i + 1) + "] " + futuro.get());
                }

                System.out.println("\n================================================");
                System.out.println("  DINERO RESTANTE EN CUENTA: " + cuenta.consultarSaldo() + " euros");
                System.out.println("================================================");

            } else {
                System.out.println("Timeout: No todos los hilos terminaron a tiempo");
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error durante la ejecución: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Método que carga los números del XML en una cola
     * Según la primera letra del nombre, carga diferentes tipos de números:
     * - A-I: Números de Fibonacci
     * - J-Q: Números primos
     * - R-Z: Números aleatorios
     * nombreCajero El nombre del cajero (para saber qué números cargar)
     * Devuelve una cola con los números cargados
     */
    public static Queue<Integer> cargarNumeros(String nombreCajero) {
        Queue<Integer> cola = new ArrayDeque<>();

        try {
            // Leemos el archivo XML
            File archivoXML = new File("src/main/resources/numeros.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document documento = builder.parse(archivoXML);

            // Normalizamos el documento
            documento.getDocumentElement().normalize();

            String etiqueta = "";
            char primeraLetra = Character.toUpperCase(nombreCajero.charAt(0));

            // Determinamos qué números cargar según la primera letra del nombre
            if (primeraLetra >= 'A' && primeraLetra <= 'I') {
                etiqueta = "fibonacci";
                System.out.println("Tu nombre empieza por '" + primeraLetra + "' (A-I): Cargando números de Fibonacci");
            } else if (primeraLetra >= 'J' && primeraLetra <= 'Q') {
                etiqueta = "primos";
                System.out.println("Tu nombre empieza por '" + primeraLetra + "' (J-Q): Cargando números primos");
            } else if (primeraLetra >= 'R' && primeraLetra <= 'Z') {
                etiqueta = "aleatorios";
                System.out.println("Tu nombre empieza por '" + primeraLetra + "' (R-Z): Cargando números aleatorios");
            } else {
                etiqueta = "fibonacci"; // Por defecto
                System.out.println("Letra no reconocida, usando Fibonacci por defecto");
            }

            // Buscamos la etiqueta en el XML
            NodeList listaNumeros = documento.getElementsByTagName(etiqueta);

            if (listaNumeros.getLength() > 0) {
                Element elemento = (Element) listaNumeros.item(0);
                String contenido = elemento.getTextContent();
                String[] numeros = contenido.split(",");

                // Añadimos los números a la cola
                for (String num : numeros) {
                    cola.offer(Integer.parseInt(num.trim()));
                }
            }

        } catch (Exception e) {
            System.err.println("Error al cargar el XML: " + e.getMessage());
            e.printStackTrace();
        }

        return cola;
    }
}
