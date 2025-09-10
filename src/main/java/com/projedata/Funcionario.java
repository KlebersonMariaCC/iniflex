package com.projedata;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Funcionario extends Pessoa {
    String funcao;
    BigDecimal salario;

    
    public Funcionario(String nome, LocalDate dataNascimento, String funcao, BigDecimal salario) {
        super(nome, dataNascimento);
        this.funcao = funcao;
        this.salario = salario;
    }


    public String getFuncao() {
        return funcao;
    }


    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }


    public BigDecimal getSalario() {
        return salario;
    }


    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }


    @Override
    public String toString() {
        return "Funcionario [nome=" + nome + ", funcao=" + funcao + ", dataNascimento=" + dataNascimento.format(FORMATO_DATA) + ", salario="
                + salario + "]";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((funcao == null) ? 0 : funcao.hashCode());
        result = prime * result + ((salario == null) ? 0 : salario.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Funcionario other = (Funcionario) obj;
        if (funcao == null) {
            if (other.funcao != null)
                return false;
        } else if (!funcao.equals(other.funcao))
            return false;
        if (salario == null) {
            if (other.salario != null)
                return false;
        } else if (!salario.equals(other.salario))
            return false;
        return true;
    }

    

    
    

}
