package pe.edu.uns.patitas.controller;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VeterinarioCitaControllerTest {

    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired EntityManager em;

    long mascotaId, vetId;

    private long idDe(String body) {
        return Long.parseLong(body.replaceAll("^\\{\"id\":(\\d+).*", "$1"));
    }

    private String post(String url, String json, int status) throws Exception {
        return mvc.perform(MockMvcRequestBuildersPost.of(url, json))
                .andExpect(status().is(status)).andReturn().getResponse().getContentAsString();
    }

    @BeforeEach
    void datos() throws Exception {
        long duenoId = idDe(post("/api/duenos",
                "{\"dni\":\"70000001\",\"nombres\":\"Carmen\",\"apellidos\":\"Rios\"}", 201));
        mascotaId = idDe(post("/api/mascotas",
                "{\"nombre\":\"Tom\",\"especie\":\"GATO\",\"sexo\":\"MACHO\",\"dueno\":{\"id\":" + duenoId + "}}", 201));
        vetId = idDe(post("/api/veterinarios",
                "{\"colegiatura\":\"CMVP-900\",\"nombres\":\"Rosa\",\"apellidos\":\"Medina\",\"activo\":true}", 201));
    }

    private String cita(String fecha, String extra) {
        return "{\"fechaHora\":\"" + fecha + "\",\"motivo\":\"Control\"" + extra
                + ",\"mascota\":{\"id\":" + mascotaId + "},\"veterinario\":{\"id\":" + vetId + "}}";
    }

    @Test
    void citaNacePROGRAMADAyRechazaFechaPasadaYCostoNegativo() throws Exception {
        mvc.perform(MockMvcRequestBuildersPost.of("/api/citas", cita("2030-01-01T10:00:00", ",\"estado\":\"ATENDIDA\"")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PROGRAMADA"));
        post("/api/citas", cita("2020-01-01T10:00:00", ""), 400);
        post("/api/citas", cita("2030-01-01T10:00:00", ",\"costo\":-5"), 400);

        mvc.perform(get("/api/citas/veterinario/" + vetId + "/programadas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void sePuedeAtenderUnaCitaCuyaHoraYaPaso() throws Exception {
        long citaId = idDe(post("/api/citas", cita("2030-01-01T10:00:00", ""), 201));
        // Simula que llegó la hora de la cita
        em.flush();
        jdbc.update("UPDATE citas SET fecha_hora = TIMESTAMP '2026-01-01 10:00:00' WHERE id = ?", citaId);
        em.clear();

        mvc.perform(put("/api/citas/" + citaId).contentType(MediaType.APPLICATION_JSON)
                        .content(cita("2026-01-01T10:00:00", ",\"estado\":\"ATENDIDA\",\"diagnostico\":\"Sano\",\"costo\":50")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ATENDIDA"));

        // Reprogramarla a una fecha pasada sí debe fallar
        mvc.perform(put("/api/citas/" + citaId).contentType(MediaType.APPLICATION_JSON)
                        .content(cita("2025-01-01T10:00:00", "")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void noSeEliminaVeterinarioConCitas() throws Exception {
        post("/api/citas", cita("2030-01-01T10:00:00", ""), 201);
        mvc.perform(delete("/api/veterinarios/" + vetId)).andExpect(status().isBadRequest());

        long otroVet = idDe(post("/api/veterinarios",
                "{\"colegiatura\":\"CMVP-901\",\"nombres\":\"Luis\",\"apellidos\":\"Paz\"}", 201));
        mvc.perform(delete("/api/veterinarios/" + otroVet)).andExpect(status().isNoContent());
    }

    /** Atajo para POST con JSON. */
    static class MockMvcRequestBuildersPost {
        static org.springframework.test.web.servlet.RequestBuilder of(String url, String json) {
            return MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON).content(json);
        }
    }
}
