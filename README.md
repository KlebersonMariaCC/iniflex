# Iniflex  
Projeto de teste prático para vaga de **Desenvolvedor Júnior** da **Projedata**.  

## 📌 Desafio  

O teste consistia em implementar um sistema em Java para manipulação de uma lista de funcionários a partir de requisitos pré-definidos, como inserção, remoção, ordenação, agrupamento e cálculos de salários.  
### Pré-Requisitos de instalação
- **Java 17+**
- **maven 3.9+**
- **SQlite 3**

### Requisitos principais
1. Classe `Pessoa` com atributos `nome (String)` e `dataNascimento (LocalDate)`.  
2. Classe `Funcionario`, estendendo `Pessoa`, com atributos `salario (BigDecimal)` e `funcao (String)`.  
3. Classe `Principal` para executar:  
   - Inserção e remoção de funcionários.  
   - Impressão formatada de dados (datas e valores numéricos).  
   - Atualização de salários (+10%).  
   - Agrupamento por função em `Map<String, List<Funcionario>>`.  
   - Filtros de aniversariantes.  
   - Identificação do funcionário mais velho.  
   - Ordenação alfabética.  
   - Soma dos salários.  
   - Cálculo de quantos salários mínimos cada funcionário recebe.  

A tabela utilizada como base está representada abaixo:  

![Tabela de funcionários](dados.png)  

##  Tecnologias  

- **SQLite + JDBC** para gerenciamento do Banco de Dados 
- **Maven**, para facilitar a organização, o gerenciamento de dependências e a execução.  



## ▶️ Execução  

Clone o repositório e execute os comandos:  

```bash
# compilar e empacotar
mvn clean package  

# executar
java -jar ./target/iniflex-1.0-SNAPSHOT-jar-with-dependencies.jar 

```
## Próximos Passos

- Implementar um CRUD completo para funcionários.

- Adicionar testes automatizados.
