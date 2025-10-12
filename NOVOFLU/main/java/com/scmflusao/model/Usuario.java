package com.scmflusao.model;

public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private Perfil perfil;
    private boolean ativo;

    public Usuario(Long id, String nome, String email, String senha, Perfil perfil, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
        this.ativo = ativo;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public Perfil getPerfil() { return perfil; }
    public boolean isAtivo() { return ativo; }

    @Override
    public String toString() {
        return nome + " (" + perfil + ")";
    }
}