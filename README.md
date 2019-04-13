# dbunit-plus
---------------------

Simple library to add lot of new features to DbUnit library:
- JUnit Rule / Runner.
- JSON DataSet.
- Integration with Spring (spring `TestExecutionListener` and spring `EmbeddedDatabase`).
- Integration with Liquibase.

## Installation

```xml
<dependency>
    <groupId>com.github.mjeanroy</groupId>
    <artifactId>dbunit-plus</artifactId>
    <version>[VERSION]</version>
    <scope>test</scope>
</dependency>
```

**Important**: If you can't upgrade to Java 8 (or more), please use version 1.X.X, otherwise use version 2.X.X (no new features will be added
to 1.X.X releases, but note that only bug fixes will be backported).

## Basics

Run DbUnit test by adding some simple annotations:

```java
package com.github.mjeanroy.repository;

import org.junit.Test;
import org.junit.Rule;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitInit;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitSetup;
import com.github.mjeanroy.dbunit.integration.junit4.DbUnitRule;

@DbUnitDataSet("/dbunit")
@DbUnitInit(sql = "/sql/schema.sql")
public class MyRepositoryTest {

    @Rule
    public DbUnitRule rule = new DbUnitRule();

    @Test
    public void testFind() {
        // Query against dataSet.
    }

    @Test
    @DbUnitDataSet("/dbunit/xml/table1.xml")
    public void testFind() {
        // Query against dataSet.
        // DataSet is defined for this method.
    }
}
```

Here are the available annotations:
- `@DbUnitDataSet`: define dataset (or directory containing dataset files) to load (can be used on `package`, entire `class` or a `method`).
- `@DbUnitInit`: define SQL script to execute before any dataset insertion (can be used on `package` or entire `class`).
- `@DbUnitSetup`: define DbUnit setup operation (can be used on `package`, entire `class` or a `method`).
- `@DbUnitTearDown`: define DbUnit tear down operation (can be used on `package`, entire `class` or a `method`).

## JUnit Runner

If you prefer to use JUnit runner instead of JUnit rule, then:

```java
package com.github.mjeanroy.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitInit;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitSetup;
import com.github.mjeanroy.dbunit.integration.junit4.DbUnitJunitRunner;

@RunWith(DbUnitJunitRunner.class)
@DbUnitDataSet("/dbunit")
@DbUnitInit(sql = "/sql/schema.sql")
public class MyRepositoryTest {

    @Test
    public void testFind() {
        // Query against dataSet.
    }

    @Test
    @DbUnitDataSet("/dbunit/xml/table1.xml")
    public void testFind() {
        // Query against dataSet.
        // DataSet is defined for this method.
    }
}
```

The same annotations can be used with JUnit runner and JUnit rule.

## JSON DataSet

By default, DbUnit is shipped with XML dataset (and some other implementations).

This library add JSON dataset:
- *Add Jackson2* (or Gson, or Jackson1) to your classpath.
- Define your json file:

```json
{
  "table1": [
    { "id": 1, "name": "John Doe" },
    { "id": 2, "name": "Jane Doe" }
  ],

  "table2": [
    { "id": 1, "title": "Star Wars" }
  ]
}
```

Now, if you add `@DbUnitDataSet` annotation to your JUnit test, this library will try to load the appropriate implementation for your file:
- If file has `.json` extention, then JSON dataset implementation is used.
- If file has `.xml` extention, then XML dataset implementation is used.

## Spring

### Test Execution Listener

To run DbUnit with Spring, you can use the test execution listener implementation:

```java
package com.github.mjeanroy.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.integration.junit4.DbUnitJunitRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestSpringConfiguration.class)
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DbUnitTestExecutionListener.class
})
@DbUnitDataSet("/dbunit")
public class MyRepositoryTest {

    @Test
    public void testFind() {
        // Query against dataSet.
    }

    @Test
    @DbUnitDataSet("/dbunit/table1.xml")
    public void testFind() {
        // Query against dataSet.
        // DataSet is defined for this method.
    }
}
```

### Working with `EmbeddedDatabase`

Spring provide implementation for `EmbeddedDatabase` (using `HSQL`, `H2` or `DERBY`). With this library, you can now use a rule to start/stop test database between test
method execution:

```java
package com.github.mjeanroy.repository;

import java.sql.Connection;

import org.junit.Test;
import org.junit.ClassRule;
import org.junit.Rule;

import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitInit;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitSetup;
import com.github.mjeanroy.dbunit.core.jdbc.AbstractJdbcConnectionFactory;
import com.github.mjeanroy.dbunit.integration.junit4.DbUnitRule;
import com.github.mjeanroy.dbunit.integration.spring.junit4.EmbeddedDatabaseRule;

@DbUnitDataSet("/dbunit")
public class MyRepositoryTest {

    @ClassRule
    public static EmbeddedDatabaseRule dbRule = new EmbeddedDatabaseRule();

    @Rule
    public DbUnitRule dbUnitRule = new DbUnitRule(new AbstractJdbcConnectionFactory() {
        @Override
        protected Connection createConnection() throws Exception {
            return dbRule.getDb().getConnection();
        }
    });

    @Test
    public void testFind() {
        // Query against dataSet.
    }

    @Test
    @DbUnitDataSet("/dbunit/table1.json")
    public void testFind() {
        // Query against dataSet.
        // DataSet is defined for this method.
    }
}
```

You can also use a simple rule to wrap spring `EmbeddedDatabase` and `DbUnit`:

```java
package com.github.mjeanroy.repository;

import org.junit.Test;
import org.junit.Rule;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitInit;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitSetup;
import com.github.mjeanroy.dbunit.integration.spring.junit4.DbUnitEmbeddedDatabaseRule;

@DbUnitDataSet("/dbunit")
public class MyRepositoryTest {

    @Rule
    public DbUnitEmbeddedDatabaseRule dbRule = new DbUnitEmbeddedDatabaseRule();

    @Test
    public void testFind() {
        // Query against dataSet.
    }

    @Test
    @DbUnitDataSet("/dbunit/table1.json")
    public void testFind() {
        // Query against dataSet.
        // DataSet is defined for this method.
    }
}
```

## Liquibase integration

Liquibase is a tool to apply database migration using simple XML file. DbUnit can run liquibase update before test
execution using `@DbUnitLiquibase` annotation:

```java
package com.github.mjeanroy.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitDataSet;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitInit;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitSetup;
import com.github.mjeanroy.dbunit.core.annotations.DbUnitLiquibase;
import com.github.mjeanroy.dbunit.integration.junit4.DbUnitJunitRunner;

@DbUnitDataSet("/dbunit/xml")
@DbUnitLiquibase("/liquibase/changelog.xml")
public class MyRepositoryTest {

    @Test
    public void testFind() {
        // Query against dataSet.
    }

    @Test
    @DbUnitDataSet("/dbunit/xml/table1.xml")
    public void testFind() {
        // Query against dataSet.
        // DataSet is defined for this method.
    }
}
```

*Important:* DbUnit will run liquibase migration using `dbunit` and `test` context (this may allow you to skip some changesets for unit test).
