package com.monglife.discovery.domain.feedback.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration("feedbackDataSourceConfig")
@EnableTransactionManagement
@EnableJpaRepositories(
        // repositoryImpl 도 넣는다. Spring Data 는 커스텀 구현(*Impl) 을 이 base package 들에서 클래스패스
        // 스캔으로 찾는다. 빼면 @Repository 빈 이름 폴백에 기대게 되는데 빈 등록 순서에 따라 못 찾고,
        // 그때 findByXxx 는 조용히 파생 쿼리로 대체되어(isDeleted 필터 누락) 나머지는 기동 실패로 이어진다.
        basePackages = {"com.monglife.discovery.domain.feedback.repository", "com.monglife.discovery.domain.feedback.repositoryImpl"},
        entityManagerFactoryRef = "feedbackEntityManager",
        transactionManagerRef = "feedbackTransactionManager"
)
public class DataSourceConfig {

    @Value("${spring.jpa.feedback.properties.hibernate.dialect}")
    private String dialect;

    @Value("${spring.jpa.feedback.properties.hibernate.hbm2ddl.auto}")
    private String ddlAuto;

    @Value("${spring.jpa.feedback.properties.hibernate.show_sql}")
    private String showSql;

    @Value("${spring.jpa.feedback.properties.hibernate.format_sql}")
    private String formatSql;

    @Bean(name = "feedbackDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.feedback.hikari")
    public HikariConfig dataSourceProperties() {
        return new HikariConfig();
    }

    @Bean(name = "feedbackDataSource")
    public DataSource dataSource(@Qualifier("feedbackDataSourceProperties") HikariConfig dataSourceProperties) {
        return new HikariDataSource(dataSourceProperties);
    }

    @Bean(name = "feedbackJpaProperties")
    public Properties feedbackJpaProperties(@Qualifier("hibernateProperties") Properties hibernateProperties, ConfigurableListableBeanFactory beanFactory) {
        Properties jpaProperties = new Properties();
        jpaProperties.put(AvailableSettings.DIALECT, dialect);
        jpaProperties.put(AvailableSettings.HBM2DDL_AUTO, ddlAuto);
        jpaProperties.put(AvailableSettings.SHOW_SQL, showSql);
        jpaProperties.put(AvailableSettings.FORMAT_SQL, formatSql);
        jpaProperties.put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(beanFactory));
        jpaProperties.putAll(hibernateProperties);
        return jpaProperties;
    }

    @Bean(name = "feedbackEntityManager")
    public LocalContainerEntityManagerFactoryBean feedbackEntityManager(@Qualifier("feedbackDataSource") DataSource dataSource, @Qualifier("feedbackJpaProperties") Properties jpaProperties) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.monglife.discovery.domain.feedback.entity");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaProperties(jpaProperties);
        return em;
    }

    @Bean(name = "feedbackTransactionManager")
    public PlatformTransactionManager feedbackTransactionManager(@Qualifier("feedbackEntityManager") LocalContainerEntityManagerFactoryBean entityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager.getObject());
        return transactionManager;
    }
}
