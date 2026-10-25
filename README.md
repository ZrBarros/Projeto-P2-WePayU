# WePayU

Sistema de folha de pagamento desenvolvido em Java para a disciplina de Programação 2. O projeto implementa as histórias 1 a 8 do primeiro milestone.

## Funcionalidades

O sistema permite cadastrar, remover e alterar empregados horistas, assalariados e comissionados. Também é possível lançar cartões de ponto, resultados de vendas e taxas de serviço do sindicato.

Ao rodar a folha de pagamento, o programa verifica quais empregados recebem na data informada e calcula salário, horas extras, comissões e descontos. O pagamento pode ser feito em mãos, pelos correios ou por depósito bancário.

As operações das histórias 1 a 7 podem ser desfeitas e refeitas com `undo` e `redo`. Os dados também são salvos em arquivo quando o sistema é encerrado.

## Organização do código

A `Facade` é apenas o ponto de entrada usado pelo EasyAccept e encaminha as chamadas para o sistema. As responsabilidades foram divididas nos seguintes packages:

- `model`: empregados, lançamentos e filiação sindical;
- `model.pagamento`: formas de pagamento;
- `service`: cadastro de empregados, lançamentos, sindicato, folha de pagamento, estado e histórico;
- `persistence`: leitura e gravação do estado;
- `exceptions`: exceções específicas do sistema;
- `util`: conversão e formatação de valores.

`Empregado` é uma classe abstrata. As regras próprias dos empregados horistas, assalariados e comissionados ficam nas respectivas subclasses. Dessa forma, os cálculos e lançamentos usam polimorfismo e não dependem de verificações com `instanceof`.

`SistemaFolha` apenas coordena os casos de uso e o histórico das alterações. As regras de cadastro, os lançamentos e as operações sindicais ficam em serviços separados; a geração do relatório e a persistência também possuem classes próprias. As coleções internas não são expostas para alteração externa.

Os valores monetários usam `BigDecimal` e as datas usam `LocalDate`. A persistência é feita no arquivo `wepayu-state.bin`.

## Como testar

Dentro da pasta do projeto, execute:

```sh
mkdir -p build/classes
javac -encoding UTF-8 -cp lib/easyaccept.jar -d build/classes $(find src -name '*.java')
java -cp build/classes:lib/easyaccept.jar Main
```

Sem argumentos, a `Main` executa todos os testes das histórias 1 a 8. Os testes de persistência (`us1_1.txt` até `us6_1.txt`) são executados automaticamente logo depois da história correspondente.

As histórias 9 e 10, relacionadas às agendas personalizadas de pagamento, não fazem parte desta versão.
