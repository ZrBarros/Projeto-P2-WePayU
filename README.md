# WePayU

Sistema de folha de pagamento desenvolvido em Java para a disciplina de Programação 2. O projeto implementa as histórias 1 a 10.

## Funcionalidades

O sistema permite cadastrar, remover e alterar empregados horistas, assalariados e comissionados. Também é possível lançar cartões de ponto, resultados de vendas e taxas de serviço do sindicato.

Ao rodar a folha de pagamento, o programa verifica quais empregados recebem na data informada e calcula salário, horas extras, comissões e descontos. O pagamento pode ser feito em mãos, pelos correios ou por depósito bancário.

Cada empregado possui uma agenda de pagamento. As agendas padrão são semanal para horistas, mensal para assalariados e quinzenal para comissionados. Também é possível criar agendas mensais ou semanais personalizadas e atribuí-las a qualquer empregado.

As operações que alteram o sistema, inclusive a criação e a atribuição de agendas, podem ser desfeitas e refeitas com `undo` e `redo`. Os dados também são salvos em arquivo quando o sistema é encerrado.

## Organização do código

A `Facade` é apenas o ponto de entrada usado pelo EasyAccept e encaminha as chamadas para o sistema. As responsabilidades foram divididas nos seguintes packages:

- `model`: empregados, lançamentos e filiação sindical;
- `model.agenda`: agendas semanais e mensais, fábrica validada e catálogo de agendas disponíveis;
- `model.pagamento`: formas de pagamento;
- `service`: cadastro de empregados, lançamentos, sindicato, folha de pagamento, estado e histórico;
- `persistence`: leitura e gravação do estado;
- `exceptions`: exceções específicas do sistema;
- `util`: conversão e formatação de valores.

`Empregado` é uma classe abstrata. As regras de horistas, assalariados e comissionados ficam nas respectivas subclasses. As agendas e as formas de pagamento também são representadas por objetos próprios.

`SistemaFolha` coordena os casos de uso e o histórico das alterações. Cadastro, lançamentos, sindicato, geração do relatório e persistência ficam separados para facilitar a manutenção.

Os valores monetários usam `BigDecimal` e as datas usam `LocalDate`. A persistência é feita no arquivo `wepayu-state.bin`.

## Como testar

Dentro da pasta do projeto, execute:

```sh
mkdir -p build/classes
javac -encoding UTF-8 -cp lib/easyaccept.jar -d build/classes $(find src -name '*.java')
java -cp build/classes:lib/easyaccept.jar Main
```

Sem argumentos, a `Main` executa todos os testes das histórias 1 a 10. Os testes de persistência são executados automaticamente logo depois da história correspondente.

