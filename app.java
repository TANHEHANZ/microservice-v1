package com.climapositivo.clima;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.URLEncoder;
import android.database.SQLException;
import android.util.Base64;
import java.io.InputStream;
import android.content.res.AssetManager;
import org.brotli.dec.BrotliInputStream;
import java.io.ByteArrayInputStream;

public class VotoManager {
    private static final String TAG = "VotoManager";
    private static final String API_URL = "https://bunkerappvotacion-g4fdeed5e8d7h7es.canadacentral-01.azurewebsites.net/enviar_voto";


    private static final String TOKEN_URL = "https://pasantia-pi.vercel.app/api/token";
    private static final String URL_REGISTRO = "https://dashboard-gray-zeta-29.vercel.app/api/register";

    private static final int MAX_VOTOS_POR_IP = 1;
    private static final long INTERVALO_VERIFICACION_IP = 30 * 1000; // 30 segundos
    private static final long INTERVALO_REINTENTO = 5 * 60 * 1000; // 5 minutos

    private static final Map<String, CiudadBolivia> CIUDADES_BOLIVIA = new HashMap<String, CiudadBolivia>() {{
        put("La Paz", new CiudadBolivia("La Paz", -16.4897, -68.1193, "La Paz"));
        put("El Alto", new CiudadBolivia("El Alto", -16.5056, -68.1192, "La Paz"));
        put("Cochabamba", new CiudadBolivia("Cochabamba", -17.3895, -66.1568, "Cochabamba"));
        put("Quillacollo", new CiudadBolivia("Quillacollo", -17.3947, -66.2758, "Cochabamba"));
        put("Santa Cruz", new CiudadBolivia("Santa Cruz", -17.7833, -63.1667, "Santa Cruz"));
        put("Montero", new CiudadBolivia("Montero", -17.3364, -63.2533, "Santa Cruz"));
        put("Oruro", new CiudadBolivia("Oruro", -17.9833, -67.1500, "Oruro"));
        put("Potosí", new CiudadBolivia("Potosí", -19.5833, -65.7500, "Potosí"));
        put("Sucre", new CiudadBolivia("Sucre", -19.0429, -65.2592, "Chuquisaca"));
        put("Trinidad", new CiudadBolivia("Trinidad", -14.8167, -64.9000, "Beni"));
        put("Cobija", new CiudadBolivia("Cobija", -11.0333, -68.7500, "Pando"));
        put("Tarija", new CiudadBolivia("Tarija", -21.5359, -64.7292, "Tarija"));
    }};

    private static class CiudadBolivia {
        String nombre;
        double latitud;
        double longitud;
        String departamento;

        CiudadBolivia(String nombre, double latitud, double longitud, String departamento) {
            this.nombre = nombre;
            this.latitud = latitud;
            this.longitud = longitud;
            this.departamento = departamento;
        }
    }

    private DatabaseHelper dbHelper;
    private Context context;
    private List<String> ipUtilizadas = new ArrayList<>();
    private String ultimaIPConocida = "";
    private ExecutorService executorService;
    private Handler mainHandler;
    private boolean verificacionEnCurso = false;

    private int totalEnvios = 0;
    private int enviosExitosos = 0;
    private int enviosConError = 0;

    private String ciudadSeleccionada = "Aleatorio"; 

    private MainActivity mainActivity;


    public static final String ACTION_RESPUESTA_SERVIDOR = "com.tecnologiasdavenport.clima.RESPUESTA_SERVIDOR";

    // Interfaz para obtener número de WhatsApp
    public interface NumeroWhatsappListener {
        String obtenerNumeroWhatsapp();
    }

    // Variable para almacenar el listener
    private NumeroWhatsappListener numeroWhatsappListener;

    // Token por defecto
    private static final String TOKEN_POR_DEFECTO = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";




    // Constantes para nombres de tablas
    private static final String TABLE_NAME_VOTOS = "votos";
    private static final String TABLE_NAME_CONFIGURACION = "configuracion";
    private static final String TABLE_NAME_TOKENS = "tokens";

    // Nueva tabla de tokens con tipos de datos explícitos y validación
    private static final String TABLE_TOKENS = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_TOKENS + " (" +
        "id TEXT PRIMARY KEY, " +
        "token TEXT NOT NULL, " +
        "estado INTEGER DEFAULT 1 CHECK(estado IN (0, 1)), " +
        "numero TEXT, " +  // Nuevo campo para número de teléfono
        "created_at TEXT NOT NULL)";

    // Tabla de votos con verificaciones adicionales
    private static final String TABLE_VOTOS = "CREATE TABLE IF NOT EXISTS votos (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "ip TEXT, " +
        "numero TEXT, " +
        "latitud TEXT, " +
        "longitud TEXT, " +
        "ci TEXT, " +
        "dia_nacimiento TEXT, " +
        "mes_nacimiento TEXT, " +
        "anio_nacimiento TEXT, " +
        "pais TEXT, " +
        "departamento TEXT, " +
        "provincia TEXT, " +
        "municipio TEXT, " +
        "recinto TEXT, " +
        "genero TEXT, " +
        "pregunta1 TEXT, " +
        "pregunta2 TEXT, " +
        "pregunta3 TEXT, " +
        "candidato TEXT, " +
        "timestamp INTEGER DEFAULT (strftime('%s', 'now')), " +
        "codigo_respuesta INTEGER, " +
        "respuesta_servidor TEXT, " +
        "estado TEXT, " +
        "id_dispositivo TEXT)";

    private static final String TABLE_CONFIGURACION = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_CONFIGURACION + " (" +
        "clave TEXT PRIMARY KEY, " +
        "valor TEXT NOT NULL)";

    public VotoManager(Context context) {
        this.context = context;
        // Si el contexto es una MainActivity, guardar referencia
        if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
        }
        dbHelper = new DatabaseHelper(context);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        iniciarVerificacionContinuaIP();
    }

    private void iniciarVerificacionContinuaIP() {
        executorService.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                verificarCambioIP();
                try {
                    Thread.sleep(INTERVALO_VERIFICACION_IP);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private void verificarCambioIP() {
        if (verificacionEnCurso) return;

        verificacionEnCurso = true;
        try {
            String ipActual = obtenerIPPublica();

            if (ipActual == null || ipActual.equals("IP_NO_DISPONIBLE")) {
                Log.w(TAG, "No se pudo obtener la IP pública");
                return;
            }

            if (!ipActual.equals(ultimaIPConocida)) {
                Log.i(TAG, "Cambio de IP detectado: " + ultimaIPConocida + " -> " + ipActual);

                // Reiniciar contadores para la nueva IP
                reiniciarContadoresIP(ipActual);

                ultimaIPConocida = ipActual;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en verificación de IP", e);
        } finally {
            verificacionEnCurso = false;
        }
    }

    private void reiniciarContadoresIP(String nuevaIP) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            // Eliminar registros de votos para la IP anterior
            db.delete("votos", "timestamp < ?",
                    new String[]{String.valueOf(System.currentTimeMillis() - 24 * 60 * 60 * 1000)});
        } catch (Exception e) {
            Log.e(TAG, "Error al limpiar registros antiguos", e);
        } finally {
            db.close();
        }
    }

    public JSONObject enviarVoto() {
        try {
            // Obtener IP pública actual
            String ipActual = obtenerIPPublica();

            // Verificar si la IP es válida
            if (ipActual == null || ipActual.equals("IP_NO_DISPONIBLE")) {
                return crearRespuestaError("IP no válida");
            }

            // Verificar si la IP ya ha sido utilizada
            if (ipUtilizadas.contains(ipActual)) {
                return crearRespuestaError("IP ya utilizada");
            }

            // Verificar límite de votos
            if (!puedeEnviarVoto(ipActual)) {
                return crearRespuestaError("Límite de votos alcanzado");
            }

            // Generar datos de voto
            JSONObject datos = generarDatosVoto();

            // Enviar voto
            String respuestaServidor = enviarVotoAServidor(datos);

            // Registrar envío
            //registrarEnvio(datos, respuestaServidor, ipActual, 200);

            // Solo agregar IP a la lista si el envío fue exitoso
            ipUtilizadas.add(ipActual);

            return crearRespuestaExitosa(datos, ipActual);

        } catch (Exception e) {
            Log.e(TAG, "Error al enviar voto", e);
            return crearRespuestaError(e.getMessage());
        }
    }

    private boolean puedeEnviarVoto(String ipActual) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            // Contar votos para esta IP en las últimas 24 horas
            String[] projection = {"COUNT(*) as total_votos"};
            String selection = "ip = ? AND timestamp > ?";
            String[] selectionArgs = {
                    ipActual,
                    String.valueOf(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
            };

            Cursor cursor = db.query("votos", projection, selection, selectionArgs, null, null, null);

            int totalVotos = 0;
            if (cursor != null && cursor.moveToFirst()) {
                totalVotos = cursor.getInt(0);
                cursor.close();
            }

            return totalVotos < MAX_VOTOS_POR_IP;
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar límite de votos", e);
            return false;
        } finally {
            db.close();
        }
    }

    public String obtenerIPPublica() {
        // Lista de servicios de IP pública
        String[] serviciosIP = {
            "https://api.ipify.org",
            "https://ipinfo.io/ip",
            "https://checkip.amazonaws.com"
        };

        for (String servicio : serviciosIP) {
            try {
                // Configurar conexión con timeout
                URL url = new URL(servicio);
                HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                conexion.setConnectTimeout(5000);  // 5 segundos de timeout de conexión
                conexion.setReadTimeout(5000);     // 5 segundos de timeout de lectura
                conexion.setRequestMethod("GET");

                // Leer respuesta
                BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String ip = lector.readLine().trim();
                lector.close();

                // Validar formato de IP
                if (validarFormatoIP(ip)) {
                    Log.d(TAG, "IP pública obtenida de " + servicio + ": " + ip);
                    return ip;
                }
            } catch (Exception e) {
                // Log del error específico
                Log.w(TAG, "Error obteniendo IP de " + servicio + ": " + e.getMessage());
            }
        }

        // Si todos los servicios fallan, devolver IP por defecto
        Log.e(TAG, "No se pudo obtener la IP pública");
        return "IP_NO_DISPONIBLE";
    }

    // Método para validar formato de IP
    private boolean validarFormatoIP(String ip) {
        // Expresión regular para validar IPv4
        String patronIP = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return ip != null && !ip.isEmpty() && ip.matches(patronIP);
    }

    // Método para seleccionar elemento aleatorio con filtro opcional
    private JSONObject seleccionarElementoAleatorio(JSONArray datos, String filtro, String tipoFiltro) {
        JSONArray datosFiltrados = new JSONArray();

        // Filtrar datos si se proporciona un filtro
        if (filtro != null && !filtro.equals("Aleatorio")) {
            for (int i = 0; i < datos.length(); i++) {
                try {
                    JSONObject item = datos.getJSONObject(i);
                    String valorFiltro = "";

                    // Seleccionar el campo de filtro correcto
                    switch (tipoFiltro) {
                        case "departamento":
                            valorFiltro = item.getString("nombre_departamento").trim();
                            break;
                        case "provincia":
                            valorFiltro = item.getString("nombre_provincia").trim();
                            break;
                        case "municipio":
                            valorFiltro = item.getString("nombre_municipio").trim();
                            break;
                    }

                    // Comparar con el filtro
                    if (valorFiltro.equalsIgnoreCase(filtro)) {
                        datosFiltrados.put(item);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error al filtrar datos JSON", e);
                }
            }
        }

        // Si no hay datos filtrados, usar todos los datos
        if (datosFiltrados.length() == 0) {
            datosFiltrados = datos;
        }

        // Seleccionar elemento aleatorio
        Random random = new Random();
        int indiceAleatorio = random.nextInt(datosFiltrados.length());
        try {
            return datosFiltrados.getJSONObject(indiceAleatorio);
        } catch (JSONException e) {
            Log.e(TAG, "Error al seleccionar elemento aleatorio", e);
            return null;
        }
    }

    private JSONObject generarDatosVoto() throws JSONException {
        Random random = new Random();

        // Cargar datos desde JSON
        JSONArray datosJSON = cargarDatosJSON();

        // Seleccionar ciudad
        String nombreCiudad;
        CiudadBolivia ciudad;

        // Obtener la ciudad
        String ciudadSeleccionadaSpinner = obtenerCiudadDesdeSpinner();
        Log.d(TAG, "Ciudad seleccionada para generar datos: " + ciudadSeleccionadaSpinner);

        // Si es "Aleatorio", seleccionar una ciudad al azar
        if (ciudadSeleccionadaSpinner.equals("Aleatorio")) {
            // Selección aleatoria
            String[] ciudades = CIUDADES_BOLIVIA.keySet().toArray(new String[0]);
            nombreCiudad = ciudades[random.nextInt(ciudades.length)];
            Log.d(TAG, "Selección aleatoria: " + nombreCiudad);
        } else {
            // Usar la ciudad seleccionada
            nombreCiudad = ciudadSeleccionadaSpinner;
            Log.d(TAG, "Ciudad específica seleccionada: " + nombreCiudad);
        }

        // Obtener la ciudad del mapa
        ciudad = CIUDADES_BOLIVIA.get(nombreCiudad);

        // Generar ubicación con variación pequeña alrededor de las coordenadas de la ciudad
        double latitudBase = generarCoordenadaAleatoria(ciudad.latitud, 0.001);
        double longitudBase = generarCoordenadaAleatoria(ciudad.longitud, 0.001);

        // Seleccionar elemento aleatorio del JSON
        JSONObject elementoAleatorio = seleccionarElementoAleatorio(datosJSON, null, null);

        // Generar número de WhatsApp
        String numero = generarNumeroWhatsapp();

        // Generar número de cédula sin que comience con 0
        String ci = generarNumeroCedula();

        // Generar datos personales
        int diaNacimiento = random.nextInt(28) + 1;
        int mesNacimiento = random.nextInt(12) + 1;
        // Cambiar la generación del año para que sea entre 1980 y 2005
        int anioBisiesto = random.nextInt(2005 - 1980 + 1) + 1980;

        // Procesar datos del elemento aleatorio
        String departamento, provincia, municipio, recinto;
        double latitud, longitud;
        if (elementoAleatorio != null) {
            departamento = elementoAleatorio.getString("nombre_departamento").trim();
            provincia = elementoAleatorio.getString("nombre_provincia").trim();
            municipio = elementoAleatorio.getString("nombre_municipio").trim();
            recinto = elementoAleatorio.getString("nombre_recinto").trim();

            // Obtener latitud y longitud, modificando los últimos dos dígitos
            latitud = Double.parseDouble(elementoAleatorio.getString("latitud"));
            longitud = Double.parseDouble(elementoAleatorio.getString("longitud"));

            // Modificar últimos dos dígitos de latitud y longitud
            latitud += random.nextDouble() * 0.01 - 0.005;
            longitud += random.nextDouble() * 0.01 - 0.005;
        } else {
            // Valores aleatorios de respaldo
            String[] departamentos = {"La Paz", "Cochabamba", "Santa Cruz", "Oruro", "Potosí", "Sucre", "Tarija", "Beni", "Pando"};
            String[] provincias = {"Murillo", "Cercado", "Andrés Ibáñez", "Nor Chichas", "Tomás Frías", "Oropeza", "Cercado"};
            String[] municipios = {"La Paz", "Cochabamba", "Santa Cruz", "Oruro", "Potosí", "Sucre", "Tarija", "Trinidad", "Cobija"};
            String[] recintos = {"Escuela", "Colegio", "Universidad", "Centro Comunal", "Parroquia", "Sindicato"};

            departamento = departamentos[random.nextInt(departamentos.length)];
            provincia = provincias[random.nextInt(provincias.length)];
            municipio = municipios[random.nextInt(municipios.length)];
            recinto = recintos[random.nextInt(recintos.length)];

            // Usar coordenadas base
            latitud = latitudBase;
            longitud = longitudBase;
        }

        // Generar preguntas aleatorias (Sí/No)
        String pregunta1 = "Sí";
        String pregunta2 = "Sí";
        String pregunta3 = "No";

        // Generar género (mantener lógica original)
        String[] generos = {"Hombre", "Mujer"};
        String genero = generos[random.nextInt(generos.length)];

        // Agregar nuevos campos al JSON de datos
        JSONObject datos = new JSONObject();
        datos.put("numero", numero);
        datos.put("latitud", String.format("%.4f", latitud));
        datos.put("longitud", String.format("%.4f", longitud));
        datos.put("ci", ci);
        datos.put("dia_nacimiento", String.format("%02d", diaNacimiento));
        datos.put("mes_nacimiento", String.format("%02d", mesNacimiento));
        datos.put("anio_nacimiento", String.valueOf(anioBisiesto));
        datos.put("pais", "Bolivia");
        datos.put("departamento", departamento);
        datos.put("candidato", "Manfred Reyes Villa");
        datos.put("genero", genero);
        datos.put("provincia", provincia);
        datos.put("municipio", municipio);
        datos.put("recinto", recinto);
        datos.put("pregunta1", pregunta1);
        datos.put("pregunta2", pregunta2);
        datos.put("pregunta3", pregunta3);

        // Log detallado de los datos generados
        Log.d(TAG,"Datos:"+datos);
        Log.d(TAG, "===== GENERACIÓN DE DATOS DE VOTO =====");
        Log.d(TAG, "Número WhatsApp: " + numero);
        Log.d(TAG, "CI: " + ci);
        Log.d(TAG, "Fecha Nacimiento: " + diaNacimiento + "/" + mesNacimiento + "/" + anioBisiesto);
        //Log.d(TAG, "Ciudad final: " + nombreCiudad);
        Log.d(TAG, "Coordenadas generadas: " + latitud + ", " + longitud);
        //Log.d(TAG, "Departamento: " + ciudad.departamento);
        Log.d(TAG, "Genero: " + genero);
        Log.d(TAG, "Departamento: " + departamento);
        Log.d(TAG, "Provincia: " + provincia);
        Log.d(TAG, "Municipio: " + municipio);
        Log.d(TAG, "Recinto: " + recinto);
        Log.d(TAG, "Pregunta 1: " + pregunta1);
        Log.d(TAG, "Pregunta 2: " + pregunta2);
        Log.d(TAG, "Pregunta 3: " + pregunta3);
        Log.d(TAG, "===== FIN GENERACIÓN DE DATOS =====");

        return datos;
    }

    private double generarCoordenadaAleatoria(double coordenada, double variacion) {
        Random random = new Random();
        return coordenada + (random.nextDouble() * 2 * variacion - variacion);
    }

    private String generarNumeroWhatsapp() {
        // Recuperar configuración de WhatsApp personalizado
        WhatsappConfiguracion configuracion = recuperarConfiguracionWhatsapp();

        // Log detallado de la configuración
        Log.d(TAG, "===== GENERACIÓN DE NÚMERO DE WHATSAPP =====");
        Log.d(TAG, "Configuración de WhatsApp - Habilitado: " + configuracion.habilitado);
        Log.d(TAG, "Número personalizado: " + (configuracion.numero != null ? configuracion.numero : "N/A"));

        // Si está habilitado
        if (configuracion.habilitado) {
            // Si hay número personalizado, usarlo
            if (configuracion.numero != null && !configuracion.numero.trim().isEmpty()) {
                Log.d(TAG, "Usando número de WhatsApp personalizado: " + configuracion.numero);
                return configuracion.numero.trim();
            }

            // Si no hay número personalizado, usar +000000000
            Log.d(TAG, "Número personalizado habilitado pero vacío. Usando +000000000");
            return "+000000000";
        }

        // Intentar obtener número de tokens disponibles
        String numeroToken = obtenerNumeroDeToken();

        // Si se obtiene un número de token, usarlo
        if (numeroToken != null) {
            Log.d(TAG, "Usando número de token: " + numeroToken);
            return numeroToken;
        }

        // Si no hay número de token, generar número aleatorio
        Random random = new Random();
        String prefijo = "+591";
        String segundoDigito = random.nextBoolean() ? "6" : "7";
        StringBuilder digitosAleatorios = new StringBuilder();

        for (int i = 0; i < 7; i++) {
            digitosAleatorios.append(random.nextInt(10));
        }

        String numeroGenerado = prefijo + segundoDigito + digitosAleatorios.toString();
        Log.d(TAG, "Generando número aleatorio: " + numeroGenerado);
        Log.d(TAG, "===== FIN GENERACIÓN DE NÚMERO DE WHATSAPP =====");

        return numeroGenerado;
    }

    // Método para obtener número de token disponible
    private String obtenerNumeroDeToken() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String numeroToken = null;

        try {
            db = dbHelper.getWritableDatabase();

            // Buscar token disponible con número
            cursor = db.query(TABLE_NAME_TOKENS,
                new String[]{"numero", "token"},
                "estado = 1 AND numero IS NOT NULL AND numero != ''",
                null, null, null, "created_at ASC", "1");

            if (cursor != null && cursor.moveToFirst()) {
                numeroToken = cursor.getString(0);
                String token = cursor.getString(1);

                // Marcar token como usado
                ContentValues valores = new ContentValues();
                valores.put("estado", 0);
                int filasActualizadas = db.update(TABLE_NAME_TOKENS,
                    valores,
                    "token = ?",
                    new String[]{token});

                Log.d(TAG, "Número de token usado: " + numeroToken +
                      ", Filas actualizadas: " + filasActualizadas);
            } else {
                Log.d(TAG, "No se encontraron tokens con número disponible");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener número de token", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return numeroToken;
    }

    private String generarNumeroCedula() {
        Random random = new Random();

        // Generar el primer dígito entre 1 y 9 para evitar que comience con 0
        int primerDigito = random.nextInt(9) + 1;

        // Generar los 6 dígitos restantes
        StringBuilder ci = new StringBuilder(String.valueOf(primerDigito));
        for (int i = 0; i < 6; i++) {
            ci.append(random.nextInt(10));
        }

        return ci.toString();
        // Devolver cadena vacía
        //return "";
    }

    private String obtenerToken() {
        try {
            // Primero verificar si hay tokens disponibles
            if (necesitaSolicitarTokens()) {
                // Solicitar nuevos tokens
                String[] urlTokens = {
                    TOKEN_URL
                };

                for (String tokenUrl : urlTokens) {
                    try {
                        URL url = new URL(tokenUrl);
                        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                        conexion.setRequestMethod("GET");
                        conexion.setConnectTimeout(10000);
                        conexion.setReadTimeout(10000);

                        // Log de detalles de solicitud de token
                        Log.d(TAG, "===== SOLICITUD DE TOKEN =====");
                        Log.d(TAG, "URL de token: " + tokenUrl);
                        Log.d(TAG, "Método: GET");

                        int codigoRespuesta = conexion.getResponseCode();
                        Log.d(TAG, "Código de respuesta de token: " + codigoRespuesta);

                        // Determinar stream de entrada correcto
                        InputStream inputStream = (codigoRespuesta >= 200 && codigoRespuesta < 300)
                            ? conexion.getInputStream()
                            : conexion.getErrorStream();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder respuesta = new StringBuilder();
                        String linea;
                        while ((linea = reader.readLine()) != null) {
                            respuesta.append(linea);
                        }
                        reader.close();

                        // Log de la respuesta de token
                        Log.d(TAG, "Respuesta de token: " + respuesta.toString());

                        JSONObject jsonRespuesta = new JSONObject(respuesta.toString());

                        // Log de contenido JSON de token
                        Log.d(TAG, "Contenido JSON de token:");
                        Log.d(TAG, "msg: " + jsonRespuesta.optString("msg"));

                        // Log de los datos de tokens
                        if (jsonRespuesta.has("data")) {
                            JSONArray tokensArray = jsonRespuesta.getJSONArray("data");
                            Log.d(TAG, "data: " + tokensArray.toString());

                            // Guardar tokens en SQLite
                            guardarTokens(tokensArray);
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Error al obtener tokens de " + tokenUrl, e);
                    }
                }
            }

            // Obtener token disponible de SQLite
            String tokenDisponible = obtenerTokenDisponible();

            // Si no hay tokens, usar token por defecto
            return tokenDisponible != null ? tokenDisponible : TOKEN_POR_DEFECTO;

        } catch (Exception e) {
            Log.e(TAG, "Excepción inesperada al obtener token", e);
            return TOKEN_POR_DEFECTO;
        }
    }

    private boolean necesitaSolicitarTokens() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        boolean necesitaTokens = true;

        try {
            db = dbHelper.getReadableDatabase();

            // Verificar primero si la tabla existe
            cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{TABLE_NAME_TOKENS});

            if (cursor != null && cursor.moveToFirst()) {
                // Tabla existe, contar tokens disponibles
                cursor = db.query(TABLE_NAME_TOKENS,
                    new String[]{"COUNT(*)"},
                    "estado = 1",
                    null, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int tokenesDisponibles = cursor.getInt(0);
                    necesitaTokens = tokenesDisponibles <= 1;
                    Log.d(TAG, "Tokens disponibles: " + tokenesDisponibles +
                          ", Necesita solicitar: " + necesitaTokens);
                }
            } else {
                Log.e(TAG, "Tabla de tokens no existe");
                necesitaTokens = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar tokens", e);
            necesitaTokens = true;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return necesitaTokens;
    }

    private String obtenerTokenDisponible() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String tokenDisponible = null;

        try {
            db = dbHelper.getWritableDatabase();

            // Verificar primero si la tabla existe
            cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{TABLE_NAME_TOKENS});

            if (cursor != null && cursor.moveToFirst()) {
                // Cerrar cursor de verificación
                cursor.close();

                // Buscar token disponible
                cursor = db.query(TABLE_NAME_TOKENS,
                    new String[]{"token"},
                    "estado = 1",
                    null, null, null, "created_at ASC", "1");

                if (cursor != null && cursor.moveToFirst()) {
                    tokenDisponible = cursor.getString(0);

                    // Marcar token como usado (cambiando estado a 0)
                    ContentValues valores = new ContentValues();
                    valores.put("estado", 0);
                    int filasActualizadas = db.update(TABLE_NAME_TOKENS,
                        valores,
                        "token = ?",
                        new String[]{tokenDisponible});

                    Log.d(TAG, "Token usado: " + tokenDisponible +
                          ", Filas actualizadas: " + filasActualizadas);
                } else {
                    Log.d(TAG, "No se encontraron tokens disponibles");
                }
            } else {
                Log.e(TAG, "Tabla de tokens no existe");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener token disponible", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return tokenDisponible;
    }

    private void guardarTokens(JSONArray tokensArray) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();

            // Iniciar transacción para mayor eficiencia
            db.beginTransaction();

            for (int i = 0; i < tokensArray.length(); i++) {
                JSONObject tokenObj = tokensArray.getJSONObject(i);

                ContentValues valores = new ContentValues();
                valores.put("id", tokenObj.getString("id"));
                valores.put("token", tokenObj.getString("token"));

                // Convertir booleano a entero (1 para true, 0 para false)
                // Si no hay estado explícito, asumir que está disponible
                int estado = 1;
                if (tokenObj.has("estado")) {
                    estado = tokenObj.getBoolean("estado") ? 1 : 0;
                }
                valores.put("estado", estado);

                // Agregar número de teléfono si está presente
                if (tokenObj.has("numero")) {
                    valores.put("numero", tokenObj.getString("numero"));
                }

                // Usar timestamp actual como created_at si no se proporciona
                String createdAt = tokenObj.has("createdAt")
                    ? tokenObj.getString("createdAt")
                    : String.valueOf(System.currentTimeMillis());
                valores.put("created_at", createdAt);

                // Insertar o reemplazar
                long resultado = db.insertWithOnConflict(TABLE_NAME_TOKENS,
                    null,
                    valores,
                    SQLiteDatabase.CONFLICT_REPLACE);

                // Log del resultado de la inserción
                Log.d(TAG, "Token guardado. ID: " + tokenObj.getString("id") +
                      ", Resultado: " + (resultado != -1 ? "Éxito" : "Error") +
                      ", Número: " + (tokenObj.has("numero") ? tokenObj.getString("numero") : "N/A"));
            }

            db.setTransactionSuccessful();
            Log.d(TAG, "Tokens guardados exitosamente: " + tokensArray.length());
        } catch (Exception e) {
            Log.e(TAG, "Error al guardar tokens", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    private String enviarVotoAServidor(JSONObject datos) throws Exception {
        try {
            // Obtener IP actual y ID de dispositivo
            String ipActual = obtenerIPPublica();
            String deviceId = recuperarIdDispositivo();

        // Registro local antes de enviar a la API
        registrarEnvioLocal(datos, ipActual);

        // Log detallado de pre-envío
        Log.d(TAG, "===== PRE-ENVÍO DE VOTO =====");
        Log.d(TAG, "IP Actual: " + ipActual);
        Log.d(TAG, "ID Dispositivo: " + deviceId);
        Log.d(TAG, "Datos de voto a enviar:");
        String[] camposPre = {
            "numero", "latitud", "longitud", "ci",
            "dia_nacimiento", "mes_nacimiento", "anio_nacimiento",
            "pais", "departamento", "candidato",
            "genero", "provincia", "municipio", "recinto",
            "pregunta1", "pregunta2", "pregunta3"
        };
        for (String campo : camposPre) {
            Log.d(TAG, campo + ": " + datos.optString(campo));
        }
        Log.d(TAG, "===== FIN PRE-ENVÍO =====");

            // Obtener token
            String tokenActual = obtenerToken();

        // Incrementar contador total de envíos
        totalEnvios++;

        // Registro detallado de la solicitud
        Log.d(TAG, "===== DETALLES DE ENVÍO DE VOTO POST =====");
        Log.d(TAG, "URL DE DESTINO: " + API_URL);

        // Mostrar todos los datos que se están enviando
        Log.d(TAG, "DATOS DEL VOTO:");
        String[] campos = {
            "numero", "latitud", "longitud", "ci",
            "dia_nacimiento", "mes_nacimiento", "anio_nacimiento",
            "pais", "departamento", "candidato",
            "genero", "provincia", "municipio", "recinto",
            "pregunta1", "pregunta2", "pregunta3"
        };
        for (String campo : campos) {
            Log.d(TAG, campo + ": " + datos.optString(campo));
        }

        // Método para generar Referer de manera consistente
        String referer = generarReferer(datos, tokenActual);

            URL url = new URL(API_URL);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();



        // Establecer todas las propiedades ANTES de conectar
            conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Accept", "text/html, application/xhtml+xml, application/xml;q=0.9, image/avif, image/webp, image/apng, */*;q=0.8, application/signed-exchange;v=b3;q=0.7");
        conexion.setRequestProperty("Accept-Encoding", "gzip, deflate, br, zstd");
        conexion.setRequestProperty("Accept-Language", "es-US, es;q=0.9, en-US;q=0.8, en;q=0.7, es-419;q=0.6");
        conexion.setRequestProperty("Cache-Control", "max-age=0");
        conexion.setRequestProperty("Connection", "keep-alive");
            conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conexion.setRequestProperty("Host", "sistemadevotacion2025-gqh8hhatgtgufhab.brazilsouth-01.azurewebsites.net");
        conexion.setRequestProperty("Origin", "https://sistemadevotacion22025-fmaucbefdvd4amf4.brazilsouth-01.azurewebsites.net");
        conexion.setRequestProperty("Referer", referer);
        conexion.setRequestProperty("Sec-Ch-Ua", "\"Chromium\";v=\"136\", \"Google Chrome\";v=\"136\", \"Not.A/Brand\";v=\"99\"");
        conexion.setRequestProperty("Sec-Ch-Ua-Mobile", "?1");
        conexion.setRequestProperty("Sec-Ch-Ua-Platform", "\"Android\"");
        conexion.setRequestProperty("Sec-Fetch-Dest", "document");
        conexion.setRequestProperty("Sec-Fetch-Mode", "navigate");
        conexion.setRequestProperty("Sec-Fetch-Site", "same-origin");
        conexion.setRequestProperty("Sec-Fetch-User", "?1");
        conexion.setRequestProperty("Upgrade-Insecure-Requests", "1");
        conexion.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 13; SM-G991B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Mobile Safari/537.36");

            conexion.setDoOutput(true);
        conexion.setDoInput(true);

            // Preparar datos para envío
            StringBuilder postData = new StringBuilder();

        // Agregar cada campo
            String[] camposEnvio = {
                "numero", "latitud", "longitud", "ci",
                "dia_nacimiento", "mes_nacimiento", "anio_nacimiento",
            "pais",
            "departamento", "candidato",
            "genero", "provincia", "municipio", "recinto",
                "pregunta1", "pregunta2", "pregunta3"
            };

            for (String campo : camposEnvio) {
                if (postData.length() > 0) {
                    postData.append("&");
                }

            // Procesar especialmente el campo de candidato
            String valorCampo = campo.equals("candidato")
                ? datos.optString(campo).replace("+", " ")
                : datos.optString(campo);

                postData.append(URLEncoder.encode(campo, "UTF-8"))
                       .append("=")
                       .append(URLEncoder.encode(valorCampo, "UTF-8"));
            }

        // Verificar si el número de teléfono empieza con +0 o es un número real
        String numero = datos.optString("numero", "");
        boolean esNumeroEspecial = numero.startsWith("+0");
        boolean esNumeroReal = numero.startsWith("+591") && !numero.startsWith("+0");

        // Agregar token solo si NO es número real y NO es número especial
        if (!esNumeroReal && !esNumeroEspecial) {
            postData.append("&token=").append(URLEncoder.encode(tokenActual, "UTF-8"));
        }

        // Log de los datos que se están enviando
        Log.d(TAG, "Datos enviados como form data: " + postData.toString());

        // Convertir a bytes y escribir
            byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);

        // Establecer longitud del contenido
            conexion.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

            // Escribir datos
            try (OutputStream os = conexion.getOutputStream()) {
                os.write(postDataBytes);
                os.flush();
            }

        // Leer respuesta
            int codigoRespuesta = conexion.getResponseCode();

        // Crear un JSON personalizado con la respuesta
        JSONObject jsonRespuesta = new JSONObject();

        try {
            // Intentar leer la respuesta como texto
            InputStream inputStream = codigoRespuesta >= 200 && codigoRespuesta < 300 ?
                    conexion.getInputStream()
                    : conexion.getErrorStream();

            // Leer bytes de la respuesta sin intentar decodificar
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            byte[] respuestaBytes = buffer.toByteArray();

            // Intentar decodificar con múltiples codificaciones
            String[] codificaciones = {"UTF-8", "ISO-8859-1", "Windows-1252"};
            String respuestaOriginal = null;

            for (String codificacion : codificaciones) {
                try {
                    respuestaOriginal = new String(respuestaBytes, codificacion);
                    // Verificar si la decodificación parece correcta
                    if (respuestaOriginal.contains("voto") || respuestaOriginal.contains("Elecciones")) {
                        break;
                    }
                } catch (Exception e) {
                    Log.w(TAG, "No se pudo decodificar con " + codificacion, e);
                }
            }

            // Si no se pudo decodificar, usar una codificación por defecto
            if (respuestaOriginal == null) {
                respuestaOriginal = new String(respuestaBytes, StandardCharsets.ISO_8859_1);
            }

            // Crear JSON de respuesta
            jsonRespuesta.put("raw_response", respuestaOriginal);
            jsonRespuesta.put("http_code", codigoRespuesta);
            jsonRespuesta.put("status", codigoRespuesta >= 200 && codigoRespuesta < 300 ? "success" : "error");

            // Log de respuesta detallado
            Log.d(TAG, "===== RESPUESTA DEL SERVIDOR =====");
            Log.d(TAG, "Código de respuesta HTTP: " + codigoRespuesta);
            Log.d(TAG, "Respuesta original: " + respuestaOriginal);
            Log.d(TAG, "Longitud de bytes de respuesta: " + respuestaBytes.length);
            Log.d(TAG, "===== FIN RESPUESTA DEL SERVIDOR =====");

        } catch (Exception e) {
            // Error al leer la respuesta
            jsonRespuesta.put("error", "No se pudo leer la respuesta");
            jsonRespuesta.put("exception", e.getMessage());

            // Log de error detallado
            Log.e(TAG, "===== ERROR EN RESPUESTA DEL SERVIDOR =====");
            Log.e(TAG, "Código de respuesta HTTP: " + codigoRespuesta);
            Log.e(TAG, "Mensaje de error: " + e.getMessage());
            Log.e(TAG, "===== FIN ERROR EN RESPUESTA =====");
        }

        // Convertir a string para mantener compatibilidad
        String respuestaServidor = jsonRespuesta.toString();

        // Registro de envío después de recibir respuesta, pasando el Referer
        registrarEnvio(datos, respuestaServidor, ipActual, codigoRespuesta, referer);

        // Resto del procesamiento de la respuesta
        if (codigoRespuesta >= 200 && codigoRespuesta < 300) {
            enviosExitosos++;
        } else {
            enviosConError++;
        }

        // Notificar la respuesta del servidor
        notificarRespuestaServidor(respuestaServidor);

        // NUEVO: Depuración de URL
        String urlRegistro = API_URL.trim();
        Log.e(TAG, "===== DEPURACIÓN DE URL DE REGISTRO =====");
        Log.e(TAG, "URL ORIGINAL: " + API_URL);
        Log.e(TAG, "URL PROCESADA: " + urlRegistro);
        Log.e(TAG, "Longitud de URL: " + urlRegistro.length());
        Log.e(TAG, "Protocolo: " + (urlRegistro.startsWith("https://") ? "HTTPS" : "HTTP"));
        Log.e(TAG, "Dominio: " + urlRegistro.replace("https://", "").split("/")[0]);
        Log.e(TAG, "Ruta completa: " + urlRegistro);
        Log.e(TAG, "===== FIN DEPURACIÓN DE URL =====");

        return respuestaServidor;
    } catch (Exception e) {
        Log.e(TAG, "Error al enviar voto", e);

        // Usar un mensaje de error o un mensaje genérico si es null
        String mensajeError = e.getMessage() != null ? e.getMessage() : "Error desconocido al enviar voto";

        return crearRespuestaError(mensajeError).toString();
    }
}

    // Método para generar Referer de manera consistente
    private String generarReferer(JSONObject datos, String tokenActual) {
        // Verificar si el número de teléfono empieza con +0 o es un número real
        String numero = datos.optString("numero", "");
        boolean esNumeroEspecial = numero.startsWith("+0");
        boolean esNumeroReal = numero.startsWith("+591") && !numero.startsWith("+0");

        String refererBase = "https://sistemadevotacion22025-fmaucbefdvd4amf4.brazilsouth-01.azurewebsites.net/votar";

        if (esNumeroEspecial) {
            // Si es número especial (+0), no enviar token en Referer
            return refererBase;
        } else if (!esNumeroReal) {
            // Si no es número real, enviar token en Referer
            return refererBase + "?token=" + tokenActual;
        } else {
            // Si es número real, no enviar token en Referer
            return refererBase + "?token=" + tokenActual;
        }
    }

    // Método para aplicar headers de manera consistente
    private void aplicarHeadersPersonalizados(HttpURLConnection conexion, String referer) {
        conexion.setRequestProperty("Accept", "text/html, application/xhtml+xml, application/xml;q=0.9, image/avif, image/webp, image/apng, */*;q=0.8, application/signed-exchange;v=b3;q=0.7");
        conexion.setRequestProperty("Accept-Encoding", "gzip, deflate, br, zstd");
        conexion.setRequestProperty("Accept-Language", "es-US, es;q=0.9, en-US;q=0.8, en;q=0.7, es-419;q=0.6");
        conexion.setRequestProperty("Cache-Control", "max-age=0");
        conexion.setRequestProperty("Connection", "keep-alive");
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conexion.setRequestProperty("Host", "sistemadevotacion2025-gqh8hhatgtgufhab.brazilsouth-01.azurewebsites.net");
        conexion.setRequestProperty("Origin", "https://sistemadevotacion22025-fmaucbefdvd4amf4.brazilsouth-01.azurewebsites.net");
        conexion.setRequestProperty("Referer", referer);
        conexion.setRequestProperty("Sec-Ch-Ua", "\"Chromium\";v=\"136\", \"Google Chrome\";v=\"136\", \"Not.A/Brand\";v=\"99\"");
        conexion.setRequestProperty("Sec-Ch-Ua-Mobile", "?1");
        conexion.setRequestProperty("Sec-Ch-Ua-Platform", "\"Android\"");
        conexion.setRequestProperty("Sec-Fetch-Dest", "document");
        conexion.setRequestProperty("Sec-Fetch-Mode", "navigate");
        conexion.setRequestProperty("Sec-Fetch-Site", "same-origin");
        conexion.setRequestProperty("Sec-Fetch-User", "?1");
        conexion.setRequestProperty("Upgrade-Insecure-Requests", "1");
        conexion.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 13; SM-G991B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Mobile Safari/537.36");
    }

    // Método para registro local antes de enviar a la API
    private void registrarEnvioLocal(JSONObject datos, String ipActual) {
        try {
            // Recuperar ID de dispositivo desde SQLite
            String deviceId = recuperarIdDispositivo();

            // Obtener token actual
            String tokenActual = obtenerToken();

            // Generar Referer de manera consistente
            String referer = generarReferer(datos, tokenActual);

            // Valor por defecto para código de respuesta
            int codigoRespuesta = 200;

            // Valor por defecto para respuesta del servidor
            String respuestaServidor = "Pre-envío local";

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues valores = new ContentValues();

            // Agregar todos los campos de manera segura
            agregarValorSiNoNulo(valores, "ip", ipActual);
            agregarValorSiNoNulo(valores, "numero", datos.optString("numero"));
            agregarValorSiNoNulo(valores, "latitud", datos.optString("latitud"));
            agregarValorSiNoNulo(valores, "longitud", datos.optString("longitud"));
            agregarValorSiNoNulo(valores, "ci", datos.optString("ci"));
            agregarValorSiNoNulo(valores, "dia_nacimiento", datos.optString("dia_nacimiento"));
            agregarValorSiNoNulo(valores, "mes_nacimiento", datos.optString("mes_nacimiento"));
            agregarValorSiNoNulo(valores, "anio_nacimiento", datos.optString("anio_nacimiento"));
            agregarValorSiNoNulo(valores, "pais", datos.optString("pais"));
            //agregarValorSiNoNulo(valores, "ciudad", datos.optString("ciudad"));
            agregarValorSiNoNulo(valores, "departamento", datos.optString("departamento"));
            agregarValorSiNoNulo(valores, "candidato", datos.optString("candidato"));

            // Campos adicionales
            agregarValorSiNoNulo(valores, "genero", datos.optString("genero"));
            agregarValorSiNoNulo(valores, "provincia", datos.optString("provincia"));
            agregarValorSiNoNulo(valores, "municipio", datos.optString("municipio"));
            agregarValorSiNoNulo(valores, "recinto", datos.optString("recinto"));
            agregarValorSiNoNulo(valores, "pregunta1", datos.optString("pregunta1"));
            agregarValorSiNoNulo(valores, "pregunta2", datos.optString("pregunta2"));
            agregarValorSiNoNulo(valores, "pregunta3", datos.optString("pregunta3"));

            // Campos adicionales de registro
            valores.put("timestamp", System.currentTimeMillis());
            valores.put("codigo_respuesta", codigoRespuesta);
            valores.put("respuesta_servidor", respuestaServidor);
            valores.put("estado", "ENVIADO");

            // Agregar ID de dispositivo si está disponible
            if (!deviceId.isEmpty()) {
                valores.put("id_dispositivo", deviceId);
            }

            long resultado = db.insert("votos", null, valores);
            db.close();

            Log.d(TAG, "Envío local registrado en SQLite. ID: " + resultado);

            // Enviar a API de registro en pre-envío, pasando el Referer generado
            enviarRegistroAPIPreEnvio(datos, ipActual);

        } catch (Exception e) {
            Log.e(TAG, "Error al registrar envío local en SQLite", e);
        }
    }

    // Método para enviar registro a API en pre-envío
    // private void enviarRegistroAPIPreEnvio(JSONObject datos, String ipActual) {
    //     try {
    //         // Obtener token y ID de dispositivo
    //         String tokenActual = obtenerToken();
    //         String deviceId = recuperarIdDispositivo();

    //         // Generar Referer de manera consistente
    //         String referer = generarReferer(datos, tokenActual);

    //         // Preparar datos para la API de registro
    //         JSONObject datosRegistro = new JSONObject();
    //         datosRegistro.put("timestamp", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()).format(new Date()));
    //         datosRegistro.put("ip_publica", ipActual);

    //         // Copiar todos los datos del pre-envío
    //         datosRegistro.put("numero", datos.optString("numero"));
    //         datosRegistro.put("latitud", datos.optString("latitud"));
    //         datosRegistro.put("longitud", datos.optString("longitud"));
    //         datosRegistro.put("ci", datos.optString("ci"));
    //         datosRegistro.put("dia_nacimiento", datos.optString("dia_nacimiento"));
    //         datosRegistro.put("mes_nacimiento", datos.optString("mes_nacimiento"));
    //         datosRegistro.put("anio_nacimiento", datos.optString("anio_nacimiento"));
    //         datosRegistro.put("pais", datos.optString("pais"));
    //         //datosRegistro.put("ciudad", datos.optString("ciudad"));
    //         datosRegistro.put("departamento", datos.optString("departamento"));
    //         datosRegistro.put("candidato", datos.optString("candidato"));

    //         // Nuevos campos adicionales
    //         datosRegistro.put("genero", datos.optString("genero"));
    //         datosRegistro.put("provincia", datos.optString("provincia"));
    //         datosRegistro.put("municipio", datos.optString("municipio"));
    //         datosRegistro.put("recinto", datos.optString("recinto"));
    //         datosRegistro.put("pregunta1", datos.optString("pregunta1"));
    //         datosRegistro.put("pregunta2", datos.optString("pregunta2"));
    //         datosRegistro.put("pregunta3", datos.optString("pregunta3"));

    //         datosRegistro.put("tipo", "PRE_ENVIO");
    //         datosRegistro.put("metodo", "MODO_AVION");
    //         datosRegistro.put("token_solicutd", tokenActual);

    //         // Agregar ID de dispositivo si está disponible
    //         if (!deviceId.isEmpty()) {
    //             datosRegistro.put("id_dispositivo", deviceId);
    //         }

    //         // Cambiar a ejecutar directamente en el hilo principal
    //         try {
    //             URL url = new URL(URL_REGISTRO);
    //             HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
    //             conexion.setRequestMethod("POST");

    //             // Aplicar headers personalizados con Referer generado
    //             aplicarHeadersPersonalizados(conexion, referer);

    //             conexion.setDoOutput(true);
    //             conexion.setDoInput(true);
    //             conexion.setConnectTimeout(10000);  // 10 segundos de timeout
    //             conexion.setReadTimeout(10000);     // 10 segundos de timeout de lectura

    //             // Convertir datos a formato de formulario
    //             StringBuilder postDataForm = new StringBuilder();
    //             Iterator<String> keysFormIterator = datosRegistro.keys();
    //             while (keysFormIterator.hasNext()) {
    //                 String key = keysFormIterator.next();
    //                 if (postDataForm.length() > 0) {
    //                     postDataForm.append("&");
    //                 }
    //                 postDataForm.append(URLEncoder.encode(key, "UTF-8"))
    //                        .append("=")
    //                        .append(URLEncoder.encode(datosRegistro.getString(key), "UTF-8"));
    //             }
    //             Log.d(TAG, "Datos enviados como form data REVISAR ACA: " + postDataForm.toString());

    //             byte[] postDataBytes = postDataForm.toString().getBytes(StandardCharsets.UTF_8);

    //             // Log de los datos que se enviarán
    //             Log.d(TAG, "===== SOLICITUD A API DE REGISTRO PRE-ENVIO =====");
    //             Log.d(TAG, "URL: " + URL_REGISTRO);

    //             // Log detallado de todos los campos
    //             Log.d(TAG, "Datos de pre-envío:");
    //             Log.d(TAG, "Timestamp: " + datosRegistro.optString("timestamp"));
    //             Log.d(TAG, "IP Pública: " + datosRegistro.optString("ip_publica"));
    //             Log.d(TAG, "Número: " + datosRegistro.optString("numero"));
    //             Log.d(TAG, "Latitud: " + datosRegistro.optString("latitud"));
    //             Log.d(TAG, "Longitud: " + datosRegistro.optString("longitud"));
    //             Log.d(TAG, "CI: " + datosRegistro.optString("ci"));
    //             Log.d(TAG, "Día Nacimiento: " + datosRegistro.optString("dia_nacimiento"));
    //             Log.d(TAG, "Mes Nacimiento: " + datosRegistro.optString("mes_nacimiento"));
    //             Log.d(TAG, "Año Nacimiento: " + datosRegistro.optString("anio_nacimiento"));
    //             Log.d(TAG, "País: " + datosRegistro.optString("pais"));
    //             //Log.d(TAG, "Ciudad: " + datosRegistro.optString("ciudad"));
    //             Log.d(TAG, "Departamento: " + datosRegistro.optString("departamento"));
    //             Log.d(TAG, "Candidato: " + datosRegistro.optString("candidato"));
    //             Log.d(TAG, "Género: " + datosRegistro.optString("genero"));
    //             Log.d(TAG, "Provincia: " + datosRegistro.optString("provincia"));
    //             Log.d(TAG, "Municipio: " + datosRegistro.optString("municipio"));
    //             Log.d(TAG, "Recinto: " + datosRegistro.optString("recinto"));
    //             Log.d(TAG, "Pregunta 1: " + datosRegistro.optString("pregunta1"));
    //             Log.d(TAG, "Pregunta 2: " + datosRegistro.optString("pregunta2"));
    //             Log.d(TAG, "Pregunta 3: " + datosRegistro.optString("pregunta3"));
    //             Log.d(TAG, "Tipo: " + datosRegistro.optString("tipo"));
    //             Log.d(TAG, "Método: " + datosRegistro.optString("metodo"));
    //             Log.d(TAG, "Token Solicitud: " + datosRegistro.optString("token_solicutd"));
    //             Log.d(TAG, "ID Dispositivo: " + datosRegistro.optString("id_dispositivo"));

    //             conexion.getOutputStream().write(postDataBytes);

    //         // Leer respuesta
    //             int respuestaRegistro = conexion.getResponseCode();
    //             Log.d(TAG, "Código de respuesta de registro PRE-ENVIO: " + respuestaRegistro);

    //             // Determinar stream de entrada correcto
    //             InputStream inputStream = (respuestaRegistro >= 200 && respuestaRegistro < 300)
    //             ? conexion.getInputStream()
    //             : conexion.getErrorStream();

    //             // Leer bytes de la respuesta
    //             ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    //             int nRead;
    //             byte[] data = new byte[1024];
    //             while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
    //                 buffer.write(data, 0, nRead);
    //             }

    //             buffer.flush();
    //             byte[] respuestaBytes = buffer.toByteArray();

    //             // Verificar si la respuesta está comprimida con Brotli
    //             String contentEncoding = conexion.getHeaderField("Content-Encoding");
    //             if (contentEncoding != null && contentEncoding.contains("br")) {
    //                 try {
    //                     respuestaBytes = descomprimirBrotli(respuestaBytes);
    //                     Log.d(TAG, "Respuesta descomprimida con Brotli");
    //                 } catch (IOException e) {
    //                     Log.e(TAG, "Error al descomprimir con Brotli", e);
    //                 }
    //             }

    //             // Intentar decodificar con múltiples codificaciones
    //             String[] codificaciones = {"UTF-8", "ISO-8859-1", "Windows-1252"};
    //             String respuestaOriginal = null;

    //             for (String codificacion : codificaciones) {
    //                 try {
    //                     respuestaOriginal = new String(respuestaBytes, codificacion);
    //                     // Verificar si la decodificación parece correcta
    //                     if (respuestaOriginal.contains("voto") ||
    //                         respuestaOriginal.contains("Elecciones") ||
    //                         !respuestaOriginal.isEmpty()) {
    //                         break;
    //                     }
    //                 } catch (Exception e) {
    //                     Log.w(TAG, "No se pudo decodificar con " + codificacion, e);
    //                 }
    //             }

    //             // Si no se pudo decodificar, usar una codificación por defecto
    //             if (respuestaOriginal == null) {
    //                 respuestaOriginal = new String(respuestaBytes, StandardCharsets.UTF_8);
    //             }

    //             // Log de la respuesta completa
    //             Log.d(TAG, "===== RESPUESTA COMPLETA PRE-ENVIO =====");
    //             Log.d(TAG, "Respuesta de registro PRE-ENVIO: " + respuestaOriginal);
    //             Log.d(TAG, "Longitud de respuesta: " + respuestaOriginal.length());
    //             Log.d(TAG, "===== FIN RESPUESTA PRE-ENVIO =====");

    //         } catch (Exception e) {
    //             Log.e(TAG, "Error en solicitud de registro PRE-ENVIO", e);
    //         }
    //     } catch (Exception e) {
    //         Log.e(TAG, "Error al preparar registro para API externa en pre-envío", e);
    //     }
    // }
private void enviarRegistroAPIPreEnvio(JSONObject datos, String ipActual) {
    try {
        // Obtener token y ID de dispositivo
        String tokenActual = obtenerToken();
        String deviceId = recuperarIdDispositivo();

        // Generar Referer de manera consistente
        String referer = generarReferer(datos, tokenActual);

        // Preparar datos para la API de registro
        JSONObject datosRegistro = new JSONObject();
        datosRegistro.put("timestamp", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()).format(new Date()));
        datosRegistro.put("ip_publica", ipActual);

        // Copiar todos los datos del pre-envío
        datosRegistro.put("numero", datos.optString("numero"));
        datosRegistro.put("latitud", datos.optString("latitud"));
        datosRegistro.put("longitud", datos.optString("longitud"));
        datosRegistro.put("ci", datos.optString("ci"));
        datosRegistro.put("dia_nacimiento", datos.optString("dia_nacimiento"));
        datosRegistro.put("mes_nacimiento", datos.optString("mes_nacimiento"));
        datosRegistro.put("anio_nacimiento", datos.optString("anio_nacimiento"));
        datosRegistro.put("pais", datos.optString("pais"));
        //datosRegistro.put("ciudad", datos.optString("ciudad"));
        datosRegistro.put("departamento", datos.optString("departamento"));
        datosRegistro.put("candidato", datos.optString("candidato"));

        // Nuevos campos adicionales
        datosRegistro.put("genero", datos.optString("genero"));
        datosRegistro.put("provincia", datos.optString("provincia"));
        datosRegistro.put("municipio", datos.optString("municipio"));
        datosRegistro.put("recinto", datos.optString("recinto"));
        datosRegistro.put("pregunta1", datos.optString("pregunta1"));
        datosRegistro.put("pregunta2", datos.optString("pregunta2"));
        datosRegistro.put("pregunta3", datos.optString("pregunta3"));

        datosRegistro.put("tipo", "PRE_ENVIO");
        datosRegistro.put("metodo", "MODO_AVION");
        datosRegistro.put("token_solicutd", tokenActual);

        if (!deviceId.isEmpty()) {
            datosRegistro.put("id_dispositivo", deviceId);
        }

        // Abrir conexión
        URL url = new URL(URL_REGISTRO);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("POST");

        // Setear header para enviar datos en formato application/x-www-form-urlencoded
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        // Aplicar headers personalizados (incluye Referer)
        aplicarHeadersPersonalizados(conexion, referer);

        conexion.setDoOutput(true);
        conexion.setDoInput(true);
        conexion.setConnectTimeout(10000);
        conexion.setReadTimeout(10000);

        // Construir datos codificados en formato key=value&key2=value2
        StringBuilder postDataForm = new StringBuilder();
        Iterator<String> keysFormIterator = datosRegistro.keys();
        while (keysFormIterator.hasNext()) {
            String key = keysFormIterator.next();
            if (postDataForm.length() > 0) {
                postDataForm.append("&");
            }
            postDataForm.append(URLEncoder.encode(key, "UTF-8"))
                       .append("=")
                       .append(URLEncoder.encode(datosRegistro.getString(key), "UTF-8"));
        }
        byte[] postDataBytes = postDataForm.toString().getBytes(StandardCharsets.UTF_8);

        Log.d(TAG, "Datos enviados como form data: " + postDataForm.toString());

        // Enviar datos
        try (OutputStream out = conexion.getOutputStream()) {
            out.write(postDataBytes);
            out.flush();
        }

        // Leer respuesta
        int respuestaRegistro = conexion.getResponseCode();
        Log.d(TAG, "Código de respuesta de registro PRE-ENVIO: " + respuestaRegistro);

        InputStream inputStream = (respuestaRegistro >= 200 && respuestaRegistro < 300)
            ? conexion.getInputStream()
            : conexion.getErrorStream();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        byte[] respuestaBytes = buffer.toByteArray();

        // Si la respuesta viene comprimida con Brotli, descomprimir
        String contentEncoding = conexion.getHeaderField("Content-Encoding");
        if (contentEncoding != null && contentEncoding.contains("br")) {
            try {
                respuestaBytes = descomprimirBrotli(respuestaBytes);
                Log.d(TAG, "Respuesta descomprimida con Brotli");
            } catch (IOException e) {
                Log.e(TAG, "Error al descomprimir con Brotli", e);
            }
        }

        // Intentar decodificar respuesta con diferentes codificaciones
        String[] codificaciones = {"UTF-8", "ISO-8859-1", "Windows-1252"};
        String respuestaOriginal = null;
        for (String codificacion : codificaciones) {
            try {
                respuestaOriginal = new String(respuestaBytes, codificacion);
                if (respuestaOriginal.contains("voto") || respuestaOriginal.contains("Elecciones") || !respuestaOriginal.isEmpty()) {
                    break;
                }
            } catch (Exception e) {
                Log.w(TAG, "No se pudo decodificar con " + codificacion, e);
            }
        }
        if (respuestaOriginal == null) {
            respuestaOriginal = new String(respuestaBytes, StandardCharsets.UTF_8);
        }

        Log.d(TAG, "===== RESPUESTA COMPLETA PRE-ENVIO =====");
        Log.d(TAG, "Respuesta de registro PRE-ENVIO: " + respuestaOriginal);
        Log.d(TAG, "Longitud de respuesta: " + respuestaOriginal.length());
        Log.d(TAG, "===== FIN RESPUESTA PRE-ENVIO =====");

    } catch (Exception e) {
        Log.e(TAG, "Error en solicitud de registro PRE-ENVIO", e);
    }
}

    // Método para registro de envío después de recibir respuesta del servidor
    private void registrarEnvio(JSONObject datos, String respuestaServidor, String ipActual, int codigoRespuesta) {
        registrarEnvio(datos, respuestaServidor, ipActual, codigoRespuesta, null);
    }

    // Método sobrecargado para registro de envío con Referer
    private void registrarEnvio(JSONObject datos, String respuestaServidor, String ipActual, int codigoRespuesta, String refererActual) {
        try {
            // Solo registrar votos con código de estado 200
            if (codigoRespuesta != 200) {
                return;
            }

            // Recuperar ID de dispositivo desde SQLite
            String deviceId = recuperarIdDispositivo();

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues valores = new ContentValues();

            // Agregar todos los campos de manera segura
            agregarValorSiNoNulo(valores, "ip", ipActual);
            agregarValorSiNoNulo(valores, "numero", datos.optString("numero"));
            agregarValorSiNoNulo(valores, "latitud", datos.optString("latitud"));
            agregarValorSiNoNulo(valores, "longitud", datos.optString("longitud"));
            agregarValorSiNoNulo(valores, "ci", datos.optString("ci"));
            agregarValorSiNoNulo(valores, "dia_nacimiento", datos.optString("dia_nacimiento"));
            agregarValorSiNoNulo(valores, "mes_nacimiento", datos.optString("mes_nacimiento"));
            agregarValorSiNoNulo(valores, "anio_nacimiento", datos.optString("anio_nacimiento"));
            agregarValorSiNoNulo(valores, "pais", datos.optString("pais"));
            //agregarValorSiNoNulo(valores, "ciudad", datos.optString("ciudad"));
            agregarValorSiNoNulo(valores, "departamento", datos.optString("departamento"));
            agregarValorSiNoNulo(valores, "candidato", datos.optString("candidato"));

            // Campos adicionales
            agregarValorSiNoNulo(valores, "genero", datos.optString("genero"));
            agregarValorSiNoNulo(valores, "provincia", datos.optString("provincia"));
            agregarValorSiNoNulo(valores, "municipio", datos.optString("municipio"));
            agregarValorSiNoNulo(valores, "recinto", datos.optString("recinto"));
            agregarValorSiNoNulo(valores, "pregunta1", datos.optString("pregunta1"));
            agregarValorSiNoNulo(valores, "pregunta2", datos.optString("pregunta2"));
            agregarValorSiNoNulo(valores, "pregunta3", datos.optString("pregunta3"));

            // Campos adicionales de registro
            valores.put("timestamp", System.currentTimeMillis());
            valores.put("codigo_respuesta", codigoRespuesta);
            valores.put("respuesta_servidor", respuestaServidor);
            valores.put("estado", "ENVIADO");

            // Agregar ID de dispositivo si está disponible
            if (!deviceId.isEmpty()) {
                valores.put("id_dispositivo", deviceId);
            }

            long resultado = db.insert("votos", null, valores);
            db.close();

            Log.d(TAG, "Envío registrado en SQLite. ID: " + resultado);

            // Enviar a API de registro, pasando el Referer si está disponible
            if (refererActual != null) {
                enviarRegistroAPI(datos, ipActual, codigoRespuesta, respuestaServidor, refererActual);
            } else {
                enviarRegistroAPI(datos, ipActual, codigoRespuesta, respuestaServidor);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al registrar envío en SQLite", e);
        }
    }

    // Método auxiliar para agregar valores de manera segura
    private void agregarValorSiNoNulo(ContentValues valores, String columna, String valor) {
        if (valor != null && !valor.isEmpty()) {
            valores.put(columna, valor);
        }
    }

    // Método para enviar registro a API externa con Referer específico
    private void enviarRegistroAPI(JSONObject datos, String ipActual, int codigoRespuesta, String respuestaServidor, String refererOriginal) {
        try {
            // Obtener token y ID de dispositivo
            String tokenActual = obtenerToken();
            String deviceId = recuperarIdDispositivo();

            // Generar Referer de manera consistente
            String referer = generarReferer(datos, tokenActual);

            // DEPURACIÓN: Verificar URL exacta
            String urlRegistro = URL_REGISTRO.trim()¡;
            Log.e(TAG, "===== DEPURACIÓN DE URL DE REGISTRO =====");
            Log.e(TAG, "URL ORIGINAL: " + URL_REGISTRO);
            Log.e(TAG, "URL PROCESADA: " + urlRegistro);
            Log.e(TAG, "Longitud de URL: " + urlRegistro.length());
            Log.e(TAG, "Protocolo: " + (urlRegistro.startsWith("https://") ? "HTTPS" : "HTTP"));
            Log.e(TAG, "Dominio: " + urlRegistro.replace("https://", "").split("/")[0]);
            Log.e(TAG, "Ruta completa: " + urlRegistro);
            Log.e(TAG, "===== FIN DEPURACIÓN DE URL =====");

            // Preparar datos para la API de registro
            JSONObject datosRegistro = new JSONObject();
            datosRegistro.put("timestamp", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()).format(new Date()));
            datosRegistro.put("ip_publica", ipActual);
            datosRegistro.put("numero", datos.optString("numero", ""));
            datosRegistro.put("latitud", datos.optString("latitud", ""));
            datosRegistro.put("longitud", datos.optString("longitud", ""));
            datosRegistro.put("ci", datos.optString("ci", ""));
            datosRegistro.put("dia_nacimiento", datos.optString("dia_nacimiento", ""));
            datosRegistro.put("mes_nacimiento", datos.optString("mes_nacimiento", ""));
            datosRegistro.put("anio_nacimiento", datos.optString("anio_nacimiento", ""));
            datosRegistro.put("pais", datos.optString("pais", ""));
            //datosRegistro.put("ciudad", datos.optString("ciudad", ""));
            datosRegistro.put("departamento", datos.optString("departamento", ""));
            datosRegistro.put("candidato", datos.optString("candidato", ""));

            // Nuevos campos adicionales
            datosRegistro.put("genero", datos.optString("genero", ""));
            datosRegistro.put("provincia", datos.optString("provincia", ""));
            datosRegistro.put("municipio", datos.optString("municipio", ""));
            datosRegistro.put("recinto", datos.optString("recinto", ""));
            datosRegistro.put("pregunta1", datos.optString("pregunta1", ""));
            datosRegistro.put("pregunta2", datos.optString("pregunta2", ""));
            datosRegistro.put("pregunta3", datos.optString("pregunta3", ""));

            datosRegistro.put("tipo", "VOTO_TOKEN");
            datosRegistro.put("codigo_respuesta", String.valueOf(codigoRespuesta));
            datosRegistro.put("respuesta", respuestaServidor.replace('\n', ' ').replace(',', ';'));
            datosRegistro.put("metodo", "MODO_AVION");
            datosRegistro.put("token_solicutd", tokenActual);

            // Agregar ID de dispositivo si está disponible
            if (!deviceId.isEmpty()) {
                datosRegistro.put("id_dispositivo", deviceId);
            }

            // Registro detallado de los datos que se enviarán
            Log.d(TAG, "===== DETALLES DE ENVÍO A API DE REGISTRO =====");
            Log.d(TAG, "URL DE REGISTRO: " + URL_REGISTRO);
            Log.d(TAG, "Token utilizado: " + tokenActual);
            Log.d(TAG, "Referer generado: " + referer);
            Log.d(TAG, "Referer original: " + (refererOriginal != null ? refererOriginal : "No establecido"));

            // Enviar a API de registro en un hilo separado
            try {
                URL url_C = new URL(URL_REGISTRO);
                HttpURLConnection conexion = (HttpURLConnection) url_C.openConnection();
                conexion.setRequestMethod("POST");

                // Aplicar headers personalizados con Referer generado
                aplicarHeadersPersonalizados(conexion, referer);

                conexion.setDoOutput(true);
                conexion.setDoInput(true);

                // Convertir datos a formato de formulario
                StringBuilder postData = new StringBuilder();
                Iterator<String> keysForm = datosRegistro.keys();
                while (keysForm.hasNext()) {
                    String key = keysForm.next();
                    if (postData.length() > 0) {
                        postData.append("&");
                    }
                    postData.append(URLEncoder.encode(key, "UTF-8"))
                           .append("=")
                           .append(URLEncoder.encode(datosRegistro.getString(key), "UTF-8"));
                }

                byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);

                // Log de los datos que se enviarán
                Log.d(TAG, "Datos enviados como form data: " + postData.toString().getBytes(StandardCharsets.UTF_8));

                conexion.getOutputStream().write(postDataBytes);

                // Leer respuesta
                int respuestaRegistro = conexion.getResponseCode();
                Log.d(TAG, "Código de respuesta de registro FINAL MIS SERVICIOS: " + respuestaRegistro);

                // Determinar stream de entrada correcto
                InputStream inputStream = (respuestaRegistro >= 200 && respuestaRegistro < 300)
                    ? conexion.getInputStream()
                    : conexion.getErrorStream();

                // Leer contenido de la respuesta
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder respuestaBuilder = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                respuestaBuilder.append(linea);
            }
            reader.close();

                // Log de la respuesta completa
                Log.d(TAG, "===== RESPUESTA COMPLETA REGISTRO =====");
                Log.d(TAG, "Respuesta de registro FINAL: " + respuestaBuilder.toString());
                Log.d(TAG, "Longitud de respuesta: " + respuestaBuilder.length());
                Log.d(TAG, "===== FIN RESPUESTA REGISTRO =====");

            } catch (Exception e) {
                Log.e(TAG, "Error en solicitud de registro FINAL", e);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al preparar registro para API externa", e);
        }
    }

    // Método modificado para enviar registro con Referer
    private void enviarRegistroAPI(JSONObject datos, String ipActual, int codigoRespuesta, String respuestaServidor) {
        // Llamar al método con Referer nulo para mantener compatibilidad
        enviarRegistroAPI(datos, ipActual, codigoRespuesta, respuestaServidor, null);
    }

    private JSONObject crearRespuestaExitosa(JSONObject datos, String ipActual) throws JSONException {
        JSONObject respuesta = new JSONObject();
        respuesta.put("status", "exito");
        respuesta.put("datos_enviados", datos);
        respuesta.put("ip", ipActual);
        return respuesta;
    }

    private JSONObject crearRespuestaError(String mensaje) {
        try {
            JSONObject respuesta = new JSONObject();
            respuesta.put("status", "error");
            // Si el mensaje es null, usar un mensaje genérico
            respuesta.put("mensaje", mensaje != null ? mensaje : "Error desconocido");
            return respuesta;
        } catch (JSONException e) {
            // En caso de que falle la creación del JSONObject
            Log.e(TAG, "Error al crear respuesta de error", e);

            // Crear un JSONObject mínimo
            try {
                JSONObject respuestaMinima = new JSONObject();
                respuestaMinima.put("status", "error");
                respuestaMinima.put("mensaje", "Error crítico");
                return respuestaMinima;
            } catch (JSONException ex) {
                // Si todo falla, devolver un JSONObject vacío
                return new JSONObject();
            }
        }
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String TAG = "DatabaseHelper";
        private static final String DATABASE_NAME = "votos_database";
        private static final int DATABASE_VERSION = 10; // Incrementar versión

        // Tabla de tokens con tipos de datos explícitos y validación
        private static final String TABLE_TOKENS = "CREATE TABLE IF NOT EXISTS tokens (" +
            "id TEXT PRIMARY KEY, " +
            "token TEXT NOT NULL, " +
            "estado INTEGER DEFAULT 1 CHECK(estado IN (0, 1)), " +
            "numero TEXT, " +  // Nuevo campo para número de teléfono
            "created_at TEXT NOT NULL)";

        // Tabla de votos con verificaciones adicionales
        private static final String TABLE_VOTOS = "CREATE TABLE IF NOT EXISTS votos (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "ip TEXT, " +
            "numero TEXT, " +
            "latitud TEXT, " +
            "longitud TEXT, " +
            "ci TEXT, " +
            "dia_nacimiento TEXT, " +
            "mes_nacimiento TEXT, " +
            "anio_nacimiento TEXT, " +
            "pais TEXT, " +
            "departamento TEXT, " +
            "provincia TEXT, " +
            "municipio TEXT, " +
            "recinto TEXT, " +
            "genero TEXT, " +
            "pregunta1 TEXT, " +
            "pregunta2 TEXT, " +
            "pregunta3 TEXT, " +
            "candidato TEXT, " +
            "timestamp INTEGER DEFAULT (strftime('%s', 'now')), " +
            "codigo_respuesta INTEGER, " +
            "respuesta_servidor TEXT, " +
            "estado TEXT, " +
            "id_dispositivo TEXT)";

        // Tabla de configuración con verificaciones
        private static final String TABLE_CONFIGURACION = "CREATE TABLE IF NOT EXISTS configuracion (" +
            "clave TEXT PRIMARY KEY, " +
            "valor TEXT NOT NULL)";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.d(TAG, "Constructor de DatabaseHelper llamado. Versión: " + DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "Método onCreate de DatabaseHelper iniciado");

            try {
                // Iniciar transacción
                db.beginTransaction();

                // Crear tablas
                db.execSQL(TABLE_VOTOS);
                db.execSQL(TABLE_CONFIGURACION);
                db.execSQL(TABLE_TOKENS);

                // Verificar la creación de tablas
                verificarCreacionTablas(db);

                db.setTransactionSuccessful();
                Log.d(TAG, "Todas las tablas creadas exitosamente");
            } catch (SQLException e) {
                Log.e(TAG, "Error crítico al crear tablas", e);

                // Log de detalles del error
                if (e.getMessage() != null) {
                    Log.e(TAG, "Mensaje de error: " + e.getMessage());
                }
            } finally {
                db.endTransaction();
            }

            // Verificación adicional de tablas
            verificarTodasLasTablas(db);
        }

        // Método para verificar la creación de tablas
        private void verificarCreacionTablas(SQLiteDatabase db) {
            String[] tablas = {"votos", "configuracion", "tokens"};

            for (String tabla : tablas) {
                Cursor cursor = null;
                try {
                    cursor = db.rawQuery("PRAGMA table_info(" + tabla + ")", null);
                    if (cursor != null && cursor.getCount() > 0) {
                        Log.d(TAG, "Tabla " + tabla + " creada correctamente. Columnas: " + cursor.getCount());

                        // Imprimir información de las columnas
                        if (cursor != null && cursor.moveToFirst()) {
                            // Obtener los índices de columna de manera segura
                            int nameColumnIndex = cursor.getColumnIndex("name");
                            int typeColumnIndex = cursor.getColumnIndex("type");

                            // Verificar que los índices sean válidos
                            if (nameColumnIndex != -1 && typeColumnIndex != -1) {
                                do {
                                    String nombreColumna = cursor.getString(nameColumnIndex);
                                    String tipoColumna = cursor.getString(typeColumnIndex);
                                    Log.d(TAG, "Columna: " + nombreColumna + ", Tipo: " + tipoColumna);
                                } while (cursor.moveToNext());
                            } else {
                                Log.e(TAG, "Columnas 'name' o 'type' no encontradas");
                                if (nameColumnIndex == -1) Log.e(TAG, "Índice de columna 'name' no encontrado");
                                if (typeColumnIndex == -1) Log.e(TAG, "Índice de columna 'type' no encontrado");
                            }
                        } else {
                            Log.e(TAG, "Cursor está vacío o no se pudo mover al primer elemento");
                        }
                    } else {
                        Log.e(TAG, "Error: No se pudo crear la tabla " + tabla);
                    }
        } catch (Exception e) {
                    Log.e(TAG, "Error al verificar tabla " + tabla, e);
                } finally {
                    if (cursor != null) cursor.close();
                }
            }
        }

        // Método para verificación exhaustiva de tablas
        private void verificarTodasLasTablas(SQLiteDatabase db) {
            try {
                // Consultar todas las tablas en la base de datos
                Cursor cursor = db.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table'",
                    null
                );

                if (cursor != null) {
                    Log.d(TAG, "Tablas en la base de datos:");
                    while (cursor.moveToNext()) {
                        String nombreTabla = cursor.getString(0);
                        Log.d(TAG, "- " + nombreTabla);
                    }
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al listar tablas", e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG, "Método onUpgrade llamado. Version antigua: " + oldVersion + ", Nueva versión: " + newVersion);

            try {
                // Iniciar transacción
                db.beginTransaction();

                // Verificar si la tabla de tokens existe
                Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                    new String[]{TABLE_NAME_TOKENS});

                boolean tablaTokensExiste = (cursor != null && cursor.moveToFirst());
                if (cursor != null) cursor.close();

                // Si la tabla de tokens existe, intentar migrar datos
                if (tablaTokensExiste) {
                    // Crear tabla temporal con nuevo esquema
                    db.execSQL("CREATE TABLE tokens_temp (" +
                        "id TEXT PRIMARY KEY, " +
                        "token TEXT NOT NULL, " +
                        "estado INTEGER DEFAULT 1 CHECK(estado IN (0, 1)), " +
                        "numero TEXT, " +
                        "created_at TEXT NOT NULL)");

                    // Copiar datos existentes
                    db.execSQL("INSERT INTO tokens_temp (id, token, estado, created_at) " +
                        "SELECT id, token, estado, created_at FROM " + TABLE_NAME_TOKENS);

                    // Eliminar tabla antigua
                    db.execSQL("DROP TABLE " + TABLE_NAME_TOKENS);

                    // Renombrar tabla temporal
                    db.execSQL("ALTER TABLE tokens_temp RENAME TO " + TABLE_NAME_TOKENS);
                }

                // Eliminar tablas existentes
                db.execSQL("DROP TABLE IF EXISTS votos");
                db.execSQL("DROP TABLE IF EXISTS configuracion");

                // Recrear todas las tablas
                onCreate(db);

                db.setTransactionSuccessful();
                Log.d(TAG, "Base de datos actualizada exitosamente");
            } catch (Exception e) {
                Log.e(TAG, "Error durante la actualización de la base de datos", e);

                // Log de detalles del error
                if (e.getMessage() != null) {
                    Log.e(TAG, "Mensaje de error: " + e.getMessage());
                }
            } finally {
                db.endTransaction();
            }
        }
    }

    // Método para limpiar recursos
    public void limpiar() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    // Método para limpiar IPs utilizadas (opcional)
    public void limpiarIpsUtilizadas() {
        ipUtilizadas.clear();
    }

    // Método para obtener lista de IPs utilizadas
    public List<String> getIpsUtilizadas() {
        return new ArrayList<>(ipUtilizadas);
    }

    // Método para obtener la última IP conocida
    public String getUltimaIPConocida() {
        return ultimaIPConocida.isEmpty() ? "IP no disponible" : ultimaIPConocida;
    }

    // Método para obtener estadísticas de envíos
    public JSONObject obtenerEstadisticasEnvios() {
        try {
            JSONObject estadisticas = new JSONObject();
            estadisticas.put("total_envios", totalEnvios);
            estadisticas.put("envios_exitosos", enviosExitosos);
            estadisticas.put("envios_con_error", enviosConError);
            return estadisticas;
        } catch (JSONException e) {
            Log.e(TAG, "Error al crear JSON de estadísticas", e);
            return new JSONObject();
        }
    }

    // Método para reiniciar contadores
    public void reiniciarContadores() {
        totalEnvios = 0;
        enviosExitosos = 0;
        enviosConError = 0;
    }

    // Método para guardar la ciudad seleccionada en SQLite
    public void guardarCiudadSeleccionada(String ciudad) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            // Borrar configuración anterior
            db.delete("configuracion", "clave = ?", new String[]{"ciudad_seleccionada"});

            // Insertar nueva configuración
            ContentValues valores = new ContentValues();
            valores.put("clave", "ciudad_seleccionada");
            valores.put("valor", ciudad);

            long resultado = db.insert("configuracion", null, valores);

            if (resultado != -1) {
                Log.d(TAG, "Ciudad guardada en SQLite: " + ciudad);
            } else {
                Log.e(TAG, "Error al guardar ciudad en SQLite");
            }
        } catch (Exception e) {
            Log.e(TAG, "Excepción al guardar ciudad", e);
        } finally {
            db.close();
        }
    }

    // Método para recuperar la ciudad seleccionada desde SQLite
    private String recuperarCiudadSeleccionada() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            // Consultar la ciudad guardada
            cursor = db.query("configuracion",
                new String[]{"valor"},
                "clave = ?",
                new String[]{"ciudad_seleccionada"},
                null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                String ciudadGuardada = cursor.getString(0);
                Log.d(TAG, "Ciudad recuperada de SQLite: " + ciudadGuardada);
                return ciudadGuardada;
            }
        } catch (Exception e) {
            Log.e(TAG, "Excepción al recuperar ciudad", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        // Si no hay ciudad guardada, devolver "Aleatorio"
        return "Aleatorio";
    }

    // Método para establecer la ciudad seleccionada
    public void setCiudadSeleccionada(String ciudad) {
        // Imprimir todos los nombres de ciudades disponibles
        Log.d(TAG, "Ciudades disponibles: " + CIUDADES_BOLIVIA.keySet());

        // Validar que la ciudad exista en el mapa o sea "Aleatorio"
        if (ciudad == null || ciudad.trim().isEmpty()) {
            ciudad = "Aleatorio";
        }

        // Verificar si la ciudad existe en el mapa o es "Aleatorio"
        if (!ciudad.equals("Aleatorio") && !CIUDADES_BOLIVIA.containsKey(ciudad)) {
            Log.w(TAG, "Ciudad no encontrada: " + ciudad + ". Usando Aleatorio.");
            ciudad = "Aleatorio";
        }

        // Recuperar la ciudad guardada actualmente
        String ciudadGuardada = recuperarCiudadSeleccionada();

        // Guardar en SQLite solo si es diferente de la ciudad ya guardada
        if (!ciudad.equals(ciudadGuardada)) {
            guardarCiudadSeleccionada(ciudad);
            Log.d(TAG, "Ciudad guardada en SQLite: " + ciudad);
        } else {
            Log.d(TAG, "Ciudad no modificada: " + ciudad);
        }

        // Establecer como ciudad actual
        this.ciudadSeleccionada = ciudad;

        Log.d(TAG, "Ciudad establecida: " + ciudad);
    }

    // Método para obtener la ciudad seleccionada
    public String getCiudadSeleccionada() {
        // Primero intentar recuperar de la variable actual
        if (ciudadSeleccionada != null && !ciudadSeleccionada.equals("Aleatorio")) {
            return ciudadSeleccionada;
        }

        // Si no hay ciudad en la variable, recuperar de SQLite
        return recuperarCiudadSeleccionada();
    }

    // Método para obtener lista de ciudades
    public String[] getListaCiudades() {
        // Agregar opción de aleatorio al inicio
        String[] ciudades = new String[CIUDADES_BOLIVIA.size() + 1];
        ciudades[0] = "Aleatorio";

        // Copiar nombres de ciudades
        int index = 1;
        for (String ciudad : CIUDADES_BOLIVIA.keySet()) {
            ciudades[index++] = ciudad;
        }

        return ciudades;
    }

    // Método para obtener la ciudad seleccionada desde el Spinner
    private String obtenerCiudadDesdeSpinner() {
        try {
            // Primero recuperar ciudad desde SQLite
            String ciudadGuardada = recuperarCiudadSeleccionada();

            // Si la ciudad guardada existe y no es "Aleatorio", usarla
            if (ciudadGuardada != null && !ciudadGuardada.equals("Aleatorio")) {
                Log.d(TAG, "Ciudad recuperada de SQLite: " + ciudadGuardada);
                return ciudadGuardada;
            }

            // Si no hay ciudad en SQLite, intentar obtener desde el Spinner
            if (mainActivity != null) {
                Spinner spinnerCiudad = mainActivity.getSpinnerCiudad();

                if (spinnerCiudad != null) {
                    Object selectedItem = spinnerCiudad.getSelectedItem();

                    if (selectedItem != null) {
                        String ciudad = selectedItem.toString();
                        Log.d(TAG, "Ciudad obtenida desde Spinner: " + ciudad);

                        // NO guardar automáticamente la ciudad del Spinner
                        return ciudad;
                    }
                }
            }

            // Si no se puede obtener, usar "Aleatorio"
            Log.d(TAG, "No se pudo obtener ciudad, usando Aleatorio");
            return "Aleatorio";

        } catch (Exception e) {
            Log.e(TAG, "Excepción al obtener ciudad", e);
            return "Aleatorio";
        }
    }

    // Método para notificar respuesta del servidor
    private void notificarRespuestaServidor(String respuesta) {
        try {
            // Crear un intent explícito para enviar el broadcast
            Intent intent = new Intent(ACTION_RESPUESTA_SERVIDOR);
            intent.putExtra("respuesta_servidor", respuesta);

            // Intentar enviar broadcast usando el contexto
            if (context != null) {
                context.sendBroadcast(intent);
                Log.d(TAG, "Broadcast de respuesta del servidor enviado: " + respuesta);
            } else {
                Log.e(TAG, "Contexto es nulo, no se puede enviar broadcast");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al enviar broadcast de respuesta del servidor", e);
        }
    }

    // Método para establecer el listener de número de WhatsApp
    public void setNumeroWhatsappListener(NumeroWhatsappListener listener) {
        this.numeroWhatsappListener = listener;
    }

    // Método para guardar el ID de dispositivo
    public void guardarIdDispositivo(String deviceId) {
        // Validar y formatear el ID de dispositivo
        String deviceIdFormateado = formatearIdDispositivo(deviceId);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            // Borrar configuración anterior
            db.delete("configuracion", "clave = ?", new String[]{"device_id"});

            // Insertar nueva configuración
            ContentValues valores = new ContentValues();
            valores.put("clave", "device_id");
            valores.put("valor", deviceIdFormateado);

            long resultado = db.insert("configuracion", null, valores);

            if (resultado != -1) {
                Log.d(TAG, "ID de dispositivo guardado en SQLite: " + deviceIdFormateado);
            } else {
                Log.e(TAG, "Error al guardar ID de dispositivo en SQLite");
            }
        } catch (Exception e) {
            Log.e(TAG, "Excepción al guardar ID de dispositivo", e);
        } finally {
            db.close();
        }
    }

    // Método para formatear y validar el ID de dispositivo
    private String formatearIdDispositivo(String deviceId) {
        // Si el ID está vacío, generar uno por defecto
        if (deviceId == null || deviceId.trim().isEmpty()) {
            return generarIdDispositivo();
        }

        // Eliminar espacios al inicio y al final
        deviceId = deviceId.trim();

        // Reemplazar espacios internos con guiones
        deviceId = deviceId.replaceAll("\\s+", "-");

        // Convertir a mayúsculas
        deviceId = deviceId.toUpperCase();

        // Limitar la longitud
        if (deviceId.length() > 50) {
            deviceId = deviceId.substring(0, 50);
        }

        // Agregar prefijo si no lo tiene
        if (!deviceId.startsWith("DEV-")) {
            deviceId = "DEV-" + deviceId;
        }

        return deviceId;
    }

    // Método para generar un ID de dispositivo único
    private String generarIdDispositivo() {
        // Generar un ID único basado en la marca de tiempo y un número aleatorio
        long timestamp = System.currentTimeMillis();
        int randomSuffix = new Random().nextInt(10000);

        // Formato: TIMESTAMP-RANDOM
        String deviceId = "DEV-" + timestamp + "-" + randomSuffix;

        Log.d(TAG, "ID de dispositivo generado por defecto: " + deviceId);

        return deviceId;
    }

    // Método para recuperar el ID de dispositivo desde SQLite
    private String recuperarIdDispositivo() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            // Consultar el ID de dispositivo guardado
            cursor = db.query("configuracion",
                new String[]{"valor"},
                "clave = ?",
                new String[]{"device_id"},
                null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                String deviceId = cursor.getString(0);
                Log.d(TAG, "ID de dispositivo recuperado de SQLite: " + deviceId);
                return deviceId;
            }
        } catch (Exception e) {
            Log.e(TAG, "Excepción al recuperar ID de dispositivo", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        // Si no hay ID de dispositivo guardado, generar uno por defecto
        String deviceIdPorDefecto = formatearIdDispositivo(null);

        return deviceIdPorDefecto;
    }

    // Método para obtener el ID de dispositivo
    public String getIdDispositivo() {
        return recuperarIdDispositivo();
    }

    // Método para guardar configuración de número de WhatsApp personalizado
    public void guardarConfiguracionWhatsapp(boolean habilitado, String numeroPersonalizado) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            // Borrar configuraciones anteriores
            db.delete("configuracion", "clave IN (?, ?)",
                new String[]{"whatsapp_personalizado_habilitado", "whatsapp_personalizado_numero"});

            // Insertar nueva configuración de habilitación
            ContentValues valoresHabilitacion = new ContentValues();
            valoresHabilitacion.put("clave", "whatsapp_personalizado_habilitado");
            valoresHabilitacion.put("valor", habilitado ? "1" : "0");
            long resultadoHabilitacion = db.insert("configuracion", null, valoresHabilitacion);

            // Log detallado del guardado de habilitación
            Log.d(TAG, "Guardando configuración de WhatsApp - Habilitado: " + habilitado +
                  ", Resultado inserción: " + resultadoHabilitacion);

            // Si hay número personalizado, guardarlo
            if (habilitado && numeroPersonalizado != null && !numeroPersonalizado.trim().isEmpty()) {
                ContentValues valoresNumero = new ContentValues();
                valoresNumero.put("clave", "whatsapp_personalizado_numero");
                valoresNumero.put("valor", numeroPersonalizado.trim());
                long resultadoNumero = db.insert("configuracion", null, valoresNumero);

                Log.d(TAG, "Guardando número personalizado: " + numeroPersonalizado +
                      ", Resultado inserción: " + resultadoNumero);
            }

            // Log final de la configuración
            Log.d(TAG, "Configuración de WhatsApp guardada. Habilitado: " + habilitado +
                  ", Número: " + (numeroPersonalizado != null ? numeroPersonalizado : "N/A"));
        } catch (Exception e) {
            Log.e(TAG, "Error al guardar configuración de WhatsApp personalizado", e);
        } finally {
            db.close();
        }
    }

    // Método público para recuperar la configuración de WhatsApp
    public WhatsappConfiguracion recuperarConfiguracionWhatsapp() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursorHabilitacion = null;
        Cursor cursorNumero = null;
        WhatsappConfiguracion configuracion = new WhatsappConfiguracion();

        try {
            // Log de inicio de recuperación
            Log.d(TAG, "===== RECUPERANDO CONFIGURACIÓN DE WHATSAPP =====");

            // Verificar si está habilitado
            cursorHabilitacion = db.query("configuracion",
                new String[]{"valor"},
                "clave = ?",
                new String[]{"whatsapp_personalizado_habilitado"},
                null, null, null);

            if (cursorHabilitacion != null && cursorHabilitacion.moveToFirst()) {
                String valorHabilitacion = cursorHabilitacion.getString(0);
                configuracion.habilitado = valorHabilitacion.equals("1");
                Log.d(TAG, "Valor de habilitación recuperado: " + valorHabilitacion);
                Log.d(TAG, "Configuración de WhatsApp - Habilitado: " + configuracion.habilitado);
            } else {
                Log.d(TAG, "No se encontró configuración de habilitación de WhatsApp");
                // Por defecto, deshabilitar
                configuracion.habilitado = false;
            }

            // Si está habilitado, recuperar el número
            if (configuracion.habilitado) {
                cursorNumero = db.query("configuracion",
                    new String[]{"valor"},
                    "clave = ?",
                    new String[]{"whatsapp_personalizado_numero"},
                    null, null, null);

                if (cursorNumero != null && cursorNumero.moveToFirst()) {
                    configuracion.numero = cursorNumero.getString(0);
                    Log.d(TAG, "Número de WhatsApp personalizado recuperado: " + configuracion.numero);
                } else {
                    Log.d(TAG, "No se encontró número de WhatsApp personalizado");
                    // Establecer número como nulo
                    configuracion.numero = null;
                }
            }

            Log.d(TAG, "Configuración final - Habilitado: " + configuracion.habilitado +
                  ", Número: " + (configuracion.numero != null ? configuracion.numero : "N/A"));
            Log.d(TAG, "===== FIN RECUPERACIÓN DE CONFIGURACIÓN DE WHATSAPP =====");

            return configuracion;
        } catch (Exception e) {
            Log.e(TAG, "Error al recuperar configuración de WhatsApp personalizado", e);
            return new WhatsappConfiguracion();
        } finally {
            if (cursorHabilitacion != null) cursorHabilitacion.close();
            if (cursorNumero != null) cursorNumero.close();
            db.close();
        }
    }

    // Clase interna para almacenar configuración de WhatsApp (ya existente)
    public static class WhatsappConfiguracion {
        public boolean habilitado = false;
        public String numero = null;
    }

    // Método público para establecer número de WhatsApp personalizado
    public void establecerNumeroWhatsappPersonalizado(String numero) {
        // Validar formato de número
        if (numero == null || numero.trim().isEmpty()) {
            // Deshabilitar número personalizado
            guardarConfiguracionWhatsapp(false, null);
            return;
        }

        // Guardar configuración con el número tal cual se ingresó
        guardarConfiguracionWhatsapp(true, numero.trim());
    }

    // Método para formatear número de WhatsApp
    private String formatearNumeroWhatsapp(String numero) {
        // Eliminar espacios y caracteres no numéricos
        String numeroLimpio = numero.replaceAll("[^0-9+]", "");

        // Si no comienza con +591, agregar prefijo
        if (!numeroLimpio.startsWith("+591")) {
            // Si comienza con 591, agregar +
            if (numeroLimpio.startsWith("591")) {
                numeroLimpio = "+" + numeroLimpio;
            }
            // Si comienza con 6 o 7, agregar prefijo completo
            else if (numeroLimpio.startsWith("6") || numeroLimpio.startsWith("7")) {
                numeroLimpio = "+591" + numeroLimpio;
            }
            // Si no tiene prefijo, asumir que es un número local
            else {
                numeroLimpio = "+591" + numeroLimpio;
            }
        }

        // Validar longitud
        if (numeroLimpio.length() < 12 || numeroLimpio.length() > 14) {
            throw new IllegalArgumentException("Número de WhatsApp inválido");
        }

        return numeroLimpio;
    }

    // Método para desactivar número personalizado
    public void desactivarNumeroWhatsappPersonalizado() {
        guardarConfiguracionWhatsapp(false, null);
    }

    // Método para cargar datos desde JSON
    private JSONArray cargarDatosJSON() {
        try {
            // Obtener AssetManager
            AssetManager assetManager = context.getAssets();

            // Abrir el archivo JSON desde assets
            InputStream inputStream = assetManager.open("datos.json");

            // Leer el contenido del archivo
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();
            inputStream.close();

            // Parsear y devolver el JSON
            return new JSONArray(jsonString.toString());
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error al cargar datos desde JSON", e);
            return new JSONArray();
        }
    }

    private byte[] descomprimirBrotli(byte[] datosComprimidos) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(datosComprimidos);
             BrotliInputStream brotliInputStream = new BrotliInputStream(bis);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = brotliInputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            return buffer.toByteArray();
        }
    }
}