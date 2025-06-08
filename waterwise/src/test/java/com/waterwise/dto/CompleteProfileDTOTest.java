package com.waterwise.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CompleteProfileDTO - Testes Essenciais")
class CompleteProfileDTOTest {

    private Validator validator;
    private CompleteProfileDTO dto;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // DTO válido para testes base
        dto = new CompleteProfileDTO();
        dto.setNomeCompleto("João da Silva Santos");
        dto.setCpfCnpj("123.456.789-01");
        dto.setTelefone("(11) 99999-9999");
    }

    @Test
    @DisplayName("Deve validar DTO completamente válido")
    void deveValidarDtoValido() {
        // When
        Set<ConstraintViolation<CompleteProfileDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar nome completo nulo")
    void deveRejeitarNomeNulo() {
        // Given
        dto.setNomeCompleto(null);

        // When
        Set<ConstraintViolation<CompleteProfileDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nomeCompleto")));
    }

    @Test
    @DisplayName("Deve rejeitar nome completo vazio")
    void deveRejeitarNomeVazio() {
        // Given
        dto.setNomeCompleto("");

        // When
        Set<ConstraintViolation<CompleteProfileDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar CPF/CNPJ nulo")
    void deveRejeitarCpfCnpjNulo() {
        // Given
        dto.setCpfCnpj(null);

        // When
        Set<ConstraintViolation<CompleteProfileDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cpfCnpj")));
    }

    @Test
    @DisplayName("Deve rejeitar telefone nulo")
    void deveRejeitarTelefoneNulo() {
        // Given
        dto.setTelefone(null);

        // When
        Set<ConstraintViolation<CompleteProfileDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("telefone")));
    }

    @Test
    @DisplayName("Deve rejeitar CPF com formato inválido")
    void deveRejeitarCpfFormatoInvalido() {
        // Given
        dto.setCpfCnpj("12345678901"); // Sem pontuação

        // When
        Set<ConstraintViolation<CompleteProfileDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("cpfCnpj")));
    }

    @Test
    @DisplayName("Deve rejeitar telefone com formato inválido")
    void deveRejeitarTelefoneFormatoInvalido() {
        // Given
        dto.setTelefone("11999999999"); // Sem formatação

        // When
        Set<ConstraintViolation<CompleteProfileDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("telefone")));
    }

    @Test
    @DisplayName("Deve rejeitar nome muito longo")
    void deveRejeitarNomeMuitoLongo() {
        // Given - Nome com 101 caracteres (máximo é 100)
        String nomeLongo = "a".repeat(101);
        dto.setNomeCompleto(nomeLongo);

        // When
        Set<ConstraintViolation<CompleteProfileDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nomeCompleto")));
    }

    @Test
    @DisplayName("Deve aceitar CNPJ válido")
    void deveAceitarCnpjValido() {
        // Given
        dto.setCpfCnpj("12.345.678/0001-90");

        // When
        Set<ConstraintViolation<CompleteProfileDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve validar múltiplos erros simultaneamente")
    void deveValidarMultiplosErros() {
        // Given - DTO com múltiplos problemas
        dto.setNomeCompleto(""); // Vazio
        dto.setCpfCnpj("123456789"); // Formato inválido
        dto.setTelefone("11999999999"); // Formato inválido

        // When
        Set<ConstraintViolation<CompleteProfileDTO>> violations = validator.validate(dto);

        // Then
        assertEquals(3, violations.size());
    }
}