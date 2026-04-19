# Alke Wallet

Aplicación de billetera virtual Android desarrollada como entrega del **Módulo 6 (ABP)**.

Implementa autenticación local, transferencias entre usuarios, gestión de contactos, historial de transacciones e imagen de perfil con Picasso — todo con arquitectura **MVVM**, **Room** y **Coroutines**.

---

## Tecnologías utilizadas

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| Arquitectura | MVVM (ViewModel + StateFlow) |
| Base de datos local | Room 2.7 |
| Red | Retrofit 2.11 + OkHttp logging |
| Imágenes | Picasso 2.8 |
| Concurrencia | Kotlin Coroutines 1.9 |
| Inyección de dependencias | Manual (ViewModelFactory) |
| Pruebas | JUnit 4 + Mockito-Kotlin 5.4 + coroutines-test |
| Build | Gradle KTS + KSP |

---

## Arquitectura MVVM

```
UI (Activity)
  │  observa StateFlow
  ▼
ViewModel  ──►  Repository  ──►  DAO (Room)  ──►  SQLite
                           ──►  RetrofitClient (API)
```

- **Activities**: solo manejan UI y eventos de usuario.
- **ViewModels**: contienen la lógica de negocio, exponen `StateFlow` y nunca referencian `Context`.
- **Repositories**: abstraen el origen de datos (Room o Retrofit).
- **Entities/DAOs**: definen el esquema de la BD y las consultas.

---

## Estructura del proyecto

```text
app/src/main/java/com/cjgr/awandroide/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── UserEntity.kt           # id, nombre, correo, password, saldo, fotoPerfil, token
│   │   ├── UserDao.kt
│   │   ├── TransactionEntity.kt
│   │   ├── TransactionDao.kt
│   │   ├── ContactEntity.kt
│   │   └── ContactDao.kt
│   └── repository/
│       ├── UserRepository.kt
│       ├── TransactionRepository.kt
│       └── ContactRepository.kt
├── network/
│   ├── RetrofitClient.kt
│   └── ApiService.kt
├── ui/
│   ├── viewmodel/
│   │   ├── AuthViewModel.kt        # login, registrar, cargarUsuario, actualizarPerfil, actualizarFotoPerfil
│   │   ├── TransactionViewModel.kt # ingresarDinero, realizarTransferencia, cargarTransacciones
│   │   ├── ContactViewModel.kt
│   │   ├── ViewModelFactory.kt
│   │   └── AuthState / TransactionState (sealed classes)
│   ├── HomeActivity.kt             # Header clickeable: foto y saludo navegan a ProfileActivity
│   ├── HomeEmptyActivity.kt
│   ├── LoginActivity.kt
│   ├── SignupActivity.kt
│   ├── ProfileActivity.kt          # Edición inline de nombre/correo + foto con Picasso
│   ├── SendMoneyActivity.kt
│   ├── RequestMoneyActivity.kt
│   └── ContactsActivity.kt
app/src/test/java/com/cjgr/awandroide/
├── AuthViewModelTest.kt            # 10 pruebas
├── TransactionViewModelTest.kt     # 8 pruebas
├── UserRepositoryTest.kt           # 8 pruebas
└── HomeNavigationTest.kt           # 8 pruebas — carga de datos en header + refresco al volver
```

---

## Flujo de navegación

```mermaid
flowchart TD
    A[SplashActivity] --> B[WelcomeActivity]
    B --> C[LoginActivity]
    B --> D[SignupActivity]
    C --> E[HomeActivity]
    D --> C
    E -- toca foto o saludo --> H[ProfileActivity]
    E --> F[SendMoneyActivity]
    E --> G[RequestMoneyActivity]
    E --> I[HomeEmptyActivity]
    E --> J[ContactsActivity]
    F --> E
    G --> E
    H --> E
    I --> E
```

---

## Funcionalidades implementadas

### Autenticación
- Registro con validación de correo duplicado.
- Login con credenciales almacenadas en Room.
- Cierre de sesión limpia el estado del ViewModel.

### Header de HomeActivity — acceso rápido al perfil
- La **foto de perfil** (esquina superior izquierda) y el **saludo "Hola, [nombre]!"** son elementos clickeables con efecto ripple.
- Tocar cualquiera de los dos navega directamente a `ProfileActivity` pasando el `userId`.
- El saludo muestra un ícono de lápiz (`ic_edit`) como indicador visual de que es editable.
- Al volver desde `ProfileActivity`, `onResume()` recarga automáticamente el nombre y la foto actualizada.
- La foto del header se carga con Picasso desde la URI guardada en Room.

### Perfil de usuario
- Edición inline de nombre y correo con validaciones (vacío, formato, correo en uso).
- Lanzador de galería con `ACTION_OPEN_DOCUMENT` + `takePersistableUriPermission`.
- Foto cargada con Picasso con `placeholder` y `error` apuntando a `ic_profile`.

### Transferencias
- Ingreso de dinero propio.
- Transferencia a cualquier correo; si el destinatario existe en Room se registra su ingreso automáticamente.
- Validación de saldo suficiente antes de procesar.

### Destinatario en SendMoney
- Seleccionar un contacto guardado, agregar uno nuevo o ingresar correo manualmente.

### Historial
- Transacciones ordenadas por fecha descendente.
- Balance calculado como suma de todos los montos del historial.

---

## Pruebas unitarias

Las pruebas usan **JUnit 4**, **Mockito-Kotlin** y **kotlinx-coroutines-test** con `UnconfinedTestDispatcher`. No requieren emulador ni dispositivo físico.

### Ejecutar todas las pruebas

```bash
./gradlew test
```

O desde Android Studio: clic derecho sobre la carpeta `test` → **Run Tests**.

---

### `AuthViewModelTest` — 10 pruebas

| # | Prueba | Resultado esperado |
|---|---|---|
| 1 | `login correcto` | `AuthState.LoginSuccess` con el usuario |
| 2 | `login credenciales incorrectas` | `AuthState.Error` |
| 3 | `registrar usuario nuevo` | `AuthState.RegisterSuccess` con id=5 |
| 4 | `registrar correo duplicado` | `AuthState.Error` con mensaje "registrado" |
| 5 | `actualizarPerfil datos válidos` | `AuthState.ProfileUpdated` con nombre actualizado |
| 6 | `actualizarPerfil nombre vacío` | `AuthState.Error` |
| 7 | `actualizarPerfil correo inválido` | `AuthState.Error` |
| 8 | `actualizarPerfil correo en uso por otro` | `AuthState.Error` con mensaje "uso" |
| 9 | `actualizarFotoPerfil` | `AuthState.PhotoUpdated` con URI guardada |
| 10 | `cerrarSesion` | `currentUser = null`, estado `Idle` |

---

### `TransactionViewModelTest` — 8 pruebas

| # | Prueba | Resultado esperado |
|---|---|---|
| 1 | `ingresarDinero usuario válido` | `Success` o `Idle` |
| 2 | `ingresarDinero usuario inexistente` | `Error("Usuario no encontrado")` |
| 3 | `realizarTransferencia exitosa` | `Success` o `Idle` |
| 4 | `realizarTransferencia saldo insuficiente` | `Error("Saldo insuficiente")` |
| 5 | `realizarTransferencia remitente inexistente` | `Error("Usuario no encontrado")` |
| 6 | `realizarTransferencia registra ingreso destinatario local` | `enviarTransaccion` con `userId=2, tipo="ingreso"` |
| 7 | `cargarTransacciones ordena por fecha desc` | Lista con `18/04 > 10/04 > 01/04` |
| 8 | `resetState devuelve Idle` | `TransactionState.Idle` |

---

### `UserRepositoryTest` — 8 pruebas

| # | Prueba | Resultado esperado |
|---|---|---|
| 1 | `registrar llama insertUser` | Retorna id, verifica llamada al DAO |
| 2 | `buscarPorCorreo existente` | Retorna `UserEntity` con nombre correcto |
| 3 | `buscarPorCorreo inexistente` | Retorna `null` |
| 4 | `actualizarSaldo llama updateSaldo` | Verifica parámetros en DAO |
| 5 | `actualizarPerfil llama updatePerfil` | Verifica parámetros en DAO |
| 6 | `actualizarFotoPerfil llama updateFotoPerfil` | Verifica URI en DAO |
| 7 | `login credenciales correctas` | Retorna `UserEntity` |
| 8 | `login credenciales incorrectas` | Retorna `null` |

---

### `HomeNavigationTest` — 8 pruebas

Verifica la carga de datos que `HomeActivity` muestra en el header y que el estado se refresca correctamente al volver desde `ProfileActivity`.

| # | Prueba | Resultado esperado |
|---|---|---|
| 1 | `cargarUsuario expone nombre y saldo` | `currentUser.nombre = "Carlo"`, `saldo = 250.0` |
| 2 | `cargarUsuario id inválido` | `currentUser = null` |
| 3 | `cargarUsuario expone fotoPerfil cuando existe` | URI devuelta en `currentUser.fotoPerfil` |
| 4 | `cargarUsuario expone fotoPerfil null sin foto` | `currentUser.fotoPerfil = null` |
| 5 | `despues de actualizarPerfil cargarUsuario devuelve nombre nuevo` | `nombre = "Carlos Nuevo"` |
| 6 | `despues de actualizarFotoPerfil cargarUsuario devuelve nueva URI` | `fotoPerfil = "content://img/nueva"` |
| 7 | `resetState despues de edicion deja estado Idle` | `AuthState.Idle` |
| 8 | (reservado para extensión futura) | — |

---

## Cómo ejecutar el proyecto

1. Clonar el repositorio y cambiarse al branch de entrega:
   ```bash
   git clone https://github.com/Carl0gonzalez/AlkewalletEvaluacionGeneral.git
   cd AlkewalletEvaluacionGeneral
   git checkout entregamodulo6
   ```
2. Abrir en **Android Studio** y esperar la sincronización de Gradle.
3. Seleccionar un AVD con API 24+ y pulsar **Run**.
4. Para ejecutar las pruebas unitarias:
   ```bash
   ./gradlew test
   ```

---

## Autor
Carlo J. González Rojas  
Proyecto desarrollado como parte del programa de formación Android — **Módulo 6 · Alke Wallet**.
