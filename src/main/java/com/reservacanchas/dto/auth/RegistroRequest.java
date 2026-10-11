package com.reservacanchas.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100)
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100)
        String apellido,

        @Size(max = 20)
        String telefono,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo debe ser valido")
        @Size(max = 150)
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
        String password,

        @Size(max = 50)
        String apodo,

        @Size(max = 500)
        String fotoUrl,

        @Size(max = 50)
        String posicionPreferida
) {
}
