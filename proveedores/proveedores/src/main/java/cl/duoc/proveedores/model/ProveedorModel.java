package cl.duoc.proveedores.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "proveedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProveedorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Long idProveedor;

    @Column(nullable = false, unique = true, length = 120)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String rubro;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(length = 255)
    private String direccion;

    @Column(nullable = false)
    private Boolean activo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();

        if (this.activo == null) {
            this.activo = true;
        }
    }
}