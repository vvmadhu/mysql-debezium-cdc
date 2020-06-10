### Details of environment
- MySQl Server version: mysql-8.0.11
- Springboot: 2.2.6.RELEASE
- debezium.version: 0.10.0.Final
- mysql-connector: auto picked by spring-boot

### Reference Link
https://stackoverflow.com/questions/62090680/mysql-changes-to-kafka-using-debezium-capturing-only-ddl-stmts

### Initialize the DB


    title mysql-server
    set MYSQL_HOME=C:\Oracle\mysql-8.0.11-winx64
    set PATH=%PATH%;%MYSQL_HOME%\bin
    mysqld --initialize-insecure --basedir=%MYSQL_HOME% --datadir=%MYSQL_HOME%\data --console 
    

  
### Start the DB

    title mysql-server
    set MYSQL_HOME=C:\Oracle\mysql-8.0.11-winx64
    set PATH=%PATH%;%MYSQL_HOME%\bin
    mysqld --basedir=%MYSQL_HOME% --datadir=%MYSQL_HOME%\data --console
    

### Connect to DB

    title mysql-client
    set MYSQL_HOME=C:\Oracle\mysql-8.0.11-winx64
    set PATH=%PATH%;%MYSQL_HOME%\bin
    mysql.exe -u hcl -p
    

### Set Global Parameters

    USE mysql;
    mysql> SET GLOBAL server-uuid = '223344';
    mysql> SET GLOBAL binlog_format = 'ROW';
    mysql> SET GLOBAL binlog_row_image = 'full';
    mysql> SET GLOBAL binlog_expire_logs_seconds  = 172800; //2 days

### Create user

    CREATE USER 'hcl'@'localhost' IDENTIFIED WITH mysql_native_password BY 'welcome1';
    GRANT ALL ON *.* TO 'hcl'@'localhost';
    FLUSH PRIVILEGES;
    

### Create database:

    mysql> create database source;
    Query OK, 1 row affected (2.16 sec)
    
### DB Scripts

    create table customer (id varchar(10), name varchar(50), number varchar(10), email varchar(30), address varchar(200), primary key(id));
    insert into customer values ("100","test1","test1","test1","test1");
    update customer set address="test1" where id="100";

### Start Embeded debezium springboot application
Once you run your springboot application, the application will display the event and the update event is like below

    {ts_sec=1591753852, file=binlog.000008, pos=849, row=1, server_id=1, event=2}
    Topic = 223344.source.customer
    sourceRecordValue =     Struct{before=Struct{id=100,name=test1,number=test1,email=test1,address=test1},after=Struct{id=100,name=test1,number=test1,email=test1,address=test2},source=Struct{version=0.10.0.Final,connector=mysql,name=223344,ts_ms=1591753852000,db=source,table=customer,server_id=1,file=binlog.000008,pos=1007,row=0,thread=13},op=u,ts_ms=1591753852431}
