package com.pcfactory.ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ventas_productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long ventaId;

    @Column
    private Long productoId;

    @Column
    private Integer cantidad;
}
