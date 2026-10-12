package com.reservacanchas.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud de registro de un nuevo jugador")
public record RegistroRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100)
        @Schema(description = "Nombre de pila", example = "Lionel", requiredMode = Schema.RequiredMode.REQUIRED)
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100)
        @Schema(description = "Apellido", example = "Messi", requiredMode = Schema.RequiredMode.REQUIRED)
        String apellido,

        @Size(max = 20)
        @Schema(description = "Telefono de contacto", example = "987654321")
        String telefono,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo debe ser valido")
        @Size(max = 150)
        @Schema(description = "Correo electronico unico", example = "lionel@futbol.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
        @Schema(description = "Contrasena (minimo 6 caracteres)", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
        String password,

        @Size(max = 50)
        @Schema(description = "Apodo deportivo", example = "La Pulga")
        String apodo,

        @Size(max = 500)
        @Schema(description = "URL opcional de foto de perfil", example = "https://cdn.fotos.com/pulga.jpg")
        String fotoUrl,

        @Size(max = 50)
        @Schema(description = "Posicion de juego preferida", example = "Delantero")
        String posicionPreferida
) {
}
