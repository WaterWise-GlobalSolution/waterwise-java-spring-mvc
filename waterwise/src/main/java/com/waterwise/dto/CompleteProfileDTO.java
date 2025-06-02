package com.waterwise.dto;

import jakarta.validation.constraints.*;

public class CompleteProfileDTO {

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nomeCompleto;

    @NotBlank(message = "CPF/CNPJ é obrigatório")
    @Size(max = 18, message = "CPF/CNPJ deve ter no máximo 18 caracteres")
    @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$|^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$",
            message = "CPF/CNPJ deve estar no formato correto")
    private String cpfCnpj;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres")
    @Pattern(regexp = "^\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}$",
            message = "Telefone deve estar no formato (11) 99999-9999")
    private String telefone;

    // Construtores
    public CompleteProfileDTO() {}

    public CompleteProfileDTO(String nomeCompleto, String cpfCnpj, String telefone) {
        this.nomeCompleto = nomeCompleto;
        this.cpfCnpj = cpfCnpj;
        this.telefone = telefone;
    }

    // Getters e Setters
    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public String toString() {
        return "CompleteProfileDTO{" +
                "nomeCompleto='" + nomeCompleto + '\'' +
                ", cpfCnpj='" + cpfCnpj + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}