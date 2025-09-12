package br.com.projedata;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Pessoa {
    Integer id;
    String nome;
    LocalDate dataNascimento;

    

    public Pessoa(String nome, LocalDate dataNascimento) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
    }

    

    public Integer getId() {
        return id;
    }



    public void setId(Integer id) {
        this.id = id;
    }



    public String getNome() {
        return nome;
    }


    public void setNome(String nome) {
        this.nome = nome;
    }


    public LocalDate getdataNascimento() {
        return dataNascimento;
    }


    public void setdataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }


    


    @Override
    public String toString() {
        
        return "Pessoa [id=" + id + ", nome=" + nome + ", dataNascimento=" + dataNascimento.format(Util.FORMATO_DATA) + "]";
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((nome == null) ? 0 : nome.hashCode());
        result = prime * result + ((dataNascimento == null) ? 0 : dataNascimento.hashCode());
        return result;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pessoa other = (Pessoa) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (nome == null) {
            if (other.nome != null)
                return false;
        } else if (!nome.equals(other.nome))
            return false;
        if (dataNascimento == null) {
            if (other.dataNascimento != null)
                return false;
        } else if (!dataNascimento.equals(other.dataNascimento))
            return false;
        return true;
    }


   

    

    

}
