/**
 * Capa de acceso a datos: interfaces que extienden JpaRepository.
 * <ul>
 *   <li>DuenoRepository       - findByDni(String dni)                         (Franco)</li>
 *   <li>MascotaRepository     - findByDuenoDni(String dni)                    (Franco)</li>
 *   <li>VeterinarioRepository - findByColegiatura(String colegiatura)         (Julio)</li>
 *   <li>CitaRepository        - findByVeterinarioIdAndEstado(Long, EstadoCita) (Julio)</li>
 * </ul>
 */
package pe.edu.uns.patitas.repository;
