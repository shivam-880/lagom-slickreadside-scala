# lagom-slickreadside-scala
This project aims to demonstrate implementing Lagom polyglot readside persistence using Slick.

## Scope
CRUD Apis for employees.

## Setup Mysql
### Create User
```
$ mysql -u root -p
mysql> CREATE USER 'codingkapoor'@'localhost' IDENTIFIED BY 'codingkapoor';
mysql> GRANT ALL PRIVILEGES ON * . * TO 'codingkapoor'@'localhost';
```

### Create Database
```
$ mysql -u codingkapoor -u
mysql > CREATE DATABASE slickreadside;
```

## Dev
### Clone Repo
```
$ git clone git@github.com:codingkapoor/lagom-slickreadside-scala.git
```

### Start All Services
```
$ cd lagom-slickreadside-scala
$ sbt
sbt> runAll

$ curl http://localhost:9008/services
[
  {
    "name": "cas_native",
    "url": "tcp://127.0.0.1:4000/cas_native",
    "portName": null
  },
  {
    "name": "kafka_native",
    "url": "tcp://localhost:9092/kafka_native",
    "portName": null
  },
  {
    "name": "employee",
    "url": "http://127.0.0.1:53823",
    "portName": null
  },
  {
    "name": "employee",
    "url": "http://127.0.0.1:53823",
    "portName": "http"
  }
]
```

### Create Employee
```
$ curl -X POST \
  http://localhost:9000/api/employees \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 7e3f3675-c27d-3dd2-df54-a32f276c7b41' \
  -d '{
	"id": "128",
	"name": "Shivam",
	"gender": "M",
	"doj": "2017-01-16",
	"pfn": "PFKN110"
}'
```

### Verify Cassandra
```
$ /opt/apache-cassandra-3.11.4/bin/cqlsh localhost 4000
cqlsh> USE slickreadside;
cqlsh:simplelms> select * from messages ;

 persistence_id                                 | partition_nr | sequence_nr | timestamp                            | timebucket | used | event                                                                                                                                                                                                                                                                                                                                      | event_manifest | message | meta | meta_ser_id | meta_ser_manifest | ser_id  | ser_manifest                                              | tag1                                                      | tag2 | tag3 | writer_uuid
------------------------------------------------+--------------+-------------+--------------------------------------+------------+------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+----------------+---------+------+-------------+-------------------+---------+-----------------------------------------------------------+-----------------------------------------------------------+------+------+--------------------------------------
                  EmployeePersistenceEntity|128 |            0 |           1 | 9757cab0-cb0c-11e9-91c9-d1a5d92dc8d6 |   20190830 | True |                                                                                                                                                                                 0x7b226964223a22313238222c226e616d65223a2253686976616d222c2267656e646572223a224d222c22646f6a223a22323031372d30312d3136222c2270666e223a2250464b4e313130227d |                |    null | null |        null |              null | 1000004 | com.codingkapoor.employee.persistence.write.EmployeeAdded | com.codingkapoor.employee.persistence.write.EmployeeEvent | null | null | 5ecbca45-9b79-46c5-a858-5c97d9ed1da2
    /sharding/kafkaProducer-employeeCoordinator |            0 |           1 | 947e2530-cb09-11e9-91c9-d1a5d92dc8d6 |   20190830 | True |                                                                                                                           0x0a65616b6b612e7463703a2f2f656d706c6f7965652d696d706c2d6170706c69636174696f6e403132372e302e302e313a33383833352f73797374656d2f7368617264696e672f6b61666b6150726f64756365722d656d706c6f79656523333832323635343635 |                |    null | null |        null |              null |      13 |                                                        AB |                                                      null | null | null | c39f782f-5a96-4628-9568-060d01ad6a93
    /sharding/kafkaProducer-employeeCoordinator |            0 |           2 | 94850300-cb09-11e9-91c9-d1a5d92dc8d6 |   20190830 | True |                                                                                                     0x0a0973696e676c65746f6e1265616b6b612e7463703a2f2f656d706c6f7965652d696d706c2d6170706c69636174696f6e403132372e302e302e313a33383833352f73797374656d2f7368617264696e672f6b61666b6150726f64756365722d656d706c6f79656523333832323635343635 |                |    null | null |        null |              null |      13 |                                                        AF |                                                      null | null | null | c39f782f-5a96-4628-9568-060d01ad6a93
    /sharding/EmployeeEventProcessorCoordinator |            0 |           1 | 947e2531-cb09-11e9-91c9-d1a5d92dc8d6 |   20190830 | True |                                                                                                                       0x0a67616b6b612e7463703a2f2f656d706c6f7965652d696d706c2d6170706c69636174696f6e403132372e302e302e313a33383833352f73797374656d2f7368617264696e672f456d706c6f7965654576656e7450726f636573736f72232d31323535303739333037 |                |    null | null |        null |              null |      13 |                                                        AB |                                                      null | null | null | b12e8f2c-485d-4c51-a7b3-7da9d66f3949
    /sharding/EmployeeEventProcessorCoordinator |            0 |           2 | 94848dd0-cb09-11e9-91c9-d1a5d92dc8d6 |   20190830 | True | 0x0a39636f6d2e636f64696e676b61706f6f722e656d706c6f7965652e70657273697374656e63652e77726974652e456d706c6f7965654576656e741267616b6b612e7463703a2f2f656d706c6f7965652d696d706c2d6170706c69636174696f6e403132372e302e302e313a33383833352f73797374656d2f7368617264696e672f456d706c6f7965654576656e7450726f636573736f72232d31323535303739333037 |                |    null | null |        null |              null |      13 |                                                        AF |                                                      null | null | null | b12e8f2c-485d-4c51-a7b3-7da9d66f3949
 /sharding/EmployeePersistenceEntityCoordinator |            0 |           1 | 947e2532-cb09-11e9-91c9-d1a5d92dc8d6 |   20190830 | True |                                                                                                                     0x0a68616b6b612e7463703a2f2f656d706c6f7965652d696d706c2d6170706c69636174696f6e403132372e302e302e313a33383833352f73797374656d2f7368617264696e672f456d706c6f79656550657273697374656e6365456e74697479232d3231303737303339 |                |    null | null |        null |              null |      13 |                                                        AB |                                                      null | null | null | d4fbac44-e315-4f76-8fe3-7184f874cb87
 /sharding/EmployeePersistenceEntityCoordinator |            0 |           2 | 975050a0-cb0c-11e9-91c9-d1a5d92dc8d6 |   20190830 | True |                                                                                                             0x0a0239351268616b6b612e7463703a2f2f656d706c6f7965652d696d706c2d6170706c69636174696f6e403132372e302e302e313a33383833352f73797374656d2f7368617264696e672f456d706c6f79656550657273697374656e6365456e74697479232d3231303737303339 |                |    null | null |        null |              null |      13 |                                                        AF |                                                      null | null | null | d4fbac44-e315-4f76-8fe3-7184f874cb87

```

### Verify Mysql
```
$ mysql -u codingkapoor -p
mysql> USE slickreadside;
mysql> SELECT * FROM employee;
+-----+--------+--------+------------+---------+
| ID  | NAME   | GENDER | DOJ        | PFN     |
+-----+--------+--------+------------+---------+
| 128 | Shivam | M      | 2017-01-16 | PFKN110 |
+-----+--------+--------+------------+---------+
1 row in set (0.00 sec)
```
