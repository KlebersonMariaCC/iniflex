package br.com.projedata;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class Util {
    
    static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    static final DecimalFormat FORMATO_SALARIO = new DecimalFormat("#,##0.00");

}
