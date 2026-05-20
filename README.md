# Algashop

Projeto de microserviços para uma loja, com foco atual no contexto de pedidos
(`algashop-ordering`). O serviço implementa regras de negócio para clientes,
carrinhos, checkout, compra direta, pedidos, frete e pontos de fidelidade.

## Estrutura do repositório

- `microservices/algashop-ordering`: microserviço principal de ordering.
- `microservices/algashop-docs`: módulo reservado para documentação.
- `docker-compose.yml`: sobe um WireMock da transportadora fictícia RapiDex na
  porta `8780`.
- `etc/wiremock/rapidex.json`: mock da API `POST /api/delivery-cost`, retornando
  custo de frete e prazo estimado.

## Stack

- Java 21
- Spring Boot 3.4.4
- Spring Data JPA
- H2 Database
- Gradle
- Lombok
- ModelMapper
- WireMock

## Arquitetura

O projeto segue uma organização próxima de DDD e arquitetura hexagonal,
separando regras de negócio, casos de uso e detalhes técnicos.

### Camada de domínio

Local: `microservices/algashop-ordering/src/main/java/.../domain/model`

Contém as regras centrais da aplicação, sem depender de detalhes de banco,
HTTP ou frameworks externos.

Principais agregados:

- `Customer`: representa o cliente, seus dados pessoais, endereço, status de
  arquivamento e pontos de fidelidade.
- `ShoppingCart`: representa o carrinho de compras, seus itens, totais e vínculo
  com o cliente.
- `Order`: representa o pedido, itens, cobrança, entrega, pagamento, status e
  datas relevantes.
- `Product`: representação local de um produto consultado no catálogo.

Principais value objects:

- `Money`: valor monetário com escala 2 e arredondamento `HALF_EVEN`.
- `Quantity`: quantidade não negativa.
- `Email`, `Phone`, `Document`, `FullName`, `Address`, `ZipCode`.
- IDs de domínio, como `CustomerId`, `ShoppingCartId`, `OrderId` e
  `OrderItemId`.

Serviços de domínio:

- `CustomerRegistrationService`: registra clientes e valida unicidade de e-mail.
- `CustomerLoyaltyPointsService`: adiciona pontos de fidelidade quando um pedido
  do cliente está pronto.
- `ShoppingService`: inicia um carrinho para um cliente existente, impedindo mais
  de um carrinho ativo por cliente.
- `CheckoutService`: transforma um carrinho válido em pedido e esvazia o
  carrinho.
- `BuyNowService`: cria um pedido direto a partir de um produto, sem passar pelo
  carrinho.

Portas do domínio:

- `Customers`, `ShoppingCarts` e `Orders`: abstrações de repositório.
- `ProductCatalogService`: abstração para consulta ao catálogo de produtos.
- `ShippingCostService`: abstração para cálculo de frete.
- `OriginAddressService`: abstração para obtenção do endereço de origem.

### Camada de aplicação

Local: `microservices/algashop-ordering/src/main/java/.../application`

Orquestra os casos de uso, controla transações e converte dados de entrada para
objetos de domínio.

Funcionalidades de cliente:

- Criar cliente.
- Buscar cliente por ID.
- Arquivar cliente.
- Trocar e-mail.
- Adicionar pontos de fidelidade a partir de um pedido pronto.

Classes principais:

- `CustomerManagementApplicationService`
- `CustomerLoyaltyPointsApplicationService`
- `CustomerInput`, `CustomerOutput`, `CustomerUpdateInput`

Funcionalidades de carrinho:

- Criar carrinho para um cliente.
- Adicionar item ao carrinho.
- Remover item.
- Esvaziar carrinho.
- Excluir carrinho.

Classe principal:

- `ShoppingCartManagementApplicationService`

Funcionalidades de checkout:

- Realizar checkout de um carrinho.
- Calcular frete a partir do CEP de origem e destino.
- Montar dados de cobrança e entrega.
- Criar pedido e esvaziar o carrinho.

Classe principal:

- `CheckoutApplicationService`

Funcionalidade de compra direta:

- Buscar produto.
- Calcular frete.
- Montar cobrança e entrega.
- Criar pedido direto para um item.

Classe principal:

- `BuyNowApplicationService`

Funcionalidades de pedido:

- Cancelar pedido.
- Marcar pedido como pago.
- Marcar pedido como pronto.

Classe principal:

- `OrderManagementApplicationService`

O fluxo de status do pedido é definido em `OrderStatus`:

```text
DRAFT -> PLACED -> PAID -> READY
```

O status `CANCELLED` pode ser aplicado a partir de `DRAFT`, `PLACED`, `PAID` ou
`READY`.

### Camada de infraestrutura

Local: `microservices/algashop-ordering/src/main/java/.../infrastructure`

Contém implementações técnicas das portas do domínio e configurações do Spring.

Persistência:

- Usa Spring Data JPA com H2.
- Ambiente principal usa H2 em arquivo: `jdbc:h2:file:~/ordering`.
- Ambiente de testes usa H2 em memória.
- `ddl-auto` está configurado como `update`.

Entidades persistidas:

- `CustomerPersistenceEntity`: tabela `"customer"`.
- `ShoppingCartPersistenceEntity`: tabela `shopping_cart`.
- `ShoppingCartItemPersistenceEntity`: tabela `shopping_cart_item`.
- `OrderPersistenceEntity`: tabela `"order"`.
- `OrderItemPersistenceEntity`: tabela `order_item`.

Providers de persistência:

- `CustomersPersistenceProvider`: implementa `Customers`.
- `ShoppingCartPersistenceProvider`: implementa `ShoppingCarts`.
- `OrdersPersistenceProvider`: implementa `Orders`.

Conversão entre domínio e banco:

- Assemblers convertem objetos de domínio para entidades JPA.
- Disassemblers convertem entidades JPA para objetos de domínio.
- Essa separação mantém o domínio desacoplado do JPA.

Integrações:

- `ProductCatalogServiceFakeImpl`: implementação fake do catálogo, retornando
  um produto `Notebook` em estoque com preço `3000`.
- `ShippingCostServiceRapidexImpl`: implementação realista que chama a API
  RapiDex via `RestClient`.
- `ShippingCostServiceFakeImpl`: implementação fake de frete, com valor fixo e
  prazo fixo.
- `FixedOriginAddressService`: fornece um endereço de origem fixo.

O provedor de frete é definido por configuração:

```yaml
algashop:
  integrations:
    shipping.provider: "RAPIDEX"
    rapidex:
      url: "http://localhost:8780"
```

### Camada de apresentação

Local: `microservices/algashop-ordering/src/main/java/.../presentation`

O pacote existe, mas ainda não há controllers REST implementados. As
funcionalidades estão disponíveis por meio dos serviços de aplicação e são
exercitadas principalmente pelos testes.

## Funcionalidades principais

### Clientes

- Cadastro de cliente com nome, e-mail, telefone, documento, data de nascimento,
  preferências de notificação e endereço.
- Validação de e-mail.
- Validação de unicidade de e-mail.
- Arquivamento de cliente, anonimizando parte dos dados.
- Alteração de e-mail.
- Acúmulo de pontos de fidelidade.

### Carrinho

- Criação de carrinho para cliente existente.
- Regra que impede mais de um carrinho ativo por cliente.
- Adição de produto ao carrinho.
- Atualização de quantidade quando o produto já existe no carrinho.
- Remoção de item.
- Esvaziamento do carrinho.
- Exclusão do carrinho.
- Recalculo automático de quantidade total e valor total.
- Atualização em massa de preço e disponibilidade de produtos em carrinhos.

### Checkout

- Conversão de carrinho em pedido.
- Validação de carrinho vazio.
- Validação de itens indisponíveis.
- Cálculo de frete.
- Montagem de dados de cobrança.
- Montagem de dados de entrega.
- Criação do pedido no status `PLACED`.
- Esvaziamento do carrinho após o checkout.

### Compra direta

- Compra de um produto sem usar carrinho.
- Consulta ao catálogo.
- Validação de estoque.
- Cálculo de frete.
- Criação do pedido com um único item.

### Pedidos

- Criação de pedido em rascunho (`DRAFT`).
- Adição e remoção de itens enquanto o pedido está editável.
- Alteração de cobrança, entrega e pagamento enquanto o pedido está em rascunho.
- Fechamento do pedido (`PLACED`).
- Marcação como pago (`PAID`).
- Marcação como pronto (`READY`).
- Cancelamento (`CANCELLED`).
- Validação de transições de status.
- Recalculo de totais com itens e frete.

### Frete

- Consulta a uma API externa RapiDex.
- Mock local com WireMock.
- Implementação fake alternativa.
- Cálculo da data estimada de entrega com base no prazo retornado pela API.

### Fidelidade

- Pontos são adicionados apenas se o pedido pertence ao cliente.
- Pontos são adicionados apenas se o pedido está `READY`.
- A regra atual usa 5 pontos a cada `1000` em valor de pedido.

## Testes

O projeto separa testes unitários e testes de integração.

Testes unitários:

```bash
./gradlew test
```

Testes de integração:

```bash
./gradlew integrationTest
```

Verificação completa:

```bash
./gradlew check
```

Cobertura existente:

- Value objects, como `Money`, `Quantity` e `BirthDate`.
- Entidades de domínio, como `Customer`, `ShoppingCart`, `Order` e itens.
- Serviços de domínio.
- Serviços de aplicação.
- Providers de persistência.
- Repositórios JPA.
- Assemblers e disassemblers.

## Execução local

Para subir o mock da RapiDex:

```bash
docker compose up
```

Para executar o microserviço:

```bash
cd microservices/algashop-ordering
./gradlew bootRun
```

O H2 Console está habilitado pela configuração da aplicação.

## Pontos de atenção

- Ainda não há controllers REST na camada `presentation`.
- As funcionalidades estão concentradas em serviços de aplicação.
- `BuyNowApplicationService` cria o pedido, mas atualmente não chama
  `orders.add(order)`, apesar de `Orders` estar injetado.
- Em `CustomerRegistrationService.changeEmail`, a validação de unicidade usa o
  e-mail atual do cliente em vez do novo e-mail recebido.
