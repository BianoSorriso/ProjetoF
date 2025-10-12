Coloque aqui o driver do H2 para que o projeto compile no Eclipse.

Passos:
- Baixe o arquivo `h2-2.2.224.jar` do site oficial: https://h2database.com/html/download.html
- Copie o `h2-2.2.224.jar` para esta pasta `lib/`.
- Garanta que o arquivo aparece no projeto e no `.classpath` como `lib/h2-2.2.224.jar`.

Observações:
- O projeto foi configurado para usar H2 em modo compatível com MySQL (`MODE=MySQL`).
- O banco ficará no arquivo `data/scmflusao` (será criado automaticamente ao rodar o app).
- Os scripts `main/resources/database/schema.sql` e `main/resources/populate_brasil.sql` são executados na inicialização.