package pe.edu.uns.patitas.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DuenoMascotaControllerTest {

    @Autowired MockMvc mvc;

    private static final String DUENO = """
            { "dni": "45678912", "nombres": "Carmen", "apellidos": "Ríos Paredes",
              "celular": "987654321", "correo": "carmen@mail.com", "direccion": "Av. Pacífico 123" }
            """;

    private long crearDueno() throws Exception {
        String body = mvc.perform(post("/api/duenos").contentType(MediaType.APPLICATION_JSON).content(DUENO))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();
        return Long.parseLong(body.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    @Test
    void crudCompletoDeDuenoYMascota() throws Exception {
        long duenoId = crearDueno();

        String mascota = """
                { "nombre": "Firulais", "especie": "PERRO", "raza": "Mestizo", "sexo": "MACHO",
                  "fechaNacimiento": "2021-05-10", "peso": 12.5, "dueno": { "id": %d } }
                """.formatted(duenoId);
        mvc.perform(post("/api/mascotas").contentType(MediaType.APPLICATION_JSON).content(mascota))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dueno.dni").value("45678912"));

        mvc.perform(get("/api/mascotas/dueno/45678912"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Firulais"));

        mvc.perform(put("/api/duenos/" + duenoId).contentType(MediaType.APPLICATION_JSON)
                        .content(DUENO.replace("Carmen", "Carmen Rosa")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombres").value("Carmen Rosa"));

        mvc.perform(delete("/api/duenos/" + duenoId)).andExpect(status().isNoContent());
        mvc.perform(get("/api/duenos/" + duenoId)).andExpect(status().isNotFound());
        mvc.perform(get("/api/mascotas/dueno/45678912")).andExpect(status().isNotFound());
    }

    @Test
    void erroresDeValidacion() throws Exception {
        crearDueno();
        // DNI repetido
        mvc.perform(post("/api/duenos").contentType(MediaType.APPLICATION_JSON).content(DUENO))
                .andExpect(status().isBadRequest());
        // DNI inválido (no tiene 8 dígitos)
        mvc.perform(post("/api/duenos").contentType(MediaType.APPLICATION_JSON)
                        .content(DUENO.replace("45678912", "123")))
                .andExpect(status().isBadRequest());
        // Mascota sin dueño
        mvc.perform(post("/api/mascotas").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nombre\": \"Michi\", \"especie\": \"GATO\", \"sexo\": \"HEMBRA\" }"))
                .andExpect(status().isBadRequest());
        // Mascota con dueño inexistente
        mvc.perform(post("/api/mascotas").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nombre\": \"Michi\", \"especie\": \"GATO\", \"sexo\": \"HEMBRA\", \"dueno\": {\"id\": 999} }"))
                .andExpect(status().isNotFound());
    }
}
