CREATE TABLE pessoa (
    id SERIAL PRIMARY KEY,
    nome TEXT NOT NULL,
    data_nascimento DATE NOT NULL
);
CREATE TABLE funcionario (
    id SERIAL PRIMARY KEY,
    nome TEXT NOT NULL,
    data_nascimento DATE NOT NULL,
    funcao TEXT NOT NULL,
    salario NUMERIC NOT NULL
);
