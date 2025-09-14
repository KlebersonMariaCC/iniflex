package br.com.projedata;

import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class App 
{
    static final String DB_URL="jdbc:sqlite:app.db";
    static final String INSERT_FILE = "dados.csv";
    static final String SCHEMA_FILE = "schema.sql";

    static final DateTimeFormatter FORMATO_DATA_BD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static final BigDecimal SALARIO_MINIMO = new BigDecimal(1212.00);

    public static void main( String[] args ){

        Scanner sc = new Scanner(System.in);

        System.out.println("Bem Vindo\n");

        while(true) {
            
            System.out.println("\nEscolha uma opção:\n");
            System.out.println("1 - Criar tabelas");
            System.out.println("2 - Inserir dados do csv");
            System.out.println("3 - Listar todos os funcionários");
            System.out.println("4 - Remover funcionário por nome");
            System.out.println("5 - Aumentar salários");
            System.out.println("6 - Agrupar funcionários por função");
            System.out.println("7 - Listar funcionários por mês de aniversário");
            System.out.println("8 - Funcionário de maior idade");
            System.out.println("9 - Listar funcionários ordenados por nome");
            System.out.println("10 - Custo total com salários");
            System.out.println("11 - Quantidade de salários de cada funcionário");
            System.out.println("0 - Sair\n");
            System.out.print(">>> ");
             try {
                int opcao = Integer.parseInt(sc.nextLine());
                if (opcao == 0) {
                    System.out.println("Saindo...");
                    sc.close();
                    break;
                }
                switch (opcao) {
                case 1:

                    criarTabelas();
                    
                    break;
                case 2:
                    inserirDados();
                    break;
                case 3:
                    recuperaFuncionarios();
                    break;
                case 4:
                    System.out.println("Digite o nome do funcionário a ser removido:");
                    String nome = sc.nextLine();
                    removerFuncionarioPorNome(nome);
                    break;
                case 5:
                    System.out.println("Digite o percentual de aumento "+
                    "em decimal entre 0 e 1 (ex: 0.10 para 10%):");
                    try {
                        BigDecimal percentualAumento = new BigDecimal(sc.nextLine());
                        atualizarSalarios(percentualAumento);
                    } catch (NumberFormatException e) {
                        System.err.println("Percentual inválido, tente novamente.");
                    }
                    break;
                case 6:
                    agruparFuncionariosPorFuncao();
                    break;
                case 7:
                    System.out.println("Digite o mês (1-12) para listar os aniversariantes: ");
                    try {
                        int mes = Integer.parseInt(sc.nextLine());
                        recuperarFuncionariosPorMesAniversario(mes);
                    } catch (NumberFormatException e) {
                        System.err.println("Mês inválido, tente novamente.");
                    }
                    break;
                case 8:
                    recuperarFuncionarioMaiorIdade();
                    break;
                case 9:
                    recuperarFuncionariosOrdenados();
                    break;
                case 10:
                    calcularCustoSalarios();
                    break;
                case 11:
                    qtdSalariosFuncionarios();
                    break;
                default:
                    System.out.println("Opção inválida, tente novamente.");
                    break;
                }
                
            }catch (Exception e) {
                System.err.println("Opção inválida, tente novamente.");
            }
        }                
        
    }
    
    public static void criarTabelas()  {
        try (Connection conn = DriverManager.getConnection(DB_URL);
         ) {
            if (conn != null) {
                try (BufferedReader reader = new BufferedReader(new FileReader(SCHEMA_FILE));
                ){
                    
                    StringBuilder sb = new StringBuilder();
                    String linha;

                    while ((linha = reader.readLine()) != null) {
                        String linhaSemEspaco = linha.trim();
                        if (linhaSemEspaco.isEmpty() || linhaSemEspaco.startsWith("--")) {
                            continue; // Ignora linhas vazias e comentários
                        }
                        sb.append(linhaSemEspaco).append(" "); // Adiciona a linha ao StringBuilder
                        if (linhaSemEspaco.endsWith(";")) {
                            sb.append(linhaSemEspaco, 0, linhaSemEspaco.length() - 1); // Remove o ponto e vírgula
                            String sql = sb.toString();
                            try (Statement stmt = conn.createStatement();){
                                stmt.execute(sql);
                            } catch (SQLException e) {
                                lidarComErro(e, "Erro ao executar a instrução SQL: " + sql);
                            }
                            
                            sb.setLength(0); // Limpa o StringBuilder para a próxima instrução
                        } 
                    }
                    System.out.println("Tabelas criadas com sucesso.");
                } catch (IOException e) {
                    lidarComErro(e, "Erro ao ler o arquivo schema.sql: ");
                }
                 
            }
            
        } catch (SQLException e) {
            lidarComErro(e, "Erro ao conectar com o banco de dados: ");
        }
    }   

    public static void inserirDados() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
         ) {
            if (conn != null) {
                try(BufferedReader reader = new BufferedReader(new FileReader("dados.csv"))) {
                    String linha;

                    //pula a linha de cabeçalho do csv
                    reader.readLine();
                    
                    try(PreparedStatement pstmt = conn .prepareStatement("INSERT INTO funcionario (nome, data_nascimento, salario, funcao) VALUES (?, ?, ?, ?)");){
                        while((linha = reader.readLine()) != null) {
                                String linhaSemEspaco = linha.trim();
                                String[] values = linhaSemEspaco.split(",");
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                                Funcionario funcionario = new Funcionario(values[0],
                                LocalDate.parse(values[1], formatter), 
                                new BigDecimal(values[2]), values[3]);

                                pstmt.setString(1, funcionario.getNome());
                                pstmt.setString(2, funcionario.getdataNascimento().toString());
                                pstmt.setBigDecimal(3, funcionario.getSalario());
                                pstmt.setString(4, funcionario.getFuncao());
                                pstmt.execute();
                        }
                        System.out.println("Dados inseridos com sucesso.");
                    } catch (SQLException e) {
                        lidarComErro(e, "Erro ao inserir dados no banco: ");
                            
                    }
                } catch ( IOException e) {
                    lidarComErro(e, "Erro ao ler o arquivo dados.csv: ");
                }
            }
            
        } catch (SQLException e) {
            lidarComErro(e, "Erro ao conectar com o banco de dados: ");
        }
    }

    public static void lidarComErro (Exception e, String mensagem ){
        System.err.println(mensagem + e.getMessage());
    }

    public static void removerFuncionarioPorNome(String nome){
        String sql ="DELETE FROM funcionario WHERE nome = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);) {
            if (conn != null) {
                try(PreparedStatement pstmt = conn.prepareStatement(sql);){
                    pstmt.setString(1, nome);
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Funcionário " + nome + " removido com sucesso.");
                    } else {
                        System.out.println("Nenhum funcionário encontrado com o nome fornecido.");
                    }
                } catch (SQLException e) {
                    lidarComErro(e, "Erro ao remover funcionário: ");
                }
            }
        } catch (SQLException e) {
            lidarComErro(e, "Erro ao conectar com o banco de dados: ");
        }
    } public static void recuperaFuncionarios(){
        String sql = "SELECT id, nome, data_nascimento, salario, funcao FROM funcionario";
        try (Connection conn = DriverManager.getConnection(DB_URL);) {
            if (conn != null) {
                try(PreparedStatement pstmt = conn.prepareStatement(sql);){
                    var rs = pstmt.executeQuery();
                    while(rs.next()){
                        Integer id = rs.getInt("id");
                        String nome = rs.getString("nome");
                        LocalDate dataNascimento = LocalDate.parse(rs.getString("data_nascimento"), FORMATO_DATA_BD);
                        BigDecimal salario = rs.getBigDecimal("salario");
                        String funcao = rs.getString("funcao");

                        Funcionario funcionario = new Funcionario(nome, dataNascimento, salario, funcao);
                        funcionario.setId(id);
                        System.out.println(funcionario);
                    }
                } catch (SQLException e) {
                    lidarComErro(e, "Erro ao recuperar funcionários: ");
                }
            }
        } catch (SQLException e) {
            lidarComErro(e, "Erro ao conectar com o banco de dados: ");
        }
    }

     public static void atualizarSalarios(BigDecimal percentualAumento) {
        if (percentualAumento.signum() <= 0 
        || percentualAumento.compareTo(BigDecimal.ONE) > 0) {
            System.out.println("Percentual de aumento inválido. Deve ser maior que 0 e menor ou igual a 1.");
            return;
            
        }
        try (Connection conn = DriverManager.getConnection(DB_URL);) {
            if (conn != null) {
                int rowsAffected = 0;
                conn.setAutoCommit(false); 
                List<Funcionario> funcionarios = new ArrayList<>();
                String sql = "SELECT id, nome, data_nascimento, salario, funcao FROM funcionario";
                try(PreparedStatement pstmt = conn.prepareStatement(sql);){
                    var rs = pstmt.executeQuery();
                    while(rs.next()){
                        Integer id = rs.getInt("id");
                        String nome = rs.getString("nome");
                        LocalDate dataNascimento = LocalDate.parse(rs.getString("data_nascimento"), FORMATO_DATA_BD);
                        BigDecimal salario = rs.getBigDecimal("salario").multiply(BigDecimal.ONE.add(percentualAumento));
                        String funcao = rs.getString("funcao");
                        Funcionario funcionario = new Funcionario(nome, dataNascimento, salario, funcao);
                        funcionario.setId(id);
                        funcionarios.add(funcionario);
                        
                    }
                    
                    String updateSql = "UPDATE funcionario SET salario = ? WHERE id = ?";

                    try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateSql)) {
                        for (Funcionario funcionario : funcionarios) {
                        
                        
                            pstmtUpdate.setBigDecimal(1, funcionario.getSalario());
                            pstmtUpdate.setInt(2, funcionario.getId());
                            pstmtUpdate.addBatch();
                        }

                        rowsAffected = IntStream.of(pstmtUpdate.executeBatch()).sum();
                         
                    } catch(SQLException e) {
                        conn.rollback(); 
                        lidarComErro(e, "Erro ao atualizar salários: ");
                        return;
                    }

                    conn.commit();

                    System.out.println("Salários atualizados para " + rowsAffected + " funcionários.");
                } catch (SQLException e) {
                    lidarComErro(e, "Erro ao atualizar salários: ");
                }
            }
        } catch (SQLException e) {
            lidarComErro(e, "Erro ao conectar com o banco de dados: ");
        }
    }

    public static void agruparFuncionariosPorFuncao () {

        try(Connection conn = DriverManager.getConnection(DB_URL);) {
            if (conn != null) {
                List<Funcionario> funcionarios = new ArrayList<>();
                String sql = "SELECT id, nome, data_nascimento, salario, funcao FROM funcionario";
                    try(PreparedStatement pstmt = conn.prepareStatement(sql);){
                    var rs = pstmt.executeQuery();
                    while(rs.next()){
                        Integer id = rs.getInt("id");
                        String nome = rs.getString("nome");
                        LocalDate dataNascimento = LocalDate.parse(rs.getString("data_nascimento"), FORMATO_DATA_BD);
                        BigDecimal salario = rs.getBigDecimal("salario");
                        String funcao = rs.getString("funcao");

                        Funcionario funcionario = new Funcionario(nome, dataNascimento, salario, funcao);
                        funcionario.setId(id);
                    funcionarios.add(funcionario);
                    }
               } catch (SQLException e) {
                lidarComErro(e, "Erro ao agrupar funcionários por função: ");
               }

                Map<String,List<Funcionario>> funcionariosAgrupados = funcionarios.stream()
                .collect(Collectors.groupingBy(Funcionario::getFuncao, TreeMap::new, 
                Collectors.collectingAndThen(Collectors.toList(), 
                lista -> {
                    lista.sort(Comparator.comparing(Funcionario::getNome));
                    return lista;
                })));
                
                System.out.println("Funcionários agrupados por função:");
                funcionariosAgrupados.forEach((funcao, listaDeFuncionarios) -> {
                    System.out.println("- " + funcao + ":");
                    listaDeFuncionarios.forEach(funcionario -> System.out.println("  " + funcionario));
                });
            }
            
        } catch (SQLException e) {
            lidarComErro(e, "Erro ao conectar com o banco de dados: ");
        }
    
    }

    public static void recuperarFuncionariosPorMesAniversario(int mes) {
        if (mes < 1 || mes > 12) {
            System.out.println("Mês inválido. Deve ser entre 1 e 12.");
            return;
        }

        String sql = "SELECT id, nome, data_nascimento, salario, funcao FROM funcionario WHERE strftime('%m', data_nascimento) = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);) {
            if (conn != null) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql);) {
                    pstmt.setString(1, String.format("%02d", mes));
                    var rs = pstmt.executeQuery();
                    List<Funcionario> funcionarios = new ArrayList<>();
                    while (rs.next()) {
                        Integer id = rs.getInt("id");
                        String nome = rs.getString("nome");
                        LocalDate dataNascimento = LocalDate.parse(rs.getString("data_nascimento"), FORMATO_DATA_BD);
                        BigDecimal salario = rs.getBigDecimal("salario");
                        String funcao = rs.getString("funcao");

                        Funcionario funcionario = new Funcionario(nome, dataNascimento, salario, funcao);
                        funcionario.setId(id);
                        funcionarios.add(funcionario);
                    }

                    if (funcionarios.isEmpty()) {
                        System.out.println("Nenhum funcionário encontrado com aniversário no mês " + mes + ".");
                    } else {
                        System.out.println("Funcionários com aniversário no mês " + mes + ":");
                        funcionarios.forEach(System.out::println);
                    }
                } catch (SQLException e) {
                    lidarComErro(e, "Erro ao recuperar funcionários por mês de aniversário: ");
                }
            }
        } catch (SQLException e) {
            lidarComErro(e, "Erro ao conectar com o banco de dados: ");
        }
    }

    public static void recuperarFuncionarioMaiorIdade() {
        try(Connection conn = DriverManager.getConnection(DB_URL);) {
            if (conn != null) {
                String sql = "SELECT nome, data_nascimento FROM funcionario ORDER BY data_nascimento ASC LIMIT 1";

                try (PreparedStatement pstmt = conn.prepareStatement(sql);){
                    var rs = pstmt.executeQuery();
                    while(rs.next()){
                        String nome = rs.getString("nome");
                        LocalDate dataNascimento = LocalDate.parse(rs.getString("data_nascimento"),FORMATO_DATA_BD);
                        Period periodo  = Period.between(dataNascimento, LocalDate.now());
                        int idade =  periodo.getYears();
                        System.out.println("-- Funcionário  de Maior idade --\n" +
                        "Nome: " + nome + "\nData de Nascimento: " + dataNascimento.format(Util.FORMATO_DATA) +
                        "\nIdade: " + idade + " anos");
                    }
                }catch (SQLException e){
                    lidarComErro(e, "Erro ao recuperar funcionário de maior idade: ");
                }
            }
        } catch (SQLException e ){
            lidarComErro(e, "Erro ao conectar com o banco de dados: ");
        }
    }

    public static void recuperarFuncionariosOrdenados() {
        try (Connection conn = DriverManager.getConnection(DB_URL);){
            if (conn != null){
                String sql = "SELECT id, nome, data_nascimento, salario, funcao FROM funcionario ORDER BY nome ASC";
                 try(PreparedStatement pstmt = conn.prepareStatement(sql);){
                    var rs = pstmt.executeQuery();
                    while(rs.next()){
                        Integer id = rs.getInt("id");
                        String nome = rs.getString("nome");
                        LocalDate dataNascimento = LocalDate.parse(rs.getString("data_nascimento"), FORMATO_DATA_BD);
                        BigDecimal salario = rs.getBigDecimal("salario");
                        String funcao = rs.getString("funcao");
                        
                        Funcionario funcionario = new Funcionario(nome, dataNascimento, salario, funcao);
                        funcionario.setId(id);
                        System.out.println(funcionario);
                    }
                 } catch (SQLException e ){
                    lidarComErro(e, "Erro ao recuperar funcionários ordenados: ");
                 }
            }
        } catch(SQLException e){
            lidarComErro(e, "Erro ao conectar com o banco de dados: ");
        }
    }
    
    public static void calcularCustoSalarios() {
        try (Connection conn = DriverManager.getConnection(DB_URL);){
            String sql = "SELECT SUM(salario) AS custo_total FROM funcionario";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);){
                var rs = pstmt.executeQuery();
                if (rs.next()) {
                    BigDecimal custoTotal = rs.getBigDecimal("custo_total");
                    System.out.println("Custo total com salários: " + 
                    Util.FORMATO_SALARIO.format(custoTotal));
                } else {
                    System.out.println("Não foi possível calcular o custo total com salários.");
                }
            } catch (SQLException e) {
                lidarComErro(e, "Erro ao calcular custo total com salários: ");
            }
        } catch (SQLException e) {
           lidarComErro(e, "Erro ao conectar com o banco de dados: ");
        }
    }
     public static void qtdSalariosFuncionarios(){
        try (Connection conn = DriverManager.getConnection(DB_URL);){ 
            String sql = "SELECT id, nome, salario FROM funcionario";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);){
                var rs = pstmt.executeQuery();
                while(rs.next()){
                    Integer id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    BigDecimal salario = rs.getBigDecimal("salario");
                    BigDecimal qtdSalarios = salario.divide(SALARIO_MINIMO,2,RoundingMode.HALF_UP);
                    System.out.println("Funcionário [id=" + id + ", nome=" + nome +
                    ", quantidade de salários= " + qtdSalarios + "]");
                }
            } catch (SQLException e) {
                lidarComErro(e, "Erro ao calcular quantidade de salários: ");
            }
        } catch (SQLException e) {
            lidarComErro(e, "Erro ao conectar com o banco de dados: ");
        }
    }

}
