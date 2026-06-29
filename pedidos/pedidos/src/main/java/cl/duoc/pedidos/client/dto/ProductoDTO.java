package cl.duoc.pedidos.client.dto;

import lombok.Data;

@Data
public class ProductoDTO {
    private Long idProducto;
    private String nombre;
    private Integer precio;
    private Boolean activo;
}