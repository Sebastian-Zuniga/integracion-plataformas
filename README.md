# Integración de Plataformas — Webpay Plus

API REST desarrollada con Java 21, Spring Boot, WebClient, Spring Data JPA y MySQL. El proyecto consume TheMealDB y realiza el flujo de creación y confirmación de pagos con Webpay Plus.

## Requisitos

- Java 21
- Maven
- MySQL
- Credenciales del ambiente de integración de Webpay Plus

## Configuración

La aplicación lee las credenciales y la conexión a la base de datos desde variables de entorno:

```text
DB_URL=jdbc:mysql://localhost:3306/ecommerce?createDatabaseIfNotExist=true&serverTimezone=America/Santiago
DB_USERNAME=root
DB_PASSWORD=
WEBPAY_COMMERCE_CODE=<codigo-de-comercio-de-integracion>
WEBPAY_API_KEY=<api-key-de-integracion>
```

No se deben subir claves reales al repositorio.

## Ejecutar

```bash
mvn spring-boot:run
```

La API se inicia en `http://localhost:8081`.

## Flujo Webpay solicitado por la evaluación

### 1. Crear transacción

```http
GET /api/webpay/crear?monto=15000&urlRetorno=http://localhost:8081/api/webpay/confirmar
```

El backend:

1. genera `buy_order` y `session_id`;
2. persiste la transacción con estado `CREADO`;
3. llama al endpoint de creación de Webpay mediante `WebClient`;
4. guarda el token entregado por Webpay;
5. retorna el token y la URL de pago.

Respuesta esperada:

```json
{
  "token": "token-entregado-por-webpay",
  "url": "url-entregada-por-webpay"
}
```

### 2. Confirmar pago

```http
POST /api/webpay/confirmar?token_ws=<token-entregado-por-webpay>
```

El backend confirma el pago directamente con Webpay usando el token recibido. La transacción cambia a:

- `PAGADO` cuando `response_code` es `0` y el estado es `AUTHORIZED`;
- `NO PAGADO` en cualquier otro resultado.

El estado no se recibe desde el usuario, sino que se obtiene desde la respuesta de Transbank.

## Tabla `transacciones`

Hibernate crea o actualiza automáticamente la tabla al iniciar la aplicación. Se almacenan, entre otros datos:

- orden de compra;
- sesión;
- monto;
- token Webpay;
- estado;
- código de respuesta;
- código de autorización;
- fechas de creación y actualización.

## Endpoints de TheMealDB

- `GET /categories`
- `GET /search?name=...`
- `GET /lookup?id=...`
- `GET /filter?ingredient=...`
