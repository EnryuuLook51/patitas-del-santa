package pe.edu.uns.patitas.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.uns.patitas.exception.RecursoNoEncontradoException;
import pe.edu.uns.patitas.exception.ReglaNegocioException;
import pe.edu.uns.patitas.model.Dueno;
import pe.edu.uns.patitas.model.Especie;
import pe.edu.uns.patitas.model.Mascota;
import pe.edu.uns.patitas.model.Sexo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class DuenoMascotaServiceTest {

    @Autowired DuenoService duenoService;
    @Autowired MascotaService mascotaService;

    private Dueno nuevoDueno(String dni) {
        Dueno d = new Dueno();
        d.setDni(dni);
        d.setNombres("Carmen");
        d.setApellidos("Ríos");
        return d;
    }

    private Mascota nuevaMascota(String nombre, Long duenoId) {
        Mascota m = new Mascota();
        m.setNombre(nombre);
        m.setEspecie(Especie.GATO);
        m.setSexo(Sexo.HEMBRA);
        if (duenoId != null) {
            Dueno ref = new Dueno();
            ref.setId(duenoId);
            m.setDueno(ref);
        }
        return m;
    }

    @Test
    void noPermiteDniRepetido() {
        duenoService.crear(nuevoDueno("11111111"));
        assertThatThrownBy(() -> duenoService.crear(nuevoDueno("11111111")))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    void actualizarConSuMismoDniEsValido() {
        Dueno d = duenoService.crear(nuevoDueno("22222222"));
        Dueno datos = nuevoDueno("22222222");
        datos.setNombres("Carmen Rosa");
        assertThat(duenoService.actualizar(d.getId(), datos).getNombres()).isEqualTo("Carmen Rosa");
    }

    @Test
    void mascotaSinDuenoOConDuenoInexistenteFalla() {
        assertThatThrownBy(() -> mascotaService.crear(nuevaMascota("Michi", null)))
                .isInstanceOf(ReglaNegocioException.class);
        assertThatThrownBy(() -> mascotaService.crear(nuevaMascota("Michi", 999L)))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void buscaMascotasPorDniDelDueno() {
        Dueno d = duenoService.crear(nuevoDueno("33333333"));
        mascotaService.crear(nuevaMascota("Tom", d.getId()));
        mascotaService.crear(nuevaMascota("Luna", d.getId()));

        assertThat(mascotaService.listarPorDniDueno("33333333"))
                .extracting(Mascota::getNombre)
                .containsExactly("Luna", "Tom");
        assertThatThrownBy(() -> mascotaService.listarPorDniDueno("00000000"))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
