CREATE TABLE pessoa (
    id INTEGER PRIMARY KEY,
    nome TEXT NOT NULL,
    data_nascimento DATE NOT NULL
);
CREATE TABLE funcionario (
    id INTEGER PRIMARY KEY,
    nome TEXT NOT NULL,
    data_nascimento DATE NOT NULL,
    funcao TEXT NOT NULL,
    salario NUMERIC NOT NULL
);
