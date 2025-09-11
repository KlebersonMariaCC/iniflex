package br.com.projedata;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Funcionario extends Pessoa {
    
    BigDecimal salario;
    String funcao;

    static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    static final DecimalFormat FORMATO_SALARIO = new DecimalFormat("#,##0.00");
    
    public Funcionario(String nome, LocalDate dataNascimento,BigDecimal salario , String funcao ) {
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
        return "Funcionario [id=" + id + ", nome=" + nome + ", funcao=" + funcao + ", dataNascimento=" + dataNascimento.format(FORMATO_DATA) + ", salario="
                + FORMATO_SALARIO.format(salario) + "]";
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
