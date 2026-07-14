# Integración de Plataformas — Webpay Plus

API REST desarrollada con Java 21, Spring Boot, WebClient, Spring Data JPA y MySQL.

## Configuración

Define estas variables de entorno antes de ejecutar:

```text
DB_URL=jdbc:mysql://localhost:3306/ecommerce?createDatabaseIfNotExist=true&serverTimezone=America/Santiago
DB_USERNAME=root
DB_PASSWORD=
WEBPAY_COMMERCE_CODE=<codigo-de-comercio-de-integracion>
WEBPAY_API_KEY=<api-key-de-integracion>
```

No subas credenciales reales al repositorio.

## Ejecutar

```bash
mvn spring-boot:run
```

La API usa el puerto `8081`.

## Crear transacción

```http
GET /api/webpay/crear?monto=15000&urlRetorno=http://localhost:8081/api/webpay/confirmar
```

El backend persiste la transacción como `CREADO`, llama a Webpay Plus mediante WebClient y retorna el token y la URL entregados por Transbank.

## Confirmar pago

```http
POST /api/webpay/confirmar?token_ws=<token-webpay>
```

El backend confirma el token directamente con Webpay. El estado queda `PAGADO` solo cuando `response_code` es `0` y `status` es `AUTHORIZED`; en otro caso queda `NO PAGADO`.

## Estados persistidos

- `CREADO`
- `PAGADO`
- `NO PAGADO`

Hibernate crea o actualiza la tabla `transacciones` al iniciar la aplicación.
