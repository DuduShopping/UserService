## UserService
A micro service provides user account related functionalities.

## Getting Start
UserService requires java >= 10, gradle >= 4.8, docker and git (duh obviously)

This guide is intended for MacOS and Linux user. Apologize to window users. but it should works on window as well.

open up a command line, run following
```
git clone https://github.com/DuduShopping/UserService.git
cd UserService
gradle bootRun
```
then go to browser enter `http://localhost:8080/api/v1/userService/greeting` to see hello world

Now it comes database. To make things easy, docker container is used. open up a command line, run following
```
sudo docker pull microsoft/mssql-server-linux:2017-latest
sudo docker run -e 'ACCEPT_EULA=Y' -e 'SA_PASSWORD=Root1027' \
      -p 1433:1433 --name sql1 \
      -d microsoft/mssql-server-linux:2017-latest
```
for MacOS users, `sudo` might be omitted.

Now there is SQL server instance running at `localhost:1433`. 
Go to `sql/` folder. Create a database called `DuduShoppingUserService` 
and then execute scehmas and stored procedures.

