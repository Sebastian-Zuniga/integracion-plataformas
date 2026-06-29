package com.pcfactory.ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

@Entity
@Table(name="clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Integer rut;

    @Column(unique = false, nullable = false)
    private Integer digitoVerificador;

    @Column(unique = false, nullable = false)
    private String nombres;

    @Column(unique = false, nullable = false)
    private String apellidos;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = true)
    private Integer telefono;
}
