package io.github.arieslab.config

import com.zaxxer.hikari.HikariDataSource
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.orm.jpa.hibernate.HibernateTransactionManager
import org.springframework.orm.jpa.hibernate.LocalSessionFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.*
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
open class PersistenceConfig {

    @Value("\${jdbc.driver}")
    private lateinit var driverClass: String

    @Value("\${jdbc.url}")
    private lateinit var jdbcUrl: String

    @Value("\${jdbc.username}")
    private lateinit var jdbcUsername: String

    @Value("\${jdbc.password}")
    private lateinit var jdbcPassword: String

    @Value("\${hibernate.dialect}")
    private lateinit var hibernateDialect: String

    @Value("\${hibernate.hbm2ddl.auto}")
    private lateinit var hbm2ddlAuto: String

    @Value("\${hibernate.show_sql}")
    private lateinit var showSql: String

    @Bean
    open fun dataSource(): DataSource {
        val ds = HikariDataSource()
        ds.driverClassName = driverClass
        ds.jdbcUrl = jdbcUrl
        ds.username = jdbcUsername
        ds.password = jdbcPassword
        ds.minimumIdle = 1
        ds.maximumPoolSize = 200
        ds.idleTimeout = 3600000
        ds.connectionTimeout = 20000
        ds.connectionTestQuery = "SELECT 1"
        return ds
    }

    @Bean
    open fun sessionFactory(dataSource: DataSource): LocalSessionFactoryBean {
        val sf = LocalSessionFactoryBean()
        sf.setDataSource(dataSource)
        sf.setPackagesToScan("io.github.arieslab.entities")

        val props = Properties()
        props.setProperty("hibernate.dialect", hibernateDialect)
        props.setProperty("hibernate.hbm2ddl.auto", hbm2ddlAuto)
        props.setProperty("hibernate.show_sql", showSql)
        sf.setHibernateProperties(props)

        return sf
    }

    @Bean
    open fun transactionManager(sessionFactory: SessionFactory): PlatformTransactionManager {
        return HibernateTransactionManager(sessionFactory)
    }

    @Bean
    open fun exceptionTranslation(): PersistenceExceptionTranslationPostProcessor {
        return PersistenceExceptionTranslationPostProcessor()
    }
}
