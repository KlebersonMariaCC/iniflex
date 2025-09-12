package br.com.projedata;

import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class App 
{
    static final String DB_URL="jdbc:sqlite:app.db";
    static final String INSERT_FILE = "dados.csv";
    static final String SCHEMA_FILE = "schema.sql";

    static final DateTimeFormatter FORMATO_DATA_BD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main( String[] args )
    {
        criarTabelas();
        System.out.println( "Hello World!" );

        //inserirDados();

        removerFuncionarioPorNome("João");

        recuperaFuncionarios();
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
                        System.out.println("Funcionário " + "nome" + "removido com sucesso.");
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
}
