# Ecossistema WaterWise

"A enchente que alaga uma avenida pode começar com uma gota que o solo seco da zona rural não absorveu. Com WaterWise, cada metro de terra volta a ser uma esponja contra desastres."

## Visão Geral do Projeto

A **WaterWise** é um ecossistema tecnológico integrado que previne enchentes urbanas através do monitoramento inteligente de propriedades rurais.

### Repositório Principal
Para uma compreensão abrangente do projeto e sua arquitetura, acesse nosso repositório de overview:

**[WaterWise - Visão Geral do Projeto](https://github.com/WaterWise-GlobalSolution/waterwise-overview)**

## Organização GitHub

Para melhor organização dos entregáveis e centralização de todas as soluções do ecossistema WaterWise, criamos uma organização dedicada no GitHub.

### Acesse Nossa Organização
**[WaterWise Organization](https://github.com/WaterWise-GlobalSolution)**

---

# WaterWise - Sistema Inteligente de Prevenção a Enchentes

Sistema com IoT integrado para monitoramento de propriedades rurais, baseado em Mairiporã-SP, e focado na prevenção de enchentes urbanas através do monitoramento da capacidade de retenção hídrica do solo.

## Sumário

- [Sobre o WaterWise Admin](#-sobre-o-waterwise-admin)
- [Detalhes Técnicos](#detalhes-técnicos)
  - [Tecnologias Utilizadas](#tecnologias-utilizadas)
  - [Configuração e Instalação](#configuração-e-instalação)
  - [Comandos de Execução](#comandos-de-execução)
  - [Endpoints Principais](#endpoints-principais)
- [Funcionalidades Principais](#funcionalidades-principais)
- [Testes Unitários](#testes-unitários)
- [Casos de Uso](#casos-de-uso)
- [Vídeo Pitch](#vídeo-pitch)
- [Vídeo Explicativo e Demonstrativo do Projeto](#vídeo-explicativo-e-demonstrativo-do-projeto)
- [Desenvolvedores](#desenvolvedores)

## Sobre o WaterWise Admin

O WaterWise Admin é uma solução completa que combina sensores IoT, inteligência artificial e análise de dados para:
- Monitorar a umidade e capacidade de absorção do solo
- Prevenir enchentes urbanas através do gerenciamento rural
- Fornecer relatórios inteligentes com IA
- Alertar sobre condições críticas em tempo real

## Viabilidade e Inovação

O WaterWise se destaca por sua abordagem inovadora e altamente viável na mitigação de enchentes urbanas a partir do monitoramento inteligente de propriedades rurais. Em vez de apenas reagir aos efeitos das chuvas intensas nas cidades, a solução propõe uma estratégia preventiva, focada na capacidade de retenção hídrica do solo em áreas adjacentes a centros urbanos, como ocorre em Mairiporã-SP, região que exerce influência direta sobre a bacia hidrográfica da Grande São Paulo.

Do ponto de vista tecnológico, o projeto é altamente viável e utiliza ferramentas consolidadas como Spring Boot, RabbitMQ e Oracle Database, integradas a sensores IoT e uma camada de inteligência artificial (IA) com modelos LLM via Ollama.

No aspecto inovador, o diferencial do WaterWise está na integração entre IA e dados de sensores de campo em tempo real, o que possibilita a geração de relatórios preditivos e alertas instantâneos. A utilização do modelo Gemma 2b para análises contextuais e recomendações técnicas expande a inteligência do sistema, tornando-o não apenas reativo, mas também prescritivo e proativo.

Em resumo, o WaterWise é viável economicamente, escalável tecnicamente e disruptivo em sua proposta. Ele se posiciona como uma solução estratégica de infraestrutura verde digital, aliando sustentabilidade ambiental, inteligência computacional e gestão de riscos em um só ecossistema.

# Detalhes Técnicos

## Tecnologias Utilizadas

### Backend Framework
- **Spring Boot 3.5.0** - Framework principal para desenvolvimento da aplicação
- **Spring Web MVC** - Para criação de APIs REST e controllers
- **Spring Data JPA** - Abstração para acesso a dados e ORM
- **Spring Security** - Segurança e autenticação da aplicação

### Banco de Dados
- **Oracle Database** - Sistema de gerenciamento de banco de dados principal
- **Hibernate** - ORM para mapeamento objeto-relacional
- **HikariCP** - Pool de conexões de alta performance

### Autenticação e Segurança
- **OAuth2 Client** - Autenticação via Google OAuth2
- **Spring Security OAuth2** - Integração com provedores OAuth2 externos
- **Session Management** - Gerenciamento de sessões de usuário

### Frontend e Interface
- **Thymeleaf** - Engine de templates para renderização server-side
- **Bootstrap 5.3** - Framework CSS para design responsivo
- **Bootstrap Icons** - Biblioteca de ícones
- **HTML5/CSS3** - Markup e estilização moderna
- **JavaScript (Vanilla)** - Interatividade no frontend

### Inteligência Artificial
- **Spring AI** - Framework para integração com modelos de IA
- **Ollama** - Plataforma para execução local de modelos LLM
- **Gemma 2b** - Modelo de linguagem para geração de relatórios
- **AI Chat Client** - Cliente para comunicação com modelos de IA

### Sistema de Mensageria
- **RabbitMQ** - Message broker para comunicação assíncrona
- **Spring AMQP** - Integração Spring com protocolo AMQP
- **Jackson JSON** - Serialização/deserialização de mensagens
- **Topic Exchange** - Padrão de roteamento de mensagens

### Validação e Processamento
- **Bean Validation (JSR-303)** - Validação de dados de entrada
- **Jackson** - Processamento JSON
- **SLF4J + Logback** - Sistema de logging

### Ferramentas de Build e Deployment
- **Maven 4.0.0** - Gerenciamento de dependências e build
- **Java 17+** - Versão LTS do Java
- **Spring Boot DevTools** - Ferramentas de desenvolvimento
- **Docker** - Containerização para execução do serviço de mensageria (RabbitMQ)

### Arquitetura e Padrões
- **MVC (Model-View-Controller)** - Padrão arquitetural principal
- **Repository Pattern** - Abstração da camada de dados
- **Service Layer** - Lógica de negócio
- **DTO (Data Transfer Objects)** - Transferência de dados
- **Event-Driven Architecture** - Comunicação via eventos (RabbitMQ)

## Configuração e Instalação

### Pré-requisitos
- IDE: IntelliJ IDEA Ultimate (recomendado)
- Java 17
- Maven 3.6+
- Oracle Database (ou outro SGBD compatível)
- Docker Desktop (para containerizar RabbitMQ)
- Ollama com modelo Gemma 2b

### Configuração do Banco de Dados
```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=waterwise
spring.datasource.password=senha
spring.jpa.hibernate.ddl-auto=update
```

### Configuração do RabbitMQ
```properties
rabbitmq.enabled=true
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

### Configuração da IA (Ollama)
```properties
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=gemma:2b
```

### Configuração OAuth2
```properties
spring.security.oauth2.client.registration.google.client-id=seu-client-id
spring.security.oauth2.client.registration.google.client-secret=seu-client-secret
```

## Comandos de Execução

### Ponto de Atenção:
Para a aplicação funcionar devidamente, precisa-se, antes, executar os seguintes comandos no PowerShell:
```bash
## Ollama gemma:2b (Funcionalidade de Relatório com IA)
ollama pull gemma:2b

## RabbitMQ (Funcionalidade de Alertas com Mensageria)
docker run -d --name waterwise-rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```
PS.: Mais detalhes abaixo.

### Instalação do Ollama e Modelo
```bash
# Instalar Ollama
curl -fsSL https://ollama.ai/install.sh | sh

# Baixar modelo Gemma 2b
ollama pull gemma:2b

# Iniciar servidor Ollama
ollama serve
```

### Criação e execução do container RabbitMQ
```bash
# Cria e executa um container RabbitMQ em background, mapeando as portas 5672 (para conexão da aplicação) e 15672 (para interface web de gerenciamento)
docker run -d --name waterwise-rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

### Executar a Aplicação
```bash
# Compilar o projeto
mvn clean compile

# Executar em modo desenvolvimento
mvn spring-boot:run

# Ou gerar JAR e executar
mvn package
java -jar target/waterwise-1.0.0.jar
```

## Endpoints Principais

- **Dashboard**: `/admin/dashboard`
- **Propriedades**: `/admin/propriedades`
- **Relatórios IA**: `/admin/relatorio-ia`
- **Sensores IoT**: `/admin/sensores`
- **Alertas**: `/admin/alertas`
- **Notificações**: `/admin/notificacoes`
- **Teste RabbitMQ**: `/admin/teste-rabbitmq`

# Funcionalidades Principais

### Dashboard Inteligente
- Visão geral do sistema com métricas em tempo real
- Estatísticas de propriedades, sensores e alertas
- Top 5 propriedades por capacidade de absorção
- Status operacional do sistema

### Gestão de Propriedades Rurais
- Cadastro completo de propriedades com coordenadas GPS
- Classificação por nível de degradação do solo
- Cálculo de capacidade de absorção hídrica
- Estimativa de famílias protegidas

### Relatórios com IA
- Análise automatizada via Ollama Gemma 2b
- Relatórios personalizados por propriedade
- Análise regional com recomendações técnicas
- Insights baseados em dados históricos

### Monitoramento IoT
- Gerenciamento de sensores de campo
- Coleta de dados de umidade, temperatura e precipitação
- Histórico de leituras e tendências
- Status de bateria e conectividade

### Sistema de Notificação
Implementação de notificações em tempo real utilizando RabbitMQ. 
A API .NET atua como producer, gerando alertas com base na leitura dos sensores, enquanto o consumer processa e gera notificação que pode ser visualizada via interface.

### Autenticação Segura
- Login via Google OAuth2
- Processo de completar perfil para novos usuários
- Interceptadores para verificação de perfil
- Gerenciamento de sessões

### Métricas Disponíveis
- Total de propriedades monitoradas
- Capacidade média de absorção hídrica
- Número de sensores ativos
- Área total monitorada em hectares

### Logs e Debugging
- Logs estruturados com SLF4J
- Console de testes RabbitMQ em tempo real
- Status da IA e conectividade Ollama
- Monitoramento de exceções

## Testes Unitários

O projeto implementa uma estratégia abrangente de testes para garantir a qualidade e confiabilidade do código:

### Estrutura de Testes
- **JUnit 5** - Framework principal para execução de testes unitários
- **Spring Boot Test** - Testes de integração com contexto Spring
- **Mockito** - Framework para criação de mocks e stubs
- **TestContainers** - Testes de integração com banco de dados Oracle

### Cobertura de Testes
- **Services** - Testes unitários da lógica de negócio
- **Controllers** - Testes de endpoints REST e MVC
- **Repositories** - Testes de queries JPA customizadas
- **Security** - Testes de autenticação e autorização OAuth2
- **RabbitMQ** - Testes de mensageria assíncrona
- **IA Integration** - Testes mock da integração com Ollama

### Executar Testes
```bash
# Executar todos os testes
mvn test

# Executar testes com relatório de cobertura
mvn test jacoco:report

# Executar apenas testes unitários (exclusão de testes de integração)
mvn test -Dtest="!*IntegrationTest"
```

## Casos de Uso

1. **Prevenção de Enchentes**: Monitoramento da capacidade de retenção do solo
2. **Gestão Rural Inteligente**: Otimização do uso da terra para máxima absorção
3. **Alertas Preventivos**: Notificações sobre condições críticas
4. **Relatórios Técnicos**: Análises automatizadas para tomada de decisão

## Vídeo Explicativo e Demonstrativo do Projeto
[Clique aqui](https://drive.google.com/file/d/1MC0rS3Li4eh3wyX9uNzeas5vo2be4J2h/view?usp=sharing)

## Vídeo Pitch
[Clique aqui](https://www.youtube.com/watch?v=hXPDFcBu9gs)

---

**Instituição de Ensino:** FIAP - Faculdade de Informática e Administração Paulista

**Turma:** 2TDSPS

## Desenvolvedores

[Felipe Amador](https://github.com/felipetosma) | 553528

[Leonardo Oliveira](https://github.com/leodascripto) | 554024

[Sara Sousa](https://github.com/sousa-sara) | 552656

---

**WaterWise** © 2025 - Sistema Inteligente de Prevenção a Enchentes
