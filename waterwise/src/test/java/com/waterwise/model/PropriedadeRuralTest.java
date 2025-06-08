package com.waterwise.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PropriedadeRural - Testes de Validação")
class PropriedadeRuralTest {

    private Validator validator;
    private PropriedadeRural propriedade;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Propriedade válida para testes base
        propriedade = new PropriedadeRural();
        propriedade.setNomePropriedade("Fazenda São João");
        propriedade.setLatitude(new BigDecimal("-23.550520"));  // São Paulo
        propriedade.setLongitude(new BigDecimal("-46.633309")); // São Paulo
        propriedade.setAreaHectares(new BigDecimal("50.5"));
        propriedade.setIdProdutor(1L);
        propriedade.setIdNivelDegradacao(1L);
    }

    @Test
    @DisplayName("Deve validar propriedade completamente válida")
    void deveValidarPropriedadeValida() {
        // When
        Set<ConstraintViolation<PropriedadeRural>> violations = validator.validate(propriedade);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar nome da propriedade nulo")
    void deveRejeitarNomeNulo() {
        // Given
        propriedade.setNomePropriedade(null);

        // When
        Set<ConstraintViolation<PropriedadeRural>> violations = validator.validate(propriedade);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nomePropriedade")));
    }

    @Test
    @DisplayName("Deve rejeitar nome da propriedade vazio")
    void deveRejeitarNomeVazio() {
        // Given
        propriedade.setNomePropriedade("");

        // When
        Set<ConstraintViolation<PropriedadeRural>> violations = validator.validate(propriedade);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nomePropriedade")));
    }

    @Test
    @DisplayName("Deve rejeitar nome muito longo")
    void deveRejeitarNomeMuitoLongo() {
        // Given - Nome com 101 caracteres (máximo é 100)
        String nomeLongo = "a".repeat(101);
        propriedade.setNomePropriedade(nomeLongo);

        // When
        Set<ConstraintViolation<PropriedadeRural>> violations = validator.validate(propriedade);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nomePropriedade")));
    }

    @Test
    @DisplayName("Deve rejeitar latitude inválida (muito alta)")
    void deveRejeitarLatitudeInvalida() {
        // Given - Latitude maior que 90
        propriedade.setLatitude(new BigDecimal("95.0"));

        // When
        Set<ConstraintViolation<PropriedadeRural>> violations = validator.validate(propriedade);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("latitude")));
    }

    @Test
    @DisplayName("Deve rejeitar longitude inválida (muito baixa)")
    void deveRejeitarLongitudeInvalida() {
        // Given - Longitude menor que -180
        propriedade.setLongitude(new BigDecimal("-185.0"));

        // When
        Set<ConstraintViolation<PropriedadeRural>> violations = validator.validate(propriedade);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("longitude")));
    }

    @Test
    @DisplayName("Deve rejeitar área muito pequena")
    void deveRejeitarAreaMuitoPequena() {
        // Given - Área menor que 0.1
        propriedade.setAreaHectares(new BigDecimal("0.05"));

        // When
        Set<ConstraintViolation<PropriedadeRural>> violations = validator.validate(propriedade);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("areaHectares")));
    }

    @Test
    @DisplayName("Deve rejeitar coordenadas nulas")
    void deveRejeitarCoordenadasNulas() {
        // Given
        propriedade.setLatitude(null);
        propriedade.setLongitude(null);
        propriedade.setAreaHectares(null);

        // When
        Set<ConstraintViolation<PropriedadeRural>> violations = validator.validate(propriedade);

        // Then
        assertEquals(3, violations.size()); // 3 campos nulos
    }

    @Test
    @DisplayName("Deve aceitar coordenadas nos limites válidos")
    void deveAceitarCoordenadasLimites() {
        // Given - Coordenadas nos limites válidos
        propriedade.setLatitude(new BigDecimal("90.0"));    // Polo Norte
        propriedade.setLongitude(new BigDecimal("-180.0")); // Limite oeste
        propriedade.setAreaHectares(new BigDecimal("0.1")); // Área mínima

        // When
        Set<ConstraintViolation<PropriedadeRural>> violations = validator.validate(propriedade);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve testar métodos auxiliares")
    void deveTestarMetodosAuxiliares() {
        // Given - Propriedade sem sensores

        // When & Then
        assertEquals(0, propriedade.getTotalSensores());
        assertEquals(0, propriedade.getSensoresAtivos());
        assertEquals("N/A", propriedade.getCapacidadeAbsorcaoDescricao());
        assertNotNull(propriedade.getDataCadastro());
    }
}