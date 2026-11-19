# Clínica Veterinaria "Patitas del Santa"

API REST con **Spring Boot 3.5 + JPA + H2**, organizada en 3 capas
(`model` → `repository` → `service` → `controller`).

## Requisitos
- Java 21
- Maven 3.9+

## Ejecutar
```bash
mvn spring-boot:run
```
- API: http://localhost:8080/api/...
- Consola H2: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:patitasdb` · User: `sa` · Password: *(vacío)*

## Estructura
```
src/main/java/pe/edu/uns/patitas/
├── model/        Dueno, Mascota, Veterinario, Cita + enums (Especie, Sexo, EstadoCita)
├── repository/   interfaces JpaRepository + consultas personalizadas
├── service/      lógica de negocio y validaciones
└── controller/   endpoints REST
```
Diagrama de clases: [docs/diagrama-clases.md](docs/diagrama-clases.md)

**Responsabilidades y flujo de trabajo: [CONTRIBUTING.md](CONTRIBUTING.md)**

## Equipo y ramas
| Integrante | Rama | Tarea |
|---|---|---|
| Franco | `feature/dueno-mascota` | Base del proyecto, CRUD Dueño y Mascota |
| Julio | `feature/veterinario-cita` | CRUD Veterinario y Cita + validaciones |
| Ilario | `test/postman` | Colección Postman, capturas y PDF |

## Reglas
- Nadie hace push directo a `main`: todo entra por Pull Request.
- Cambios en `model/`, `pom.xml` o `application.properties` se avisan antes.

## Ejemplo JSON para crear una mascota
```json
{
  "nombre": "Firulais",
  "especie": "PERRO",
  "raza": "Mestizo",
  "sexo": "MACHO",
  "fechaNacimiento": "2021-05-10",
  "peso": 12.5,
  "dueno": { "id": 1 }
}
```
