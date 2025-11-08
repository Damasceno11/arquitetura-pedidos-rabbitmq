CREATE SEQUENCE pedido_seq START 1 INCREMENT 50;
CREATE SEQUENCE pedido_item_seq START 1 INCREMENT 50;

CREATE TABLE tb_pedidos (
                            id BIGINT NOT NULL,
                            cliente_id BIGINT,
                            data_criacao TIMESTAMP WITHOUT TIME ZONE,
                            status VARCHAR(50) NOT NULL,
                            total DECIMAL(19, 2),
                            PRIMARY KEY (id)
);

CREATE TABLE tb_pedido_itens (
                                 id BIGINT NOT NULL,
                                 pedido_id BIGINT NOT NULL,
                                 produto_id VARCHAR(255),
                                 descricao VARCHAR(255),
                                 quantidade INTEGER NOT NULL,
                                 preco_unitario DECIMAL(19, 2),
                                 PRIMARY KEY (id)
);

ALTER TABLE tb_pedido_itens
    ADD CONSTRAINT fk_pedido_id
        FOREIGN KEY (pedido_id)
            REFERENCES tb_pedidos (id)
            ON DELETE CASCADE;