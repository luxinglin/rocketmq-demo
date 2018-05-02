RocketMq 

1. download & install
    > unzip rocketmq-all-4.2.0-source-release.zip

    > cd rocketmq-all-4.2.0/

    > mvn -Prelease-all -DskipTests clean install -U

    > cd distribution/target/apache-rocketmq
    
2. Start Name Server
    > nohup sh bin/mqnamesrv &
    
    > tail -f ~/logs/rocketmqlogs/namesrv.log
    
    The Name Server boot success...
    
3. Start Broker
    > nohup sh bin/mqbroker -n localhost:9876 &
    
    > tail -f ~/logs/rocketmqlogs/broker.log 
      
    The broker[%s, 172.30.30.233:10911] boot success...
    
4. Send & Receive Messages

    Before sending/receiving messages, we need to tell clients the location of name servers. RocketMQ provides multiple ways to achieve this. For simplicity, we use environment variable NAMESRV_ADDR
        > export NAMESRV_ADDR=localhost:9876
        
        > sh bin/tools.sh org.apache.rocketmq.example.quickstart.Producer
 
        SendResult [sendStatus=SEND_OK, msgId= ...

        > sh bin/tools.sh org.apache.rocketmq.example.quickstart.Consumer
        
        ConsumeMessageThread_%d Receive New Messages: [MessageExt...
        
5. Shutdown Servers
    
    >sh bin/mqshutdown broker
    
    The mqbroker(36695) is running...
    
    Send shutdown request to mqbroker(36695) OK

    >sh bin/mqshutdown namesrv
    
    The mqnamesrv(36664) is running...
    
    Send shutdown request to mqnamesrv(36664) OK