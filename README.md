Backend Summary – From My Perspective (Peter Ren, Backend Developer)

As the Backend Developer for our ACME College project, I am responsible for designing, implementing, and maintaining the entire backend system using Java, Jakarta EE, Payara, and MySQL.
Below is a complete summary of the work I have completed.

👨‍💻 1. Backend Environment Setup (Java EE Infrastructure)

I set up the full backend development environment, including:

Java 17

Jakarta EE 10 (JAX-RS, CDI, JPA, Security)

Payara Server 6

Maven Build System

MySQL Database

Payara JDBC Connection Pool + Data Source

This ensures our backend runs successfully at:

http://localhost:8080

🗄️ 2. Database Initialization & JPA Integration

I configured the entire database layer using Jakarta Persistence (JPA).

This includes:

Creating all JPA entities (Student, Program, Course, Professor, etc.)

Ensuring mappings (@Entity, @Id, @OneToMany, etc.)

Enabling schema generation

Executing SQL seed scripts:

acmecollege-create.sql

acmecollege-data.sql

The seed data initializes:

Sample students

100+ program names

Courses

Professors

Security users + roles

The database is now fully populated and operational.

🔐 3. Implemented Backend Security (Basic Authentication)

Our project uses Jakarta EE Security with Basic Authentication.
I analyzed and verified the Security module:

CustomAuthenticationMechanism.java

CustomIdentityStore.java

CustomIdentityStoreJPAHelper.java

I confirmed that:

Password validation works through the IdentityStore

API access is correctly restricted via @RolesAllowed

Unauthorized requests return 401 or 403

The system includes two default accounts:

Username	Password	Role
admin	admin	ADMIN_ROLE
cst8277	8277	USER_ROLE
📡 4. REST API Development with Java JAX-RS

I validated and tested all REST endpoints implemented in Java using JAX-RS.

✔ Base URL
/REST-ACMECollege-Skeleton-0.0.1-SNAPSHOT/api/v1

✔ Student API (Main feature)
GET    /student
GET    /student/{id}
POST   /student
PUT    /student/{id}
DELETE /student/{id}

✔ Program API
GET /student/program


Each endpoint properly connects to:

REST Layer → JAX-RS (StudentResource.java)

Service Layer → EJB (ACMECollegeService.java)

Database Layer → JPA EntityManager

Example Java code I verified:

@GET
@RolesAllowed(ADMIN_ROLE)
public Response getStudents() {
    List<Student> students = service.getAllStudents();
    return Response.ok(students).build();
}

🔁 5. Backend Three-Layer Architecture

I ensured our backend follows the classic Java EE 3-tier architecture:

REST Resource (Controller)
      ↓
EJB Service (Business Logic)
      ↓
JPA EntityManager (Database Access)


Each Java layer is working correctly and follows Jakarta EE standards.

🧪 6. API Testing with curl & Postman (With Authentication)

I confirmed that all endpoints work with Basic Auth.

Example:

curl -u admin:admin \
  http://localhost:8080/REST-ACMECollege-Skeleton-0.0.1-SNAPSHOT/api/v1/student


I also provided:

Axios examples

Postman usage instructions

cURL commands

Error handling documentation

So the frontend team can easily consume the backend API.




👨‍💻 1. 我搭建了完整的 Java EE 后端运行环境（已完成 ✔）

我负责了整个后端基础环境的搭建，包括：

Java 17

Jakarta EE 10（JAX-RS、JPA、CDI、Security）

Payara Server 6

Maven 项目结构

MySQL + JDBC 数据源

确保整个 Java Web 应用可以成功运行在：

http://localhost:8080

🗄️ 2. 我配置了数据库，并通过 Java JPA 实现 ORM（已完成 ✔）

我使用 Jakarta Persistence (JPA) 实现了实体类映射，包括：

Student.java

Program.java

Course.java

Professor.java

我负责确保：

✔ JPA 实体字段与数据库完全一致
✔ 主键、关系、@Entity、@Id、@Column 正常工作
✔ 数据能被 JPA 自动创建/更新

（通过 persistence.xml + schema-generation + SQL 脚本）

🧪 3. 我执行了所有 SQL 初始化脚本，并验证数据（已完成 ✔）

我运行并确认成功导入：

acmecollege-create.sql

acmecollege-data.sql

使数据库自动拥有：

学生（John / Mary）

100+ Program 数据

课程

老师

security_user（admin、cst8277）

security_role（ADMIN / USER）

🔐 4. 我实现并配置了 Java Security（Basic Auth）（已完成 ✔）

本项目采用 Jakarta Security（非 JWT）。

我负责：

阅读 security 包的 Java 代码

CustomAuthenticationMechanism.java

CustomIdentityStore.java

CustomIdentityStoreJPAHelper.java

✔ 理解并验证 Basic Authentication 工作原理
✔ 确保用户凭证从数据库比对密码
✔ 与 Payara Security 集成成功

最终，所有受保护 API 能正确返回 401 / 403 / 200。

📡 5. 我提供并测试了所有 REST API（Java JAX-RS 实现）（已完成 ✔）

所有 API 使用 Java 实现，例如：

StudentResource.java

我负责并验证：

@GET
@Path("/student")
@RolesAllowed(ADMIN_ROLE)
public Response getStudents() {
    List<Student> students = service.getAllStudents();
    return Response.ok(students).build();
}


我确保 REST 层能成功调用 Service 层：

ACMECollegeService.java
public List<Student> getAllStudents() {
    TypedQuery<Student> query = em.createQuery("SELECT s FROM Student s", Student.class);
    return query.getResultList();
}

🔁 6. 我配置并验证了后端三层结构（Java EE 经典架构）（已完成 ✔）

整个后端遵循三层架构：

REST Resource（Controller）
↓
EJB Service（Business Logic）
↓
JPA EntityManager（Database）


我确保每一层的 Java 代码都正常工作，并符合 Jakarta EE 标准设计。

🧰 7. 我生成了完整的 API 文档、README 和前端说明（已完成 ✔）

我整理了：

Base URL

所有 Java 实现的 API

Basic Auth 登录方式

JSON 数据格式

curl/axios 请求示例

项目结构说明（包含所有 Java 包）

前端只需要照着文档就能马上调用后端。

🔧 8. 我完成了 API 调试，并提供给前端可直接调用的例子（已完成 ✔）

我使用 cURL 测试：

curl -u admin:admin http://localhost:8080/.../student


确保所有 Java API 都已可用。

我还给前端提供了 axios 示例：

axios.get(url, {
  auth: { username: "admin", password: "admin" }
});
