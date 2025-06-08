package com.waterwise.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "GS_WW_PRODUTOR_RURAL")
public class ProdutorRural {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRODUTOR")
    private Long idProdutor;

    // Removendo @NotBlank para permitir criação via OAuth2 sem validação inicial
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(name = "NOME_COMPLETO", length = 100)
    private String nomeCompleto;

    @Size(max = 18, message = "CPF/CNPJ deve ter no máximo 18 caracteres")
    @Column(name = "CPF_CNPJ", length = 18, unique = true)
    private String cpfCnpj;

    // Mantendo validações do email pois é obrigatório sempre
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    @Column(name = "EMAIL", nullable = false, length = 100, unique = true)
    private String email;

    @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres")
    @Column(name = "TELEFONE", length = 15)
    private String telefone;

    // Senha pode ser nula para usuários OAuth2
    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    @Column(name = "SENHA", length = 100)
    private String senha;

    @Column(name = "DATA_CADASTRO")
    private LocalDateTime dataCadastro;

    // Relacionamentos
    @OneToMany(mappedBy = "produtor", fetch = FetchType.LAZY)
    private List<PropriedadeRural> propriedades;

    // Construtores
    public ProdutorRural() {
        this.dataCadastro = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getIdProdutor() { return idProdutor; }
    public void setIdProdutor(Long idProdutor) { this.idProdutor = idProdutor; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public List<PropriedadeRural> getPropriedades() { return propriedades; }
    public void setPropriedades(List<PropriedadeRural> propriedades) { this.propriedades = propriedades; }

    // Métodos auxiliares
    public boolean isAtivo() {
        return true; // Todos são considerados ativos no novo schema
    }

    public int getTotalPropriedades() {
        return propriedades != null ? propriedades.size() : 0;
    }

    /**
     * Verifica se o perfil está completo (usado para OAuth2)
     */
    public boolean isProfileComplete() {
        return this.cpfCnpj != null && !this.cpfCnpj.trim().isEmpty() &&
                this.telefone != null && !this.telefone.trim().isEmpty() &&
                this.nomeCompleto != null && !this.nomeCompleto.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "ProdutorRural{" +
                "idProdutor=" + idProdutor +
                ", nomeCompleto='" + nomeCompleto + '\'' +
                ", email='" + email + '\'' +
                ", cpfCnpj='" + cpfCnpj + '\'' +
                ", telefone='" + telefone + '\'' +
                ", dataCadastro=" + dataCadastro +
                '}';
    }
}