Instruções de compilação e Instalação - WeEat

Todo o código utiliza lambda expressions apenas disponiveis a partir do java 8, pelo que devera ser assegurada a utilização de java 8.
É tambem necessario a existencia de postgres na máquina em que são corridos os servidores este poderá ser instalado seguindo as instruções deste link: https://www.postgresql.org/download/
Uma vez instalado o postgress a manipulação da base de dados assume que a conta default 'postgres' terá a password 'q1w2e3r4t5' por razões de segurança, é possivel alterar a palavra pass com estes comandos:

1. sudo -u postgres psql template1
2. ALTER USER postgres with encrypted password 'q1w2e3r4t5';
3. sudo systemctl restart postgresql.service

Procede-se agora então às instruções para correr o loadbalancer, servidores e clientes:

LoadBalancer:

  Instalação:
    Compilar o source code.

  Execução:
  	Executar o main contido na classe: /server/src/network/load_balancer/LoadBalancer.java


    USAGE:
     	<loadBalancer_HTTPS_Port> <loadBalancer_SSLSOCKET_Port>

    loadBalancer_HTTPS_Port - Porta utilizada para conexões https ao balancer (conexões dos clientes)
    loadBalancer_SSLSOCKET_Port - Porta utilizada para conexões por sslsockets ao balancer (conexões dos servidores)


Servidor:

  Instalação:

    Compilar o source code.

  Execução:

    Executar o main contido na classe: /server/src/network/ServerWeEat.java

    USAGE:
     	<locationString> <serverIp> <serverPort> <balancerIp> <balancerPort> <WebSocketPort> <backupPort> <path_to_pgsql_bin>

    locationString - String que identifica a área de operação do servidor, por ex: 'PORTO'
    serverIp - Ip da máquina em que o server está a correr.
    serverPort - Porta utilizada com a base de dados.
    balancerIp -  Ip da máquina em que o balancer está a correr.
    balancerPort - Porta para comunicação ssl com o balancer
    WebSocketPort -  Porta em que  o websocket opera.
    backupPort - Porta utilizada para os sslsockets que realizam a comunicação entre server operador e backup para o envio dos dados da base de dados
    path_to_pgsql_bin - Caminho para a pasta bin do pg_sql da máquina em que o servidor está a correr

Client

  Instalação:
    Criar um projeto no android studio e gerar a apk, ou então utilizar a apk, fornecida na pasta do cliente.

  Execução:
    Na primeira atividade da aplicação, alterar os settings para que estes coincidam com o ip e a porta do Balancer.
