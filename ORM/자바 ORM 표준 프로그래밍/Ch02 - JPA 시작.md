# **Chapter02 - JPA 시작**

# **[ 📋 목차 ]**
책과 다른 환경에서 진행함

****

# **[ 🗂️ 정리 ]**

## 📌 인텔리제이와 그래들

Version Info

```
- intelliJ 2021.03
- Spring Boot 3.2.3
- JDK 1.7
- PostgresSql@16
```

책과 다르게 웹 환경에서 진행하며 lombok 을 이용해서 조금 더 쉽고 간편한 코드 구성 예정
jpa 를 사용하기 위한 의존성과 웹 환경에서 진행하기 위핸 web 의존성 추가 및 H2 가 아닌 postgresql 을 사용  

Dependecies

```
dependencies {
	compileOnly 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'

	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'org.postgresql:postgresql:42.5.0'

	runtimeOnly 'org.postgresql:postgresql'
}
```

application.properties

```
### Database settings
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/데이터베이스명
spring.datasource.username=아이디
spring.datasource.password=비밀번호

### Hibernate settings
#spring.jpa.hibernate.ddl-auto=create
spring.jpa.hibernate.ddl-auto=update

### Jpa settings
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

### log settings
logging.level.org.hibernate=INFO

logging.level.org.hibernate.SQL=INFO
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=INFO
```

책에서 persitence.xml 에 영속성 유닛이라는 속성을 등록하는데 필요하다는 생각은 안들어서 없음(gradle 에서는 등록 불가능한걸로 보임)

- dialect 속성: 데이터베이스마다 고유의 문법이 있어성 특정 예약어들은 DB 에 종속적이다. 그래서 DB 끼리는 호환이 되지 않아서 다른 DB 로 변경하는게
쉽지 않은 작업이지만 JPA는 dialect 만 변경하면 특정 DB 에 맞춰서 동작하기 때문에 교체가 쉽다.
